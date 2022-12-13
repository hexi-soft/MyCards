package hexi.web.servlet;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import common.Json;
import common.Log;
import hexi.dbc.Jdbc;
import hexi.nlp.Jwnl;

public class Dictionary extends HttpServlet{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		response.setContentType("application/json;charset=UTF-8");
		response.setHeader("Cache-Control", "no-cache");

		String word = (String) request.getParameter("word");
		String uid = (String) request.getParameter("uid");
		String resp = "";
		if (word != null && uid != null) {
			//resp  = Json.toJson(hexi.web.Dictionary.lookup(uid, word));			
			resp = Json.toJson(haici(word));
		}
		response.getWriter().write(resp);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		doGet(request, response);
	}
	
	public static String haici(String word) {
		String r = "";
		String sql = "select * from wordfreq220000 where word='"+word+"'";
		Log.l(sql);
		try {
			ResultSet rs = Jdbc.query(sql);
			if (rs.next()) {
				String s1=rs.getString(1)+" ";
				double d2=rs.getDouble(2);
				String s3=" "+rs.getString(3)+" ";
				String s4=" "+rs.getString(4)+" ";
				String s5=rs.getString(5)+" ";
				String s6=rs.getString(6)+" ";
				String s7=rs.getString(7)+" ";
				String s8=rs.getString(8)+" ";
				String s9=rs.getString(9)+" ";
				String s10=rs.getString(10)+" ";
				String s11=rs.getString(11)+" ";
				String s12=rs.getString(12)+" ";
				String s13=rs.getString(13)+" ";
				String s14=rs.getString(14)+" ";
				String s15=rs.getString(15)+" ";
				String s16=rs.getString(16)+" ";
				String s17=rs.getString(17);
				r += s1 + d2 + s4 + s5 + s6 +s7 +s8 +s9 + s10 +s11 + s12 + s13 + s14 + s15 + s16 + s17;
				Log.l("r:"+r);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return r;
	}
	
	public static void main(String[] args) {
		//String mooncake = Jwnl.getLemma("mooncakes");
		Log.d(haici("the"));
	}

}
