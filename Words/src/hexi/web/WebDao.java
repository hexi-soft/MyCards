package hexi.web;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import common.Log;
import hexi.dbc.Jdbc2;
import hexi.web.base.UserNewWord;

public class WebDao {

	private Jdbc2 mDB;
	
	public List<UserNewWord> getUserNewWords(String uid) {
		ArrayList<UserNewWord> newWords = new ArrayList<UserNewWord>();
		String sql = "select * from student_new_word where uid='"+uid.replace("'", "''")+"'";
		try {
			ResultSet rs = mDB.query(sql);
			while (rs.next()) {
				String user = rs.getString("uid");
				String word = rs.getString("word");
				Date addTime = rs.getDate("add_time");
				String memo = rs.getString("memo");
				String explains = rs.getString("explains");
				String source = rs.getString("source");
				newWords.add(new UserNewWord(user,word,addTime,memo,explains,source));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return newWords;
	}
	
	public List<UserNewWord> getUserNewWords(String uid, String condition_expression) {
		ArrayList<UserNewWord> newWords = new ArrayList<UserNewWord>();
		String sql = "select * from student_new_word where uid='"+uid.replace("'", "''")+"'";
		if (condition_expression != null && !condition_expression.isEmpty()) {
			sql += " and " + condition_expression;
		}
		try {
			ResultSet rs = mDB.query(sql);
			while (rs.next()) {
				String user = rs.getString("uid");
				String word = rs.getString("word");
				Date addTime = rs.getDate("add_time");
				String memo = rs.getString("memo");
				String explains = rs.getString("explains");
				String source = rs.getString("source");
				newWords.add(new UserNewWord(user,word,addTime,memo,explains,source));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return newWords;
	}
	
	public List<UserNewWord> getUserNewWords(String uid, String condition_expression, int maximum) {
		ArrayList<UserNewWord> newWords = new ArrayList<UserNewWord>();
		String sql = "select * from student_new_word where uid='"+uid.replace("'", "''")+"'";
		if (condition_expression != null && !condition_expression.isEmpty()) {
			sql += " and " + condition_expression;
		}
		try {
			ResultSet rs = mDB.query(sql, maximum);
			while (rs.next()) {
				String user = rs.getString("uid");
				String word = rs.getString("word");
				Date addTime = rs.getDate("add_time");
				String memo = rs.getString("memo");
				String explains = rs.getString("explains");
				String source = rs.getString("source");
				newWords.add(new UserNewWord(user,word,addTime,memo,explains,source));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return newWords;
	}
	
	public WebDao (String dbms, String database, String ip, String user, String pwd) {
		mDB = new Jdbc2(dbms, database, ip, user, pwd);
	}
	
	public static void main(String[] args) {
		WebDao db = new WebDao(Jdbc2.SQL_SERVER_2005, "txtkbase", "192.168.1.112", "sa", "hujia521");
		Log.l(db.getUserNewWords("a", "add_time>'2022-11-22'").size());

	}

}
