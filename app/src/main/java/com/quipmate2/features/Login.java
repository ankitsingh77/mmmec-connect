package com.quipmate2.features;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.quipmate2.R;
import com.quipmate2.constants.AppProperties;
import com.quipmate2.utils.CommonMethods;
import com.quipmate2.utils.NetworkHelper;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;


public class Login extends Activity implements OnClickListener {
	private ProgressBar progressBar;
	EditText etemail, etpassword;
	private Button blogin;
	Session session;
	String email, password;
	private JSONObject last;
	private boolean isPressed = false;
	private JSONArray result;
	private TextView signup;
 
	@Override  
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		session = new Session(Login.this);
		if((session.hasKey(AppProperties.PARAM_PASSWORD) && !session.getValue(AppProperties.PARAM_PASSWORD).trim().equalsIgnoreCase("")))
		{
			Log.e("Redirecting", "Redirecting to Messages");
			new AutoLogin().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		}
		else
		{	
			Log.e("No Redirecting", "Stay on login page");
			setContentView(R.layout.login);
			initView();
			
			//hide the keyboard
			getWindow().setSoftInputMode(
				      WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	
			if (session.hasKey(AppProperties.PARAM_EMAIL)) {
				etemail.setText(session.getValue(AppProperties.PARAM_EMAIL));
			}
			if (session.hasKey(AppProperties.PARAM_PASSWORD)) {
				etpassword.setText(session.getValue(AppProperties.PARAM_PASSWORD));
				
			}
		}
	}
	private void initView() {
		progressBar = (ProgressBar) findViewById(R.id.progressbar);
		progressBar.setVisibility(View.INVISIBLE);
		etemail = (EditText) findViewById(R.id.etemail);
		etpassword = (EditText) findViewById(R.id.etpassword);
		blogin = (Button) findViewById(R.id.blogin);
		signup = (TextView) findViewById(R.id.signup);
		blogin.setOnClickListener(this);
		signup.setOnClickListener(this);
	}

	
	
	@Override
	public void onClick(View v) {
		if (isPressed) {
			return;
		}
		isPressed = true;
		if (v.getId() == R.id.blogin)
		{
			new AttempLogin().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		}
		else if(v.getId() == R.id.signup)
		{
			Intent signup = new Intent(this, SignUp.class);
			startActivity(signup);
		}
		isPressed = false;
	}


	public class AutoLogin extends AsyncTask<Void, Void, Void>
	{
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
		}

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			//progressBar.setVisibility(View.VISIBLE);

			session = new Session(getApplicationContext());
			
			System.out.println("Trying to auto login");
			email = session.getValue("email");
			password = session.getValue("password");

			try {

				if (NetworkHelper.checkNetworkConnection(Login.this)) {
					List<NameValuePair> apiParams = new ArrayList<NameValuePair>();
					apiParams.add(new BasicNameValuePair(AppProperties.ACTION, "login"));
					apiParams.add(new BasicNameValuePair(AppProperties.PARAM_PASSWORD, password));
					apiParams.add(new BasicNameValuePair(AppProperties.PARAM_EMAIL,email));
					result = CommonMethods.loadJSONData(AppProperties.URL, AppProperties.METHOD_POST, apiParams);
					System.out.println(result);
					if(result != null)
					{
						last = result.getJSONObject(0);
						if (last != null) 
						{
							Log.e("Auto Login", last.toString());	
							if (last.has(AppProperties.ACK) && last.getString(AppProperties.ACK).equals(AppProperties.ACK_CODE)) 
							{
								session.setValue(AppProperties.PARAM_EMAIL, email);
								session.setValue(AppProperties.PARAM_PASSWORD, password);
								session.setValue(AppProperties.MY_PROFILE_NAME, 
										last.getString(AppProperties.MY_PROFILE_NAME));
								session.setValue(AppProperties.MY_PROFILE_PIC,
										last.getString(AppProperties.MY_PROFILE_PIC));
								session.setValue(AppProperties.SESSION_ID,
										last.getString(AppProperties.SESSION_ID));
								session.setValue(AppProperties.PROFILE_ID,
										last.getString(AppProperties.MY_PROFILE_ID));
								session.setValue(AppProperties.SESSION_NAME,
										last.getString(AppProperties.SESSION_NAME));
								session.setValue(AppProperties.DATABASE, 
										last.getString(AppProperties.DATABASE));
								
								if (session.commit()) 
								{
									startService(new Intent(Login.this,RealTimeService.class));
									startService(new Intent(Login.this,ChatService.class));	
	 
									Intent intent = new Intent(Login.this, WelcomeActivity.class);
									startActivity(intent);
									finish();
	
								} else {
									System.out.println("Some problem in Sigin. Please try again.");
								}
							} else if (last.has(getString(R.string.error))) {
								
								JSONObject error = last
										.getJSONObject(getString(R.string.error));
								if (error.getString(getString(R.string.code)).equals(
										AppProperties.WRONG_CREDENTIAL_CODE)) {
									runOnUiThread(new Runnable() {
										
										@Override
										public void run() {
											// TODO Auto-generated method stub
											progressBar.setVisibility(View.INVISIBLE);
											CommonMethods.ShowInfo(Login.this,
													getString(R.string.invalid_credential))
													.show();
										}
									});
									
								}
							}
						}
						else
						{
							Log.e("Unable to login","Empty return by the API");
						}
					}
					else
					{
						Log.e("Unable to login","Empty return by the API");
					}
				} 
				else {
					
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() { 
							// TODO Auto-generated method stub
							progressBar.setVisibility(View.INVISIBLE);
							CommonMethods.ShowInfo(Login.this,
									getString(R.string.network_error)).show();
						}
					});
					
				}
			} catch (JSONException e) {
				// should not happen
				e.printStackTrace();
			}

			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub

		}
		
	}
	
	public class AttempLogin extends AsyncTask<Void, Void, Void> 
	{
		@Override
		protected void onPreExecute() {
			progressBar.setVisibility(View.VISIBLE);
		}

		@Override
		protected Void doInBackground(Void... params) {

			System.out.println("Trying to log you in");
			email = etemail.getText().toString();
			password = etpassword.getText().toString();

			try {

				if (NetworkHelper.checkNetworkConnection(Login.this)) 
				{
					List<NameValuePair> apiParams = new ArrayList<NameValuePair>();
					apiParams.add(new BasicNameValuePair(AppProperties.ACTION, "login"));
					apiParams.add(new BasicNameValuePair(AppProperties.PARAM_PASSWORD, password));
					apiParams.add(new BasicNameValuePair(AppProperties.PARAM_EMAIL, email));
					result = CommonMethods.loadJSONData(AppProperties.URL, AppProperties.METHOD_POST, apiParams);
					System.out.println(result);
					if(result != null)
					{
						last = result.getJSONObject(0);
						if (last != null) 
						{
							Log.e("Normal Login", last.toString());
							if (last.has(AppProperties.ACK) && last.getString(AppProperties.ACK).equals(AppProperties.ACK_CODE)) 
							{
								session.setValue(AppProperties.PARAM_EMAIL, email);
								session.setValue(AppProperties.PARAM_PASSWORD, password);
								session.setValue(AppProperties.MY_PROFILE_NAME, 
										last.getString(AppProperties.MY_PROFILE_NAME));
								session.setValue(AppProperties.MY_PROFILE_PIC,
										last.getString(AppProperties.MY_PROFILE_PIC));
								session.setValue(AppProperties.SESSION_ID,
										last.getString(AppProperties.SESSION_ID));
								session.setValue(AppProperties.PROFILE_ID,
										last.getString(AppProperties.MY_PROFILE_ID));
								session.setValue(AppProperties.SESSION_NAME,
										last.getString(AppProperties.SESSION_NAME));
								session.setValue(AppProperties.DATABASE,
										last.getString(AppProperties.DATABASE));
								
								if (session.commit()) 
								{
									//startService(new Intent(Login.this,RealTimeService.class));
									startService(new Intent(Login.this,ChatService.class));	
									Intent intent = new Intent(Login.this, WelcomeActivity.class);
									startActivity(intent);
									finish();
								} 
								else 
								{
									System.out.println("Some problem in Sigin. Please try again.");
								}
							} 
							else if (last.has(getString(R.string.error))) 
							{
								JSONObject error = last.getJSONObject(getString(R.string.error));
								if (error.getString(getString(R.string.code)).equals(AppProperties.WRONG_CREDENTIAL_CODE)) 
								{
									runOnUiThread(new Runnable() 
									{
										@Override
										public void run() {
											progressBar.setVisibility(View.INVISIBLE);
											CommonMethods.ShowInfo(Login.this, getString(R.string.invalid_credential)).show();
										}
									});
								}
							}
						}
						else
						{
							Log.e("Empty Return from API", "Unable to login");
						}
					}
					else
					{
						Log.e("Empty Return from API", "Unable to login");
					}
				}
				else 
				{	
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							progressBar.setVisibility(View.INVISIBLE);
							CommonMethods.ShowInfo(Login.this,
									getString(R.string.network_error)).show();
						}
					});
					
				}
			} catch (JSONException e) {
				// should not happen
				e.printStackTrace();
			}

			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			
			if(progressBar.getVisibility()==View.VISIBLE)
			progressBar.setVisibility(View.INVISIBLE);
		}
	}

	@Override
	public void onBackPressed() 
	{
		// TODO Auto-generated method stub
		super.onBackPressed();
		Log.e("Stay On Login Page", "Stay on Login PAge on back Pressed");
	}
}
