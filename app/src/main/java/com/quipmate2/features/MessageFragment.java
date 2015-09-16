package com.quipmate2.features;


import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;

import com.example.quipmate2.R;
import com.quipmate2.adapter.MessageSendAdapter;
import com.quipmate2.constants.AppProperties;
import com.quipmate2.utils.CommonMethods;

import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.AbsListView.OnScrollListener;

public class MessageFragment extends Fragment implements OnKeyListener, OnClickListener{

	private ListView lvMsg;
	private List<MsgInfo> msgfrnd;
	private Session session;
	private MessageSendAdapter adapter;
	private String actionBarTitle;
	private int start;
	EditText et_msg;
	private List<NameValuePair> apiParams;
	private HttpPost post;
	private HttpResponse rChatUpdate;
	private int status;
	private String url = "http://www.quipmate.com/chat/chat_new";
	String msg="WOW", userid="1000000002";
	private String last_chat_time="1409486777901";
	LocalBroadcastManager broadcaster ;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
		//userid = savedInstanceState.getString("userid");
		
		View view =  inflater.inflate(R.layout.messages_content_frag, 
                container, false);
		
		lvMsg = (ListView) view.findViewById(R.id.lv_msg_frag); 
		
		View view1 =  inflater.inflate(R.layout.edit_text_frag, 
                container, false);
		
		et_msg = (EditText)view1.findViewById(R.id.et_msg);
		//Button bt_send_msg  = (Button) view1.findViewById(R.id.bt_send_msg);
		session = new Session(getActivity());
		msgfrnd = new ArrayList<MsgInfo>();
		start = 0;
		adapter = new MessageSendAdapter(msgfrnd, getActivity());
		lvMsg.setAdapter(adapter);
		lvMsg.setOnScrollListener(new EndlessScrollListener());
		lvMsg.setStackFromBottom(true);
		et_msg.setOnKeyListener(new OnKeyListener() {
			
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// TODO Auto-generated method stub
				System.out.println("Working !!!");
				if (event.getAction() == KeyEvent.ACTION_DOWN)
		        {
		            switch (keyCode)
		            {
		                case KeyEvent.KEYCODE_DPAD_CENTER:
		                case KeyEvent.KEYCODE_ENTER:
		                    Log.e("Seding Chat", "Sending CHat");
		                    new SendChat().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		                    
		                    return true;
		                default:
		                    break;
		            }
		        }
				return false;
			}
		});
		/*
		bt_send_msg.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new SendChat().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			}
		});
		
		*/
		new fetchMsgFriend().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		
	
        return view1;
	}

	 class SendChat extends AsyncTask<Void, Void, Void>{
			
			@Override
			protected void onPreExecute() {
				// TODO Auto-generated method stub
				msg = et_msg.getText().toString();
				et_msg.setText("");
			}
			@Override
			protected Void doInBackground(Void... params) {
				// TODO Auto-generated method stub
				try{
						
						apiParams = new ArrayList<NameValuePair>();
						apiParams.add(new BasicNameValuePair(AppProperties.DATABASE, session.getValue(AppProperties.DATABASE)));
						apiParams.add(new BasicNameValuePair("profileid", session.getValue(AppProperties.PROFILE_ID)));  
						apiParams.add(new BasicNameValuePair("chat_sent_time", last_chat_time));
						apiParams.add(new BasicNameValuePair("userid", userid));
						apiParams.add(new BasicNameValuePair("name", "Brijesh Kushwaha"));
						apiParams.add(new BasicNameValuePair("message", msg));
						apiParams.add(new BasicNameValuePair("photo", "https://ebdd192075d95c350eef-28241eefd51f43f0990a7c61585ebde0.ssl.cf2.rackcdn.com/1000000002_1405785647.jpg"));
						apiParams.add(new BasicNameValuePair("auth", session.getValue(AppProperties.PROFILE_ID)));
						apiParams.add(new BasicNameValuePair("PHPSESSID", session.getValue(AppProperties.SESSION_ID)));
						Log.e("Chat NEW Parameters", apiParams.toString());
						
						Log.e("url", url);
						
						 
						
						post = new HttpPost(url);
						post.addHeader("PHPSESSID", session.getValue(AppProperties.SESSION_ID));
						Log.e("PHPSESSID",session.getValue(AppProperties.SESSION_ID));
						post.setEntity(new UrlEncodedFormEntity(apiParams));
						  BasicCookieStore cookieStore = new BasicCookieStore();
						  HttpClient client = new DefaultHttpClient();
						    // Create local HTTP context
						    HttpContext localContext = new BasicHttpContext();
						    // Bind custom cookie store to the local context
						    localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
						
						
						rChatUpdate = client.execute(post);
						status = rChatUpdate.getStatusLine().getStatusCode();
						System.out.println("Stage -1");
						if (status == 200) {
							System.out.println("Satus 200 obtained from Quipmate"); 
							HttpEntity e = rChatUpdate.getEntity();
							System.out.println("Stage 1");
							String d = EntityUtils.toString(e);
							if(d != null){
									
									Log.e("Chat", d.toString());
									 
							}
						}
				}	
				 catch (Exception e) {
						System.out.println("Some Unknown exception occured");
						e.printStackTrace();
					}
				return null;
			}
			
			@Override
			protected void onPostExecute(Void result) {
				// TODO Auto-generated method stub
				
				//Log.e("Rerunred Data", data.toString());
				
				
			}
		}
	 
		
	 @SuppressWarnings("unused")
	public class EndlessScrollListener implements OnScrollListener {

	    public EndlessScrollListener() {
	    }
	    public EndlessScrollListener(int visibleThreshold) {
	    }

	    @Override
	    public void onScroll(AbsListView view, int firstVisibleItem,
	            int visibleItemCount, int totalItemCount) {
	    	if(firstVisibleItem == 0){
	    		start+=10;
	    		new fetchMsgFriend().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	    	}
	    }

	    @Override
	    public void onScrollStateChanged(AbsListView view, int scrollState) {
	    }
	}
	
	class fetchMsgFriend extends AsyncTask<Void, Void, Void>{
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			getActivity().setProgressBarIndeterminateVisibility(true);
		}
		@Override
		protected Void doInBackground(Void... params) {
			try{
					apiParams = new ArrayList<NameValuePair>();
					apiParams.add(new BasicNameValuePair(AppProperties.DATABASE, "profile"));
					apiParams.add(new BasicNameValuePair("profileid", session.getValue(AppProperties.PROFILE_ID)));  
					apiParams.add(new BasicNameValuePair("last_chat_time", last_chat_time));
					apiParams.add(new BasicNameValuePair("auth", session.getValue(AppProperties.PROFILE_ID)));
					Log.e("Chat Update Parameters", apiParams.toString());
					
					JSONArray data = CommonMethods.loadJSONData(url, "post", apiParams);
						if(data != null){
								
							Log.e("Chat", data.toString());
								 
						}
				}
			 catch (Exception e) {
					System.out.println("Some Unknown exception occured");
					e.printStackTrace();
				}
			return null;
			}
		
		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			
			//change the action bar title to the name of the friend
			getActivity().getActionBar().setTitle(actionBarTitle);
			getActivity().setProgressBarIndeterminateVisibility(false);
			adapter.notifyDataSetChanged();
			if(start == 0){
				//lvMsg.smoothScrollToPosition(adapter.getCount());
			}
			else{
				//lvMsg.smoothScrollToPosition(0);
			}
			
		}
	}


	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		System.out.println("OWOOWOWOOWOWOWOOOOOOOOOOOOOOOOWOWOOWOOOWOOWOOWOOOOOWOOOW");
		return false;
	}


	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}
}
