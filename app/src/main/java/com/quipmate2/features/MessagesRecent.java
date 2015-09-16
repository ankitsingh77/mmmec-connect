package com.quipmate2.features;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.quipmate2.R;
import com.quipmate2.adapter.MessageAdapter;
import com.quipmate2.constants.AppProperties;
import com.quipmate2.features.ChatService.ChatUpdate;
import com.quipmate2.features.Message.ReciverChangeProgress;
import com.quipmate2.utils.CommonMethods;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class MessagesRecent extends Activity {

	private ListView lvmsg;
	private List<MsgInfo> msgs;
	private Session session;
	private JSONArray result, action;
	private JSONObject data, names, pimage;
	private MessageAdapter adapter;
	BroadcastReceiver receiver;
	
	
	 public class ReciverChangeProgress extends BroadcastReceiver{

		    @Override
		    public void onReceive(Context arg0, Intent arg1) {
		        // TODO Auto-generated method stub
		        android.util.Log.e("ReciverChagne", "IReciver");
		    }

		}

		public ReciverChangeProgress reciverChangeProgress;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.messages_recent);
		getActionBar().setTitle("Messages");
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		lvmsg = (ListView)findViewById(R.id.msgrecent);
		
		msgs = new ArrayList();
		session = new Session(this);
		reciverChangeProgress = new ReciverChangeProgress();
		receiver = new BroadcastReceiver() {
	        @Override
	        public void onReceive(Context context, Intent intent) {
	        	System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
	            String s = intent.getStringExtra(COPAService.COPA_MESSAGE);
	            Log.e("Receiver", s);
	            Log.e("Received DATA", "got it");
	        }
	    };
		
	    System.out.println(receiver.getResultData()+"kook");
	    
		lvmsg.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Intent openmsg = new Intent(MessagesRecent.this,Message.class);
				MsgInfo selectedMsg =(MsgInfo) lvmsg.getAdapter().getItem(position);
				String frndId = selectedMsg.msgBy.getId();
				
				openmsg.putExtra("friendId", frndId);
				openmsg.putExtra("friendName", selectedMsg.msgBy.getName());
				openmsg.putExtra("friendImgUrl", selectedMsg.msgBy.getImageURL());
				startActivity(openmsg);
				//Toast.makeText(MessagesRecent.this, frndId, Toast.LENGTH_LONG).show();
			} 
			
		
		});
		new FetchMsg().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		
	}
	

	@Override
	protected void onStart() {
	    super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter());
	}

	@Override 
	protected void onStop() {
		super.onStop();
	    LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
	}
	
	
	public void onResume() {
		super.onResume();
	    LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter());
	}


	public void onPause() {
		super.onPause();
	    LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
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
	
	class FetchMsg extends AsyncTask<Void, Void, Void>{

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			setProgressBarIndeterminateVisibility(true);
		}
		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			try{
				
				Log.e("tryong to make a new connectgion", "tryong to make a new connectgion");
			List<NameValuePair> apiParams = new ArrayList<NameValuePair>();
			apiParams.add(new BasicNameValuePair(AppProperties.ACTION, "message_recent_fetch"));
			apiParams.add(new BasicNameValuePair(AppProperties.PROFILE_ID, session.getValue(AppProperties.PROFILE_ID)));
			apiParams.add(new BasicNameValuePair("auth", session.getValue(AppProperties.PROFILE_ID)));
			Log.e("message", apiParams.toString());
			result = CommonMethods.loadJSONData(AppProperties.URL, AppProperties.METHOD_GET, apiParams);
			if(result != null){
				data = result.getJSONObject(0);
				Log.e("Recent Message", data.toString());
				action = data.getJSONArray(AppProperties.ACTION);
				names = data.getJSONObject(AppProperties.NAME);
				pimage = data.getJSONObject(AppProperties.PROFILE_IMAGE);
				
				if(action != null){
					JSONObject temp;
					String by, on, msg, time, imgurl, name;
					for(int i = 0;i < action.length(); i++){
						temp = action.getJSONObject(i);
						//System.out.println(temp);
						
						if(temp != null){
						by = temp.getString("actionby");
						on = temp.getString("actionon");
						msg = temp.getString("message");
						time = temp.getString("time");
						
						//to store the id of the friend in the conversation.
						String id;
						
						if(session.getValue(AppProperties.PROFILE_ID).equals(by)) id = on;  
						else id = by;
						name = names.getString(id); 
						imgurl = pimage.getString(id);
						
						msgs.add(new MsgInfo(new FriendInfo(id, name, imgurl), time, msg)); 
						}
					}
				}
			}
			}catch(JSONException e){
				e.printStackTrace();
			}
			return null;
		}
		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			setProgressBarIndeterminateVisibility(false);
			adapter = new MessageAdapter(msgs, MessagesRecent.this);
			lvmsg.setAdapter(adapter);
			
			//adapter.notifyDataSetChanged();
		}
		
	}
}
