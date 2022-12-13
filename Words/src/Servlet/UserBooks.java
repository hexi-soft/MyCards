package Servlet;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.*;

import common.*;
import hexi.dbc.Jdbc;

public class UserBooks extends HttpServlet {
	private static final long serialVersionUID = 1;
	
	public static ArrayList<TreeMap<String,String>> get_user_books (String uid)
	{
		ArrayList<TreeMap<String,String>> res = new ArrayList<TreeMap<String,String>>();
		String sql = "select id, title from user_book join textbooks on bid=id where uid='"+uid+"'";
		try{
			ResultSet r = Jdbc.query(sql);
			while(r.next()){
				TreeMap<String,String> book = new TreeMap<String,String>();
				book.put("id", r.getString(1));
				book.put("title", r.getString(2));
				res.add(book);
			}
		}catch(SQLException e){
			e.printStackTrace();
		}
		return res;
	}
	
	public void doGet(HttpServletRequest request, HttpServletResponse response)
	  throws IOException, ServletException
	{
		response.setContentType("application/json;charset=UTF-8");
		response.setHeader("Cache-Control", "no-cache");
		
		String uid  = (String) request.getParameter("uid");
		
		String resp = "null";
		
		if (uid!=null)
		{
			if (IsContain(uid))
				resp = Json.toJson(get_user_books(uid));
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
		 System.out.println(Json.toJson(UserBooks.get_user_books("a")));
	}

}
