package hexi.web.servlet;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.TreeMap;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import common.Json;
import common.Log;

import hexi.dbc.Jdbc;

public class pupil_words extends HttpServlet{
	private static final long serialVersionUID = 1;
	
	private String get_pupil_words(HttpServletRequest request){
		String re = "[]";
		String book = request.getParameter("book");
		String module = request.getParameter("module");
		ArrayList<Object> items = new ArrayList<Object>();
		String sql = "select * from cards";
		if (book !=null && !book.isEmpty()){
			sql += " where book='"+book+"'";
			if (module !=null && !module.isEmpty()){
				sql += " and module='"+module+"'";
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
				String memo = r.getString("memo");
				int wid = r.getInt("wid");
				TreeMap<String,Object> item = new TreeMap<String,Object>();
				item.put("e",e);item.put("memo", memo);item.put("wid", wid);
				item.put("c",c);item.put("b", b);item.put("m",m);
				items.add(item);
			}
			re = Json.toJson(items);
		}catch(SQLException e){
			e.printStackTrace();
		}
		return re;
	}
	
	private String get_wrong_words(String uid){
		String re = "[]";
		ArrayList<Object> items = new ArrayList<Object>();
		String sql = "select English,Chinese,book,module,memo,c.wid from cards c, user_word_state u"
				+ " where uid='" + uid + "' and c.wid=u.wid and value=-1";
		try{
			Log.debug(sql);
			ResultSet r = Jdbc.query(sql);
			while(r.next()){
				String e = r.getString("English");
				String c = r.getString("Chinese");
				String b = r.getString("book");
				Integer m = r.getInt("module");
				String memo = r.getString("memo");
				int wid = r.getInt("wid");
				TreeMap<String,Object> item = new TreeMap<String,Object>();
				item.put("e",e);item.put("memo", memo);item.put("wid", wid);
				item.put("c",c);item.put("b", b);item.put("m",m);
				items.add(item);
			}
			re = Json.toJson(items);
		}catch(SQLException e){
			e.printStackTrace();
		}
		return re;
	}
	
	private String get_all_words(String uid){
		String re = "[]";
		ArrayList<Object> items = new ArrayList<Object>();
		String sql = "select * from cards";
		try{
			ResultSet r = Jdbc.query(sql);
			while(r.next()){
				String e = r.getString("English");
				String c = r.getString("Chinese");
				int i = r.getInt("wid");
				String b = r.getString("book");
				String m = r.getString("memo");
				int u = r.getInt("module");
				TreeMap<String,Object> item = new TreeMap<String,Object>();
				item.put("e",e);
				item.put("c",c);
				item.put("i",i);
				item.put("b",b);
				item.put("m",m);
				item.put("u",u);
				items.add(item);
			}
			re = Json.toJson(items);
		}catch(SQLException e){
			e.printStackTrace();
		}
		return re;
	}
	
	private String get_words_by_sql(String uid, HttpServletRequest request){
		String re = "[]";
		ArrayList<Object> items = new ArrayList<Object>();
		String sql = request.getParameter("sql");
		Log.debug("sql="+sql);
		if (sql==null){
			return re;
		}
		try{
			ResultSet r = Jdbc.query(sql);
			ResultSetMetaData meta = r.getMetaData();
			int columnCount = meta.getColumnCount();
			while(r.next()){
				TreeMap<String,Object> item = new TreeMap<String,Object>();
				for(int i=1; i<=columnCount; ++i){
					String attr = r.getString(i);
					String columnName = meta.getColumnLabel(i);
					item.put(columnName, attr);
				}
				items.add(item);
			}
			re = Json.toJson(items);
		}catch(SQLException e){
			e.printStackTrace();
		}
		return re;
	}
	
	private String get_memorized_words(String uid){
		String re = "[]";
		ArrayList<Object> items = new ArrayList<Object>();
		String sql = "select English,Chinese,book,module,memo,c.wid from cards c, user_word_state u"
				+ " where uid='" + uid + "' and c.wid=u.wid and state='watching'";
		try{
			Log.debug(sql);
			ResultSet r = Jdbc.query(sql);
			while(r.next()){
				String e = r.getString("English");
				String c = r.getString("Chinese");
				String b = r.getString("book");
				Integer m = r.getInt("module");
				String memo = r.getString("memo");
				int wid = r.getInt("wid");
				TreeMap<String,Object> item = new TreeMap<String,Object>();
				item.put("e",e);item.put("memo", memo);item.put("wid", wid);
				item.put("c",c);item.put("b", b);item.put("m",m);
				items.add(item);
			}
			re = Json.toJson(items);
		}catch(SQLException e){
			e.printStackTrace();
		}
		return re;
	}
	
	private String get_word_example_sentences(String uid, String word) {
		ArrayList<Object> sents = new ArrayList<Object>();
		if (word.isEmpty()) {
			return "[]";
		}
		String sql = "select distinct e,'' as c from sentences where (e like '% "+word+" %'"
				+ " or e like '" +word.substring(0,1).toUpperCase()+word.substring(1) + " %'"
				+ " or e like '% " +word + "_' or e like '% " +word + "_ %')"
				+ " and b in (select bid from user_book where uid=?) limit 3";
		try {
			Jdbc.prepare_sql(sql);
			Jdbc.pstmt.setString(1, uid);
			ResultSet r = Jdbc.pstmt.executeQuery();
			while(r.next()) {
				String e = r.getString("e");
				String c = r.getString("c");
				TreeMap<String,String> sent = new TreeMap<String,String>();
				sent.put("e", e);
				sent.put("c", c);
				sents.add(sent);
			}
		}catch(SQLException e) {
			Log.debug(e);
		}
		return Json.toJson(sents);			
	}
	
	private String get_word_example_sentences(String uid, HttpServletRequest request) {
		String word = request.getParameter("word");
		return get_word_example_sentences(uid,word);
	}
	
	private String increase_words_frequencies(String uid, HttpServletRequest request) {
		boolean ret = false;
		String words = request.getParameter("words");
		String[] ws = words.split("_") ;
		String sql = "update user_new_word set freq = freq+1 where uid='"+uid+"' and(";
		for (String w : ws) {
			sql += " word='"+w+"' or";
		}
		sql += " 1=0)";
		try {
			Jdbc.execute_sql(sql);
			ret = true;
		}catch(SQLException e) {
			Log.debug(e.getMessage());
		}
		return Json.boolean2Json(ret);
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
			if (cmd==null){
				resp = get_pupil_words(request);
			}else if(cmd.equals("all")){
				resp = get_all_words(uid);
			}else if(cmd.equals("wrong")){
				resp = get_wrong_words(uid);
			}else if(cmd.equals("memorized")){
				resp = get_memorized_words(uid);
			}else if(cmd.equals("sql")){
				resp = get_words_by_sql(uid, request);
			}else if(cmd.contentEquals("example")) {
				resp = get_word_example_sentences(uid, request);
			}else if(cmd.contentEquals("occur")) {
				resp = increase_words_frequencies(uid, request);
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
		pupil_words pw = new pupil_words();
		Log.debug(pw.get_word_example_sentences("a", "bored"));
	}
}
