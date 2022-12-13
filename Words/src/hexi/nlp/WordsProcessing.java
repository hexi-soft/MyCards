package hexi.nlp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import common.Log;
import common.Pair;
import edu.stanford.nlp.ie.util.RelationTriple;
import edu.stanford.nlp.ling.CoreLabel;
import hexi.nlp.test.Executor;
import hexi.nlp.test.Intent;

public class WordsProcessing {

	public static final String TRIPLES = "triples";
	
	static CoreNLP sNLP = new CoreNLP();
	
	public static void anaphora_resolution() {
		
	}
	
	public static Map<String,Object> process(String words) {
		Map<String,Object> res = new TreeMap<String,Object>();;
		Pair<String,String> words_lan = pretreat(words);
		String _words = words_lan.getFirst();
		String lan = words_lan.getSecond(); 
		if (lan.equals("en")) {
			List<Collection<RelationTriple>> triplesList = collectFacts(_words);
			res.put(TRIPLES, triplesList);
		}else if(lan.equals("zh")){
			;//sNLP.annotate(_words);
		}
		return res;
	}
	
	static List<Collection<RelationTriple>> collectFacts(String words) {
		sNLP.annotate(words);
		 return sNLP.triples();
	}
	
	public static String judgeLanguage(String words) {
		String lan = "";
		char ch = words.charAt(0);
		if (ch<128) {
			lan = "en";
		}
		else if(ch>=0x4e00) {
			lan = "zh";
		}
		return lan;
	}
	
	public static String normalize(String words) {
		String r = words;
		return r;
	}
	
	public static Pair<String,String> pretreat(String words){
		Pair<String,String> pair = new Pair<String,String>();
		String lan = judgeLanguage(words);
		words = normalize(words);
		pair.setFirst(words);
		pair.setSecond(lan);
		return pair;
	}
	
	public static void main(String[] args) {
		 

	}

}
