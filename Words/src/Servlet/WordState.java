package Servlet;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import common.Json;
import hexi.dbc.Jdbc;

public class WordState extends HttpServlet{
	
	public void doGet(HttpServletRequest request, HttpServletResponse response)
	  throws IOException, ServletException
	{
		response.setContentType("application/json;charset=UTF-8");
		response.setHeader("Cache-Control", "no-cache");
				
		String word = (String) request.getParameter("word");
		String uid = (String) request.getParameter("uid");
		String state = (String) request.getParameter("state");
		String value = (String) request.getParameter("value");
		String resp = "false";
				
		if (uid != null && word != null && word.matches("[a-zA-Z]+")
				&& state != null && value != null)
		{
			resp = Json.toJson(save_user_word_state(word, uid, state, value));
		}
		response.getWriter().write(resp);
	}

	public boolean save_user_word_state(String word, String uid, String state, String value)
	{
		String sql = "update student_word set "+state+"="+value
				+" where uid='"+uid+"' and word='"+word+"'";
		try{
			Jdbc.execute_sql(sql);
			return true;
		}catch(SQLException e){
			String msg = e.getMessage();
			System.out.println("WordState.save_user_word_state: "+msg+"\n"+sql);
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

}
