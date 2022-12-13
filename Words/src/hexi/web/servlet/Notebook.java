package hexi.web.servlet;

import hexi.dbc.Jdbc;

import java.io.IOException;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.TreeMap;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.*;

import common.Json;
import common.Log;

public class Notebook extends HttpServlet {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String get_words(String uid) {
		String res = "[]";
		ArrayList<Object> ws = new ArrayList<Object>();
		String sql = "select word, explains, source_id, append_time, last_time from user_new_word where uid=?"
				+ " order by last_time";
		try{
			Jdbc.prepare_sql(sql);
			Jdbc.pstmt.setString(1, uid);
			ResultSet r = Jdbc.pstmt.executeQuery();
			while(r.next()){
				TreeMap<String,Object> item = new TreeMap<String,Object>();
				String w = r.getString("word");
				item.put("word", w);
				String explains = r.getString("explains");
				item.put("explains", explains);
				String source_id = r.getString("source_id");
				item.put("source_id", source_id);
				Date t = r.getDate("append_time");
				item.put("append_time", t);
				t = r.getDate("last_time");
				item.put("last_time", t);
				ws.add(item);
			}
			res = Json.toJson(ws);
		}catch(SQLException e){
			Log.debug(e);
		}		
		return res;
	}
	
	public String delete_word(String uid, String word) {
		String ret = "0";
		Jdbc.prepare_sql("delete from user_new_word where uid=? and word=?");
		try {
			Jdbc.pstmt.setString(1, uid);
			Jdbc.pstmt.setString(2, word);
			ret = ""+Jdbc.pstmt.executeUpdate();
		}catch(SQLException e) {
			Log.d(e);
		}
		return ret;
	}
	
	public void doGet(HttpServletRequest request, HttpServletResponse response)
	  throws IOException, ServletException
	{
		response.setContentType("application/json;charset=UTF-8");
		response.setHeader("Cache-Control", "no-cache");
		
		String uid  = request.getParameter("uid");
		String cmd  = request.getParameter("cmd");
		
		String resp = "false";
		
		if (uid != null && cmd !=null)
		{
			if (cmd.equals("w"))
			{
				resp = get_words(uid);
			}else if(cmd.contentEquals("d")) {
				String word = request.getParameter("word");
				resp = delete_word(uid, word);
			}
		}
		response.getWriter().write(resp);
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		doGet(request, response);
	}

	public void init(ServletConfig config) throws ServletException {
	}

	public static void main(String[] args) throws Exception{
		Jdbc.prepare_sql("delete from user_new_word where uid='a' and word='a'");
		int i = Jdbc.pstmt.executeUpdate();
		Log.d(i);
	}
}
