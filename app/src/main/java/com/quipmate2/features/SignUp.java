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

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class SignUp extends Activity implements OnClickListener {
	private EditText etemail;
	private Button btsignup;
	private TextView codehave;
	private JSONArray result;
	private JSONObject data;
	private Session signup;
	private String code,mobile;
	private Session session;
	private ProgressBar progressBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		getActionBar().setTitle("Malaviyan Login");
		setContentView(R.layout.signup);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		signup = new Session(SignUp.this);
		etemail = (EditText) findViewById(R.id.etemail_signup);
		btsignup = (Button) findViewById(R.id.blogin_signup);
		codehave = (TextView) findViewById(R.id.code_got);
		btsignup.setOnClickListener(this);
		codehave.setOnClickListener(this);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
        case android.R.id.home:
            // app icon in action bar clicked; go home
           finish();
            return true;
        default:
            return super.onOptionsItemSelected(item);
    }
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		finish();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v.getId() == R.id.blogin_signup){
			
		 mobile = etemail.getText().toString().trim();
		if(mobile !=null){
			
			new sendEmail().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		}
		}
		else if(v.getId() == R.id.code_got){
			//show dialog here
			
			AlertDialog.Builder codeDialog = new AlertDialog.Builder(this);
			codeDialog.setTitle("VALIDATION");
			codeDialog.setMessage("Enter the 4 digit code");
			final EditText input = new EditText(this);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.MATCH_PARENT);
              input.setLayoutParams(lp);
              codeDialog.setView(input);
              
              codeDialog.setIcon(R.drawable.ic_status);
              
              codeDialog.setPositiveButton("OK", 
            		  new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							code = input.getText().toString();
							new verifyCode().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
						}
					});
			codeDialog.show();
			
		}
	}

	class verifyCode extends AsyncTask<Void, Void, Void>{

		ProgressDialog pdialog;

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			pdialog = new ProgressDialog(SignUp.this);
			pdialog.setMessage("Verifying the code");
			pdialog.show();
		}

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			try{
				List<NameValuePair> apiParams = new ArrayList<NameValuePair>();

				apiParams.add(new BasicNameValuePair(AppProperties.ACTION, "malaviyan_login"));
				apiParams.add(new BasicNameValuePair("mobile", mobile));
				apiParams.add(new BasicNameValuePair("code", code));

				result = CommonMethods.loadJSONData(AppProperties.URL, AppProperties.METHOD_GET, apiParams);
				if(result != null){
					data = result.getJSONObject(0);
					System.out.println(data);
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
			session = new Session(SignUp.this);

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
						startService(new Intent(SignUp.this, RealTimeService.class));
						startService(new Intent(SignUp.this, ChatService.class));
						Intent intent = new Intent(SignUp.this, WelcomeActivity.class);
						startActivity(intent);
						finish();
					} else {
						CommonMethods.ShowInfo(SignUp.this, "Some problem in Login. Please try again.").show();
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
								CommonMethods.ShowInfo(SignUp.this,
										getString(R.string.invalid_credential))
										.show();
							}
						});

					}

				}
			}
			catch(Exception e){
				System.out.println(e);
			}
		}
	}
	
	class sendEmail extends AsyncTask<Void, Void, Void>{

		ProgressDialog pdialog;
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
		pdialog = new ProgressDialog(SignUp.this);
		pdialog.setMessage("Please Wait");
		pdialog.show();
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			try{
			List<NameValuePair> apiParams = new ArrayList<NameValuePair>();
			
			apiParams.add(new BasicNameValuePair(AppProperties.ACTION, "malaviyan_litmus_test"));
			apiParams.add(new BasicNameValuePair("mobile", mobile));
			
			result = CommonMethods.loadJSONData(AppProperties.URL, AppProperties.METHOD_GET, apiParams);
			if(result != null){
				data=result.getJSONObject(0);
				System.out.println(data);
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
				Log.e("Code", data.toString());
			if(data != null && data.has(AppProperties.ACK)){
				//save the code and email if user wishes to validate the code later.
				signup.setValue("user_email_signup", mobile);
				signup.commit();
				System.out.println(code);
				CommonMethods.ShowInfo(SignUp.this, "A 4 digit code has been sent to your email").show();
			}
			else{
				CommonMethods.ShowInfo(SignUp.this, "Something went wrong. Please try again.").show();
			}
		}
			catch(Exception e){
				System.out.println(e);
			}
		}
	}
}
