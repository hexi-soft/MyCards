package batch;

import java.sql.ResultSet;

import common.Log;
import hexi.dbc.Jdbc;

public class Words {

	public static void main(String[] args) throws Exception {
		String sql = "select distinct English,Chinese from cards"
				+ " where book like 'E%(fltrp3)' or book='E7-1(pep)'"
				+ " order by English";
		ResultSet r = Jdbc.query(sql);
		int i=0;
		while(r.next()) {
			++i;
			String e = r.getString("English");
			//e = "__________";
			String c = r.getString("Chinese");
			//String b = r.getString("book");
			//int m = r.getInt("module");
			//Log.d(i+"\t"+e+"\t"+c+"\t"+"\t"+b+"\t"+m);
			System.out.print(" "+i+" "+e+" "+c);
			if (i%3==0) {
				System.out.println();
			}
		}
	}

}
