package com.hexisoft.nlp.mycards;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import com.hexisoft.common.android.FileDialog;
import com.hexisoft.common.android.GoogleTTS;
import com.hexisoft.common.android.ICallable;
import com.hexisoft.common.android.MyLog;
import com.hexisoft.nlp.base.NewWord;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class CardPagerActivity extends FragmentActivity implements MyListener,SensorEventListener{

	boolean sensorSwitchOn = true;
	
	//private static SQLiteDatabase db = MyCardsActivity.db;
	private MySQLiteHelper mDB;
	private SQLiteDatabase db;
	private ViewPager vp;
	private ArrayList<Card> mCards;
	private Text mText=null;
	private String mStr2read;
	private boolean mAutoPlay = true;
	
	private String mFront="";
	private String mBack="";
	private String mBook="";
	private int mUnit= -2;
	private String mMemo="";
	private String mImg="";
	
	String mExtra_params;
	public static String ACTION_WORD_SQL = "action.word.sql";
	public static String DATA_WORD_BOOK = "data.word.book";
	public static String DATA_WORD_UNIT = "data.word.unit";
	public static String ACTION_WORD_SET = "action.word.set";
	public static String ACTION_NEW_WORD_SET = "action.new_word.set";
	public static String ACTION_SENT_SQL = "action.sent.sql";
	public static String ACTION_PAGE_FILE = "action.page.file";
	public static String DATA_WORD_SQL = "data.word.sql";
	public static String DATA_WORD_SET = "data.word.set";
	public static String DATA_NEW_WORD_SET = "data.new.word.set";
	public static String DATA_SENT_SQL = "data.sent.sql";
	public static String DATA_FILE_PATH = "data.file.path";
	
	@Override
	public void onSensorChanged(SensorEvent event) {
		if (sensorSwitchOn) {
			switch (event.sensor.getType()) {
			case Sensor.TYPE_ACCELEROMETER:
				float x = event.values[0];
				float y = event.values[1];
				float z = event.values[2];
				double acc = x * x + y * y + z * z;
				if (acc > MyCardsActivity.MOTION_SENSOR_GATE) {
					sensorSwitchOn = false;
					Timer timer = new Timer();
					timer.schedule(new TimerTask() {
						@Override
						public void run() {
							sensorSwitchOn = true;
						}
					}, 3000);
					flip_read_status();
				}
				break;
			}
		}
	}
	
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {

	}
	
	public void set_card_front_status(String s_front){
		mFront = s_front;
	}
	public void set_card_back_status(String s_back){
		mBack = s_back;
	}
	public void set_card_img_status(String s_img){
		mImg = s_img;
	}
	
	private String get_extra_params(){
		mExtra_params = "";
		if (!mFront.isEmpty()){
			mExtra_params += "&s_front="+mFront;
		}
		if (!mBack.isEmpty()){
			mExtra_params += "&s_back="+mBack;
		}
		if (!mBook.isEmpty()){
			mExtra_params += "&book="+mBook;
		}
		if (mUnit>-2){
			mExtra_params += "&unit="+mUnit;
		}
		if (!mImg.isEmpty()){
			mExtra_params += "&s_img="+mImg;
		}
		return mExtra_params;
	}
		
	Handler mHandler = new Handler() {
 	    public void handleMessage(Message msg) {
 	       if (msg.what == 0) {
 	    	  int current_item = vp.getCurrentItem();
				int count = vp.getAdapter().getCount();
				if (current_item < count-1){
					current_item++;
					vp.setCurrentItem(current_item, true);
				}
 	          //sendEmptyMessageDelayed(0, 100);
 	       }else if(msg.what == 1){
				textSpeak();
 	       }
 	    }
    };
    
    @Override
    public void onDestroy(){
    	GoogleTTS tts = ((MyApp)getApplicationContext()).tts;
    	if (tts != null){
    		tts.stop();
    	}
    	if (mDB != null) {
    		mDB.close();
    	}
    	super.onDestroy();
    }
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		 SensorManager sensorManager = (SensorManager) getSystemService (SENSOR_SERVICE);
	        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
	        		SensorManager.SENSOR_DELAY_NORMAL);
		mDB = new MySQLiteHelper(this, "txtkbase", null);
        db = mDB.getReadableDatabase();
		vp = new ViewPager(this);
		vp.setId(R.id.viewPager);
		setContentView(vp);
		mStr2read = "";
		Intent i = getIntent();
		String action = i.getAction();
		//action = ACTION_NEW_WORD_SET;
		MyLog.f("action:"+action);
		if (action.equals(ACTION_WORD_SQL)){
			String sql = i.getStringExtra(DATA_WORD_SQL);
			if (sql != null){
				mBook = i.getStringExtra(DATA_WORD_BOOK);
				mUnit = i.getIntExtra(DATA_WORD_UNIT,1);
				init_word_cards_by_sql(sql);
			}
		}else if(action.equals(ACTION_WORD_SET)){
			ArrayList<Map<String,Object>> word_freq_list = (ArrayList<Map<String,Object>>)i.getSerializableExtra(DATA_WORD_SET);
			if (word_freq_list != null){
				init_word_cards_by_set(word_freq_list);
			}
			
		}else if(action.equals(ACTION_NEW_WORD_SET)){
			MyLog.f("Got new words");
			ArrayList<NewWord> new_words = (ArrayList<NewWord>)i.getSerializableExtra(DATA_NEW_WORD_SET);
			if (new_words != null) {
				MyLog.f("Got new words"+" initing...");
				init_word_cards_by_new_word_set(new_words);
			}
		}
		
		else if(action.equals(ACTION_PAGE_FILE)){
			String path = i.getStringExtra(DATA_FILE_PATH);
			if(path != null){
				init_page_cards_by_path(path);
			}
		}else if(action.equals(ACTION_SENT_SQL)){
				String sql = i.getStringExtra(DATA_SENT_SQL);
				if (sql != null){
				init_sent_cards_by_sql(sql);
			}
		}
	}
	
	private void init_page_cards_by_path(String filepath){
		mStr2read = null;
		CardLab cardLab = CardLab.get_page(this, filepath);
		mCards = cardLab.getCards();
		if (mCards.size()>0){
			Card card = mCards.get(0);
			mStr2read = card.getFront();
			mStr2read += "\n"+card.getBack();
			mText = new Text(mStr2read, CardLab.get_text_page_size());
			mHandler.sendEmptyMessageDelayed(1, 100);	
		}
		FragmentManager fm = getSupportFragmentManager();
		vp.setAdapter(new FragmentStatePagerAdapter(fm) {
			@Override
			public int getCount() {
				return 100000000;
				//return mCards.size();
			}

			@Override
			public Fragment getItem(int pos) {
				Card card = mCards.get(pos % mCards.size());
				return MyWebFragment.newInstance(card.getUrl(), false);
			}
		});
		vp.setOnPageChangeListener(new ViewPager.OnPageChangeListener(){

			@Override
			public void onPageScrollStateChanged(int state) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

			}

			@Override
			public void onPageSelected(int position) {
				// TODO Auto-generated method stub
				Card card = mCards.get(position % mCards.size());
				mStr2read = card.getFront();
				mStr2read += "\n"+card.getBack();
				mText = new Text(mStr2read, CardLab.get_text_page_size());
				if (mAutoPlay){
					mHandler.sendEmptyMessageDelayed(1, 100);
				}
			}
			
		});
		if (mCards.size()>0){
			vp.setCurrentItem(mCards.size()*100);
		}
	}

	private void read_card(boolean bAutoRead) {
		GoogleTTS tts = ((MyApp)getApplicationContext()).tts;
		if (bAutoRead) {
			tts.speak(mStr2read, 1, new ICallable() {
				public void callback(int cause) {
					if (cause == GoogleTTS.TTS_SPEAK_END && mAutoPlay) {
						mHandler.sendEmptyMessageDelayed(0, 100);
					}
				}
			}, true);
		} else {
			tts.speak(mStr2read, 1, null, true);
		}
	}

	public void init_word_cards_by_sql(String sql){
		mText = null;
		CardLab cardLab = CardLab.get_word(this,  sql, db);
		mCards = cardLab.getCards();
		if (mCards.size()>0){
			Card card = mCards.get(0);
			mStr2read = "";
			if(!mFront.equals("hidden")){
				mStr2read += card.getFront();
			}
			if (!mBack.equals("hidden")){
				mStr2read += "\n"+card.getBack();
			}
			read_card(mAutoPlay);
		}else{
			return;
		}
		FragmentManager fm = getSupportFragmentManager();
		vp.setAdapter(new FragmentStatePagerAdapter(fm) {
			@Override
			public int getCount() {
				return 100000000;
				//return mCards.size();
			}

			@Override
			public Fragment getItem(int pos) {
				Card card = mCards.get(pos % mCards.size());
				return MyWebFragment.newInstance(card.getUrl()+get_extra_params(),false);
			}
			/*			
			@Override
		    public boolean isViewFromObject(View view, Object object) {
		        return view == ((Fragment)object).getView();
		    }
			
			@Override
		    public void destroyItem(ViewGroup container, int position, Object object) {
				Card card = mCards.get(position % mCards.size());
				View view = MyWebFragment.newInstance(card.getUrl(),false).getView();
		        container.removeView(view);
		    }

			@Override
			 public Object instantiateItem(View container, int position) { 
				Card card = mCards.get(position % mCards.size());
			    View view = MyWebFragment.newInstance(card.getUrl(),false).getView();  
			    ((ViewPager)container).addView(view, position);    
			    return view;   
			}*/
		});
		vp.setOnPageChangeListener(new ViewPager.OnPageChangeListener(){

			@Override
			public void onPageScrollStateChanged(int state) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

			}

			@Override
			public void onPageSelected(int position) {
				// TODO Auto-generated method stub
				Card card = mCards.get(position % mCards.size());
				mStr2read = "";
				if(!mFront.equals("hidden")){
					mStr2read += card.getFront();
				}
				if (!mBack.equals("hidden")){
					mStr2read += "\n"+card.getBack();
				}
				read_card(mAutoPlay);
			}
		});
		if (mCards.size()>0){
			vp.setCurrentItem(mCards.size()*100);
		}
	}
	
	public void init_word_cards_by_set(ArrayList<Map<String,Object>> word_freq_list){
		mText = null;
		CardLab cardLab = CardLab.get_words(this, word_freq_list);
		mCards = cardLab.getCards();
		Log.d("My", "load cards: "+ mCards.size());
		if (mCards.size()>0){
			Card card = mCards.get(0);
			mStr2read = "";
			if(!mFront.equals("hidden")){
				mStr2read += card.getFront();
			}
			if (!mBack.equals("hidden")){
				mStr2read += "\n"+card.getBack();
			}
			read_card(mAutoPlay);
		}else{
			return;
		}
		FragmentManager fm = getSupportFragmentManager();
		vp.setAdapter(new FragmentStatePagerAdapter(fm) {
			@Override
			public int getCount() {
				return 100000000;
				//return mCards.size();
			}

			@Override
			public Fragment getItem(int pos) {
				Card card = mCards.get(pos % mCards.size());
				return MyWebFragment.newInstance(card.getUrl()+get_extra_params(),false);
			}
		});
		vp.setOnPageChangeListener(new ViewPager.OnPageChangeListener(){

			@Override
			public void onPageScrollStateChanged(int state) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

			}

			@Override
			public void onPageSelected(int position) {
				// TODO Auto-generated method stub
				Card card = mCards.get(position % mCards.size());
				mStr2read = "";
				if(!mFront.equals("hidden")){
					mStr2read += card.getFront();
				}
				if (!mBack.equals("hidden")){
					mStr2read += "\n"+card.getBack();
				}
				read_card(mAutoPlay);
			}
		});
		if (mCards.size()>0){
			vp.setCurrentItem(mCards.size()*100);
		}
	}
	
	public void init_word_cards_by_new_word_set(ArrayList<NewWord> new_words){
		mText = null;
		CardLab cardLab = CardLab.get_new_words(this, new_words);
		mCards = cardLab.getCards();
		Log.d("My", "load cards: "+ mCards.size());
		MyLog.f("Load new words cards: "+mCards.size());
		if (mCards.size()>0){
			Card card = mCards.get(0);
			mStr2read = "";
			if(!mFront.equals("hidden")){
				mStr2read += card.getFront();
			}
			if (!mBack.equals("hidden")){
				mStr2read += "\n"+card.getBack();
			}
			read_card(mAutoPlay);
		}else{
			return;
		}
		FragmentManager fm = getSupportFragmentManager();
		vp.setAdapter(new FragmentStatePagerAdapter(fm) {
			@Override
			public int getCount() {
				return 100000000;
				//return mCards.size();
			}

			@Override
			public Fragment getItem(int pos) {
				Card card = mCards.get(pos % mCards.size());
				return MyWebFragment.newInstance(card.getUrl()+get_extra_params(),false);
			}
		});
		vp.setOnPageChangeListener(new ViewPager.OnPageChangeListener(){

			@Override
			public void onPageScrollStateChanged(int state) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

			}

			@Override
			public void onPageSelected(int position) {
				// TODO Auto-generated method stub
				Card card = mCards.get(position % mCards.size());
				mStr2read = "";
				if(!mFront.equals("hidden")){
					mStr2read += card.getFront();
				}
				if (!mBack.equals("hidden")){
					mStr2read += "\n"+card.getBack();
				}
				read_card(mAutoPlay);
			}
		});
		if (mCards.size()>0){
			vp.setCurrentItem(mCards.size()*100);
		}
	}
	public void init_sent_cards_by_sql(String sql){
		mText = null;
		CardLab cardLab = CardLab.get_sent(this,  sql);
		mCards = cardLab.getCards();
		if (mCards.size()>0){
			Card card = mCards.get(0);
			mStr2read = card.getFront();
			mStr2read += "\n"+card.getBack();
			read_card(mAutoPlay);
		}else{
			return;
		}
		FragmentManager fm = getSupportFragmentManager();
		vp.setAdapter(new FragmentStatePagerAdapter(fm) {
			@Override
			public int getCount() {
				return 100000000;
				//return mCards.size();
			}

			@Override
			public Fragment getItem(int pos) {
				Card card = mCards.get(pos % mCards.size());
				return MyWebFragment.newInstance(card.getUrl(),false);
			}
		});
		vp.setOnPageChangeListener(new ViewPager.OnPageChangeListener(){

			@Override
			public void onPageScrollStateChanged(int state) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

			}

			@Override
			public void onPageSelected(int position) {
				// TODO Auto-generated method stub
				Card card = mCards.get(position % mCards.size());
				mStr2read = card.getFront();
				mStr2read += "\n"+card.getBack();
				read_card(mAutoPlay);
			}
		});
		if (mCards.size()>0){
			vp.setCurrentItem(mCards.size()*100);
		}
	}
	
	public void set_data(Bundle options){
		int play_mode = options.getInt("play_mode");
		if (play_mode == SettingsDialogFragment.REPEAT_CURRENT_PAGE_PLAY_MODE){
			MyCardsActivity.tts_loop = true;
		}else{
			MyCardsActivity.tts_loop = false;
		}
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.card_pager, menu);
        return true;
    }

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		GoogleTTS tts = ((MyApp)getApplicationContext()).tts;
		FragmentManager fm = null;
		switch (id) {
		case R.id.action_settings:
        	fm = getSupportFragmentManager();
        	SettingsDialogFragment settings_dialog = new SettingsDialogFragment(this);
        	settings_dialog.show(fm, "settings");
			return true;
		case R.id.action_open:
			//String rootDir = Environment.getExternalStorageDirectory() + "/1/";
			String rootDir = ((MyApp)getApplicationContext()).data_dir+"my_library";
			// fragment.trace("open "+rootDir);
			File mPath = new File(rootDir);
			FileDialog fileDialog = new FileDialog(this, mPath, ".txt");
			fileDialog.addFileListener(new FileDialog.FileSelectedListener() {
				public void fileSelected(File file) {
					String filepath = file.toString();
					GoogleTTS tts = ((MyApp)getApplicationContext()).tts;
					tts.stop();
					init_page_cards_by_path(filepath);
				}
			});
			fileDialog.showDialog();
			return true;
        case R.id.menu_item_init_tts:
        	tts.initialize(new ICallable(){
        		public void callback(int cause){
        			if (cause==GoogleTTS.TTS_INIT_OK){
    					GoogleTTS tts = ((MyApp)getApplicationContext()).tts;
        				tts.speak("TTS¾ÍÐ÷",2,null,true);
        				Toast.makeText(CardPagerActivity.this, "Initializing tts ok", Toast.LENGTH_SHORT).show();
        			}
        		}
        	});
        	return true;

		case R.id.menu_item_stop_read:
			flip_read_status();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
 
	void flip_read_status() {
		GoogleTTS tts = ((MyApp)getApplicationContext()).tts;
		MyCardsActivity.tts_stop = !MyCardsActivity.tts_stop;
		mAutoPlay = !MyCardsActivity.tts_stop;
		if (mAutoPlay) {
			if (mText != null){
				textSpeak();
			}else if(mStr2read != null){
				read_card(true);
			}
		}else{
			tts.stop();
		}
	}
	
	public void textSpeak(){
		if (MyCardsActivity.tts_stop||mText==null){
			return;
		}
    	String s = mText.get_current_block();
    	//trace(s);
		GoogleTTS tts = ((MyApp)getApplicationContext()).tts;
    	if (s != null){
    		tts.speak(s, 2, new ICallable(){
    			public void callback(int cause){
    				if (cause == GoogleTTS.TTS_SPEAK_END){
    					if (mText != null){
    						String s = mText.next_block();
    						while(s!=null && s.trim().length()<1){
    							s = mText.next_block();
    						}
    						if (s != null && mAutoPlay){
    							textSpeak();
    						}else if(MyCardsActivity.tts_loop){
    							mText.reset_cursor();
    							textSpeak();
    						}else if(mAutoPlay){
    							mHandler.sendEmptyMessageDelayed(0, 100);
    						}
    					}
    				}
    			}
    		}, false);
    	}
    }
}
