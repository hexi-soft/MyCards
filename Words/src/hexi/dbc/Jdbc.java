package hexi.dbc;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

import common.Log;
import common.UFileReader;
import hexi.nlp.base.NGram;

public class Jdbc {

	static String database = "TXTKBASE";  
	
	static Connection dbConn;
	static Statement stmt;
	public static PreparedStatement pstmt;
	
	static {
		
		try {
			//dbConn = DatabaseConnectionFactory.getMySQLConnection().getConnection();
			dbConn = DatabaseConnectionFactory.getSQL2005Connection().getConnection();
			stmt = dbConn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
			//stmt.execute("create database if not exists "+database); //for mysql
			//stmt.execute("if not exists(select * from sys.databases where name = '"+database+"')"
				//	+ "create database "+database); //for sql server
			stmt.execute("USE " + database);
			Log.debug("DATABASE: "+database);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void prepare_sql(String sql)
	{
		try{
			pstmt = dbConn.prepareStatement(sql);
		}catch(SQLException e){
			e.printStackTrace();
		}
		
	}
	
	public static void prepare(String sql)
	{
		try{
			pstmt = dbConn.prepareStatement(sql);
		}catch(SQLException e){
			e.printStackTrace();
		}
		
	}
	
	public static void setString(int parameterIndex, String x)
		throws SQLException{
		pstmt.setString(parameterIndex, x);
	}
	
	public static void setInt(int parameterIndex, int x)
		throws SQLException{
		pstmt.setInt(parameterIndex, x);
	}
	
	public static boolean execute_sql(String sql)throws SQLException
	{
		boolean ret = true;
		try {
			ret = stmt.execute(sql);
		}catch(SQLException e){
			throw e;
		}
		return ret;
	}
	
	public static int execute()throws SQLException
	{
		return pstmt.executeUpdate();
	}
	
	public static ResultSet query(String sql) throws SQLException {
		return stmt.executeQuery(sql);
	}
	
	public static ResultSet query() throws SQLException {
		return pstmt.executeQuery();
	}
	
	static void testConnection(){
		
	}
	
	public static void main(String[] args)throws Exception
	{		
		testConnection();
		System.out.println("done!");
	}
	
}
