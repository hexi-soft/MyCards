package hexi.web.servlet;

import hexi.dbc.Jdbc;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.TreeMap;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import common.Json;
import common.Log;

public class pupil_sentences extends HttpServlet{

	private static final long serialVersionUID = 1;
	
	private String get_pupil_sentences(HttpServletRequest request){
		String re = "[]";
		String book = request.getParameter("book");
		String module = request.getParameter("module");
		ArrayList<Object> items = new ArrayList<Object>();
		String sql = "select * from sentences";
		if (book != null && module !=null){
			sql += " where b='"+book+"' and m="+module+" order by s_id";
		}
		try{
			ResultSet r = Jdbc.query(sql);
			while(r.next()){
				int s_id = r.getInt(1);
				String e = r.getString("e");
				String c = r.getString("c");
				Date t = r.getDate("t");
				String memo = r.getString("memo");
				String b = r.getString("b");
				int m = r.getInt("m");
				TreeMap<String,Object> item = new TreeMap<String,Object>();
				item.put("s_id", s_id);
				item.put("e",e);
				item.put("c",c);
				item.put("t",t);
				item.put("b", b);
				item.put("memo", memo);
				item.put("m",m);
				items.add(item);
			}
			re = "{\"type\":\"sentences\",\"value\":"+Json.toJson(items)+"}";
		}catch(SQLException e){
			System.out.println(e.getMessage());
			//e.printStackTrace();
		}
		return re;	
	}
	
	private String get_all_sentences(String uid){
		String re = "[]";
		ArrayList<Object> items = new ArrayList<Object>();
		String sql = "select * from sentences";
		try{
			ResultSet r = Jdbc.query(sql);
			while(r.next()){
				int i = r.getInt(1);
				String e = r.getString("e");
				String c = r.getString("c");
				//Date t = r.getDate("t");
				String m = r.getString("memo");
				String b = r.getString("b");
				int u = r.getInt("m");
				TreeMap<String,Object> item = new TreeMap<String,Object>();
				item.put("i", i);
				item.put("e",e);
				item.put("c",c);
				//item.put("t",t);
				item.put("b", b);
				item.put("u", u);
				item.put("m",m);
				items.add(item);
			}
			re = Json.toJson(items);
		}catch(SQLException e){
			System.out.println(e.getMessage());
			//e.printStackTrace();
		}
		return re;	
	}
	
	private boolean del_sentence(String sid) {
		boolean ret = false;
		String sql = "delete from sentences where s_id="+sid;
		try {
			Jdbc.execute_sql(sql);
			ret = true;
		}catch(SQLException e) {
			Log.debug(e.getMessage());
		}
		return ret;
	}
	
	private String del_sentence(String uid, HttpServletRequest request) {
		boolean ret = false;
		if (!uid.contentEquals("a")) {
			return Json.toJson(ret);
		}
		String sid = request.getParameter("sid");
		if (sid != null) {
			ret = del_sentence(sid);
		}
		return Json.boolean2Json(ret);
	}
	
	private boolean update_sentence(String sid, String e, String c) {
		boolean r = false;
		try {
			Jdbc.prepare_sql("update sentences set e=?,c=? where s_id="+sid);
			Jdbc.pstmt.setString(1, e);
			Jdbc.pstmt.setString(2, c);
			Jdbc.pstmt.executeUpdate();
			r = true;
		}catch(SQLException ex) {
			Log.debug(ex.getMessage());
		}
		return r;
	}
	
	private String update_sentence(String uid, HttpServletRequest request) {
		boolean r = false;
		String i = request.getParameter("sid"); 
		String e = request.getParameter("e");
		String c = request.getParameter("c");
		if(c==null) {
			c = "";
		}
		r = update_sentence(i, e, c);
		return Json.toJson(r);
	}
	
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
			String cmd = request.getParameter("cmd");
			if(cmd!=null) {
				if(cmd.contentEquals("delete")) {
					resp = del_sentence(uid, request);
				}else if(cmd.contentEquals("update")){
					update_sentence(uid, request);
				}else if(cmd.contentEquals("all")) {
					resp = get_all_sentences(uid);
				}
			}else {
				resp = get_pupil_sentences(request);
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
		
	}
	
	public static void main(String[] args) {
		pupil_sentences pw = new pupil_sentences();
		String r = pw.get_pupil_sentences(null);
		System.out.println(r.substring(0,200));
	}

}
