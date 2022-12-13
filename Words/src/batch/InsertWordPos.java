package batch;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import common.Log;
import common.UFileReader;
import hexi.dbc.Jdbc;

public class InsertWordPos {


	static void insert_word_pos() {
		
		Logger log = Log.log;
		try{
			//Jdbc.execute_sql("truncate table word_pos");
			Jdbc.prepare_sql("insert word_pos(w, pos) values(?,?)");
			String sql = "select word, pos from cee_voc";
			ResultSet r = Jdbc.query(sql);
			while(r.next()){
				String w = r.getString(1);
				String p = r.getString(2);
				String[] ps = p.split("[,&]");
				for(int i=0; i<ps.length; ++i){
					String pos = ps[i].trim();
					if (pos.isEmpty()){
						continue;
					}
					Jdbc.pstmt.setString(1, w);
					Jdbc.pstmt.setString(2, pos);
					Jdbc.pstmt.executeUpdate();
					System.out.print(pos+"\t");
				}
				System.out.println();
				log.debug(w +"\t"+p);
			}
			
		}catch(Exception e){
			log.info(e);
		}
	}
	
	static void insert_word_category() {
		String data = UFileReader.read("e:\\r\\word_cate.txt");
		String[] lines = data.split("\r\n");
		String sql = "insert word_category values('',?,?)";
		Jdbc.prepare(sql);
		for (String line : lines) {
			String[] pair = line.split("\t");
			String word = pair[1];
			String cate = pair[2];
			try {

					Jdbc.setString(1, word);
					Jdbc.setString(2, cate);
					Log.l(word+"\t"+cate);
					Jdbc.execute();					
			} catch (SQLException e) {
				Log.l(e+word);
				
			}
			
		}
	}
	
	static void testSelectExplains() {
		String sql = "select * from word_propertis\r\n" + 
				"where property_name='explain'\r\n" + 
				"";
		try {
			ResultSet rs = Jdbc.query(sql);
			ArrayList<String[]> ws = new ArrayList<String[]>();
			String tw = "",te="";
			while(rs.next()) {
				String w = rs.getString("word");
				String e = rs.getString("property_value");
				if (!w.contentEquals(tw)) {
					String[] we = new String[2];
					we[0] = tw; we[1] = te;
					Log.l(we[0]+"\t"+we[1]);
					tw = w;
					te = e;
					ws.add(we);
				}else {
					te += "; "+e; 
				}				
			}
			String[] lwe = new String[2];
			lwe[0] = tw; lwe[1] = te;
			Log.l(lwe[0]+"\t"+lwe[1]);
			ws.add(lwe);
			rs.close();
			sql = "update words set explains=?,memo='youdao' where word=?";
			Jdbc.prepare(sql);
			for(String[] v : ws) {
				try {
					Jdbc.setString(1, v[1]);
					Jdbc.setString(2, v[0]);
					Jdbc.execute();
				}catch(SQLException e) {
					Log.p(e);
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	static void testUpdateCardsExplains() {
		String sql = "select word,explains from words";
		try {
			ResultSet rs = Jdbc.query(sql);
			ArrayList<String[]> ws = new ArrayList<String[]>();
			while(rs.next()) {
				String w = rs.getString("word");
				String e = rs.getString("explains");
				String[] we = new String[2];
				we[0] = w; we[1] = e;
				ws.add(we);
			}
			rs.close();
			sql = "update cards set Chinese=?,memo='youdao' where English=? and book='words'and memo!='youdao'";
			Jdbc.prepare(sql);
			for(String[] v : ws) {
				try {
					Jdbc.setString(1, v[1]);
					Jdbc.setString(2, v[0]);
					Jdbc.execute();
					Log.l(v[1]+"\t"+v[0]);

				}catch(SQLException e) {
					Log.p(e);
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		insert_word_category();
		//testUpdateCardsExplains();
	}

}
