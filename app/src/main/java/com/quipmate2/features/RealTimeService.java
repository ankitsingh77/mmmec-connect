package com.quipmate2.features;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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

public class RealTimeService extends Service{

	private Session session;
	private List<NameValuePair> apiParams;
	private String database;
	private Random rand;
	private HttpPost post;
	private HttpResponse rChatUpdate;
	private int status;
	private String url = "http://www.quipmate.com/chat/real_time", data;
	private String last_chat_time, notif_count="0";
	LocalBroadcastManager broadcaster ;
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		System.out.println("Real Time service started");
		session = new Session(getApplicationContext());
		database = session.getValue(AppProperties.DATABASE);
		last_chat_time = -1+"";
		rand = new Random();
		broadcaster = LocalBroadcastManager.getInstance(RealTimeService.this);
		//HttpParams httpParameters = new BasicHttpParams();
		//HttpConnectionParams.setConnectionTimeout(httpParameters, 1000);
		//HttpConnectionParams.setSoTimeout(httpParameters, 1000);
		new RealTimeUpdate().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}
	

    class RealTimeUpdate extends AsyncTask<Void, Void, Void>{

    	@Override
    		protected void onPreExecute() { 
    			// TODO Auto-generated method stub 
    			super.onPreExecute();
    			System.out.println("Real Time Update pre execute.");
    		} 
    	
	@Override
	protected Void doInBackground(Void... params) {
		// TODO Auto-generated method stub
		try{
			
			if (NetworkHelper.checkNetworkConnection(RealTimeService.this)) {
				
				apiParams = new ArrayList<NameValuePair>();
				apiParams.add(new BasicNameValuePair(AppProperties.DATABASE, session.getValue(AppProperties.DATABASE)));
				apiParams.add(new BasicNameValuePair("profileid", session.getValue(AppProperties.PROFILE_ID)));  
				apiParams.add(new BasicNameValuePair("random", rand.nextInt(1000000000)+""));
				apiParams.add(new BasicNameValuePair("last_poll_time", last_chat_time));
				Log.e("Real Time Parameters", apiParams.toString());
				
				post = new HttpPost(url);
				System.out.println(url);
				post.setEntity(new UrlEncodedFormEntity(apiParams));
				HttpClient client = new DefaultHttpClient();
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
							//sendResult(data.toString());
							 
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
		 final String COPA_RESULT = "com.controlj.copame.backend.COPAService.REQUEST_PROCESSED";
		 Log.e("Sending Broadcast", "broadcasting");
	     Intent intent = new Intent(COPA_RESULT);
	     if(message != null)
	         intent.putExtra("COPA_MESSAGE", message);
	     broadcaster.sendBroadcast(intent);
	 }
	
	@Override
	protected void onPostExecute(Void result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		System.out.println("real time update post executed");
		System.out.println("data obtained = "+data);
		JSONArray a,ca;
		JSONObject ao,co,cn;
		String b,type = null,message=null,name= null,n=null;
		if(data != null)
		{	
			try { 
				Object json = new JSONTokener(data).nextValue(); 
				if (json instanceof JSONObject)
				{
					ao = new JSONObject(data);
					last_chat_time = ao.getString("last_poll_time");
					notif_count = ao.getString("count");
				} 
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			if(!notif_count.equals("0"))
			{	
				NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
				Intent intent = new Intent(RealTimeService.this, Notifications.class);
				 PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);
				Notification notification = new Notification(R.drawable.ic_launcher,"Quipmate",SystemClock.currentThreadTimeMillis());
				notification.setLatestEventInfo(RealTimeService.this, "Quipmate", "You have" +" "+notif_count+" "+"notification", PendingIntent.getActivity(RealTimeService.this, 1, intent, 1));
				notification.flags |=  Notification.FLAG_AUTO_CANCEL;
				notificationManager.notify(0,notification);
				try {
				    Uri notification1 = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
				    Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification1);
				    r.play();
				} catch (Exception e) { 
				    e.printStackTrace();
				}
			}
		}
		Log.e("Poll Time", last_chat_time);
		new RealTimeUpdate().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}
	
}
    
    @Override
    public void onDestroy() {
    	// TODO Auto-generated method stub
    	super.onDestroy();
    	System.out.println("real time service stopped");
    }

}
