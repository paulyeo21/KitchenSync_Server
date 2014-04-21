
import org.hibernate.annotations.GenericGenerator;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by jeffrey on 2/11/14.
 * a Meal represents all a meal in cafe mac, it's broken up into different stations
 */

@Entity
public class Meal {
    @Id
    @GeneratedValue(generator="increment")
    @GenericGenerator(name="increment", strategy="increment")
    private Long mealId; //DB only

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "meal")
    private Set<Station> stations;

    @NotNull
    private Date date;

    @Enumerated(EnumType.ORDINAL)
    private MealType mealType;
    /**
     * constructor that creates a meal
     * @param mealData HTML Element containing the data needed to make a meal
     */
    public Meal(Element mealData, MealType mealType){
        this.mealType = mealType;
        stations = new HashSet<Station>();
        Elements items = mealData.select("tbody > *");
        System.out.println(items.size());
        Station currentStation = new Station("", this);
        for (Element item : items){
            if (item.hasClass("menu-station")) {
                String stationName = item.getElementsByTag("td").first().text();
                currentStation = new Station(stationName, this);
                stations.add(currentStation);
            } else if (item.hasClass("price-")) {
                Food food = new Food(item);
                currentStation.addFood(food);
                food.addStation(currentStation);
            }
        }
    }

    public Meal(){
        stations = new HashSet<Station>();
    }

    public void setStations(Set<Station> stations){
        this.stations = stations;
    }

    public void setDate(Date date, MealType mealType){
        this.date = date;
        this.mealType = mealType;
    }

    public Set<Station> getStations(){
        return stations;
    }

    public MealType getMealType() {
        return mealType;
    }

    public void setMealType(boolean isLunch) {
        this.mealType = mealType;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Long getMealId() {
        return mealId;
    }

    public void setMealId(Long id) {
        this.mealId = mealId;
    }

    public boolean sameMeal(Meal other){
        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
        return fmt.format(this.date).equals(fmt.format(other.date))
                && (this.mealType == other.mealType);
    }
}
