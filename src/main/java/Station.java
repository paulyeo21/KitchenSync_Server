import java.util.ArrayList;
import java.util.List;

/**
 * Created by jeffrey on 2/11/14.
 * a Station represents an actual station in cafe mac and contains menu items
 */
public class Station {
    private final String name;
    private Meal meal;
    private List<Food> foods;

    /**
     * Creates a station
     * @param name the name of the Station e.g. South
     */
    public Station(String name, Meal meal){
        this.name = name;
        foods = new ArrayList<Food>();
    }

    public void setMeal(Meal meal) {
        this.meal = meal;
    }

    public Meal getMeal(){
        return meal;
    }

    public void addFood(Food food){
       foods.add(food);
    }

    public void setFoods(ArrayList<Food> foods){
        this.foods = foods;
    }

    public List<Food> getFoods(){
        return foods;
    }

    public void setFoods(List<Food> foods) {
        this.foods = foods;
    }

    public String getName(){
        return name;
    }

}
