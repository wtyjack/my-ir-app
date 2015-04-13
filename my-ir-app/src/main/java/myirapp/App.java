package myirapp;

import static spark.Spark.get;
import static spark.SparkBase.port;
import static spark.SparkBase.staticFileLocation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import spark.ModelAndView;
import spark.template.velocity.VelocityTemplateEngine;
import GoogleSearchAPI.GoogleSearch;
import GoogleSearchAPI.ResultEntry;
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

    		
        get("/result.html", (request, response) -> {
        	System.out.println("I'm here");
        	
        	
        	
        	//1. Get request
        	String person = request.queryParams("person");
        	String attribute = request.queryParams("attribute");        	
        	System.out.println("person: "+ person+" attribute: "+attribute);
        	
        	
        	
        	//2. Goole search, get urls
        	GoogleSearch gsc = new GoogleSearch();
            ArrayList<ResultEntry> result = gsc.getSearchResult("walid database", 15);
        	
            /* test -- print result */
            for(int i=0; i< result.size(); i++) {
            	ResultEntry temp = result.get(i);
            	System.out.printf("%d: \n", (i+1));
            	System.out.printf("Tile: %s\n", temp.getTitle());
            	System.out.printf("URL: %s\n", temp.getURL());
            	System.out.printf("Snippet: %s\n", temp.getSnippet());
            	System.out.printf("---------------------\n");
            }
    		
            
        	
        	//3. Vips
        	
        	
        	
        	//4. Retrieval
        	
        	
        	
        	//5.Output
        	Map<String, Object> attributes = new HashMap<>();
            attributes.put("person", person);

            // The hello.ftl file is located in directory:
            // src/test/resources/spark/template/freemarker
            return new ModelAndView(attributes, "result.html");
        	
        	
				
        }, new VelocityTemplateEngine());

        
        /*
        
        get("/hello", (request, response) -> {
    		Map<String, Object> attributes = new HashMap<>();
            attributes.put("hello", 1111);

            // The hello.ftl file is located in directory:
            // src/test/resources/spark/template/freemarker
            return new ModelAndView(attributes, "hello.wm");
        }, new VelocityTemplateEngine());
        
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
        */
    }
}
