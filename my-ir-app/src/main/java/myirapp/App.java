package myirapp;

import static spark.Spark.get;
import static spark.SparkBase.port;
import static spark.SparkBase.staticFileLocation;

import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.jsoup.Jsoup;

import spark.ModelAndView;
import spark.template.velocity.VelocityTemplateEngine;
import vips.Vips;
import BlockRetrievalAPI.BlockRetrieval;
import BlockRetrievalAPI.RetrievalItem;
import GoogleSearchAPI.GoogleSearch;
import GoogleSearchAPI.ResultEntry;
import XMLHandler.Block;
import XMLHandler.DOMTree;


/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws InterruptedException 
    {
    	port(2345);
    	staticFileLocation("/");        

    		
        get("/result.html", (request, response) -> {
        	
        	double start, end;
        	double t1, t2, t3, t4;
        	
        	
        	//1. Get request
        	String person = request.queryParams("person");
        	String attribute = request.queryParams("attribute");   
        	String query = person/* + " " + attribute*/;
        	System.out.printf("1. Query: \"%s %s\"\n", person, attribute);

        	
        	
        	//2. Google search, get urls
        	start = System.currentTimeMillis()/1000.0;
        	System.out.printf("2. Google Search ... ");
        	GoogleSearch gsc = new GoogleSearch();
            ArrayList<ResultEntry> result = gsc.getSearchResult(query, 3);
            System.out.printf("Done\n");
            end = System.currentTimeMillis()/1000.0;
            t1 = end - start;
            
            
            /*String [] result = {"https://www.cs.purdue.edu/homes/lsi/",
            						"https://www.cs.purdue.edu/homes/aref/", 
            						"http://ahmedmoustafa.wix.com/home#!publications/clku",
            						"https://www.cs.purdue.edu/people/aref", 
            						"http://faculty.washington.edu/mhali/Publications/Publications.htm",
            						"http://dblp.uni-trier.de/pers/hd/a/Aref:Walid_G=",
            						"http://www-users.cs.umn.edu/~mokbel/publications.htm",
            						"http://scholar.google.com/citations?user=vX45evgAAAAJ",
            						"http://www.researchgate.net/profile/Walid_Aref",
            						"http://dmlab.cs.umn.edu/publications.html",
            						"https://cs.uwaterloo.ca/~ilyas/publist.html"};*/
            
            
            
            
            //3. Vips
            ArrayList<Block> blocks = new ArrayList<Block>();
            start = System.currentTimeMillis()/1000.0;
            for(int i=0; i< result.size(); i++) {
            	System.out.printf("Vips %d ... ", i);
            	Vips vips = new Vips();
            	vips.enableGraphicsOutput(false);	// disable graphics output
            	vips.enableOutputToFolder(false);	// disable output to separate folder 
            	vips.setOutputFileName("./output/result");
            	vips.setPredefinedDoC(8);			// set permitted degree of coherence
            	try{
            		System.out.println(result.get(i).getURL());
            		vips.startSegmentation(result.get(i).getURL());		// start segmentation on page
            		//vips.startSegmentation("https://www.cs.purdue.edu/homes/lsi/");		// start segmentation on page
            	    //vips.startSegmentation(result[i]);
            	}catch(Exception e){
            		continue;
            	}
            	
            	DOMTree dtree = new DOMTree("output/result.xml", 2, result.get(i).getURL());
            	//DOMTree dtree = new DOMTree("output/result.xml", 2, result[i]);
            	ArrayList<Block> temp_block= dtree.iterate();
            	blocks.addAll(temp_block);
            	System.out.printf("Done\n");
            }
            end = System.currentTimeMillis()/1000.0;
            t2 = end - start;
            

            
            
            // TODO: query expansion
            //String querystr = "Publication Walid";
           String querystr = "";
           ArrayList<ResultEntry> queryExpandResult = gsc.getSearchResult(attribute, 100);
           for(int i=0; i< queryExpandResult.size(); i++) {
        	   ResultEntry temp = queryExpandResult.get(i);
        	   querystr += temp.getSnippet()+" ";
           }
           //result_string.append(bindSnippet);
           querystr = Jsoup.parse(querystr).text();
            
           // // TEST
           try {
				PrintWriter out = new PrintWriter("Contact.txt");
				out.println(querystr);
				out.close();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
            
            byte[] encoded = null;
			try {
				if(attribute.equals("Publications"))
					encoded = Files.readAllBytes(Paths.get("Publications.txt"));
				else if(attribute.equals("Contact"))
					encoded = Files.readAllBytes(Paths.get("Contact.txt"));
				querystr = new String(encoded);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			querystr = BlockRetrieval.extractContactInfo(querystr);
			querystr = querystr.replaceAll("[^a-zA-Z0-9]", " ");
			querystr = querystr.substring(0, 8000);
            //System.out.println(querystr);



        	//4. Block Retrieval
			start = System.currentTimeMillis()/1000.0;
            BlockRetrieval br = new BlockRetrieval();
            ArrayList<RetrievalItem> rankResult = new ArrayList<RetrievalItem>();
			try {
				br.genDocIndex(blocks);
				rankResult = br.search(querystr);
			} catch (Exception e) {
				e.printStackTrace();
			}
			end = System.currentTimeMillis()/1000.0;
            t3 = end - start;
            
            
            
			StringBuilder result_string = new StringBuilder();
			result_string.append("Query: \""+query+"\"<br/>");
			result_string.append("Google Search Time: " + t1 + "<br/>");
			result_string.append("VIPS time: " +t2 + "<br/>");
			result_string.append("Block Retrieval Time: " + t3 + "<br/>");
			result_string.append("<br/><br/>");
			
			
			for (int i = 0; i < rankResult.size(); i++) {
				RetrievalItem item = rankResult.get(i);
				result_string.append("<article data-readmore aria-expanded=\"false\">");
				result_string.append("<b>Rank "+ item.rank+"</b>");
            	result_string.append("<br/>");
            	result_string.append("<b>Score</b>: " + item.score);
            	result_string.append("<br/>");
            	result_string.append("<b>URL</b>: ");
            	result_string.append("<a href=\"" + item.url + "\">" + item.url+  "</a>");
            	result_string.append("<br/>");
            	result_string.append("<p>");
            	result_string.append(item.content);
            	result_string.append("</p>");
				result_string.append("</article>");
            	result_string.append("<br/><br/>");
			}
			
			
			System.out.println("Google Search Time: " + t1);
			System.out.println("VIPS time: " + t2);
			System.out.println("Block Retrieval Time: " + t3);

			
        	//5.Output
        	Map<String, Object> attributes = new HashMap<>();
            attributes.put("result", result_string.toString());

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
