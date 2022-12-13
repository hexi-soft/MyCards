package hexi.dbc.impl;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

import common.Log;
import hexi.dbc.DatabaseConnection;

public class SQLServer2005Connection implements DatabaseConnection{

	private static final String SQLServerDriver = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
	private static String dbURL = "";
	private String userName;
	private String userPwd;
	
	private Connection conn = null;
	
	public SQLServer2005Connection()throws Exception{
		try {
			Class.forName(SQLServerDriver);
			Properties pro = new Properties();
			FileInputStream in = new FileInputStream("servers.properties");
			pro.load(in);
			in.close();
			String ip = pro.getProperty("SQLSERVER_IP");
			dbURL = "jdbc:sqlserver://"+ip+":1433; DatabaseName=TXTKBASE";
			userName = pro.getProperty("SQLSERVER_ACCOUNT");
			userPwd = pro.getProperty("SQLSERVER_PWD");
			conn  = DriverManager.getConnection(dbURL, userName, userPwd);
			Log.debug("Connect to SQL Server 2005 successfully!");
		} catch (Exception e) {
			Log.debug(e+" dbURL:"+dbURL+" user:"+userName+" pwd:"+userPwd);
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

