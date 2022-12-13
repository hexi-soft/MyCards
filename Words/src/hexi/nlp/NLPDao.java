package hexi.nlp;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import common.Log;
import common.SetUtil;
import hexi.dbc.Jdbc;

public class NLPDao {

	public static SetUtil<String> getPupilWords(){
		SetUtil<String> wordset = new SetUtil<String>();
		ArrayList<String> words = new ArrayList<String>();
		String sql = "select distinct English from cards where book like 'E%(fltrp3)'";
		try {
			ResultSet rs = Jdbc.query(sql);
			while(rs.next()) {
				words.add(rs.getString(1));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		for(String w : words) {
			wordset.add(w);
			wordset.union(getWordVariants(w));
		}	
		return wordset;
	}
	
	public static List<String> getWordVariants(String word){
		ArrayList<String> variants = new ArrayList<String>();
		String sql = "select variant from word_variants where word='"+word.replace("'",	"''")+"'";
		try {
			ResultSet rs = Jdbc.query(sql);
			while(rs.next()) {
				variants.add(rs.getString(1));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return variants;
	}
	
	public static List<Chapter> get_book_chapters(String bookId){
		ArrayList<Chapter> chapters = new ArrayList<Chapter>();
		String sql = "select * from book_module where book_id='"+bookId+"'";
		try {
			ResultSet rs = Jdbc.query(sql);
			int i=0;
			while(rs.next()) {
				String content = rs.getString("ncontent");
				i = rs.getInt("module");
				String title = rs.getString("topic");
				chapters.add(new Chapter(i,title, content, bookId, title));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}		
		return chapters;
	}
	
	public static String get_word_lemma(String word) {
		String lemma = word;
		String w = word.replaceAll("'","''");
		String sql = "select * from word_variants where variant='"+w+"'";
		try {
			ResultSet rs = Jdbc.query(sql);
			if (rs.next()) {
				lemma = rs.getString("word");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return lemma;
	}
	
	public static void testGetLemma(String token) {
		Log.l(get_word_lemma(token));
	}
	
	public static void main(String[] args) {
		testGetLemma("snakes");

	}

}
