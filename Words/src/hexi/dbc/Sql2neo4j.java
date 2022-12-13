package hexi.dbc;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.TreeMap;

import org.neo4j.graphdb.GraphDatabaseService;

import common.Log;
import java.util.UUID;

public class Sql2neo4j {

	Neo4jDB neo;
	
	public Sql2neo4j(String neoDatabase) {
		neo = Neo4jDB.getInstance(neoDatabase);
	}
	
	public void drop_table(String sLabel) {
		String cypher = "MATCH (n) REMOVE n:"+sLabel;
		neo.execute_cypher(cypher);
	}
	
	public void insert_table(String tabName) {
		String sql = "select * from " + tabName;
		try{
			ResultSet r = Jdbc.query(sql);
			ResultSetMetaData meta = r.getMetaData();
			int columnCount = meta.getColumnCount();
			int n = 0;
			while(r.next()){
				TreeMap<String,Object> item = new TreeMap<String,Object>();
				UUID uuid = UUID.randomUUID();
				item.put("uuid", uuid.toString());
				for(int i=1; i<=columnCount; ++i){
					String attr = r.getString(i);
					if (attr==null) {
						attr = "null";
					}
					String columnName = meta.getColumnLabel(i);
					//Log.d(columnName+": "+attr);
					item.put(columnName, attr);
				}
				neo.create_node(tabName, item);
				++n;
				Log.d(n);
			}
		}catch(SQLException e){
			e.printStackTrace();
		}
		/*neo.execute_cypher("CREATE CONSTRAINT ON (p:users) ASSERT p.uid IS UNIQUE");
		neo.execute_cypher("CREATE CONSTRAINT ON (p:users) ASSERT p.name IS UNIQUE");
		neo.find_nodes(tabName);*/
	}
	
	public static void main(String[] args) {
		Sql2neo4j sql2neo4j = new Sql2neo4j("f:\\neo4jcn\\data\\databases\\graph.db");
		//sql2neo4j.drop_table("BookWord");
		sql2neo4j.insert_table("Fruit");
	}

}
