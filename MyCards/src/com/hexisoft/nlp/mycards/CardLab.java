package com.hexisoft.nlp.mycards;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Map;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import hexi.common.UFileReader;

import com.hexisoft.nlp.base.NewWord;
import com.hexisoft.nlp.web.Text2html;

public class CardLab {
	private static CardLab sCardLab;
	private ArrayList<Card> m_cards;

	private Text text;
	private static int text_page_size = 1000;
	MySQLiteHelper mySQLiteHelper;
    public SQLiteDatabase db;
    
	MyApp m_app;
	
	static String m_sql;
	
	public Text getText(){
		return text;
	}
	
	public static int get_text_page_size(){
		return text_page_size;
	}
	
	private CardLab(Context c, ArrayList<Map<String, Object>> word_freq_list) {
		m_cards = new ArrayList<Card>();
		m_app = (MyApp)c.getApplicationContext();
		try {
			mySQLiteHelper = new MySQLiteHelper(c, "txtkbase", null);
			mySQLiteHelper.get_all_words();
			int i = 0;
			for (Map<String, Object> wf : word_freq_list) {
				++i;
				String front = (String) wf.get("word");
				String back = mySQLiteHelper.lookup(front);
				back = back==null? "":back;
				Card card = new Card(i, front, back, "", 1, "");
				String q = "id=" + i + "&front="
						+ URLEncoder.encode(front, "utf-8");
				q += "&back=" + URLEncoder.encode(back, "utf-8");
				card.setUrl("file://" + m_app.data_dir + "word_card.htm?" + q);
				m_cards.add(card);
			}
		} catch (Exception e) {
			Log.d("My", e.getMessage());
		}
	}
	
	private CardLab(Context c, SQLiteDatabase database) {
		m_app = (MyApp)c.getApplicationContext();
		m_cards = new ArrayList<Card>();
		try {
			mySQLiteHelper = new MySQLiteHelper(c, "txtkbase", null);
		    db = mySQLiteHelper.getReadableDatabase();
			Cursor cursor = db.rawQuery(m_sql, null);
			while (cursor.moveToNext()) {
				int column_index = cursor.getColumnIndex("front");
				String front = cursor.getString(column_index);
				column_index = cursor.getColumnIndex("id");
				int id = cursor.getInt(column_index);
				column_index = cursor.getColumnIndex("back");
				String back = cursor.getString(column_index);
				column_index = cursor.getColumnIndex("book");
				String book = cursor.getString(column_index);
				column_index = cursor.getColumnIndex("unit");
				int unit = cursor.getInt(column_index);
				column_index = cursor.getColumnIndex("memo");
				String memo = cursor.getString(column_index);
				Card card = new Card(id, front, back, book, unit, memo);
				String q = "id="+id+"&front="+URLEncoder.encode(front, "utf-8");
				q += "&back=" + URLEncoder.encode(back, "utf-8");
				q += "&book=" + URLEncoder.encode(book, "utf-8");
				q += "&unit=" + unit;
				q += "&memo=" + URLEncoder.encode(memo, "utf-8");
				//card.setUrl("file:///android_asset/word_card.htm?"+q);
				card.setUrl("file://"+m_app.data_dir+"word_card.htm?"+q);
				m_cards.add(card);
			}
			mySQLiteHelper.close();
		} catch (Exception e) {
			Log.d("My", e.getMessage());
		}
		//MyLog.d("load " + m_cards.size() + " cards.");
	}

	private CardLab(Context c) {
		m_app = (MyApp)c.getApplicationContext();
		m_cards = new ArrayList<Card>();
		try {
			mySQLiteHelper = new MySQLiteHelper(c, "txtkbase", null);
		    db = mySQLiteHelper.getReadableDatabase();
			Cursor cursor = db.rawQuery(m_sql, null);
			while (cursor.moveToNext()) {
				int column_index = cursor.getColumnIndex("e");
				String front = cursor.getString(column_index);
				column_index = cursor.getColumnIndex("i");
				int id = cursor.getInt(column_index);
				column_index = cursor.getColumnIndex("c");
				String back = cursor.getString(column_index);
				column_index = cursor.getColumnIndex("b");
				String book = cursor.getString(column_index);
				column_index = cursor.getColumnIndex("u");
				int unit = cursor.getInt(column_index);
				column_index = cursor.getColumnIndex("m");
				String memo = cursor.getString(column_index);
				Card card = new Card(id, front, back, book, unit, memo);
				String q = "front="+URLEncoder.encode(front, "utf-8");
				q += "&back=" + URLEncoder.encode(back, "utf-8");
				q += "&book=" + URLEncoder.encode(book, "utf-8");
				q += "&unit=" + unit;
				q += "&memo=" + URLEncoder.encode(memo, "utf-8");
				//card.setUrl("file:///android_asset/sent_card.htm?"+q);
				card.setUrl("file://"+m_app.data_dir+"sent_card.htm?"+q);
				m_cards.add(card);
			}
			mySQLiteHelper.close();
		} catch (Exception e) {
			Log.d("My", e.getMessage());
			//MyLog.t(e.getMessage());
		}
		//MyLog.d("load " + m_cards.size() + " cards.");
	}
	
	private CardLab(Context c, String filepath) {
		m_cards = new ArrayList<Card>();
       	String s = UFileReader.read(filepath);
       	s = s.replaceAll("\\.{4,}|_{4,}","....");
       	Log.d("My", "file length:"+s.length()+"");
       	if (s.length()>0){
       		text = new Text(s, text_page_size);
       		do{ 
       		String page = text.get_current_page();
       		String page_html = Text2html.getHTML("",page);
       		//Log.d("My", "page length:"+page.length()+"");
       		byte[] buf = page_html.getBytes();
       		//File sdCardDir = Environment.getExternalStorageDirectory();
       		File cacheDir = c.getCacheDir();
       		File file = new File(cacheDir, text.get_page_no()+".htm"); 
       		Card card = new Card();
       		card.setFront(page);
       		card.setBack("");
       		card.setUrl("file://"+file.toString());
       		try{
       			FileOutputStream out  = new FileOutputStream(file);
       			//OutputStreamWriter osw = new OutputStreamWriter (out);
       			out.write(buf);
       			//osw.close();
       			out.close();
       			m_cards.add(card);
       		}catch(Exception e){
       			Log.d("My", e.getMessage());
       		}
       		//textSpeak();
       		}while(text.next_page());
       		text = new Text(s, text_page_size);
       	}else{
       		Log.d("My","Empty file!");
       	}	
	}

	public CardLab(Context c, ArrayList<NewWord> new_words, String version) {
		m_cards = new ArrayList<Card>();
		m_app = (MyApp)c.getApplicationContext();
		try {
			mySQLiteHelper = new MySQLiteHelper(c, "txtkbase", null);
			mySQLiteHelper.get_all_words();
			int i = 0;
			for (NewWord nw : new_words) {
				++i;
				String front = nw.get_word();
				String back = nw.get_explains();
				back = back==null? "":back;
				Card card = new Card(i, front, back, "", 1, "");
				String q = "id=" + i + "&front="
						+ URLEncoder.encode(front, "utf-8");
				q += "&back=" + URLEncoder.encode(back, "utf-8");
				card.setUrl("file://" + m_app.data_dir + "word_card.htm?" + q);
				m_cards.add(card);
			}
		} catch (Exception e) {
			Log.d("My", e.getMessage());
		}
	}

	public ArrayList<Card> getCards() {
		return m_cards;
	}

	public Card getCard(int id) {
		for (Card c : m_cards) {
			if (c.getId() == id) {
				return c;
			}
		}
		return null;
	}

	public static CardLab get_word(Context c, String sql, SQLiteDatabase db) {
		m_sql = sql;
		sCardLab = new CardLab(c.getApplicationContext(), db);
		return sCardLab;
	}
	
	public static CardLab get_words(Context c, ArrayList<Map<String,Object>> word_freq_list) {
		sCardLab = new CardLab(c, word_freq_list);
		return sCardLab;
	}
	
	public static CardLab get_sent(Context c, String sql) {
		m_sql = sql;
		sCardLab = new CardLab(c.getApplicationContext());
		return sCardLab;
	}
	
	public static CardLab get_page(Context c, String filepath) {
		sCardLab = new CardLab(c.getApplicationContext(), filepath);
		return sCardLab;
	}

	public static CardLab get_new_words(Context c, ArrayList<NewWord> new_words) {
		sCardLab = new CardLab(c, new_words, "1");
		return sCardLab;
	}
}
