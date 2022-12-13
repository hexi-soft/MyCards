package hexi.dbc;

import java.sql.Connection;
import java.sql.Statement;
import hexi.dbc.impl.MySQLDatabaseConnection;
import hexi.dbc.impl.SQLServer2005Connection;
import hexi.dbc.impl.SQLServerDatabaseConnection;

public class DatabaseConnectionFactory {

	public static DatabaseConnection getSQL2000Connection()throws Exception{
		return new SQLServerDatabaseConnection();
	}
	
	public static DatabaseConnection getMySQLConnection()throws Exception{
		return new MySQLDatabaseConnection();
	}
	
	public static DatabaseConnection getSQL2005Connection()throws Exception{
		return new SQLServer2005Connection();
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		/*
		PreparedStatement pstmt = conn.prepareStatement("insert into article_en values(?,?,?)");
		
		File dir = new File("d:\\corpus\\txt\\passages");
		File[] fs = dir.listFiles();
		int i = 0;
		for(File f : fs){
			String path = f.getAbsolutePath();
			String name = f.getName();
			if(!name.endsWith(".txt"))
				continue;
			name = name.replaceAll("\\s\\W+\\.txt$", "");
			name = name.replaceAll("\\.txt$", "");
			if (name.length()<3)
				continue;
			++i;
			String text = UFileReader.read(path);
			pstmt.setInt(1, i);
			pstmt.setString(2, name);
			pstmt.setString(3, text);
			pstmt.execute();
			System.out.println(i + " " + name);
		}*/
		//getDatabaseConnection1();
	}

}
