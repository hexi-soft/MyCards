package com.hexisoft.nlp.myservice;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import com.hexisoft.common.android.GoogleTTS;

import com.hexisoft.common.android.IMyService;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

public class MyIntentService extends IntentService {
	private static final String TAG = "MyIntentService";
	static final int TIME_INTERVAL=3000;
	GoogleTTS mTTS = null;
	
	public class MyServiceImpl extends IMyService.Stub{
		@Override
		public void speak(String s) throws RemoteException
		{
			if (mTTS != null){
				mTTS.speak(s, 2, null, true);
			}
		}
	}
	
	public static boolean isServiceAlarmOn(Context c){
		Intent i = new Intent(c, MyIntentService.class);
		PendingIntent pi = PendingIntent.getService(c, 0, i, PendingIntent.FLAG_NO_CREATE);
		return pi != null;
	}
	
	public static void setServiceAlarm(Context c, boolean isOn, Messenger messenger){
		mMessenger = messenger;
		Intent i = new Intent(c, MyIntentService.class);
		PendingIntent pi = PendingIntent.getService(c, 0, i, 0);
		AlarmManager alarmManager = (AlarmManager)c.getSystemService(c.ALARM_SERVICE);
		if (isOn){
			alarmManager.setRepeating(AlarmManager.RTC, System.currentTimeMillis(), TIME_INTERVAL, pi);
		}else{
			alarmManager.cancel(pi);
			pi.cancel();
		}
	}
	
	static Messenger mMessenger;
	ServerSocket s = null;
	public MyIntentService() {
		super(TAG);
	}

	public static void print(Object o){
		Message msg = new Message();
		msg.what = 1;
		msg.obj = o;
		try{
			mMessenger.send(msg);
		}catch(Exception e){
			Log.d("My", e.getMessage());
		}
	}
	
	public void startNetService(){
		try{
			s = new ServerSocket(8189);
			boolean serve = true;
			while(serve)
			{
				print("Waiting for clients...");
				Socket comer = s.accept();
				print("Got a client!");
				Runnable r = new NetServiceThreadHandler(comer, mMessenger);
				Thread t = new Thread(r);
				t.start();
			}
			s.close();
		}catch(IOException e){
			print(e.getMessage());
		}
	}
	
	@Override
	public void onCreate(){
		super.onCreate();
		mTTS = GoogleTTS.get(this, null);
		print("on create.");
	}
	
	@Override
	protected void onHandleIntent(Intent intent){
		//mMessenger = (Messenger)intent.getExtras().get("HANDLER");
		mTTS.speak("O", 2, null, true);
		print("O");
		/*
		ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
		boolean isNetworkAvailable = cm.getBackgroundDataSetting() && cm.getActiveNetworkInfo() != null;
		if (!isNetworkAvailable){
			print("The network is not available.");
			return;
		}
		String ip = MyServer.getIP();
		print(ip);
		startNetService();*/
		
		//try{
		//}catch(InterruptedException e){
			//e.printStackTrace();
		//}
	}
	
	@Override
	public void onDestroy(){
		try{
			if (mTTS != null){
				//mTTS.shutdown();
				print("mTTS is not null.");
			}
			if (s != null){
				s.close();
			}
		}catch(IOException e){
			print(e.getMessage());
		}
		print("After onHandleIntent is done this wil be called automatically!");
		super.onDestroy();
	}
}
