import static spark.Spark.*;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.hibernate.*;
import org.hibernate.Session;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.json.JSONException;
import org.json.JSONObject;
import spark.*;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.*;


public class CafeMacServer {

    private static SessionFactory sessionFactory = createSessionFactory();
    private static Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setPrettyPrinting().create();

    public static void main(String[] args) {

//        for heroku deployment
        if(System.getenv("PORT") != null)
            setPort(Integer.parseInt(System.getenv("PORT")));

        get(new Route("/v1/menu") {
            @Override
            public Object handle(Request request, Response response) {
                Week week = null;
                boolean success = false;
                String error = "";
                Map<String,String> validationErrors = new HashMap<String, String>();
                try {
                    week = reconstruct();
                    response.type("application/json");

                    if (week != null) {
                        success = true;
                    }
                } catch (ConstraintViolationException e) {

                    // From P. Cantrell Jokes Server
                    // https://github.com/pcantrell/jokes-spark/blob/master/src/main/java/edu/macalester/jokes/ApiRoute.java
                    // HTTP codes in the 4xx range mean that the client submitted a bad request.
                    // 400 is a good one for validation errors.
                    response.status(400);
                    success = false;
                    error = e.getLocalizedMessage();

                    // Give the client field-by-field user-readable error messages
                    for(ConstraintViolation<?> violation : e.getConstraintViolations()) {
                        validationErrors.put(violation.getPropertyPath().toString(), violation.getMessage());
                    }

                } catch (Exception e) {

                    // HTTP codes in the 5xx range mean that something went wrong on the server,
                    // and it's not necessarily the client's fault.
                    response.status(500);
                    success = false;
                    error= e.getLocalizedMessage();
                }
                Map<String, Object> responseBody = new HashMap<String, Object>();
                responseBody.put("success", success);
                responseBody.put("error", error);
                responseBody.put("week", week);
                responseBody.put("validation errors", validationErrors);
                return gson.toJson(responseBody);
            }
        });

        post(new Route("/v1/addReview") {
            @Override
            public Object handle(Request request, Response response) {

                Map<String,Object> responseBody = new HashMap<String, Object>();
                String error = "";
                boolean success = false;

                Session session = sessionFactory.openSession();
                Transaction tx = session.beginTransaction();
                try {
                    JSONObject jsonArray = new JSONObject(request.body());
                    // Request parameters "id" and "review"
                    long id = jsonArray.getLong("id");
                    String reviewJson = jsonArray.getString("review");
                    Review review = gson.fromJson(reviewJson, Review.class);

                    // Check if food_id of review already exists
                    Food dbFood = (Food) session.createQuery("FROM Food WHERE foodId = :id")
                            .setLong("id", id).iterate().next();

                    String reviewText = review.getText();

                    // Check if review text is empty
                    if (reviewText.equals("")) {
                        review.setFood(dbFood);
                        dbFood.getReviews().add(review);
                        session.update(dbFood);
                        tx.commit();
                        success = true;
                        response.status(200);

                    // If review text is not empty
                    } else {
                        try {
                            // Check if review text, reviewer, and food id already exists
                            Review dbReview = (Review) session.createQuery("FROM Review WHERE text = :reviewText AND reviewer = :reviewer AND food_id = :id")
                                    .setString("reviewText", reviewText)
                                    .setString("reviewer", review.getReviewer())
                                    .setLong("id", review.getId()).iterate().next();
                            success = false;
                            error = "Duplicate Review";

                        // If review text is not a duplicate
                        } catch (NoSuchElementException e) {
                            review.setFood(dbFood);
                            dbFood.getReviews().add(review);
                            session.update(dbFood);
                            tx.commit();
                            success = true;
                            response.status(200);
                        }
                    }

                // Catch if the sent request body is malformed
                } catch (JSONException e) {
                    success = false;
                    error = "Malformed body";

                // Catch exception if food does not exist from query on line 115
                } catch (NoSuchElementException e) {
                    success = false;
                    error = "Food does not exist in database";
                } finally {
                    session.close();
                }

                responseBody.put("success", success);
                responseBody.put("error", error);
                return responseBody;
            }
        });
    }

    public static Week reconstruct(){
        Calendar calendar = Week.getNormalizedCalendar();
        Week week = new Week();
        for(int i=0; i<7; i++) {
            calendar.add(Calendar.DATE, 1);
            Date date = calendar.getTime();
            Day day = new Day();
            Session session = sessionFactory.openSession();
            try {
                Query query = session.createQuery("FROM Meal WHERE date = :date")
                        .setDate("date", date);
                List<Meal> meals = query.list();
                for (Meal meal : meals)
                    day.setMeal(meal, meal.getMealType());
            } catch (NoSuchElementException e) {
                return null;
            } finally {
                session.close();
            }
            week.setDay(day, Weekday.values()[i]);
        }
        return week;
    }

    private static SessionFactory createSessionFactory() {
        Configuration configuration = new Configuration().configure();
//        if(System.getenv("DATABASE_URL") != null)
//            configuration.setProperty("hibernate.connection.url", System.getenv("DATABASE_URL"));
        return configuration.buildSessionFactory(
                new StandardServiceRegistryBuilder()
                        .applySettings(configuration.getProperties())
                        .build());
    }
}
