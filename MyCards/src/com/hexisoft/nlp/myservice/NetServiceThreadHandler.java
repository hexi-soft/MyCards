package com.hexisoft.nlp.myservice;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import android.os.Message;
import android.os.Messenger;
import android.util.Log;

public class NetServiceThreadHandler implements Runnable
{
	static Messenger mMessenger;
	private Socket comer;
	private PrintWriter out;
	
	public NetServiceThreadHandler(Socket i, Messenger messenger)
	{
		comer = i;
		mMessenger = messenger;
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
	
	public void run() {
		try {
			Scanner in = null;
			try {
				InputStream inStream = comer.getInputStream();
				OutputStream outStream = comer.getOutputStream();
				in = new Scanner(inStream);
				out = new PrintWriter(outStream, true/* autoFlush */);
				String r = "HTTP/1.1 200 OK\n";
				r += "Server: MyServer\n";
				r += "Cache-Control: no-cache\n";
				r += "Content-Type: application/json;charset=UTF-8\n";
				r += "Content-Length: "+"0\n";
				r += "Date: Sun, 17 Mar 2019 04:02:28 GMT\n";
				r += "Connection: close\n";
				out.println(r);
				boolean done = false;
				while (!done && in.hasNextLine()) {
					String line = in.nextLine();
					if (line.isEmpty()){
						done = true;
					}else{
						print(line);
					}
				}
			}finally {
				comer.close();
				if (in != null){
					in.close();
				}
				print("Client is gone! "+comer+" closed.");
			}
		}catch (Exception e) {
			print(e.getMessage());
		}
	}
}

