package hexi.nlp.base;

import java.util.Comparator;

public class word_freq implements Comparator<Object>{
	public String word;
	public int freq;
	
	public word_freq() {
	}
	
	public word_freq(String word, int freq) {
		this.word = word;
		this.freq = freq;
	}
	
	public String toString(){
		String s = word+" "+freq;
		return s;
	}
	public int compare(Object o1, Object o2){
		return ((word_freq)o2).freq-((word_freq)o1).freq;
	}
}
