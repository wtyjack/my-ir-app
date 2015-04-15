package BlockRetrievalAPI;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
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
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

import java.util.*;
import java.io.IOException;

public class BlockRetrieval {
	// Members
	private StandardAnalyzer analyzer;
	private Directory index;
	
	// Methods
	// Constructor
	public BlockRetrieval(){
		index = new RAMDirectory();
		analyzer = new StandardAnalyzer();
	}

	// Generate document index based on the list of blocks 
	public void genDocIndex(ArrayList<String> docs) throws IOException{
		IndexWriterConfig config = new IndexWriterConfig(analyzer);
		IndexWriter w = new IndexWriter(index, config);
		
		for (int i = 0; i < docs.size(); i++) {
			addDoc(w, docs.get(i));
		}
		w.close();
	}
	
	// Do the search
	public void search(String querystr) throws IOException, ParseException{
		int hitsPerPage = 10;
		QueryParser qparser = new QueryParser("content", analyzer);
		Query q = qparser.parse(querystr);

		IndexReader reader = DirectoryReader.open(index);
		//IndexSearcher searcher = new IndexSearcher(reader);
		IndexSearcher searcher = new IndexSearcher(reader);
		TopScoreDocCollector collector = TopScoreDocCollector.create(hitsPerPage);
		searcher.search(q, collector);
		ScoreDoc[] hits = collector.topDocs().scoreDocs;
		
		System.out.println("Found " + hits.length + " hits.");
	    for(int i = 0; i < hits.length; i++) {
	      int docId = hits[i].doc;
	      double docScore = hits[i].score;
	      Document d = searcher.doc(docId);
	      System.out.println((i + 1) + ". " + d.get("content") + "\n" + docScore);
	      System.out.println(searcher.explain(q, docId) + "\n\n");
	    }

	    // reader can only be closed when there
	    // is no need to access the documents any more.
	    reader.close();
	
	}
	
	// compute relevance score for each document
	private double computeRelScore(){
		
		return 0;
	}
	
	private static void addDoc(IndexWriter w, String content) throws IOException {
		  Document doc = new Document();
		  /*
		  FieldType ft = new FieldType();
		  ft.setStored(true);
		  ft.setTokenized(true);
		  ft.setStoreTermVectors(true);
		  ft.setStoreTermVectorPositions(true);
		  */
		  doc.add(new TextField("content", content, Field.Store.YES));

		  w.addDocument(doc);
	}
		 
}

