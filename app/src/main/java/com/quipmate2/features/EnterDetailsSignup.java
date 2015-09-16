package com.quipmate2.features;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.quipmate2.R;
import com.quipmate2.constants.AppProperties;
import com.quipmate2.utils.CommonMethods;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;

@SuppressLint("SimpleDateFormat")
public class EnterDetailsSignup extends Activity implements OnClickListener {
	
	



	private EditText etname, etpass, etbirth;
	private ImageButton datepick;
	private String email, name, password, gender, birth, code;
	private ImageView wrongName, wrongPass, wrongGender;
	private Button btsignup, backtologin;
	private Session signup;
	private JSONArray result;
	private JSONObject data;
	private int day, month, year;
	
	final static String DATE_FORMAT = "dd/MM/yyyy";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.enter_details_signup);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		init();
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
	
	private void init() {
		// TODO Auto-generated method stub
		gender = null;
		datepick = (ImageButton) findViewById(R.id.datepicker);
		etname = (EditText) findViewById(R.id.etname);
		etpass = (EditText) findViewById(R.id.etpassword);
		etbirth = (EditText) findViewById(R.id.etbirth);
		wrongName = (ImageView) findViewById(R.id.wrong_name);
		wrongPass = (ImageView) findViewById(R.id.wrong_pass);
		wrongGender = (ImageView) findViewById(R.id.wrong_gender);
		btsignup = (Button) findViewById(R.id.signup);
		backtologin = (Button) findViewById(R.id.btnLinkToLoginScreen);
		signup = new Session(this);
		
		datepick.setOnClickListener(this);
		btsignup.setOnClickListener(this);
		backtologin.setOnClickListener(this);
	}

	
	
	private boolean validateName(){
		
		if(name != null && !name.equals("")){
			return true;
		}
		return false;
	}
	
	private boolean validatePass(){
		if(password != null && password.length() >= 6)
			return true;
		return false;
	}
	
	private boolean validateGender(){
		if(gender != null)
			return true;
		return false;
	}

	public void onRadioButtonClicked(View view) {
	    // Is the button now checked?
	    boolean checked = ((RadioButton) view).isChecked();
	    
	    // Check which radio button was clicked
	    switch(view.getId()) {
	        case R.id.radio_male:
	            if (checked)
	              gender = "1";
	            break;
	        case R.id.radio_female:
	            if (checked)
	            	gender = "0";
	            break;
	    }
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		
		case R.id.signup: name = etname.getText().toString().trim();
		                  password = etpass.getText().toString().trim();
		                  birth = etbirth.getText().toString().trim();
		                  email = signup.getValue("user_email_signup");
		                  code = signup.getValue("code");
		
		                  if(!validateName()) wrongName.setVisibility(View.VISIBLE);
		                  else wrongName.setVisibility(View.INVISIBLE);
		                  if(!validatePass()) wrongPass.setVisibility(View.VISIBLE);
		                  else wrongPass.setVisibility(View.INVISIBLE);
		                  if(!validateGender()) wrongGender.setVisibility(View.VISIBLE);
		                  else wrongGender.setVisibility(View.INVISIBLE);
		                 /* if(!validateDate()) wrongBirth.setVisibility(View.VISIBLE);
		                  else wrongBirth.setVisibility(View.INVISIBLE);*/
		
		                 if(validateName() && validatePass() && validateGender()){
			              new Signup().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		                 }
		                 break;
		case R.id.datepicker :  //showDialog(0); break;
			DialogFragment picker = new DatePickerFragment();
			picker.show(getFragmentManager(), "datePicker"); break;
			
		case R.id.btnLinkToLoginScreen:
		finish();
			
	}
	}
	

	@SuppressLint("ValidFragment")
	public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
		
			// Use the current date as the default date in the picker
		final Calendar c = Calendar.getInstance();
		 year = c.get(Calendar.YEAR);
		 month = c.get(Calendar.MONTH);
		 day = c.get(Calendar.DAY_OF_MONTH); 

		// Create a new instance of DatePickerDialog and return it
		return new DatePickerDialog(getActivity(), this, year, month, day);
		}
		
		@Override
		public void onDateSet(DatePicker view, int year, int month, int day) {
		Calendar c = Calendar.getInstance();
		c.set(year, month, day);

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		String formattedDate = sdf.format(c.getTime());
		etbirth.setText(formattedDate);
		}
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		finish();
	}
	
	class Signup extends AsyncTask<Void, Void, Void>
	{
		private ProgressDialog pdialog;
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			pdialog = new ProgressDialog(EnterDetailsSignup.this);
			pdialog.setMessage("Please wait...");
			pdialog.show();
		}
		
		
		
		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			try{
				int first = birth.indexOf('/');
				int second = birth.lastIndexOf('/');
				
				String day = birth.substring(0, first);
				String month = birth.substring(first + 1 , second);
				String year = birth.substring(second + 1, birth.length());
				
				List<NameValuePair> apiParams = new ArrayList<NameValuePair>();
				
				//System.out.println(email+" "+code+" "+name+" "+password+" "+day+" "+month+" "+year);
				
				apiParams.add(new BasicNameValuePair(AppProperties.ACTION, "validate_user_mobile"));
				apiParams.add(new BasicNameValuePair("email", email));
				apiParams.add(new BasicNameValuePair("identifier", code));
				apiParams.add(new BasicNameValuePair("name",name));
				apiParams.add(new BasicNameValuePair("password",password));
				apiParams.add(new BasicNameValuePair("gender", gender));
				apiParams.add(new BasicNameValuePair("day", day));
				apiParams.add(new BasicNameValuePair("month", month));
				apiParams.add(new BasicNameValuePair("year", year));
				
				result = CommonMethods.loadJSONData(AppProperties.URL, AppProperties.METHOD_POST, apiParams);
				System.out.println(result);
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
			// TODO Auto-generated method stub
			if(pdialog.isShowing())
				pdialog.dismiss();
			if(data != null){
			if(data.has(AppProperties.ACK)){
				
				//delete the code and signup email stored earlier if signup process is successful
				signup.delValue("code");
				signup.delValue("user_email_signup");
				signup.commit();
				
				AlertDialog.Builder codeDialog = new AlertDialog.Builder(EnterDetailsSignup.this);
				codeDialog.setTitle("Done");
				codeDialog.setMessage("Press Ok to go to the login page.");
				codeDialog.setIcon(R.drawable.ic_done);
				
				 codeDialog.setPositiveButton("OK", 
	            		  new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
									
									finish();
							}
						});
				codeDialog.show();
			}
			else if(data.has("error")){
				try {
					JSONObject err = data.getJSONObject("error");
					String errmsg = err.getString("message");
					CommonMethods.ShowInfo(EnterDetailsSignup.this, errmsg).show();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else{
				CommonMethods.ShowInfo(EnterDetailsSignup.this, "Something went wrong! Please try again.");
			}
			}
		}
		
	}

}
