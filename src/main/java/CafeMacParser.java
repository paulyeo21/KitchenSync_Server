import com.google.gson.JsonObject;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.json.simple.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by paulyeo on 4/4/14.
 */
public class CafeMacParser {

    private static final String CAFE_MAC_URL = "http://macalester.cafebonappetit.com/hungry/cafe-mac";
    private static SessionFactory sessionFactory = createSessionFactory();
    private static Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

    public Week parse(String url) {
        Document doc = null;
        try {
            doc = Jsoup.connect(url).get();
        } catch (IOException e) {
            //TODO Handle this error by emailing someone
        }
        return new Week(doc);
    }

    public static void main (String[] args) {
        Week week = new CafeMacParser().parse(CAFE_MAC_URL);
        List<Meal> meals = week.getMeals();

    //        Check if the week is null before it gets saved
        if (!week.isEmpty()) {
//            String jsonMenu = gson.toJson(week);
//            CachedServerResponse menu = new CachedServerResponse();

            Session session = sessionFactory.openSession();
            Transaction tx = session.beginTransaction();

    //        Delete previous rows from db tables
//            String delete = "DELETE FROM CachedServerResponse";
//            Query query = session.createQuery(delete);
//            query.executeUpdate();

//            menu.setMenu(jsonMenu);
//            menu.setCreatedAt(new Date());
//            session.save(menu);

            for (Meal meal : meals) {
                session.save(meal);
                for (Station station: meal.getStations()) {
                    session.save(station);
                    for (Food food: station.getFoods()) {

                        // Check whether food item already exists in DB
                        try { Food duplicateItem = (Food) session.createQuery("FROM Food f where f.name = :name")
                                .setString("name", food.getName()).iterate().next();
                        } catch (NoSuchElementException e) {
                            session.save(food);
                        }
                    }
                }
            }

            tx.commit();

//            try { Food duplicateItem = (Food) session.createQuery("FROM Food f where f.name = :name")
//                    .setString("name", "Yo").iterate().next();
//            } catch (NoSuchElementException e) {
//                System.out.println("-----> Nope");
//            }
        }
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

