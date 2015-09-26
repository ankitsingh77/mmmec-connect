package com.quipmate2.features;


import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.example.quipmate2.R;
import com.quipmate2.adapter.MessageSendAdapter;
import com.quipmate2.constants.AppProperties;
import com.quipmate2.features.CoWorkers.CoWorkerFetch;
import com.quipmate2.utils.CommonMethods;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Observable;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.NumberPicker;
import android.widget.ScrollView;
import android.widget.TextView;

public class Message extends Activity implements OnKeyListener, OnClickListener,OnTouchListener{
 
	private Session session;
	private MessageSendAdapter adapter;
	private String actionBarTitle;
	String start="0";
	EditText et_msg;
	private List<NameValuePair> apiParams;
	private HttpPost post;
	private HttpGet get;
	private HttpResponse rChatUpdate;
	private int status;
	private String url = "http://www.quipmate.com/chat/group_chat_new";
	private String URL = AppProperties.URL,photo=null;
	String msg="WOW", userid="1000";
	Drawable d;
	private String last_chat_time="1409486777901";
	LocalBroadcastManager broadcaster ;
	TextView tvPrev,tv;
	ImageView iv; 
	LinearLayout llPrev;
	LinearLayout lhlay;
	ScrollView scrllPrev;
	BroadcastReceiver receiver;
	JSONArray adata;
	Boolean loading = false;
	int i=0, scrollPosition=0;
	LinearLayout[] plhlay;
	ImageView [] piv;
	String title;
	
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
		setContentView(R.layout.window); 
		Intent intent = getIntent(); 
		Bundle idata = intent.getExtras();
		title = "MALAVIYAN CONNECT";
		scrllPrev = (ScrollView) findViewById(R.id.scrllPrev);
		llPrev = (LinearLayout)findViewById(R.id.llPrev);
		et_msg = (EditText)findViewById(R.id.et_msg);
		Button bt_send_msg  = (Button)findViewById(R.id.bt_send_msg);
		session = new Session(getApplicationContext()); 
		get = new HttpGet();
		start = "0";
		scrllPrev.setOnTouchListener(this);
		reciverChangeProgress = new ReciverChangeProgress();
		
		 receiver = new BroadcastReceiver() {
		        @Override
		        public void onReceive(Context context, Intent intent) {
		            String data = intent.getStringExtra("COPA_MESSAGE");
		            Log.e("Received DATA", "got it");
		            Log.e("Receiver", data);         
		            
		            JSONArray a,ca;
		    		JSONObject ao,co,cn;
		    		String b,type = null,message=null,name= null,n=null,p=null,image=null,sentby=null,sentto;
		    				try {
								ao = new JSONObject(data);
								b = ao.getString("action");
			    				ca = new JSONArray(b);
			    				b = ca.getString(0);
			    				co = new JSONObject(b);
			    				type = co.getString("type");
			    				sentby = co.getString("sentby");
			    				sentto = co.getString("sentto");
			    				n = ao.getString("name");
			    				cn = new JSONObject(n);
			    				name = cn.getString(sentby);
			    				if(userid.equalsIgnoreCase(sentby))
					            {
				    				if(type.equalsIgnoreCase("3"))
				    				{
				    					setTitle(name);
					    				message = co.getString("message");
					    				p = ao.getString("photo");
					    				cn = new JSONObject(p);
					    				image = cn.getString(co.getString("sentby"));
							            
											tv = new TextView(Message.this); 
											tv.setBackgroundColor(Color.rgb(202, 213, 228));
											tv.setPadding(10, 10, 10, 10);
											lhlay = new LinearLayout(Message.this);
											LinearLayout lhlay = new LinearLayout(Message.this);
											lhlay.setPadding(5, 5, 5, 5);
											tv.setText(message);
											iv = new ImageView(Message.this);
											LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(60, 60);
											layoutParams.setMargins(5, 0, 10, 0);
											iv.setLayoutParams(layoutParams);
											new single_image_fetch().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
											lhlay.setPadding(5, 5, 5, 5);
											lhlay.addView(iv);
											lhlay.addView(tv);
											llPrev.addView(lhlay);
											scrllPrev.fullScroll(ScrollView.FOCUS_DOWN);
							            }
							            else if(type.equalsIgnoreCase("2"))
							            {
							            	setTitle(name+" "+"is typing");
							            }
							            else
							            {
							            	Log.e("Unknown Type","Unknown Type");
							            }
			    				}
			    				else if(userid.equalsIgnoreCase(sentto) && type.equalsIgnoreCase("1"))
						        {
			    					setTitle(name+" "+"saw your message");
			    				}
			    				else
			    				{
			    					Log.e("Message is not sent by the person you are chatting with", "Message is not sent by the person you are chatting with");
			    				}
		    				} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
		        }
		    };
		    
		  LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter("android.intent.action.MAIN"));
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
		                    if(!et_msg.getText().toString().trim().equalsIgnoreCase(""))
		                    {	
		                    	new SendChat().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		                    }	
		                    return true;
		                default: 
		                    break;
		            }
		        }
				return false;
			}
		});
		
		
		bt_send_msg.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(!et_msg.getText().toString().trim().equalsIgnoreCase(""))
                {	
                	new SendChat().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }
			}
		});
		
		new fetchMsgFriend().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}

	
	
	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.onNewIntent(intent);
		Bundle idata = intent.getExtras();
		userid = "1000000122";
		Log.e("new Intent","ON NEW INTENTNTNTNNTNTNNTNTNTNTNTNTNTNTNTNTNTNNTNTNTNTNTNTNTNTNTNTNTNTNTNTNNT");
		title = "KUNAL SINGH";
		llPrev.removeAllViews();
		new fetchMsgFriend().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}



	@Override
	protected void onStart() {
	    super.onStart();
	    LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter("android.intent.action.MAIN"));
	}

	@Override 
	protected void onStop() {
		super.onStop();
	    LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
	}
	
	
	public void onResume() {
		super.onResume();
		LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
	    LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter("android.intent.action.MAIN"));
	}


	public void onPause() {
		super.onPause();
	    LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
	}
	
	 class SendChat extends AsyncTask<Void, Void, Void>{
			
			@Override
			protected void onPreExecute() {
				// TODO Auto-generated method stub
				tv = new TextView(Message.this);
				tv.setBackgroundColor(Color.rgb(223, 238, 207));
				tv.setPadding(10, 10, 10, 10);
				lhlay = new LinearLayout(Message.this);
				LinearLayout lhlay = new LinearLayout(Message.this);
				lhlay.setPadding(5, 5, 5, 5);
				msg = et_msg.getText().toString();
				et_msg.setText("");
				tv.setText(msg);
				lhlay.setPadding(5, 5, 5, 5);
				lhlay.addView(tv);
				lhlay.setGravity(Gravity.RIGHT);
				llPrev.addView(lhlay);
				scrllPrev.fullScroll(ScrollView.FOCUS_DOWN);
			}
			
			
			
			@Override
			protected Void doInBackground(Void... params) {
				// TODO Auto-generated method stub
				try{
						
						apiParams = new ArrayList<NameValuePair>();
						apiParams.add(new BasicNameValuePair("profileid", session.getValue(AppProperties.PROFILE_ID)));  
						apiParams.add(new BasicNameValuePair("chat_sent_time", last_chat_time));
						apiParams.add(new BasicNameValuePair("userid", userid));
						apiParams.add(new BasicNameValuePair("name", session.getValue(AppProperties.NAME)));
						apiParams.add(new BasicNameValuePair("message", msg));
						apiParams.add(new BasicNameValuePair("photo", session.getValue(AppProperties.MY_PROFILE_PIC)));
						apiParams.add(new BasicNameValuePair("auth", session.getValue(AppProperties.PROFILE_ID)));
						apiParams.add(new BasicNameValuePair("PHPSESSID", session.getValue(AppProperties.SESSION_ID)));
					    apiParams.add(new BasicNameValuePair("database","mmmut"));
						Log.e("Chat NEW Parameters", apiParams.toString());
						
						Log.e("url", url);
						post = new HttpPost(url);
						post.addHeader("PHPSESSID", session.getValue(AppProperties.SESSION_ID));
						Log.e("PHPSESSID",session.getValue(AppProperties.SESSION_ID));
						post.setEntity(new UrlEncodedFormEntity(apiParams));
						BasicCookieStore cookieStore = new BasicCookieStore();
					    HttpClient client = new DefaultHttpClient();
				        HttpContext localContext = new BasicHttpContext();
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
				scrllPrev.fullScroll(ScrollView.FOCUS_DOWN);
				
			}
		}
	
	class fetchMsgFriend extends AsyncTask<Void, Void, Void>{
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			setProgressBarIndeterminateVisibility(true);
			//Log.e("IntentExtraaaaaaaaaaaaaaaaaaaaaaaaaa from message _ fetch", userid);
		}
		@Override 
		protected Void doInBackground(Void... params) {
					
			List<NameValuePair> apiParams = new ArrayList<NameValuePair>();
			apiParams.add(new BasicNameValuePair(AppProperties.ACTION, "group_chat_fetch"));
			apiParams.add(new BasicNameValuePair("auth", session.getValue(AppProperties.PROFILE_ID)));
			apiParams.add(new BasicNameValuePair("start", start));
			apiParams.add(new BasicNameValuePair("database","mmmut"));
			Log.e("Previous Chat Parameter", apiParams.toString());
			try{ 
				adata = CommonMethods.loadJSONData(AppProperties.URL, AppProperties.METHOD_GET, apiParams);		
			}
			catch(Exception e1)
			{
				System.out.println("Background Exceptions");
				e1.printStackTrace();
			} 
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			
			if(adata != null){
				
				Log.e("Chat", adata.toString());
				JSONArray a = null,action;
				JSONObject ao = null,co,cn,data;
				String b,actionby = null,message=null,name= null,n=null,p=null;
				
				try { 
						a = new JSONArray(adata.toString());
						b = a.getString(0);
						data = new JSONObject(b);
						n = data.getString("name"); 
						p = data.getString("pimage"); 
						action = new JSONArray(data.getString("action"));
						Log.e("action",action.toString());
						plhlay = new LinearLayout[action.length()];
						piv = new ImageView[action.length()];
						String myprofileid = session.getValue(AppProperties.PROFILE_ID);
						for(i=0;i< action.length(); i++)
						{
							b = action.getString(i);
							co = new JSONObject(b);
							actionby = co.getString("actionby");
							message = co.getString("message");
							cn = new JSONObject(p); 
							
							photo = cn.getString(userid);
							Log.e("message",  "after photo");   
							tv = new TextView(Message.this);  
							tv.setBackgroundColor(Color.rgb(202, 213, 228));
							tv.setPadding(10, 10, 10, 10);
							 
							 
							plhlay[i] = new LinearLayout(Message.this);
							piv[i] = new ImageView(Message.this);
							LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(60, 60);
							layoutParams.setMargins(5, 0, 10, 0);
							piv[i].setLayoutParams(layoutParams);
							new image_fetch().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
							
							
							tv.setText(message);
							
							plhlay[i].setPadding(10, 10, 10, 10); 
							if(actionby.equalsIgnoreCase(myprofileid))
							{
								plhlay[i].setGravity(Gravity.RIGHT);
								plhlay[i].addView(tv); 
								tv.setBackgroundColor(Color.rgb(223, 238, 207));
							}
							else
							{
								plhlay[i].addView(piv[i]);
								plhlay[i].addView(tv); 
								cn = new JSONObject(n);  
								name = cn.getString(actionby);
					
							}
						
							llPrev.addView(plhlay[i],0);
							
						}
						loading = true;
						scrllPrev.fullScroll(ScrollView.FOCUS_DOWN);
				}
			 catch (Exception e1) {
					System.out.println("Unable to cast json data");
					e1.printStackTrace();
				}
			setTitle(title);
			setProgressBarIndeterminateVisibility(false);
		}
	}
	} 
	
	
	class single_image_fetch extends AsyncTask<Void, Void, Void>{
		 
		@Override
		protected void onPreExecute() { 
			
		}
		@Override 
		protected Void doInBackground(Void... params) { 
					try{ 
						d = CommonMethods.photo_fetch(photo);
					}
					catch(Exception e1)
					{
						System.out.println("Background Exceptions");
						e1.printStackTrace();
					} 
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			Log.e("value of i",i+"");
			iv.setBackgroundDrawable(d);
		} 
	}
	
class image_fetch extends AsyncTask<Void, Void, Void>{
		 
		@Override
		protected void onPreExecute() { 
			ImageView iv;
		}
		@Override 
		protected Void doInBackground(Void... params) { 
					try{ 
						d = CommonMethods.photo_fetch(photo);
					}
					catch(Exception e1)
					{
						System.out.println("Background Exceptions");
						e1.printStackTrace();
					} 
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			Log.e("value of i",i+"");
			i=i-1;
			piv[i].setBackgroundDrawable(d);
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
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		
		Log.e("Svroll Position x", scrllPrev.getScrollX()+"");
		Log.e("Svroll Position y", scrllPrev.getScrollY()+"");
		
		Log.e("Svroll Position h", v.getHeight()+"");
		
		int h = scrllPrev.getChildAt(0).getHeight();
		if(scrllPrev.getScrollY() > scrollPosition && loading == true)
		{
			scrollPosition = scrllPrev.getScrollY();
			if(scrollPosition > 0.36 * h)
			{
				loading = false;
				Log.e("Loading more data", "Loading  more data");
				int x = Integer.parseInt(start) + 10;
				start = x+"";
				new fetchMsgFriend().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				scrollPosition = scrllPrev.getScrollY();
			}
		}
		return false;
	}

}
