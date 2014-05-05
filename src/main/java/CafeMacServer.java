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

//        get(new Route("/v1/test") {
//            @Override
//            public Object handle(Request request, Response response) {
//                response.type("application/json");
//
//                Session session = sessionFactory.openSession();
//
//                Food test = (Food) session.createQuery("FROM Food WHERE name = :name")
//                        .setString("name", "Criss cut fries").iterate().next();
//
//                Map<String,Object> responseBody = new HashMap<String, Object>();
//                Map<String, Integer> rating = new HashMap<String, Integer>();
//                rating.put("ratingCount", test.getRatingCount());
//                rating.put("rating", test.getRating());
//                responseBody.put(test.getName(), rating);
//
//                return gson.toJson(responseBody);
//            }
//        });

        post(new Route("/v1/addReview") {
            @Override
            public Object handle(Request request, Response response) {

                Map<String,Object> responseBody = new HashMap<String, Object>();
                String error = "";
                boolean success = false;

                if (request.body() != null) {
                    Session session = sessionFactory.openSession();
                    Transaction tx = session.beginTransaction();
                    try {
                        JSONObject jsonArray = new JSONObject(request.body());
                        long id = jsonArray.getLong("id");
                        String reviewJson = jsonArray.getString("review");
                        Review review = gson.fromJson(reviewJson, Review.class);

                        // Check if review text already exists
                        Food dbFood = (Food) session.createQuery("FROM Food WHERE foodId = :id")
                                .setLong("id", id).iterate().next();
                        if (dbFood != null) {
                            String reviewText = review.getText();
                            if (reviewText.equals("")) {
                                review.setFood(dbFood);
                                dbFood.getReviews().add(review);
                                session.update(dbFood);
                                tx.commit();
                                success = true;
                                response.status(200);
                            } else {
                                Review dbReview = (Review) session.createQuery("FROM Review WHERE text = :reviewText")
                                        .setString("reviewText", reviewText).iterate().next();
                                if (dbReview != null) {
                                    review.setFood(dbFood);
                                    dbFood.getReviews().add(review);
                                    session.update(dbFood);
                                    tx.commit();
                                    success = true;
                                    response.status(200);
                                } else {
                                    success = false;
                                    error = "Duplicate Review";
                                }
                            }
                        } else {
                            success = false;
                            error = "Food not in database";
                        }
                    } catch (JSONException e) {
                        success = false;
                        error = "Malformed body";
                    } finally {
                        session.close();
                    }
                    // If no request body was sent
                }
                responseBody.put("success", success);
                responseBody.put("error", error);
                return responseBody;
            }
        });

 /*       post(new Route("/v1/incrementRating") {
            @Override
            public Object handle(Request request, Response response) {

                if (request.body() != null) {
                    Session session = sessionFactory.openSession();
                    Transaction tx = session.beginTransaction();

                    String foodName = "";
                    int rating = 0;

                    try {
                        foodName = request.queryParams("name");
                        rating = Integer.parseInt(request.queryParams("rating"));
                    } catch (NumberFormatException e) {

                    }

                    // Increment the rating for specific food item in database
                    Query query = session.createQuery("UPDATE Food SET ratingCount = ratingCount + 1 WHERE name = :name")
                            .setString("name", foodName);

                    Query query1 = session.createQuery("UPDATE Food SET rating = rating + :num WHERE name = :name")
                            .setString("name", foodName)
                            .setInteger("num", rating);

                    query.executeUpdate();
                    query1.executeUpdate();

                    tx.commit();
                    response.status(200);
                    return "";
                }
                return "";
            }
        });*/
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
