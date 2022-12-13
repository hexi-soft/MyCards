package Servlet;

/*Flash Intelligent Blackboard Functions Class*/

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.*;

public class FlashCheckout extends HttpServlet{
	private ServletConfig conf = null;
	public void doGet(HttpServletRequest request, HttpServletResponse response)
	  throws IOException, ServletException
	{
		response.setContentType("application/json;charset=UTF-8");
		response.setHeader("Cache-Control", "no-cache");
		request.setCharacterEncoding("utf-8");
		String word = (String) request.getParameter("word");
		String msg_length = (String)request.getParameter("msg_length");
		String uid = (String)request.getParameter("uid");
		ServletContext application=this.conf.getServletContext();
		if (msg_length!=null)
		if (((String)application.getAttribute("msgs")).length() == Integer.parseInt(msg_length))
			return;
		String resp = (String)application.getAttribute("msgs");
		String msg = (String)request.getParameter("msg");
		if (msg != null && uid != null)
		{
			resp += "<br><span class=\"uid\">"+uid+": </span>"+msg;
			application.setAttribute("msgs", resp);
			//resp = Json.toJson(resp);
		}
		response.getWriter().write(resp);
	}

	public boolean check_out(String param)
	{
		String base = "D:\\tomcat\\webapps\\ROOT\\flash\\";
		String f = base+param+".swf";
		//System.out.println(f);
		File file = new File(f);
		return file.exists();
	}
	
	public void doPost(HttpServletRequest request,HttpServletResponse response)
	  throws IOException,ServletException
	 {
		doGet(request, response);
	 }
			
	public void init(ServletConfig config) throws ServletException {
		conf = config;
		ServletContext application=this.conf.getServletContext();
		application.setAttribute("msgs", "");
		System.out.println("msg: "+((String)application.getAttribute("msgs")).length());
	 }		

}
