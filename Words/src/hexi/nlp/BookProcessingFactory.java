package hexi.nlp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import common.Log;
import common.SetUtil;
import hexi.nlp.base.word_freq;

public class BookProcessingFactory {

	public static SetUtil<String> filtUpper(SetUtil<String> oldSet){
		SetUtil<String> newSet = new SetUtil<String>();
		for(String s : oldSet) {
			if (!s.isEmpty() && s.charAt(0)<'a'||s.length()<2) {
				continue;
			}
			newSet.add(s);
		}
		return newSet;
	}
	
	static void test() {
		SetUtil<String> pupilWords = NLPDao.getPupilWords();
		List<Chapter> chapters = NLPDao.get_book_chapters("ECaillou");
		ArrayList<word_freq> wfs = new ArrayList<word_freq>();
		for(Chapter c : chapters) {
			SetUtil<String> newWords = pupilWords.diffSet(filtUpper(c.getWords())); 
			int newWordsNum = newWords.size();
			Log.l(newWords);
			wfs.add(new word_freq(c.getId()+" "+c.getTitle(), newWordsNum));
			Log.p(c.getId()+" "+c.getTitle()+" "+newWordsNum);
		}
		Collections.sort(wfs, new word_freq());
		for (word_freq wf : wfs) {
			Log.p(wf.word+" "+wf.freq);
		}
	}
	
	public static void main(String[] args) {
		test();

	}

}
