package com.quipmate2.features;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeoutException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.example.quipmate2.R;
import com.quipmate2.constants.AppProperties;
import com.quipmate2.utils.NetworkHelper;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

public class ChatService extends Service {

	private Session session;
	private List<NameValuePair> apiParams;
	private String database;
	private Random rand;
	private HttpPost post;
	private HttpResponse rChatUpdate;
	private int status;
	private String url = "http://www.quipmate.com/chat/chat_update", data="Sample Data";
	private String last_chat_time;
	LocalBroadcastManager broadcaster ;
	public ChatUpdate chatupdate;
	HttpClient client;
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		System.out.println("Chat service started");
		session = new Session(getApplicationContext());
		database = session.getValue(AppProperties.DATABASE);
		last_chat_time = -1+"";
		rand = new Random();
		broadcaster = LocalBroadcastManager.getInstance(ChatService.this);
		post = new HttpPost(url);

		//HttpParams httpParameters = new BasicHttpParams();
		//HttpConnectionParams.setConnectionTimeout(httpParameters, 1000);
		//HttpConnectionParams.setSoTimeout(httpParameters, 1000);
		client = new DefaultHttpClient();
		chatupdate = new ChatUpdate();
		chatupdate.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}
	
	
	
    @Override
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		super.onStart(intent, startId); 
	}

    public void killChatUpdate(){
    	try{
    		
    	
		Log.e("Chat update", "Stopping aync task");
		post.abort(); 
		//client.getConnectionManager().shutdown();
		Log.e("Chat update", "Post is cancelled");
		new ChatUpdate().cancel(true); 
		Log.e("Chat update", "WOWOW Stooped");
    	}
    	catch(Exception e)
    	{
    		Log.e("Network Exception","Network Exception");
    	}
	}
    


	class ChatUpdate extends AsyncTask<Void, Void, Void>{

    	@Override
    		protected void onPreExecute() { 
    			// TODO Auto-generated method stub 
    			super.onPreExecute();
    			System.out.println("chat update pre execute.");
    		} 
    	
	@Override
	protected Void doInBackground(Void... params) {
		// TODO Auto-generated method stub
		try{
			
			if (NetworkHelper.checkNetworkConnection(ChatService.this)) {
				
				apiParams = new ArrayList<NameValuePair>();
				apiParams.add(new BasicNameValuePair(AppProperties.DATABASE, session.getValue(AppProperties.DATABASE)));
				apiParams.add(new BasicNameValuePair("profileid", session.getValue(AppProperties.PROFILE_ID)));  
				apiParams.add(new BasicNameValuePair("random", rand.nextInt(1000000000)+""));
				apiParams.add(new BasicNameValuePair("last_chat_time", last_chat_time));
				Log.e("Chat Update Parameters", apiParams.toString());
				
				
				System.out.println(url);
				post.setEntity(new UrlEncodedFormEntity(apiParams));
				
				

				//client.setParams(httpParameters);
				
				rChatUpdate = client.execute(post);
				
				status = rChatUpdate.getStatusLine().getStatusCode();
				System.out.println("Stage -1");
				if (status == 200) {
					System.out.println("Satus 200 obtained from Quipmate"); 
					HttpEntity e = rChatUpdate.getEntity();
					System.out.println("Stage 1");
					data = EntityUtils.toString(e);
					if(data != null){
							
							Log.e("Chat", data.toString());
							sendResult(data.toString());
							 
					}
				} else { 
					System.out.println("Status is not 200 " + status); 
				}
				}
			}
		 catch (Exception e) {
				System.out.println("Some Unknown exception occured");
				e.printStackTrace();
			}
		return null;
		}
	
	public void sendResult(String message) {
		 final String COPA_RESULT = "android.intent.action.MAIN";
		 Log.e("Sending Broadcast", "broadcasting");
	     Intent intent = new Intent(COPA_RESULT);
	     if(message != null)
	         intent.putExtra("COPA_MESSAGE", message);
	     Log.e("message", message);
	     broadcaster.sendBroadcast(intent);
	 }
	
	@Override
	protected void onPostExecute(Void result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		System.out.println("chat update post executed");
		System.out.println("data obtained = "+data);
		if(data != null)
		{
			JSONArray a,ca;
			JSONObject ao,co,cn;
			String b,type = null,message=null,name= null,n=null,sentby=null;
			try { 
				Object json = new JSONTokener(data).nextValue();
				if (json instanceof JSONObject)
				{
					ao = new JSONObject(data);
					b = ao.getString("action");
					ca = new JSONArray(b);
					b = ca.getString(0);
					co = new JSONObject(b);
					type = co.getString("type");
					message = co.getString("message");
					n = ao.getString("name"); 
					cn = new JSONObject(n);
					sentby = co.getString("sentby");
					name = cn.getString(co.getString("sentby"));
					//last_chat_time = cn.getString(co.getString("time"));
				}
				else if (json instanceof JSONArray)
				{
					a = new JSONArray(data); 
					b = a.getString(1);  
					co = new JSONObject(b);
					type = co.getString("type");
					message = co.getString("message");
					n = co.getString("name"); 
					cn = new JSONObject(n);
					sentby = co.getString("sentby");
					name = cn.getString(co.getString("sentby"));
					//last_chat_time = cn.getString(co.getString("time"));
				}
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			if(type != null && type.equalsIgnoreCase("3"))
			{	
				NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
				Intent intent = new Intent(ChatService.this, Message.class); 
				Log.e("IntentExtra", sentby);
				intent.putExtra("friendName",name);
				intent.putExtra("friendId", sentby);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
				PendingIntent pIntent = PendingIntent.getActivity(getApplicationContext(), PendingIntent.FLAG_UPDATE_CURRENT, intent, 0);
				Notification notification = new Notification(R.drawable.ic_launcher,"Quipmate",SystemClock.currentThreadTimeMillis());
				notification.setLatestEventInfo(ChatService.this, name+" "+"sent you a message", message, pIntent);
				notification.flags |=  Notification.FLAG_AUTO_CANCEL;
				notificationManager.notify(0,notification);
				try {  
				    Uri notification1 = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
				    Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification1); 
				    Log.e("Playing ringtone","ppaying ringtone");
				    r.play();
				} catch (Exception e) {
				    e.printStackTrace();
				}
			}
		}
		else
		{
			Log.e("Data is null", "Data is null");
		}
		data = null;
		new ChatUpdate().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}

	@Override
	protected void onCancelled() {
		// TODO Auto-generated method stub
		System.out.println("Killing the aync task");
		super.onCancelled();
		System.out.println("Async task stopped");
	}
	
}
    
	
	
    @Override
    public void onDestroy() {
    	// TODO Auto-generated method stub
    	chatupdate.cancel(true);
    	System.out.println("chat service stoping");
    	super.onDestroy();
    	System.out.println("chat service stopped");
    }
}
