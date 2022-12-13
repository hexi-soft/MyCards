package hexi.nlp.test;

import java.util.HashMap;

import common.Log;
import common.Pair;

public class MyRobot {
	
	public static HashMap<String,Executor> executors = new HashMap<String,Executor>();
	public static String sCurrentUser = "";
	
	public MyRobot() {
		
	}
	
	public void test(String yourWords) {
		String res = talkBack(yourWords);
		System.out.println(res);
	}

	public String talkBack(String words) {
		String talkBack = "Hi!";
		if (words != null && !words.isEmpty()) {
			Pair<Intent,String> res = IntentClassifier.classify(words);
			Intent intent = res.getFirst();
			String response = res.getSecond();
			switch (intent) {
			case greet:
				talkBack = "Hi! ";
				break;
			case self_intro:
				talkBack = "Nice to meet You! ";
				break;
			case create_fact:
				talkBack = "Very good. ";
				break;
			case create_my_fact:
				talkBack = "Are you serious? ";
				break;
			case bye:
				talkBack = "Goodbye! ";
				break;
			default:
				break;
			}
			talkBack += response;
		}		
		Log.l("talkBack:"+talkBack);
		return talkBack;
	}
	
	public String talkBack(String uid, String words) {
		sCurrentUser = uid;
		if (executors.get(uid)==null) {
			executors.put(uid, new Executor());
		}
		String talkBack = "Hi!";
		if (words != null && !words.isEmpty()) {
			Pair<Intent,String> res = IntentClassifier.classify(uid, words);
			Intent intent = res.getFirst();
			String response = res.getSecond();
			switch (intent) {
			case greet:
				talkBack = "Hi! ";
				break;
			case self_intro:
				talkBack = "Nice to meet You! ";
				break;
			case create_fact:
				talkBack = "Very good. ";
				break;
			case create_my_fact:
				talkBack = "Are you serious? ";
				break;
			case bye:
				talkBack = "Goodbye! ";
				break;
			default:
				break;
			}
			talkBack += response;
		}		
		Log.l("talkBack:"+talkBack);
		return talkBack;
	}
	
	public static void main(String[] args) {
		MyRobot myRobot = new MyRobot();
		myRobot.test("I'm Patrick.");
		myRobot.test("I live in Langzhong.");
		myRobot.test("I live in Guangzhou.");
		//myRobot.test("I want ten big red apples.");
	}
}
