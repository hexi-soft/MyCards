package hexi.nlp;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import org.neo4j.driver.v1.StatementResult;

import common.Log;
import hexi.dbc.Neo4jDR;

public class Sentence2graph {
	
	static Neo4jDR sNeo = Neo4jDR.getInstance();
	static String sUID = "a";
	static ArrayList<Object[]> sTriples = new ArrayList<Object[]>();
	
	public Sentence2graph(String uid) {
		sNeo = Neo4jDR.getInstance();
		sUID = uid;
	}
	
	public static String get_entity_id(Map<String,Object> e) {
		String id = "";
		String name = (String)e.get("name");
		String label = (String)e.get("label");
		if (name.contentEquals("I")) {
			id = sUID;
		}
		Log.d(name+":"+label+":"+id);
		return id;
	}
	
	public void insert_sentence(String sent) {
		String tokenized_sent = NLPHelper.tokenize_sentence(sent);
		String[] tokens = tokenized_sent.split("\\s+");
		if (tokenized_sent.isEmpty() || tokens.length == 0) {
			return;
		}
		String first_token = tokens[0];
		sNeo.execute_cypher("MERGE (h:Head{token:$t}) ON CREATE SET h.occurs=0", "t", first_token);
		String label = NLPHelper.get_token_label(first_token);
		String cypher = "MATCH (h:Head{token:$0}) SET h.occurs=h.occurs+1,h:"+label;
		sNeo.execute_cypher(cypher, "0", first_token);
		String token1 = first_token;
		for(int i=1; i<tokens.length; ++i) {
			//cypher = "MERGE ("
		}

	}
	
	static String retrieve_attrs(Map<String,Object> e) {
		String r = "";
		Set<String> keys = e.keySet();
		for(String key : keys) {
			String value = (String)e.get(key);
			if (!key.contentEquals("name") && !key.contentEquals("label")) {
				r += ","+key+":'"+value+"'";
			}
		}
		return r;
	}
	
	static String attributes(Map<String,Object> e) {
		String r = "[";
		Set<String> keys = e.keySet();
		boolean has_attributes = false;
		for(String key : keys) {
			String value = (String)e.get(key);
			if (!key.contentEquals("name") && !key.contentEquals("label")) {
				r += "'"+value+"',";
				has_attributes = true;
			}
		}
		if (has_attributes) {
			r = r.substring(0,r.length()-1);
		}
		r += "]";
		return r;
	}
	
	static String attribute(Map<String,Object> e) {
		String r = "";
		String name = (String)e.get("name");
		if (name != null) {
			r = "'"+name+"'";
		}
		return r;
	}
	
	static void merge_event(Map<String,Object> obj1, Map<String,Object> obj2, Map<String,Object> rel ) {
		String label1 = (String)obj1.get("label");
		String name1 = (String)obj1.get("name");
		Log.d(name1+":"+label1);
		String label2 = (String)obj2.get("label");
		String name2 = (String)obj2.get("name");
		Log.d(name2+":"+label2);
		String label_rel = (String)rel.get("label");
		String name_rel = (String)rel.get("name");
		Log.d(name_rel+":"+label_rel);
		if (NLPHelper.is_entity_tag(label1) && NLPHelper.is_entity_tag(label2)) {
			String id1 = get_entity_id(obj1);
			String id2 = get_entity_id(obj2);
			String cypher = "MERGE(a:" + label1 + "{name:$1" + retrieve_attrs(obj1) + "})" + "-[r:" + label_rel
					+ "{name:$3}]-(b:" + label2 + "{name:$2" + retrieve_attrs(obj2) + "});";
			if (!id1.isEmpty()) {
				cypher = "MERGE(a:" + label1 + "{id:$0,name:$1" + retrieve_attrs(obj1) + "})" + "-[r:" + label_rel
						+ "{name:$3}]-(b:" + label2 + "{name:$2" + retrieve_attrs(obj2) + "});";
			}
			Log.d(cypher);
			StatementResult result = sNeo.execute_cypher(cypher, "0", id1,"1", name1, "2", name2, "3", name_rel);
		}else if (NLPHelper.is_entity_tag(label1) && NLPHelper.is_attribute_tag(label2)) {
			String id = get_entity_id(obj1);
			String cypher = "MERGE(a:" + label1 + "{name:$1" + retrieve_attrs(obj1)+",`"+label2 + "`:$2});";
			if (!id.isEmpty()) {
				String prop_value = attribute(obj2);
				if (!prop_value.isEmpty()) {
					cypher = "MERGE(n:" + label1 + "{id:$0,name:$1})"
							+" ON CREATE SET n.JJ=["+prop_value+"]"
							+" WITH n WHERE NOT ("+prop_value+" in n.JJ)"
							+" SET n.JJ = n.JJ + ["+prop_value+"]"; 
					Log.d(cypher);
					StatementResult result = sNeo.execute_cypher(cypher, "0", id, "1", name1, "2", name2, "3", name_rel);
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	public static boolean collect_event(String sent) {
		boolean r = false;
		Object[] triple = Extractor.getTriple(sent);
		if (triple[0] != null && triple[1] != null && triple[2] != null) {
			Map<String, Object> e1 = (Map<String, Object>) triple[0];
			Map<String, Object> e2 = (Map<String, Object>) triple[1];
			Map<String, Object> rel = (Map<String, Object>) triple[2];
			merge_event(e1, e2, rel);
			/*
			 * for(Object o : triple) { Map<String,Object> e = (Map<String,Object>)o;
			 * Log.print_map(e); }
			 */
		}
		return r;
	}
	
	public static void main(String[] args) throws Exception{
		String s = "My brother is tall.";
		s = "She is from China.";
		//s = "I'm Patrick.";
		//s = "I'm very nice.";
		collect_event(s);
		sNeo.close();
		Log.d("done!");
	}

}
