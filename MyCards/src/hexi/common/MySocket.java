package hexi.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class MySocket extends Thread {

	final String TAG = "MySocket";

	Handler mHandler;
	Socket mSocket=null;
	String mHost;
	int mPort;
	String mUri;
	
	void log(Object o){
		Message msg = new Message();
		msg.what = 1;
		msg.obj = o;
		mHandler.sendMessage(msg);
	}
	
	public MySocket(String host, int port, String uri, Handler handler){
		mHandler = handler;
		mHost = host;
		mPort = port;
		mUri = uri;
	}
	
	public void connect(){
		try{
			mSocket = new Socket(mHost, mPort);
			log("connected!");
		}catch(Exception e){
			log(e.getMessage());
		}
	}
	
	boolean process_line(String line){
		boolean end = false;
		if(line.isEmpty()){
			log("");
			end = true;
		}else{
			log(line);
		}
		return end;
	}
	
	@Override
	public void run(){
		if (mSocket == null){
			connect();
		}
		if (mSocket != null){
			try
	         {
	            InputStream inStream = mSocket.getInputStream();
	            //char[] ss = new char[4096];
	            //BufferedReader buf_rdr = new BufferedReader(new InputStreamReader(inStream,"UTF-8"));
	            //int len=0;
	            Scanner in = new Scanner(inStream);
	            OutputStream outStream = mSocket.getOutputStream();
	            PrintWriter out = new PrintWriter(outStream, true);
	            String header = "GET "+mUri+" HTTP/1.1\n"; 
	            header += "Accept: text/html,application/xhtml+xml,*/*\n";
	            header += "Accept-Language: zh-CN\n";
	            header += "User-Agent: Mozilla/5.0(Windows NT 6.1)\n";
	            header += "Accept-Encoding: utf-8\n";
	            header += "Host: "+mHost+":"+mPort+"\n";
	            header += "Connection: close\n";
	            out.println(header);
	            StringBuffer strBuffer = new StringBuffer();
	            boolean done = false;
	            while(in.hasNextLine()){
	            	String line = in.nextLine();
	            	done = process_line(line);
	            }
	            out.close();
	            in.close();
	            mSocket.close();
	            /*
	            while ((len=buf_rdr.read(ss))>0)
	            {
	            	strBuffer.append(ss, 0, len);
	            }*/
	            String result = strBuffer.toString();
	            log("received: "+result);
	         }catch(Exception e){
	        	 log(e.getMessage());
	         }
		}else{
			log("Can't create socket.");
		}
	}
	
	public void close(){
		if (mSocket != null){
			try{
				mSocket.close();
			}catch(IOException e){
				Log.d(TAG, e.getMessage());
			}
		}
	}
	
}
