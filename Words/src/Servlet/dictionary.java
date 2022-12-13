package Servlet;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import hexi.dbc.Jdbc;
import common.Json;
import hexi.web.Dictionary;

public class dictionary extends HttpServlet{
	
	public void doGet(HttpServletRequest request, HttpServletResponse response)
	  throws IOException, ServletException
	{
		response.setContentType("application/json;charset=UTF-8");
		response.setHeader("Cache-Control", "no-cache");
		request.setCharacterEncoding("utf-8");
		String uid = request.getParameter("uid");
		String word = request.getParameter("word");
			
		String resp = "null";
				
		if (uid != null && word != null && word.matches("[a-zA-Z]+"))
		{
			resp = look_up(uid, word);
		}
		response.getWriter().write(resp);
	}

	public static String look_up(String uid, String word){
		return Json.toJson(Dictionary.lookup(uid, word));
	}
	
	public static String look_up3(String param)
	{
		String chinese = null;
		ResultSet r = null;
		String sql = "select * from words where word='"+param+"'";
		try{
			r = Jdbc.query(sql);
			if (r.next()){
				String s = r.getString("chinese");
				//if (s.length()>10)
					//s = s.substring(0,10)+"...";
				chinese = "[S] "+ s;
			}
		}catch(SQLException e){
			e.printStackTrace();
		}	
		return chinese;
	}
	
	public String look_up1(String param)
	{
		String chinese = null;
		ResultSet r = null;
		String sql = "select * from cards2 where word = '"+param+"'";
		try{
			r = Jdbc.query(sql);
			if (r.next()){
				chinese = "[J] " + r.getString("Chinese");
			}
		}catch(SQLException e){
			e.printStackTrace();
		}	
		return chinese;
	}
			
	public void doPost(HttpServletRequest request,HttpServletResponse response)
	  throws IOException,ServletException
	 {
		doGet(request, response);
	 }
			
	public void init(ServletConfig config) throws ServletException {
	 }
	
	public static Boolean is_irregular(String word){
		Boolean ret = false;
		if (word.equals("am")||word.equals("is")||word.equals("are")||word.equals("has")){
			ret = true;
		}else{
			String sql = "select * from irregular_verbs where verb='"+word+"'";
			try{
				ResultSet r = Jdbc.query(sql);
				while(r.next()){
					ret = true;
				}
			}catch(SQLException e){
				System.out.println(e.getMessage());
			}
		}
		return ret;
	}
	
	public static void pmain(String[] args){
		String sql = "select * from cards2";
		try{
			ResultSet r = Jdbc.query(sql);
			while (r.next()){
				String word = r.getString("word");
				String chinese = r.getString("Chinese");
				String memo = r.getString("memo");
				Pattern p = Pattern.compile(".*\\bv[it]?\\.");
				Matcher m = p.matcher(chinese);
				if (m.find()){
					if (!memo.equals("i") && !is_irregular(word) && word.endsWith("e")){
						String word_pt = word + "d";
						sql = "insert into word_variants values('"
								+ word_pt+"','"+word+"', 'pt')";
						//Jdbc.execute_sql(sql);
						//System.out.println(word_pt+" "+word +" "+chinese);
					}else if(!is_irregular(word)&&Pattern.compile("\\w*[^aeiou][aeiou][^aeiouwrlxy]$").matcher(word).find()){
						String word_pt = word+word.substring(word.length()-1)+"ed";
						sql = "insert into word_variants values('"
								+ word_pt+"','"+word+"', 'pt')";
						if (word.length()==6){
							;//System.out.println(word_pt+" "+word);
							//Jdbc.execute_sql(sql);
						}
						else{
							;//System.out.println(word_pt+" "+word);
						}
						
					}else if(Pattern.compile("\\w+[^aeiou]y$").matcher(word).find()){
						String word_pt = word.replaceAll("y$", "ied");
						sql = "insert into word_variants values('"
								+ word_pt+"','"+word+"', 'pt')";
						//Jdbc.execute_sql(sql);
						//System.out.println(word_pt+" "+word + " "+chinese);
					}else if (!is_irregular(word) && !memo.equals("i")){
						String word_pt = word+"ed";
						sql = "insert into word_variants values('"
								+ word_pt+"','"+word+"', 'pt')";
						Jdbc.execute_sql(sql);
						System.out.println(word+" "+chinese);
					}
				}
			}
		}catch(SQLException e){
			System.out.println(e.getMessage());
		}
	}
	
	public static void imain(String[] args){
		String sql = "select * from cards2";
		try{
			ResultSet r = Jdbc.query(sql);
			while (r.next()){
				String word = r.getString("word");
				String chinese = r.getString("Chinese");
				Pattern p = Pattern.compile(".*\\bv[it]?\\.");
				Matcher m = p.matcher(chinese);
				if (m.find()){
					if (word.endsWith("e")&&!word.endsWith("ee")&&!word.equals("be")
							&&!word.equals("die")&&!word.equals("lie")){
						String wording = word.substring(0, word.length()-1)+"ing";
						sql = "insert into word_variants values('"
								+ wording+"','"+word+"', 'ing')";
						//Jdbc.execute_sql(sql);
						//System.out.println(wording+" "+word +" "+chinese);
					}else if(Pattern.compile("\\w*[^aeiou][aeiou][^aeiouwrlxy]$").matcher(word).find()){
						String wording = word+word.substring(word.length()-1)+"ing";
						sql = "insert into word_variants values('"
								+ wording+"','"+word+"', 'ing')";
						if (word.length()==3){;}
							//Jdbc.execute_sql(sql);}
						else System.out.println(wording+" "+word);
						
					}else if(!word.equals("am")&&!word.equals("is")
							&&!word.equals("are")&&!word.equals("must")&&!word.equals("may")
							&&!word.equals("can")&&!word.equals("could")&&!word.equals("might")
							&&!word.equals("should")&&!word.equals("shall")&&!word.equals("can't")){
						String word_v = word+"ing";
						sql = "insert into word_variants values('"
								+ word_v+"','"+word+"', 'ing')";
						//Jdbc.execute_sql(sql);
						System.out.println(word_v+" "+word + " "+chinese);
					}
				}
			}
		}catch(SQLException e){
			System.out.println(e.getMessage());
		}
	}
	
	public static void main2(String[] args){
		String sql = "select * from cards2";
		try{
			ResultSet r = Jdbc.query(sql);
			while (r.next()){
				String word = r.getString("word");
				String chinese = r.getString("Chinese");
				Pattern p = Pattern.compile(".*\\bn\\..*");
				Matcher m = p.matcher(chinese);
				if (m.find()){
					if (word.endsWith("s")||word.endsWith("x")||word.endsWith("sh")
							||word.endsWith("o")||word.endsWith("ch")){
						sql = "insert into word_variants values('"
								+ word+"es','"+word+"', 'pl.')";
						//Jdbc.execute_sql(sql);
						//System.out.println(word);
					}else if(Pattern.compile("\\w+[^aeiou]y$").matcher(word).find()){
						String word_v = word.replaceAll("y$", "ies");
						sql = "insert into word_variants values('"
								+ word_v+"','"+word+"', 'pl.')";
						try{
							Jdbc.execute_sql(sql);
							System.out.println(word_v+" "+word);
						}
						catch(Exception e){
							
						}
					}else{
						String word_v = word+"s";
						sql = "insert into word_variants values('"
								+ word_v+"','"+word+"', 'singular')";
						try{
							Jdbc.execute_sql(sql);
							System.out.println(word_v+" "+word + " "+chinese);
						}catch(SQLException e){
							sql = "update word_variants set memo='pl.'"
									+ " where variant='"+word_v+"'";
							Jdbc.execute_sql(sql);
						}
						
					}
				}
			}
		}catch(SQLException e){
			System.out.println(e.getMessage());
		}
	}
	
	public static void tmain(String[] args){
		System.out.println(look_up("a", "looked"));
		String sql = "select * from cards2";
		try{
			ResultSet r = Jdbc.query(sql);
			while (r.next()){
				String word = r.getString("word");
				String chinese = r.getString("Chinese");
				Pattern p = Pattern.compile("\\w+1$");
				Matcher m = p.matcher(word);
				if (m.find()){
					String word0 = word.substring(0,word.length()-1);
					sql = "update cards2 set word='"+word0+"'"
							+ ", Chinese='"+chinese+" '+Chinese"
							+ " where word='"+word0+"2'";
					Jdbc.execute_sql(sql);
					System.out.println(word+" "+word0);
				}
			}
		}catch(SQLException e){
			System.out.println(e.getMessage());
		}
		
	}
	
	public static void main(String[] args){
		System.out.println(look_up("a", "Everyone"));		
	}

}
