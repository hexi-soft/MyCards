package com.hexisoft.nlp.mycards;

import com.hexisoft.common.android.MyLog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class SynchronizeDialogFragment extends DialogFragment {

	static SQLiteDatabase db;
	
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
 	    	  //mStatusText += message+"\n";
 	    	  mStatusText = message;
 	    	  mStatusBar.setText(mStatusText);
 	       }
 	    }
    };
    
	public Dialog onCreateDialog(Bundle savedInstanceState){
		View v = getActivity().getLayoutInflater()
				.inflate(R.layout.synchronize_dialog, null);
		Button syn_word_btn = (Button)v.findViewById(R.id.btn_syn_word);
		mStatusBar = (TextView)v.findViewById(R.id.tv_syn_info);
		syn_word_btn.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				MySQLiteHelper mySQLiteHelper = new MySQLiteHelper(getActivity(), "txtkbase", null);
		        db = mySQLiteHelper.getWritableDatabase();
				String url = "http://192.168.1.112/pupil_words?uid=a&cmd=all";
	        	URIFetcher f = new URIFetcher((MyCardsActivity)getActivity(), url, "json-card", db, new MyLog(mHandler));
				f.start();
			}
		});
		Button syn_sent_btn = (Button)v.findViewById(R.id.btn_syn_sent);
		syn_sent_btn.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				MySQLiteHelper mySQLiteHelper = new MySQLiteHelper(getActivity(), "txtkbase", null);
		        db = mySQLiteHelper.getWritableDatabase();
				String url = "http://192.168.1.112/pupil_sentences?uid=a&cmd=all";
	        	URIFetcher f = new URIFetcher((MyCardsActivity)getActivity(), url, "json-sent", db, new MyLog(mHandler));
				f.start();
			}
		});
		Button syn_library_btn = (Button)v.findViewById(R.id.btn_syn_library);
		syn_library_btn.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				String url = "http://192.168.1.112/text?uid=a&url=library:///";
	        	URIFetcher f = new URIFetcher((MyCardsActivity)getActivity(), url, "xml-library", db, new MyLog(mHandler));
				f.start();
			}
		});
		return new AlertDialog.Builder(getActivity())
		.setView(v)
		.setTitle(R.string.synchronize_dialog_title)
		.setPositiveButton(android.R.string.ok, null)
		.create();
	}
}
