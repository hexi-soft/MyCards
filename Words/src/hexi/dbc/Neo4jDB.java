package hexi.dbc;

import java.io.File;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Notification;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.ResourceIterator;
import org.neo4j.graphdb.Result;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexHits;
import org.neo4j.graphdb.index.IndexManager;

import common.Json;
import common.Log;
import hexi.nlp.IisMatchNode;
import hexi.nlp.RelTypes;

public class Neo4jDB {

	private static void registerShutdownHook(final GraphDatabaseService graphDb) {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				graphDb.shutdown();
			}
		});
	}
	
	static File DB_PATH = new File("F:\\neo4jcn\\data\\databases\\graph.db");
	static Neo4jDB sDB = null;
	
	GraphDatabaseService db;

	public static Neo4jDB getInstance() {
		if (sDB == null) {
			sDB = new Neo4jDB();
		}
		return sDB;
	}
	
	public static Neo4jDB getInstance(String db_path) {
		if (sDB == null) {
			sDB = new Neo4jDB(db_path);
		}
		return sDB;
	}
	
	private Neo4jDB() {
		Log.debug("Neo4j is starting...");
		/*
		db = new GraphDatabaseFactory()
				.newEmbeddedDatabaseBuilder(DB_PATH)
				.setConfig(GraphDatabaseSettings.pagecache_memory,"245760")
				.setConfig(GraphDatabaseSettings.string_block_size, "60")
				.setConfig(GraphDatabaseSettings.array_block_size, "300")
				.newGraphDatabase();*/

		db = new GraphDatabaseFactory().newEmbeddedDatabase(DB_PATH);
		registerShutdownHook(db);
		Log.debug("Neo4j started successfully!");
	}
	
	private Neo4jDB(String db_path) {
		Log.debug("Neo4j is starting...");
		/*
		db = new GraphDatabaseFactory()
				.newEmbeddedDatabaseBuilder(DB_PATH)
				.setConfig(GraphDatabaseSettings.pagecache_memory,"245760")
				.setConfig(GraphDatabaseSettings.string_block_size, "60")
				.setConfig(GraphDatabaseSettings.array_block_size, "300")
				.newGraphDatabase();*/

		db = new GraphDatabaseFactory().newEmbeddedDatabase(new File(db_path));
		registerShutdownHook(db);
		Log.debug("Neo4j started successfully!");
	}

	
	public void create_node(String sLabel, Map<String,Object> properties) {
		try ( Transaction tx = db.beginTx() ){
			Label label = Label.label(sLabel);
			Node node = db.createNode(label);
			for(String key : properties.keySet()){
				Object value = properties.get(key);
				node.setProperty(key, value);
			}
			tx.success();
		}catch(Exception e) {
			Log.d(e.getMessage());
		}
	}
	
	public Node create_node(String sLabel, String prop_key, Object prop_value) {
		Node node = null;
		try ( Transaction tx = db.beginTx() ){
			Label label = Label.label(sLabel);
			node = db.createNode(label);
			node.setProperty(prop_key, prop_value);
			tx.success();
		}catch(Exception e) {
			Log.d(e.getMessage());
		}
		return node;
	}
	
	public void create_relationship(Node n1, Node n2, RelTypes rel_type) {
		Transaction tx = db.beginTx();
		try{
			n1.createRelationshipTo(n2, rel_type);
			tx.success();
		}catch(Exception e) {
			e.printStackTrace();
			tx.failure();
		}finally {
			tx.close();
		}
	}
	
	public void merge_relationship(Node n1, Node n2, RelTypes rel_type) {
		Transaction tx = db.beginTx();
		try{
			Relationship relationship = n1.getSingleRelationship(rel_type,  Direction.OUTGOING);
			if (relationship == null) {
				relationship = n1.createRelationshipTo(n2, rel_type);
				relationship.setProperty("occurs", 0);
			}
			Integer occurs = (Integer)relationship.getProperty("occurs")+1;
			relationship.setProperty("occurs", occurs);
			tx.success();
		}catch(Exception e) {
			e.printStackTrace();
			tx.failure();
		}finally {
			tx.close();
		}
	}

	public void addLabel2node(Node node, String label) {
		try (Transaction tx = db.beginTx()) {
			node.addLabel(Label.label(label));
			tx.success();
		} catch (Exception e) {
			Log.d(e.getMessage());
		}
	}

	@SuppressWarnings("deprecation")
	public Index<Node> create_node_index(String indexName, Node node) {

		Transaction tx = db.beginTx();
		Index<Node> idx = null;
		try {
			IndexManager index = db.index();
			idx = index.forNodes(indexName);
			Iterator<Label> labels = node.getLabels().iterator();
			while (labels.hasNext()) {
				Label label = labels.next();
				idx.add(node, "LABEL", label);
			}
			Map<String, Object> properties = node.getAllProperties();
			for (String key : properties.keySet()) {
				idx.add(node, key, node.getProperty(key));
			}
			tx.success();
		} catch (Exception e) {
			Log.d(e);
			tx.failure();
		} finally {
			tx.close();
		}
		return idx;
	}
	
	@SuppressWarnings("deprecation")
	public Node get_node_by_index(String indexName, Map<String,Object> properties) {
		Transaction tx = db.beginTx();
		Node node = null;
		try{
			Index<Node> idx = db.index().forNodes(indexName);
			String q = "";
			for(String key : properties.keySet()) {
				q += key+":"+properties.get(key)+" AND ";
			}
			q = q.substring(0, q.length()-5);
			Log.d("q="+q);
			IndexHits<Node> hits = idx.query(q);
			if (hits.hasNext()) {
				node = hits.getSingle();
				Log.d("find head "+q);
			}else {
				Log.d("Sentence head \""+q+"\" not found.");
			}
			tx.success();
		}catch(Exception e) {
			e.printStackTrace();
			tx.failure();
		}finally {
			tx.close();
		}
		return node;
	}
	
	public ArrayList<Node> find_nodes(String label, String prop_key, String prop_value) {
		ArrayList<Node> nodeList = new ArrayList<Node>();
		ResourceIterator<Node> nodes = null;
		try ( Transaction tx = db.beginTx() ){
			nodes = db.findNodes(Label.label(label), prop_key, prop_value);
			if (nodes != null) {
				while(nodes.hasNext()) {
					Node node = nodes.next();
					nodeList.add(node);
					print_node(node);
				}
			}
			tx.success();
		}catch(Exception e) {
			Log.d(e.getMessage());
		}
		return nodeList;
	}
	
	public ArrayList<Node> find_nodes(String label) {
		ArrayList<Node> nodeList = new ArrayList<Node>();
		ResourceIterator<Node> nodes = null;
		try ( Transaction tx = db.beginTx() ){
			nodes = db.findNodes(Label.label(label));
			if (nodes != null) {
				while(nodes.hasNext()) {
					Node node = nodes.next();
					nodeList.add(node);
					print_node(node);
				}
			}
			tx.success();
		}catch(Exception e) {
			Log.d(e.getMessage());
		}
		return nodeList;
	}
	
	public Map<String,Object> get_node_properties(Node n) {
		Transaction tx = db.beginTx();
		Map<String,Object> map = null;
		try{
			map = n.getAllProperties();
			tx.success();
		}catch(Exception e) {
			e.printStackTrace();
			tx.failure();
		}finally {
			tx.close();
		}
		return map;
	}
	
	public Iterator<Label> get_node_labels(Node n) {
		Transaction tx = db.beginTx();
		Iterator<Label> iter = null;
		try{
			iter = n.getLabels().iterator();
			tx.success();
		}catch(Exception e) {
			e.printStackTrace();
			tx.failure();
		}finally {
			tx.close();
		}
		return iter;
	}
	
	public void print_nodes(ResourceIterator<Node> nodes) {
		try(Transaction tx = db.beginTx()){
			if (nodes != null) {
				while(nodes.hasNext()) {
					Node node = nodes.next();
					print_node(node);
				}
			}
			tx.success();
		}
	}
	
	public void print_node(Node n) {
		if (n == null) {
			return;
		}
		long id = n.getId();
		System.out.println("id:"+id);
		Iterator<Label> iter = get_node_labels(n);
		while(iter.hasNext()) {
			Label label = iter.next();
			String name = label.name();
			System.out.println("label: "+name);
		}
		Map<String,Object> properties = get_node_properties(n);
		print_map(properties);
	}
	
	private static void print_map(Map<String,Object> map) {
		System.out.println("properties: "+map.keySet().size());
		for(String key : map.keySet()){
			Object value = map.get(key);
			System.out.println(key+": "+value);
		}
	}
	
	public void test_insert_word(String e, Object c) {
		TreeMap<String,Object> map = new TreeMap<String,Object>();
		map.put("e", e);
		map.put("c", c);
		create_node("Word", map);	
	}
	
	public Node find_node(String label, String prop_key, Object prop_value) {
		Node node = null;
		try ( Transaction tx = db.beginTx() ){
			node = db.findNode(Label.label(label), prop_key, prop_value);
			tx.success();
		}catch(Exception e) {
			Log.d(e.getMessage());
		}
		return node;
	}
	
	public Node find_node(String label, String prop_key1, Object prop_value1, String prop_key2, Object prop_value2) {
		Node node = null;
		try ( Transaction tx = db.beginTx() ){
			Node n = db.findNode(Label.label(label), prop_key1, prop_value1);
			if(n != null && n.getProperty(prop_key2).equals(prop_value2)) {
				node = n;
			}
			tx.success();
		}catch(Exception e) {
			Log.d(e.getMessage());
		}
		return node;
	}
	
	public void func() {
		try ( Transaction tx = db.beginTx() ){
			
			tx.success();
		}catch(Exception e) {
			Log.d(e.getMessage());
		}
	}
	
	public Node merge_node(String sLabel, String prop_key, String prop_value) {
		Node node = null;
		try ( Transaction tx = db.beginTx() ){
			Map<String,Object> params = new HashMap<String,Object>();
			params.put("prop_value",prop_value);
			String query = "MERGE (n:" + sLabel + " {" + prop_key +": $prop_value})"
					+ " ON CREATE SET n.occurs = 0";
			db.execute(query, params);
			query = "MATCH (n:" + sLabel +" {" + prop_key +": $prop_value}) SET n.occurs=n.occurs+1 RETURN n";
			Result result = db.execute(query, params);
			Iterator<Node> n_column = result.columnAs("n");
			node = n_column.next();
			tx.success();
		}catch(Exception e) {
			Log.d("Log: "+e);
		}
		return node;
	}

	public Map<String,Object> findPage(String cypher, Map<String,Object> params){
		Map<String,Object> page = new HashMap<>();
		try ( Transaction tx = db.beginTx() ){
			//String query = "MATCH (n) WHERE n.English =~ $English RETURN n SKIP $skip LIMIT $limit";
			String query = cypher;
			Result result = db.execute(query, params);
			List<String> columns = result.columns();
			Iterator<Node> n_column = result.columnAs("n");
			List<Map<String,Object>> content = new ArrayList<>();
			while(n_column.hasNext()) {
				Node node = n_column.next();
				
				Map<String,Object> data = new HashMap<>();
				data.put("id",  node.getId());
				data.put("English",  node.getProperty("English"));
				data.put("Chinese",  node.getProperty("Chinese"));
				//data.put("append_time", new Date((Long)node.getProperty("append_time")));
				
				content.add(data);;
			}
			
			query = "MATCH (n) WHERE n.English =~ $English RETURN count(n) as count";
			result = db.execute(query, params);
			
			if (result.hasNext()) {
				Map<String,Object> row = result.next();
				page.put("totalElements",  row.get("count"));
			}
			
			page.put("content",  content);
			
			tx.close();
		}
		return page;
	}
	
	public Map<String,Object> run_cypher_get_page(String cypher, Map<String,Object> params){
		Map<String,Object> page = new HashMap<>();
		List<Map<String,Object>> rows = new ArrayList<>();
		String notification = "";
		try ( Transaction tx = db.beginTx() ){
			//String query = "MATCH (n) WHERE n.English =~ $English RETURN n SKIP $skip LIMIT $limit";
			String query = cypher;
			Result result = null;
			if(params!=null) {
				result = db.execute(query, params);
			}else {
				result = db.execute(query);
			}
			while(result.hasNext()) {
				Map<String,Object> row = result.next();	
				Set<String> keys = row.keySet();
				for(String key : keys) {
					Object value = row.get(key);
					if (value instanceof org.neo4j.graphdb.Node) {
						Node n = (Node)value;
						row.put(key, n.getAllProperties());
					}
				}
				rows.add(row);;
			}						
			page.put("rows",  rows);
			if (rows.isEmpty()) {
				notification = result.resultAsString();
			}
			page.put("msgs", notification);
			tx.close();
		}catch(Exception e) {
			Log.d(e);
		}
		return page;
	}
	
	public boolean execute_cypher(String cypher) {
		boolean ret = false;
		try ( Transaction tx = db.beginTx() ){
			db.execute(cypher);
			tx.success();
			ret = true;
		}catch(Exception e) {
			Log.d("Log: "+e);
		}
		return ret;
	}
	
	public boolean execute_cypher(String cypher, Map<String,Object> params) {
		boolean ret = false;
		try ( Transaction tx = db.beginTx() ){
			db.execute(cypher, params);
			tx.success();
			ret = true;
		}catch(Exception e) {
			Log.d("Log: "+e);
		}
		return ret;
	}
	
	public String run_cypher_get_string(String cypher){
		String result = "<NULL>";
		try ( Transaction tx = db.beginTx() ){
			String query = cypher;
			result = db.execute(query).resultAsString();
			tx.close();
		}catch(Exception e) {
			Log.d(e);
		}
		return result;
	}
	
	public String run_cypher_get_result(String cypher){
		Map<String,Object> page = new HashMap<>();
		String r = "<NULL>";
		try ( Transaction tx = db.beginTx() ){
			String query = cypher;
			Result result = db.execute(query);
			r = result.resultAsString();
			Iterator<Notification> notes = result.getNotifications().iterator();
			Log.d(notes.toString());
			while(notes.hasNext()) {
				Notification n = notes.next();
				Log.d(n.getDescription());
				Log.d(n);
			}
			List<Map<String,Object>> content = new ArrayList<>();
			
			tx.close();
		}catch(Exception e) {
			Log.d(e);
		}
		return r;
	}
	
	public List<Map<String,Object>> execute_get_cypher(String match, String return_column){
		Map<String,Object> page = new HashMap<>();
		List<Map<String,Object>> content = new ArrayList<>();
		try ( Transaction tx = db.beginTx() ){
			String query = match;
			Result result = db.execute(query);
			Iterator<Node> n_column = result.columnAs(return_column);
			while(n_column.hasNext()) {
				Node node = n_column.next();
				Map<String,Object> properties = node.getAllProperties();
				Map<String,Object> data = new HashMap<>();
				data.put("id", node.getId());
				for(String key : properties.keySet()) {
					data.put(key,  node.getProperty(key));
				}
				//data.put("append_time", new Date((Long)node.getProperty("append_time")));				
				content.add(data);;
			}
			page.put("content",  content);
			
			tx.close();
		}
		return content;
	}
	
	public Object get_node_property(Node n, String key) {
		Transaction tx = db.beginTx();
		Object p = null;
		try{
			p = n.getProperty(key);
			tx.success();
		}catch(Exception e) {
			e.printStackTrace();
			tx.failure();
		}finally {
			tx.close();
		}
		return p;
	}
	
	public void set_node_property(Node node, String key, Object value) {
		Transaction tx = db.beginTx();
		try{
			node.setProperty(key, value);
			tx.success();
		}catch(Exception e) {
			e.printStackTrace();
			tx.failure();
		}finally {
			tx.close();
		}
	}
	
	public Node find_node_next_match_node(Node n, RelTypes rel_type, IisMatchNode iisMatchNode) {
		Node next_node = null;
		Transaction tx = db.beginTx();
		try {
			if (n.hasRelationship(Direction.OUTGOING, rel_type)) {
				Iterator<Relationship> rs = n.getRelationships(Direction.OUTGOING, rel_type).iterator();
				while (rs.hasNext()) {
					Relationship r = rs.next();
					Node node = r.getEndNode();
					if (iisMatchNode.match(node)) {
						next_node = node;
						break;
					}
				}
			}
		} catch (Exception e) {
			Log.d(e);
			tx.failure();
		} finally {
			tx.close();
		}
		return next_node;
	}
	
	public ArrayList<Node> get_node_next_nodes(Node n, RelTypes rel_type) {
		ArrayList<Node> nodes = new ArrayList<Node>();
		Transaction tx = db.beginTx();
		try {
			if (n.hasRelationship(Direction.OUTGOING, rel_type)) {
				Iterator<Relationship> rs = n.getRelationships(Direction.OUTGOING, rel_type).iterator();
				while (rs.hasNext()) {
					Relationship r = rs.next();
					Node node = r.getEndNode();
					nodes.add(node);
				}
			}
		} catch (Exception e) {
			Log.d(e);
			tx.failure();
		} finally {
			tx.close();
		}
		return nodes;
	}
	
	public Map<String,Object> find_node_next_nodes_sorted(String label, String prop_key, String prop_value, String rel_type, Map<String,Object> params) {
		Map<String,Object> page = new HashMap<>();
		try ( Transaction tx = db.beginTx() ){
			String query = "MATCH (m:"+label+"{"+prop_key+": $prop_value})-[:"+rel_type+"]->(n)"
					+ " RETURN n ORDER BY n.occurs DESC SKIP $skip LIMIT $limit";
			params.put("prop_value", prop_value);
			Result result = db.execute(query, params);
			Iterator<Node> n_column = result.columnAs("n");
			List<Map<String,Object>> content = new ArrayList<>();
			while(n_column.hasNext()) {
				Node node = n_column.next();
				
				Map<String,Object> data = new HashMap<>();
				//data.put("id",  node.getId());
				data.put(prop_key,  node.getProperty(prop_key));
				data.put("occurs",  node.getProperty("occurs"));
				//data.put("append_time", new Date((Long)node.getProperty("append_time")));
				
				content.add(data);;
			}
			page.put("content",  content);
			tx.close();
		}catch(Exception e) {
			Log.debug(e);
		}
		return page;
	}
	
	public static void tmain(String[] args) {
		Neo4jDB neo = Neo4jDB.getInstance();
		//neo.insert_word("a", "һ");
		/*
		neo.merge_node("Word", "English", "\"am\"");
		Node node = neo.find_node("Word", "English", "am");
		neo.print_node(node);
		neo.merge_node("Word", "English", "\"hot\"");
		node = neo.find_node("Word", "English", "hot");
		neo.print_node(node);
		neo.merge_node("Word", "English", "\"hot\"");
		node = neo.find_node("Word", "English", "hot");
		neo.print_node(node);
		*/
		Map<String,Object> params = new HashMap<>();
		//params.put("English",  "(?i)a.*");
		params.put("skip",  0*10);
		params.put("limit",  10);
		Map<String,Object> page = neo.find_node_next_nodes_sorted("Word","token","'love'", "NEXT", params);
		System.out.println("==page list==");
		List<Map<String,Object>> results = (List<Map<String,Object>>)page.get("content");
		String res = Json.toJson(results);
		System.out.println(res);
		for(Map<String,Object> list : (List<Map<String,Object>>)page.get("content")) {
			
			System.out.println("id="+list.get("id")+"; English="+list.get("token"));
		}
		//System.out.println("page total="+page.get("totalElements"));
		
	}
	
	public static void main(String[] args) {
		Neo4jDB db = Neo4jDB.getInstance();
		//String cypher = "match(w:Punc{token:'.'})--(n) with w,count(*) as nofr where nofr<2 detach delete w";//match(u:users{uid:'a'})-[r:KNOWS]->(n) return n,r";
		String cypher = "create constraint on (h:Head) assert h.token is unique";
		String r = Json.toJson(db.run_cypher_get_page(cypher,null));
		Log.d(r);
	}

}
