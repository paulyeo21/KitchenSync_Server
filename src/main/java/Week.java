/**
 * Created by jeffrey on 2/11/14.
 * the week class provides describes the menu for a week in cafe mac
 * it's composed of a list of days. Creating a weeks will automatically
 * parse the menu from cafe mac's website
 */

import com.google.gson.annotations.Expose;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Set;

public class Week {
    @Expose
    private Day[] days = new Day[7];

    /**     * creates a new weekly menu by scraping data from the Bon Appetit website
     */
    public Week(Document doc){
        Elements dayData = doc.getElementsByClass("eni-menu-day");
        // Days of week start with 0 on Sunday
        for (Element dayMenu : dayData){
            String dayOfWeek = dayMenu.className();
            String date =
                    dayOfWeek = dayOfWeek.substring(dayOfWeek.length() -1);
            days[Integer.parseInt(dayOfWeek)] = new Day(dayMenu);
        }
        Calendar calendar = getNormalizedCalendar();
        for(int i=0; i<7; i++){
            calendar.add(Calendar.DATE, 1);
            if (days[i] != null)
                days[i].setDate(calendar.getTime());
        }
    }
    public Week(){}

    public Day[] getDays(){
        return days;
    }

    public void setDay(Day day, Weekday dayOfWeek){
        days[dayOfWeek.ordinal()] = day;
    }

    /**
     * @param day a day of the week represented as an int
     * @return the daily menu of that day
     */
    public Day getDay(Weekday day){
        return days[day.ordinal()];
    }

    /**
     * @return a human readable version of the Week's menu
     */
    public String toString(){
        String menu = "";
        for (Day day : days){
            menu = menu + day.toString() + "\n";
        }
        return menu;
    }

    // Check if days array is all null
    public boolean isEmpty(){
        for (Day day: days)
            if (day != null)
                return false;
        return true;
    }

    public List<Meal> getMeals(){
        List<Meal> meals = new ArrayList<Meal>();
        for(Day day : days)
            meals.addAll(day.getRealMeals());
        return meals;
    }

    public static Calendar getNormalizedCalendar(){
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.SUNDAY);
        int today = calendar.get(Calendar.DAY_OF_WEEK);
        calendar.add(Calendar.DATE, -today);
        calendar.set(Calendar.HOUR_OF_DAY,0);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);
        calendar.set(Calendar.MILLISECOND,0);
        return calendar;
    }

}
