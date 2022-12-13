package batch;

import java.io.File;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import common.Log;
import common.UFileReader;
import hexi.dbc.Jdbc;
import hexi.nlp.PosTagger;

public class Sentences {

	/**
	 * @param args
	 */
	public static void tmain(String[] args) {

		String sql = "select s_id,e from sentences where b like 'E%fl%'";
		try{
			ResultSet r = Jdbc.query(sql);
			int i = 0;
			StringBuilder sb = new StringBuilder();
			while(r.next()){
				++i;
				String n = new DecimalFormat("0000").format(i);
				int id = r.getInt("s_id");
				Jdbc.execute_sql("update sentences set memo='"+n+"' where s_id="+id);
				String e = r.getString("e");
				Log.debug(n+" "+e);
				e += "\r\n\r\n\r\n";
				sb.append(e);
			}
			//UFileWriter.write("d:\\tmp\\ss.txt", sb.toString());
		}catch(SQLException e){
			Log.debug(e);
		}
		
	}
	
	public static void ins_sents(String book, String filepath) {
		String sql = "insert into sentences(e,c,b,m) values(?,?,?,?)";
		File file = new File(filepath);
		// FileInputStream fis = new FileInputStream(file);
		// BufferedInputStream bis = new BufferedInputStream(fis);
		try {
			Scanner scanner = new Scanner(file,"GBK");
			//Log.d(scanner);
			Jdbc.prepare_sql(sql);
			boolean odd = false;
			String e = "";
			String c = "";
			PreparedStatement pstmt = Jdbc.pstmt;
			pstmt.setString(3, book);
			sql = "insert into book_module values(?,?,?)";
			Jdbc.prepare_sql(sql);
			PreparedStatement pstmt2 = Jdbc.pstmt;
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				//Log.d("got line: "+line);
				if (line.toLowerCase().startsWith("unit ")) {
					String[] ss = line.split(" ");
					int unit = Integer.parseInt(ss[1]);
					Log.debug("unit:" + unit);
					pstmt.setInt(4, unit);
					pstmt2.setString(1, book);
					pstmt2.setInt(2, unit);
					pstmt2.setString(3, "");
					pstmt2.executeUpdate();
					continue;
				}
				odd = !odd;
				if (odd) {
					e = line;
				} else {
					c = line;
				}
				if (!odd) {
					String l = e + "\t" + c;
					Log.debug(l);
					//System.out.println(l);
					pstmt.setString(1, e);
					pstmt.setString(2, c);
					pstmt.executeUpdate();
				}
			}
			scanner.close();
		} catch (Exception exception) {
			Log.d(exception);
		}
	}

	public static void get_sentences(String filepath) {
		String s = UFileReader.read(filepath);
		String[] lines = s.split("\r\n");
		for(String line : lines) {
			String l = line.trim();
			if (l.isEmpty()) {
				continue;
			}
			Pattern p = Pattern.compile(".*[\u4e00-\u9fa5]+.*|\\d[a-z].*|^Conversation \\d$|^Section [A-D]$");
			Matcher m = p.matcher(l);
			if (m.matches()) {
				continue;
			}
			l = l.replaceAll("^[A-Z].{0,9}[：:](?!\\d{2})", "");
			List<String> sents = PosTagger.split_sents(l);
			for(String sent : sents) {
				Log.d(sent+"\n");
			}
			
		}
	}
	
	public static void main(String[] args) throws IOException {
		//get_sentences("d:\\My Docs\\lessons\\6-2-sents.txt");
		ins_sents("E6-2(fltrp3)", "d:\\My Docs\\lessons\\6-2-sents.txt");
	}
}
