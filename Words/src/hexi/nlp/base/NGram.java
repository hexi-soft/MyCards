package hexi.nlp.base;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import common.Log;
import common.UFileReader;
import common.UFileWriter;

public class NGram {
	
	public static ArrayList<word_freq> count_ngrams(List<String> tokens, int n) {
		ArrayList<word_freq> wfs = new ArrayList<word_freq>();
		if (n >= tokens.size()) {
			MapCounter mc = new MapCounter();
			for (int i = 0; i < tokens.size() - n + 1; ++i) {
				StringBuilder sb = new StringBuilder();
				for (int j = i; j < i+n; ++j) {
					sb.append(tokens.get(j) + " ");
				}
				String ngrams = sb.substring(0, sb.length() - 1);
				mc.add(ngrams, 1);
			}
			wfs = mc.get_sorted_items();
		}
		return wfs;
	}
	
	public static ArrayList<word_freq> count_ngrams(String[] tokens, int n) {
		ArrayList<word_freq> wfs = new ArrayList<word_freq>();
		int len = tokens.length;
		if (n <= len) {
			MapCounter mc = new MapCounter();
			for (int i = 0; i < len - n + 1; ++i) {
				StringBuilder sb = new StringBuilder();
				for (int j = i; j < i+n; ++j) {
					sb.append(tokens[j] + " ");
				}
				String ngrams = sb.substring(0, sb.length() - 1);
				mc.add(ngrams, 1);
			}
			wfs = mc.get_sorted_items();
		}
		return wfs;
	}
	
	public static void count_ngrams(String s, int n) {
		String tokens[] = Tokenizer.tokenize(s);
		List<word_freq> wfs = count_ngrams(tokens, n);
		StringBuilder sb = new StringBuilder();
		int i=0;
		for (word_freq wf : wfs) {
			if(wf.word.length()>1) {
				++i;
				sb.append(i+"\t"+wf.toString()+"\r\n");
				Log.d(wf);
			}
		}
		UFileWriter.write("d:\\test.txt", sb.toString());

	}
	
	public static void main(String[] args) {
		String s = "I love you love me love you love me.";
		//s = UFileReader.read("e:\\books\\speech.and.language.Processing.ed3.txt"); 
		count_ngrams(s, 1);
	
	}
}
