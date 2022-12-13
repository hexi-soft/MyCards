package Servlet;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.youdao.ai.FanyiV3Demo;
import com.youdao.ai.YoudaoDictItem;

import common.Log;
import hexi.dbc.Jdbc;
import hexi.nlp.Dao;
import hexi.nlp.base.Mapper;
import hexi.nlp.base.word_freq;

public class WordVec extends HttpServlet{
	public void doGet(HttpServletRequest request, HttpServletResponse response)
	  throws IOException, ServletException
	{
		response.setContentType("application/json;charset=UTF-8");
		response.setHeader("Cache-Control", "no-cache");
				
		String book = request.getParameter("book");
		String uid = request.getParameter("uid");
		String resp = "null";	
		if (book != null&&uid!=null)
		{
			Gson gson = new Gson();
			resp = gson.toJson(get_word_vector(book, uid));
		}
		response.getWriter().write(resp);
	}

	public String get_word_vector(String book_id){
		//log.info("book_id="+book_id);
		String s = Passages.get_passage_content(book_id);
		//log.info(s);
		String res = Mapper.get_word_vect(s);
		return res;
	}
	
	public ArrayList<word_freq> get_term_vector(String book_id){
		String s = Passages.get_passage_content(book_id);
		return Mapper.get_term_vector(s);
	}
	
	public ArrayList<Object[]> get_word_vector(String book, String uid)
	{
		ArrayList<Object[]> word_vec = new ArrayList<Object[]>();
		ArrayList<word_freq> items = get_term_vector(book);
		for (word_freq wf : items) {
			String w = wf.word;
			//Log.l("w:"+w);
			if (w.charAt(0)<'a')
				continue;
			w = Dao.get_word_lemma(w);
			if (!Dao.is_word_interested(uid, w)) {
				continue;
			}
			YoudaoDictItem item = FanyiV3Demo.lookup(w);
			String word = item.getReturnPhrase();
			if (!word.isEmpty() && word.charAt(0)>='a') {
				String explain = item.getTranslation();
				String phonetic = "";
				if (item.getBasic()!=null) {
					String explains = Log.joinStrings(item.getBasic().getExplains()," ");
					if (item.getBasic().getPhonetic()!=null) {
						phonetic = item.getBasic().getPhonetic();
					}
					Dao.save_user_new_word(item.getReturnPhrase(), explains, book, wf.freq, uid);
					Dao.save_word(item.getReturnPhrase(), explains, phonetic, "youdao");
					Object[] objs = new Object[2];
					objs[0] = item;
					objs[1] = wf.freq;
					word_vec.add(objs);
					if (w!=word) {
						try {
							Jdbc.execute_sql("insert into word_variants values('"+w+"','"+word+"','')");
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							//e.printStackTrace();
						}
					}
					Log.l(w+"->"+item.getReturnPhrase()+"\t"+item.getTranslation());
				}
			}
		}
		return word_vec;
	}
	
	public void doPost(HttpServletRequest request,HttpServletResponse response)
	  throws IOException,ServletException
	 {
		doGet(request, response);
	 }
			
	public void init(ServletConfig config) throws ServletException {
		
	 }

	public static void main(String[] args)throws Exception{
		WordVec wv = new WordVec();
		wv.init(null);
		wv.get_word_vector("China_English_Web_Pages", "a");
	}

}
