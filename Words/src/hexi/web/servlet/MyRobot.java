package hexi.web.servlet;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MyRobot extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException
	{
		response.setContentType("text/xml;charset=UTF-8");
		response.setHeader("Cache-Control", "no-cache");
		request.setCharacterEncoding("utf-8");
		
		response.addHeader("content-type", "application/javascript");
		String func = request.getParameter("jsoncallback");
		String uid = request.getParameter("uid");
		String resp = func;
		if (uid != null)
		{
			String cmd = request.getParameter("cmd");
			if (cmd != null) {
				resp += "(\"" + talkBack(uid, cmd) + "\");";
			}
		}
		response.getWriter().write(resp);
	}
	
	String talkBack(String uid, String cmd) {
		String ret = "Hi!";
		
		return ret;
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
	
	public static void main(String[] args) {
		

	}

}
