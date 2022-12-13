package com.hexisoft.nlp.base;

import java.util.Map;
import java.util.TreeMap;

import hexi.common.IToJson;

public class Sentence implements IToJson{
	private int id;
	private String e;
	private String c;
	private String b;
	private int m;
	private String memo;

	public Map<String,Object> toJsonObject(){
		TreeMap<String,Object> map = new TreeMap<String,Object>();
		map.put("e", e);
		map.put("b", b);
		map.put("c", c);
		map.put("m", m);
		map.put("memo", memo);
		return map;
	}
	
	public Sentence(int id, String e, String c, String b, int m, String memo) {
		this.id = id;
		this.e = e;
		this.c = c;
		this.b = b;
		this.m = m;
		this.memo = memo;
	}

	public String toString() {
		return e+" ("+b+")";
	}
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getE() {
		return e;
	}

	public void setE(String e) {
		this.e = e;
	}

	public String getC() {
		return c;
	}

	public void setC(String c) {
		this.c = c;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}
	
}
