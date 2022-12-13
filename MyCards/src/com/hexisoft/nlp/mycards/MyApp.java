package com.hexisoft.nlp.mycards;

import com.hexisoft.common.android.GoogleTTS;
import com.hexisoft.common.android.ICallable;

import android.app.Application;
import android.os.Environment;

public class MyApp extends Application {
	public GoogleTTS tts;
	public String data_dir;
	
	public MyApp(){
		data_dir = Environment.getExternalStorageDirectory() + "/";
	}
	
	public void init_tts(){
	    tts = GoogleTTS.get(this, new ICallable(){
        	public void callback(int cause){
        		if (cause == GoogleTTS.TTS_INIT_OK){
        			tts.speak("TTS¾ÍÐ÷", 2, null, true);
        		}
        	}
        });
    	
	}
	
}
