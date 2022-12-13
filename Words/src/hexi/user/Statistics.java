package hexi.user;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.TreeSet;

import common.Log;
import hexi.dbc.Jdbc;
import hexi.nlp.Dictionary;
import hexi.nlp.Jwnl;
import hexi.nlp.base.MapCounter;
import hexi.nlp.base.Tokenizer;
import hexi.nlp.base.word_freq;

public class Statistics {

	public static TreeSet<String> get_wordbook_words(String uid){
		TreeSet<String> words = new TreeSet<String>();
		String sql = "select word from user_new_word where uid='"+uid+"'";
		try{
			ResultSet r = Jdbc.query(sql);
			while(r.next()){
				String word = r.getString(1);
				words.add(word);
			}
		}catch(SQLException e){
			Log.debug(e);
		}
		return words;
	}
	
	public static TreeSet<String> get_new_words(String uid, ArrayList<String>words){
		TreeSet<String> new_words = new TreeSet<String>();
		TreeSet<String> old_words = get_wordbook_words(uid);
//		Log.debug(old_words);
		for(String w : words){
			if (!old_words.contains(w)){
				new_words.add(w);
			}
		}
		return new_words;
	}
	
	public static TreeSet<String> get_new_words(String uid, String text){
		String[] tokens = Tokenizer.tokenize(text);
		MapCounter mc = new MapCounter();
		for(String token : tokens) {
			if (token.length()>1 && token.length()<20) {
				mc.add(token, 1);
			}
		}
		ArrayList<word_freq> wfs = mc.get_items();
		mc = new MapCounter();
		for(word_freq wf : wfs) {
			String word = Jwnl.getLemma(wf.word);
			mc.add(word, wf.freq);
		}
		wfs = mc.get_items();
		ArrayList<String> words = new ArrayList<String>();
		for(word_freq wf:wfs){
			words.add(wf.word);
		}
		//Log.debug(words);
		return get_new_words(uid, words);
	}
	
	public static String get_new_word_list(String uid, String text){
		TreeSet<String> new_words = get_new_words(uid,text);
		return Dictionary.look_up(new_words);
	}
	
	public static void main(String[] args) {
		String words = "Please help me Chinese new table tennis taught screens";
		//Log.debug(get_new_word_list("a",words)); 
		Log.debug(get_new_word_list("Wang Siyu",words));
	}

}
