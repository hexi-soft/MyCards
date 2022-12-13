package com.hexisoft.nlp.base;

import java.util.ArrayList;
import java.util.HashSet;

import hexi.common.MD5;

public class Text2sents {
	
	static HashSet sMD5set = new HashSet();
	
	public static ArrayList<String> get_sentences(String text){
		ArrayList<String> res = new ArrayList<String>();
		String[] ss = text.split("[¡££¡£¿¡±\\s]+");
		for(String s : ss){
			if (s.length()<5){
				continue;
			}
			String md5 = MD5.md5s(s);
			boolean not_in_set = sMD5set.add(md5);
			if (not_in_set){
				res.add(s);
			}
		}
		return res;
	}
	
	public static String summarize(String text){
		StringBuilder sb = new StringBuilder();
		ArrayList<String> sents = get_sentences(text);
		for(String sent : sents){
			sb.append(sent+" ");
		}
		return sb.toString();
	}

}
