import static spark.Spark.*;

import com.google.gson.Gson;
import org.hibernate.*;
import org.hibernate.Session;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import spark.*;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Map;

public class CafeMacServer {

    private static SessionFactory sessionFactory = createSessionFactory();

    public static void main(String[] args) {

//        for heroku deployment
        if(System.getenv("PORT") != null)
            setPort(Integer.parseInt(System.getenv("PORT")));

        get(new Route("/v1/menu") {
            @Override
            public Object handle(Request request, Response response) {
                Session session = sessionFactory.openSession();

                try {
                response.type("application/json");
                CachedServerResponse cachedServerResp = (CachedServerResponse) session.createQuery("from CachedServerResponse cached " +
                            "order by cached.createdAt desc").iterate().next();

                return cachedServerResp.getMenu();

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

                    return responseBody;

                } catch (Exception e) {

                    // HTTP codes in the 5xx range mean that something went wrong on the server,
                    // and it's not necessarily the client's fault.
                    response.status(500);

                    Map<String,Object> responseBody = new HashMap<String, Object>();
                    responseBody.put("success", false);
                    responseBody.put("error", e.getLocalizedMessage());
                    responseBody.put("For more information", "http://macalester.cafebonappetit.com/" +
                            "hungry/cafe-mac/http://macalester.cafebonappetit.com/hungry/cafe-mac/");
                    return responseBody;
                }
            }
        });
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