package com.hexisoft.nlp.mycards;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class SettingsDialogFragment extends DialogFragment {
	
	public static final int REPEAT_LIST_PLAY_MODE = 0;
	public static final int REPEAT_CURRENT_PAGE_PLAY_MODE = 1;
	public static final int PLAY_IN_ORDER_PLAY_MODE = 2;
	
	MyListener myListener;
	int mPlayMode = 0;
	
	public SettingsDialogFragment(MyListener myListener){
		this.myListener = myListener;
	}
	
	private void sendResult(int resultCode){
		if (getTargetFragment() == null){
			Bundle data = new Bundle();
			data.putInt("play_mode", mPlayMode);
			myListener.set_data(data);
			return;
		}
		Intent i = new Intent();
		i.putExtra("PLAY_MODE", mPlayMode);		
		getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, i);
	}
	
	public Dialog onCreateDialog(Bundle savedInstanceState){
		View v = getActivity().getLayoutInflater()
				.inflate(R.layout.settings_dialog, null);
		RadioGroup play_mode_radiogroup = (RadioGroup)v.findViewById(R.id.play_mode_radiogroup);
		final RadioButton repeat_list_radio;
		final RadioButton repeat_current_page_radio;
		final RadioButton play_in_order_radio;
		repeat_list_radio = (RadioButton)v.findViewById(R.id.radio_repeat_list);
		repeat_current_page_radio = (RadioButton)v.findViewById(R.id.radio_repeat_current_page);
		play_in_order_radio = (RadioButton)v.findViewById(R.id.radio_play_in_order);
		play_mode_radiogroup.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId){
				if(checkedId == repeat_current_page_radio.getId()){
					mPlayMode = REPEAT_CURRENT_PAGE_PLAY_MODE;
				}else if(checkedId == repeat_list_radio.getId()){
					mPlayMode = REPEAT_LIST_PLAY_MODE;
				}else if(checkedId == play_in_order_radio.getId()){
					mPlayMode = PLAY_IN_ORDER_PLAY_MODE;
				}
			}
		});
		return new AlertDialog.Builder(getActivity())
		.setView(v)
		.setTitle("Settings")
		.setPositiveButton(android.R.string.ok,
			new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface dialog, int which){
					sendResult(Activity.RESULT_OK);
			}
		})
		.create();
	}
}

