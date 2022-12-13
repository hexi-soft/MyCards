package hexi.nlp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import common.Log;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.SentenceUtils;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.process.TokenizerFactory;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;

public class PosTagger {

static MaxentTagger m_tagger;
static final String defaultModel = "english-bidirectional-distsim.tagger"; 

	public PosTagger(String modelFilePath) {
		if (m_tagger == null) {
			if (modelFilePath != null && !modelFilePath.isEmpty()) {
				m_tagger = new MaxentTagger(modelFilePath);
			} else {
				m_tagger = new MaxentTagger(defaultModel);
			}
		}
	}

	public String tag_s(String sent) {
		return m_tagger.tagString(sent);
	}

	public static List<String> split_sents(String text) {
		List<String> sents = new ArrayList<String>();
		BufferedReader r = new BufferedReader(new StringReader(text));
		DocumentPreprocessor documentPreprocessor = new DocumentPreprocessor(r);
		for (List<HasWord> sentence : documentPreprocessor) {
			sents.add(SentenceUtils.listToString(sentence, false));
		}
		return sents;
	}
	
	void print_tagged_words(List<TaggedWord> taggedSent) {
		for(TaggedWord tw : taggedSent) {
			Log.d(tw.word()+"_"+tw.tag());
		}
	}
	
	public List<List<TaggedWord>> tag_d(String text) {
		List<List<TaggedWord>> tagged_sents = new ArrayList<List<TaggedWord>>();		
		TokenizerFactory<CoreLabel> ptbTokenizerFactory = PTBTokenizer.factory(new CoreLabelTokenFactory(),
				"untokenizable=noneKeep");
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

	public List<List<TaggedWord>> tag_d(File file) {
		List<List<TaggedWord>> tagged_sents = new ArrayList<List<TaggedWord>>();
		TokenizerFactory<CoreLabel> ptbTokenizerFactory = PTBTokenizer.factory(new CoreLabelTokenFactory(),
				"untokenizable=noneKeep");
		try {
			BufferedReader r = new BufferedReader(new InputStreamReader(new FileInputStream(file), "utf-8"));
			DocumentPreprocessor documentPreprocessor = new DocumentPreprocessor(r);
			documentPreprocessor.setTokenizerFactory(ptbTokenizerFactory);
			for (List<HasWord> sentence : documentPreprocessor) {
				if (sentence.size()>20) {
					continue;
				}
				List<TaggedWord> tSentence = m_tagger.tagSentence(sentence);
				tagged_sents.add(tSentence);
				// print_tagged_words(tSentence);
				Log.d(SentenceUtils.listToString(tSentence, false));
			}
		} catch (IOException e) {
			Log.d(e);
		}
		return tagged_sents;
	}

	static void test()  throws IOException{
		//PosTagger tagger = new PosTagger(null);
		File file = new File("d:\\r\\ed3.txt");
		InputStream in = new FileInputStream(file);
		InputStreamReader r = new InputStreamReader(in,"GBK");
		String s = m_tagger.tokenizeText(r).toString();
		Log.d(s);
		//tagger.tag_d(file);		
	}
	
	public static void main(String[] args){
		
	}

}
