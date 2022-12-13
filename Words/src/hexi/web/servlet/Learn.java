package hexi.web.servlet;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import common.Json;
import common.Log;
import hexi.dbc.Jdbc;

public class Learn extends HttpServlet{
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		response.setContentType("application/json;charset=UTF-8");
		response.setHeader("Cache-Control", "no-cache");
		request.setCharacterEncoding("utf-8");
		
		String cmd = request.getParameter("cmd");
		Log.d("cmd: "+cmd);
		String q = request.getParameter("query");
		Log.d("q:"+q);
		String resp = null;
		
		/*
		if (cmd != null) {
			if (cmd.equals("query")) {
				try {
					resp = query_next_word();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}else{
			String word = request.getParameter("word");
			if (word!=null) {
				try {
					String para = request.getParameter("tran");
					if (para != null)
						fill_word(word, "translation", para);
					para = request.getParameter("wfs");
					if (para != null)
						fill_word(word, "wfs", para);
					para = request.getParameter("exam");
					if (para != null)
						fill_word(word, "exam_type", para);
					para = request.getParameter("web");
					if (para != null)
						fill_word(word, "web_dict", para);
					String sql = "update words set memo='ok' where word=?";
					Jdbc.prepare_sql(sql);
					Jdbc.setString(1, word);
					Jdbc.execute();
					resp = "true";
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}*/
		response.getWriter().write(Json.toJson(resp));
	}

	static String query_next_word() throws SQLException {
		String word = null;
		String sql = "select top 1 word from words where memo = '' and phonetic!=''";
		ResultSet r = Jdbc.query(sql);
		if (r.next()) {
			word = r.getString(1);
		}
		return word;
	}
	
	static boolean fill_word(String word, String property_name, String property_value) throws SQLException {
		boolean ret = false;
		String sql = "insert word_propertis(word,property_name, property_value)values(?,?,?)";
		Jdbc.prepare_sql(sql);
		String[] values = property_value.split("``");
		Jdbc.setString(1, word);
		Jdbc.setString(2, property_name);
		for (int i=0; i<values.length; ++i) {
			Jdbc.setString(3, values[i]);
			Jdbc.execute();
		}
		ret = true;
		return ret;
		
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		doGet(request, response);
	}
	
	public void init(ServletConfig config) throws ServletException {
	}

	public static void main(String[] args) throws SQLException {
			String word = query_next_word();
			word += "``";
			String[] vs = word.split("``");
			Log.d(vs.length);
			for (int i=0; i<vs.length; ++i) {
				Log.d(vs[i]);
			}
			//fill_word("a", "ei");
			Log.d("ok");
		}
		
	}
