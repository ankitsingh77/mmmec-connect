package com.quipmate2.features;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.quipmate2.R;
import com.quipmate2.adapter.FriendListAdapter;
import com.quipmate2.constants.AppProperties;
import com.quipmate2.utils.CommonMethods;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;


public class SearchFriend extends Activity {

	

	private String action="search_people";
	private String searchQuery;
	private Session session;
	private String search="q";
	private LinearLayout llLayoutSearch;
	private TextView noResults;
	private JSONArray jsonTask,friendsId;
	private JSONObject friendsName,friendsImage;
	
	private ListView friendListView;
	private List<FriendInfo> friendDataList;
	private FriendListAdapter adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_friends);
		
		llLayoutSearch = (LinearLayout) findViewById(R.id.llayout_search_friend);
		friendListView = (ListView) findViewById(R.id.lv_search_friend);
		noResults = (TextView) findViewById(R.id.noResultsFound);
		friendDataList=new ArrayList<FriendInfo>();
		
		friendListView.addHeaderView(new View(this));
		friendListView.addFooterView(new View(this));
		
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
		    searchQuery = extras.getString("search_name");	   
		}
		
		new searchFreinds().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}
	
	
	public class searchFreinds extends AsyncTask<Void, Void, Void> {
		
		ProgressBar pBar;
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			pBar = new ProgressBar(
					SearchFriend.this,null,android.R.attr.progressBarStyleLarge); 
			pBar.setIndeterminate(true);
			llLayoutSearch.addView(pBar);
		}

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			session = new Session(SearchFriend.this);
			List<NameValuePair> apiParams = new ArrayList<NameValuePair>();
			apiParams.add(new BasicNameValuePair(AppProperties.ACTION,action));
			apiParams.add(new BasicNameValuePair(AppProperties.PROFILE_ID, session.
					getValue(AppProperties.PROFILE_ID)));
			apiParams.add(new BasicNameValuePair(search,searchQuery));
			
			jsonTask = CommonMethods.loadJSONData(AppProperties.URL, AppProperties.METHOD_GET, apiParams);
			
			try {
				
				JSONObject data = jsonTask.getJSONObject(0);
				//System.out.println(data);
				if (data != null) {
					if (!data.has(getString(R.string.error))) {
						friendsId = data.getJSONArray(AppProperties.ACTION);
						friendsName = data.getJSONObject(AppProperties.NAME);
				 	    friendsImage = data.getJSONObject(AppProperties.PROFILE_IMAGE);
						
						
						// ready data for adapter
						String id=null;
						String name = null;
						String imageURL = null;
						for(int i = 0; i<friendsId.length(); i++){
							 id = friendsId.getString(i);
							// System.out.println(id);
							 if(id != null){
								
								 if(friendsName != null){
									 name = friendsName.getString(id);
									 //System.out.println(name);
								 }
								 
								 if(friendsImage != null){
									 imageURL = friendsImage.getString(id);
								 }
								 
								 friendDataList.add(new FriendInfo(id,name, imageURL));
								System.out.println(friendDataList);
							 }
							
						}
						
					}
				}
				
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			llLayoutSearch.removeView(pBar);
			getActionBar().setTitle("Search Results");
			if(friendDataList.size()==0)
				noResults.setVisibility(View.VISIBLE);
			adapter = new FriendListAdapter(friendDataList,SearchFriend.this);
			friendListView.setAdapter(adapter);
		}

	}

}
