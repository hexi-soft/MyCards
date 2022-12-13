package hexi.web.servlet;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import common.Json;
import common.Log;
import hexi.dbc.Neo4jDB;

public class UserWords extends HttpServlet {

	static Neo4jDB graph_db = Neo4jDB.getInstance();
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		response.setContentType("application/json;charset=UTF-8");
		response.setHeader("Cache-Control", "no-cache");

		//String wid = request.getParameter("wid");
		String word = request.getParameter("word");
		String uid = request.getParameter("uid");
		String state = request.getParameter("state");
		String s_value = request.getParameter("value");
		int value = -1;
		if (s_value != null) {
			value = Integer.parseInt(s_value);
		}
		String resp = "false";

		if (uid != null && word != null && state != null) {
			resp = Json.toJson(save_user_word_state(uid, word, state, value));
		}
		response.getWriter().write(resp);
	}

	static boolean save_user_word_state(String uid, String word, String state, int value) {
		boolean ret = false;
		Map<String,Object> params = new HashMap<>();
		params.put("uid", uid);
		params.put("word", word);
		params.put("value", value);
		String cypher = "MATCH(u:users{uid:$uid}),(w:word{lemma:$word})";
		cypher += " MERGE (u)-[r:KNOWS]->(w) SET r."+state+"=$value";
		ret = graph_db.execute_cypher(cypher, params);
		return ret;
	}
	
	public static void main(String[] args) {
		
		boolean r = save_user_word_state("a","a", "understand_literally", 100);
		Log.d(r);

	}

}
