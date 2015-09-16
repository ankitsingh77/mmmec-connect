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
import android.widget.TextView;
import android.widget.Toast;

public class SignUp extends Activity implements OnClickListener {
	private EditText etemail;
	private Button btsignup;
	private TextView codehave;
	private JSONArray result;
	private JSONObject data;
	private Session signup;
	private String code,email;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		getActionBar().setTitle("Sign Up");
		setContentView(R.layout.signup);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		signup = new Session(SignUp.this);
		etemail = (EditText) findViewById(R.id.etemail_signup);
		btsignup = (Button) findViewById(R.id.blogin_signup);
		codehave = (TextView) findViewById(R.id.code_got);
		
		if(signup.hasKey("code"))
		code = signup.getValue("code");
		else
			code = null;
		
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
			
		 email = etemail.getText().toString().trim();
		if(email !=null){
			
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
							String inputcode = input.getText().toString();
							if(inputcode.equals(code)){
								Intent details = new Intent(SignUp.this,EnterDetailsSignup.class);
								startActivity(details);
								finish();
							}
							else{
								Toast.makeText(SignUp.this,"Wrong Code", Toast.LENGTH_LONG).show();
							}
						}
					});
			codeDialog.show();
			
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
			
			apiParams.add(new BasicNameValuePair(AppProperties.ACTION, "self_invite_mobile"));
			apiParams.add(new BasicNameValuePair("email", email));
			
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
				code = data.getString("code");
				
				//save the code and email if user wishes to validate the code later.
				signup.setValue("user_email_signup", email);
				signup.setValue("code", code);
				signup.commit();
				System.out.println(code);
				CommonMethods.ShowInfo(SignUp.this, "A 4 digit code has been sent to your email").show();
			}
			else{
				CommonMethods.ShowInfo(SignUp.this, "Something went wrong. Please try again.").show();
			}
		}
			catch(JSONException e){
				
			}
		}
	}
}
