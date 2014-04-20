import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by jeffrey on 2/11/14.
 * a Food represents a dish being served in cafe mac
 * it also contains data about the food such as dietary restrictions
 */
public class Food {
    private String name;
    private String description;
    private Boolean glutenFree;
    private Restriction restriction; //convert to something else int? for DB
    private Set<Review> reviews;
    private Set<Station> stations; //DB Only
    private int rating;
    private int ratingCount;
    // ArrayList<Review> reviews;

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
    public Food(String name, String description, ArrayList<String> restrictions){
        glutenFree = false;
        this.name = name;
        this.description = description;
        this.restriction = makeRestrictions(restrictions);
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
    private Restriction makeRestrictions(ArrayList<String> restrictions){
        Restriction highestLevel = Restriction.NONE;
        for (String r : restrictions){
            Restriction restriction = stringToRestriction(r);
            if (restriction.ordinal() < highestLevel.ordinal())
                highestLevel = restriction;
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
}
