package hexi.nlp.test;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.StatementResult;

import common.Log;
import edu.stanford.nlp.ie.util.RelationTriple;
import edu.stanford.nlp.ling.CoreLabel;
import hexi.dbc.Neo4jDR;

public class Executor {
	
	Neo4jDR sNeo4j = Neo4jDR.getInstance();
	int userId = 0;
	public  String handleSelfIntro(String name) {
		String res = "Your name is "+name+", ";
		String cypher = "MATCH (n{name:{name}}) return n.id";
		StatementResult r = sNeo4j.execute_cypher(cypher, "name", name);
		if (r != null && r.hasNext()) {
			Record record = r.next();
			if(record != null) {
				userId = record.get(0).asInt();
				res += " and you are number "+userId+".";
			}
			else {
				res += " but we didn't find your number in our system. Would you like to sign up? ";
			}
		}else {
			res += " but we didn't find your number in our system. Would you like to sign up? ";
		}
		return res;
	}
	
	public  String reapTagString(List<CoreLabel> labels) {
		String r = "";
		for(CoreLabel label : labels) {
			r += label.tag()+"_";
		}
		if (r.length()>0) {
			r = r.substring(0, r.length()-1);
		}
		return r;
	}
	
	public  String handleUserFact(RelationTriple triple) {
		String res = "";
		String o = reapTagString(triple.object);
		Log.l("o:"+o);
		String object = triple.objectLemmaGloss();
		//sNeo4j.execute_cypher("MERGE(n{name:{name}})","name", o);
		String cypher = "MERGE(n{name:'"+object+"'})";
		Log.l(cypher);
		sNeo4j.execute_cypher(cypher);
		Log.l("object:"+object);
		String rel = reapTagString(triple.relation);
		Log.l("rel:"+rel);
		String relation = triple.relationLemmaGloss().replaceAll("\\s+", "_");
		Pattern pattern = Pattern.compile(RegexGrammar.JJ);
		Matcher matcher = pattern.matcher(o);
		if (matcher.matches()) {
			cypher = "MATCH (n{id:"+userId+"}) set n.status='"+triple.objectGloss()+"'";
			Log.l(cypher);
			StatementResult r = sNeo4j.execute_cypher(cypher);
			if (r!=null) {
				res = "OK. ";
			}
		}else {
			pattern = Pattern.compile(RegexGrammar.NP);
			matcher = pattern.matcher(o);
			if (matcher.matches()) {
				cypher = "MATCH (n{id:"+userId+"}),(o{name:{title}}) MERGE (n)-[:"+relation+"]->(o)";
				Log.l(cypher);
				StatementResult r = sNeo4j.execute_cypher(cypher, "title", object);
				if (r != null) {
					res = "Good. ";
				}
			}
		}
		return res;
	}
	
	public  String handleFact(RelationTriple triple) {
		String r = "Okay!";
		String sub = triple.subjectLemmaGloss();
		String rel = triple.relationLemmaGloss().replaceAll("\\s", "_");
		String obj = triple.objectLemmaGloss();
		Log.l(sub+"-["+rel+"]->"+obj);
		String cypher = "MERGE(n{name:'"+sub+"'})";
		sNeo4j.execute_cypher(cypher);
		cypher = "MERGE(n{name:'"+obj+"'})";
		sNeo4j.execute_cypher(cypher);
		cypher = "MATCH(m{name:{name1}}),(n{name:{name2}}) MERGE (m)-[:"+rel+"]->(n)";
		Log.l(cypher);
		StatementResult res = sNeo4j.execute_cypher(cypher, "name1", sub, "name2", obj);
		if (res != null) {
			r += " I've written it down. ";
		}
		return r;
	}
	
	public static void main(String[] args) {
		

	}

}
