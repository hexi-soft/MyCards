package com.hexisoft.nlp.mycards;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;


public class CardPickerFragment extends DialogFragment {
	public static final String EXTRA_SQL = "com.hexisoft.nlp.mycards.sql";
	private String mSQL;

	public static CardPickerFragment newInstance(String sql) {
		Bundle args = new Bundle();
		args.putString(EXTRA_SQL, sql);

		CardPickerFragment fragment = new CardPickerFragment();
		fragment.setArguments(args);

		return fragment;
	}

	private void sendResult(int resultCode){
		if (getTargetFragment() == null)
			return;
		
		Intent i = new Intent();
		i.putExtra(EXTRA_SQL, mSQL);
		
		getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, i);
	}
	
	EditText mEditSQL;
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState){
		mSQL = getArguments().getString(EXTRA_SQL);
		View v = getActivity().getLayoutInflater()
				.inflate(R.layout.card_picker, null);
		
		mEditSQL = (EditText)v.findViewById(R.id.editCardPickerSQL);
		mEditSQL.setText(mSQL);
		mEditSQL.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence c, int start, int before,
					int count) {
				mSQL = c.toString();
				getArguments().putString(EXTRA_SQL, mSQL);
			}
			public void beforeTextChanged(CharSequence c, int start, int count,
					int after) {

			}
			public void afterTextChanged(Editable c) {

			}
		});

		Button btn_select = (Button)v.findViewById(R.id.btn_select);
        btn_select.setOnClickListener(new OnClickListener(){
        	public void onClick(View v){
        		String input = mEditSQL.getText().toString();
        		input += "select ";
             	mEditSQL.setText(input);mEditSQL.requestFocus();
             	mEditSQL.setSelection(input.length());
        	}
        });
        Button btn_from = (Button)v.findViewById(R.id.btn_from);
        btn_from.setOnClickListener(new OnClickListener(){
        	public void onClick(View v){
        		String input = mEditSQL.getText().toString();
        		input += "from ";
             	mEditSQL.setText(input);mEditSQL.requestFocus();
             	mEditSQL.setSelection(input.length());
        	}
        });
        Button btn_where = (Button)v.findViewById(R.id.btn_where);
        btn_where.setOnClickListener(new OnClickListener(){
        	public void onClick(View v){
        		String input = mEditSQL.getText().toString();
        		input += "where ";
             	mEditSQL.setText(input);mEditSQL.requestFocus();
             	mEditSQL.setSelection(input.length());
        	}
        });
        Button btn_equal = (Button)v.findViewById(R.id.btn_equal);
        btn_equal.setOnClickListener(new OnClickListener(){
        	public void onClick(View v){
        		String input = mEditSQL.getText().toString();
        		input += "= ";
             	mEditSQL.setText(input);mEditSQL.requestFocus();
             	mEditSQL.setSelection(input.length());
        	}
        });
        Button btn_and = (Button)v.findViewById(R.id.btn_and);
        btn_and.setOnClickListener(new OnClickListener(){
        	public void onClick(View v){
        		String input = mEditSQL.getText().toString();
        		input += "and ";
             	mEditSQL.setText(input);mEditSQL.requestFocus();
             	mEditSQL.setSelection(input.length());
        	}
        });
        Button btn_quote = (Button)v.findViewById(R.id.btn_quote);
        btn_quote.setOnClickListener(new OnClickListener(){
        	public void onClick(View v){
        		String input = mEditSQL.getText().toString();
        		input += "'";
             	mEditSQL.setText(input);mEditSQL.requestFocus();
             	mEditSQL.setSelection(input.length());
        	}
        });
        Button btn_percent = (Button)v.findViewById(R.id.btn_percent);
        btn_percent.setOnClickListener(new OnClickListener(){
        	public void onClick(View v){
        		String input = mEditSQL.getText().toString();
        		input += "%";
             	mEditSQL.setText(input);mEditSQL.requestFocus();
             	mEditSQL.setSelection(input.length());
        	}
        });
        Button btn_sents = (Button)v.findViewById(R.id.btn_sents);
        btn_sents.setOnClickListener(new OnClickListener(){
        	public void onClick(View v){
        		String input = mEditSQL.getText().toString();
        		input += "sents ";
             	mEditSQL.setText(input);mEditSQL.requestFocus();
             	mEditSQL.setSelection(input.length());
        	}
        });
        Button btn_cards = (Button)v.findViewById(R.id.btn_cards);
        btn_cards.setOnClickListener(new OnClickListener(){
        	public void onClick(View v){
        		String input = mEditSQL.getText().toString();
        		input += "cards ";
             	mEditSQL.setText(input);mEditSQL.requestFocus();
             	mEditSQL.setSelection(input.length());
        	}
        });
        
		return new AlertDialog.Builder(getActivity())
		.setView(v)
		.setTitle(R.string.card_picker_title)
		.setPositiveButton(
				android.R.string.ok,
				new DialogInterface.OnClickListener(){
					public void onClick(DialogInterface dialog, int which){
						sendResult(Activity.RESULT_OK);
					}
				})
		.create();
	}
}
