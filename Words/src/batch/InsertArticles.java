package batch;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import common.Log;
import common.MD5;
import common.UFileReader;
import hexi.dbc.Jdbc;

public class InsertArticles {

	static void test() {
		String s = UFileReader.read("e:\\r\\news_in_levels\\8 billion people in world – level 1.txt");
		String id = MD5.md5s(s);
		Log.l("id:"+id);
		String author = "News in Levels Team";
		String source = "News in Levels";
		String[] ss = s.split("\\s+");
		int words_num = ss.length;
		Log.l("words_num:"+words_num);
		String[] lines = s.split("\r\n");
		String title = lines[0];
		Log.l("title:"+title);
		String post_time = lines[2];
		SimpleDateFormat ft = new SimpleDateFormat ("dd-MM-yyyy hh:mm");
		Date time = new Date();
		java.sql.Date t = new java.sql.Date(time.getTime());
		try {
			time = ft.parse(post_time);
			t = new java.sql.Date(time.getTime());
			String sql = "insert articles values(?,?,?,?,?,?,?,?)";
			Jdbc.prepare(sql);
			Jdbc.setString(1, id);
			Jdbc.setString(2, title);
			Jdbc.setString(3, s);
			Jdbc.setString(4, author);
			Jdbc.setString(5, source);
			Jdbc.pstmt.setDate(6, t);
			Jdbc.setInt(7, words_num);
			Jdbc.setString(8, "news_in_levels");
			Jdbc.execute();
		} catch (Exception e) {
			Log.d(e);
		}
		Log.l("post_time:"+t);
		Log.l("post_time:"+post_time);
		
	}
	public static void main(String[] args) {
		test();

	}

}
