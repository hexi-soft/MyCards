package com.hexisoft.common.android;

import java.util.HashMap;
import java.util.Locale;
import java.util.UUID;

import android.annotation.SuppressLint;
import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;

class TtsUtteranceListener extends UtteranceProgressListener {

	public void onDone(String utteranceId) {
		if (ctx != null && utteranceId.equals(id)){
			ctx.callback(2);
		}
   }

	public void onStart(String utteranceId) {
        
	}
	
	public void onError(String utteranceId) {

    }
	
	public ICallable ctx=null;
	public String id = null;
	
	public TtsUtteranceListener(ICallable context, String id){
		ctx = context;
		this.id = id;
	}
}

class TTSListener implements OnInitListener {
	
	private ICallable ctx=null;
	
	public TTSListener(ICallable context){
		ctx = context;
	}
	
	String TAG = "O";
    @SuppressLint("NewApi") @Override
    public void onInit(int status) {
        // TODO Auto-generated method stub
        if (status == TextToSpeech.SUCCESS) {
            Log.i(TAG, "onInit: TTS引擎初始化成功");
            if (ctx != null){
            	ctx.callback(GoogleTTS.TTS_INIT_OK);
            }
        }
        else{
            Log.i(TAG, "onInit: TTS引擎初始化失败");
        }
    }
}

public class GoogleTTS {

	public static final int TTS_INIT_OK = 1;
	public static final int TTS_SPEAK_END = 2;
	/**
	 * @param args
	 */
	private static GoogleTTS sTTS;
	
	public static GoogleTTS get(Context c, ICallable callback){
		if (sTTS == null){
			sTTS = new GoogleTTS(c, callback);
		}
		return sTTS;
	}

	private Context ctx;
	public TextToSpeech mSpeech = null;
	public boolean bStop = false;
	private float speed = (float)0.0;
	public TtsUtteranceListener mListener = null;
	public static Locale LOCALE_NUMBER = Locale.US;
	public static Locale LOCALE_PUNCTUATION = Locale.US;
	
	private GoogleTTS(Context c, ICallable callback){
		ctx = c;
		mSpeech = new TextToSpeech(ctx, new TTSListener(callback),"com.google.android.tts");
	}
	
	public void initialize(ICallable callback){
		shutdown();
		speed = (float)0.0;
		mSpeech = new TextToSpeech(ctx, new TTSListener(callback), "com.google.android.tts");
	}
	
	public void speed_up(){
		speed += 1.0;
	}
	
	public void speed_down(){
		if (speed>=1.0){
		 speed -= 1.0;
		}
	}
	
	public void stop(){
		if (mSpeech != null){
			addListener(null, null);
			mSpeech.stop();
		}
	}
	
	public void shutdown(){
		if (mSpeech != null){
			mSpeech.stop();
			mSpeech.shutdown();
			mSpeech = null;
		}
	}
	
	@SuppressLint("NewApi")
	public void addListener(ICallable onend, String id){
		if (mSpeech != null){
			mListener = new TtsUtteranceListener(onend, id);
			mSpeech.setOnUtteranceProgressListener(mListener);
		}else{
			initialize(null);
			mListener = new TtsUtteranceListener(onend, id);
			mSpeech.setOnUtteranceProgressListener(mListener);
		}
	}
	
	public void speak(String s, Locale lang, float rate, String id){
		if(s.length()==0){
			return;
		}
		int supported = mSpeech.setLanguage(lang);
		if(lang==Locale.CHINESE){
			mSpeech.setSpeechRate(rate+1+speed);
		}else{
			mSpeech.setSpeechRate(rate+speed);
		}
		HashMap<String,String> kv = new HashMap<String,String>();
		kv.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, id);
		mSpeech.speak(s, TextToSpeech.QUEUE_ADD, kv);
	}
	
	public static Locale language(char c){
		Locale r = Locale.US;
		if(c>=48 && c<58){
			return LOCALE_NUMBER;
		}
		if(c>32 && c<48 || c>57 && c<65 || c>90 && c<97 || c>122 && c<127){
			return LOCALE_PUNCTUATION;
		}
		if(c>0 && c<255 || c==8217 || c==8221 || c==8220){
			r = Locale.US;
		}else{
			r = Locale.CHINESE;
		}
		return r;
	}
	
	public void speakE(String s, float rate, ICallable onend, boolean bFlush){
		bStop = false;
		if (bFlush){
			mSpeech.speak("", TextToSpeech.QUEUE_FLUSH, null);
		}
		String id = UUID.randomUUID().toString();
		addListener (onend, id);
		if (s.trim().length()>0){
			speak(s,Locale.US, rate, id);		
		}else{
			if(onend != null){
				onend.callback(TTS_SPEAK_END);
			}
		}
	}
	
	public void speak(String s, float rate, ICallable onend, boolean bFlush){
		bStop = false;
		if (bFlush){
			mSpeech.speak("", TextToSpeech.QUEUE_FLUSH, null);
		}
		String id = UUID.randomUUID().toString();
		addListener (onend, id);
		s = s.trim();
		if(s.length()>0){
			Locale l = language(s.charAt(0));
			int start = 0;
			int length = s.length();
			String ss = "";
			for(int i=0; i<length; ++i){
				Locale lang = language(s.charAt(i));
				if(lang != l){
					ss = s.substring(start, i);
					if(ss.trim().length()>0){
						if(l==Locale.CHINESE){
							mSpeech.setSpeechRate(rate+1+speed);
						}else{
							mSpeech.setSpeechRate(rate+speed);
						}
						mSpeech.setLanguage(l);
						mSpeech.speak(ss, TextToSpeech.QUEUE_ADD, null);
						//Log.d("My", "speak without id: "+ss);
					}
					start = i;
					l = lang;
				}
				LOCALE_NUMBER = l;
				LOCALE_PUNCTUATION = l;
			}
			ss = s.substring(start);
			if(ss.trim().length()>0){	
				speak(ss,l, rate, id);
				//Log.d("My", "speak with id: "+ss);
			}
		}else{
			if(onend != null){
				onend.callback(TTS_SPEAK_END);
			}
		}
	}
}
