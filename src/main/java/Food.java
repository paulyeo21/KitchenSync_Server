import org.hibernate.annotations.GenericGenerator;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.*;
/**
 * Created by jeffrey on 2/11/14.
 * a Food represents a dish being served in cafe mac
 * it also contains data about the food such as dietary restrictions
 */
@Entity
public class Food {
    @Id
    @GeneratedValue(generator="increment")
    @GenericGenerator(name="increment", strategy="increment")
    private Long foodId; //DB only

    @NotNull
    private String name;

    private String description;

    private Boolean glutenFree;

    @Enumerated(EnumType.STRING)
    private Restriction restriction; //convert to something else int? for DB

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "food")
    private Set<Review> reviews;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "food_station_pair", joinColumns = {
            @JoinColumn(name = "FOOD_ID", nullable = false, updatable = false) },
            inverseJoinColumns = { @JoinColumn(name = "STATION_ID",
                    nullable = false, updatable = false) })
    private Set<Station> stations; //DB Only

    private int rating;

    private int ratingCount;

    public Food(){}
    /* takes an HTML element representing a food and then
       turns it into a useful java object.
     */
    public Food(Element itemData){
        reviews = new HashSet<Review>();
        description = "";
        glutenFree = false;
        Element foodData = itemData.getElementsByClass("eni-menu-item-name").first();
        name = foodData.text();
        makeDescription(itemData);
        restriction = makeRestrictions(foodData);
        stations = new HashSet<Station>();
    }

    /**
     * helper method that adds a description to the menu item
     * @param itemData an HTML element collection representing a food's description
     */
    private void makeDescription(Element itemData){
        Elements foodDescription = itemData.getElementsByClass("eni-menu-description");
        if (foodDescription.size() > 0){
            description = foodDescription.first().text();
        }
    }

    /**
     * A helper method that adds dietary restriction information to the menu item
     * @param foodData a collection of HTML elements that contain dietary restriction info
     */
    private Restriction makeRestrictions(Element foodData){
        Elements restrict = foodData.select(".tipbox");
        Restriction highestLevel = Restriction.NONE;
        for (Element restriction : restrict){
            String restrictionType = restriction.className();
            restrictionType = restrictionType.substring(restrictionType.lastIndexOf(" ")+1);
            Restriction r = stringToRestriction(restrictionType);
            if (r.ordinal() < highestLevel.ordinal())
                highestLevel = r;
        }
        return highestLevel;
    }

    private Restriction stringToRestriction(String s){
        if (s.equals("vegan"))
            return Restriction.VEGAN;
        if (s.equals("vegetarian"))
            return Restriction.VEGETARIAN;
        if (s.equals("seafood-watch"))
            return Restriction.PESCETARIAN;
        if (s.equals("made-without-gluten"))
            glutenFree = true;
        return Restriction.NONE;
    }

    public Set<Station> getStations() {
        return stations;
    }

    public void setStations(Set<Station> stations) {
        this.stations = stations;
    }

    public void addStation(Station station){
        stations.add(station);
    }

    public String getName(){
        return name;
    }

    public String getDescription(){
        return description;
    }

    public Set<Review> getReviews(){
        return reviews;
    }

    public void addReview(Review review){
        reviews.add(review);
    }

    public void setReviews(Set<Review> reviews){
        this.reviews = reviews;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public int getRatingCount() {
        return ratingCount;
    }

    public void setRatingCount(int ratingCount) {
        this.ratingCount = ratingCount;
    }

    public Long getId() {
        return foodId;
    }

    public void setId(Long id) {
        this.foodId = foodId;
    }

    public boolean equals(Food other){
        return this.name == other.name;
    }
}
