package com.hexisoft.nlp.web;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.hexisoft.common.android.GoogleTTS;
import com.hexisoft.common.android.ICallable;
import com.hexisoft.common.android.MyLog;
import com.hexisoft.common.android.MyMediaPlayer;
import com.hexisoft.nlp.base.Text2sents;
import com.hexisoft.nlp.mycards.MyApp;
import com.hexisoft.nlp.mycards.MySQLiteHelper;
import com.hexisoft.nlp.mycards.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import hexi.common.MD5;

@SuppressLint({ "SetJavaScriptEnabled", "JavascriptInterface" })

public class MyWebActivity extends Activity {

	Deque<Link> linkQueue = new ArrayDeque<Link>();
	Set<String> md5Set = new HashSet<String>();
	FetchURLTask fetcher;
	
	MyMediaPlayer player;	
	MySQLiteHelper sql;
	String mUrl = "file:///android_asset/home.htm";
	//String mUrl = "file://"+((MyApp)getApplicationContext()).data_dir+"home.htm";;
	String mContentText = "";
	String mContentHTML = "<html><body>My Cards Home</body></html>";
	EditText mEditUrl;
	WebView mWebView;
	TextView mStatusBar;
	String mStatusText = "ready";
	GoogleTTS tts;
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()) {

			mWebView.goBack();

			return true;

		} else

			return super.onKeyDown(keyCode, event);
	}

	boolean is_interested(String content){
		boolean ret = false;
		if(content.length()>100){
			ret = true;
		}
		return ret;
	}
	
	boolean is_dropout_url(String url){
		boolean b = false;
		//Pattern pattern = Pattern.compile("");
		if (url.endsWith(".jpg")){
			b = true;
		}
		return b;
	}
	
	private class FetchURLTask extends AsyncTask<String,Void,String>{
		@Override
		protected String doInBackground(String... params){
			String content = "";
			String url = params[0];
			Pair<Document,ArrayList<Link>> result = new UrlFetcher(url, UrlFetcher.TEXT_HTML).getDocumentWithLinks();
			Document doc = result.first;
			if (doc == null) {
				return "";
			}else {
				content = doc.text();
				doc.select("body").attr("style", "background:#000;color:#fff;font-size:20px;");
				doc.select("img").attr("width","360");
				doc.select("img").attr("height","240");
				String base = doc.baseUri().replaceAll("[^/]+$", "");
				//Elements elements = doc.select("a[href~=^(?!http).+$]");
				Elements elements = doc.select("a");
				for (Element element : elements) {
					element.attr("href",element.attr("abs:href"));
				}
				Element head = doc.select("head").first();
				head.prepend("<script src=\"jquery.js\"></script>\n"
						+"<script src=\"functions2.js\"></script>\n"
						+"<script src=\"home2.js\"></script>\n" 
						+ "<link rel=\"stylesheet\" href=\"home2.css\" />");
				mContentHTML = doc.html();
				sql.add_webpage(result.first.title(), content, url, "done", mLog);
			}
			ArrayList<Link> links = result.second;
			for(Link link : links){
				String href = link.get_href();
				if (href.isEmpty() || is_dropout_url(href)){
					continue;
				}
				String linkMD5 = MD5.md5s(href);
				boolean not_in_set = md5Set.add(linkMD5);
				if (not_in_set){
					linkQueue.offer(link);					
				}
			}
			int n = linkQueue.size();
			log("doInBackground->links in queue: "+n);
			return Text2sents.summarize(content);
		}
		
		@Override
		protected void onPostExecute(String contentText){
			linkQueue.poll();
			int n = linkQueue.size();
			//log("onPostExecute->links in queue: "+n);
			if (is_interested(contentText)){
				mContentText = contentText;
				setUpWebContent();
				readContent();
			}else{
				next_task();
			}
		}
	}
	
	Handler mHandler = new Handler() {
 	    public void handleMessage(Message msg) {
 	       if (msg.what == 0) {
 	    	   mStatusBar.setText(mStatusText);
 	    	   //mEditUrl.setText(mUrl);
 	       }else {
 	    	   mStatusBar.setText(msg.obj.toString());
 	       }
 	    }
    };
    MyLog mLog = new MyLog(mHandler);
    
    public void trace(String msg){
    	mStatusText += msg+"\n";
    	mHandler.sendEmptyMessageDelayed(0,100);
    }
    
    public void log(String msg){
    	mStatusText = msg;
    	mHandler.sendEmptyMessageDelayed(0,100);
    }
    
	void next_task(){
		tts = ((MyApp)getApplicationContext()).tts;
		tts.stop();
		Link link = linkQueue.peek();
		trace("in next_task->links in queue: "+linkQueue.size());
		if (link != null){
			mUrl = link.get_href();
			mStatusText = link.get_title();
			mHandler.sendEmptyMessageDelayed(0, 100);
			new FetchURLTask().execute(link.get_href(),link.get_title());
		}else {
			trace("at the end of next_task->no more task!");
		}
	}
	
	void setUpWebContent(){
		mWebView.loadDataWithBaseURL("file:///android_asset/", mContentHTML, "text/html", "UTF-16", null);
	}
	
	void readContent(){
		tts = ((MyApp)getApplicationContext()).tts;
		tts.speak(mContentText,2,new ICallable() {
			public void callback(int cause) {
				if (cause == GoogleTTS.TTS_SPEAK_END) {
					log("next task...");
					next_task();
				}
			}
		}, true);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_web);
		sql = new MySQLiteHelper(this, "txtkbase", null);
		player = new MyMediaPlayer(this);
		mEditUrl = (EditText)findViewById(R.id.webURL);
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
            			load_url(mUrl, "");
            		}else{
            			next_task();
            		}
                    return true;
                }
                return false;
            }
        });
		mWebView = (WebView)findViewById(R.id.webBrowser);
		mWebView.setBackgroundColor(0);
		mWebView.getBackground().setAlpha(127); // ÉèÖÃÌî³äÍ¸Ã÷¶È ·¶Î§£º0-255
		mWebView.getSettings().setJavaScriptEnabled(true);
		//mWebView.getSettings().
		mWebView.setWebViewClient(new WebViewClient(){
			public boolean shouldOverrideUrlLoading(WebView view, String url){
				return false;
			}
		});
		
		mWebView.addJavascriptInterface(new Object(){
			@JavascriptInterface
			public void drop() {
				player.play(R.raw.drop);
			}
			
			@JavascriptInterface
			public String load_webpage(String url) {
				WebPage page = sql.get_webpage(url, mLog);
				String content = "";
				player.play(R.raw.del);
				if (page!=null) {
					content = page.content;
				}
				return content;
			}
			
			@JavascriptInterface
			public void load_url(String url, String title) {
				if (url==null || url.isEmpty()) {
					mStatusText = "Invalid url";
					mHandler.sendEmptyMessageDelayed(0,100);
					return;
				}
				mUrl = url;
				mStatusText = "loading "+title;
				mHandler.sendEmptyMessageDelayed(0,100);
				String linkMD5 = MD5.md5s(url);
				Link link = new Link(title, mUrl);
				linkQueue.clear();
				md5Set.clear();
				md5Set.add(linkMD5);
				linkQueue.offer(link);
				fetcher = new FetchURLTask();
				fetcher.execute(mUrl,title);
			}
		
		}, "myAndroid");
		//mUrl = "file://"+((MyApp)getApplicationContext()).data_dir+"home.htm";;
		mUrl = "file:///android_asset/home.htm";
		mWebView.loadUrl(mUrl);

		mStatusBar = (TextView)findViewById(R.id.webStatus);
		
	}
	
	void load_url(String url, String title){
		mUrl = url;
		mStatusText = title;
		mHandler.sendEmptyMessageDelayed(0,100);
		Link link = new Link(title, mUrl);
		String linkMD5 = MD5.md5s(link.get_href());
		linkQueue.clear();
		md5Set.clear();
		md5Set.add(linkMD5);
		linkQueue.offer(link);
		fetcher = new FetchURLTask();
		fetcher.execute(mUrl,title);
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.web_activity, menu);
        return true;
    }
	
	 @Override
	    public boolean onOptionsItemSelected(MenuItem item) {
	        // Handle action bar item clicks here. The action bar will
	        // automatically handle clicks on the Home/Up button, so long
	        // as you specify a parent activity in AndroidManifest.xml.
	        int id = item.getItemId();
	        switch (id) {
	        case R.id.menu_item_init_tts:
	        	tts.initialize(new ICallable(){
	        		public void callback(int cause){
	        			if (cause==GoogleTTS.TTS_INIT_OK){
	    					GoogleTTS tts = ((MyApp)getApplicationContext()).tts;
	        				tts.speak("TTS¾ÍÐ÷",2,null,true);
	        				Toast.makeText(MyWebActivity.this, "Initializing tts ok", Toast.LENGTH_SHORT).show();
	        			}
	        		}
	        	});
	        	return true;
	        case R.id.menu_item_speed_up:
				((MyApp)getApplicationContext()).tts.speed_up();
	        	return true;
	        case R.id.menu_item_speed_down:
				((MyApp)getApplicationContext()).tts.speed_down();;
	        	return true;
	        case R.id.menu_item_stop_read:
	        	tts.stop();
	        	return true;
	        }
	        return true;
	 }
		
}
