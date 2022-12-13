package hexi.nlp;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.neo4j.graphdb.Node;

import common.Json;
import common.Log;
import hexi.nlp.MyLabels;
import hexi.nlp.RelTypes;

import hexi.dbc.Neo4jDB;

public class Sentence2neo4j {

	Neo4jDB mNeo;
	
	public Sentence2neo4j() {
		mNeo = Neo4jDB.getInstance();
	}
	
	Node create_first_token_node(String first_token) {
		Node node1 = mNeo.merge_node("Head", "token", first_token);
		mNeo.addLabel2node(node1, NLPHelper.get_token_label(first_token));
		return node1;
	}
	
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
		Node node1 = mNeo.get_node_by_index("words", properties);
		if (node1 == null) {
			node1 = create_first_token_node(first_token);
			mNeo.print_node(node1);
			mNeo.create_node_index("words", node1);
		} 
		for (int i = 1; i < tokens.length; ++i) {
			Log.d("insert token: " + tokens[i]);
			NodeEvaluator nodeEvaluator = new NodeEvaluator();
			nodeEvaluator.setProp_key("token");
			nodeEvaluator.setProp_value(tokens[i]);
			Node next_match_node = mNeo.find_node_next_match_node(node1, RelTypes.NEXT, nodeEvaluator);
			Log.d("get next match node: " + next_match_node);
			if (next_match_node != null) {
				mNeo.print_node(next_match_node);
				int occurs = (Integer) mNeo.get_node_property(next_match_node, "occurs");
				occurs += 1;
				mNeo.set_node_property(next_match_node, "occurs", occurs);
				node1 = next_match_node;
				continue;
			}
			Node node = mNeo.create_node(NLPHelper.get_token_label(tokens[i]), "token", tokens[i]);
			mNeo.set_node_property(node, "occurs", 1);
			mNeo.create_relationship(node1, node, RelTypes.NEXT);
			mNeo.create_node_index("words", node);
			Log.d("create new node:");
			mNeo.print_node(node);
			node1 = node;
		}
		mNeo.addLabel2node(node1,"Tail");
	}
	
	public Map<String,Object> get_next_words(String word) {
		Log.d("got word: "+word);
		Map<String,Object> params = new HashMap<>();
		params.put("skip",  0*10);
		params.put("limit",  10);
		Map<String,Object> page = mNeo.find_node_next_nodes_sorted("Word","token", word, "NEXT", params);
		return page;
	}
	
	public static void main(String[] args) {
		Sentence2neo4j s2n = new Sentence2neo4j();
		String sent = "I love English.";
		s2n.insert_sentence(sent);
		sent = "I love English.";
		s2n.insert_sentence(sent);
		sent = "I love you.";
		s2n.insert_sentence(sent);
		Log.d(Json.toJson(s2n.get_next_words("love")));
		
	}

}
