package batch;

import hexi.dbc.Jdbc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import common.Log;

public class InsertSentences {

	static void sents() throws FileNotFoundException {
		String sql = "insert into sentences(e,c,memo,b,m) values(?,?,?,?,?)";
		File file = new File("d:\\My Docs\\lessons\\Caillou-sents2.txt");
		Scanner scanner = new Scanner(file);
		Jdbc.prepare_sql(sql);
		String c="";
		PreparedStatement pstmt = Jdbc.pstmt;
		try{
			pstmt.setString(4, "ECaillou");
			Jdbc.prepare_sql("insert into book_module values('ECaillou',?,?)");
			while(scanner.hasNextLine()){
				String line = scanner.nextLine().trim();
				if (line.isEmpty()){
					continue;
				}
				Pattern p = Pattern.compile("^(\\d+)\\.\\s+(.+)");
				Matcher m = p.matcher(line);
				if (m.find()){
					Log.debug("Got unit "+m.group(1)+": "+m.group(2));
					int unit = Integer.parseInt(m.group(1));
					pstmt.setInt(5, unit);
					pstmt.setString(3, m.group(2));
					Jdbc.pstmt.setInt(1, unit);
					Jdbc.pstmt.setString(2, m.group(2));
					Jdbc.pstmt.executeUpdate();
					continue;
				}
				String[] sents = line.split("(?<=[\\.?!])\\s+");
				for(String e:sents) {
					//e = line;
					System.out.println(e);
					pstmt.setString(1, e);
					pstmt.setString(2, c);
					pstmt.executeUpdate();
				}
			}
		}catch(SQLException exception){
			Log.debug(exception);
		}
	
	}
	
	static void modules() throws FileNotFoundException {
		String sql = "update book_module set ncontent=? where book_id='ECaillou' and module=?";
		File file = new File("E:\\r\\Caillou.txt");
		Scanner scanner = new Scanner(file);
		Jdbc.prepare(sql);
		String c="";
		try{
			Jdbc.setInt(2, 1);
			
			while(scanner.hasNextLine()){
				String line = scanner.nextLine().trim();
				if (line.isEmpty()){
					continue;
				}
				Pattern p = Pattern.compile("^(\\d+)\\.\\s*(.+)");
				Matcher m = p.matcher(line);
				if (m.find()){
					Log.debug("Got unit "+m.group(1)+": "+m.group(2));
					if (!c.isEmpty()) {
						Log.p(c.substring(0,c.length()/20));
						Jdbc.setString(1, c);
						Jdbc.execute();
						c = "";
					}
					int unit = Integer.parseInt(m.group(1));
					Jdbc.setInt(2, unit);
					continue;
				}
				c += line+"\r\n";
			}
			if (!c.isEmpty()) {
				Log.p(c.substring(0,c.length()/100));
				Jdbc.setString(1, c);
				Jdbc.execute();
			}
		}catch(SQLException exception){
			Log.debug(exception);
		}finally{
			scanner.close();
		}
	}
	
	
	public static void main(String[] args)throws IOException{
		modules();
	}
	
	public static void tmain(String[] args)throws IOException{
		String text = "What did you learn yesterday? Do you still remember? What do you have at school today?";
		String[] sents = text.split("(?<=[\\.?!])\\s+");
		for(String sent:sents){
			Log.debug(sent);
		}
	}
}
