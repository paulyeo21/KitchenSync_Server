import static spark.Spark.*;

import com.google.gson.Gson;
import org.hibernate.*;
import org.hibernate.Session;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import spark.*;

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

                // Handle error when parsing hasn't happened but server still requests
                response.type("application/json");
                CachedServerResponse cachedServerResp = (CachedServerResponse) session.createQuery("from CachedServerResponse cached " +
                            "order by cached.createdAt desc").iterate().next();
                return cachedServerResp.getMenu();
            }
        });
    }

    private static SessionFactory createSessionFactory() {
        Configuration configuration = new Configuration().configure();
        if(System.getenv("DATABASE_URL") != null)
            configuration.setProperty("hibernate.connection.url", System.getenv("DATABASE_URL"));
        return configuration.buildSessionFactory(
                new StandardServiceRegistryBuilder()
                        .applySettings(configuration.getProperties())
                        .build());
    }
}