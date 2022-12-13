package hexi.nlp.test;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import common.Log;
import common.Pair;
import edu.stanford.nlp.ie.util.RelationTriple;
import edu.stanford.nlp.ling.CoreLabel;
import hexi.nlp.WordsProcessing;

public class IntentClassifier {
	
	static final Pattern selfIntroPattern = Pattern.compile("PRP\\sVBP\\s");
	
	public static String reapTagString(List<CoreLabel> labels) {
		String r = "";
		for(CoreLabel label : labels) {
			r += label.tag()+"_";
		}
		if (r.length()>0) {
			r = r.substring(0, r.length()-1);
		}
		return r;
	}
	
	static Pair<Intent, String> classify(RelationTriple triple) {
		Intent intent = Intent.greet;
		String response = "";
		List<CoreLabel> subject = triple.subject;
		List<CoreLabel> relation = triple.relation;
		List<CoreLabel> object = triple.object;
		String tripleWordsS = triple.subjectLemmaGloss() + "-" + triple.relationLemmaGloss() + "-"
				+ triple.objectLemmaGloss();
		String tripleTagsS = reapTagString(subject) + " " + reapTagString(relation) + " " + reapTagString(object);
		Log.l("tripleWprdsS: " + tripleWordsS);
		Log.l(tripleTagsS);
		Executor executor = MyRobot.executors.get(MyRobot.sCurrentUser);
		if (executor != null) {
			if (tripleWordsS.startsWith("I-") || tripleWordsS.startsWith("My name is")) {
				if (tripleWordsS.startsWith("I-be") && tripleTagsS.startsWith("PRP VBP NNP")) {
					intent = Intent.self_intro;
					response = executor.handleSelfIntro(triple.objectLemmaGloss());
				} else {
					intent = Intent.create_my_fact;
					response = executor.handleUserFact(triple);
				}
			} else {
				intent = Intent.create_fact;
				response = executor.handleFact(triple);
			}
		}
		return new Pair<Intent, String>(intent, response);
	}

	public static Pair<Intent,String> classify(String words) {
		Intent intent = Intent.greet;
		String response = "";
		Map<String, Object> r = WordsProcessing.process(words);
		@SuppressWarnings("unchecked")
		List<Collection<RelationTriple>> triplesList = (List<Collection<RelationTriple>>) r
				.get(WordsProcessing.TRIPLES);
		if (triplesList.size() > 0) {
			Collection<RelationTriple> triples = triplesList.get(0);
			if (triples.size() > 0) {
				RelationTriple triple = triples.iterator().next();
				Pair<Intent,String> res = IntentClassifier.classify(triple);
				intent = res.getFirst();
				response = res.getSecond();
			}else {
				Pattern pattern = Pattern.compile("Hi.*");
				Matcher matcher = pattern.matcher(words);
				if (matcher.matches()) {
					intent = Intent.greet;
				}else {
					pattern = Pattern.compile("\\w*bye\\b.*", Pattern.CASE_INSENSITIVE);
					matcher = pattern.matcher(words);
					if (matcher.matches()) {
						intent = Intent.bye;
					}else {
						intent = Intent.create_fact;
					}
				}		
			}
		}	
		return new Pair<Intent,String>(intent, response);
	}
	
	public static Pair<Intent,String> classify(String uid, String words) {
		Intent intent = Intent.greet;
		String response = "";
		Map<String, Object> r = WordsProcessing.process(words);
		@SuppressWarnings("unchecked")
		List<Collection<RelationTriple>> triplesList = (List<Collection<RelationTriple>>) r
				.get(WordsProcessing.TRIPLES);
		if (triplesList.size() > 0) {
			Collection<RelationTriple> triples = triplesList.get(0);
			if (triples.size() > 0) {
				RelationTriple triple = triples.iterator().next();
				Pair<Intent,String> res = IntentClassifier.classify(triple);
				intent = res.getFirst();
				response = res.getSecond();
			}else {
				Pattern pattern = Pattern.compile("Hi.*");
				Matcher matcher = pattern.matcher(words);
				if (matcher.matches()) {
					intent = Intent.greet;
				}else {
					pattern = Pattern.compile("\\w*bye\\b.*", Pattern.CASE_INSENSITIVE);
					matcher = pattern.matcher(words);
					if (matcher.matches()) {
						intent = Intent.bye;
					}else {
						intent = Intent.create_fact;
					}
				}		
			}
		}	
		return new Pair<Intent,String>(intent, response);
	}
	
	public static Intent classify(List<CoreLabel> subject, List<CoreLabel> relation, List<CoreLabel> object) {
		Intent intent = Intent.create_fact;
		
		return intent;
	}
	
	public static void main(String[] args) {
		

	}

}
