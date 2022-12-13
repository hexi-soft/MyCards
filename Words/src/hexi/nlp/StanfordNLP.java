package hexi.nlp;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import common.Log;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.SentenceUtils;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import edu.stanford.nlp.util.logging.Redwood;

public class StanfordNLP {

	/** A logger for this class */
	
	private static Redwood.RedwoodChannels log = Redwood
			.channels(StanfordNLP.class);
	MaxentTagger m_tagger;
	
	public StanfordNLP(String modelFilePath) {
		Log.d("stanfordnlp is starting...");
		m_tagger	 = new MaxentTagger(modelFilePath);
		Log.d("stanfordnlp is started");
	}

	public ArrayList<String> tag(String tagFilePath) {
		ArrayList<String> s_r = new ArrayList<String>();
		try {
			List<List<HasWord>> sentences = MaxentTagger
					.tokenizeText(new BufferedReader(
							new FileReader(tagFilePath)));
			for (List<HasWord> sentence : sentences) {
				List<TaggedWord> tSentence = m_tagger.tagSentence(sentence);
				s_r.add(SentenceUtils.listToString(tSentence, false));
			}
		} catch (IOException ioe) {
			Log.d(ioe.getMessage());
		}
		return s_r;
	}

	public static void main(String[] args) {
		StanfordNLP nlp = new StanfordNLP("english-bidirectional-distsim.tagger");
		ArrayList<String> tagged_sents = nlp.tag("test.txt");
		String grammer = "([A-Za-z]+/DT\\s)?([A-Za-z]+/JJ\\s)*([A-Za-z]+)/NNP?";
		Pattern p = Pattern.compile(grammer);
		for(String ts : tagged_sents) {
			Matcher m = p.matcher(ts);
			while(m.find()) {
				Log.d("n: "+m.group(1));
			}
			Log.d(ts);
		}
	}
}
