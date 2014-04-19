
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by jeffrey on 2/11/14.
 * a Meal represents all a meal in cafe mac, it's broken up into different stations
 */

public class Meal {
    private ArrayList <Station> stations;
    private Date date;
    private boolean isLunch;
    /**
     * constructor that creates a meal
     * @param mealData HTML Element containing the data needed to make a meal
     */
    public Meal(Element mealData){
        stations = new ArrayList<Station>();
        Elements items = mealData.select("tbody > *");
        Station currentStation = new Station("", this);
        for (Element item : items){
            if (item.hasClass("menu-station")) {
                String stationName = item.getElementsByTag("td").first().text();
                currentStation = new Station(stationName, this);
                stations.add(currentStation);
            } else if (item.hasClass("price-")) {
                Food food = new Food(item);
                currentStation.addFood(food);
            }
        }
    }

    public Meal(){
        stations = new ArrayList<Station>();
    }

    public void setStations(ArrayList<Station> stations){
        this.stations = stations;
    }

    public void setDate(Date date, boolean isLunch){
        this.date = date;
        this.isLunch = isLunch;
    }

    public ArrayList<Station> getStations(){
        return stations;
    }
}
