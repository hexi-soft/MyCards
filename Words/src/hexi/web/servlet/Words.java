package hexi.web.servlet;

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
import common.Log;
import hexi.dbc.Jdbc;

public class Words extends HttpServlet {
	
	static String get_book_module_words(String book, String module) {
		String re = "[]";
		ArrayList<Object> items = new ArrayList<Object>();
		String sql = "select * from epc";
		if (book !=null && !book.isEmpty()){
			sql += " where book='"+book+"'";
			if (module !=null && !module.isEmpty()){
				sql += " and module="+module;
			}
		}
		try{
			Log.debug(sql);
			ResultSet r = Jdbc.query(sql);
			while(r.next()){
				String e = r.getString("English");
				String c = r.getString("Chinese");
				String b = r.getString("book");
				Integer m = r.getInt("module");
				String p = r.getString("phonetic");
				int i = r.getInt("wid");
				TreeMap<String,Object> item = new TreeMap<String,Object>();
				item.put("e",e);item.put("p", p);item.put("wid", i);
				item.put("c",c);item.put("b", b);item.put("m",m);
				items.add(item);
			}
			re = Json.toJson(items);
		}catch(SQLException e){
			e.printStackTrace();
		}
		return re;
	}
	
	private String req_book_module_words(HttpServletRequest request){
		String re = "[]";
		String book = request.getParameter("book");
		String module = request.getParameter("module");
		if (book!=null&&!book.isEmpty()&&module!=null) {
			re = get_book_module_words(book, module);
		}
		return re;
	}
	
	public void doGet(HttpServletRequest request, HttpServletResponse response)
	  throws IOException, ServletException
	{
		response.setContentType("application/json;charset=UTF-8");
		response.setHeader("Cache-Control", "no-cache");		
		String resp = req_book_module_words(request);
		response.getWriter().write(resp);
	}
	
	public static boolean IsContain(String uid)
	{
		boolean ret = false;
		String sql = "select * from users where uid=?";
		try{
			Jdbc.prepare_sql(sql);
			Jdbc.setString(1, uid);
			ResultSet r = Jdbc.query();
			if (r.next()){
				ret = true;
			}
		}catch(SQLException e){
			e.printStackTrace();
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
	 
	 public static void main(String[] args) throws ServletException {
		 String ws = get_book_module_words("E3-2(fltrp3)", "1");
		 Log.d(ws);
	 }

}
