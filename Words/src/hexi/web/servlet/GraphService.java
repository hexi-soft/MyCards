package hexi.web.servlet;

import java.io.IOException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import common.Json;
import common.Log;
import hexi.dbc.Neo4jDB;

public class GraphService extends HttpServlet{
	private static final long serialVersionUID = 1;
	static Logger log = Log.log;
	static Neo4jDB db;
	
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
		db = Neo4jDB.getInstance();
	}
	
	public static String get_by_cypher(String uid, HttpServletRequest request) {
		String res = "[]";
		String cypher = request.getParameter("cypher");
		if (cypher != null) {
			res = Json.toJson(db.run_cypher_get_page(cypher,null));
		}
		return res;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}

