package com.hexisoft.common.android;

import java.io.IOException;

import android.content.Context;
import android.media.MediaPlayer;

public class MyMediaPlayer {
	
	private MediaPlayer mPlayer;
	private Context c;
	
	public MyMediaPlayer(Context context){
		c = context;
	}

	public void stop() {
		if (mPlayer != null) {
			mPlayer.release();
			mPlayer = null;
		}
	}

	public void play(String path) {
		stop();
		mPlayer = new MediaPlayer();
		try{
			mPlayer.setDataSource(path);
			mPlayer.prepare();
			mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
				public void onCompletion(MediaPlayer mp) {
					stop();
				}
			});
			mPlayer.start();
		}catch(IOException e){
			;
		}
		
	}
	
	public void play(int r) {
		stop();
		mPlayer = MediaPlayer.create(c, r);
		mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
			public void onCompletion(MediaPlayer mp) {
				stop();
			}
		});
		mPlayer.start();
	}
}
