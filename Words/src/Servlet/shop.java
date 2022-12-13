package Servlet;

import hexi.dbc.Jdbc;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.TreeMap;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import common.Json;


public class shop extends HttpServlet{
	
	public void doGet(HttpServletRequest request, HttpServletResponse response)
	  throws IOException, ServletException
	{
		response.setContentType("application/json;charset=UTF-8");
		response.setHeader("Cache-Control", "no-cache");
				
		String category = (String) request.getParameter("category");
		String uid = (String) request.getParameter("uid");
			
		String resp = "[]";
				
		if (uid != null && category != null && category.matches("[a-zA-Z]+"))
		{
			resp = check_out(category);
		}
		response.getWriter().write(resp);
	}

	public String check_out(String param)
	{
		ArrayList<TreeMap<String,Object>> goods = new ArrayList<TreeMap<String,Object>>();
		ResultSet r = null;
		String sql = "select * from goods where category='"+param+"'";
		try{
			r = Jdbc.query(sql);
			while (r.next()){
				String title = r.getString(1);
				String category = r.getString(2);
				String desc = r.getString(3);
				double price = r.getDouble(4);
				String memo = r.getString(5);
				TreeMap<String,Object> item = new TreeMap<String,Object>();
				item.put("title", title);
				item.put("category", category);
				item.put("desc", desc);
				item.put("price", price);
				item.put("countable", memo);
				goods.add(item);
			}
		}catch(SQLException e){
			String msg = e.getMessage();
			System.out.println("SQL ERROR: "+msg+"\n"+sql);
		}	
		return Json.toJson(goods);
	}
	
	public void doPost(HttpServletRequest request,HttpServletResponse response)
	  throws IOException,ServletException
	 {
		doGet(request, response);
	 }
			
	public void init(ServletConfig config) throws ServletException {
	 }
	
	public static void main(String[] args){
		shop g = new shop();
		
		System.out.println(g.check_out("clothes"));
	}
	
}


