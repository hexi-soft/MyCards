package hexi.nlp.base;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import common.Log;
import common.UFileReader;
import common.UFileWriter;
import hexi.nlp.Jwnl;
import hexi.user.Statistics;

public class MapCounter {

	public Hashtable<String,Integer> hash;

	public MapCounter() {
		hash = new Hashtable<String,Integer>();
	}

	public void add(String o, int power) {
		Integer p = hash.get(o);
		if (p != null) {
			p += power;
			hash.put(o, p);
		} // p!=null end
		else { // p==null
			hash.put(o, power);
		} // p=null end
	} // add end

	public MapCounter(List<String> objs) {
		hash = new Hashtable<String,Integer>();
		map(objs);
	}

	public MapCounter(String[] objs) {
		hash = new Hashtable<String,Integer>();
		map0(objs);
	}
	
	public ArrayList<word_freq> get_sorted_items(){
		Set<String> keySet = hash.keySet();
		ArrayList<word_freq> wfs = new ArrayList<word_freq>(keySet.size());
		for(String key:keySet){
			word_freq wf = new word_freq();
			wf.word = key;
			wf.freq = hash.get(key);
			wfs.add(wf);
		}
		Collections.sort(wfs, new word_freq());
		return wfs;
	}
	
	public ArrayList<word_freq> get_items(){
		Set<String> keySet = hash.keySet();
		ArrayList<word_freq> wfs = new ArrayList<word_freq>(keySet.size());
		for(String key:keySet){
			word_freq wf = new word_freq();
			wf.word = key;
			wf.freq = hash.get(key);
			wfs.add(wf);
		}
		return wfs;
	}
	
	public void map(List<String> objs) {
		for (String s : objs) {
			if (hash.get(s) != null) {
				int n = hash.get(s);
				hash.put(s, ++n);
			} else {
				hash.put(s, 1);
			}
		}
	}

	public void map0(String[] objs) {
		for (String s : objs) {
			if(s.isEmpty())
				continue;
			if (hash.get(s) != null) {
				int n = hash.get(s);
				hash.put(s, ++n);
			} else {
				hash.put(s, 1);
			}
		}
	}

	@SuppressWarnings("rawtypes")
	public Hashtable getHash() {
		return hash;
	}
	
	public static void main(String[] args)throws Exception {
		String lemma = Jwnl.getLemma("likes");
		Set<String> words = Statistics.get_wordbook_words("a");
		String s = UFileReader.read_recursively("d:\\corpus\\BASE",".txt");
		String[] tokens = Tokenizer.tokenize(s);
		Log.d(tokens.length);
		MapCounter m = new MapCounter();
		for(String token:tokens) {
			if (token.length()>1 && token.length()<20) {
				m.add(token.toLowerCase(),1);
			}
		}
		/*
		ArrayList<word_freq> t = m.get_items();
		int n = t.size(), i=0;
		Log.d(n);
		MapCounter mc = new MapCounter();
		for(word_freq wf : t) {
			++i;
			String word = Jwnl.getLemma(wf.word);
			double per = i*1.0/n*100;
			Log.d(per+"%:"+word);
			mc.add(word, wf.freq);
		}*/
		ArrayList<word_freq> r = m.get_sorted_items();
		Log.d(r.size());
		String result="";
		for(word_freq wf : r) {
			String word = wf.word;
			if(!words.contains(word)&&word.length()>2) {
				result += wf+"\r\n";
				//Log.d(wf);
			}
		}
		UFileWriter.write("d:\\result.txt", result);
	}

}

