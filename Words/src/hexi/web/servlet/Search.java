package hexi.web.servlet;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import common.Json;
import common.Log;

public class Search extends HttpServlet{

	private static final long serialVersionUID = 1L;

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		response.setContentType("application/json;charset=UTF-8");
		response.setHeader("Cache-Control", "no-cache");

		String q = request.getParameter("q");
		String resp = "[]";
		if (q != null) {
			File indexDir = new File("indexed1");
			try {
				Log.d("searching...");
				Gson gson = new Gson();
				//resp = gson.toJson(Searcher.searchSentence(indexDir, q));
				Log.d("searched.");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		response.getWriter().write(resp);
	}
}
