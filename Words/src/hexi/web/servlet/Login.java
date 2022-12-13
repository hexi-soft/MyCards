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

public class Login extends HttpServlet {

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
		String pwd = request.getParameter("pwd");
		if (uid==null || uid.isEmpty()) {
			request.setAttribute("error.message", "User id can't be empty!");
			RequestDispatcher requestDispatcher = request.getRequestDispatcher("error.jsp");
			requestDispatcher.forward(request,  response);
			return;
		}
		if (pwd==null || pwd.isEmpty()) {
			request.setAttribute("error.message", "Password can't be empty!");
			RequestDispatcher requestDispatcher = request.getRequestDispatcher("error.jsp");
			requestDispatcher.forward(request,  response);
			return;
		}
		
		String resp = check_out(uid, pwd);
		if (!resp.isEmpty()) {
			request.setAttribute("user.info", resp);
			RequestDispatcher requestDispatcher = request.getRequestDispatcher("success.jsp");
			requestDispatcher.forward(request,  response);
		}else {
			request.setAttribute("error.message", "Eithr your id or your password is wrong!");
			RequestDispatcher requestDispatcher = request.getRequestDispatcher("error.jsp");
			requestDispatcher.forward(request,  response);
		}
	}

	public String check_out(String uid, String pwd) {
		String ret = "";
		Node node = graphDB.find_node("users", "uid", uid, "pwd", pwd);
		if (node != null) {
			Map<String,Object> props = graphDB.get_node_properties(node);
			ret = Json.toJson(props);
		}
		return ret;
	}

	public static String test_check_out(String uid, String pwd) {
		String ret = "";
		Node node = graphDB.find_node("users", "uid", uid, "pwd", pwd);
		if (node != null) {
			Map<String,Object> props = graphDB.get_node_properties(node);
			ret = Json.toJson(props);
		}
		return ret;
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		doGet(request, response);
	}

	public void init(ServletConfig config) throws ServletException {
	}

	public static void main(String[] args) {
		String user = Login.test_check_out("a","142857");
		Log.d(user);
	}

}
