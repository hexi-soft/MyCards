package com.hexisoft.nlp.mycards;

import org.json.JSONException;
import org.json.JSONObject;

public class Card {

	int mId;
	String mUrl;
	String mFront;
	String mBack;
	String mBook;
	int mUnit;
	String mMemo;
	
	public Card(){
		
	}
	
	public Card(int id, String front, String back, String book, int unit, String memo){
		mId = id;
		mFront = front;
		mBack = back;
		mBook = book;
		mUnit = unit;
		mMemo = memo;
	}
	
	public Card(JSONObject json) throws JSONException{
		mId = json.getInt("i"); 
		mFront = json.getString("e");
		mBack = json.getString("c");
		mBook = json.getString("b");
		mMemo = json.getString("m");
		mUnit = json.getInt("u");		 
	}
	 
	 public JSONObject toJSON() throws JSONException{
		 JSONObject json = new JSONObject();
		 json.put("e",mFront);
		 json.put("c", mBack);
		 json.put("i",mId);
		 json.put("b", mBook);
		 json.put("u",mUnit);
		 json.put("m", mMemo);
		 return json;
	 }

	public int getId() {
		return mId;
	}

	public void setId(int id) {
		mId = id;
	}

	public String getUrl() {
		return mUrl;
	}

	public void setUrl(String url) {
		mUrl = url;
	}

	public String getFront() {
		return mFront;
	}

	public void setFront(String front) {
		mFront = front;
	}

	public String getBack() {
		return mBack;
	}

	public void setBack(String back) {
		mBack = back;
	}

	public String getBook() {
		return mBook;
	}

	public void setBook(String book) {
		mBook = book;
	}

	public int getUnit() {
		return mUnit;
	}

	public void setUnit(int unit) {
		mUnit = unit;
	}

	public String getMemo() {
		return mMemo;
	}

	public void setMemo(String memo) {
		mMemo = memo;
	}
	
	@Override
	public String toString(){
		return mFront+" "+mBack;
	}
	
}
