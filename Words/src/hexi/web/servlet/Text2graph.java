package hexi.web.servlet;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import common.Json;
import common.Log;
import hexi.nlp.Sentence2neo4j;
import hexi.web.Crawler;

public class Text2graph extends HttpServlet{
	private static final long serialVersionUID = 1;
	static Logger log = Log.log;
	static Sentence2neo4j s2n;
	
	String add_sentence(String uid, HttpServletRequest request) {
		String resp = "false";
		String sentence = request.getParameter("sent");
		if (sentence != null){
			s2n.insert_sentence(sentence);
			resp = "true";
		}
		return resp;
	}
	
	String get_word_next_words(String uid, HttpServletRequest request) {
		String resp = "[]";
		String word = request.getParameter("word");
		resp = Json.toJson(s2n.get_next_words(word));
		return resp;
	}
	
	String get_words_next_words(String uid, HttpServletRequest request) {
		String resp = "[]";
		String words = request.getParameter("words");
		return resp;
	}
	
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException
	{
		response.setContentType("text/xml;charset=UTF-8");
		response.setHeader("Cache-Control", "no-cache");
		request.setCharacterEncoding("utf-8");
		String uid = request.getParameter("uid");
		String resp = "false";
		if (uid != null)
		{
			String cmd = request.getParameter("cmd");
			if (cmd != null) {
				if (cmd.contentEquals("add_sent")) {
					resp = add_sentence(uid, request);
				}else if(cmd.contentEquals("word_next")) {
					resp = get_word_next_words(uid, request);
				}else if(cmd.contentEquals("words_next")) {
					resp = get_words_next_words(uid, request);
				}
			}
		}
		response.getWriter().write(resp);
	}
	
	public void doPost(HttpServletRequest request,HttpServletResponse response)
			throws IOException,ServletException
	{
		doGet(request, response);
	}
					
	public void init(ServletConfig config)
			throws ServletException
	{
		s2n = new Sentence2neo4j();
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
