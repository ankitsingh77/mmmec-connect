package com.quipmate2.features;

import com.example.quipmate2.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.Window;

public class MessageActivity extends Activity implements EditTextFragment.onMessageAttached{

	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.message);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
        case android.R.id.home:
            // application icon in action bar clicked; go back
           finish();
            return true;
        default:
            return super.onOptionsItemSelected(item);
    }
	}

	@Override
	public void attachMsg(String msg) {
		// TODO Auto-generated method stub
		 MessageFragment fragment = (MessageFragment) getFragmentManager()
		            .findFragmentById(R.id.message_fragment);
		        if (fragment != null) {
		          //fragment.additem(msg);
		        } 
	}
}
