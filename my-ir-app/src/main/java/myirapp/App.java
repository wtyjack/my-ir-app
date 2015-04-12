package myirapp;

import static spark.Spark.get;
import static spark.Spark.halt;
import static spark.Spark.post;
import static spark.SparkBase.port;
import static spark.SparkBase.staticFileLocation;

import java.util.HashMap;
import java.util.Map;

import spark.ModelAndView;
import spark.template.velocity.VelocityTemplateEngine;
/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
    	port(2345);
    	staticFileLocation("/");
    	
    	get("/hello", (request, response) -> {
    		Map<String, Object> attributes = new HashMap<>();
            attributes.put("hello", 1111);

            // The hello.ftl file is located in directory:
            // src/test/resources/spark/template/freemarker
            return new ModelAndView(attributes, "hello.wm");
        }, new VelocityTemplateEngine());
    	
        post("/hello", (request, response) ->
            "Hello World: " + request.body()
        );
        

        get("/private", (request, response) -> {
            response.status(401);
            return "Go Away!!!";
        });

        get("/users/:name", (request, response) -> "Selected user: " + request.params(":name"));

        get("/news/:section", (request, response) -> {
            response.type("text/xml");
            return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><news>" + request.params("section") + "</news>";
        });

        get("/protected", (request, response) -> {
            halt(403, "I don't think so!!!");
            return null;
        });

        get("/redirect", (request, response) -> {
            response.redirect("/news/world");
            return null;
        });
        
        get("/:name", (request, response) -> {
            System.out.println( "Hello: " + request.params(":name"));
            return null;
        });
        
       /* get("/", (request, response) -> {
        	
			return null;	
        });

        get("/", (request, response) -> {
    		Map<String, Object> attributes = new HashMap<>();
            //attributes.put("hello", 1111);

            // The hello.ftl file is located in directory:
            // src/test/resources/spark/template/freemarker
            return new ModelAndView(attributes, "index.html");
        }, new VelocityTemplateEngine());
        
        post("/", (request, response) ->
        request.body()
        );
        */
        
    }
}
