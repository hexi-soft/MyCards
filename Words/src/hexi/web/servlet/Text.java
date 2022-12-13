package hexi.web.servlet;

import hexi.dbc.Jdbc;
import hexi.web.Crawler;

import java.io.File;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import common.Log;

public class Text extends HttpServlet{
	private static final long serialVersionUID = 1;
	static Logger log = Log.log;
	
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException
	{
		response.setContentType("text/xml;charset=UTF-8");
		response.setHeader("Cache-Control", "no-cache");
		request.setCharacterEncoding("utf-8");
		String uid = (String) request.getParameter("uid");
		String resp = "false";
		if (uid != null)
		{
			String url = (String)request.getParameter("url");
			if (url != null){
				resp = Crawler.get_content_by_url(url);
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
	
	public static String[] tag(String s){
		log.debug(s);
		String[] r = null;
		Pattern p = Pattern.compile("^(<\\w+>).*$");
		Matcher m = p.matcher(s);
		if (m.find()){
			r = new String[2];
			r[0] = "markup";
			r[1] = m.group(1) ;
			//log.debug("match: "+r[1]);
		}
		return r;
	}
	
	public static void main(String[] args)throws IOException
	{
		String sql = "insert into lines(l, m) values(?, ?)";
		File file = new File("d:\\corpus\\html5.txt");
		Scanner scanner = new Scanner(file);
		Jdbc.prepare_sql(sql);
		String l="";
		PreparedStatement pstmt = Jdbc.pstmt;
		while(scanner.hasNextLine()){
			l = scanner.nextLine().trim();
			if (l.isEmpty()){
				continue;
			}
			String[] tag = tag(l);
			if (tag==null){
				continue;
			}			
			//log.debug(l);
			//System.out.println(tag[0]+" "+tag[1]);
			try{
				pstmt.setString(1, l);
				pstmt.setString(2, tag[1]);
				//pstmt.execute();
			}catch(SQLException e){
				log.debug(e);
			}
		}
	}
}
