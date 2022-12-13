package hexi.nlp;

import org.neo4j.graphdb.Label;

public enum MyLabels implements Label{
	Word,
	Punc,
	Head,
	Tail,
	NULL
}

