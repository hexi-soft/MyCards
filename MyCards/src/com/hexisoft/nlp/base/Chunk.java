package com.hexisoft.nlp.base;

import java.util.List;

public class Chunk {
	
	List<MyTaggedWord> m_mtws;
	String m_label;
	
	public Chunk(List<MyTaggedWord> mtws) {
		m_mtws = mtws;
	}
	
	public String getTags() {
		String r = "";
		for(MyTaggedWord tw : m_mtws) {
			r += "<"+tw.tag()+">";
		}
		return r;
	}
	
	public void setLabel(String label) {
		m_label = label;
	}
	
	public String getLabel() {
		return m_label;
	}
	
	@Override
	public String toString() {
		String s = "";
		for(MyTaggedWord tp : m_mtws) {
			s += tp.word() + " ";
		}
		if (s.length()>0) {
			s = s.substring(0, s.length()-1);
		}
		return s;
	}

}
