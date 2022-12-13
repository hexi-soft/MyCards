package hexi.nlp.base;

import hexi.dbc.Jdbc;
import hexi.web.Dictionary;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Hashtable;
import java.util.ArrayList;
import java.util.Set;
import java.util.TreeMap;

import common.Json;
import common.Log;
import common.UFileReader;

public class Mapper {

	public Hashtable<String,Integer> hash;

	public Mapper() {
		hash = new Hashtable<String,Integer>();
	}

	public void add(String o, int power) {
		Integer p = hash.get(o);
		if (p != null) {
			p += power;
			hash.put(o, p);
		} // p!=null end
		else { // p==null
			hash.put(o, power);
		} // p=null end
	} // add end

	public Mapper(ArrayList objs) {
		hash = new Hashtable<String,Integer>();
		map(objs);
	}

	public Mapper(String[] objs) {
		hash = new Hashtable<String,Integer>();
		map0(objs);
	}
	
	public ArrayList<word_freq> get_sorted_items(){
		Set<String> keySet = hash.keySet();
		ArrayList<word_freq> wfs = new ArrayList<word_freq>(keySet.size());
		for(String key:keySet){
			word_freq wf = new word_freq();
			wf.word = key;
			wf.freq = hash.get(key);
			wfs.add(wf);
		}
		Collections.sort(wfs, new word_freq());
		return wfs;
	}
	
	public void map(ArrayList<String> objs) {
		for (String s : objs) {
			if (hash.get(s) != null) {
				int n = hash.get(s);
				hash.put(s, ++n);
			} else {
				hash.put(s, 1);
			}
		}
	}

	public void map0(String[] objs) {
		for (String s : objs) {
			if(s.isEmpty())
				continue;
			s = s.toLowerCase();
			if (hash.get(s) != null) {
				int n = hash.get(s);
				hash.put(s, ++n);
			} else {
				hash.put(s, 1);
			}
		}
	}

	@SuppressWarnings("rawtypes")
	public Hashtable getHash() {
		return hash;
	}
	
	public static String get_word_vect(String s){
		String res = "";
		s = s.trim();
		if(s.isEmpty()){
			return "[]";
		}
		String[] ss = s.split("[^A-Za-z]+"); 
		Mapper m = new Mapper(ss);
		ArrayList<word_freq> items = m.get_sorted_items();
		ArrayList<TreeMap<String,Object>> wfs = new ArrayList<TreeMap<String,Object>>();
		for (word_freq item: items){
			TreeMap<String,Object> wf = new TreeMap<String,Object>();
			wf.put("word", item.word);
			wf.put("freq", item.freq);
			wfs.add(wf);
		}
		res = Json.toJson(wfs);
		return res;
	}
	
	public static ArrayList<word_freq> get_term_vector(String s){
		s = s.trim();
		if(s.isEmpty()){
			return null;
		}
		String[] ss = s.split("[^A-Za-z]+"); 
		Mapper m = new Mapper(ss);
		ArrayList<word_freq> items = m.get_sorted_items();
		return items;
	}
	
	public static String get_word_vect0(String s){
		String res = "";
		s = s.trim();
		if(s.isEmpty()){
			return "[]";
		}
		String[] ss = s.split("[^A-Za-z]+"); 
		Mapper m = new Mapper(ss);
		ArrayList<word_freq> items = m.get_sorted_items();
		res = Json.toJson(items);
		return res;
	}
	
	public static void save_text_WV(String s, int id)throws SQLException{
		String[] ss = s.split("[^A-Za-z]+");
		Mapper m = new Mapper(ss);
		ArrayList items = m.get_sorted_items();
		m = new Mapper();
		for (Object item : items){
			word_freq wf = (word_freq)item;
			TreeMap<String,Object> entry = Dictionary.lookup("a", wf.word);
			if (entry == null)
				continue;
			String word = (String)entry.get("src");
			m.add(word, wf.freq);
		}	
		items = m.get_sorted_items();
		Jdbc.prepare_sql("insert into word_freq_book values(?,?,?)");
		for (Object item : items){
			word_freq wf = (word_freq)item;
			Jdbc.pstmt.setString(1, wf.word);
			Jdbc.pstmt.setInt(2, wf.freq);
			Jdbc.pstmt.setInt(3, id);
			Jdbc.pstmt.executeUpdate();
			//System.out.println(item.toString()+"\t"+1);
		}
	}
		
	public static void tmain(String[] args)throws Exception {
		Jdbc.prepare_sql("select * from passages");
		ResultSet r = Jdbc.pstmt.executeQuery();
		while (r.next()){
			int id = r.getInt("p_id");
			String path = r.getString("path");
			String text = UFileReader.read(path);
			String[] ss = text.split("[^A-Za-z]+");
			save_text_WV(text, id);
			System.out.println(path);
		}
	}
	
	public static void main(String[] args)throws Exception {
		Log.debug(get_word_vect("I'm A boy."));
		Log.debug(get_word_vect0("I'm A boy."));
	}

}

