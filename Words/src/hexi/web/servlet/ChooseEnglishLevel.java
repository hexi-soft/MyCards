package hexi.web.servlet;

import java.io.IOException;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.neo4j.graphdb.Node;

import common.Json;
import common.Log;
import hexi.dbc.Neo4jDB;

public class ChooseEnglishLevel extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	static Neo4jDB graphDB = Neo4jDB.getInstance();
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		response.setContentType("application/json;charset=UTF-8");
		response.setHeader("Cache-Control", "no-cache");
		request.setCharacterEncoding("utf-8");
		
		String uid = request.getParameter("uid");
		if (uid==null || uid.isEmpty()) {
			request.setAttribute("error.message", "User id can't be empty!");
			RequestDispatcher requestDispatcher = request.getRequestDispatcher("error.jsp");
			requestDispatcher.forward(request,  response);
			return;
		}
		String level = request.getParameter("level");
		if (level==null || level.isEmpty()) {
			request.setAttribute("error.message", "level can't be empty!");
			RequestDispatcher requestDispatcher = request.getRequestDispatcher("error.jsp");
			requestDispatcher.forward(request,  response);
			return;
		}		
		boolean resp = set_user_level(uid, level);
		if (resp) {
			request.setAttribute("ok.message", "Choose level \""+level+"\" OK.");
			RequestDispatcher requestDispatcher = request.getRequestDispatcher("ok.jsp");
			requestDispatcher.forward(request,  response);
		}else {
			request.setAttribute("error.message", "Choose level error!");
			RequestDispatcher requestDispatcher = request.getRequestDispatcher("error.jsp");
			requestDispatcher.forward(request,  response);
		}
	}

	public static boolean set_user_level(String uid, String level) {
		boolean ret = false;
		//Node node = graphDB.find_node("users", "uid", uid);
		String cypher = "MATCH (n:users {uid: '"+uid+"'}) SET n.level='"+level+"'";
		ret = graphDB.execute_cypher(cypher);
		return ret;
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		doGet(request, response);
	}

	public void init(ServletConfig config) throws ServletException {
	}

	public static void main(String[] args) {
		boolean r = ChooseEnglishLevel.set_user_level("a","9-1");
		Log.d(r);
	}

}
