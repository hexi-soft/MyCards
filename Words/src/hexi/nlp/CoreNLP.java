package hexi.nlp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import common.Log;
import edu.stanford.nlp.ie.util.RelationTriple;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.naturalli.NaturalLogicAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.pipeline.StanfordCoreNLPClient;
import edu.stanford.nlp.simple.Document;
import edu.stanford.nlp.simple.Sentence;
import edu.stanford.nlp.util.CoreMap;

public class CoreNLP {

	StanfordCoreNLPClient pipeline;
	Annotation doc;
	
	public CoreNLP() {
		Properties props = new Properties();
		props.setProperty("annotators", "tokenize,ssplit,pos,lemma,depparse,natlog,openie");
		pipeline = new StanfordCoreNLPClient(props, "http://192.168.1.112", 9000, 2);
	}

	public void annotate(String words) {
		doc = new Annotation(words);
		pipeline.annotate(doc);
	}

	public List<Collection<RelationTriple>> triples() {
		List<Collection<RelationTriple>> ret = new ArrayList<Collection<RelationTriple>>();
		// Loop over sentences in the document
		for (CoreMap sentence : doc.get(CoreAnnotations.SentencesAnnotation.class)) {
			// Get the OpenIE triples for the sentence
			Collection<RelationTriple> triples = sentence.get(NaturalLogicAnnotations.RelationTriplesAnnotation.class);
			ret.add(triples);
			// Print the triples
			for (RelationTriple triple : triples) {
				/*
				Log.l("confidence: "+triple.confidence);
				Log.l(triple.subjectLemmaGloss() + "|"+ triple.relationLemmaGloss() + "|" + triple.objectLemmaGloss());
				Log.l(triple.canonicalObject);
				Log.l(triple.canonicalSubject);
				Log.l(triple.object);
				Log.l(triple.relation);
				Log.l(triple.subject);
				Log.l(triple.allTokens());
				Log.l(triple.asDependencyTree());
				Log.l(triple.asSentence());
				Log.l(triple.objectHead());
				Log.l(triple.objectLemmaGloss());
				Log.l(triple.relationHead());
				Log.l(triple.objectLink());
				Log.l(triple.toString());*/
			}
		}
		return ret;
	}
	
	public List<RelationTriple> getRelationTriples(String words){
		List<RelationTriple> triples = new ArrayList<RelationTriple>();
		Document doc = new Document(words);
		for (Sentence sent : doc.sentences()) {
			for (RelationTriple triple : sent.openieTriples()) {
				triples.add(triple);
			}
		}
		return triples;
	}
	
	public void testGetRelationTriples(String words) {
		List<RelationTriple> triples = getRelationTriples(words);
		for(RelationTriple triple : triples) {
			Log.l(triple.confidence + "\t" + triple.subjectLemmaGloss() + "|"
					+ triple.relationLemmaGloss() + "|" + triple.objectLemmaGloss());					
			Log.l("canonicalObject:"+triple.canonicalObject);
			Log.l("canonicalSubject:"+triple.canonicalSubject);
			for(CoreLabel label : triple.object) {
				Log.l("label:"+label.word()+"|"+label.tag());
			}
			Log.l("relation:"+triple.relation);
			Log.l("subject:"+triple.subject);
			Log.l("allTokens:"+triple.allTokens());
			Log.l("asDependencyTree:"+triple.asDependencyTree());
			Log.l("asSentence:"+triple.asSentence());
			Log.l("objectHead:"+triple.objectHead());
			Log.l("objectLemmaGloss:"+triple.objectLemmaGloss());
			Log.l("relationHead:"+triple.relationHead());
			Log.l("objectLink:"+triple.objectLink());
			Log.l("toString:"+triple.toString());
			
		}
	}
	
	public static void main(String[] args) {
		CoreNLP nlp = new CoreNLP();
		/*
		String words = "Patrick wants ten big red apples.";
		//nlp.testGetRelationTriples(words);
		nlp.annotate(words);
		nlp.triples();
		words = "Ten big bananas are up there on the wall..";
		nlp.annotate(words);
		nlp.triples();
		//testGetRelationTriples("Patrick wants ten big red apples.");*/
		Log.l("OK");

	}

}
