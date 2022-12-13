package hexi.dbc.impl;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

import hexi.dbc.DatabaseConnection;

public class MySQLDatabaseConnection implements DatabaseConnection{
	private static final String MySQLDriver = "com.mysql.jdbc.Driver";
	private static String dbURL = "";
	private String userName;
	private String userPwd;
	
	private Connection conn = null;
	
	public MySQLDatabaseConnection()throws Exception{
		try {
			Class.forName(MySQLDriver);
			Properties pro = new Properties();
			FileInputStream in = new FileInputStream("servers.properties");
			pro.load(in);
			in.close();
			String ip = pro.getProperty("MYSQL_IP");
			dbURL = "jdbc:mysql://"+ip+":3306";
			userName = pro.getProperty("MYSQL_ACCOUNT");
			userPwd = pro.getProperty("MYSQL_PWD");
			conn  = DriverManager.getConnection(dbURL, userName, userPwd);
			System.out.println("MySQLDatabaseConnection: connect to MySQL("+ip+") successfully!");
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

}
