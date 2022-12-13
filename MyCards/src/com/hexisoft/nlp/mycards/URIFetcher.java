package com.hexisoft.nlp.mycards;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONTokener;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import hexi.common.MD5;
import hexi.common.MyXML;

import com.hexisoft.common.android.MyLog;

public class URIFetcher extends Thread {

	public URIFetcher(MyCardsActivity context, String url, String content_type, SQLiteDatabase db, MyLog log){
		ctx = context;
		type = content_type;
		_url = url;
		this.db = db;
		this.log = log;
	}
	
	static public MyCardsActivity ctx;
	private String type;
	private String _url;
	private SQLiteDatabase db;
	MyLog log;
	
	public String content = "";
	
	public void add2urls(String url, String title){
    	try{
    		if(url.length()>0 && url.startsWith("http") && title.trim().length()>0){
    			String url_md5 = MD5.md5s(url);
    			ContentValues cv = new ContentValues();
    			cv.put("md5", url_md5);
    			cv.put("url", url);
    			cv.put("title", title);
    			db.insert("urls", null, cv);
    		}
        	//trace("add "+title+" to urls");
    	}catch(Exception e){
    		;//trace(e.getMessage());
    	}
    }
    
	public void add2cards(Card c){
	   	ctx.l(c.mBook + " "+c.mUnit + " "+c.mFront);
	   	try {
    	ContentValues cv = new ContentValues();
    	cv.put("id", c.mId);
    	cv.put("front", c.mFront);
    	cv.put("back", c.mBack);
    	cv.put("book", c.mBook);
    	cv.put("unit", c.mUnit);
    	cv.put("memo", c.mMemo);
    	cv.put("type", "word");
    	db.insert("cards", null, cv);
	   	}catch(Exception e) {
	   		ctx.l("Fail to add card "+c.mBook + " "+c.mUnit + " "+c.mFront);
	   		ctx.l(e.toString());
	   	}
    }
    
    public void add2sents(Card c){
    	ContentValues cv = new ContentValues();
    	cv.put("i", c.mId);
    	cv.put("e", c.mFront);
    	cv.put("c", c.mBack);
    	cv.put("b", c.mBook);
    	cv.put("u", c.mUnit);
    	cv.put("m", c.mMemo);
    	db.insert("sents", null, cv);
    	ctx.l(c.mBook+" "+c.mUnit);
    }
    
    public ArrayList<Card> loadCards(String jsonCards) throws Exception{
    	ArrayList<Card> cards = new ArrayList<Card>();
    	JSONArray array = (JSONArray) new JSONTokener(jsonCards).nextValue();
    	for(int i=0; i<array.length(); ++i){
    		cards.add(new Card(array.getJSONObject(i)));
    	}
    	ctx.l("load "+cards.size()+" sents");
    	return cards;
    }
    
    public ByteArrayOutputStream fetch(String url){
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			URL _url = new URL(url);
			URLConnection connection = _url.openConnection();
			//connection.setUseCaches(false);
			connection.connect();
			InputStream inStream = connection.getInputStream();
			BufferedInputStream bufIn = new BufferedInputStream(inStream);
			byte tmpBytes[] = new byte[4096];
			int len;
			while ((len=bufIn.read(tmpBytes))!=-1) {
				baos.write(tmpBytes,0,len);			
			}
		} catch (Exception e) {
			Log.d("My",e.getMessage());
		}
		return baos;
    }
    
	public void run() {
        try{
        	if (type.equals("text/html")){
        		Document doc = Jsoup.connect(_url).get();
                Elements links = doc.select("a[href]");
                for(Element link : links){
                	String linkHref = link.attr("abs:href");
                	if(linkHref.length()==0){
                		continue;
                	}
                	String linkTitle = link.text();
                	if (linkTitle.length()==0)
                		continue;
                	add2urls(linkHref, linkTitle);
                }
                content = doc.text();
                content = content.replace("|","");
        		//ctx.text = new Text(content,10000);
        		//ctx.trace(content);
                //ctx.textSpeak();
        	}else if(type.equals("json-card")){
 /*       	
        		URL url = new URL(_url);
        		URLConnection connection = url.openConnection();
        		connection.setUseCaches(false);
        		ctx.l("connecting server "+_url);
        		connection.connect();
        		ctx.l("connecting server ok");
        		InputStream inStream = connection.getInputStream();
        		ctx.l("Got input stream ok");
        		Scanner in = new Scanner(inStream);
        		ctx.l("begin to scan received data lines...");
        		while(in.hasNext()){
        			String line = in.nextLine();
        			content += line;
        		}
        		ctx.l("received all lines.");
 */
        		ByteArrayOutputStream out = fetch(_url);
        		content = out.toString();
        		ArrayList<Card> cards = loadCards(content);
        		ctx.l("load cards "+ cards.size());
        		for(Card c:cards){
        			add2cards(c);
        		}
        		ctx.l("add "+cards.size()+" cards ok");
        	}else if(type.equals("json-sent")){
        		content = fetch(_url).toString();
        		ArrayList<Card> cards = loadCards(content);
        		for(Card c:cards){
        			add2sents(c);
        		}
        		ctx.l("add "+cards.size()+" sentences ok");
        	}else if(type.equals("xml-library")){
        		ByteArrayOutputStream bytes = fetch(_url);
        		String xml = bytes.toString("utf8");
        		MyXML my_xml = new MyXML(xml);
        		my_xml.set_logger(log);
        		my_xml.xml2file_tree("");
        	}
        }catch(Exception e){
        	ctx.l(e.getMessage());
        }
	}
}
