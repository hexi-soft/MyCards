package hexi.web;

import java.sql.ResultSet;
import java.sql.SQLException;

import common.Log;
import hexi.dbc.Jdbc;

public class BookUnit {

	int mUnit;
	String mUnitTitle;
	String mBookId;
	String mBookTitle;
	String mText;
	
	public BookUnit(String bookId, int unit) throws Exception{
		mBookId = bookId;
		mUnit = unit;
		mBookTitle = "unknown";
		mUnitTitle = "unknown";
		mText = "";
		ResultSet r = null;
		String sql = "";
		try {
			sql = "select title from textbooks where id=?";
			Jdbc.prepare_sql(sql);
			Jdbc.pstmt.setString(1, bookId);
			r = Jdbc.pstmt.executeQuery();
			if (r.next()) {
				mBookTitle = r.getString(1); 
			}else {
				throw new Exception("No such book:"+bookId);
			}
			r.close();
			sql = "select topic from book_module where book_id=? and module=?";
			Jdbc.prepare_sql(sql);
			Jdbc.pstmt.setString(1, bookId);
			Jdbc.pstmt.setInt(2, unit);
			r = Jdbc.pstmt.executeQuery();
			if (r.next()) {
				mUnitTitle = r.getString(1); 
			}else {
				throw new Exception("Either no such book: "+mBookTitle+" or no such unit: "+unit);
			}
			r.close();
			sql = "select e from sentences where b=? and m=?";
			Jdbc.prepare_sql(sql);
			Jdbc.pstmt.setString(1, bookId);
			Jdbc.pstmt.setInt(2, unit);
			r = Jdbc.pstmt.executeQuery();
			while (r.next()) {
				mText += r.getString(1)+"\n"; 
			}
			if (mText.length()==0) {
				throw new Exception("No book "+mBookTitle+" unit "+unit+" text");
			}
			r.close();
		}catch(SQLException e) {
			Log.debug(sql);
			Log.debug(e.getMessage());
		}
	}
	
	public String getUnitTitle() {
		return mUnitTitle;
	}

	public void setUnitTitle(String unitTitle) {
		mUnitTitle = unitTitle;
	}

	public String getBookTitle() {
		return mBookTitle;
	}

	public void setBookTitle(String bookTitle) {
		mBookTitle = bookTitle;
	}

	public String getText() {
		return mText;
	}

	public void setText(String text) {
		mText = text;
	}

	public String getBookId() {
		return mBookId;
	}

	public void setBookId(String bookId) {
		mBookId = bookId;
	}

	public int getUnit() {
		return mUnit;
	}

	public void setUnit(int unit) {
		mUnit = unit;
	}

	public static void main(String[] args)throws Exception {
		for(int i=1; i<300; i++) {
			try {
				BookUnit u = new BookUnit("ECaillou", i);
				//Log.debug(u.getBookTitle());
				Log.debug(u.getUnitTitle());
				//Log.debug(u.getText());
			}catch(Exception e) {
				Log.debug(e.getMessage());
			}
		}
	}

}
