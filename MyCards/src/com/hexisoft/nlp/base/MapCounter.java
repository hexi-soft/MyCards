package com.hexisoft.nlp.base;

import java.util.Collections;
import java.util.Hashtable;
import java.util.ArrayList;
import java.util.Set;

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

	public MapCounter(ArrayList<String> objs) {
		hash = new Hashtable<String,Integer>();
		map(objs);
	}

	public MapCounter(String[] objs) {
		hash = new Hashtable<String,Integer>();
		map0(objs);
	}
	
	public ArrayList<WordFreq> get_items(boolean bSorted){
		Set<String> keySet = hash.keySet();
		ArrayList<WordFreq> wfs = new ArrayList<WordFreq>(keySet.size());
		for(String key:keySet){
			WordFreq wf = new WordFreq();
			wf.word = key;
			wf.freq = hash.get(key);
			wfs.add(wf);
		}
		if (bSorted){
			Collections.sort(wfs, new WordFreq());
		}
		return wfs;
	}
	
	public void map(ArrayList<String> objs) {
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

}

