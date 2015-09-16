package com.quipmate2.features;


import java.util.ArrayList;

import com.example.quipmate2.R;
import com.quipmate2.adapter.MoodAdapter;

import android.app.Activity;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class MoodSelect extends Activity implements OnItemClickListener {
	ListView lv;
	String[] moodTitles;
	TypedArray moodIcons;
	ArrayList<MoodItem> arraylist;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mood_select);
		getActionBar().setTitle("Select Mood");
		getActionBar().setDisplayHomeAsUpEnabled(true);
		lv= (ListView) findViewById(R.id.listMode);
		arraylist = new ArrayList();
		
		moodTitles=getResources().getStringArray(R.array.moods);
		moodIcons=getResources().obtainTypedArray(R.array.mood_icons);
		
		for(int i=0;i<moodTitles.length;i++){
			arraylist.add(new MoodItem(moodTitles[i], moodIcons.getResourceId(i, -1)));
		}
		MoodAdapter adapter=new MoodAdapter(this, arraylist);
		lv.setAdapter(adapter);
		moodIcons.recycle();
		
		lv.setOnItemClickListener(this);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
        case android.R.id.home:
            // application icon in action bar clicked; go home
           finish();
            return true;
        default:
            return super.onOptionsItemSelected(item);
    }
	}


@Override
public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	// TODO Auto-generated method stub
	
	//passing back the data to the calling activity
	Intent back = new Intent();
	back.putExtra("mood_number", position);
	setResult(RESULT_OK,back);
	finish();
}
public class MoodItem {
	public String name;
	public int icon;

	public MoodItem() {
		// TODO Auto-generated constructor stub
	}
	public MoodItem(String name,int icon){
		this.name=name;
		this.icon=icon;
	}
}

}
