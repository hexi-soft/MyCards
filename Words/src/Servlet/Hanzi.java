package Servlet;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import common.Json;
import hexi.dbc.Jdbc;

public class Hanzi extends HttpServlet{
	
	public void doGet(HttpServletRequest request, HttpServletResponse response)
	  throws IOException, ServletException
	{
		response.setContentType("application/json;charset=UTF-8");
		response.setHeader("Cache-Control", "no-cache");
		
		request.setCharacterEncoding("utf-8");
		String word = (String) request.getParameter("word");
			
		String resp = "";
				
		if (word != null)
		{
			resp = Json.toJson(check_out(word));
		}
		response.getWriter().write(resp);
	}

	public String check_out(String param)
	{
		String ret = "";
		ResultSet r = null;
		String sql = "";
		try{
			if (!param.equals("%"))
			{
				sql = "update hz set memo='pass' where zi='"+param+"'";
				Jdbc.execute_sql(sql);
			}
			sql = "select top 1 * from hz where memo != 'pass' order by freq desc";
			r = Jdbc.query(sql);
			if (r.next()){
				ret = r.getString("zi");
			}
		}catch(SQLException e){
			System.out.println("SQL ERROR "+": "+sql);
			System.out.println(e.getMessage());
		}	
		return ret;
	}
	
	public void doPost(HttpServletRequest request,HttpServletResponse response)
	  throws IOException,ServletException
	 {
		doGet(request, response);
	 }
			
	public void init(ServletConfig config) throws ServletException {
	 }		

}
