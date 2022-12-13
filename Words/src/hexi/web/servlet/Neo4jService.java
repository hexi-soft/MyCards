package hexi.web.servlet;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import common.Log;
import hexi.dbc.Neo4jHttp;

public class Neo4jService extends HttpServlet{
	private static final long serialVersionUID = 1;
	Neo4jHttp neo;
	
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException
	{
		response.setContentType("text/xml;charset=UTF-8");
		response.setHeader("Cache-Control", "no-cache");
		request.setCharacterEncoding("utf-8");
		String uid = request.getParameter("uid");
		String resp = "false";
		if (uid != null)
		{
			String cmd = request.getParameter("cmd");
			if (cmd != null) {
				if (cmd.contentEquals("get_by_cypher")) {
					resp = get_by_cypher(uid, request);
				}
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
		neo = Neo4jHttp.getInstance("192.168.1.112","neo4j","hexi521");
	}
	
	public String get_by_cypher(String uid, HttpServletRequest request) {
		String res = "[]";
		String cypher = request.getParameter("cypher");
		Log.d(cypher);
		if (cypher != null) {
			res = neo.exeCypher(cypher);
		}
		return res;
	}

}

