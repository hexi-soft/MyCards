package hexi.nlp;

import java.util.TreeMap;
import org.neo4j.graphdb.Node;
import common.Log;
import hexi.dbc.Neo4jDB;

public class Neo4NLP {

	Neo4jDB mNeo;
	
	public void insert_sentence(String sentence) {
		String tokenized_sent = NLPHelper.tokenize_sentence(sentence);
		Log.d("insert \"" + tokenized_sent + "\"");
		String[] tokens = tokenized_sent.split("\\s+");
		if (tokenized_sent.isEmpty() || tokens.length == 0) {
			return;
		}
		String first_token = tokens[0];
		TreeMap<String, Object> properties = new TreeMap<String, Object>();
		properties.put("LABEL", MyLabels.Head);
		properties.put("token", first_token);
		String label = NLPHelper.get_token_label(first_token);
		Node node1 = mNeo.merge_node(label, "token", first_token);
		for (int i = 1; i < tokens.length; ++i) {
			Log.d("insert token: " + tokens[i]);
			label = NLPHelper.get_token_label(tokens[i]);
			Node node = mNeo.merge_node(label, "token", tokens[i]);
			mNeo.merge_relationship(node1, node, RelTypes.NEXT);
			node1 = node;
		}
		// node1.addLabel(MyLabels.Tail);
	}
	
	public Neo4NLP(String neoDatabase) {
		mNeo = Neo4jDB.getInstance();
	}
	
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
