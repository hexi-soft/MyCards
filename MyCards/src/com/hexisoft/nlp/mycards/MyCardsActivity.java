package com.hexisoft.nlp.mycards;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.hexisoft.common.android.FileDialog;
import com.hexisoft.common.android.GoogleTTS;
import com.hexisoft.common.android.ICallable;
import com.hexisoft.common.android.MyMediaPlayer;
import com.hexisoft.nlp.base.NewWord;
import com.hexisoft.nlp.web.MyWebActivity;

import android.app.Fragment;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;



public class MyCardsActivity extends FragmentActivity implements MyListener,SensorEventListener{
	
	public static MyMediaPlayer player;
	public static boolean tts_stop = false;
	public static boolean tts_loop = false;
	public static GoogleTTS tts;
	public static boolean sensorSwitchOn = true;
	
	static ArrayList<NewWord> sNewWordList = new ArrayList<NewWord>();
	static int sCurrentNewWordIndex = 0;
	public static final int MOTION_SENSOR_GATE = 1000;
	public static final int MOTION_SENSOR_GATE_MAX = 20000;
	static int sMotionSensorGate = MOTION_SENSOR_GATE;
	static float maxAcc =  (float)0.0;
	static int dim = 1;
	
	MySQLiteHelper mDB;    
	MyWebFragment fragment;
	
	@Override
	public void onSensorChanged(SensorEvent event) {
		if (sensorSwitchOn) {
			switch (event.sensor.getType()) {
			case Sensor.TYPE_ACCELEROMETER:
				float x = event.values[0];
				float y = event.values[1];
				float z = event.values[2];
				String accelerometer = "(" + x + ", " + y + ", " + z + ")\n";
				double acc = x * x + y * y + z * z;
				if (acc > sMotionSensorGate) {
					sMotionSensorGate = 20000;
					accelerometer += "R: " + acc;
					maxAcc = x; dim = 1;
					if (y>maxAcc) {
						maxAcc = y;
						dim = 2;
					}
					if (z>maxAcc) {
						maxAcc = z;
						dim = 3;
					}
					l(accelerometer+" dim: "+dim);

					NewWord w = null;
					if (dim == 1) {
						sCurrentNewWordIndex = --sCurrentNewWordIndex > 0 ? sCurrentNewWordIndex : 0;
						w = sNewWordList.get(sCurrentNewWordIndex);
					}else {
						sCurrentNewWordIndex = (++sCurrentNewWordIndex)%sNewWordList.size();
						w = sNewWordList.get(sCurrentNewWordIndex);
					}
					if (w != null) {
						tts = ((MyApp) getApplication()).tts;
						String word = w.get_word();
						String explains = w.get_explains();
						String toSpeakText = word + " " + explains;
						tts.speak(toSpeakText, 2, new ICallable() {
							public void callback(int cause) {
								if (cause == GoogleTTS.TTS_SPEAK_END) {
									sMotionSensorGate = MOTION_SENSOR_GATE;
								}
							}
						}, true);
					} else {
						sMotionSensorGate = MOTION_SENSOR_GATE;
					}
				}
				break;
			}
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {

	}
	
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        
    	if ((keyCode == KeyEvent.KEYCODE_BACK) && fragment.mWebView.canGoBack()) {   

    	       fragment.mWebView.goBack();   

    	           return true;   

    	       }else 

    	       return super.onKeyDown(keyCode, event);   

    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDB = new MySQLiteHelper(this, "txtkbase", null);        
        sNewWordList = mDB.get_special_new_words("");
        tts = ((MyApp)getApplicationContext()).tts;
        player = new MyMediaPlayer(this);
        player.play(R.raw.pass);
        ((MyApp)getApplicationContext()).init_tts();

        
        setContentView(R.layout.activity_my_cards);
        if (savedInstanceState == null) {
       		String indexPath = "file://"+((MyApp)getApplicationContext()).data_dir+"index2.htm";
        	//String indexPath = "file:///android_asset/index2.htm";
       		Log.d("My", indexPath);
       		fragment = MyWebFragment.newInstance(indexPath,true);
        	getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, fragment)
                    .commit();
        }
        
        SensorManager sensorManager = (SensorManager) getSystemService (SENSOR_SERVICE);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
        		SensorManager.SENSOR_DELAY_NORMAL);
        List<Sensor> sensors = sensorManager.getSensorList(Sensor.TYPE_ALL);
        //MyLog.display_long(this, sensors.toString());
        String s = sensors.toString()+"\r\n";
        for(Sensor sensor : sensors){
        	s += (sensor.getName()+ "\r\n");
        }
        //MyLog.f("Log:"+s);

   }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my_cards, menu);
        return true;
    }
	
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        FragmentManager fm = null;
        switch (id) {
        case R.id.action_syn:
        	fm = getSupportFragmentManager();
        	SynchronizeDialogFragment dialog = new SynchronizeDialogFragment();
        	dialog.show(fm, "synchronize");
        	return true;
        case R.id.action_settings:
        	fm = getSupportFragmentManager();
        	SettingsDialogFragment settings_dialog = new SettingsDialogFragment(this);
        	settings_dialog.show(fm, "settings");
            return true;
        case R.id.action_open:
        	//String rootDir = Environment.getExternalStorageDirectory() + "/my_library/";
        	String rootDir = ((MyApp)getApplicationContext()).data_dir+"my_library/";
    		File mPath = new File(rootDir);
    	    FileDialog fileDialog = new FileDialog(this, mPath, null);
    	    fileDialog.addFileListener(new FileDialog.FileSelectedListener() {
    	        public void fileSelected(File file) {
    	            String path = file.toString();
    	            String ext = path.substring(path.lastIndexOf(".")+1).toLowerCase();
    	            Intent i = null;
    	            if (ext.equals("txt")){
    	            	i = new Intent(MyCardsActivity.this, CardPagerActivity.class);
    	            	i.setAction(CardPagerActivity.ACTION_PAGE_FILE);
    	            	i.putExtra(CardPagerActivity.DATA_FILE_PATH, path);
    	            }else if(ext.equals("mp4")||ext.equals("flv")){
    	            	i = new Intent();
    	            	i.setAction(Intent.ACTION_VIEW);
    	            	String type = "video/*";
    	            	Uri uri = Uri.parse(path);
    	            	i.setDataAndType(uri,type);
    	            }else if(ext.equals("mp3")||ext.equals("wav")){
    	            	i = new Intent(MyCardsActivity.this, AudioWebActivity.class);
    	            	i.putExtra("FILE_PATH", path);
    	            	//player.play(path);
    	            	//return;
    	            	//ComponentName comp = new ComponentName("com.android.music","com.android.music.MusicBrowserActivity");
    	            	/*
    	            	String extension = MimeTypeMap.getFileExtensionFromUrl(path);
    	            	String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
    	            	Log.d("My", mimeType);
    	            	i = new Intent(Intent.ACTION_VIEW);
    	            	//i = new Intent(MediaStore.INTENT_ACTION_MUSIC_PLAYER);
    	                //i.setComponent(comp);
    	            	i.setDataAndType(Uri.parse(path), mimeType);*/
    	            }
    	            startActivity(i);
    	        }
    	    });
    	    //fileDialog.addDirectoryListener(new FileDialog.DirectorySelectedListener() {
    	    //  public void directorySelected(File directory) {
    	    //      Log.d(getClass().getName(), "selected dir " + directory.toString());
    	    //  }
    	    //});
    	    //fileDialog.setSelectDirectoryOption(false);
    	    fileDialog.showDialog();
        	return true;
        case R.id.menu_item_my_web:
        	Intent i = new Intent(MyCardsActivity.this, MyWebActivity.class);
            startActivity(i);
            return true;
        case R.id.menu_item_stop_read:
        	tts_stop = !tts_stop;
        	sensorSwitchOn = !sensorSwitchOn;
        	sMotionSensorGate = MOTION_SENSOR_GATE;
        	if (tts_stop){       		
        		tts.stop();
        	}
        	return true;
        case R.id.menu_item_init_tts:
        	((MyApp)getApplicationContext())
        	.tts.initialize(new ICallable(){
        		public void callback(int cause){
        			if (cause==GoogleTTS.TTS_INIT_OK){
    					GoogleTTS tts = ((MyApp)getApplicationContext()).tts;
        				tts.speak("TTS¾ÍÐ÷",2,null,true);
        				Toast.makeText(MyCardsActivity.this, "Initializing tts ok", Toast.LENGTH_SHORT).show();
        			}
        		}
        	});
        	return true;
        case R.id.action_select:
        	fm = getSupportFragmentManager();
        	CardPickerFragment dialog_picker = CardPickerFragment.newInstance("select * from cards");
        	dialog_picker.setTargetFragment(fragment, MyWebFragment.REQUEST_CARD);
        	dialog_picker.show(fm, "card_picker");
        	return true;
        	
        }
        return super.onOptionsItemSelected(item);
    }
    
    public void l(String s){
    	fragment.log(s);
    }
    
    public void d(String s){
    	fragment.trace(s);
    }
    
    @Override
    protected void onStart(){
    	super.onStart();
    }
    
    @Override
    protected void onStop(){
    	super.onStop();
    }
    @Override
    protected void onResume(){
    	super.onResume();
    }

    @Override
    protected void onDestroy() {
        player.play(R.raw.drop);
        if (mDB != null){
        	mDB.close();
        }
        super.onDestroy();
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }
        
        String mStr = "";
        TextView tv;
        
        Handler mHandler = new Handler() {
  	      public void handleMessage(Message msg) {
  	          if (msg.what == 0) {
  	             tv.setText(mStr);
  	             //sendEmptyMessageDelayed(0, 100);
  	          }
  	       }
        };

        public void trace(String s){
        	mStr += s +"\n";
        	mHandler.sendEmptyMessageDelayed(0, 100);
        }
        
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_my_cards, container, false);
            tv = (TextView)rootView.findViewById(R.id.text_view_root);
            
            return rootView;
        }
    }
    
	public void set_data(Bundle options){
		int play_mode = options.getInt("play_mode");
		if (play_mode == SettingsDialogFragment.REPEAT_CURRENT_PAGE_PLAY_MODE){
			tts_loop = true;
			player.play(R.raw.pass);
		}else{
			tts_loop = false;
		}
	}

}
