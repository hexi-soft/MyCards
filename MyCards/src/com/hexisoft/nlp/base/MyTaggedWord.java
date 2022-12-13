package com.hexisoft.nlp.base;

public class MyTaggedWord extends edu.stanford.nlp.ling.TaggedWord{

	String m_mark;
	
	public void setMark(String mark) {
		m_mark = mark;
	}
	public String getMark() {
		return m_mark;
	}
}
