package Servlet;

import hexi.dbc.Jdbc;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import common.Log;

public class Messages extends HttpServlet{
	private static final long serialVersionUID = 1;
	static Logger log = Log.log;
	
	private String get_messages(HttpServletRequest request, String uid){
		String res = "";
		String m_id = request.getParameter("last_mid");
		if (m_id == null)
			return res;
		String sql = "select m_id, msg, add_time, m.memo, sender, usr as sender_name"
				+ " from messages m, users u"
				+ " where sender=u.uid and m_id>"+m_id
				+ " and (receiver='" + uid + "' or receiver='public')";
//		log.debug(sql);
		try{
			ResultSet r = Jdbc.query(sql);
			while(r.next()){
				int id = r.getInt("m_id");
				String msg = r.getString("msg");
				String sender = r.getString("sender");
				String add_time = r.getString("add_time");
				String sender_name = r.getString("sender_name");
				String memo = r.getString("memo");
				res += "<message m_id=\""+id+"\""
						+ " add_time=\""+add_time+"\""
						+ " sender_id=\""+sender+"\""
						+ " sender_name=\""+sender_name+"\""
						+ " memo=\""+memo+"\">"
						+ msg + "</message>\n";
			}
		}catch(SQLException e){
			//e.printStackTrace();
			System.out.println("get_messages: "+sql);
			System.out.println("get_messages: "+e.getMessage());
		}
		return res;
	}
	
	private String add_message(HttpServletRequest request){
		String res = "<info>";
		String msg = request.getParameter("msg");
		String from = request.getParameter("uid");
		String to = request.getParameter("to");
		log.debug("add_message->msg="+msg+";from="+from+";to="+to);
		if (msg != null){
			try{
				Jdbc.prepare_sql("insert into messages (msg,sender, receiver) values(?,?,?)");
				Jdbc.pstmt.setString(1, msg);
				Jdbc.pstmt.setString(2, from);
				Jdbc.pstmt.setString(3, to);
				Jdbc.pstmt.execute();
				res += "ok";
			}catch(SQLException e){
				//e.printStackTrace();
				System.out.println(e.getMessage());
			}			
		}
		res += "</info>\n";
		return res;				
	}

	private String del_message(HttpServletRequest request){
		String res = "<info>";
		String m_id = request.getParameter("m_id");
		String uid = request.getParameter("uid");
		if (m_id != null && uid.equals("a")){
			try{
				String sql = "delete messages where m_id="+m_id;
				Jdbc.execute_sql(sql);
				res += "ok";
			}catch(SQLException e){
				//e.printStackTrace();
				System.out.println("del_message:"+e.getMessage());
			}			
		}
		res += "</info>\n";
		return res;				
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
//		log.info(uid+":"+cmd);
		String resp = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<root>\n";
		if (uid != null)
		{
			if (cmd.equals("check")){
				resp += get_messages(request,uid);
			}else if(cmd.equals("add")){
				resp += add_message(request);
			}else if(cmd.equals("del")){
				resp += del_message(request);
			}
		}
		resp += "</root>";
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
		ResultSet r = Jdbc.query("select * from messages");
		while(r.next()){
			Date d = r.getDate(3);
			System.out.println(r.getString(2));
			System.out.println(d);
		}
	}
}
