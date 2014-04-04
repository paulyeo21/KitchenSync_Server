import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

/**
 * Created by paulyeo on 4/4/14.
 */
public class CafeMacParser {

    private static final String url = "http://macalester.cafebonappetit.com/hungry/cafe-mac/";
    private Gson gson = new GsonBuilder().setPrettyPrinting().create();
    public static Week model;

    public String render(Object model){
        return gson.toJson(model);
    }

    public static void main (String[] args) {
        CafeMacParser cafeMac = new CafeMacParser();
        try {
            Document doc = Jsoup.connect(url).get();
            model = new Week(doc);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
