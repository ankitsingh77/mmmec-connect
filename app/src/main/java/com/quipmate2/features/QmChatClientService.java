package com.quipmate2.features;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.quipmate2.constants.AppProperties;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;

public class QmChatClientService extends Service {

	Session session;
	String website = "http://www.quipmate.com/", url, sessionid, myprofileid,
			data, online_data;
	HttpPost post;
	List<NameValuePair> nameValuePairs;
	HttpResponse rChatOnline;
	int status;
	String last_poll_time;
	public static final String SENDDATA = "com.quipmate2.features";
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		session = new Session(getApplicationContext());
		last_poll_time = session.getValue("last_poll_time");
		sessionid = session.getValue("sessionid");
		myprofileid = session.getValue("profileid");
		new QmChatOnline().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}
	
	class QmChatOnline extends AsyncTask<Void, Void, Void> {
		JSONArray user;
		JSONObject pname, photo;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			System.out.println("Sevice QmChatOnline! onPreExecute");
			url = website + "chat/real_time";
			System.out.println(url);
			post = new HttpPost(url);
			
			nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair(AppProperties.DATABASE, session.getValue(AppProperties.DATABASE)));
			nameValuePairs.add(new BasicNameValuePair("profileid", myprofileid));
			nameValuePairs.add(new BasicNameValuePair("last_poll_time", last_poll_time));
			
			
		}

		@Override
		protected Void doInBackground(Void... params) {

			try {
				post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				System.out.println("Service online Intent Started");
				HttpClient client = null;
				client = new DefaultHttpClient();
				rChatOnline = client.execute(post);
				status = rChatOnline.getStatusLine().getStatusCode();
				System.out.println("Stage -1");
				
				if (status == 200) {
					
					System.out.println("Online Satus 200 obtained from Quipmate");
					
					HttpEntity e = rChatOnline.getEntity();
					System.out.println("Stage 1");
					online_data = EntityUtils.toString(e);
					
					JSONObject result = new JSONObject(online_data);
					
					user = result.getJSONArray("user");
					//System.out.println(user);
					pname = result.getJSONObject("name");
					//System.out.println(pname);
					photo = result.getJSONObject("photo");
					//System.out.println(photo);
					last_poll_time = result.getString("last_poll_time");
					session.setValue("last_poll_time", last_poll_time);
					session.commit();
					
				} else {
					System.out.println("Status is not 200 " + status);
				}
			} catch (UnknownHostException e) {
				System.out
						.println("Unable to reach Quipmate. Check Internet Connection !");
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				System.out.println("Some Unknown exception occured");
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
			System.out.println("Sevice QmChatUpdate has been cancelled");
		}

		@Override
		protected void onPostExecute(Void params) {
			super.onPostExecute(params);
			System.out
					.println("Sevice QmChatOnline has been executed ! onPostExecute");
			
			sendResultToActivity();
			new QmChatOnline().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		}

		private void sendResultToActivity() {
			// TODO Auto-generated method stub
			Intent send = new Intent(SENDDATA);
			if(user != null){
				send.putExtra("user", user.toString());
				send.putExtra("pname", pname.toString());
				send.putExtra("photo", photo.toString());
				sendBroadcast(send);
				System.out.println("broadcast send");
			}
		}
	}
@Override
public void onDestroy() {
	// TODO Auto-generated method stub
	System.out.println("Service destroyed");
	super.onDestroy();
}

}
