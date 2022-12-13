package hexi.nlp;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import common.Log;
import common.MD5;
import hexi.dbc.Jdbc;
import hexi.dbc.Jdbc2;
import hexi.nlp.base.Sentence;
import hexi.nlp.base.WebPage;

public class Dao {
	
	static Set<UserWords> user_list = new HashSet<UserWords>();

	static UserWords getUserWords(String uid) {
		UserWords user = null;
		for (UserWords u : user_list) {
			if (uid == u.getUserId()) {
				return u;
			}
		}
		String level = getUserLevel(uid);
		if (level != null) {
			user = new UserWords(uid, level);
			user_list.add(user);
		}
		return user;
	}
	
	public static String getUserLevel(String uid) {
		String ret = null;
		String sql = "select profession from users where uid='"+uid+"'";
		try {
			ResultSet rs = Jdbc.query(sql);
			if (rs.next()) {
				ret = rs.getString(1);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ret;
	}
	
	public static boolean is_duplicated_url(String url) {
		boolean ret = false;
		if (url==null || url.isEmpty()) {
			ret = true;
		}else {
			String md5 = MD5.md5s(url);
			String sql = "select md5 from url_md5 where md5='"+md5+"'";
			try {
				ResultSet r = Jdbc.query(sql);
				if (r.next()) {
					ret = true;
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		return ret;
	}
	
	public static boolean save_url_md5(String url) throws SQLException {
		boolean ret = false;
		if (url!=null && !url.isEmpty()) {
			String md5 = MD5.md5s(url);
			String sql = "insert url_md5 (md5,url) values(?,?)";
			Jdbc.prepare(sql);
			Jdbc.setString(1, md5);
			Jdbc.setString(2, url);
			try {
				ret = Jdbc.execute()==1;
			}catch(SQLException e) {
				ret = e.getErrorCode()==2627;
				if (!ret) {
					e.printStackTrace();
				}
			}
		}
		return ret;
	}
	
	public static void save_web_page(String title, String content, String url, String memo) {
		if (title!=null && content!=null && url!=null) {
			String sql = "insert web_page(title,ncontent,url,memo) values(?,?,?,?)";
			Jdbc.prepare(sql);
			try {
				Jdbc.setString(1, title);
				Jdbc.setString(2, content);
				Jdbc.setString(3, url);
				Jdbc.setString(4, memo);
				Jdbc.execute();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
		}
	}

	public static HashSet<String> words_from_level(String level){
		HashSet<String> hs = new HashSet<String>();
		String sql = "select word from "+level+"_words";
		try {
			ResultSet rs = Jdbc.query(sql);
			while(rs.next()) {
				String w = rs.getString(1);
				hs.add(w);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return hs;
	}
	
	public static HashSet<String> get_user_words(String uid){
		HashSet<String> hs = new HashSet<String>();
		String level = getUserLevel(uid);
		String sql = "select word from "+level+"_words";
		try {
			ResultSet rs = Jdbc.query(sql);
			while(rs.next()) {
				String w = rs.getString(1);
				hs.add(w);
			}
			sql = "select word from student_new_word where uid='"+uid+"'";
			rs = Jdbc.query(sql);
			while(rs.next()) {
				hs.add(rs.getString(1));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}	
		return hs;
	}
	
	public static void print_set(Set set) {
		for(Object o : set) {
			Log.l(o);
		}
	}
	
	public static String get_word_lemma(String word) {
		String lemma = word;
		word = word.replaceAll("'","''");
		String sql = "select variant, word, memo from word_variants where variant='"+word+"'";
		try {
			ResultSet r = Jdbc.query(sql);
			if (r.next()) {
				lemma = r.getString("word");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return lemma;
	}

	public static boolean is_word_interested(String uid, String word) {
		boolean ret = false;
		UserWords user = null;
		if (word!=null && !word.isEmpty()) {
			if (word.charAt(0)<95) {
				return false;
			}
			user = getUserWords(uid);
			if (user != null) {
				word = get_word_lemma(word);
				if (!user.is_word_familiar(word)) {
					if (word.length()>2) {
						ret = true;
					}
				}
			}
		}
		return ret;
	}

	public static String get_webpage_texts(String where) {
		String text = "";
		String sql = "select ncontent from web_page";
		if (where != null && !where.isEmpty()) {
			sql += " where " + where;
		}
		try {
			ResultSet rs = Jdbc.query(sql);
			while(rs.next()) {
				text += rs.getString(1)+"\r\n";
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return text;
	}

	public static List<WebPage> get_web_pages(String where) {
		ArrayList<WebPage> pages = new ArrayList<WebPage>();
		
		String sql = "select * from web_page";
		if (where != null && !where.isEmpty()) {
			sql += " where " + where;
		}
		try {
			ResultSet rs = Jdbc.query(sql);
			while(rs.next()) {
				String url = rs.getString("url");
				String title = rs.getString("title");
				String content = rs.getString("ncontent");
				pages.add(new WebPage(url,title,content));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return pages;
	}
	
	public static List<Sentence> get_sentences(String where) {
		ArrayList<Sentence> sents = new ArrayList<Sentence>();
		
		String sql = "select * from sentences";
		if (where != null && !where.isEmpty()) {
			sql += " where " + where;
		}
		try {
			ResultSet rs = Jdbc.query(sql);
			while(rs.next()) {
				String sentence = rs.getString("e");
				String chinese = rs.getString("c");
				String book = rs.getString("b");
				int module = rs.getInt("m");
				String memo = rs.getString("memo");
				sents.add(new Sentence(sentence,chinese,book,module,memo));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return sents;
	}
	
	public static void save_sents(String eng, String chi, String book, int module, String memo) {
		String sql = "insert into sentences(e,c,b,m,memo) values(?,?,?,?,?)";
		Jdbc.prepare(sql);
		try {
			Jdbc.setString(1, eng);
			Jdbc.setString(2, chi);
			Jdbc.setString(3, book);
			Jdbc.setInt(4, module);
			Jdbc.setString(5, memo);
			Jdbc.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	public static boolean save_word(String word, String explains, String phonetic, String memo) {
		boolean ret = false;
		String sql = "insert words values(?,?,?,?)";
		Jdbc.prepare(sql);
		try {
			Jdbc.setString(1, word);
			Jdbc.setString(2, explains);
			Jdbc.setString(3, phonetic);
			Jdbc.setString(4, memo);
			Jdbc.execute();
			ret = true;
		} catch (SQLException e) {
			if (e.getErrorCode()!=2627) {
				e.printStackTrace();
			}
		}
		return ret;	
	}
	
	public static boolean save_user_new_word(String word, String explains, String source, int freq, String uid) {
		boolean ret = false;
		String sql = "insert student_new_word(word, explains, source, freq, uid) values(?,?,?,?,?)";
		Jdbc.prepare(sql);
		try {
			Jdbc.setString(1, word);
			Jdbc.setString(2, explains);
			Jdbc.setString(3, source);
			Jdbc.setInt(4, freq);
			Jdbc.setString(5, uid);
			Jdbc.execute();
			ret = true;
		} catch (SQLException e) {
			if (e.getErrorCode()!=2627) {
				e.printStackTrace();
			}
		}
		return ret;	
	}
	
	static void test() throws SQLException {
		String sql = "select * from variants where variant not in (select variant from word_variants)";
		ResultSet rs = Jdbc.query(sql);
		Jdbc2 dbc2 = new Jdbc2("SQLServer2005", "192.168.1.112", "sa", "hujia521");
		while (rs.next()) {
			String v = rs.getString(1);
			v = v.replaceAll("'", "''");
			sql = "select variant, word, memo from word_variant where variant='"+v+"'";
			ResultSet r = dbc2.query(sql);
			if (r.next()) {
				String variant = r.getString(1).replaceAll("'", "''");
				String word = r.getString(2);
				String memo = r.getString(3);
				sql = "insert word_variants values('"+variant+"','"+word+"','"+memo+"')";
				try {
					dbc2.execute_sql(sql);
				}catch(SQLException e) {
					if (e.getErrorCode()!=2627) {
						e.printStackTrace();
						Log.l(sql);
					}
				}
			}
		}
	}
	
	public static void main(String[] args) throws SQLException {
		Log.l(is_word_interested("a", "dark"));
		//Log.l(get_word_lemma("boys"));
		//test();
	}

}
