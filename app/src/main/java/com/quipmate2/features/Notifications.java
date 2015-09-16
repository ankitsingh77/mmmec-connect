package com.quipmate2.features;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import com.example.quipmate2.R;
import com.quipmate2.adapter.NotifsAdapter;
import com.quipmate2.constants.AppProperties;
import com.quipmate2.utils.CommonMethods;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.AbsListView.OnScrollListener;

public class Notifications extends Activity implements OnClickListener, OnItemClickListener {

	private ListView lvNotifs;
	private ArrayList<NotificationInfo> notifList;
	private Session session;
	private JSONArray result, action;
	private JSONObject data, name, pimage;
	private NotifsAdapter adapter;
	private int start,notif_count=0;
	HashMap<String, String> hashmap; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.notifications);
		
		getActionBar().setTitle("Notifications");
		getActionBar().setDisplayHomeAsUpEnabled(true);
		init();	
		hashmap = new HashMap<String, String>();
		lvNotifs.setOnScrollListener(new EndlessScrollListener());
		lvNotifs.setOnItemClickListener(this);
		
		new NotifFetch().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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
	
	public class EndlessScrollListener implements OnScrollListener {

	    private int visibleThreshold = 5;
	    @SuppressWarnings("unused")
		private int currentPage = 0;
	    private int previousTotal = 0;
	    private boolean loading = true;

	    public EndlessScrollListener() {
	    }
	    public EndlessScrollListener(int visibleThreshold) {
	        this.visibleThreshold = visibleThreshold;
	    }

	    @Override
	    public void onScroll(AbsListView view, int firstVisibleItem,
	            int visibleItemCount, int totalItemCount) {
	        if (loading) {
	            if (totalItemCount > previousTotal) {
	                loading = false;
	                previousTotal = totalItemCount;
	                currentPage++;
	            }
	        }
	        if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
	            // I load the next page of gigs using a background task,
	            // but you can call any function here.
	        	start+=20;
	            new NotifFetch().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	            loading = true;
	        }
	    }

	    @Override
	    public void onScrollStateChanged(AbsListView view, int scrollState) {
	    }
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		finish();
	}

	
	private void init() {
		// TODO Auto-generated method stub
		lvNotifs = (ListView) findViewById(R.id.lv_notifs);
		start = 0;
		notifList = new ArrayList();
		session = new Session(this);
		notifList = new ArrayList<NotificationInfo>();
		adapter = new NotifsAdapter(notifList, Notifications.this);
		lvNotifs.setAdapter(adapter);
	}

	public class NotifFetch extends AsyncTask<Void, Void, Void>{

		private ProgressDialog progress;

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			setProgressBarIndeterminateVisibility(true);
		}
		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			progress = new ProgressDialog(getApplicationContext());
			 progress.setMessage("Downloading...");
		      progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		      progress.setIndeterminate(true);
		      progress.show();
			try{
			ArrayList<NameValuePair> apiParams = new ArrayList<NameValuePair>();
			apiParams.add(new BasicNameValuePair(AppProperties.ACTION, "notice_fetch"));
			apiParams.add(new BasicNameValuePair(AppProperties.PROFILE_ID,
					session.getValue(AppProperties.PROFILE_ID)));
			apiParams.add(new BasicNameValuePair("start", start+""));
			
			result = CommonMethods.loadJSONData(AppProperties.URL, AppProperties.METHOD_GET, apiParams);
			if(result!=null){
				data = result.getJSONObject(0);
				action = data.getJSONArray(AppProperties.ACTION); 
				name = data.getJSONObject(AppProperties.NAME);
				pimage = data.getJSONObject(AppProperties.PROFILE_IMAGE);
				
				for(int i=0;i<action.length();i++){
					JSONObject temp = action.getJSONObject(i);
					
					String actiontype = null, time=null;
					FriendInfo actionby = new FriendInfo();
					FriendInfo postby = new FriendInfo();
					if(temp != null){
						time = temp.getString("time");
						Log.e("Notofications count !!!!!!!!!!!!!!!!!!!!!!!!!!!!",notif_count+"");
						hashmap.put("actionid"+notif_count, temp.getString("pageid").toString());
						hashmap.put("life_is_fun"+notif_count, temp.getString("life_is_fun").toString());
						
						actiontype = temp.getString("actiontype");
						
						String actionbytemp = temp.getString("actionby");
						String postbytemp = temp.getString("postby");
						
						actionby.setName(name.getString(actionbytemp));
						actionby.setImageURL(pimage.getString(actionbytemp));
						
						postby.setName(name.getString(postbytemp));
						postby.setImageURL(pimage.getString(postbytemp));
						notif_count++;
					}
					
					notifList.add(new NotificationInfo( actiontype, actionby, postby, time));
				}
			}
			}
			catch(Exception e){
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			setProgressBarIndeterminateVisibility(false);
			int currentPos=lvNotifs.getFirstVisiblePosition();
			/*adapter = new NotifsAdapter(notifList, Notifications.this);
			lvNotifs.setAdapter(adapter);*/
			adapter.notifyDataSetChanged(); 
			lvNotifs.setSelectionFromTop(currentPos, 0);
			
			  progress.dismiss();
			  progress.cancel();
		}
}


	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Log.e("Notification CLicked", position+" "+hashmap.get("actionid"+position));
		Intent i = new Intent(Notifications.this, Post.class); 	
		i.putExtra("actionid", hashmap.get("actionid"+position));
		i.putExtra("life_is_fun", hashmap.get("life_is_fun"+position));
		startActivity(i);
		System.out.println("Notifications clicked");
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}
}
