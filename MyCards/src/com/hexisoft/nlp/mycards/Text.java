package com.hexisoft.nlp.mycards;

import java.util.ArrayList;
import java.util.Locale;

class Span {

	public String s;
	public Locale l;
	public Span(String s, Locale l){
		this.s = s;
		this.l = l;
	}
}

public class Text {

	private String text = "";
	private int page_size = 1000;
	private int page_start = 0;
	private int page_no = 0;
	private String current_page = "";
	private String[] blocks;
	private int iCur = -1;
	private String sent_splitter = "(?<=[\\.?!¡££¡£¿¡±])\\s*|\\s{2,}";
	
	public static Locale LOCALE_NUMBER = Locale.US;
	public static Locale LOCALE_PUNCTUATION = Locale.US;
	public static Locale language(char c){
		Locale r = Locale.US;
		if(c>=48 && c<58){
			return LOCALE_NUMBER;
		}
		if(c>32 && c<48 || c>57 && c<65 || c>90 && c<97 || c>122 && c<127){
			return LOCALE_PUNCTUATION;
		}
		if(c>0 && c<255 || c==8217 || c==8221 || c==8220){
			r = Locale.US;
		}else{
			r = Locale.CHINESE;
		}
		return r;
	}

	public Text(String s, int pageSize){
		text = s;
		page_size = pageSize;
		int page_end = page_start+pageSize<s.length()? page_start+pageSize:s.length();
		current_page = text.substring(page_start, page_end);
		String[] ss = text.split(sent_splitter);
		ArrayList<Span> spans = new ArrayList<Span>();
		int count = 0;
		for(String str:ss){
			if(str.trim().length()>0){
				Locale l = language(str.charAt(0));
				int start = 0;
				int length = str.length();
				String sub = "";
				for(int i=0; i<length; ++i){
					Locale lang = language(str.charAt(i));
					if(lang != l){
						sub = str.substring(start, i);
						if(sub.trim().length()>0){
							Span span = new Span(sub,l);
							spans.add(span);
							++count;
						}
						start = i;
						l = lang;
					}
					LOCALE_NUMBER = l;
					LOCALE_PUNCTUATION = l;
				}
				sub = str.substring(start);
				if(sub.trim().length()>0){
					Span span = new Span(sub,l);
					spans.add(span);
					++count;
				}		
			}
		}
		int cc = spans.size();
		count = 0;
		blocks = new String[cc];
		for(Span sp:spans){
			blocks[count++] = sp.s;
		}
		if(blocks.length>0){
			iCur = 0;
		}
	}
	
	public int blocks(){
		return blocks.length;
	}
	
	public boolean has_next_page(){
		if (page_start + page_size < text.length()){
			return true;
		}else{
			return false;
		}
	}
	
	public boolean has_previous_page(){
		 return (page_start > 0);
	}
	
	public boolean next_page(){
		if (page_start+page_size < text.length()){
			page_start += page_size;
			int page_end = page_start+page_size<text.length()? page_start+page_size:text.length();
			current_page = text.substring(page_start, page_end);
			blocks = current_page.split(sent_splitter);
			if (blocks.length>0){
				iCur =0;
			}else iCur = -1;
			page_no++;
			return true;
		}
		return false;
	}

	public boolean previous_page(){
		if (page_start-page_size >= 0){
			page_start -= page_size;
			int page_end = page_start+page_size<text.length()? page_start+page_size:text.length();
			current_page = text.substring(page_start, page_end);
			blocks = current_page.split(sent_splitter);
			if (blocks.length>0){
				iCur =0;
			}else iCur = -1;
			page_no--;
			return true;
		}
		return false;
	}

	public int get_page_no (){
		 return page_no;
	}
	
	public String toString (){
		return text;
	}
	
	public String get_current_block(){
		if (iCur>=0){
			return blocks[iCur];
		}
		else return null;
	}

	public Locale get_current_block_lang(){
		if (iCur>=0){
			return language(blocks[iCur].charAt(0));
		}
		else return null;
	}
	
	public String get_current_page(){
		return current_page;
	}

	public String next_block(){
		if (iCur < blocks.length-1){
			 iCur++;
			 return blocks[iCur];
		}else return null;
	}

	public void reset_cursor(){
		if (blocks.length>0){
		  iCur = 0;
		}else{
			iCur = -1;
		}
	}




	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
