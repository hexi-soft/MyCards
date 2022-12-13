package Servlet;

import hexi.dbc.Jdbc;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import common.*;

public class Checkout extends HttpServlet{
	
	public void doGet(HttpServletRequest request, HttpServletResponse response)
	  throws IOException, ServletException
	{
		response.setContentType("application/json;charset=UTF-8");
		response.setHeader("Cache-Control", "no-cache");
				
		String word = request.getParameter("word");
		String uid = request.getParameter("uid");
			
		String resp = "false";
				
		if (uid != null && word != null && word.matches("[a-zA-Z]+"))
		{
			resp = Json.toJson(check_out(word, uid));
		}
		response.getWriter().write(resp);
	}

	public boolean check_out(String param, String uid)
	{
		ResultSet r = null;
		String sql = "select * from words where word='"+param+"'";
		try{
			r = Jdbc.query(sql);
			if (r.next()){
				sql = "insert student_word values('"+uid+"','"+param+"',1,0,0,0)";
				Jdbc.execute_sql(sql);
				return true;
			}

		}catch(SQLException e){
			String msg = e.getMessage();
			System.out.println("SQL ERROR: "+msg+"\n"+sql);
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
