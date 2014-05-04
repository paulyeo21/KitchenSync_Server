import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.List;
import java.util.Set;


/**
 * Created by paulyeo on 4/4/14.
 */
public class CafeMacParser {

    private static final String CAFE_MAC_URL = "http://macalester.cafebonappetit.com/hungry/cafe-mac";
    private static SessionFactory sessionFactory = createSessionFactory();

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
            week.clean();
            Week oldWeek = CafeMacServer.reconstruct();
            Session session = sessionFactory.openSession();
            Transaction tx = session.beginTransaction();
            if (oldWeek != null) {
                Set<Food> oldFood = week.mergeFoods(oldWeek.getStrippedFoods());
                for (Food food: oldFood) {
                    session.save(food);
                }
                for (Meal meal : oldWeek.getMeals()) {
                    session.delete(meal);
                }
            }
            for (Meal meal : week.getMeals()) {
                session.save(meal);
            }
            session.close();
            tx.commit();
            session.close();
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

