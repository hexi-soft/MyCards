package com.hexisoft.common.android;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;
import android.widget.Toast;
import hexi.common.Pair;
import hexi.common.UFileWriter;

public class MyLog {

	public static final int LOG_LEVEL_DEBUG = 1;
	public static final int LOG_LEVEL_INF0 = 2;
	public static final int LOG_LEVEL_WARN = 3;
	public static final int LOG_LEVEL_ERROR = 4;
	public static final int LOG_LEVEL_FATAL = 5;
	
	static Handler sHandler = new Handler() {
 	    public void handleMessage(Message msg) {
  	       if (msg.what == 0) {
  	          sendEmptyMessageDelayed(0, 100);
  	       }else if(msg.what == LOG_LEVEL_WARN){  	    	   
  	    	   if (msg.obj != null){
  	  	    	   @SuppressWarnings("unchecked")
				Pair<TextView,String> pcs = (Pair<TextView,String>)msg.obj; 
  	  	    	   TextView tv = pcs.getFirst();
  	  	    	   String message = tv.getText().toString();
  	  	    	   message += "WARN: ";
  	  	    	   message += pcs.getSecond()+"\n";
  	    		   tv.setText(message);
  	    	   }
  	       }
  	    }
     };
	
	Handler mHandler;
	Context mContext;
	
	public static void display_long(Context ctx, String str) {
		Toast.makeText(ctx, str, Toast.LENGTH_LONG).show();
	}
	
	public static void display_short(Context ctx, String str) {
		Toast.makeText(ctx, str, Toast.LENGTH_SHORT).show();
	}
	
	public MyLog(Handler handler){
		mHandler = handler;
	}
	
	public MyLog(Context context){
		mContext = context;
	}
	
	public static void debug2(TextView v, String s){
		Message msg = new Message();
		msg.what = LOG_LEVEL_DEBUG;
		msg.obj = new Pair<TextView,String>(v,s);
		sHandler.sendMessage(msg);
	}
	
	public void d(Object o){
		if (mHandler == null){
			return;
		}
		Message msg = new Message();
		msg.what = LOG_LEVEL_DEBUG;
		msg.obj = o;
		mHandler.sendMessage(msg);
	}
	
	public void i(String msg){
		Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
	}
	
	public void w(Message msg){
		sHandler.sendMessage(msg);
	}
	
	public static void f(Object s) {
		UFileWriter.write(Environment.getExternalStorageDirectory()+"/log_my_cards.txt",s.toString());
	}
}
