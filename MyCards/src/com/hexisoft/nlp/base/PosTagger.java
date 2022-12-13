package com.hexisoft.nlp.base;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import android.util.Log;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.process.TokenizerFactory;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;

public class PosTagger {

	static PosTagger sPosTagger = null;
	
	public static PosTagger getInstance(String model){
		if (sPosTagger==null){
			sPosTagger = new PosTagger(model);
		}
		return sPosTagger;
	}
	
	MaxentTagger m_tagger;
	static final String defaultModel = "/storage/emulated/0/my_library/english-bidirectional-distsim.tagger"; 
	
	private PosTagger(String modelFilePath) {
		if (modelFilePath != null && !modelFilePath.isEmpty()) {
			m_tagger	 = new MaxentTagger(modelFilePath);
		}else {
			m_tagger	 = new MaxentTagger(defaultModel);
		}
	}
	
	public List<List<TaggedWord>> tag_d(String text) {
		List<List<TaggedWord>> tagged_sents = new ArrayList<List<TaggedWord>>();		
		TokenizerFactory<CoreLabel> ptbTokenizerFactory = PTBTokenizer.factory(new CoreLabelTokenFactory(), "untokenizable=noneKeep");
		BufferedReader r = new BufferedReader(new StringReader(text));
		DocumentPreprocessor documentPreprocessor = new DocumentPreprocessor(r);
		documentPreprocessor.setTokenizerFactory(ptbTokenizerFactory);
		for (List<HasWord> sentence : documentPreprocessor) {
			List<TaggedWord> tSentence = m_tagger.tagSentence(sentence);
			tagged_sents.add(tSentence);
			//print_tagged_words(tSentence);
			//Log.d(SentenceUtils.listToString(tSentence, false));
		}
		return tagged_sents;
	}

	public String tag_s(String sent) {
		return m_tagger.tagString(sent);
	}
	
	public static void main(String[] args) {
		/*PosTagger tagger = new PosTagger(null);
		String s = tagger.tag_s(null);
		Log.d("tagged sentence: "+s);*/
	}

}
