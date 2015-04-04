import java.applet.Applet;
import java.util.List;

import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.services.customsearch.Customsearch;
import com.google.api.services.customsearch.model.Search;


public class GoogleSearch extends Applet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	//final private String GOOGLE_SEARCH_URL = "https://www.googleapis.com/customsearch/v1?";
	 
    //api key
    final private String API_KEY = "AIzaSyBGf8ZbmGPTSGD_62gimFN6gyfJaexYuwI";
    //custom search engine ID
    final private String SEARCH_ENGINE_ID = "002665980160487806545:madupghnco0";
 
    //final private String FINAL_URL= GOOGLE_SEARCH_URL + "key=" + API_KEY + "&cx=" + SEARCH_ENGINE_ID;
 
    public static void main(String[] args){
 
    	GoogleSearch gsc = new GoogleSearch();
        gsc.getSearchResult("walid database");
        
    }
 
    public String getSearchResult(String keyword){
         // Set up the HTTP transport and JSON factory
        HttpTransport httpTransport = new NetHttpTransport();
        JsonFactory jsonFactory = new JacksonFactory();
        //HttpRequestInitializer initializer = (HttpRequestInitializer)new CommonGoogleClientRequestInitializer(API_KEY);
        Customsearch customsearch = new Customsearch(httpTransport, jsonFactory,null);
 
        List<com.google.api.services.customsearch.model.Result> resultList = null;
        try {
                Customsearch.Cse.List list = customsearch.cse().list(keyword);
                list.setKey(API_KEY);
                list.setCx(SEARCH_ENGINE_ID);
                //num results per page
                //list.setNum(50L);
 
                //for pagination
                list.setStart(1L);
                Search results = list.execute();
                resultList = results.getItems();
 
        }catch (Exception e) {
                e.printStackTrace();
        }
 
        
        int ct = 1;
        if(resultList != null && resultList.size() > 0){
               for(com.google.api.services.customsearch.model.Result result: resultList){
            	   System.out.printf("%d:\n", ct++);
            	   System.out.printf("Title: %s\n", result.getHtmlTitle());
                   System.out.printf("Link: %s\n", result.getLink());
                   System.out.printf("FormattedURL: %s\n", result.getFormattedUrl());
                   System.out.printf("Snippet: %s\n", result.getHtmlSnippet());
                   System.out.println("----------------------------------------");
                   return result.getHtmlTitle()+"\n"+result.getLink()+"\n"+result.getHtmlSnippet();
               }
        }
		return keyword;
    }
    
    
	public GoogleSearch() {
		// TODO Auto-generated constructor stub
	}


}
