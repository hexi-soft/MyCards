package hexi.common;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import com.hexisoft.common.android.MyLog;
import com.hexisoft.nlp.base.Sentence;

/**
 * This code was originally written for
 * Erik's Lucene intro java.net article
 */
public class Searcher {
	
	private String mIndexDirectory;
	private File mIndexDir;
	private IndexSearcher mSearcher;
	
	public Searcher(String indexDirectory) throws Exception {
		mIndexDir = new File(indexDirectory);
		if (!mIndexDir.exists() || !mIndexDir.isDirectory()) {
		    throw new Exception(mIndexDir +
		     " does not exist or is not a directory.");
		}
	    Directory fsDir = FSDirectory.getDirectory(mIndexDir, false);
	    mSearcher = new IndexSearcher(fsDir);
	}

  public ArrayList<Sentence> search(String[] args) {
    String q = "";
    for (String term : args) {
    	q += term +" AND ";
    }
    if (q.length()>0) {
    	q = q.substring(0,q.length()-5);
    }
    MyLog.f("q:"+q);
    return search(q);
  }

  public ArrayList<Sentence> search(String q)
  {
	  ArrayList<Sentence> hitList = new ArrayList<Sentence>();
	  Query query;
	try {
		query = QueryParser.parse(q, "content", new StandardAnalyzer());
	    Hits hits = mSearcher.search(query);
	    for (int i = 0; i < hits.length(); i++) {
	        Document doc = hits.doc(i);
	        String e = doc.get("content");
	        String b = doc.get("book");
	        int u = Integer.parseInt(doc.get("module"));
	        String memo = doc.get("memo");
	        Sentence sent = new Sentence(i,e,"",b, u, memo);
	        hitList.add(sent);
	      }
	} catch (ParseException e) {
		MyLog.f(e);
	}catch(IOException e) {
		MyLog.f(e);
	}
    return hitList;
  }
}
