package com.hexisoft.nlp.mycards;

import java.util.ArrayList;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import hexi.common.UFileReader;

@SuppressLint("SetJavaScriptEnabled") 

public class AudioWebActivity extends Activity{
	
	WebView mWebView;
	String mAudioURL, mLrcString;
	
	void setUpWebContent(String audioURL){
		//mWebView.loadDataWithBaseURL("file:///android_asset/", html, "text/html", "UTF-16", null);
		mWebView.loadUrl("file:///android_asset/audio.htm");
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_audio_web);
		mWebView = (WebView)findViewById(R.id.webBrowser);
		mWebView.setBackgroundColor(0); // …Ë÷√±≥æ∞…´
		mWebView.getBackground().setAlpha(127); // …Ë÷√ÃÓ≥‰Õ∏√˜∂» ∑∂Œß£∫0-255
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.addJavascriptInterface(new Object(){
			@JavascriptInterface
			public String load_lrc() {
				return mLrcString;
			}
			
			@JavascriptInterface
			public String get_audioURL() {
				return mAudioURL;
			}
		
		}, "myAndroid");
		
		String filepath = getIntent().getStringExtra("FILE_PATH");
		mAudioURL = "file://"+filepath;
		String lrcPath = filepath.substring(0, filepath.lastIndexOf('.'))+".lrc";
		Log.i("My", mAudioURL+" "+lrcPath);
		mLrcString = UFileReader.read(lrcPath);
		
		setUpWebContent(mAudioURL);
	}

}
