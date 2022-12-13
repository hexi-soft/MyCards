package hexi.web;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.TreeMap;

import Servlet.StudentWords;
import Servlet.UserBooks;
import common.Log;
import hexi.dbc.Jdbc;

public class Dictionary {
	
	private static TreeMap<String, String> word_variants;
	
	static{
		word_variants = new TreeMap<String,String>();
		String sql = "select * from word_variants";
		try {
			Jdbc.prepare_sql(sql);
			ResultSet r = Jdbc.pstmt.executeQuery();
			while(r.next()){
				String variant = r.getString(1);
				String word = r.getString(2);
				word_variants.put(variant, word);
			}
		}catch(SQLException e){
			e.printStackTrace();
		}
		
	}
	
	public static TreeMap<String,Object> lookup(String uid, String word)
	{
		TreeMap<String,Object> result = new TreeMap<String,Object>();
		ArrayList<TreeMap<String,String>> dst = new ArrayList<TreeMap<String,String>>();
		TreeMap<String,String> item = null;
		ResultSet r = null;
		//ArrayList<TreeMap<String,String>> books = UserBooks.get_user_books(uid);
		String sql = "select * from cards where (English = ? or English = ?)"
				+ " and book in(select bid from user_book where uid=?)";
		sql = "select * from cards where (English = ? or English = ?)"
				+ " and book='ECEE_VOC'";
		String explain = "";
		try{
			Jdbc.prepare_sql(sql);
			Jdbc.pstmt.setString(1, word);
			Jdbc.pstmt.setString(2, word.toLowerCase());
			r = Jdbc.pstmt.executeQuery();
			if (r.next()){
				item = new TreeMap<String,String>();
				String wid = r.getString("wid");
				word = r.getString("English");
				explain = r.getString("Chinese");
				String book = r.getString("book");
				String module = r.getString("module");
				String memo = r.getString("memo");
				item.put("id", wid);
				item.put("explains", explain);
				item.put("book", book);
				item.put("module", module);
				item.put("memo", memo);
				dst.add(item);
			}else {
				String word0 = hexi.nlp.Dictionary.get_lemma(word); 
				if (!word0.contentEquals(word)) {
					word = word0;
					//Log.d(word);
					sql = "select * from cards where (English = ?)"
							+ " and book='ECEE_VOC'";					
					Jdbc.prepare_sql(sql);
					Jdbc.pstmt.setString(1, word);
					r = Jdbc.pstmt.executeQuery();
					if (r.next()){
						item = new TreeMap<String,String>();
						String wid = r.getString("wid");
						explain = r.getString("Chinese");
						String book = r.getString("book");
						String module = r.getString("module");
						String memo = r.getString("memo");
						item.put("id", wid);
						item.put("explains", explain);
						item.put("book", book);
						item.put("module", module);
						item.put("memo", memo);
						dst.add(item);
					}
				}

			}
			if (r != null)
				r.close();
		}catch(SQLException e){
			System.out.println("Dictionary.lookup: sql="+sql);
			System.out.println(e);
			//e.printStackTrace();
		}
		result.put("src", word);
		result.put("dst", dst);
		StudentWords.save_user_new_word(uid, word, explain);
		return result;
	}

	public static ArrayList<String> get_variants(String word){
		ArrayList<String> variants = new ArrayList<String>();
		String sql = "select variant from word_variants where word=?";
		try {
			Jdbc.prepare_sql(sql);
			Jdbc.pstmt.setString(1, word);
			ResultSet r = Jdbc.pstmt.executeQuery();
			while(r.next()) {
				String variant = r.getString(1);
				variants.add(variant);
			}
		}catch(SQLException e) {
			Log.debug(e.getMessage());
		}
		return variants;
	}
	
	public static void main(String[] args){
		//System.out.println(lookup("We")[2]);
		Log.debug(get_variants("let").toString());
		Log.d(lookup("a","looks"));
	}
}
