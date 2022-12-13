package hexi.nlp.test;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import common.Log;
import edu.stanford.nlp.dcoref.CorefChain;
import edu.stanford.nlp.dcoref.CorefCoreAnnotations.CorefChainAnnotation;
import edu.stanford.nlp.ie.util.RelationTriple;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.naturalli.NaturalLogicAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation;
import edu.stanford.nlp.simple.Document;
import edu.stanford.nlp.simple.Sentence;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations.TreeAnnotation;
import edu.stanford.nlp.util.CoreMap;

public class TestNLP {
	
	public static void testAnnotationTriple() throws Exception {
		// Create the Stanford CoreNLP pipeline
		Properties props = new Properties();
		props.setProperty("annotators", "tokenize,ssplit,pos,lemma,depparse,natlog,openie");
		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

		// Annotate an example document.
		Annotation doc = new Annotation("My name is Patrick.");
		pipeline.annotate(doc);

		// Loop over sentences in the document
		for (CoreMap sentence : doc.get(CoreAnnotations.SentencesAnnotation.class)) {
			// Get the OpenIE triples for the sentence
			Collection<RelationTriple> triples = sentence.get(NaturalLogicAnnotations.RelationTriplesAnnotation.class);
			// Print the triples
			for (RelationTriple triple : triples) {
				System.out.println(triple.confidence + "\t" + triple.subjectLemmaGloss() + "\t"
						+ triple.relationLemmaGloss() + "\t" + triple.objectLemmaGloss());
				
				Log.d(triple.canonicalObject);
				Log.d(triple.canonicalSubject);
				Log.d(triple.object);
				Log.d(triple.relation);
				Log.d(triple.subject);
				Log.d(triple.allTokens());
				Log.d(triple.asDependencyTree());
				Log.d(triple.asSentence());
				Log.d(triple.objectHead());
				Log.d(triple.objectLemmaGloss());
				Log.d(triple.relationHead());
				Log.d(triple.objectLink());
				Log.d(triple.toString());
			}
		}
	}

	/**
	 * A demo illustrating how to call the OpenIE system programmatically.
	 */
	public static void testDocumentTriple() throws Exception {
		// Create a CoreNLP document
		Document doc = new Document("My name is Alice. I want to travel around the world. I'm a nice girl of eleven years old."
									+" I have a good mother. She is an English teacher."
									+" She bought many books for me.");

		// Iterate over the sentences in the document
		for (Sentence sent : doc.sentences()) {
			// Iterate over the triples in the sentence
			for (RelationTriple triple : sent.openieTriples()) {
				// Print the triple
				System.out.println(triple.confidence + "\t" + triple.subjectLemmaGloss() + "\t"
						+ triple.relationLemmaGloss() + "\t" + triple.objectLemmaGloss());
				
				Log.d(triple.canonicalObject);
				Log.d(triple.canonicalSubject);
				Log.d(triple.object);
				Log.d(triple.relation);
				Log.d(triple.subject);
				Log.d(triple.allTokens());
				Log.d(triple.asDependencyTree());
				Log.d(triple.asSentence());
				Log.d(triple.objectHead());
				Log.d(triple.objectLemmaGloss());
				Log.d(triple.relationHead());
				Log.d(triple.objectLink());
				Log.d(triple.toString());
			}
		}
	}

	public static void testDcoref() {
		// creates a StanfordCoreNLP object, with POS tagging, lemmatization, NER,
		// parsing, and coreference resolution
		Properties props = new Properties();
		props.put("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref");
		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
		// read some text in the text variable
		String text = "Mrs. Clinton previously worked for Mr. Obama, but she is now distancing herself from him! I love the United States. Do you love me? Oh, yes.";
		// create an empty Annotation just with the given text
		Annotation document = new Annotation(text);
		// run all Annotators on this text
		pipeline.annotate(document);
		// these are all the sentences in this document
		// a CoreMap is essentially a Map that uses class objects as keys and has values
		// with custom types
		List<CoreMap> sentences = document.get(SentencesAnnotation.class);
		System.out.println("sentences: "+sentences.size());
		System.out.println("word\t pos\t lemma\t ner");
		for (CoreMap sentence : sentences) {
			// traversing the words in the current sentence
			// a CoreLabel is a CoreMap with additional token-specific methods
			for (CoreLabel token : sentence.get(TokensAnnotation.class)) {
				// this is the text of the token
				String word = token.get(TextAnnotation.class);
				// this is the POS tag of the token
				String pos = token.get(PartOfSpeechAnnotation.class);
				// this is the NER label of the token
				//String ne = token.get(NamedEntityTagAnnotation.class);
				String lemma = token.get(LemmaAnnotation.class);
				//System.out.println(word + "\t" + pos + "\t" + lemma + "\t" + ne);
				System.out.println(word + "\t" + pos + "\t" + lemma + "\t");
			}
			// this is the parse tree of the current sentence // ���ӵĽ�����
			Tree tree = sentence.get(TreeAnnotation.class);
			System.out.println("\nparse tree:");
			tree.pennPrint(); // this is the Stanford dependency graph of the current sentence 
			SemanticGraph dependencies = sentence.get(CollapsedCCProcessedDependenciesAnnotation.class);
			System.out.println("\ndependencies:");
			System.out.println(dependencies.toString(SemanticGraph.OutputFormat.LIST));
		} 
		// This is the coreference link graph // Each chain stores a set of mentions
		// that link to each other,
		// along with a method for getting the most representative mention
		// Both sentence and token offsets start at 1!
		Map<Integer, CorefChain> graph = document.get(CorefChainAnnotation.class);
		
	}
	
	public static void main(String[] args) throws Exception {
		//testAnnotationTriple();
		//testDocumentTriple();
		//testDcoref();
		Log.l("OK");
	}
}
