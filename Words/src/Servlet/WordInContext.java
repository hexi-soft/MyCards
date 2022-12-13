package Servlet;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import common.Json;
import common.UFileReader;
import hexi.dbc.Jdbc;

public class WordInContext extends HttpServlet {
	
	private String uid = null;
	private ArrayList<Object> user_texts = new ArrayList<Object>();
	private ArrayList<Object> word_contexts = new ArrayList<Object>();

	public ArrayList<Object> get_user_texts (String uid)
	{
		ArrayList<Object> res = new ArrayList<Object>();
		ArrayList<TreeMap<String,String>> books = UserBooks.get_user_books(uid);
		for(TreeMap<String,String> book : books)
		{
			String text = UFileReader.read((String)book.get("path"));
			book.put("text", text);
			book.put("title", book.get("title"));
			res.add(book);
		}
		return res;
	}
	
	public ArrayList<Object> get_word_contexts(String uid, String word)
	{
		ArrayList<Object> books = get_user_texts(uid);
		ArrayList<Object> contexts = new ArrayList<Object>();
		Pattern p = Pattern.compile("\\w.{0,80}\\b"+word+"e?s?d?\\b.*?[\\.!\\?]\\s*");
		
		for(Object o : books)
		{
			TreeMap<String,Object> book = (TreeMap<String,Object>)o;
			String text = (String)book.get("text");
			Matcher m = p.matcher(text);
			while(m.find()){
				TreeMap<String,Object> item = new TreeMap<String,Object>();
				String context = m.group();
				item.put("bookID", book.get("id"));
				item.put("title", book.get("title"));
				item.put("content", context);
				contexts.add(item);
			}
		}
		return contexts;
	}
	public void doGet(HttpServletRequest request, HttpServletResponse response)
	  throws IOException, ServletException
	{
		response.setContentType("application/json;charset=UTF-8");
		response.setHeader("Cache-Control", "no-cache");
		
		uid  = (String) request.getParameter("uid");
		String word  = (String) request.getParameter("word");
		String resp = "null";
		
		if (uid!=null && word!=null && word.matches("[a-zA-Z]+"))
		{
			if (IsContain(uid))
				resp = Json.toJson(get_word_contexts(uid, word));
		}
		response.getWriter().write(resp);
	}
	
	public boolean IsContain(String uid)
	{
		ResultSet r = null;
		String sql = "select * from users where uid='"+uid+"'";
		try{
			r = Jdbc.query(sql);
			if (r.next()){
				return true;
			}
		}catch(SQLException e){
			System.out.println(e.getMessage()+"\r\nSQL: "+sql);
			//e.printStackTrace();
		}	
		return false;
	}
	
	public void doPost(HttpServletRequest request,HttpServletResponse response)
	  throws IOException,ServletException
	  {
		doGet(request, response);
	  }
	
	 public void init(ServletConfig config) throws ServletException {

	 }
	 
	 public static void main(String[] args) throws ServletException {
		 WordInContext w = new WordInContext();
		 ServletConfig config=null;
		 //w.init(config);
		 System.out.println(Json.toJson(w.get_word_contexts("a","either")));
	}

}
