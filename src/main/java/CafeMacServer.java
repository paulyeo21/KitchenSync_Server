import static spark.Spark.*;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.hibernate.*;
import org.hibernate.Session;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import spark.*;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.text.NumberFormat;
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

                try {
                    Week week = reconstruct();
                    response.type("application/json");
                    if (week != null) {
                        return gson.toJson(week);
                    }
                    return "";

                } catch (ConstraintViolationException e) {

                    // From P. Cantrell Jokes Server
                    // https://github.com/pcantrell/jokes-spark/blob/master/src/main/java/edu/macalester/jokes/ApiRoute.java
                    // HTTP codes in the 4xx range mean that the client submitted a bad request.
                    // 400 is a good one for validation errors.
                    response.status(400);

                    Map<String,Object> responseBody = new HashMap<String, Object>();
                    responseBody.put("success", false);
                    responseBody.put("error", e.getLocalizedMessage());

                    // Give the client field-by-field user-readable error messages
                    Map<String,String> errors = new HashMap<String, String>();
                    for(ConstraintViolation<?> violation : e.getConstraintViolations()) {
                        errors.put(violation.getPropertyPath().toString(), violation.getMessage());
                    }
                    responseBody.put("validationErrors", errors);
                    return gson.toJson(responseBody);

                } catch (Exception e) {

                    // HTTP codes in the 5xx range mean that something went wrong on the server,
                    // and it's not necessarily the client's fault.
                    response.status(500);

                    Map<String,Object> responseBody = new HashMap<String, Object>();
                    responseBody.put("success", false);
                    responseBody.put("error", e.getLocalizedMessage());
                    responseBody.put("For more information", "http://macalester.cafebonappetit.com/" +
                            "hungry/cafe-mac/");
                    return gson.toJson(responseBody);
                }
            }
        });

        get(new Route("/v1/test") {
            @Override
            public Object handle(Request request, Response response) {
                response.type("application/json");

                Session session = sessionFactory.openSession();

                Food test = (Food) session.createQuery("from Food where name = :name")
                        .setString("name", "Criss cut fries").iterate().next();

                Map<String,Object> responseBody = new HashMap<String, Object>();
                Map<String, Integer> ratingReview = new HashMap<String, Integer>();
                ratingReview.put("Rating", test.getRating());
                ratingReview.put("Rating Count", test.getRatingCount());
                responseBody.put(test.getName(), ratingReview);

                return gson.toJson(responseBody);
            }
        });

        post(new Route("/v1/addReview/") {
            @Override
            public Object handle(Request request, Response response) {
                Session session = sessionFactory.openSession();
                Transaction tx = session.beginTransaction();

                // Update database with reviews made by users
                Query query = session.createQuery("UPDATE Food SET rating = :review WHERE name = :name")
                        .setString("name", "Vegetable egg rolls")
                        .setString("review", ":review");
                query.executeUpdate();

                tx.commit();
                response.status(200);
                return "";
            }
        });

        post(new Route("/v1/incrementRating") {
            @Override
            public Object handle(Request request, Response response) {
                Session session = sessionFactory.openSession();
                Transaction tx = session.beginTransaction();

                Integer rating = 0;

                try {
                    rating = Integer.parseInt(request.queryParams("rating"));
                } catch (NumberFormatException e) {
                }
                String foodName = request.queryParams("name");

                // Increment the rating for specific food item in database
                Query query = session.createQuery("UPDATE Food SET ratingCount = ratingCount + 1 WHERE name = :name")
                        .setString("name", foodName);

                System.out.println("--->" + rating);
                System.out.println("--->" + foodName);

                Query query1 = session.createQuery("UPDATE Food SET rating = rating + :num WHERE name = :name")
                        .setString("name", foodName)
                        .setInteger("num", rating);

                query.executeUpdate();
                query1.executeUpdate();

                tx.commit();
                response.status(200);
                return "";
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
            try {
                Session session = sessionFactory.openSession();
                Query query = session.createQuery("FROM Meal WHERE date = :date")
                        .setDate("date", date);
                List<Meal> meals = query.list();
                for (Meal meal : meals)
                    day.setMeal(meal, meal.getMealType());
                session.close();
            } catch (NoSuchElementException e) {
                return null;
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
