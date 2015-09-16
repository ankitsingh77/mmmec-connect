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
import com.quipmate2.utils.NetworkHelper;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;


public class FriendsFragment extends Fragment {
	
	
	private JSONArray result,friendsId;
	private JSONArray jsonTask;
	private JSONObject  friendsName, friendsImage;
	private ListView friendListView;
	private Session session;
	private String action = "friend_fetch_incremental";
	private int friendsCount;
	private Button btnLoadMore,btSearch;
	private EditText searchFreind;
	NetworkHelper connection=new NetworkHelper();
	
	private List<FriendInfo> friendDataList;
	private FriendListAdapter adapter;
	
	private int currentFriendCount;
	LinearLayout llayout;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		currentFriendCount=0;
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub		
		View view = inflater.inflate(R.layout.friends_fragment, null);
		friendListView = (ListView) view.findViewById(R.id.friendListView);
		llayout = (LinearLayout) view.findViewById(R.id.friend_layout);
		searchFreind = (EditText) view.findViewById(R.id.search);
		btSearch = (Button) view.findViewById(R.id.btsearch);
		friendDataList=new ArrayList<FriendInfo>();
		adapter = new FriendListAdapter(friendDataList,getActivity());
		
		View viewButton= inflater.inflate(R.layout.button_load_more, null);
		
		//button to be added at the end of listview
		btnLoadMore = (Button) viewButton.findViewById(R.id.btnLoadMore);
		btnLoadMore.setText(getResources().getString(R.string.load_more_friends));
		btnLoadMore.setTextColor(Color.WHITE);
		
		friendListView.addHeaderView(new View(getActivity()));
		friendListView.addFooterView(btnLoadMore);
		friendListView.setAdapter(adapter);
		btnLoadMore.setVisibility(View.INVISIBLE);
			
		new loadMoreFriends().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		
		btnLoadMore.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(NetworkHelper.checkNetworkConnection(getActivity())){
						currentFriendCount+=20;	
						if(currentFriendCount>=(friendsCount)){
							CommonMethods.ShowInfo(getActivity(),
									getString(R.string.friendlist_complete)).show();
						}
						else{
							new loadMoreFriends().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
						}
				}
				else{
					CommonMethods.ShowInfo(getActivity(),
							getString(R.string.network_error)).show();
				}
			}
		});
		
		btSearch.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(NetworkHelper.checkNetworkConnection(getActivity())){
				Intent searchIntent=new Intent(getActivity(), SearchFriend.class);
		          String searchQuery=searchFreind.getText().toString();
		          if(!searchQuery.equals(null) && !searchQuery.equals("")){
		        	  searchIntent.putExtra("search_name", searchQuery);
		        	  startActivity(searchIntent);
			}
				}
				else{
					CommonMethods.ShowInfo(getActivity(),
							getString(R.string.network_error)).show();
				}
				
			}
		});
		
		return view;
	}
	
	public class loadMoreFriends extends AsyncTask<Void, Void, Void> {
		ProgressBar pBar;

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			pBar = new ProgressBar(
					getActivity(),null,android.R.attr.progressBarStyleLarge); 
			pBar.setIndeterminate(true);
			llayout.addView(pBar);
			btnLoadMore.setText("Loading...");
		}
		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			session = new Session(getActivity());
			List<NameValuePair> apiParams = new ArrayList<NameValuePair>();
			apiParams.add(new BasicNameValuePair(AppProperties.START, currentFriendCount+""));
			apiParams.add(new BasicNameValuePair(AppProperties.ACTION, action));
			apiParams.add(new BasicNameValuePair(AppProperties.PROFILE_ID, session
					.getValue(AppProperties.PROFILE_ID)));
			jsonTask = CommonMethods.loadJSONData(AppProperties.URL, AppProperties.METHOD_GET,
					apiParams);
			
			try {
				result = jsonTask;
				if(result != null){
				JSONObject data = result.getJSONObject(0);
				//System.out.println(data);
				if (data != null) {
					if (!data.has(getString(R.string.error))) {
						friendsId = data.getJSONArray(AppProperties.ACTION);
						friendsName = data.getJSONObject(AppProperties.NAME);
						friendsImage = data.getJSONObject(AppProperties.PROFILE_IMAGE);
						friendsCount = Integer.parseInt(data.getString(AppProperties.FRIEND_COUNT));
						
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
								// System.out.println(friendDataList);
							 }
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
			try{
				llayout.removeView(pBar);
				btnLoadMore.setText(getResources().getString(R.string.load_more_friends));
				
				// get listview current position - used to maintain scroll position
				int currentPos=friendListView.getFirstVisiblePosition();
				
				adapter.notifyDataSetChanged();
				btnLoadMore.setVisibility(View.VISIBLE);
				// Setting new scroll position
				friendListView.setSelectionFromTop(currentPos, 0);
				}
				catch(Exception e){
					e.printStackTrace();
				}
		}

	}
	
}
