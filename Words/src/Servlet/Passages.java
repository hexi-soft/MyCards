package Servlet;

import hexi.dbc.Jdbc;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import common.Log;
import common.UFileReader;
import common.UFileWriter;

public class Passages extends HttpServlet{
	static private String corpus_dir="E:\\corpus\\";
	
	public void doGet(HttpServletRequest request, HttpServletResponse response)
	  throws IOException, ServletException
	{
		response.setContentType("application/json;charset=UTF-8");
		response.setHeader("Cache-Control", "no-cache");
				
		String p_id = (String) request.getParameter("p_id");
		String uid = (String) request.getParameter("uid");
		String cmd = (String) request.getParameter("cmd");
		String resp = "";
		if (p_id!=null && uid!=null && cmd!=null){
			if (cmd.equals("get")){
				resp = get_passage_html(p_id, uid);
			}else if(cmd.equals("done")){
				resp = save_user_passage_state(p_id, uid).toString();
			}else if(cmd.equals("upload")) {
				
			}
		}
		response.getWriter().write(resp);
	}

	public static String get_passage_content(String bid)
	{
		String s = "";
		String path = corpus_dir+bid+".txt";
		s = UFileReader.read(path);
		return s;
	}
	
	private Boolean save_user_passage_state (String p_id, String uid){
		Boolean ret = false;
		String sql = "update user_passage set memo='done' where uid='"+uid+"' and p_id='"+p_id+"'";
		try{
			Jdbc.execute_sql(sql);
			ret = true;
		}catch(SQLException e){
			e.printStackTrace();
		}
		return ret;
	}
	
	public String get_passage_html(String p_id, String uid)
	{
		String s = "";
		ResultSet r = null;
		String sql = "select title, path from passages where p_id="+p_id;
		try{
			r = Jdbc.query(sql);
			if (r.next()){
				String path = r.getString("path");
				String title = r.getString("title");
				s += "<p class=\"title\">"+title+"</p>\r\n";
				String passage = UFileReader.read(path);
				String[] lines = passage.split("(\r\n)+");
				for(String l:lines){
					s += "<p>"+l+"</p>\r\n";
				}
				s += "<input type=\"button\" id=\""+ uid + "\"  title=\"" + p_id + "\" class=\"btn\" onclick=\"on_passage_done(this)\" value=\"Done\"/>";
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
			UFileWriter.write(corpus_dir+title+".txt", content);
	 }
			
	public void init(ServletConfig config) throws ServletException {
		corpus_dir = "E:\\corpus\\";
		System.out.println("Passages servlet init...");
		File file = new File(corpus_dir+"txt\\passages");
		File[] files = file.listFiles();
		for (File f:files){
			String sql = "select * from passages where path=?";
			try {
				Jdbc.prepare_sql(sql);
				Jdbc.pstmt.setString(1, f.getPath());
				ResultSet r = Jdbc.pstmt.executeQuery();
				if (r.next()){
					continue;
				}else{
					sql = "insert into passages (title, path) values(?,?)";
					Jdbc.prepare_sql(sql);
					String title = f.getName().replaceAll("\\.txt$", "");
					Jdbc.pstmt.setString(1,title);
					Jdbc.pstmt.setString(2, f.getAbsolutePath());
					Jdbc.pstmt.executeUpdate();
				}
			}catch(SQLException e){
				System.out.println(e.getMessage());
			}
		}
		System.out.println("PassagesServlet has been initialized successfully.");
	}

	public static void main(String[] args)throws Exception{
		Passages p = new Passages();
		p.init(null);

	}
}
