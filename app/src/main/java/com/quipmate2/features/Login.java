package com.quipmate2.features;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.telephony.TelephonyManager;
import android.provider.Settings.System;

import com.example.quipmate2.R;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

public class Login extends Activity implements OnClickListener {
	private EditText etMobile;
	private Button btsignup;
	private JSONArray result;
	private JSONObject data;
	private Session signup;
	private String code,mobile;
	private Session session;
	private ProgressBar progressBar;
	private String myImei, myMobile, device_unique_id;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		getActionBar().setTitle("Malaviyan Login");
		setContentView(R.layout.login);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		signup = new Session(Login.this);
		etMobile = (EditText) findViewById(R.id.etemail_signup);
		btsignup = (Button) findViewById(R.id.blogin_signup);

		session = new Session(getApplicationContext());

		if(session.hasKey(AppProperties.MOBILE) && session.hasKey(AppProperties.CODE))
		{
			Log.e("Redirecting", "Redirecting to Messages");
			new AutoLogin().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		}else{
			TelephonyManager tMgr = (TelephonyManager)getSystemService(getApplicationContext().TELEPHONY_SERVICE);
			myMobile = tMgr.getLine1Number();
			myImei = tMgr.getDeviceId();
			device_unique_id = System.getString(this.getContentResolver(), System.ANDROID_ID);
			session.setValue(AppProperties.IMEI, myImei);
			session.setValue(AppProperties.MOBILE, myMobile);
			session.setValue(AppProperties.DEVICE_UNIQUE_ID, device_unique_id);
			etMobile.setText(myMobile);
		}
		btsignup.setOnClickListener(this);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
			   finish();
				return true;
			default:
				return super.onOptionsItemSelected(item);
    	}
	}

	@Override
	public void onBackPressed()
	{
		super.onBackPressed();
		Log.e("Stay On SignUp Page", "Stay on SignUP page back Pressed");
	}

	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.blogin_signup){
			
		 	mobile = etMobile.getText().toString().trim();
			if(mobile !=null){
				signup.setValue(AppProperties.MOBILE, mobile);
				signup.commit();
				new sendEmail().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			}
		}
	}

	public class AutoLogin extends AsyncTask<Void, Void, Void>
	{
		ProgressDialog pdialog;

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			pdialog = new ProgressDialog(Login.this);
			pdialog.setMessage("Signing in...");
			pdialog.show();
		}

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			try{
				List<NameValuePair> apiParams = new ArrayList<NameValuePair>();

				apiParams.add(new BasicNameValuePair(AppProperties.ACTION, "malaviyan_login"));
				apiParams.add(new BasicNameValuePair(AppProperties.MOBILE, session.getValue(AppProperties.MOBILE)));
				apiParams.add(new BasicNameValuePair(AppProperties.CODE, session.getValue(AppProperties.CODE)));
				apiParams.add(new BasicNameValuePair(AppProperties.IMEI, myImei));
					result = CommonMethods.loadJSONData(AppProperties.URL, AppProperties.METHOD_GET, apiParams);
					if(result != null){
						data = result.getJSONObject(0);
						Log.i("data", data.toString());
					}
				}
				catch(JSONException e){
					e.printStackTrace();
				}

				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				progressBar = (ProgressBar) findViewById(R.id.progressbar);
				session = new Session(Login.this);

				if(pdialog.isShowing())
					pdialog.dismiss();

				try {
					Log.e("Code", data.toString());
					if (data != null && data.has(AppProperties.ACK)) {
						session.setValue(AppProperties.MY_PROFILE_NAME,
								data.getString(AppProperties.MY_PROFILE_NAME));
						session.setValue(AppProperties.MY_PROFILE_PIC,
								data.getString(AppProperties.MY_PROFILE_PIC));
						session.setValue(AppProperties.SESSION_ID,
								data.getString(AppProperties.SESSION_ID));
						session.setValue(AppProperties.PROFILE_ID,
								data.getString(AppProperties.MY_PROFILE_ID));
						session.setValue(AppProperties.SESSION_NAME,
								data.getString(AppProperties.SESSION_NAME));
						session.setValue(AppProperties.DATABASE,
								data.getString(AppProperties.DATABASE));

						if (session.commit()) {
							startService(new Intent(Login.this, RealTimeService.class));
							startService(new Intent(Login.this, ChatService.class));
							Intent intent = new Intent(Login.this, WelcomeActivity.class);
							startActivity(intent);
							finish();
						} else {
							CommonMethods.ShowInfo(Login.this, "Some problem in Login. Please try again.").show();
						}
					} else if (data.has(getString(R.string.error))) {

						JSONObject error = data
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
				catch(Exception e){
					Log.i("Exception", e.toString());
				}
			}
		}

	class sendEmail extends AsyncTask<Void, Void, Void>{

		ProgressDialog pdialog;
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
		pdialog = new ProgressDialog(Login.this);
		pdialog.setMessage("Please Wait");
		pdialog.show();
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			try{
			List<NameValuePair> apiParams = new ArrayList<NameValuePair>();
			
			apiParams.add(new BasicNameValuePair(AppProperties.ACTION, "malaviyan_litmus_test"));
			apiParams.add(new BasicNameValuePair(AppProperties.MOBILE, mobile));
			
			result = CommonMethods.loadJSONData(AppProperties.URL, AppProperties.METHOD_GET, apiParams);
			if(result != null){
				data = result.getJSONObject(0);
				Log.i("data", data.toString());
			}
			}
			catch(JSONException e){
				e.printStackTrace();
			}
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			
			if(pdialog.isShowing())
				pdialog.dismiss();
			
			try{
			if(data != null && data.has(AppProperties.ACK)){
				CommonMethods.ShowInfo(Login.this, "A 4 digit code has been sent to your email").show();
				Intent intent = new Intent(Login.this, VerifyCode.class);
				intent.putExtra(AppProperties.MOBILE, mobile);
				intent.putExtra(AppProperties.PARAM_EMAIL, data.getString(AppProperties.PARAM_EMAIL));
				startActivity(intent);
				finish();
			}
			else{
				CommonMethods.ShowInfo(Login.this, "This number is not registered in Malaviyan database.").show();
			}
		}
			catch(Exception e){
				Log.i("Exception", e.toString());
			}
		}
	}
}