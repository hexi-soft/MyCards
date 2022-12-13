package hexi.web.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import common.Json;
import common.Log;
import hexi.dbc.Jdbc;
import hexi.nlp.NLPDao;

public class Checkout extends HttpServlet{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public List<String> get_word_synonyms(String word){
		ArrayList<String> synonyms = new ArrayList<String>();
		word = NLPDao.get_word_lemma(word);
		String sql = "select word,category from word_category\r\n" + 
				"where category in (select top 1 category from word_category\r\n" + 
				"where word='"+word+"' order by len(category) desc) and word != '"+word+"'";
		String syn = "", cate = "";
		try {
			ResultSet rs = Jdbc.query(sql);
			while (rs.next()) {
				syn = rs.getString("word");
				synonyms.add(syn);
			}
		} catch (SQLException e) {
			Log.l(e+"\n"+sql);
		}
		return synonyms;
	}
	
	public static void testGetWordSynonyms() {
		Checkout checkout = new Checkout();
		Log.l(checkout.get_word_synonyms("English"));
	}
	
	public void doGet(HttpServletRequest request, HttpServletResponse response)
	  throws IOException, ServletException
	{
		//response.setContentType("application/javascript");
		response.setHeader("Cache-Control", "no-cache");
		String msg = "testing";
		
		// jsonpøÁ”Ú–≠“È
		response.addHeader("content-type", "application/javascript");
		String func = request.getParameter("jsoncallback");
		String word = request.getParameter("word");
		String syn = get_word_synonyms(word).toString();
		TreeMap<String,Object> r = new TreeMap<String,Object>();
		r.put("src", word);
		r.put("dst", syn);
		//r.put("category", cate);
		msg = Json.toJson(r);
		PrintWriter pw = response.getWriter();
		pw.print(func + "(" + msg + ");");
		pw.flush();
		//response.getWriter().write(resp);
	}

	public void doPost(HttpServletRequest request,HttpServletResponse response)
	  throws IOException,ServletException
	 {
		doGet(request, response);
	 }
			
	public void init(ServletConfig config) throws ServletException {
	 }		
	
	public static void main(String[] args) {
		testGetWordSynonyms();
	}
}
