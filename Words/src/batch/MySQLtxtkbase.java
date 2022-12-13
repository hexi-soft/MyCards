package batch;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import common.Log;
import common.UFileReader;
import hexi.dbc.Jdbc;
import hexi.dbc.Jdbc2;
import hexi.nlp.base.NGram;

public class MySQLtxtkbase {
	static String sql = "use txtkbase";
	static Jdbc2 sDB = new Jdbc2(Jdbc2.MYSQL_SERVER,"192.168.1.112","root","hujia521");
	static void create_articles()throws SQLException {
		String sql = "create table if not exists articles("
				+ "aid char(32) primary key,"
				+ "title varchar(1000) not null,"
				+ "content text not null default '',"
				+ "memo varchar(100) not null default '')";
		sDB.execute_sql(sql);
		/*
		String uuid = UUID.randomUUID().toString();
		//uuid = uuid.replaceAll("-", "");
		int len = uuid.length();
		Log.d(uuid+":"+len);
		 */
	}
	
	static void create_passages()throws SQLException {
		String sql = "create table if not exists passages("
				+ "p_id int primary key auto_increment,"
				+ "title varchar(200) not null,"
				+ "path varchar(255) not null,"
				+ "memo varchar(100) not null default '')";
		sDB.execute_sql(sql);
	}
	
	static void testCountSentenceNGrams() throws Exception{
		StringBuilder sb = new StringBuilder();
		ResultSet r = sDB.query("select * from sentences where "
							+ "b='E7-1(pep)' or b='E3-1(fltrp3)' "
							+ "or b='E3-2(fltrp3)' or b='E4-1(fltrp3)'"
							+ "or b='E4-1(fltrp3)' or b='E4-2(fltrp3)'"
							+ "or b='E5-1(fltrp3)' or b='E5-2(fltrp3)'"
							+ "or b='E6-1(fltrp3)' or b='E6-2(fltrp3)'");
		int i=0;
		while(r.next()) {
			String s = r.getString("e");
			++i;
			sb.append(s+"\n");
			Log.d(i+": "+s);
		}
		NGram.count_ngrams(sb.toString().toLowerCase(), 1);
	}
	
	static void testAddArticle() throws Exception{
		String sql = "insert into articles(aid,title,content) values(?,?,?)";
		sDB.prepare_sql(sql);
		File file = new File("c:\\corpus\\txt\\passages");
		File[] fs = file.listFiles();
		for(File f:fs) {
			if (f.isFile()) {
				String title = f.getName();
				title = title.replace(".txt", "");
				String uuid = UUID.randomUUID().toString();
				uuid = uuid.replace("-", "");
				Log.d(uuid);
				sDB.setString(1, uuid);
				sDB.setString(2, title);
				String content = UFileReader.read(f.getAbsolutePath());
				sDB.setString(3, content);
				Log.d(title);
				Log.d(content);
				sDB.execute();
			}
			
		}
		
	}
	
	static void create_users() throws SQLException {
		sql = "create table if not exists users("
				+ "uid varchar(15) primary key,"
				+ "usr varchar(15) not null,"
				+ "pwd varchar(6) not null,"
				+ "gender varchar(4) not null,"
				+ "born_date datetime not null,"
				+ "reg_date timestamp not null default now(),"
				+ "mobile_phone char(11) null,"
				+ "fixed_phone char(12) null ,"
				+ "chinese_name varchar(10) null,"
				+ "profession varchar(50) not null default 'pupil',"
				+ "memo varchar(100         ) null)";
		sDB.execute_sql(sql);
	}

	static void create_user_word()throws SQLException {
		sql = "create table if not exists user_new_word("
				+ "uid varchar(15) not null references users(uid),"
				+ "wid int not null default 0,"
				+ "word varchar(100) not null,"
				+ "append_time timestamp not null default now(),"
				+ "last_time date not null default '2019-12-20',"
				+ "re_degree int not null default 1,"
				+ "strange int not null default 7,"
				+ "freq int not null default 1,"
				+ "explains varchar(200) not null default '',"
				+ "source_id varchar(100) not null default '',"
				+ "memo varchar(20) not null default '',"
				+ "constraint pk primary key(uid,word))";
		sDB.execute_sql(sql);
	}

	static void create_word_variants()throws SQLException {
		sql = "create table if not exists word_variants("
				+ "variant varchar(31) not null,"
				+ "word varchar(31) not null,"
				+ "memo varchar(9) not null)";
		sDB.execute_sql(sql);
	}
	
	static void create_cards()throws SQLException {
		sql = "create table if not exists cards("
				+ "wid int unsigned primary key auto_increment,"
				+ "English varchar(100) not null,"
				+ "phonetic varchar(50) not null default '',"
				+ "Chinese varchar(200) not null default '',"
				+ "book varchar(50) not null default '',"
				+ "module smallint not null default 1,"
				+ "memo varchar(10) not null default '')";
		sDB.execute_sql(sql);
	}
	
	static void create_word_pos()throws SQLException {
		sql = "create table if not exists word_pos("
				+ "w varchar(50) not null,"
				+ "pos varchar(10) not null,"
				+ "prob float default null,"
				+ "constraint pk_w_pos primary key(w,pos))";
		sDB.execute_sql(sql);
	}
	
	static void create_user_sentence()throws SQLException {
		sql = "create table if not exists user_sentence("
				+ "sid int not null auto_increment primary key,"
				+ "uid varchar(15) not null references users(uid),"
				+ "sent varchar(180) not null,"
				+ "memo varchar(100) not null default '',"
				+ "constraint u_u_s unique(uid,sent))";
		sDB.execute_sql(sql);
	}
	
	static void create_messages()throws SQLException {
		sql = "create table if not exists messages("
				+ "m_id int not null auto_increment primary key,"
				+ "uid varchar(15) not null references users(uid),"
				+ "msg varchar(180) not null,"
				+ "add_time timestamp not null default now(),"
				+ "sender varchar(20) not null,"
				+ "receiver varchar(20) not null)";
		sDB.execute_sql(sql);
	}
	
	static void create_tables()throws SQLException {
		String sql = "create table if not exists textbooks("
				+"id varchar(20) primary key,"
				+"title varchar(200) not null,"
				+"authors varchar(100),"
				+"publisher varchar(100),"
				+"isbn varchar(40),"
				+"memo varchar(100))\n";
		sDB.execute_sql(sql);
		sql = "create table if not exists sentences("
				+ "s_id int auto_increment primary key,"
				+ "e varchar(1000) not null,"
				+ "c varchar(3000) not null default '',"
				+ "b varchar(20) not null,"
				+ "m int not null default 1,"
				+ "memo varchar(200))\n";
		sDB.execute_sql(sql);
		sql = "create table if not exists book_module("
				+ "book_id varchar(20) not null,"
				+ "module int not null,"
				+ "topic varchar(200),"
				+ "constraint pk_b_m primary key(book_id,module))\n";
		sDB.execute_sql(sql);
		sql = "create table if not exists user_book("
				+"uid varchar(15),bid varchar(20),constraint pk_u_b primary key(uid,bid))\n";
		sDB.execute_sql(sql);
		create_users();
		create_user_word();
		create_word_variants();
		create_cards();
		create_word_pos();
		create_user_sentence();
		create_messages();
		create_articles();
		create_passages();
	}
	
	static void testPassages() throws Exception{
		ResultSet r = sDB.query("select * from passages");
		while(r.next()) {
			Log.d(r.getString("path"));
		}
	}
	public static void main(String[] args) throws Exception {
		create_tables();
		//testPassages();
		//testAddArticle();
		//testCountSentenceNGrams();
	}

	static void testX() {
		/*
		prepare_sql("select * from cee_voc");
		ResultSet r = pstmt.executeQuery();
		TreeMap<String,String> words = new TreeMap<String,String>();
		while (r.next()){
			String wid = r.getString("wid");
			String word = r.getString("word");
			String pos = r.getString("pos");
			String chinese = r.getString("chinese");
			String value = words.get(word);
			if (value==null){
				words.put(word, pos);
			}else{
				pos += " & " + value;
				System.out.println(word + " "+pos+" " +chinese);
				String sql = "update cee_voc set pos='" + pos + "' where word='"+word+"'"; 
				//execute_sql(sql);
				sql = "delete cee_voc where wid="+wid;
				//execute_sql(sql);
			}
		}//pstmt.executeUpdate();*/
	
	}
}
