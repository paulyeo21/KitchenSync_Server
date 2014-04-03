import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;

import static spark.Spark.*;
import spark.*;

public class HelloSpark {

    private static final String url = "http://macalester.cafebonappetit.com/hungry/cafe-mac/";
    private static String string = null;
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static void main(String[] args) {

        // for heroku deployment, comment for local host
        if(System.getenv("PORT") != null)
            setPort(Integer.parseInt(System.getenv("PORT")));

        try {
            Document doc = Jsoup.connect(url).get();
            Week week = new Week(doc);
            string = gson.toJson(week);

        } catch (IOException e) {
            e.printStackTrace();
        }

        before(new Filter() {
            @Override
            public void handle(Request req, Response res) {
                res.type("application/json");
            }
        });

        get(new Route("/") {
            @Override
            public Object handle(Request request, Response response) {
                return string;
            }
        });
    }
}