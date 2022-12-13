package com.hexisoft.nlp.mycards;

import com.hexisoft.common.android.GoogleTTS;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MyBroadcastReceiver extends BroadcastReceiver{
	@Override
	public void onReceive(Context c, Intent i){
		Log.d("My", "o");
		GoogleTTS.get(c,null).speak("o", 2, null, true);
	}

}
