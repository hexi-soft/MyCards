package hexi.web.servlet;

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

public class Message extends HttpServlet{
	private static final long serialVersionUID = 1;
	
	private String get_messages(HttpServletRequest request){
		String res = "";
		String m_id = request.getParameter("m_id");
		if (m_id == null)
			return res;
		String sql = "select m_id,msg,m.memo,m.uid,usr from messages m,users u where m.uid=u.uid and m_id>"+m_id;
		try{
			ResultSet r = Jdbc.query(sql);
			while(r.next()){
				int id = r.getInt("m_id");
				String msg = r.getString("msg");
				//String uid = r.getString("uid");
				String usr = r.getString("usr");
				String memo = r.getString("memo");
				res += "<message m_id=\""+id+"\" usr=\""+usr+"\" memo=\""+memo+"\">"
						+ msg+"</message>\n";
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
		String uid = request.getParameter("uid");
		if (msg != null){
			try{
				Jdbc.prepare_sql("insert into messages (msg,uid) values(?,?)");
				Jdbc.pstmt.setString(1, msg);
				Jdbc.pstmt.setString(2, uid);
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
	
	private String add_picture(HttpServletRequest request){
		String res = "<info>";
		String msg = request.getParameter("msg");
		String pic = request.getParameter("pic");
		String uid = request.getParameter("uid");
		if (pic != null){
			try{
				Jdbc.prepare_sql("insert into messages (msg, memo ,uid) values(?,?,?)");
				Jdbc.pstmt.setString(1, msg);
				Jdbc.pstmt.setString(2, pic);
				Jdbc.pstmt.setString(3, uid);
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
		String resp = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<root>\n";
		if (uid != null)
		{
			if (cmd.equals("check")){
				resp += get_messages(request);
			}else if(cmd.equals("add")){
				resp += add_message(request);
			}else if(cmd.equals("del")){
				resp += del_message(request);
			}else if(cmd.equals("add_pic")){
				resp += add_picture(request);
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
