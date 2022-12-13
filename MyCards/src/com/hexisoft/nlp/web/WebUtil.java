package com.hexisoft.nlp.web;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.hexisoft.common.android.MyLog;

public class WebUtil {

	static final String haiciUrl = "http://dict.cn/";
	static final String bingUrl = "http://bing.com/";
	
	public static boolean isEnglish(String words) {
		if(words!=null && !words.isEmpty())
			return words.charAt(0)<='z';
		else
			return false;
	}
	
	static String getDetailsCN(Element div) {
		String res = "[]";
		if (div==null)
			return res;
		Element ul = div.select("ul").first();
		Elements children = ul.children();
		res = "";
		String explains = "";
		for(Element child : children) {
			explains +=child.text()+"|";
		}
		if (explains.length()>1) {
			res = explains.substring(0, explains.length()-1);
		}else {
			res = "[]";
		}
		return res;
	}
	
	static String getDetails(Element div) {
		String res = "[]";
		if (div==null)
			return res;
		int size = div.childrenSize();
		Elements children = div.children();
		res = "";
		String explains = "";
		for(int i=0; i<size; ++i) {
			Element element = children.get(i);
			if (element.is("span")||element.is("div")) {					
				explains = element.text();
			}else if(element.is("ol")) {
				for(Element e : element.children()) {
					explains +=e.text()+"|";
				}
				if(explains.length()>1) {
					explains = explains.substring(0, explains.length()-1);
				}
				res += explains+"||";
			}
		}
		if (res.length()>1) {
			res = res.substring(0,res.length()-2);
		}else {
			res = "[]";
		}
		if (res.isEmpty()) {
			res = "[]";
		}
		return res;
	}
		
	static String haici_basic(String word) {
		String ret = word;
		String url = haiciUrl+word;
		try {
			Document doc = Jsoup.connect(url).get();
			ret = (doc.getElementsByClass("dict-basic-ul").text());
		}catch(Exception e) {
			MyLog.f(e);
		}
		return ret;
	}
	
	static String haici_phonetic(String word) {
		String ret = word;
		String url = haiciUrl+word;
		try {
			Document doc = Jsoup.connect(url).get();
			ret = (doc.getElementsByClass("phonetic").text());
		}catch(Exception e) {
			MyLog.f(e);
		}
		return ret;
	}
	
	static String haici_detail(String word) {
		String ret = word;
		String url = haiciUrl+word;
		try {
			Document doc = Jsoup.connect(url).get();
			ret = getDetails(doc.getElementsByClass("layout detail").first());
		}catch(Exception e) {
			MyLog.f(e);
		}
		return ret;
	}

	static String haici_basic_cn(String word) {
		String ret = word;
		String url = haiciUrl+word;
		try {
			Document doc = Jsoup.connect(url).get();
			ret = getDetailsCN(doc.getElementsByClass("layout cn").first());
		}catch(Exception e) {
			MyLog.f(e);
		}
		return ret;
	}

	public static String lookupWordfromHaici(String word) {
		String res = word;
		if (isEnglish(word)) {
			res += " "+haici_phonetic(word)+"\r\n";
			res += "Basic: "+haici_basic(word)+"\r\n";
			res += "Detail: "+haici_detail(word);
		}else {
			res = haici_basic_cn(word);
		}
		return res;
	}
	
}
