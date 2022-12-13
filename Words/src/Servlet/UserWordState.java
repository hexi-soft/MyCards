package Servlet;

import hexi.dbc.Jdbc;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.*;

import org.apache.log4j.Logger;

import common.*;

public class UserWordState extends HttpServlet{
	
	static final int IBinhouse_curve[] = {0,1,3,5,20,30,40,60};
	
	public void doGet(HttpServletRequest request, HttpServletResponse response)
	  throws IOException, ServletException
	{
		response.setContentType("application/json;charset=UTF-8");
		response.setHeader("Cache-Control", "no-cache");
				
		String wid = request.getParameter("wid");
		String word = request.getParameter("word");
		String uid = request.getParameter("uid");
		String state = request.getParameter("state");
		String value = request.getParameter("value");
		String resp = "false";
				
		if (uid != null && wid != null && wid.matches("\\d+") && word!=null
				&& state != null && value != null)
		{
			resp = Json.toJson(save_user_word_state(wid, word, uid, state, value));
		}
		response.getWriter().write(resp);
	}

	public boolean save_user_word_remember_state(String uid, String word){
		boolean ret = false;
		String sql = "select * from user_new_word where uid=? and word=?";
		int re_degree = 0;
		Date date = new Date();
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String time = format.format(date); 
		try {
			//System.out.println(sql);
			Jdbc.prepare_sql(sql);
			Jdbc.pstmt.setString(1, uid);
			Jdbc.pstmt.setString(2, word);
			ResultSet r = Jdbc.pstmt.executeQuery();
			if (r.next()){
				re_degree = r.getInt("re_degree");
				date.setDate(date.getDate()-IBinhouse_curve[re_degree]);
				String time1 = format.format(date);
				sql = "update user_new_word set re_degree=re_degree+1"
						+ " where uid=? and word=? and (re_degree=0 or "
						+ " last_time='"+time1+"') and re_degree<7";
				Jdbc.prepare_sql(sql);
				Jdbc.pstmt.setString(1, uid);
				Jdbc.pstmt.setString(2, word);
				Jdbc.pstmt.executeUpdate();
				sql = "update user_new_word set last_time='"+time+"'"
						+ ", strange=strange-1, memo='O'"
						+ " where uid=? and word=?";
				Jdbc.prepare_sql(sql);
				Jdbc.pstmt.setString(1, uid);
				Jdbc.pstmt.setString(2, word);
				Jdbc.pstmt.executeUpdate();
				ret = true;
				Log.debug("save user word remember ok.");
			}else{
				Log.debug("Not found:"+uid+":"+word);
				sql = "insert into user_new_word(uid,wid, word) values(?,0, ?)";
				Jdbc.prepare_sql(sql);
				Jdbc.pstmt.setString(1, uid);
				Jdbc.pstmt.setString(2, word);
				Jdbc.pstmt.executeUpdate();
				Log.debug("add word to wordbook ok.");
			}
		}catch(SQLException e){
			System.out.println(e.getMessage());
		}
		return ret;
	}
	
	public boolean save_user_word_state(String wid, String word, String uid, String state, String value)
	{
		boolean ret = false;
		Date date = new Date();
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String time=format.format(date);
		String sql = "insert into user_word_state "
				+"values(?,?,?,?,?,?)";
		try{			
			Jdbc.prepare_sql(sql);
			Jdbc.pstmt.setString(1, uid);
			Jdbc.pstmt.setString(2, wid);
			Jdbc.pstmt.setString(3, state);
			Jdbc.pstmt.setInt(4, Integer.parseInt(value));
			Jdbc.pstmt.setDate(5, new java.sql.Date(date.getTime()));
			Jdbc.pstmt.setString(6, word);
			Jdbc.pstmt.executeUpdate();
			Log.debug("save user word state ok.");
			ret = true;
		}catch(SQLException e){
			int error_code = e.getErrorCode();
			if (error_code==2627){
				sql = "update user_word_state\n"
				 + " set value="+value+",last_time='"+time+"'\n"
				 + " where uid='"+uid+"' and word=? and state='"+state+"'";
				try{
					Jdbc.prepare_sql(sql);
					Jdbc.pstmt.setString(1, word);
					Jdbc.pstmt.executeUpdate();
					Log.debug("update user word state ok.");
					//System.out.println("save user remember state:"+uid+" "+wid);
					ret=true;
				}catch(SQLException e2){
					//e2.printStackTrace();
					Log.debug(e2.getMessage());
				}
			}else{
			 String msg = e.getMessage();
			 System.out.println("[ERROR CODE] "+error_code+"\n"+msg);
			}
		}
		int interval = Integer.parseInt(value);
		if (interval >0 && interval < 5000){
			Log.debug("save user word remember state...");
			save_user_word_remember_state(uid, word);
		}
		return ret;
	}
	
	public void doPost(HttpServletRequest request,HttpServletResponse response)
	  throws IOException,ServletException
	 {
		doGet(request, response);
	 }
			
	public void init(ServletConfig config) throws ServletException {
	 }
	
	public static void main(String[] args){
		String one = "1";
		if (one=="1"){
			System.out.println("o");
		}
	}

}
