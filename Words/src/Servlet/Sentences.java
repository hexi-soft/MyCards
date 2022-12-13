package Servlet;

import hexi.dbc.Jdbc;

import java.io.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.regex.Pattern;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.*;

import org.apache.log4j.Logger;

import common.*;

public class Sentences extends HttpServlet{
	
	public void doGet(HttpServletRequest request, HttpServletResponse response)
	  throws IOException, ServletException
	{
		response.setContentType("application/json;charset=UTF-8");
		response.setHeader("Cache-Control", "no-cache");
		request.setCharacterEncoding("utf-8");
		
		String uid = request.getParameter("uid");
			
		String resp = "false";
				
		if (uid != null)
		{
			Log.debug("uid="+uid);
			String cmd = request.getParameter("cmd");
			Log.debug("cmd="+cmd);
			if (cmd != null){
				if(cmd.equals("add")){
					resp = add(request);
				}else if(cmd.equals("query")){
					resp = query(request);
				}
			}
		}
		response.getWriter().write(resp);
	}

	private String query(HttpServletRequest request){
		String ret = "[]";
		String filter = request.getParameter("filter");
		Log.debug("filter="+filter);
		if (filter != null){
			ret = Json.toJson(find_out(filter));					
		}else{
			ret = Json.toJson(find_out(null));
		}
		return ret;
	}
	
	public static ArrayList<TreeMap<String,Object>> find_out(String filter)
	{
		ArrayList<TreeMap<String,Object>> sentenceList = new ArrayList<TreeMap<String,Object>>();
		ResultSet r = null;
		String sql = "select top 1000 * from sentences";
		if (filter != null)
			sql += " where "+filter;
		Log.debug(sql);
		try{
			r = Jdbc.query(sql);
			while (r.next()){
				TreeMap<String,Object> record = new TreeMap<String,Object>();
				String id = r.getString("s_id");
				String e = r.getString("e");
				String c = r.getString("c");
				record.put("id", id);
				record.put("e", e);
				record.put("c", c);
				sentenceList.add(record);
			}
		}catch(SQLException e){
			String msg = e.getMessage();
			System.out.println("SQL ERROR: "+msg+"\n"+sql);
		}	
		return sentenceList;
	}
	
	private String add(HttpServletRequest request){
		String ret = "false";
		String e = request.getParameter("e");
		String c = request.getParameter("c");
		Log.debug(e);
		Log.debug(c);

		if (e.isEmpty())
			return ret;
		if (c == null)
			c = "";
		String sql = "insert into sentences(e,c) values(?,?)";
		try{
			Jdbc.prepare_sql(sql);
			Jdbc.pstmt.setString(1, e);
			Jdbc.pstmt.setString(2, c);
			Jdbc.pstmt.execute();
			ret = "true";
		}catch(SQLException exception){
			Log.debug(exception.toString());
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

	public static void tmain(String[] args)throws IOException{
		//String sql = "insert into sentences(e,c,m) values(?,?,?)";
		String sql = "update sentences set unit=? where e=?";
		Jdbc.prepare_sql(sql);
		PreparedStatement pstmt = Jdbc.pstmt;
		File file = new File("d:\\tomcat\\bin\\lesson\\sentences0.txt");
		Scanner scanner = new Scanner(file);
		String book = "", module="", e="", c="";
		while(scanner.hasNextLine()){
			String line = scanner.nextLine().trim();
			Pattern p = Pattern.compile("\\d-\\d");
			if (p.matcher(line).matches()){
				book = line;
				continue;
			}
			p = Pattern.compile("\\d{1,2}");
			if (p.matcher(line).matches()){
				module = line;
				continue;
			}
			p = Pattern.compile("\\w.+");
			if (p.matcher(line).matches()){
				e = line;
				continue;
			}
			p = Pattern.compile("[\u4e00-\u9fa5]+.*");
			if (p.matcher(line).matches()){
				c = line;
				String memo = "p-"+book+"-"+module;
				try{
					pstmt.setString(1, module);
					pstmt.setString(2, e);
					//pstmt.setString(3, memo);
					pstmt.execute();
				}catch(SQLException exception){
					Log.debug(e+"("+memo+")");
					Log.debug(exception.toString());
				}
			}
		}
	}

}
