package hexi.web.servlet;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import common.Log;
import hexi.dbc.Jdbc;

public class UserSents extends HttpServlet{
	private static final long serialVersionUID = 1;		
	
	static Boolean add_sent(String uid, String sentence) {
		Boolean ret = false;
		String sent = sentence;
		String sql = "insert into user_sentence(uid,sent) values(?,?)";
		try {
			Jdbc.prepare_sql(sql);
			Jdbc.pstmt.setString(1, uid);
			Jdbc.pstmt.setString(2, sent);
			if (Jdbc.pstmt.executeUpdate()>0);
				ret = true;
		}catch(SQLException e) {
			Log.d(e);
		}
		return ret;
	}
	
	static Boolean del_sent(String sid) {
		Boolean ret = false;
		try {
			ret = Jdbc.execute_sql("delete from user_sentence where sid="+sid);
		}catch(SQLException e) {
			Log.d(e);
		}
		return ret;
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException
	{
		response.setContentType("text/xml;charset=UTF-8");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("utf-8");
		request.setCharacterEncoding("utf-8");
		String uid =  request.getParameter("uid");
		String cmd = request.getParameter("cmd");
		String resp = "false";
		if (uid != null)
		{
			if (cmd.equals("add")){
				String sent = request.getParameter("sent");
				resp =add_sent(uid, sent).toString();
			}else if(cmd.equals("del")){
				String sid = request.getParameter("sid");
				resp = del_sent(sid).toString();
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
	
	public static void main(String[] args) throws SQLException{
		//Log.d(add_sent("a","I love dogs."));
		//Log.d(del_sent("3"));
		Log.d(del_sent("4"));
	}
}

