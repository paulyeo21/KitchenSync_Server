/**
 * Created by paulyeo on 3/10/14.
 */

import spark.*;

import java.util.ArrayList;

import static spark.Spark.*;

public class OldHelloSpark {
    // Just store POST data within a ArrayList for now
//    public static ArrayList<String> things = new ArrayList<String>();
//
//    public static void main(String[] args) {
//
//        get(new Route("/hello") {
//            @Override
//            public Object handle(Request request, Response response) {
//                return "Hello Spark MVC Framework!";
//            }
//        });
//
//        get(new Route("/goodbye") {
//            @Override
//            public Object handle(Request request, Response response) {
//                return "Goodbye Spark MVC Framework";
//            }
//        });
//
//        get(new Route("/parameter/:param") {
//            @Override
//            public Object handle(Request request, Response response) {
//                StringBuffer myParam = new StringBuffer(request.params(":param"));
//                return "I reversed your param for ya \"" + myParam.reverse() + "\"";
//            }
//        });
//
//        get(new Route("/list") {
//            @Override
//            public Object handle(Request request, Response response) {
//                StringBuilder html = new StringBuilder();
//
//                if (HelloSpark.things.isEmpty()) {
//                    html.append("<b>Try adding some things to your list</b>");
//                } else {
//                    html.append("<ul>");
//                    for (String thing : HelloSpark.things) {
//                        html.append("<li>").append(thing).append("</p>");
//                    }
//                    html.append("</ul>");
//                }
//                return html.toString();
//            }
//        });
//
//        post(new Route("/add/:item") {
//            @Override
//            public Object handle(Request request, Response response) {
//                HelloSpark.things.add(request.params(":item"));
//                response.status(200);
//                return response;
//            }
//        });
//    }
}
