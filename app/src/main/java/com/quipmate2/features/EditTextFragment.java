package com.quipmate2.features;

import com.example.quipmate2.R;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class EditTextFragment extends Fragment {

	private EditText et;
	private onMessageAttached attach;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.edit_text_frag, container, false);
		
		et = (EditText)view.findViewById(R.id.et_msg);
		//Button bt = (Button) view.findViewById(R.id.bt_send_msg);
		
		/*
		bt.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				updateList();
			}
		});
		
		*/
		return view;
	}
	
	public interface onMessageAttached{
		public void attachMsg(String msg);
	}
	

	public void updateList(){
		String msg = et.getText().toString().trim();
		if(msg != null && !msg.equals("")){
			et.setText("");
			attach.attachMsg(msg);
		}
	}
	
	@Override
    public void onAttach(Activity activity) {
      super.onAttach(activity);
      if (activity instanceof onMessageAttached) {
        attach = (onMessageAttached) activity;
      } else {
        throw new ClassCastException(activity.toString()
            + " must implemenet MyListFragment.OnItemSelectedListener");
      }
    }
}
