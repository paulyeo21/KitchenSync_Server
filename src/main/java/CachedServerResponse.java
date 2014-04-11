import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.sql.Time;
import java.util.Date;

@Entity
public class CachedServerResponse {
    @Id
    @GeneratedValue(generator="increment")
    @GenericGenerator(name="increment", strategy="increment")
    private Long id;

//    private String date;
//
//    private String lunch;
//
//    private String stations;
//
//    private String stationName;
//
//    private String foods;
//
//    private String name;
//
//
    public CachedServerResponse(){
    }

//    public Long getId() {
//        return id;
//    }

//    public String getDate() {
//        return date;
//    }
//
//    public void setDate(String date) {
//        this.date = date;
//    }
//
//    public String getLunch() {
//        return lunch;
//    }
//
//    public void setLunch(String lunch) {
//        this.lunch = lunch;
//    }
//
//    public String getStations() {
//        return stations;
//    }
//
//    public void setStations(String stations) {
//        this.stations = stations;
//    }
//
//    public String getStationName() {
//        return stationName;
//    }
//
//    public void setStationName(String stationName) {
//        this.stationName = stationName;
//    }
//
//    public String getFoods() {
//        return foods;
//    }
//
//    public void setFoods(String foods) {
//        this.foods = foods;
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }
//
//    public String getDescription() {
//        return description;
//    }
//
//    public void setDescription(String description) {
//        this.description = description;
//    }
//
//    public boolean isGlutenFree() {
//        return glutenFree;
//    }
//
//    public void setGlutenFree(boolean glutenFree) {
//        this.glutenFree = glutenFree;
//    }
//
//    public String getRestriction() {
//        return restriction;
//    }
//
//    public void setRestriction(String restriction) {
//        this.restriction = restriction;
//    }
//
//    private String description;
//
//    private boolean glutenFree;
//
//    private String restriction;

    @Lob
    private String menuJson;

    @NotNull
    private Date createdAt;

    public String getMenu() {
        return menuJson;
    }

    public void setMenu(String menuJson) {
        this.menuJson = menuJson;
    }

    public Date getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

}