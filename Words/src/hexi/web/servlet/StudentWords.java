package hexi.web.servlet;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import common.Json;
import common.Log;
import hexi.dbc.Jdbc;
import hexi.dbc.Jdbc2;
import hexi.web.WebDao;
import hexi.web.base.UserNewWord;

public class StudentWords extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private WebDao mDB = new WebDao(Jdbc2.SQL_SERVER_2005,"txtkbase", "192.168.1.112","sa","hujia521");
	private Gson mGson = new Gson();

	private List<UserNewWord> getUserNewWords(String uid, String condition_expression, int maximum) {
		return (mDB.getUserNewWords(uid, condition_expression, maximum));
	}
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		response.setContentType("application/json;charset=UTF-8");
		response.setHeader("Cache-Control", "no-cache");
		request.setCharacterEncoding("utf-8");
		Object resp = null;
		String uid = request.getParameter("uid");
		String cmd = request.getParameter("cmd");
		Log.d(uid + " " + cmd);
		if (uid != null && cmd != null) {
			if (cmd.equals("add")) {
				String word = request.getParameter("word");
				Log.d("word: " + word);
				if (word != null) {
					resp = add_student_word(uid, word);
					if (!is_contain(word)) {
						String explains = request.getParameter("explains");
						String phonetic = request.getParameter("phonetic");
						String web = request.getParameter("web");
						if (explains != null) {
							resp = add_word_explains(word, explains, phonetic, web);
						}
					}
				}
			}else if(cmd.equals("get")) {
				Log.debug("to get user new words...");
				String where = request.getParameter("where");
				String maximum = request.getParameter("maximum");
				resp = getUserNewWords(uid, where, Integer.parseInt(maximum));
			}
		}
		response.getWriter().write(mGson.toJson(resp));
	}

	boolean add_word_explains(String word, String explains, String phonetic, String web) {
		boolean ret = false;
		try {
			String sql = "insert words(word, chinese, phonetic) values(?,?,?)";
			Jdbc.prepare_sql(sql);
			Jdbc.setString(1, word);
			Jdbc.setString(2, explains.replace("``", ";"));
			Jdbc.setString(3, phonetic);
			Jdbc.execute();
			String[] es = explains.split("``");
			for (int i = 0; i < es.length; ++i) {
				sql = "insert word_propertis(word,property_name,property_value) values(?,?,?)";
				Jdbc.prepare_sql(sql);
				Jdbc.setString(1, word);
				Jdbc.setString(2, "explain");
				Jdbc.setString(3, es[i]);
				Jdbc.execute();
				ret = true;
			}
			String[] webs = web.split("``");
			for (int i = 0; i < webs.length; ++i) {
				sql = "insert word_propertis(word,property_name,property_value) values(?,?,?)";
				Jdbc.prepare_sql(sql);
				Jdbc.setString(1, word);
				Jdbc.setString(2, "web_dict");
				Jdbc.setString(3, webs[i]);
				Jdbc.execute();
				ret = true;
			}
		} catch (SQLException e) {
			Log.d(e);
		}
		return ret;
	}

	boolean add_student_word(String uid, String word) {
		boolean ret = false;
		String sql = "insert student_new_word(uid, word) values(?,?)";
		try {
			Jdbc.prepare_sql(sql);
			Jdbc.setString(1, uid);
			Jdbc.setString(2, word);
			Jdbc.execute();
			ret = true;
		} catch (SQLException e) {
			Log.debug(e.getMessage());
		}
		return ret;

	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		doGet(request, response);
	}

	public void init(ServletConfig config) throws ServletException {
		mDB = new WebDao(Jdbc2.SQL_SERVER_2005,"txtkbase", "192.168.1.112","sa","hujia521");
		mGson = new Gson();
	}

	public static void main(String[] args) {
		Log.d(is_contain("luminouss"));
		String s = "a``b``c";
		Log.d(s.replace("``", ";"));
		Log.d(s);

	}

	static boolean is_contain(String word) {
		boolean ret = false;
		String sql = "select word from words where word=?";
		try {
			Jdbc.prepare_sql(sql);
			Jdbc.setString(1, word);
			ResultSet r = Jdbc.query();
			if (r.next()) {
				ret = true;
			}
		} catch (SQLException e) {
			Log.d(e);
		}
		return ret;
	}

}
