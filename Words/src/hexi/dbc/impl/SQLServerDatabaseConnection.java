package hexi.dbc.impl;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

import hexi.dbc.DatabaseConnection;

public class SQLServerDatabaseConnection implements DatabaseConnection{

	private static final String SQLServerDriver = "com.microsoft.jdbc.sqlserver.SQLServerDriver";
	private static String dbURL = "jdbc:microsoft:sqlserver://localhost:1433; DatabaseName=TXTKBASE";
	private String userName;
	private String userPwd;
	
	private Connection conn = null;
	
	public SQLServerDatabaseConnection()throws Exception{
		try {
			Class.forName(SQLServerDriver);
			Properties pro = new Properties();
			FileInputStream in = new FileInputStream("servers.properties");
			pro.load(in);
			in.close();
			String ip = pro.getProperty("SERVER_IP");
			dbURL = "jdbc:microsoft:sqlserver://"+ip+":1433; DatabaseName=TXTKBASE";
			userName = pro.getProperty("SQLSERVER_ACCOUNT");
			userPwd = pro.getProperty("SQLSERVER_PWD");
			conn  = DriverManager.getConnection(dbURL, userName, userPwd);
			System.out.println("SQLServerDatabaseConnection: connect to SQL Server 2000 successfully!");
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	public Connection getConnection(){
		return this.conn;
	}
	
	public void close(){
		if (this.conn != null){
			try{
				conn.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
