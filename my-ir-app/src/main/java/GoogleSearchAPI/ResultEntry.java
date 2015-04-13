package GoogleSearchAPI;

public class ResultEntry {
	private String title;
	private String url;
	private String snippet;
	
	public ResultEntry(String title, String url, String snippet){
		this.title = title;
		this.url = url;
		this.snippet = snippet;
	}
	
	public String getTitle(){
		return title;
	}
	
	public String getURL(){
		return url;
	}
	
	public String getSnippet(){
		return snippet;
	}
}
