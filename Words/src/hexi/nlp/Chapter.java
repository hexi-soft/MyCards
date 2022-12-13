package hexi.nlp;

import common.Log;
import common.SetUtil;

public class Chapter {

	private int id;
	private String title;
	private String content;
	private String bookId;
	private String memo;
	
	public Chapter(int id, String title, String content, String bookId, String memo) {
		this.id = id;
		this.title = title;
		this.content = content;
		this.bookId = bookId;
		this.memo = memo;
	}
	
	public String[] getWordList(){
		String[] wordList = new String[0];
		if (content.length()>0) {
			wordList = content.split("[^a-zA-Z-]");			
		}
		return wordList;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getBookId() {
		return bookId;
	}

	public void setBookId(String bookId) {
		this.bookId = bookId;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public SetUtil<String> getWords(){
		SetUtil<String> wordSet = new SetUtil<String>();
		String[] wordList = getWordList();
		for(String w : wordList) {
			wordSet.add(w);
		}
		return wordSet;
	}
	
	public static void main(String[] args) {
		String s = "I'm a very very big box.";
		Chapter c = new Chapter(1,"",s,"","");
		Log.p(c.getWords());

	}

}
