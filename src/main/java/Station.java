import com.google.gson.annotations.Expose;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import java.util.*;

/**
 * Created by jeffrey on 2/11/14.
 * a Station represents an actual station in cafe mac and contains menu items
 */
@Entity
public class Station {
    @Id
    @GeneratedValue(generator="increment")
    @GenericGenerator(name="increment", strategy="increment")
    private Long id;

    @Expose
    private String name;

    @ManyToOne(fetch = FetchType.EAGER)
    @Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE, org.hibernate.annotations.CascadeType.DELETE})
    @JoinColumn(name = "MEAL_ID", nullable = false)
    private Meal meal;

    @Expose
    @Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE, org.hibernate.annotations.CascadeType.DELETE})
    @ManyToMany(fetch = FetchType.EAGER, mappedBy = "stations")
    private Set<Food> foods;

    public Station(){}

    /**
     * Creates a station
     * @param name the name of the Station e.g. South
     */
    public Station(String name, Meal meal){
        this.name = name;
        foods = new HashSet<Food>();
        this.meal = meal;
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

    public void setFoods(Set<Food> foods){
        this.foods = foods;
    }

    public Set<Food> getFoods(){
        return foods;
    }

    public String getName(){
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean equals(Station other){
        return this.name == other.name;
    }
}
