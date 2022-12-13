package hexi.nlp;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;

import common.Log;
import junit.framework.TestCase;

import net.didion.jwnl.JWNL;
import net.didion.jwnl.data.IndexWord;
import net.didion.jwnl.data.POS;
import net.didion.jwnl.data.Pointer;
import net.didion.jwnl.data.PointerType;
import net.didion.jwnl.data.PointerUtils;
import net.didion.jwnl.data.Synset;
import net.didion.jwnl.data.Word;
import net.didion.jwnl.data.list.PointerTargetNodeList;
import net.didion.jwnl.dictionary.Dictionary;
import net.didion.jwnl.dictionary.morph.DefaultMorphologicalProcessor;

public class Jwnl extends TestCase {
	private static Dictionary dict;
	private static PointerUtils pu;
	public static ArrayList<String> get_hyponyms(String word)
	throws Exception {
		ArrayList<String> hyponyms = new ArrayList<String>();
		IndexWord indexWord = dict.getIndexWord(POS.NOUN, word);
		if (indexWord==null)
			return hyponyms;
		Synset set = indexWord.getSense(1);
		Pointer[] pointerArr = set.getPointers(PointerType.HYPONYM);
		for (Pointer x : pointerArr) {
			Synset target = x.getTargetSynset();
			Word[] words = target.getWords();
			for(Word w:words)
				hyponyms.add(w.getLemma());
		}
		System.out.println(hyponyms);
		System.out.println(indexWord.toString());
		return hyponyms;
	}
	
	public static ArrayList<String> get_relevant(Synset syn)
	 throws Exception {
		ArrayList<String> hyponyms = new ArrayList<String>();
		Pointer[] pointerArr = syn.getPointers(PointerType.HYPONYM);
		for (Pointer x : pointerArr) {
			Synset target = x.getTargetSynset();
			Word[] words = target.getWords();
			for (Word w : words)
				hyponyms.add(w.getLemma());
		}
		pointerArr = syn.getPointers(PointerType.ATTRIBUTE);
		for (Pointer x : pointerArr) {
			Synset target = x.getTargetSynset();
			Word[] words = target.getWords();
			for (Word w : words)
				hyponyms.add(w.getLemma());
		}
		pointerArr = syn.getPointers(PointerType.CATEGORY_MEMBER);
		for (Pointer x : pointerArr) {
			Synset target = x.getTargetSynset();
			Word[] words = target.getWords();
			for (Word w : words)
				hyponyms.add(w.getLemma());
		}
		pointerArr = syn.getPointers(PointerType.CAUSE);
		for (Pointer x : pointerArr) {
			Synset target = x.getTargetSynset();
			Word[] words = target.getWords();
			for (Word w : words)
				hyponyms.add(w.getLemma());
		}
		pointerArr = syn.getPointers(PointerType.INSTANCES_HYPONYM);
		for (Pointer x : pointerArr) {
			Synset target = x.getTargetSynset();
			Word[] words = target.getWords();
			for (Word w : words)
				hyponyms.add(w.getLemma());
		}
		pointerArr = syn.getPointers(PointerType.MEMBER_HOLONYM);
		for (Pointer x : pointerArr) {
			Synset target = x.getTargetSynset();
			Word[] words = target.getWords();
			for (Word w : words)
				hyponyms.add(w.getLemma());
		}
		pointerArr = syn.getPointers(PointerType.MEMBER_MERONYM);
		for (Pointer x : pointerArr) {
			Synset target = x.getTargetSynset();
			Word[] words = target.getWords();
			for (Word w : words)
				hyponyms.add(w.getLemma());
		}
		pointerArr = syn.getPointers(PointerType.NOMINALIZATION);
		for (Pointer x : pointerArr) {
			Synset target = x.getTargetSynset();
			Word[] words = target.getWords();
			for (Word w : words)
				hyponyms.add(w.getLemma());
		}
		pointerArr = syn.getPointers(PointerType.PART_HOLONYM);
		for (Pointer x : pointerArr) {
			Synset target = x.getTargetSynset();
			Word[] words = target.getWords();
			for (Word w : words)
				hyponyms.add(w.getLemma());
		}
		pointerArr = syn.getPointers(PointerType.PART_MERONYM);
		for (Pointer x : pointerArr) {
			Synset target = x.getTargetSynset();
			Word[] words = target.getWords();
			for (Word w : words)
				hyponyms.add(w.getLemma());
		}
		pointerArr = syn.getPointers(PointerType.VERB_GROUP);
		for (Pointer x : pointerArr) {
			Synset target = x.getTargetSynset();
			Word[] words = target.getWords();
			for (Word w : words)
				hyponyms.add(w.getLemma());
		}
		pointerArr = syn.getPointers(PointerType.USAGE_MEMBER);
		for (Pointer x : pointerArr) {
			Synset target = x.getTargetSynset();
			Word[] words = target.getWords();
			for (Word w : words)
				hyponyms.add(w.getLemma());
		}
		pointerArr = syn.getPointers(PointerType.USAGE);
		for (Pointer x : pointerArr) {
			Synset target = x.getTargetSynset();
			Word[] words = target.getWords();
			for (Word w : words)
				hyponyms.add(w.getLemma());
		}
		pointerArr = syn.getPointers(PointerType.SUBSTANCE_MERONYM);
		for (Pointer x : pointerArr) {
			Synset target = x.getTargetSynset();
			Word[] words = target.getWords();
			for (Word w : words)
				hyponyms.add(w.getLemma());
		}
		pointerArr = syn.getPointers(PointerType.SUBSTANCE_HOLONYM);
		for (Pointer x : pointerArr) {
			Synset target = x.getTargetSynset();
			Word[] words = target.getWords();
			for (Word w : words)
				hyponyms.add(w.getLemma());
		}

		System.out.println(hyponyms);
		return hyponyms;
	}
	
	public static ArrayList<String> get_hypernyms(String word) 
	  throws Exception {
		ArrayList<String> hypernyms = new ArrayList<String>();
		IndexWord indexWord = dict.getIndexWord(POS.NOUN, word);
		if (indexWord == null)
			return hypernyms;
		Synset set = indexWord.getSense(1);
		Pointer[] pointerArr = set.getPointers(PointerType.HYPERNYM);
		for (Pointer x : pointerArr) {
			Synset target = x.getTargetSynset();
			hypernyms.add(target.getWord(0).getLemma());
		}
		System.out.println(hypernyms);
		return hypernyms;
	}

	public static String lemmaAnalyze(String s)
	throws Exception {
		StringBuffer r= new StringBuffer("");
		TreeSet<String> lemmas = new TreeSet<String>();
		String[] sentences = s.split("\r\n|\n");
		String lemma;
		for (String sent:sentences){
			if (sent.trim().length()<3)
				continue;
			String[] tokens=sent.split(" |\\.|,|\\?|��|!|\"|'s |��|��|��|��|\\(|\\)|��|��|\\[|\\]");
			for (String token:tokens){
				if (token.length()<3||token.length()>50)
					continue;
				lemma = getLemma (token);
				lemmas.add(lemma);
			}
		}
		Iterator<String> iter=lemmas.iterator();
		while(iter.hasNext()){
			String morphy = iter.next()+" ";
			r.append(morphy);
		}
		return r.toString();
	}

	public static String getLemma(String s)
	{
		String r = s;
		String lemma_noun="",lemma_verb="";
		IndexWord lemma;
		int numNoun=0,numVerb=0;
		//DefaultMorphologicalProcessor morphy = new DefaultMorphologicalProcessor();
		//lemma = morphy.lookupBaseForm(POS.NOUN, s);
		try {
		lemma = dict.lookupIndexWord(POS.NOUN, s);
		if (lemma!=null){
			numNoun=lemma.getSenseCount();
			lemma_noun=lemma.getLemma();
		}
		lemma = dict.lookupIndexWord(POS.VERB, s);
		if (lemma != null) {
			numVerb = lemma.getSenseCount();
			lemma_verb = lemma.getLemma();
		}
		if (numNoun>0||numVerb>0){
			r = numNoun>=numVerb?lemma_noun:lemma_verb;
		}
		}catch(Exception e) {
			Log.debug(e);
		}
		return r;
	}
		
	public static void main(String[] args)
	throws Exception {
		//System.out.println(lemmaAnalyze("They aren't children women me.Tom's baof off-season,Your works very hard.Your exam is fine"));
		System.out.println("That->"+getLemma("women"));
		Log.d(get_hyponyms("leg"));
		/*IndexWord word = dict.lookupIndexWord(POS.NOUN, "apple");
		System.out.println(word.getSenseCount());
		Synset syn = word.getSense(2);
		get_relevant(syn);
		get_hyponyms("plant");*/
	}
	
	static {
	  try
	  {
		 JWNL.initialize(new FileInputStream("lib\\jwnl14-rc2\\config\\file_properties.xml"));
		 dict = Dictionary.getInstance();
		 pu = PointerUtils.getInstance();
	  }
	  catch(Exception e)
	  {
		  e.printStackTrace();
	  }
	}

}
