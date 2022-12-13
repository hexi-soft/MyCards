package common;

import hexi.dbc.Jdbc;
import java.io.*;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import org.apache.log4j.Logger;

public class BulkInsert {
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		File f = new File("d:\\tomcat\\bin\\lessons\\t.txt");
		
		try {
			Scanner scanner = new Scanner(f);
			String sql = "insert into cards(English, Chinese, memo,book, module)"
						+ " values(?,?,?,?,?)";
			Jdbc.prepare_sql(sql);
			PreparedStatement pstmt = Jdbc.pstmt;
			pstmt.setString(3, "");
			pstmt.setString(4, "ETOEFL");
			pstmt.setInt(5, 1);
			while(scanner.hasNextLine()){
				String line = scanner.nextLine();
				String[] fields = line.split("\t");
				if(fields.length==1){
					String module = line.split(" ")[1];
					Log.debug("Got module:" + module);
					pstmt.setInt(5, Integer.parseInt(module));
					continue;
				}
				if (fields.length==2){
					for (int i=0; i<fields.length; ++i){
						pstmt.setString(i+1,fields[i]);
						System.out.print(fields[i]+"\t");
					}
					System.out.println();
				}else{
					Log.debug("There's something wrong with line:"+line);
				}
				//System.out.println();
				pstmt.executeUpdate();
				//Log.debug(line);
			}
			scanner.close();
		}catch(IOException e){
			Log.debug(e);
		}catch(SQLException e){
			Log.debug(e);
		}
		
	}

}
