import com.google.gson.annotations.Expose;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
import java.util.Date;


/**
 * Created by jeffrey on 4/19/14.
 */

@Entity
public class Review {
    @Id
    @GeneratedValue(generator="increment")
    @GenericGenerator(name="increment", strategy="increment")
    private Long id; //DB only

    @ManyToOne(fetch = FetchType.EAGER)
    @Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE, org.hibernate.annotations.CascadeType.DELETE})
    @JoinColumn(name = "FOOD_ID", nullable = false)
    private Food food; //DB only


    @Expose
    private String reviewer;
    @Expose
    private String text;

    @Expose
    private int rating;


    private int foodID;

    @Expose
    @NotNull
    private Date createdAt;

    public Review(){}

    public Review(String reviewer, String text, Food food){
        this.food = food;
        this.reviewer = reviewer;
        this.food = food;
        createdAt = new Date();
    }

    public Food getFood() {
        return food;
    }

    public void setFood(Food food) {
        this.food = food;
    }

    public String getReviewer() {
        return reviewer;
    }

    public void setReviewer(String reviewer) {
        this.reviewer = reviewer;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date date) {
        this.createdAt = createdAt;
    }

}
