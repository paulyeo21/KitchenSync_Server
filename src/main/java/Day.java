/**
 * Created by jeffrey on 2/11/14.
 * a day represents all the food served in cafe mac during a given
 * day. Each day has two meals, lunch and dinner
 */

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Date;

public class Day {
    private String date="";
    private Meal lunch;
    private Meal dinner;

    /**
     * Creates a new daily menu object containing 2 meals, lunch and dinner
     * @param dayElem an HTML element that contains the data needed to create a daily menu
     */
    public Day(Element dayElem){
        Elements meals = dayElem.select(".row-item > .my-day-menu-table");
        if (dayElem.select("> *").hasClass("menu-date-heading")){
            date = dayElem.getElementsByClass("menu-date-heading").first().text();
            dinner = new Meal(meals.get(1));
            lunch = new Meal(meals.get(0));
        }
        else{
            //this means there's no menu for the day
            lunch = null;
            dinner = null;
        }
    }
    public Day(){
        lunch = null;
        dinner = null;
    }

    public void setDate(Date date1){
        if (lunch != null)
            lunch.setDate(date1, true);
        if (dinner != null)
            dinner.setDate(date1, false);
    }

    public void setDinner(Meal dinner){
        this.dinner = dinner;
    }

    public void setLunch(Meal lunch){
        this.lunch = lunch;
    }

    public Meal getDinner(){
        return dinner;
    }

    public Meal getLunch(){
        return lunch;
    }

}
