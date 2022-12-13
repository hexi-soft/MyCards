package hexi.dbc;

import static org.neo4j.driver.v1.Values.parameters;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Transaction;
import org.neo4j.driver.v1.TransactionWork;

import common.Log;

public class Neo4jDR implements AutoCloseable
{
    private final Driver driver;

    static Neo4jDR sNeo = null;
    
    public static Neo4jDR getInstance() {
    	if (sNeo == null) {
    		//sNeo = new Neo4jDR("bolt://localhost:7687", "neo4j", "hexi521" );
    		sNeo = new Neo4jDR("bolt://192.168.1.112:7687", "neo4j", "hexi521" );
    	}
    	return sNeo;
    }
    
    private Neo4jDR(String uri, String user, String password )
    {
        driver = GraphDatabase.driver( uri, AuthTokens.basic( user, password ) );
    }

    @Override
    public void close() throws Exception
    {
        driver.close();
    }

    public StatementResult execute_cypher(final String cypher) {
    	StatementResult result = null;
    	try(Session session = driver.session()){
    		try(Transaction tx = session.beginTransaction()){
    			result = tx.run(cypher);
    			tx.success();
    		}
    	}catch(Exception e) {
    		Log.debug(e);
    	}
    	return result;
    }
    
    public StatementResult execute_cypher(final String cypher, Object... params) {
    	StatementResult result = null;
    	try(Session session = driver.session()){
    		try(Transaction tx = session.beginTransaction()){
    			if (params == null) {
    				result = tx.run(cypher);
    			}else {
    				result = tx.run(cypher, parameters(params));
    			}
    			tx.success();
    		}
    	}catch(Exception e) {
    		Log.debug(e);
    		result = null;
    	}
    	return result;
    }
    
    public ArrayList<Object> execute_cypher_get_objects(String cypher, String column){
    	ArrayList<Object> rs = new ArrayList<Object>();
    	StatementResult r = execute_cypher(cypher);
    	if (r != null) {
    		while(r.hasNext()) {
    			Record rec = r.next();
    			rs.add(rec.get(column).asObject());
    		}
    	}
    	return rs;
    }
    
    public void printGreeting( final String message )
    {
        try ( Session session = driver.session() )
        {
            String greeting = session.writeTransaction( new TransactionWork<String>()
            {
                @Override
                public String execute( Transaction tx )
                {
                    StatementResult result = tx.run( "CREATE (a:Test) " +
                                                     "SET a.e = $e " +
                                                     "RETURN a.e + ', from node ' + id(a)",
                           parameters( "e", message ) );
                    return result.single().get( 0 ).asString();
                }
            } );
            System.out.println( greeting );
        }
    }
    
    public void createNode(String label, String prop_key, String prop_value) {
    	UUID uuid = UUID.randomUUID();
    	String cypher = "CREATE (n:"+label+"{id:{id},"+prop_key+":{a}})";
    	execute_cypher(cypher, "id", uuid.toString(), "a", prop_value);
    	   	
    }
    
    public void createNode(String label, String prop_key1, String prop_value1, String prop_key2,String prop_value2) {
    	String cypher = "CREATE (n:"+label+"{"+prop_key1+":{a},"+prop_key2+":{b}})";
    	execute_cypher(cypher, "a", prop_value1, "b", prop_value2);    	   	
    }
    
    public void createRelation( final String node1_id, final String node2_id, final String r_type )
    {
        try ( Session session = driver.session() )
        {
            String created = session.writeTransaction( new TransactionWork<String>()
            {
                @Override
                public String execute( Transaction tx )
                {
                	long time = new Date().getTime();
                    StatementResult result = tx.run( "MATCH (a),(b)"
                    		+ " WHERE a.id=$id1 and b.id=$id2"
                    		+ " MERGE (a)-[r:"+r_type+"{created:"+time+"}]->(b)"
                            + " RETURN type(r)",
                           parameters( "id1", node1_id,"id2",node2_id ) );
                    return result.single().get( 0 ).asString();
                }
            } );
            System.out.println( created );
        }
    }
    
    public static void test() throws Exception {
    	Neo4jDR neo = Neo4jDR.getInstance();
    	/*
    	String cypher = "match (u:users{uid:'a'})--(w:BookWord) return w.English";
    	ArrayList<Object> r = neo.execute_cypher_get_objects(cypher,"w.English");
    	for(Object o : r) {
    		String s = (String)o;
    		Log.d(s);
    	}*/
    	//neo.createNode("Test", "a", "��");
    	neo.createRelation("fa4047c5-f85b-4bfd-b705-75055bed9621", "af7280e3-fd65-4d9b-94e4-6ed41b81cd5b", "r");
    	neo.close();
    
    }
    
    public static void test2() throws Exception {
    	Neo4jDR neo = Neo4jDR.getInstance();
    	String sql = "select * from fruit";
    	ResultSet rs = Jdbc.query(sql);
    	while(rs.next()) {
    		String title = rs.getString("title");
    		String image = rs.getString("image");
    		neo.createNode("Fruit", "title", title, "image", image);
    	}
    	
    	neo.close();
    
    }
    
    public static void testColor() throws Exception {
    	Neo4jDR neo = Neo4jDR.getInstance();
    	String sql = "select * from word_category where category='color'";
    	ResultSet rs = Jdbc.query(sql);
    	while(rs.next()) {
    		String name = rs.getString("word");
    		neo.createNode("Color","name", name, "color", name);
    	}
    	
    	neo.close();
    
    }
    
    public static void main( String... args ) throws Exception{
    	testColor();
    }
    
    public static void tmain( String... args ) throws Exception
    {
        try ( Neo4jDR neo = Neo4jDR.getInstance() )
        {
          //neo.printGreeting( "you" );
//        	greeter.createRelation("Ice","clock","Don't understand");
  //      	greeter.createRelation("Ice","hungry","Don't understand");
        	
        	String cypher = "match({token:{a}})-[:NEXT]->(n) return n.token as token, n.occurs as occurs";
        	//neo.execute_cypher(cypher, "a","I'm");
        	
        	StatementResult r = neo.execute_cypher(cypher, "a","I'm");
        	if (r != null) {
        		while(r.hasNext()) {
        			Record record = r.next();
        			Log.d(String.format("%s %d",  record.get("token").asString(), record.get("occurs").asInt()));
        		}
        	}else {
        		Log.d(cypher);
        	}
        }
    }
}