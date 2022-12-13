package com.hexisoft.nlp.base;

import java.util.Comparator;

public class WordFreq implements Comparator<Object>{
	public String word;
	public int freq;
	public String toString(){
		String s = word+" "+freq;
		return s;
	}
	public String getWord(){
		return word;
	}
	public int getFreq(){
		return freq;
	}
	public int compare(Object o1, Object o2){
		return ((WordFreq)o2).freq-((WordFreq)o1).freq;
	}
}
