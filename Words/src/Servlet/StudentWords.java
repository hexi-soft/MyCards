package Servlet;

import hexi.dbc.Jdbc;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TreeMap;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.*;

import common.*;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class StudentWords extends HttpServlet{
	private static final long serialVersionUID = 1;
	static final int IBinhouse_curve[] = {0,1,3,5,20,30,40,60};
	
	static{
		Date date = new Date();
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		date.setDate(date.getDate()-1);
		String time = format.format(date);
		String sql = "update user_new_word set re_degree=1"
				+ " where re_degree=1 and last_time<'" + time + "'";
		for(int i=2; i < 4;/*IBinhouse_curve.length*;*/ ++i){
			date.setDate(date.getDate()-IBinhouse_curve[i]);
			time = format.format(date);
			String sI = " or re_degree= " + i + " and last_time < '" + time +"'";
			sql += sI;
		}
		Log.debug("maintain user new words table. sql:"+sql);
		try{
			Jdbc.execute_sql(sql);
		}catch(SQLException e){
			Log.debug(e.getMessage());
		}
	}
	
	public static String get_cookie_value(String key, HttpServletRequest request) {
		String value = null;
		Cookie[] cookies = request.getCookies();
		if (cookies != null){
			for (int i = 0; i < cookies.length; ++i) {
				if (cookies[i].getName().equals(key))
					value = cookies[i].getValue();
			}
		}
		return value;
	}
	
	public void doGet(HttpServletRequest request, HttpServletResponse response)
	  throws IOException, ServletException
	{
		response.setContentType("application/json;charset=UTF-8");
		response.setHeader("Cache-Control", "no-cache");
		request.setCharacterEncoding("utf-8");
		String uid = (String) request.getParameter("uid");
		String resp = "false";
	
		if (uid != null)
		{
			String cmd = request.getParameter("cmd");
			if (cmd.equals("sql")){
				resp = get_words_by_sql(uid, request);
			}else if (cmd.equals("save_user_new_word")){
				String word = request.getParameter("word"); 
				String explains = request.getParameter("explains");
				/*
				int len = explains.length();
				int cp0 = explains.codePointAt(0);
				int cp1 = explains.codePointAt(1);
				int cp2 = explains.codePointAt(2);
				Log.d(len+" "+cp0+" "+cp1+" "+cp2);*/
				resp = save_user_new_word(uid, word, explains).toString();
			}else if(cmd.equals("is_user_new_word")){
				String word = request.getParameter("word");
				resp = is_user_new_word(uid, word);
				//Log.debug("is new word? "+resp);
			}else if(cmd.equals("change_user_word_status")){
				resp = change_user_word_status(uid, request).toString();
			}else if (cmd.equals("change_redegree")){
				String wid = request.getParameter("wid");
				if (wid != null){
					resp = update_user_word_re_degree(uid, wid, false).toString();
				}else{
					Log.debug("change user word remember_degree -> the requested parameters wid is null!");
				}
			}
			else{
				resp = find_out(uid, cmd);
				//Log.debug(resp);
			}
		//Log.debug(cmd + " " + resp);
		}else{
			Log.debug("The requested parameter uid is null!");
		}
		response.getWriter().write(resp);
	}

	public static String is_user_new_word(String uid, String word){
		TreeMap<String,Object> word_info = new TreeMap<String,Object>();
		
		if (word != null){
			word_info.put("word", word);
			String sql = "select word from user_new_word where uid=? and word=?";
			try{
				Jdbc.prepare_sql(sql);
				Jdbc.pstmt.setString(1, uid);
				Jdbc.pstmt.setString(2, word);
				ResultSet r = Jdbc.pstmt.executeQuery();
				if (r.next()){
					word_info.put("is_new", false);
				}else{
					word_info.put("is_new", true);
				}
			}catch(SQLException e){
				Log.debug(e.getMessage());
			}
		}else{
			Log.debug("is user new word -> the requested parameter word is null!");
		}
		return Json.toJson(word_info);
	}
	
	public static Boolean save_user_new_word(String uid, String word, String explains){	
		boolean ret = false;
		if (word != null){
			try{
				String sql = "insert into user_new_word(uid, wid, word, explains)"
						+ " values(?, 0, ?, ?)";
				Jdbc.prepare_sql(sql);
				Jdbc.pstmt.setString(1, uid);
				Jdbc.pstmt.setString(2, word);
				Jdbc.pstmt.setString(3, explains);
				Jdbc.pstmt.executeUpdate();
				ret = true;
			}catch(SQLException e){
				int code = e.getErrorCode();
				//Log.d(code);
				if (code == 2627/*for sql server*/
						|| code== 1062/*for mysql*/) {
					String sql = "update user_new_word set last_time=";
					if (code==2627)
						sql += "getdate() where uid=? and word=?";
					else if (code==1062)
						sql += "now() where uid=? and word=?";
					Jdbc.prepare_sql(sql);
					try {
						Jdbc.pstmt.setString(1, uid);
						Jdbc.pstmt.setString(2, word);
						Jdbc.pstmt.executeUpdate();
					}catch(SQLException se) {
						Log.debug(se);
					}
				}else {
					Log.debug(e);
				}
			}
		}else{
			Log.debug("the requested parameter word is null!");
		}
		return ret;
	}
	
	public String word_test(String uid){
		ArrayList<Object> res = new ArrayList<Object>();
		String sql = "select wid, c.word, chinese, freq"
				+ " from cards2 c join word_freq_book w"
				+ " on c.word=w.word"
				+ " where wid not in (select wid from user_word where uid='"+uid+"')"
				+ " order by freq desc";
		try{
			Log.debug(sql);
		ResultSet r = Jdbc.query(sql);
		while(r.next()){
			TreeMap<String,Object> word = new TreeMap<String,Object>();
			int i = r.getInt("wid");
			String e = r.getString("word");
			String c = r.getString("Chinese");
			int freq = r.getInt("freq");
			word.put("id", i+"");
			word.put("English", e);
			word.put("Chinese", c);
			word.put("freq", freq);
			res.add(word);
		}}catch(SQLException e){
			Log.debug(e.getMessage());
		}
		return Json.toJson(res);
	}
	
	public String find_out(String uid, String cmd)
	{
		ArrayList<Object> res = new ArrayList<Object>();
		String sql = "select u.wid, u.word, ' ' as Chinese,re_degree, strange, last_time, u.memo from user_new_word u";
		if (cmd.equals("books")){
			sql = "select wid, English, Chinese, book from cards,user_book where uid='"+uid+"' and bid like '%'+book+'%'";
			try{
				Log.debug(sql);
			ResultSet r = Jdbc.query(sql);
			while(r.next()){
				TreeMap<String,Object> word = new TreeMap<String,Object>();
				int i = r.getInt("wid");
				String e = r.getString("English");
				String c = r.getString("Chinese");
				word.put("id", i+"");
				word.put("English", e);
				word.put("Chinese", c);
				res.add(word);
			}}catch(SQLException e){
				Log.debug(e.getMessage());
			}
			return Json.toJson(res);
		}
		else if (cmd.equals("all")){
			sql += " where u.uid = '" + uid + "'";
		}else if (cmd.equals("review")){
			sql += " where u.uid='" + uid + "' and (" + get_review_condition()
					+ " or u.memo='X') order by strange";
			Log.debug("review sql:"+sql);
		}else if (cmd.equals("today")){
			Date date = new Date();
			DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			String today = format.format(date);
			sql += " where u.uid='" + uid + "'and append_time>'" + today + "'";
		}else{
			return word_test(uid); 
		}
		try{
			Log.debug(sql);
		ResultSet r = Jdbc.query(sql);
		while(r.next()){
			TreeMap<String,Object> word = new TreeMap<String,Object>();
			int i = r.getInt("wid");
			String e = r.getString("word");
			String c = r.getString("Chinese");
			int re = r.getInt("re_degree");
			int strange = r.getInt("strange");
			Date date = r.getDate("last_time");
			String memo = r.getString("memo");
			word.put("id", i+"");
			word.put("English", e);
			word.put("Chinese", c);
			word.put("re_degree", re);
			word.put("strange", strange);
			word.put("last_time", date.toString());
			word.put("memo", memo);
			res.add(word);
		}}catch(SQLException e){
			Log.debug(e.getMessage());
		}
		return Json.toJson(res);
	}
	
	public String get_words_by_sql(String uid, HttpServletRequest request)
	{
		ArrayList<Object> res = new ArrayList<Object>();
		//String sql = "select u.wid, u.word, ' ' as Chinese,re_degree, strange, last_time, u.memo from user_new_word u";
		String sql = request.getParameter("sql");
		try{
			Log.debug(sql);
			ResultSet r = Jdbc.query(sql);
			while(r.next()){
				TreeMap<String,Object> word = new TreeMap<String,Object>();
				int i = r.getInt("wid");
				String e = r.getString("word");
				String c = r.getString("Chinese");
				int re = r.getInt("re_degree");
				int strange = r.getInt("strange");
				Date date = r.getDate("last_time");
				String memo = r.getString("memo");
				word.put("id", i+"");
				word.put("English", e);
				word.put("Chinese", c);
				word.put("re_degree", re);
				word.put("strange", strange);
				word.put("last_time", date.toString());
				word.put("memo", memo);
				res.add(word);
			}
		}catch(SQLException e){
			Log.debug(e.getMessage());
		}
		return Json.toJson(res);
	}
	
	public void doPost(HttpServletRequest request,HttpServletResponse response)
	  throws IOException,ServletException
	 {
		doGet(request, response);
	 }
			
	public void init(ServletConfig config) throws ServletException {
	 }		

	public Boolean change_user_word_status(String uid, HttpServletRequest request){
		Boolean ret = false;
		Log.debug("change user word status.");
		String wid = request.getParameter("wid");
		Log.debug("wid="+wid);
		String status = request.getParameter("status");
		Log.debug("status="+status);
		if (wid != null && status != null){
			
		}else{
			Log.debug("change user word status -> the requested parameters either wid or status is null!");
		}

		String sql = "update user_new_word"
				+ " set memo = '" + status + "' where uid='" + uid + "' and wid=" + wid;
		Log.debug(sql);
		try{
			Jdbc.execute_sql(sql);
			ret = true;
		}catch(SQLException e){
			Log.debug(e.getMessage());
		}
		return ret;
	}
	
	public Boolean delete_user_word(String uid, String wid){
		Boolean ret = false;
		String sql = "delete user_new_word"
				+ " where uid='" + uid + "' and wid=" + wid;
		try{
			Jdbc.execute_sql(sql);
			ret = true;
		}catch(SQLException e){
			Log.debug(e.getMessage());
		}
		return ret;
	}
	
	public Boolean is_in_user_new_word(String uid, String wid){
		Boolean ret = false;
		String sql = "select wid from user_new_word"
				+ " where uid='" + uid + "' and wid=" + wid;
		try{
			ResultSet r = Jdbc.query(sql);
			if (r.next()){
				ret = true;
			}
		}catch(SQLException e){
			Log.debug(e.getMessage());
		}
		return ret;
	}
	
	public boolean is__user_new_word(String uid, String word){
		Boolean ret = false;
		String sql = "select word from user_new_word u, cards2 c"
				+ " where uid='" + uid + "' and u.wid=c.wid and word='" + word + "'";
		try{
			ResultSet r = Jdbc.query(sql);
			if (r.next()){
				ret = true;
			}
		}catch(SQLException e){
			Log.debug(e.getMessage());
		}
		return ret;
	}
	
	public Boolean update_user_word_starange(String uid, String wid, Boolean bIncrease){
		Boolean ret = false;
		String sql = "update user_new_word"
				+ " set strange = strange";
		if (bIncrease){
			sql += "+1";
		}else{
			sql += "-1";
		}
		sql += " where uid='" + uid + "' and wid=" + wid;
		if (bIncrease){
			sql += " and strange_degree < 8";
		}else{
			sql += " and strange_degree > 0";
		}
		try{
			ret = Jdbc.execute_sql(sql);
		}catch(SQLException e){
			Log.debug(e.getMessage());
		}
		return ret;
	}
	
	public Boolean update_user_word_re_degree(String uid, String wid, Boolean bIncrease){
		Boolean ret = false;
		String sql = "update user_new_word"
				+ " set re_degree = re_degree";
		if (bIncrease){
			sql += "+1";
		}else{
			sql += "-1";
		}
		sql += " where uid='" + uid + "' and wid=" + wid;
		if (bIncrease){
			sql += " and re_degree < 7";
		}else{
			sql += " and re_degree > 0";
		}
		try{
			ret = Jdbc.execute_sql(sql);
		}catch(SQLException e){
			Log.debug(e.getMessage());
		}
		return ret;
	}
	
	private static String get_review_condition(){
		Date date = new Date();
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String time=format.format(date);
		String sRevision = "";
		String sI = "";
		for (int i=1; i<IBinhouse_curve.length; ++i){
			date = new Date();
			date.setDate(date.getDate()-StudentWords.IBinhouse_curve[i]);
			time = format.format(date);
			sI = "re_degree="+i+" and last_time = '"+time+ "' or ";
			sRevision += sI;
		}
		sRevision += "0=1";
		return sRevision;
	}
	
	public static void tmain(String[] args)throws Exception{
		StudentWords sw = new StudentWords();
		Date date = new Date();
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String time=format.format(date);
		String sRevision = "";
		String sI = "";
		for (int i=1; i<2;/*IBinhouse_curve.length;*/ ++i){
			date.setDate(date.getDate()-StudentWords.IBinhouse_curve[i]);
			time = format.format(date);
			sI = "re_degree="+i+" and last_time = '"+time+ "' or ";
			sRevision += sI;
		}
		sRevision += "0=1 or (re_degree=0)";
		System.out.println(sRevision);
	
	}
	
	public static void main(String[] args)throws Exception{
		//StudentWords sw = new StudentWords();
		//Log.debug(sw.find_out("baby", "all"));
		System.out.println(is_user_new_word("a", "right"));
	}
}
