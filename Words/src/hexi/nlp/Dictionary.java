package hexi.nlp;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.TreeSet;

import common.Json;
import common.Log;

import hexi.dbc.Jdbc;

public class Dictionary {

	public static TreeMap<String,Object> lookup(String word){
		TreeMap<String,Object> ret = null;
		String sql = "select wid, word, chinese from words where word=?";
		Jdbc.prepare_sql(sql);
		try{
			Jdbc.pstmt.setString(1, word);
			ResultSet r = Jdbc.pstmt.executeQuery();
			if(r.next()){
				ret = new TreeMap<String,Object>();
				String e = r.getString("word");
				String c = r.getString("chinese");
				int wid = r.getInt("wid");
				ret.put("e", e);
				ret.put("c", c);
				ret.put("wid", wid);
			}
		}catch(SQLException e){
			Log.debug(e);
		}
		return ret;
	}
	
	public static ArrayList<Object> look_them_up(TreeSet<String> words){
		ArrayList<Object> ret = new ArrayList<Object>();;
		for(String word : words){			
			TreeMap<String,Object> d = lookup(word);
			if (d != null){
				ret.add(d);
			}
		}
		return ret;
	}

	public static String look_up(TreeSet<String> words){
		ArrayList<Object> r = look_them_up(words);
		return Json.toJson(r);
	}
	
	public static String get_lemma(String word) {
		if (word.isEmpty())
			return word;
		String ret = word;
		Jdbc.prepare_sql("select * from word_variants where variant=?");
		try {
			Jdbc.pstmt.setString(1, word);
			ResultSet r = Jdbc.pstmt.executeQuery();
			if (r.next()) {
				ret = r.getString(2);
			}else {
				String lemma = Jwnl.getLemma(word);
				if (!lemma.contentEquals(word)) {
					ret = lemma;
					String sql = "insert into word_variants(variant,word,memo)"
							+ "values('"+word+"','"+ret+"','')";
					Jdbc.execute_sql(sql);
				}
			}
		}catch(SQLException e) {
			Log.d(e);
		}
		return ret;
	}
	
	public static void main(String[] args) {
		Log.d(get_lemma("stopped"));
	}

}
