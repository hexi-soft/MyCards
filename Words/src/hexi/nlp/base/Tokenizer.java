package hexi.nlp.base;

import common.Log;

public class Tokenizer {

	public static String[] tokenize(String s){
		String[] tokens = new String[0];
		if (s.trim().length()>0){
			tokens = s.split("[^A-Za-z]+");			
		}
		return tokens;
	}
	
	public static void main(String[]args) {
		String s = "";
		String[] tokens = tokenize(s);
		Log.d(tokens.length);
		for(int i=0; i<tokens.length; ++i) {
			Log.d("token: "+tokens[i]);
		}
	}

}
