package hexi.user;

import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;

import common.Log;
import hexi.dbc.Neo4jDR;

public class Agent {

	Neo4jDR mNeo;
	String uid;
	
	public Agent(String uid) {
		mNeo = Neo4jDR.getInstance();
		this.uid = uid;
	}
	public void close() {
		try {
			mNeo.close();
		}catch(Exception e){
			
		}
	}
	
	Set<String> get_user_words(){
		Set<String> word_set = new TreeSet<String>();
		String cypher = String.format("match (u:users{uid:'%s'})--(w:BookWord) return w.English as word",uid);
		ArrayList<Object> rs = mNeo.execute_cypher_get_objects(cypher,"word");
		for(Object r : rs) {
			word_set.add((String)r);
		}
		return word_set;
	}
	
	public static void main(String[] args) {
		Agent agent = new Agent("Wang Siyu");
		Set<String> words = agent.get_user_words();
		for(String w :words) {
			Log.d(w);
		}
		agent.close();
	}

}
