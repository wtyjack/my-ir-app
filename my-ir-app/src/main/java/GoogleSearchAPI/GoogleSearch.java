package GoogleSearchAPI;

import java.util.ArrayList;

import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.services.customsearch.Customsearch;
import com.google.api.services.customsearch.model.Result;
import com.google.api.services.customsearch.model.Search;

public class GoogleSearch {
	

    //api key
    final private String API_KEY = "AIzaSyBGf8ZbmGPTSGD_62gimFN6gyfJaexYuwI";
    //custom search engine ID
    final private String SEARCH_ENGINE_ID = "002665980160487806545:madupghnco0";
 
    
    /* 
     * Google search for 'keyword'
     * Fetch 'num' pages
     */
    public ArrayList<ResultEntry> getSearchResult(String keyword, int num){
    	
         // Set up the HTTP transport and JSON factory
        HttpTransport httpTransport = new NetHttpTransport();
        JsonFactory jsonFactory = new JacksonFactory();
        
        Customsearch customsearch = new Customsearch(httpTransport, jsonFactory,null);
 
        
        long count = 1;
        ArrayList<ResultEntry> resultentry = new ArrayList<ResultEntry>();
        
        while(true) {
            java.util.List<Result> resultList = null;
            try {
            		//Execute search query
                    Customsearch.Cse.List list = customsearch.cse().list(keyword);
                    list.setKey(API_KEY);
                    list.setCx(SEARCH_ENGINE_ID);
    
                    list.setStart(count);
                    Search results = list.execute();
                    resultList = results.getItems();
                    
                    
                    //Parse result
                    if(resultList != null && resultList.size() > 0){
                           for(com.google.api.services.customsearch.model.Result result: resultList){
                        	   
                        	   ResultEntry temp = new ResultEntry(result.getHtmlTitle(), 
                        			   							result.getLink(),
                        			   							result.getHtmlSnippet());
                        	   
                        	   resultentry.add(temp);
                        	   count++;
                        	   if(count > num)
                        		   return resultentry;
                           }
                    }
            		
            }catch (Exception e) {
                    e.printStackTrace();
            }
        }//End while(true)
        
    }//End getSearchResult

}
