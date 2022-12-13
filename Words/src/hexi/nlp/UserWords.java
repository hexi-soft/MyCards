package hexi.nlp;

import java.util.HashSet;

import hexi.nlp.Dao;

public class UserWords {

	private String uid;
	private String level;
	private HashSet<String> words_familiar;
	
	public UserWords(String uid, String level) {
		this.uid = uid;
		this.level = level;
		words_familiar = Dao.get_user_words(uid);
	}
	
	public HashSet<String> getFamiliarWords(){
		return words_familiar;
	}
	
	public void setFamiliarWords(HashSet<String> newValue) {
		words_familiar = newValue;
	}
	
	public String getUserId() {
		return uid;
	}
	
	public boolean is_word_familiar(String word) {
		return words_familiar.contains(word);
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
