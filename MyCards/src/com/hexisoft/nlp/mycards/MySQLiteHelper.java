package com.hexisoft.nlp.mycards;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import com.hexisoft.common.android.MyLog;
import com.hexisoft.common.android.MyMediaPlayer;
import com.hexisoft.nlp.base.NewWord;
import com.hexisoft.nlp.base.Sentence;
import com.hexisoft.nlp.web.WebPage;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import hexi.common.MD5;
import hexi.common.SetUtil;

public class MySQLiteHelper extends SQLiteOpenHelper {
	
	private static final int version = 7;
	private static Map<String,String> sFB = new HashMap<String,String>();
	private static SetUtil<String> sAllWords = null;
	
	private Context m_ctx;
	private SQLiteDatabase m_db;
	private  MyMediaPlayer m_player;

	public ArrayList<Sentence> get_book_unit_sents(String book, String unit){
		ArrayList<Sentence> sentences = new ArrayList<Sentence>();
		String sql = "select * from sents where b='"+book+"' and u="+unit;
		MyLog.f(sql);
		try {
		Cursor rs = m_db.rawQuery(sql, null);
		while (rs.moveToNext()) {
			int i = rs.getInt(rs.getColumnIndex("i"));
			String e = rs.getString(rs.getColumnIndex("e"));
			String c = rs.getString(rs.getColumnIndex("c"));
			String b = rs.getString(rs.getColumnIndex("b"));
			int u = rs.getInt(rs.getColumnIndex("u"));
			String m = rs.getString(rs.getColumnIndex("m"));
			sentences.add(new Sentence(i,e,c,b,u,m));
		}
		}catch(Exception e) {
			MyLog.f(e);
		}
		return sentences;
	}
	
	public String get_word_variants(String word) {
		String ret = word;
		String sql = "select front from cards where book='variants' and back=?";
		String[] args = new String[1];
		args[0] = word;
		Cursor rs = m_db.rawQuery(sql, args);
		while (rs.moveToNext()) {
			ret += " " + rs.getString(rs.getColumnIndex("front"));
		}
		return ret;
	}
	
	public String get_word_lemma(String word) {
		String ret = word;
		String sql = "select back from cards where book='variants' and front=?";
		String[] args = new String[1];
		args[0] = word;
		Cursor rs = m_db.rawQuery(sql, args);
		if (rs.moveToNext()) {
			ret = rs.getString(rs.getColumnIndex("back"));
		}
		return ret;
	}
	
	public void execute_sql(String sql) {
		m_db.execSQL(sql);
		m_player.play(R.raw.del);
	}
	
	public int get_table_records_number(String tableName) {
		int r = -1;
		String sql = "select count(*) from "+tableName;
		try {
			Cursor cursor = m_db.rawQuery(sql, null);
			if (cursor.moveToNext()) {
				r = cursor.getInt(0);
			}
		} catch (Exception e) {
			MyLog.f(e.getMessage()+"\n"+sql);
		}
		return r;
	}

	public void add_new_word(String word, String explains, String source, String memo) {
		String sql = "insert into new_words(word,explains,source_id,memo) values(?,?,?,?)";
		try{
			m_db.execSQL(sql, new String[]{word, explains, source, memo});
		}catch(Exception e){
			Log.d("My", e.getMessage());
			MyLog.f(e+"\r\nsql:"+sql);
		}
	}

	public MySQLiteHelper(Context context, String name, CursorFactory factory){
		super(context, name, factory, version);
		m_ctx = context;
		m_db = getWritableDatabase();
		m_player = new MyMediaPlayer(m_ctx);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		
		db.execSQL("create table cards(id integer primary key, front text, back text, type text, book text, unit integer, memo text)");
		db.execSQL("create table sents(i integer primary key, e text, c text, b text, u integer, m text)");
		db.execSQL("create table urls(md5 text primary key, url text unique not null, title text, visited int not null default 0, memo text)");
		get_all_words();
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	
		if (newVersion > oldVersion){
			db.execSQL("create table if not exists books(bookId text primary key, bookTitle text not null, path text not null, memo text not null default '')");
			db.execSQL("create table if not exists web_pages(_id integer primary key autoincrement, title text not null default '',"
					+"content text not null default '', url text not null, memo text not null default '')");
			db.execSQL("create table if not exists new_words(id integer primary key autoincrement, word text not null,"
					+ "explains text not null default '', re_degree int not null default 0, "
					+ "add_time TIMESTAMP not null default(datetime('now','localtime')), "
					+ "source_id text not null default '', memo text not null default '')");
			db.execSQL("create table if not exists new_sents(id integer primary key autoincrement,"
					+" e text not null, c not null default '', memo text not null default '')");
					
		}
		
	}
			
	public void add_new_sent(String en, String ch, String memo) {
		String sql = "insert into new_sents(e,c,memo) values(?,?,?)";
		try{
			m_db.execSQL(sql, new String[]{en, ch, memo});
		}catch(Exception e){
			MyLog.f(e+"\r\nsql:"+sql);
		}
	}
	
	public void add_new_sent(String en) {
		String sql = "insert into new_sents(e) values(?)";
		try{
			m_db.execSQL(sql, new String[]{en});
		}catch(Exception e){
			MyLog.f(e+"\r\nsql:"+sql);
		}
	}
	
	public void add_new_word(String word, String source, String memo) {
		String explains = "";
		String explain = lookup(word);
		if (explain != null) {
			explains = explain;
		}
		String sql = "insert into new_words(word,explains,source_id,memo) values(?,?,?,?)";
		try{
			m_db.execSQL(sql, new String[]{word, explains, source, memo});
		}catch(Exception e){
			Log.d("My", e.getMessage());
			MyLog.f(e+"\r\nsql:"+sql);
		}
	}
	
	public void del_new_words(String condition_expression) {
		String sql = "delete from new_words";
		if (condition_expression != null && !condition_expression.isEmpty())
			sql += " where "+condition_expression;
		try {
			m_db.execSQL(sql);
		}catch(SQLException e) {
			MyLog.f(e.toString());
		}
	}
	
	public void del_new_sents(String condition_expression) {
		String sql = "delete from new_sents";
		if (condition_expression != null && !condition_expression.isEmpty())
			sql += " where "+condition_expression;
		try {
			m_db.execSQL(sql);
		}catch(SQLException e) {
			MyLog.f(e.toString());
		}
	}
	
	public ArrayList<NewWord> get_special_new_words(String where){
		ArrayList<NewWord> words = new ArrayList<NewWord>();
		String sql = "select * from new_words";
		if (where != null && !where.isEmpty()) {
			sql += " where "+where;
		}
		try {
			Cursor cursor = m_db.rawQuery(sql, null);
			while (cursor.moveToNext()) {
				int column_index = cursor.getColumnIndex("id");
				int id = cursor.getInt(column_index);
				column_index = cursor.getColumnIndex("word");
				String word = cursor.getString(column_index);
				String explains = cursor.getString(cursor.getColumnIndex("explains"));
				int re_degree = cursor.getInt(cursor.getColumnIndex("re_degree"));
				String add_time_s = cursor.getString(cursor.getColumnIndex("add_time"));
				SimpleDateFormat sdf = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
		        Date add_time = sdf.parse (add_time_s);
				String source = cursor.getString(cursor.getColumnIndex("source_id"));
				String memo = cursor.getString(cursor.getColumnIndex("memo"));
				words.add(new NewWord(id, word, explains, re_degree, add_time, source, memo));
			}
		} catch (Exception e) {
			Log.d("My", e.getMessage());
			MyLog.f(e+"\r\nsql:"+sql);
		}
		Log.d("My","load " + words.size() + " words.");	
		return words;
	}
	
	public ArrayList<Sentence> get_special_new_sents(String where){
		ArrayList<Sentence> sents = new ArrayList<Sentence>();
		String sql = "select * from new_sents";
		if (where != null && !where.isEmpty()) {
			sql += " where "+where;
		}
		try {
			Cursor cursor = m_db.rawQuery(sql, null);
			while (cursor.moveToNext()) {
				int column_index = cursor.getColumnIndex("id");
				int id = cursor.getInt(column_index);
				column_index = cursor.getColumnIndex("e");
				String sent = cursor.getString(column_index);
				String explains = cursor.getString(cursor.getColumnIndex("c"));
				String book = cursor.getString(cursor.getColumnIndex("b"));
				int u = cursor.getInt(cursor.getColumnIndex("u"));
				String memo = cursor.getString(cursor.getColumnIndex("m"));
				sents.add(new Sentence(id, sent, explains, book, u, memo));
			}
		} catch (Exception e) {
			MyLog.f(e+"\r\nsql:"+sql);
		}
		return sents;
	}
	
	public String lookup(String word){
		if (word==null||word.isEmpty()) {
			return null;
		}
		String w = get_word_lemma(word);
		String sql = "select back from cards where front='"+w.replaceAll("'", "''")+"'"
				+ " and book='words'";
		String back = w;
		Cursor cursor = m_db.rawQuery(sql, null);
		if (cursor.moveToNext()) {
			back = cursor.getString(cursor.getColumnIndex("back"));
		}else if(w.charAt(0)<'a'){
			w = w.toLowerCase();
			sql = "select back from cards where front='"+w.replaceAll("'", "''")+"'"
				+ " and book='words'";
			cursor = m_db.rawQuery(sql, null);
			if (cursor.moveToNext()) {
				back = cursor.getString(cursor.getColumnIndex("back"));
			}
		}
		return back;
	}
	
	public void add_book(String bookPath, String bookTitle){
		String bookId = MD5.md5s(bookPath);
		String sql = "insert into books values(?,?,?,'')";
		try{
			m_db.execSQL(sql, new String[]{bookId, bookTitle, bookPath});
		}catch(Exception e){
			Log.d("My", e.getMessage());
			Log.d("My", sql);
		}
	}
	
	public boolean delete_book(String bookPath){
		boolean r = false;
		String bookId = MD5.md5s(bookPath);
		String sql = "delete from books where bookId='"+bookId+"'";
		try{
			m_db.execSQL(sql);
			m_player.play(R.raw.del);
			r = true;
		}catch(Exception e){
			Log.d("My", e.getMessage());
			Log.d("My", sql);
		}
		return r;
	}
	
	public void add_webpage(String title, String content, String url, String memo, MyLog log){
		String sql = "insert into web_pages(title,content,url,memo) values(?,?,?,?)";
		try{
			m_db.execSQL(sql, new String[]{title, content, url, memo});
		}catch(Exception e){
			Log.d("My", e.getMessage());

		}
	}
	
	public WebPage get_webpage(String url, MyLog log){
		WebPage ret = null;
		String sql = "select title, content from web_pages where url='"+url+"'";
		try {
			Cursor cursor = m_db.rawQuery(sql, null);
			if (cursor.moveToNext()) {
				int column_index = cursor.getColumnIndex("content");
				String content = cursor.getString(column_index);
				column_index = cursor.getColumnIndex("title");
				String title = cursor.getString(column_index);
				ret = new WebPage(url, title, content);
			}
		} catch (Exception e) {
			Log.d("My", e.getMessage());
		}
			
		return ret;
	}
	
	public SetUtil<String> get_special_words(String where){
		SetUtil<String> words = new SetUtil<String>();
		String sql = "select front from cards where "+where;
		try {
			Cursor cursor = m_db.rawQuery(sql, null);
			while (cursor.moveToNext()) {
				int column_index = cursor.getColumnIndex("front");
				String front = cursor.getString(column_index);
				words.add(front);
			}
		} catch (Exception e) {
			Log.d("My", e.getMessage());
		}
		Log.d("My","load " + words.size() + " words.");	
		return words;
	}
	
	public SetUtil<String> get_all_words(){
		if (sAllWords !=null) return sAllWords;
		sAllWords = new SetUtil<String>();
		String sql = "select * from cards where book!='variants'";
		try {
			Cursor cursor = m_db.rawQuery(sql, null);
			while (cursor.moveToNext()) {
				int column_index = cursor.getColumnIndex("front");
				String front = cursor.getString(column_index);
				sAllWords.add(front);
				column_index = cursor.getColumnIndex("back");
				String back = cursor.getString(column_index);
				sFB.put(front, back);
			}
		} catch (Exception e) {
			Log.d("My", e.getMessage());
		}
		Log.d("My","load " + sAllWords.size() + " words.");	
		return sAllWords;
	}
	
	public Map<String,String> get_all_words_with_explains(){
		TreeMap<String,String> words = new TreeMap<String,String>();
		String sql = "select * from cards where book!='variants'";
		try {
			Cursor cursor = m_db.rawQuery(sql, null);
			while (cursor.moveToNext()) {
				int column_index = cursor.getColumnIndex("front");
				String front = cursor.getString(column_index);
				column_index = cursor.getColumnIndex("back");
				String back = cursor.getString(column_index);
				words.put(front,back);
				sFB.put(front, back);
			}
		} catch (Exception e) {
			Log.d("My", e.getMessage());
		}
		Log.d("My","load " + words.size() + " words.");	
		return words;
	}
	
	public Map<String,String> get_word_variants(){
		Map<String,String> variants = new HashMap<String,String>();
		String sql = "select front,back from cards where book='variants'";
		try {
			Cursor cursor = m_db.rawQuery(sql, null);
			while (cursor.moveToNext()) {
				int column_index = cursor.getColumnIndex("front");
				String front = cursor.getString(column_index);
				column_index = cursor.getColumnIndex("back");
				String back = cursor.getString(column_index);
				variants.put(front, back);
			}
		} catch (Exception e) {
			Log.d("My", e.getMessage());
		}
		Log.d("My","load " + variants.size() + " word variants.");	
		return variants;
	}
	
	public SetUtil<String> get_all_distinct_words(){
		SetUtil<String> words = new SetUtil<String>();
		String sql = "select distinct front from cards where book!='variants'";
		try {
			Cursor cursor = m_db.rawQuery(sql, null);
			while (cursor.moveToNext()) {
				int column_index = cursor.getColumnIndex("front");
				String front = cursor.getString(column_index);
				words.add(front);
			}
		} catch (Exception e) {
			Log.d("My", e.getMessage());
		}
		Log.d("My","load " + words.size() + " words.");	
		return words;
	}
	
	public ArrayList<Map<String,Object>> get_all_my_books(){
		ArrayList<Map<String,Object>> books = new ArrayList<Map<String,Object>>();
		String sql = "select * from books";
		try {
			Cursor cursor = m_db.rawQuery(sql, null);
			while (cursor.moveToNext()) {
				Map<String,Object> book = new HashMap<String,Object>();
				int column_index = cursor.getColumnIndex("bookTitle");
				String bookTitle = cursor.getString(column_index);
				book.put("title", bookTitle);
				column_index = cursor.getColumnIndex("bookId");
				String bookId = cursor.getString(column_index);
				book.put("id", bookId);
				column_index = cursor.getColumnIndex("path");
				String bookPath = cursor.getString(column_index);
				book.put("path", bookPath);
				column_index = cursor.getColumnIndex("memo");
				String memo = cursor.getString(column_index);
				book.put("memo", memo);
				books.add(book);
			}
		} catch (Exception e) {
			Log.d("My", e.getMessage());
		}
		Log.d("My","load " + books.size() + " books.");	
		return books;
	}
	
	public SetUtil<String> get_right_words(){
		return get_special_words("memo='O'");
	}
	
	public SetUtil<String> get_wrong_words(){
		return get_special_words("memo='X'");
	}
	
}
