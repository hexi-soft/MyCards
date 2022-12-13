package hexi.dbc;

import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Transaction;
import org.neo4j.driver.v1.TransactionWork;
import static org.neo4j.driver.v1.Values.parameters;

public class neo4j implements AutoCloseable
{
    private final Driver driver;

    public neo4j(String uri, String user, String password )
    {
        driver = GraphDatabase.driver( uri, AuthTokens.basic( user, password ) );
    }

    @Override
    public void close() throws Exception
    {
        driver.close();
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
                    StatementResult result = tx.run( "CREATE (a:Word) " +
                                                     "SET a.e = $e " +
                                                     "RETURN a.e + ', from node ' + id(a)",
                           parameters( "e", message ) );
                    return result.single().get( 0 ).asString();
                }
            } );
            System.out.println( greeting );
        }
    }
    
    public void createRelation( final String node1, final String node2, final String r_type )
    {
        try ( Session session = driver.session() )
        {
            String greeting = session.writeTransaction( new TransactionWork<String>()
            {
                @Override
                public String execute( Transaction tx )
                {
                    StatementResult result = tx.run( "MATCH(a:student),(w:Word)"
                    		+ " WHERE a.name=$name and w.e=$word"
                    		+ " CREATE (a)-[r:do_not_understand]->(w)"
                            + " RETURN type(r)",
                           parameters( "name", node1,"word",node2/*,"r",r_type*/ ) );
                    return result.single().get( 0 ).asString();
                }
            } );
            System.out.println( greeting );
        }
    }
    
    public static void main( String... args ) throws Exception
    {
        try ( neo4j greeter = new neo4j( "bolt://localhost:7687", "neo4j", "hexi521" ) )
        {
          greeter.printGreeting( "every" );
          greeter.printGreeting( "I" );
          greeter.printGreeting( "he" );
          greeter.printGreeting( "she" );
          greeter.printGreeting( "us" );
          greeter.printGreeting( "them" );
          greeter.printGreeting( "it" );
          greeter.printGreeting( "everything" );
          greeter.printGreeting( "everyone" );
          greeter.printGreeting( "am" );
          greeter.printGreeting( "is" );
          greeter.printGreeting( "we" );
          greeter.printGreeting( "you" );
//        	greeter.createRelation("Ice","clock","Don't understand");
  //      	greeter.createRelation("Ice","hungry","Don't understand");
        }
    }
}