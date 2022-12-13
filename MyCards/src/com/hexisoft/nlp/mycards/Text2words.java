package com.hexisoft.nlp.mycards;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.hexisoft.nlp.base.MapCounter;
import com.hexisoft.nlp.base.NewWord;
import com.hexisoft.nlp.base.Tokenizer;
import com.hexisoft.nlp.base.WordFreq;

import android.content.Context;
import hexi.common.SetUtil;

public class Text2words {

	public static ArrayList<Map<String,Object>> get_new_words(Context context, String text){
		MyCardsActivity ctx = (MyCardsActivity)context;
		ArrayList<Map<String,Object>> nwf = new ArrayList<Map<String,Object>>();
		String[] tokens = Tokenizer.tokenize(text);
		MapCounter mc = new MapCounter();			
		MySQLiteHelper sqlite = new MySQLiteHelper(context,"txtkbase",null);
		Map<String,String> variants = sqlite.get_word_variants();
		for(String token : tokens){
			if (token.length()>2){
				if (token.charAt(0)<'a') {
					continue;
				}
				String w = token;
				String lemma = variants.get(w);
				if (lemma != null){
					w = lemma;
				}
				mc.add(w, 1);
			}
		}
		ArrayList<WordFreq> wfs = mc.get_items(true);
		ctx.l("Get sorted tokens: "+wfs.size());
		SetUtil<String> old_words = sqlite.get_special_words("book like'E%(%)' or book='ECEE_VOC' or book='ECET4'");
		ArrayList<NewWord> new_words = sqlite.get_special_new_words(null);
		for (NewWord nw : new_words) {
			old_words.add(nw.get_word());
		}
		ctx.d("Get old words: "+old_words.size());
		Map<String,String> all_words = sqlite.get_all_words_with_explains();
		ctx.d("Get all words: "+all_words.size());
		int i=0;
		for(WordFreq wf : wfs){
			++i;
			String word = wf.getWord();
			String explains = all_words.get(word);
			ctx.l(word+" "+explains);
			//if (!old_words.contains(word))
			//ctx.d("word in old:"+old_words.contains(word)+" explains:"+explains);
			if (!old_words.contains(word)&&explains!=null){
				//ctx.l(word+" "+explains);
				HashMap<String,Object> mwf = new HashMap<String,Object>();
				mwf.put("word", word);
				mwf.put("freq", wf.getFreq());
				mwf.put("explains", explains);
				nwf.add(mwf);
			}
			//if (i>10000)break;
		}
		sqlite.close();
		return nwf;
	}
	
	public static ArrayList<WordFreq> get_new_words2(Context context, String text){
		ArrayList<WordFreq> nwf = new ArrayList<WordFreq>();
		String[] tokens = Tokenizer.tokenize(text);
		MapCounter mc = new MapCounter();			
		MySQLiteHelper sqlite = new MySQLiteHelper(context,"txtkbase",null);
		Map<String,String> variants = sqlite.get_word_variants();
		for(String token : tokens){
			if (token.length()>2){
				if (token.charAt(0)<'a') {
					continue;
				}
				String w = token;
				String lemma = variants.get(w);
				if (lemma != null){
					w = lemma;
				}
				mc.add(w, 1);
			}
		}
		ArrayList<WordFreq> wfs = mc.get_items(true);
		SetUtil<String> old_words = sqlite.get_special_words("book like'E%(%)' or book='ECEE_VOC' or book='ECET4'");
		ArrayList<NewWord> new_words = sqlite.get_special_new_words(null);
		for (NewWord nw : new_words) {
			old_words.add(nw.get_word());
		}
		SetUtil<String> all_words = sqlite.get_all_distinct_words();
		for(WordFreq wf : wfs){
			String word = wf.getWord();
			if (!old_words.contains(word) && all_words.contains(word)){
				nwf.add(wf);
			}
		}
		sqlite.close();
		return nwf;
	}
}
