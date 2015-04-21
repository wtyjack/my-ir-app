package BlockRetrievalAPI;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

import XMLHandler.Block;

public class BlockRetrieval {
	// Members
	private StandardAnalyzer analyzer;
	private Directory index;
	
	private static String phoneReg = "\\d{3}?-\\d{3}-\\d{4}|\\(\\d{3}\\)-\\d{3}-\\d{4}"; 
	private static String emailReg = "[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})";
	
	// Methods
	// Constructor
	public BlockRetrieval(){
		index = new RAMDirectory();
		analyzer = new StandardAnalyzer();
	}

	// Generate document index based on the list of blocks 
	public void genDocIndex(ArrayList<Block> docs) throws IOException{
		IndexWriterConfig config = new IndexWriterConfig(analyzer);
		IndexWriter w = new IndexWriter(index, config);
		
		for (int i = 0; i < docs.size(); i++) {
			addDoc(w, docs.get(i));
		}
		w.close();
	}
	
	// Do the search
	public ArrayList<RetrievalItem> search(String querystr) throws IOException, ParseException{
		int hitsPerPage = 10;
		QueryParser qparser = new QueryParser("content", analyzer);
		Query q = qparser.parse(querystr);
		

		IndexReader reader = DirectoryReader.open(index);
		//IndexSearcher searcher = new IndexSearcher(reader);
		IndexSearcher searcher = new IndexSearcher(reader);
		TopScoreDocCollector collector = TopScoreDocCollector.create(hitsPerPage);
		searcher.search(q, collector);
		ScoreDoc[] hits = collector.topDocs().scoreDocs;
		
		ArrayList<RetrievalItem> result = new ArrayList<RetrievalItem>(); 
		//RetrievalItem item = new RetrievalItem();
		//System.out.println("Found " + hits.length + " hits.");

	    for(int i = 0; i < hits.length; i++) {
	      int docId = hits[i].doc;
	      double docScore = hits[i].score;
	      RetrievalItem item = new RetrievalItem();
	      Document d = searcher.doc(docId);
	      item.rank = i + 1;
	      item.content = d.get("content");
	      item.url = d.get("url");
	      item.score = docScore;
	      result.add(item);
	      //System.out.println((i + 1) + ". " + d.get("content") + "\n" + docScore);
	      //System.out.println(searcher.explain(q, docId) + "\n\n");
	    }


	    // reader can only be closed when there
	    // is no need to access the documents any more.
	    reader.close();
	    return result;
	
	}
	
	// compute relevance score for each document
	private double computeRelScore(){
		
		return 0;
	}
	
	private static void addDoc(IndexWriter w, Block docs) throws IOException {
		  Document doc = new Document();
		  // extract phone num and email
		  doc.add(new TextField("content", extractContactInfo(docs.getContent()), Field.Store.YES));
		  doc.add(new StringField("url", docs.getUrl(), Field.Store.YES));

		  w.addDocument(doc);
	}
	
	public static String extractContactInfo(String content) {
		String tmp = content;
		Pattern r = Pattern.compile(emailReg);
		Matcher m = r.matcher(tmp);
		m.replaceAll(" email ");

		r = Pattern.compile(phoneReg);
		m = r.matcher(tmp);
		m.replaceAll(" phone ");

		return tmp;
	}
	
}

