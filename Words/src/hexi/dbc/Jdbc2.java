package hexi.dbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import common.Log;

public class Jdbc2 {

	private static final String MySQLDriver = "com.mysql.jdbc.Driver";
	private static final String SQLServerDriver = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
	
	public static final String SQL_SERVER_2005 = "SQLServer2005";
	public static final String MYSQL_SERVER = "MySQL";
	
	private String dbURL = "";
	private String mDatabase="txtkbase";  
	private Connection dbConn;
	private Statement stmt;
	private PreparedStatement pstmt;
	private String dbmsType;
	
	public Jdbc2(String dbms, String ip, String user, String pwd) {
		try {
			if (dbms==SQL_SERVER_2005) {
				Class.forName(SQLServerDriver);
				dbURL = "jdbc:sqlserver://"+ip+":1433; DatabaseName="+mDatabase;
				dbmsType = dbms;
			}else if(dbms==MYSQL_SERVER) {
				Class.forName(MySQLDriver);
				dbURL = "jdbc:mysql://"+ip+":3306";
				dbmsType = dbms;
			}else {
				throw new Exception("Unsurpported Databas Type:"+dbms);
			}
			dbConn  = DriverManager.getConnection(dbURL, user, pwd);
			System.out.println(dbURL);
			stmt = dbConn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
			stmt.execute("Use " + mDatabase);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
public Jdbc2(String dbms, String database, String ip, String user, String pwd) {
		
		try {
			if (dbms==SQL_SERVER_2005) {
				Class.forName(SQLServerDriver);
				dbURL = "jdbc:sqlserver://"+ip+":1433; DatabaseName="+database;
				dbmsType = dbms;
			}else if(dbms==MYSQL_SERVER) {
				Class.forName(MySQLDriver);
				dbURL = "jdbc:mysql://"+ip+":3306";
				dbmsType = dbms;
			}else {
				throw new Exception("Unsurpporte database type: "+dbms);
			}
			dbConn  = DriverManager.getConnection(dbURL, user, pwd);
			System.out.println(dbURL);
			stmt = dbConn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
			stmt.execute("Use " + database);
			mDatabase = database;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void prepare_sql(String sql)
	{
		try{
			pstmt = dbConn.prepareStatement(sql);
		}catch(SQLException e){
			e.printStackTrace();
		}
		
	}
	
	public void setString(int column, String s)
	{
		try{
			pstmt.setString(column, s);
		}catch(SQLException e){
			e.printStackTrace();
		}
		
	}
	
	public void setInt(int column, int i)
	{
		try{
			pstmt.setInt(column, i);
		}catch(SQLException e){
			e.printStackTrace();
		}
		
	}
	
	public void prepare(String sql)
	{
		try{
			pstmt = dbConn.prepareStatement(sql);
		}catch(SQLException e){
			e.printStackTrace();
		}
		
	}
	
	public int execute() {
		int r = 0;
		if (pstmt==null) {
			Log.debug("Not attatche to any database yet.");
			return r;
		}
		try {
			r = pstmt.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return r;
	}
	
	public boolean execute_sql(String sql)throws SQLException
	{
		boolean ret = true;
		try {
			ret = stmt.execute(sql);
		}catch(SQLException e){
			throw e;
		}
		return ret;
	}
	
	public ResultSet query(String sql) throws SQLException {
		ResultSet r = null;
		r = stmt.executeQuery(sql);
		return r;
	}
	
	public ResultSet query(String sql, int maximum) throws SQLException {
		ResultSet r = null;
		if (dbmsType==MYSQL_SERVER) {
			sql += " limit "+maximum;
		}
		else if(dbmsType == SQL_SERVER_2005) {
			sql = sql.replace("select", "select top "+maximum+" ");
		}
		r = stmt.executeQuery(sql);
		return r;
	}
	
	static void transfer_cards()throws SQLException{
		Jdbc2 dbc1 = new Jdbc2("MySQL", "192.168.1.112", "root", "hujia521");
		Jdbc2 dbc2 = new Jdbc2("SQLServer2005", "192.168.1.112", "sa", "hujia521");
		dbc1.prepare_sql("insert into cards(English,phonetic,Chinese,book,module)"
				+ "values(?,?,?,?,?)");
		ResultSet r = dbc2.query("select * from cards");
		while(r.next()) {
			String English = r.getString("English");
			String Chinese = r.getString("Chinese");
			String phonetic = r.getString("phonetic");
			String book = r.getString("book");
			int module = r.getInt("module");
			dbc1.setString(1, English);
			dbc1.setString(2, phonetic);
			dbc1.setString(3, Chinese);
			dbc1.setString(4, book);
			dbc1.setInt(5, module);
			dbc1.execute();
			//Log.d(English+"\t"+Chinese);
		}
	}
	
	static void transfer_sents()throws SQLException{
		Jdbc2 dbc1 = new Jdbc2("MySQL", "192.168.1.112", "root", "hujia521");
		Jdbc2 dbc2 = new Jdbc2("SQLServer2005", "192.168.1.112", "sa", "hujia521");
		dbc1.prepare_sql("insert into sentences(e,c,b,m,memo)"
				+ "values(?,?,?,?,'')");
		ResultSet r = dbc2.query("select * from sentences where s_id>135328");
		while(r.next()) {
			String English = r.getString("e").replaceAll("[^\\w\\s\\.\\?\\!-\",'@\\$&\\*_\\:\\<\\>\\=]+", "").trim();
			English = English.replaceAll("\\s{2,}", " ");
			English = English.replaceAll("'\\s+'","");
			if (English.length()<5) {
				continue;
			}
			String Chinese = r.getString("c");
			String book = r.getString("b");
			int module = r.getInt("m");
			try {
			dbc1.pstmt.setString(1, English);
			dbc1.pstmt.setString(2, Chinese);
			dbc1.pstmt.setString(3, book);
			dbc1.pstmt.setInt(4, module);
			dbc1.pstmt.executeUpdate();
			}catch(SQLException e) {
				Log.debug(e);
			}
			Log.d(English+"\t"+Chinese);
		}
	}
	
	static void transfer_sents2()throws SQLException{
		Jdbc2 dbc2 = new Jdbc2("MySQL", "192.168.1.112", "root", "hujia521");
		Jdbc2 dbc1 = new Jdbc2("SQLServer2005", "192.168.1.112", "sa", "hujia521");
		dbc1.prepare_sql("insert into sentences2(e,c,b,m,memo)"
				+ "values(?,?,?,?,'')");
		ResultSet r = dbc2.query("select * from sentences where b='center.top'");
		while(r.next()) {
			String English = r.getString("e");
			String Chinese = r.getString("c");
			String book = r.getString("b");
			int module = r.getInt("m");
			try {
			dbc1.pstmt.setString(1, English);
			dbc1.pstmt.setString(2, Chinese);
			dbc1.pstmt.setString(3, book);
			dbc1.pstmt.setInt(4, module);
			dbc1.pstmt.executeUpdate();
			}catch(SQLException e) {
				Log.debug(e);
			}
			Log.l(English+"\t"+Chinese);
		}
	}
	
	static void transfer_variants() throws SQLException {
		Jdbc2 dbc1 = new Jdbc2("MySQL", "106.12.143.86", "root", "hujia521");
		Jdbc2 dbc2 = new Jdbc2("SQLServer2005", "192.168.1.101", "sa", "hujia521");
		if (dbc1 != null && dbc2 != null) {
			dbc1.prepare_sql("insert into word_variants " + "values(?,?,?)");
			ResultSet r = dbc2.query("select * from word_variants");
			while (r.next()) {
				String variant = r.getString(1);
				String word = r.getString(2);
				String memo = r.getString(3);
				dbc1.pstmt.setString(1, variant);
				dbc1.pstmt.setString(2, word);
				dbc1.pstmt.setString(3, memo);
				dbc1.pstmt.executeUpdate();
				Log.d(variant + "\t" + word);
			}
		}
	}
	
	static boolean hasUpperLetter(String word) {
		for(int i=0; i<word.length();++i) {
			char c = word.charAt(i); 
			if (c!='-' && c<'a')
				return true;
		}
		return false;
	}

	static void transfer_word_freq() throws SQLException {
		Jdbc2 dbc1 = new Jdbc2(Jdbc2.SQL_SERVER_2005, "192.168.1.112", "sa", "hujia521");
		Jdbc2 dbc2 = new Jdbc2(Jdbc2.SQL_SERVER_2005, "192.168.1.112", "sa", "hujia521");
		if (dbc1 != null && dbc2 != null) {
			String sql = "select  word, freq from wordfreq";
			ResultSet r = dbc1.query(sql);
			while (r.next()) {
				String word = r.getString(1);
				if (hasUpperLetter(word)) {
					String lowW = word.toLowerCase();
					double freq = r.getDouble(2);
					String sql2 = "select * from wordfreq where word='" + lowW + "'";
					ResultSet r2 = dbc2.query(sql2);
					if (r2.next()) {

						sql2 = "update wordfreq set freq=freq+" + freq + " where word='" + lowW + "'";
						dbc2.execute_sql(sql2);
						Log.p(sql2);
						sql2 = "delete wordfreq where word='" + word + "'";
						dbc2.execute_sql(sql2);
						Log.p(sql2);
					}
				}
			}
		}
	}

	public static void main(String[] args)throws Exception
	{		
		transfer_cards();
		//transfer_word_freq();
		//transfer_sents2();
		System.out.println("done!"+hasUpperLetter("a-zDbb"));
	}
}
