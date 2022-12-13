package hexi.web;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import common.Log;
import common.MyXML;
import common.UFileReader;
import common.UFileWriter;
import hexi.web.base.Link;

public class Crawler {
	
	public static String get_content_by_url(String url){
		String result = "Nothing";
		try{
			if (url.startsWith("file://")){
				String head = "file://";
				String path = url.substring(head.length());
				System.out.println(path);
				String content = UFileReader.read(path);
				if (!content.isEmpty())
					result = content;
			}else if(url.startsWith("http")) {
				Document doc = Jsoup.connect(url).get();
				String content = doc.text();
				result = content;
			}else if(url.startsWith("library://")) {
				String category = url.substring("library://".length());
				category = category.replace("/", File.separator);
				String path = "f:"+File.separator+"my_library"+category;
				Log.d(path);
				MyXML my_xml = new MyXML();
				String str_xml = my_xml.get_file_tree_xml(path);
				result = str_xml;
			}
		}catch(IOException e){
			e.getMessage();
		}
		return result;
	}
	
	public static ArrayList<Link> get_urls_in_url(String url) {
		ArrayList<Link> urls = new ArrayList<Link>();
		try {
			Document doc = Jsoup.connect(url).get();
			Elements links = doc.select("a[href]");
            for(Element link : links){
            	String linkHref = link.attr("abs:href");
            	if(linkHref.length()==0){
            		continue;
            	}
            	String linkTitle = link.text();
            	urls.add(new Link(linkTitle,linkHref));
            }	
		}catch(IOException e) {
			Log.debug(e);
		}		
		return urls;
	}
	
	public static ByteArrayOutputStream fetch(String url){
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
			Log.debug(e.getMessage());
		}
		return baos;
    }
	
	public static void download(String url, String filename) {
		String r = fetch(url).toString();
		if (!r.isEmpty()) {
			UFileWriter.write(filename, r);
		}
	}
	
	public static void tmain(String[] args) {
		String s = get_content_by_url("library:///");
		MyXML x = new MyXML(s);
		x.xml2file_tree("");
		Log.d(s);
	}
	
	public static void main(String[] args) {
		String a = "https://warwick.ac.uk/fac/soc/al/research/collections/base/lecturetranscripts/ps";
		ArrayList<Link> urls = get_urls_in_url(a);
		for(Link link : urls) {
			String url = link.get_href();
			String title = link.get_title();
			//Log.d(title+": "+url);
			
			if (url.endsWith(".txt")) {
				if (!title.endsWith(".txt")) {
					title += ".txt";
				}
				download(url, "d:\\corpus\\BASE\\Physical Sciences\\"+title);
				Log.d(title+": "+url);
			}
		}
	}

}
