package com.quipmate2.features;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import com.example.quipmate2.R;
import com.quipmate2.constants.AppProperties;
import com.quipmate2.features.Message.image_fetch;
import com.quipmate2.utils.CommonMethods;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.AsyncTaskLoader;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.MarginLayoutParamsCompat;
import android.text.Layout;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class CoWorkers extends Activity implements OnClickListener, OnTouchListener{
	Session session;
	String start = "0",title="Malaviyans",photo=null;
	JSONArray adata=null;
	JSONArray result=null;
	Drawable d;
	String profileid = null,iname=null;
	ScrollView scrcoworker;
	Boolean loading=false;
	LinearLayout rlCoworker;
	RelativeLayout[] llhlay;
	ImageView[] iv;
	TextView tvemail,tvname,tvprofession,tvteam,tvdesignation,tv;
	int i, scrollPosition=0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.coworker);
		scrcoworker = (ScrollView)findViewById(R.id.scrcoworker);
		scrcoworker.setOnTouchListener(this);
		rlCoworker = (LinearLayout)findViewById((R.id.rlcoworker));
		session = new Session(getApplicationContext());
		new CoWorkerFetch().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}
	
	
	class CoWorkerFetch extends AsyncTask<Void, Void, Void>{
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			setProgressBarIndeterminateVisibility(true);
			//Log.e("IntentExtraaaaaaaaaaaaaaaaaaaaaaaaaa from message _ fetch", userid);
		}
		@Override 
		protected Void doInBackground(Void... params) {
					
			List<NameValuePair> apiParams = new ArrayList<NameValuePair>();
			apiParams.add(new BasicNameValuePair(AppProperties.ACTION, "malaviyan_fetch"));
			apiParams.add(new BasicNameValuePair("auth", session.getValue(AppProperties.PROFILE_ID)));
			apiParams.add(new BasicNameValuePair("database","mmmut"));
			
			apiParams.add(new BasicNameValuePair("start", start+"")); 
			Log.e("Previous Chat Parameters", apiParams.toString());
			try{ 
				adata = CommonMethods.loadJSONData(AppProperties.URL, AppProperties.METHOD_GET, apiParams);	
				if(adata!=null)
				{
					Log.e("Result coworker", adata.toString());
				}
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
				String b,name=null,message=null,n=null,p=null,profession=null,designation=null,status=null,email=null,team=null;
				
				try { 
						a = new JSONArray(adata.toString());
						b = a.getString(0);
						data = new JSONObject(b);
						n = data.getString("name"); 
						p = data.getString("pimage"); 
						action = new JSONArray(data.getString("action"));
						Log.e("action",action.toString()); 
						llhlay = new RelativeLayout[action.length()];
						iv = new ImageView[action.length()];
						String myprofileid = session.getValue(AppProperties.PROFILE_ID);
						RelativeLayout.LayoutParams name_params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
						RelativeLayout.LayoutParams email_params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
						RelativeLayout.LayoutParams designation_params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
						RelativeLayout.LayoutParams profession_params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
						RelativeLayout.LayoutParams team_params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
						for(i=0;i< action.length(); i++)
						{
							b = action.getString(i);
							co = new JSONObject(b);
							profileid = co.getString("profileid");
							email = co.getString("email");   
							tvname = new TextView(CoWorkers.this);  
							tvemail = new TextView(CoWorkers.this);
							llhlay[i] = new RelativeLayout(CoWorkers.this);
							cn = new JSONObject(n);  
							name = cn.getString(profileid);
							iname = name;
							llhlay[i].setOnClickListener((CoWorkers.this));
							llhlay[i].setTag(R.id.TAG_PROFILEID,profileid);
							llhlay[i].setTag(R.id.TAG_NAME, iname);
							
							if(i%2==0)
							{
								llhlay[i].setBackgroundColor(Color.rgb(202, 213, 228));
							}
							iv[i] = new ImageView(CoWorkers.this);
							LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(150, 150);
							layoutParams.setMargins(5, 0, 10, 0);
							iv[i].setLayoutParams(layoutParams);
							cn = new JSONObject(p); 
							photo = cn.getString(profileid);
							Log.e("message",  "after photo");
							new image_fetch().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,photo,iv[i]);
						    name_params.addRule(RelativeLayout.RIGHT_OF, iv[i].getId());
						    email_params.addRule(RelativeLayout.RIGHT_OF, iv[i].getId());

						    
						    name_params.setMargins(170, 10, 0, 0);
						    tvname.setTypeface(null,Typeface.BOLD);
						    tvname.setText(name);
						    tvname.setLayoutParams(name_params);
						    
						    email_params.setMargins(170, 35, 0, 0);
							tvemail.setText(email);
							tvemail.setLayoutParams(email_params);
							

							llhlay[i].setPadding(10, 10, 10, 10); 
							
							llhlay[i].addView(iv[i]);
							llhlay[i].addView(tvname); 
							llhlay[i].addView(tvemail);
							rlCoworker.addView(llhlay[i]);
						}
						scrcoworker.fullScroll(ScrollView.FOCUS_DOWN);
				}
			 catch (Exception e1) {
					System.out.println("Unable to cast json data");
					e1.printStackTrace();
				}
			setTitle(title);
			loading = true;
			setProgressBarIndeterminateVisibility(false);
			if(start == "0"){
				//lvMsg.smoothScrollToPosition(adapter.getCount());
			}
			else{
				//lvMsg.smoothScrollToPosition(0);
			}
			
		}
	}
	}
	
	class image_fetch extends AsyncTask<Object, Void, Void>{ 
		 
		ImageView imv;
		@Override
		protected void onPreExecute() { 
			//ImageView iv;
		}
		
		protected Void doInBackground(Object... params) { 
					try{ 
						String ph = (String)params[0];
						imv = (ImageView)params[1];
						d = CommonMethods.photo_fetch(ph);
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
			imv.setBackgroundDrawable(d);
		}
	}
 
	@Override
	public void onClick(View v) { 
		// TODO Auto-generated method stub
		Log.i("onlick", "Inside On click Listener");
		Intent in = new Intent(CoWorkers.this,Message.class); 
		Log.i("profileid", profileid);
		Log.i("name", iname);
		in.putExtra("friendId", v.getTag(R.id.TAG_PROFILEID).toString());
		in.putExtra("friendName", v.getTag(R.id.TAG_NAME).toString());
		startActivity(in);
	}
 
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		
		Log.e("Svroll Position x", scrcoworker.getScrollX()+"");
		Log.e("Svroll Position y", scrcoworker.getScrollY()+"");
		
		Log.e("Svroll Position h", v.getHeight()+"");
		
		int h = scrcoworker.getChildAt(0).getHeight();
		if(scrcoworker.getScrollY() > scrollPosition && loading == true)
		{
			scrollPosition = scrcoworker.getScrollY();
			if(scrollPosition > 0.36 * h)
			{
				loading = false;
				Log.e("Loading more data", "Loading  more data");
				int x = Integer.parseInt(start) + 10;
				start = x+"";
				new CoWorkerFetch().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				scrollPosition = scrcoworker.getScrollY();
			}
		}
		return false;
	}
	
}
