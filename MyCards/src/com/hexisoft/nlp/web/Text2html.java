package com.hexisoft.nlp.web;

public class Text2html {

	public static String getHTML(String title, String text){
		text = text.replace("<","&lt;");
		text = text.replace(">","&gt;");
		text = text.replaceAll("(\r\n)+","<p>");
		String html = "<!DOCTYPE html><html><head><title>";
		html += title + "</title></head><body>";
		html += text;
		html += "<style>body{background-color:#000;color:#fff}</style>";
		html += "</body></html>";
		return html;
	}
}
