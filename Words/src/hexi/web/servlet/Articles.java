package hexi.web.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import common.Log;
import common.UFileReader;
import hexi.dbc.Jdbc;

public class Articles extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		response.setContentType("application/json;charset=UTF-8");
		response.setHeader("Cache-Control", "no-cache");

		String aid = (String) request.getParameter("aid");
		String uid = (String) request.getParameter("uid");
		String cmd = (String) request.getParameter("cmd");
		String resp = "";
		if (aid != null && uid != null && cmd != null) {
			if (cmd.equals("get")) {
				resp = get_article_html(aid, uid);
			} else if (cmd.equals("done")) {
				resp = save_user_article_state(aid, uid).toString();
			}
		}
		response.getWriter().write(resp);
	}

	public static String get_passage_content(String aid)
	{
		String s = "";
		ResultSet r = null;
		String sql = "select * from passages where p_id='"+aid+"'";
		try{
			r = Jdbc.query(sql);
			if (r.next()){
				s = r.getString("cont");
			}
		}catch(SQLException e){
			String msg = e.getMessage();
			Log.d("SQL ERROR: "+msg+"\n"+sql);
		}	
		return s;
	}
	
	private Boolean save_user_article_state (String aid, String uid){
		Boolean ret = false;
		String sql = "update user_article set memo='done' where uid='"+uid+"' and aid='"+aid+"'";
		try{
			Jdbc.execute_sql(sql);
			ret = true;
		}catch(SQLException e){
			e.printStackTrace();
		}
		return ret;
	}
	
	public String get_article_html(String aid, String uid)
	{
		String s = "";
		ResultSet r = null;
		String sql = "select title, cont from passages where p_id="+aid;
		try{
			r = Jdbc.query(sql);
			if (r.next()){
				String content = r.getString("cont");
				String title = r.getString("title");
				s += "<p class=\"title\">"+title+"</p>\r\n";
				String[] lines = content.split("(\r\n)+");
				for(String l:lines){
					s += "<p>"+l+"</p>\r\n";
				}
				s += "<input type=\"button\" id=\""+ uid + "\"  title=\"" + aid + "\" class=\"btn\" onclick=\"on_article_done(this)\" value=\"Done\"/>";
			}

		}catch(SQLException e){
			String msg = e.getMessage();
			System.out.println("SQL ERROR: "+msg+"\n"+sql);
		}	
		return s;
	}
	
	public void doPost(HttpServletRequest request,HttpServletResponse response)
	  throws IOException,ServletException
	 {
		 request.setCharacterEncoding("UTF-8");
		String title = request.getParameter("title");
		String content = request.getParameter("content");
		Log.d("title:"+title);
		Log.d(content);
		String sql = "insert passages(title, path, memo, cont) values(?,'','',?)";
		Jdbc.prepare_sql(sql);
		try {
			Jdbc.setString(1, title);
			Jdbc.setString(2, content);
			Jdbc.execute();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	 }

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
