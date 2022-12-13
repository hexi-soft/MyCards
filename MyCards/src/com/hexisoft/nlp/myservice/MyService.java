package com.hexisoft.nlp.myservice;

import com.hexisoft.common.android.GoogleTTS;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

public class MyService extends Service {
	static Messenger sMessenger;
	public static void print(Object o){
		Message msg = new Message();
		msg.what = 1;
		msg.obj = o;
		try{
			sMessenger.send(msg);
		}catch(Exception e){
			Log.d("My", e.getMessage());
		}
	}
	
	private final String TAG = "MyService";
	GoogleTTS mTTS;
		
	@Override
	public void onCreate(){
		super.onCreate();
		Log.d("My","on create");
		print("on create.");
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		//mTTS.shutdown();
		print("on destroy.");
	}
	
	@Override
	public void onStart(Intent intent, int startId){
		super.onStart(intent, startId);
		//mTTS = GoogleTTS.get(this, null);
		print("on start.");
	}
	
	@Override
	public IBinder onBind(Intent arg0){
		print("on bind.");
		return null;
	}

}
