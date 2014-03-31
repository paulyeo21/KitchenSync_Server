import java.util.ArrayList;
/**
 * Created by jeffrey on 2/11/14.
 * a Station represents an actual station in cafe mac and contains menu items
 */
public class Station {
    private final String name;
    private ArrayList<MenuItem> menuItems;

    /**
     * Creates a station
     * @param name the name of the Station e.g. South
     */
    public Station(String name){
        this.name = name;
        menuItems = new ArrayList<MenuItem>();
    }

    /**
     * adds a menu item to the station
     * @param menuItem a menu item served at the station
     */
    public void addMenuItem(MenuItem menuItem){
       menuItems.add(menuItem);
    }

    public void setMenuItems(ArrayList<MenuItem> menuItems){
        this.menuItems = menuItems;
    }
    /**
     * @return all the menu items served at the station
     */
    public ArrayList<MenuItem> getMenuItems(){
        return menuItems;
    }

    /**
     * @return returns station's name
     */
    public String getName(){
        return name;
    }
    /**
     * @return a human readable representation of the Station
     */
    public String toString(){
        String station = name + "\n";
        for (MenuItem menuItem : menuItems){
            station = station + menuItem.toString() + "\n";
        }
        return station;
    }

    /**
     * @param restriction a dietary restriction either vegan, vegetarian, made-without-gluten, or seafood-watch
     * @return a list of all items at the station that match the restriction e.g. all vegan options
     */
    public ArrayList<MenuItem> getMatches(Restriction restriction, boolean matchGlutenFree){
        ArrayList<MenuItem> items = new ArrayList<MenuItem>();
        for (MenuItem menuItem : menuItems){
            if (menuItem.matchRestriction(restriction, matchGlutenFree))
                items.add(menuItem);
        }
        return items;
    }

    public ArrayList<String> getMenuItemNames(){
        ArrayList<String> items = new ArrayList<String>();
        for (MenuItem menuItem : menuItems){
            items.add(menuItem.getName());
        }
        return items;
    }
}