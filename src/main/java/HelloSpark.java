import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import spark.Request;
import spark.Response;
import spark.Route;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;

import static spark.Spark.*;
import spark.*;

//public class HelloSpark {
//    // Just store POST data within a ArrayList for now
//    public static ArrayList<String> things = new ArrayList<String>();
//    public static Document doc;
//
//    public static void main(String[] args) {
//
//        get(new Route("/hello") {
//            @Override
//            public Object handle(Request request, Response response) {
////                response.type("text/json");
//                return "Hello Spark.";
//            }
//        });
//        get(new Route("/hello.xml") {
//            @Override
//            public Object handle(Request request, Response response) {
//
//                String title = "";
//                Elements dayData = null;
//
//                try {
//                    doc = Jsoup.connect("http://macalester.cafebonappetit.com/hungry/cafe-mac/").get();
//                    dayData = doc.getElementsByClass("eni-menu-day");
//                    title = doc.title();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
////                response.type("text/xml");
//                return dayData;
//
////                JSONObject obj = new JSONObject();
////                obj.put("name", "hello world.");
////                response.type("text/json");
////                return obj;
//            }
//        });
//        get(new Route("/goodbye") {
//            @Override
//            public Object handle(Request request, Response response) {
//                return "Goodbye Spark MVC Framework";
//            }
//        });
//        get(new Route("/parameter/:param") {
//            @Override
//            public Object handle(Request request, Response response) {
//                StringBuffer myParam = new StringBuffer(request.params(":param"));
//                return "I reversed your param for ya \"" + myParam.reverse() + "\"";
//            }
//        });
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
//        post(new Route("/add/:item") {
//            @Override
//            public Object handle(Request request, Response response) {
//                HelloSpark.things.add(request.params(":item"));
//                response.status(200);
//                return response;
//            }
//        });
//    }
//}


public class HelloSpark {
    public static Deque<Article> articles = new ArrayDeque<Article>();

    public static void main(String[] args) {
        setPort(Integer.parseInt(System.getenv("PORT")));

        get(new Route("/") {
            @Override
            public Object handle(Request request, Response response) {
                String title = "Kitchen Sync Sample Blog";
                String createArticleLink = "<a href='/article/create'>Write Article</a>";
                StringBuilder html = new StringBuilder();

                html.append("<h1>").append(title).append("</h1>").append(createArticleLink);
                html.append("<hr>");

                if(HelloSpark.articles.isEmpty()) {
                    html.append("<b>No articles have been posted</b>");
                } else {
                    for(Article article : HelloSpark.articles) {
                        if(article.readable()) {
                            html.append("<a href='/article/read/"+ article.getId() + "'>Title</a>" + ": " ).append(article.getTitle())
                                    .append("<br/>")
                                    .append(article.getCreatedAt())
                                    .append("<br/>")
                                    .append("Summary: ").append(article.getSummary())
                                    .append("<br/>")
                                    .append(article.getEditLink()).append(" | ").append(article.getDeleteLink())
                                    .append("</p>");
                        }
                    }
                }

                return html.toString();
            }
        });

        get(new Route("/article/create") {
            @Override
            public Object handle(Request request, Response response) {
                StringBuilder form = new StringBuilder();

                form.append("<form id='article-create-form' method='POST' action='/article/create'>")
                        .append("Title: <input type='text' name='article-title' />")
                        .append("<br/>")
                        .append("Summary: <input type='text' name='article-summary' />")
                        .append("<br/>")
                        .append("</form>")
                        .append("<textarea name='article-content' rows='4' cols='50' form='article-create-form'></textarea>")
                        .append("<br/>")
                        .append("<input type='submit' value='Publish' form='article-create-form' />");

                return form.toString();
            }
        });

        post(new Route("/article/create") {
            @Override
            public Object handle(Request request, Response response) {
                String title = request.queryParams("article-title");
                String summary = request.queryParams("article-summary");
                String content = request.queryParams("article-content");

                Article article = new Article(title, summary, content, HelloSpark.articles.size() + 1);

                HelloSpark.articles.addFirst(article);

                response.status(201);
                response.redirect("/");
                return "";
            }
        });

        get(new Route("/article/read/:id") {
            @Override
            public Object handle(Request request, Response response) {
                Integer id = Integer.parseInt(request.params(":id"));
                StringBuilder html = new StringBuilder();

                for(Article article : HelloSpark.articles) {
                    if(id.equals(article.getId())) {
                        html.append("<a href='/'>Home</a>").append("<p />")
                                .append("Title: ").append(article.getTitle()).append("<br />")
                                .append(article.getCreatedAt())
                                .append("<p>").append(article.getContent()).append("</p>");
                        break;
                    }
                }
//                response.type("article/read/:id/json");
                return html.toString();
            }
        });

        get(new Route("/article/update/:id") {
            @Override
            public Object handle(Request request, Response response) {
                Integer id = Integer.parseInt(request.params(":id"));
                StringBuilder form = new StringBuilder();

                for(Article article : HelloSpark.articles) {
                    if(id.equals(article.getId())) {
                        form.append("<form id='article-create-form' method='POST' action='/article/update/:id'>")
                                .append("Title: <input type='text' name='article-title' value='").append(article.getTitle()).append("' />")
                                .append("<br/>")
                                .append("Summary: <input type='text' name='article-summary' value='").append(article.getSummary()).append("' />")
                                .append("<input type='hidden' name='article-id' value='").append(article.getId().toString()).append("' />")
                                .append("<br/>")
                                .append("</form>")
                                .append("<textarea name='article-content' rows='4' cols='50' form='article-create-form'>").append(article.getContent())
                                .append("</textarea>")
                                .append("<br/>")
                                .append("<input type='submit' value='Update' form='article-create-form' />");
                        break;
                    }
                }
                return form.toString();
            }
        });

        post(new Route("/article/update/:id") {
            @Override
            public Object handle(Request request, Response response) {
                String title = request.queryParams("article-title");
                String summary = request.queryParams("article-summary");
                String content = request.queryParams("article-content");
                Integer id = Integer.parseInt(request.queryParams("article-id"));

                for(Article article : HelloSpark.articles) {
                    if(id.equals(article.getId())) {
                        article.setTitle(title);
                        article.setContent(content);
                        article.setSummary(summary);
                    }
                }

                response.status(200);
                response.redirect("/");
                return "";
            }
        });

        get(new Route("/article/delete/:id") {
            @Override
            public Object handle(Request request, Response response) {
                Integer id = Integer.parseInt(request.params(":id"));

                for(Article article : HelloSpark.articles) {
                    if(id.equals(article.getId())) {
                        article.delete();
                    }
                }

                response.status(200);
                response.redirect("/");
                return "";
            }
        });

//        org.apache.log4j.BasicConfigurator.configure();
    }
}