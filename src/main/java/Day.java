/**
 * Created by jeffrey on 2/11/14.
 * a day represents all the food served in cafe mac during a given
 * day. Each day has two meals, lunch and dinner
 */

import com.google.gson.annotations.Expose;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Day {
    @Expose
    private Meal[] meals = new Meal[3];

    /**
     * Creates a new daily menu object containing 2 meals, lunch and dinner
     * @param dayElem an HTML element that contains the data needed to create a daily menu
     */
    public Day(Element dayElem){
        Element mealData = dayElem.select(".row-item").first();
        if (mealData != null){
            Elements  meals = mealData.children();
            for (int i=0; i<meals.size(); i++){
                String type = meals.get(i).text();
                MealType mealType= MealType.valueOf(type.toUpperCase());
                i++;
                setMeal(new Meal(meals.get(i) , mealType), mealType);
            }
        }
    }
    public Day(){}

    public void setDate(Date date1){
        for (Meal meal : meals) {
            if (meal != null)
                meal.setDate(date1);
        }
    }

    public Meal[] getMeals() {
        return meals;
    }

    public void setMeals(Meal[] meals) {
        this.meals = meals;
    }
    public void setMeal(Meal meal, MealType type){
        meals[type.ordinal()] = meal;
    }
    public Meal getMeal(MealType type){
        return meals[type.ordinal()];
    }
    public List<Meal> getRealMeals(){
        List<Meal> mealList = new ArrayList<Meal>();
        for(Meal meal : meals)
            if (meal != null)
                mealList.add(meal);
        return mealList;
    }
}
