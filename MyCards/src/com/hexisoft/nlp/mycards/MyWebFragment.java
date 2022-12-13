package com.hexisoft.nlp.mycards;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.google.gson.Gson;
import com.hexisoft.common.android.FileDialog;
import com.hexisoft.common.android.GoogleTTS;
import com.hexisoft.common.android.MyLog;
import com.hexisoft.common.android.MyMediaPlayer;
import com.hexisoft.nlp.base.Chunk;
import com.hexisoft.nlp.base.NewWord;
import com.hexisoft.nlp.base.PosTagger;
import com.hexisoft.nlp.base.Sentence;
import com.hexisoft.nlp.base.WordFreq;
import com.hexisoft.nlp.ie.Extractor;
import com.hexisoft.nlp.web.WebUtil;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;
import hexi.common.Json;
import hexi.common.MySocket;
import hexi.common.Searcher;
import hexi.common.UFileReader;

public class MyWebFragment extends Fragment {

	private static final String DIALOG_CARD_PICKER = "card_picker";

	public static final int REQUEST_CARD = 0;

	private MySQLiteHelper mDB;
	private Searcher mSearcher;

	public static PosTagger tagger = null;
	static MyMediaPlayer player = MyCardsActivity.player;
	static GoogleTTS tts;
	static boolean timmer_on = false;
	
	String mUrl;
	public WebView mWebView;
	TextView mStatusBar;
	String mStatusText;


	Handler mHandler = new Handler() {
 	    public void handleMessage(Message msg) {
 	       if (msg.what == 0) {
 	    	   mStatusBar.setText(mStatusText);
 	          //sendEmptyMessageDelayed(0, 100);
 	       }else if(msg.what == 1){
 	    	   String message = "Log: ";
 	    	   if (msg.obj != null){
 	    		   message += msg.obj.toString();
 	    	   }
 	    	   mStatusText += message+"\n";
 	    	   mStatusBar.setText(mStatusText);
 	       }
 	    }
    };
    
    public void playPass() {
    	player.play(R.raw.pass);
    }
    
    public void trace(String msg){
    	mStatusText += msg+"\n";
    	mHandler.sendEmptyMessageDelayed(0,100);
    }
    
    public MyLog myLog = new MyLog(mHandler);
    
    public void log(String msg){
    	mStatusText = msg;
    	mHandler.sendEmptyMessageDelayed(0,100);
    }
    
	public static MyWebFragment newInstance(String url, boolean isEditUsable){
		Bundle args = new Bundle();
		args.putString("URL", url);
		MyWebFragment fragment = new MyWebFragment();
		fragment.setArguments(args);
		//fragment.mIsEditUsable = isEditUsable;
		return fragment;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		try {
			mDB = new MySQLiteHelper(getActivity(),"txtkbase", null);
			mSearcher = new Searcher(((MyApp)(getActivity().getApplicationContext())).data_dir+"indexed1");
		}catch(Exception e) {
			MyLog.f(e);
		}
		//setRetainInstance(true);
		setHasOptionsMenu(true);
		mStatusText = "";
		mUrl = getArguments().getString("URL");
		/*try{
			String fileNames[] =getActivity().getAssets().list("js");
			for(String fileName : fileNames){
				mStatusText += fileName+"\r\n";
			}
			mHandler.sendEmptyMessageDelayed(0,100);
		//mStatusText = URIFetcher.getIP(getActivity().getApplicationContext());
		}catch(Exception e){
			Log.d("My", e.getMessage());
		}*/
	}
	
	@Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.web_fragment, menu);
    }
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
        case R.id.menu_item_connect:
        	String host = "192.168.1.112";
        	int port = 80;
        	String uri = "/pupil_words?uid=a&cmd=wrong";
        	MySocket sock = new MySocket(host, port, uri, mHandler);
        	sock.start();
        	return true;
        case R.id.menu_item_speed_up:
			((MyApp)(getActivity().getApplicationContext())).tts.speed_up();
        	return true;
        case R.id.menu_item_speed_down:
			((MyApp)(getActivity().getApplicationContext())).tts.speed_down();;
        	return true;
        case R.id.menu_item_choose_book:
        	String rootDir = ((MyApp)getActivity().getApplicationContext()).data_dir+"my_library/";
    		File mPath = new File(rootDir);
    	    FileDialog fileDialog = new FileDialog(getActivity(), mPath, "txt");
    	    fileDialog.addFileListener(new FileDialog.FileSelectedListener() {
    	        public void fileSelected(File file) {
    	            String path = file.toString();
    	            mDB.add_book(path, file.getName());
    	            Log.d("My", path);
    	            MyLog.display_long(getActivity(), path);	    	        }
    	    });
    	    fileDialog.showDialog();
        	return true;
        default:
        	trace("memu selected!");
			Toast.makeText(getActivity(), "Select menu!", Toast.LENGTH_SHORT).show();
        	return super.onOptionsItemSelected(item);
        }
    }
	
    @Override
    public void onDestroy() {
    	if (mDB != null) {
    		mDB.close();
    	}
    	
    	super.onDestroy();
    }
    
	public void load(String url){
		mUrl = url;
		//mEditUrl.setText(url);
		mWebView.loadUrl(mUrl);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data){
		if (resultCode != Activity.RESULT_OK) return;
		if (requestCode == REQUEST_CARD){
			String sql = data.getStringExtra(CardPickerFragment.EXTRA_SQL);
			Intent i = new Intent(getActivity(), CardPagerActivity.class);
			i.setAction(CardPagerActivity.ACTION_WORD_SQL);
            i.putExtra(CardPagerActivity.DATA_WORD_SQL, sql);
            startActivity(i);
		}
	}
	
	public void onOpenBook(String path) {
		String ext = path.substring(path.lastIndexOf(".")+1).toLowerCase();
        Intent i = null;
        if (ext.equals("txt")){
        	i = new Intent(getActivity(), CardPagerActivity.class);
        	i.setAction(CardPagerActivity.ACTION_PAGE_FILE);
        	i.putExtra(CardPagerActivity.DATA_FILE_PATH, path);
        	startActivity(i);
        }
	}
	
	@SuppressLint({ "SetJavaScriptEnabled", "JavascriptInterface" })
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState){
		View v = inflater.inflate(R.layout.fragment_my_web, parent, false);
		/*mEditUrl = (EditText)v.findViewById(R.id.webAddress);
		if (mIsEditUsable){
		mEditUrl.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence c, int start, int before,
					int count) {
				mUrl = c.toString();
			}
			public void beforeTextChanged(CharSequence c, int start, int count,
					int after) {

			}
			public void afterTextChanged(Editable c) {

			}
		});

		mEditUrl.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            	if (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER
            			&& event.getAction() == KeyEvent.ACTION_DOWN) {
                    //do something
            		if(mUrl.startsWith("http")){
            			load(mUrl);
            		}else{
            			FragmentManager fm = getActivity().getSupportFragmentManager();
            			mUrl = "select * from cards ";
                    	CardPickerFragment dialog = CardPickerFragment.newInstance(mUrl);
                    	dialog.setTargetFragment(MyWebFragment.this, REQUEST_CARD);
                    	dialog.show(fm, DIALOG_CARD_PICKER);                    	
            		}
                    return true;
                }
                return false;
            }
        });}else{
        	mEditUrl.setEnabled(false);
        }*/

		mStatusBar = (TextView)v.findViewById(R.id.statusBar);
		mStatusBar.setMovementMethod(ScrollingMovementMethod.getInstance());
		mWebView = (WebView)v.findViewById(R.id.webView);
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.getSettings().setDefaultTextEncodingName("utf-8") ;
		//mWebView.setBackgroundColor(0); // 设置背景色
		//mWebView.getBackground().setAlpha(127); // 设置填充透明度 范围：0-255

		mWebView.setWebViewClient(new WebViewClient(){
			public boolean shouldOverrideUrlLoading(WebView view, String url){
				return false;
			}
		});
		mWebView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
            	WebView v = (WebView)view;
            	
            //如果想要屏蔽只需要返回ture 即可
                return false;
            }
        });
		
		mWebView.loadUrl(mUrl);
		mWebView.addJavascriptInterface(new Object(){
			@JavascriptInterface
			public void speak(String s){
				tts = ((MyApp)(getActivity().getApplicationContext())).tts;
	        	tts.speak(s, 2, null, true);
				return ;
			}
			
			@JavascriptInterface
			public String test4js(){
				String r = "Hello from Android!";
				player.play(R.raw.drop);
				return r;
			}
			
			@JavascriptInterface
			public String lookupWordfromHaici(String word){
				String r = WebUtil.lookupWordfromHaici(word);
				return r;
			}
			
			@JavascriptInterface
			public String getBookUnitSents(String book, String unit){
				//Gson gson = new Gson();
				return Json.toJson(mDB.get_book_unit_sents(book, unit));
			}
			
			@JavascriptInterface
			public String look_up_word(String word){
				String r = mDB.lookup(word);
				//trace("word:"+word);
				//trace("r:"+r);
				return r;
			}
			
			@JavascriptInterface
			public void openBook(String bookPath){
				onOpenBook(bookPath);
				player.play(R.raw.drop);
			}
			
			@JavascriptInterface
			public String testSqlite(String tableName){
				String r = "Hello from Android!";
				r = mDB.get_table_records_number(tableName)+"";
				log("r:"+r);
				player.play(R.raw.drop);				
				return r;
			}
			
			@JavascriptInterface
			public String get_my_sents(String where){
				String r = "Hello from Android!";
				ArrayList<Sentence> sents = mDB.get_special_new_sents(where);
				Gson gson = new Gson();
				r = gson.toJson(sents);
				log(r);
				player.play(R.raw.drop);				
				return r;
			}
			
			@JavascriptInterface
			public String search(String s){
				String ret = "[]";
				try {
					String[] qs = s.split("AND\\s+");
					for(int i=0; i<qs.length; ++i) {
						String[] terms = qs[i].split("\\s+");
						String q = "";
						for(int j=0; j<terms.length; ++j) {
							q += mDB.get_word_variants(terms[j])+" ";
						}
						qs[i] = q;
					}
					List<Sentence> sents = mSearcher.search(qs);
					if (sents.size()>0) {
						playPass();
					}
					ret = Json.toJson(sents);
				} catch (Exception e) {
					MyLog.f(e.toString());
				}
				return ret;
			}
			
			@JavascriptInterface
			public void stop(){
				tts = ((MyApp)(getActivity().getApplicationContext())).tts;
				tts.stop();
				return ;
			}
			
			@JavascriptInterface
			public String getWordLemma(String word){
				return mDB.get_word_lemma(word);
			}
			
			@JavascriptInterface
			public void delete_new_words(String where){
				mDB.del_new_words(where);
				player.play(R.raw.del);
			}
						
			@JavascriptInterface
			public String pos_tag(String s){
				String r = "tagging...";
				tagger = PosTagger.getInstance("/storage/emulated/0/my_library/english-bidirectional-distsim.tagger");
				r = tagger.tag_s(s);
				Log.d("My", r);
				return r;
			}
			
			@JavascriptInterface
			public String sents2triples(String s){
				Log.d("My", "extracting...");
				String r = "[]";
				List<Chunk[]> triples = Extractor.sents2triples(s);
				r = Json.toJson(triples);
				Log.d("My", r);
				return r;
			}
			
			@SuppressLint("NewApi") @JavascriptInterface
			public String test_service(){
				String r = "starting service...";
				AlarmManager alarmManager = (AlarmManager)getActivity().getSystemService(MyCardsActivity.ALARM_SERVICE);
				Intent intent = new Intent(getActivity(), MyBroadcastReceiver.class);
				PendingIntent pi = PendingIntent.getBroadcast(getActivity(), 0, intent,
						PendingIntent.FLAG_UPDATE_CURRENT);
				if (!timmer_on){
				//intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				//intent.setAction("Action.Alarm");
				long firstime = SystemClock.elapsedRealtime();
				alarmManager.setWindow(AlarmManager.RTC_WAKEUP, 0, 3000, pi);
				timmer_on = true;
				r = "timmer on";
				}else{
					alarmManager.cancel(pi);
					timmer_on = false;
					r = "timmer off";
				}

        		/*
        		boolean isOn = MyIntentService.isServiceAlarmOn(getActivity());
        		MyIntentService.setServiceAlarm(getActivity(), !isOn, new Messenger(mHandler));
        		if (isOn){
        			r = "stopped service.";
        		}else{
        			r = "started service.";
        		}*/
				Log.d("My", r);
				trace(r);
				return r;
			}
			
			@JavascriptInterface
			public void select_book_new_words(String path){
				String text = UFileReader.read(path);
				ArrayList<Map<String,Object>> word_freq_list = Text2words.get_new_words(getActivity(), text);
				Intent i = new Intent(getActivity(), CardPagerActivity.class);
				i.setAction(CardPagerActivity.ACTION_WORD_SET);
				i.putExtra(CardPagerActivity.DATA_WORD_SET, word_freq_list);
				startActivity(i);
			}
			
			@JavascriptInterface
			public void execSQL(String sql){
				mDB.execute_sql(sql);
			}
			
			@JavascriptInterface
			public void select_my_new_words(String where){
				ArrayList<NewWord> new_words = mDB.get_special_new_words(where);
				ArrayList<Map<String,Object>> word_list = new ArrayList<Map<String,Object>>();
				for (NewWord w : new_words) {
					TreeMap<String,Object> word = new TreeMap<String,Object>();
					word.put("word", w.get_word());
					word.put("explains", w.get_explains());
					word.put("freq", 1);
					word_list.add(word);
				}
				trace("Got new words: "+word_list.size());
				Intent i = new Intent(getActivity(), CardPagerActivity.class);
				i.setAction(CardPagerActivity.ACTION_WORD_SET);
				trace("setted intent action.");
				i.putExtra(CardPagerActivity.DATA_WORD_SET, word_list);
				trace("Put new words list as word set.");
				MyCardsActivity.sensorSwitchOn = false;
				startActivity(i);
				trace("Started new words activity!");
			}
			
			@JavascriptInterface
			public void select_my_new_words2(String where){
				trace("selecting my new words...");
				where += " limit 10";
				ArrayList<NewWord> new_words = mDB.get_special_new_words(where);
				ArrayList<Map<String,Object>> word_list = new ArrayList<Map<String,Object>>();
				for (NewWord w : new_words) {
					TreeMap<String,Object> word = new TreeMap<String,Object>();
					word.put("word", w.get_word());
					word.put("explains", w.get_explains());
					word.put("freq", 1);
					word_list.add(word);
				}
				trace("Got new words: "+word_list.size());
				Intent i = new Intent(getActivity(), CardPagerActivity.class);
				i.setAction(CardPagerActivity.ACTION_WORD_SET);
				trace("setted intent action.");
				i.putExtra(CardPagerActivity.DATA_WORD_SET, word_list);
				trace("Put new words list as word set.");
				startActivity(i);
				trace("Started new words activity!");
			}
			
			@JavascriptInterface
			public String get_book_new_words(String path){
				trace("get book new words...");
				String r = "[]";
				String text = UFileReader.read(path);
				trace("text: "+text.length());
				ArrayList<Map<String,Object>> word_freq_list = Text2words.get_new_words(getActivity(), text);
				String source = path.replaceAll("/.+/", "");
				source = source.replaceAll("\\..+$", "");
				trace("Got new words: "+word_freq_list.size());
				for(Map<String,Object> wef : word_freq_list) {
					mDB.add_new_word((String)wef.get("word"), source, wef.get("freq")+"");
				}
				r = Json.toJson(word_freq_list);
				trace("r: "+r.length());
				return r;
			}
			
			@JavascriptInterface
			public void msg(String message){
				MyLog.display_long(getActivity(), message);
			}
			
			@JavascriptInterface
			public void add_new_word(String jsonWordItems){
				JSONArray array;
				try {
					array = (JSONArray) new JSONTokener(jsonWordItems).nextValue();
					for (int i=0; i<array.length(); ++i) {
						JSONObject wordItem = array.getJSONObject(i);
						String word = wordItem.getString("word");
						String explains = wordItem.getString("explains").replaceAll("``", "\n");
						String source = wordItem.getString("source_id");
	    	            mDB.add_new_word(word, explains, source, "");
	    	            player.play(R.raw.drop);
					}
				} catch (JSONException e) {
					MyLog.f(e.getMessage());
				}finally {
					;
				}
				
			}
			
			@JavascriptInterface
			public String get_my_new_words(String condition_expression){
	            ArrayList<NewWord> words = mDB.get_special_new_words(condition_expression);
	            JSONArray array = new JSONArray();
	            for (NewWord word : words) {
	            	array.put(word.toJson());
	            }
				return array.toString();
			}
						
			@JavascriptInterface
			public String get_all_my_books(){
				String r = "[]";
	            r = Json.toJson(mDB.get_all_my_books());
				return r;
			}
			
			@JavascriptInterface
			public boolean delete_book(String bookPath){
				boolean r = false;
	            r = mDB.delete_book(bookPath);
	            MyLog.display_long(getActivity(),"Deleted book: "+bookPath);
	            return r;
			}
						
			@JavascriptInterface
			public void select_cards_by_memo(String memo) {
				String sql = "select * from new_words where memo='" + memo + "'";
				Intent i = new Intent(getActivity(), CardPagerActivity.class);
				i.setAction(CardPagerActivity.ACTION_WORD_SQL);
				i.putExtra(CardPagerActivity.DATA_WORD_SQL, sql);
				startActivity(i);
			}

			@JavascriptInterface
			public void start_word_cards(String book, int unit){
				String sql = "select * from cards where book='"+book+"' and unit="+unit;
				Intent i = new Intent(getActivity(), CardPagerActivity.class);
				i.setAction(CardPagerActivity.ACTION_WORD_SQL);
	            i.putExtra(CardPagerActivity.DATA_WORD_SQL, sql);
	            i.putExtra(CardPagerActivity.DATA_WORD_BOOK, book);
	            i.putExtra(CardPagerActivity.DATA_WORD_UNIT, unit);
	            startActivity(i);
			}
			@JavascriptInterface
			public void start_sent_cards(String book, int unit){
				String sql = "select * from sents where b='"+book+"' and u="+unit;
				Intent i = new Intent(getActivity(), CardPagerActivity.class);
				i.setAction(CardPagerActivity.ACTION_SENT_SQL);
	            i.putExtra(CardPagerActivity.DATA_SENT_SQL, sql);
	            startActivity(i);
			}
			@JavascriptInterface
			public void set_card_front_status(String s_front){
				player.play(R.raw.pass);
				CardPagerActivity a = (CardPagerActivity)getActivity();
				a.set_card_front_status(s_front);
			}
			@JavascriptInterface
			public void set_card_img_status(String s_img){
				player.play(R.raw.pass);
				CardPagerActivity a = (CardPagerActivity)getActivity();
				a.set_card_img_status(s_img);
			}
			@JavascriptInterface
			public void set_card_back_status(String s_back){
				player.play(R.raw.drop);
				CardPagerActivity a = (CardPagerActivity)getActivity();
				a.set_card_back_status(s_back);
			}
			@JavascriptInterface
			public void set_card_memo(String word, String s_memo){
				try{
					MyLog.f("word="+word);
					SQLiteDatabase db = mDB.getWritableDatabase();
					word = word.replaceAll("'","''");
					String sql = "update new_words set memo='"+s_memo+"' where word='"+word+"'";
					MyLog.f("sql="+sql);
					db.execSQL(sql);
					mDB.close();
					player.play(R.raw.del);
				}catch(Exception e){
					MyLog.f(e.getMessage());
				}
			}
			@JavascriptInterface
			public String get_book_units(String bookId){
				String r = "{}";
				try{
					SQLiteDatabase db = mDB.getReadableDatabase();
					String sql = "select distinct u from sents where b='"+bookId+"'";
					Cursor cursor = db.rawQuery(sql, null);
					ArrayList<Integer> units = new ArrayList<Integer>();
					while (cursor.moveToNext()) {
						int column_index = cursor.getColumnIndex("u");
						int unit = cursor.getInt(column_index);
						units.add(unit);
					}
					JSONObject obj = new JSONObject();
					obj.put("units", units);
					r = obj.toString();
					Log.d("My", r);
				}catch(Exception e){
					Log.d("My", e.getMessage());
				}
				return r;
			}
		
		}, "myAndroid");		
		mHandler.sendEmptyMessageDelayed(0,100);
		return v;
	}
}
