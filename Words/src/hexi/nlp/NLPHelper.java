package hexi.nlp;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class NLPHelper {
	
	static final String[] IN_CHUNK_TAGS = {"DT","RB","JJ","NN","NNP","NNS", "PRP", "PRP$"};
	static final String[] ENTITY_TAGS = {"NN","NNP", "NNS", "PRP"};
	static final String[] ATTRIBUTE_TAGS = {"JJ"};
			
	public static boolean is_in_chunk(String tag) {
		for(String pos : IN_CHUNK_TAGS) {
			if (tag.equals(pos)) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean is_entity_tag(String tag) {
		for(String pos : ENTITY_TAGS) {
			if (tag.equals(pos)) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean is_attribute_tag(String tag) {
		for(String pos : ATTRIBUTE_TAGS) {
			if (tag.equals(pos)) {
				return true;
			}
		}
		return false;
	}
	
	public static MyLabels get_token_type(String token) {
		MyLabels label = MyLabels.NULL;
		Pattern p = Pattern.compile("^[a-zA-Z]+$|^[A-Za-z]+.*[A-Za-z]+$");
		Matcher m = p.matcher(token);
		if (m.find()) {
			label = MyLabels.Word;
		}else {
			
			label = MyLabels.Punc;
		}
		return label;
	}
	
	public static String get_token_label(String token) {
		String label = "";
		Pattern p = Pattern.compile("^[a-zA-Z]+$|^[A-Za-z]+.*[A-Za-z]+$");
		Matcher m = p.matcher(token);
		if (m.find()) {
			label = "Word";
		}else {
			label = "Punc";
		}
		return label;
	}
	
	public static boolean is_punctuation(char c) {
		boolean ret = false;
		if (c!=' ' && (c<'A'||c>'Z'&&c<'a'||c>'z')) {
			ret = true;
		}
		return ret;
	}
	
	static String tokenize_sentence(String s) {
		String res = s.trim();
		int len = s.length();
		int i = len;
		while (i>1) {
			char c = s.charAt(i-1);
			if (NLPHelper.is_punctuation(c)) {
				--i;
			}else {
				break;
			}
		}
		if (i>0) {
			char c = s.charAt(i-1);
			if (c != ' ') {
				res = s.substring(0,i)+" "+s.substring(i);
			}
		}
		return res;
	}
	
}
