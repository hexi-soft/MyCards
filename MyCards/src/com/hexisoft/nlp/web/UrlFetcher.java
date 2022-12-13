package com.hexisoft.nlp.web;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.util.Log;
import android.util.Pair;


public class UrlFetcher extends Thread{
	
	public final static String TEXT_HTML = "text/html";
	
	public UrlFetcher(){
	}
	public UrlFetcher(String url, String content_type){
		type = content_type;
		_url = url;
	}
	
	private String type;
	private String _url;
	private String content;
	ByteArrayOutputStream bytes;
	
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
			baos.close();
		} catch (Exception e) {
			Log.d("My",e.getMessage());
		}
		return baos;
    }
	
	public byte[] getBytes(String url){
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		HttpURLConnection connection = null;
		try {
			URL _url = new URL(url);
			connection = (HttpURLConnection)_url.openConnection();
			//connection.setUseCaches(false);
			connection.connect();
			InputStream inStream = connection.getInputStream();
			BufferedInputStream bufIn = new BufferedInputStream(inStream);
			byte tmpBytes[] = new byte[4096];
			int len;
			while ((len=bufIn.read(tmpBytes))!=-1) {
				baos.write(tmpBytes,0,len);			
			}
			baos.close();
		} catch (Exception e) {
			Log.d("My",e.getMessage());
			return null;
		}finally{
			if (connection != null){
				connection.disconnect();
			}
		}
		return baos.toByteArray();
    }
	
	public String getString(String url){
		return new String(getBytes(url));
	}
	
	public String getText(String url){
		String r = "";
		try{
			Document doc = Jsoup.connect(url).get();
			r = doc.text();
		}catch(IOException e){
			Log.i("My", e.getMessage());
		}
		return r;
	}
	
	public Map<String,Object> getTextWithLinks(String url){
		Map<String,Object> result = new TreeMap<String,Object>();
		String text = "";
		ArrayList<Link> Links = new ArrayList<Link>();
		try{
			Log.i("My", url);
			Document doc = Jsoup.connect(url).get();
			
			 Elements links = doc.select("a[href]");
             for(Element link : links){
             	String linkHref = link.attr("href");
             	if(linkHref.length()==0){
             		continue;
             	}
             	linkHref = link.attr("abs:href");
             	if(linkHref.length()==0){
             		continue;
             	}
             	String linkTitle = link.text();
             	Link l = new Link(linkTitle, linkHref);
             	Links.add(l);
             }
			text = doc.text();
		}catch(Exception e){
			Log.i("My", e.getMessage());
		}
		result.put("text", text);
		result.put("links", Links);
		return result;
	}

	public Pair<Document,ArrayList<Link>> getDocumentWithLinks(){
		ArrayList<Link> Links = new ArrayList<Link>();;
		Document doc = null;
		try{
			doc = Jsoup.connect(_url).get();	
			 Elements links = doc.select("a[href]");
             for(Element link : links){
             	String linkHref = link.attr("href");
             	if(linkHref.length()==0){
             		continue;
             	}
             	linkHref = link.attr("abs:href");
             	if(linkHref.length()==0){
             		continue;
             	}
             	String linkTitle = link.text();
             	Link l = new Link(linkTitle, linkHref);
             	Links.add(l);
             }
		}catch(Exception e){
			e.printStackTrace();
		}
		if (doc != null) {
			return new Pair<Document, ArrayList<Link>>(doc, Links);
		}else {
			return null;
		}
	}
	
	public void run() {
        try{
        	if (type.equals(TEXT_HTML)){
        		Document doc = Jsoup.connect(_url).get();
                content = doc.text();
        	}else if(type.equals("text-raw")){
        		URL url = new URL(_url);
        		URLConnection connection = url.openConnection();
        		connection.setUseCaches(false);
        		connection.connect();
        		InputStream inStream = connection.getInputStream();	
        		Scanner in = new Scanner(inStream);
        		while(in.hasNext()){
        			String line = in.nextLine();
        			content += line;
        		}
        		in.close();
        	}else if(type.equals("binary-raw")){
        		bytes = fetch(_url);
        	}
        }catch(Exception e){
        	Log.d("My", e.getMessage());
        }
	}
}
