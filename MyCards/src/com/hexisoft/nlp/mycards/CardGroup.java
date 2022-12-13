package com.hexisoft.nlp.mycards;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import hexi.common.SetUtil;

public class CardGroup {
	
	private ArrayList<Card> m_cards;
	private SetUtil<String> m_filters;
	
	public ArrayList<Card> get_cards(){
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
	
	public CardGroup(Collection<String> words){
		m_cards = new ArrayList<Card>();
		int id_seed = 0;
		for(String word : words){
			Card card = new Card();
			card.setId(++id_seed);
			card.setFront(word);
			m_cards.add(card);
		}
	}
	
	public CardGroup(Context context, String database, String sql) {
		m_cards = new ArrayList<Card>();
		try {
			MySQLiteHelper mySQLiteHelper = new MySQLiteHelper(context, database, null);
		    SQLiteDatabase db = mySQLiteHelper.getReadableDatabase();
			Cursor cursor = db.rawQuery(sql, null);
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
				String q = "front="+URLEncoder.encode(front, "utf-8");
				q += "&back=" + URLEncoder.encode(back, "utf-8");
				q += "&book=" + URLEncoder.encode(book, "utf-8");
				q += "&unit=" + unit;
				q += "&memo=" + URLEncoder.encode(memo, "utf-8");
				card.setUrl("file:///android_asset/card.htm?"+q);
				m_cards.add(card);
			}
			mySQLiteHelper.close();
		} catch (Exception e) {
			Log.d("My", e.getMessage());
		}
		Log.d("My","load " + m_cards.size() + " cards.");
	}
	

}
