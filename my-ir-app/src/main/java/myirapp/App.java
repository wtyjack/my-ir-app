package myirapp;

import static spark.Spark.get;
import static spark.SparkBase.port;
import static spark.SparkBase.staticFileLocation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import spark.ModelAndView;
import spark.template.velocity.VelocityTemplateEngine;
import vips.Vips;
import BlockRetrievalAPI.BlockRetrieval;
import GoogleSearchAPI.GoogleSearch;
import GoogleSearchAPI.ResultEntry;
import XMLHandler.DOMTree;

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
        	
        	double start, end;
        	//1. Get request
        	String person = request.queryParams("person");
        	String attribute = request.queryParams("attribute");   
        	System.out.printf("1. Query: \"%s %s\"\n", person, attribute);
        	//System.out.println("person: "+ person+" attribute: "+attribute);
        	
        	
        	
        	//2. Goole search, get urls
        	start = System.currentTimeMillis()/1000.0;
        	System.out.printf("2. Google Search ... ");
        	GoogleSearch gsc = new GoogleSearch();
            ArrayList<ResultEntry> result = gsc.getSearchResult("walid database", 5);
            StringBuilder result_string = new StringBuilder();
            
            /* test -- print result */
            for(int i=0; i< result.size(); i++) {
            	ResultEntry temp = result.get(i);
            	result_string.append(i);
            	result_string.append("<br/>");
            	result_string.append(temp.getTitle());
            	result_string.append("<br/>");
            	result_string.append(temp.getURL());
            	result_string.append("<br/>");
            	result_string.append(temp.getSnippet());
            	result_string.append("<br/><br/>");
            	/*ResultEntry temp = result.get(i);
            	System.out.printf("%d: \n", (i+1));
            	System.out.printf("Tile: %s\n", temp.getTitle());
            	System.out.printf("URL: %s\n", temp.getURL());
            	System.out.printf("Snippet: %s\n", temp.getSnippet());
            	System.out.printf("---------------------\n");*/
            }
            System.out.printf("Done\n");
            end = System.currentTimeMillis()/1000.0;
            System.out.println("Excution time: " + ( end - start));
            
        	
            
            
        	//3. Vips
            start = System.currentTimeMillis()/1000.0;
            System.out.printf("Vips ... ");
            //for(int i=0; i< result.size(); i++) {
            	Vips vips = new Vips();
            	vips.enableGraphicsOutput(false);	// disable graphics output
            	vips.enableOutputToFolder(false);	// disable output to separate folder 
            	vips.setOutputFileName("./output/result");
            	vips.setPredefinedDoC(8);			// set permitted degree of coherence
            	vips.startSegmentation(result.get(0).getURL());		// start segmentation on page
            //}
            System.out.printf("Done\n");	
            end = System.currentTimeMillis()/1000.0;
            System.out.println("Excution time: " + ( end - start));
            
            ArrayList<String> block = new ArrayList<String>();
            DOMTree dtree = new DOMTree("output/result.xml", 3);	
            block = dtree.getDocuments();
            
            
            // TODO: query expansion
            String querystr = "Publication Walid";

        	//4. Block Retrieval
            BlockRetrieval br = new BlockRetrieval();
			try {
				br.genDocIndex(block);
				br.search(querystr);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            
        	//5.Output
        	Map<String, Object> attributes = new HashMap<>();
            attributes.put("person", result_string.toString());

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
