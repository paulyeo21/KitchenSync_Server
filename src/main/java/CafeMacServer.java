import static spark.Spark.*;
import spark.*;

public class CafeMacServer {

    public static void main(String[] args) {

        // for heroku deployment
        if(System.getenv("PORT") != null)
            setPort(Integer.parseInt(System.getenv("PORT")));

        final CafeMacParser cafeMac = new CafeMacParser();
        CafeMacParser.main(args);

        get(new Route("/") {
            @Override
            public Object handle(Request request, Response response) {
                response.type("application/json");
                return cafeMac.render(CafeMacParser.model);
            }
        });
    }
}