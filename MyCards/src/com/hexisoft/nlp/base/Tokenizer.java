package com.hexisoft.nlp.base;

public class Tokenizer {

	public static String[] tokenize(String s){
		String[] tokens = new String[0];
		if (s.trim().length()>0){
			tokens = s.split("[^A-Za-z]+");			
		}
		return tokens;
	}
}
