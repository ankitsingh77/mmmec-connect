package com.quipmate2.features;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.quipmate2.R;
import com.quipmate2.constants.AppProperties;
import com.quipmate2.utils.CommonMethods;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Profile extends Activity{
	private JSONArray result;
	private JSONObject data;
	private ProgressBar progressBar;
	private TextView tvName, tvBranch, tvBatch, tvMobile, tvEmail, tvBirthday, tvMarriageDay, tvCompany, tvCity;
	private String profileid;
	Map<String,String> branchList = new HashMap<>();


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.profile);
		getActionBar().setTitle("Malaviyan Login");
		getActionBar().setDisplayHomeAsUpEnabled(true);
		branchList.put("COMPUTER SCIENCE","Computer Science & Engineering");
		branchList.put("ELECTRONICS","Electronics & Communication Engineering");
		branchList.put("MECHANICAL","Mechanical Engineering");
		branchList.put("ELECTRICAL","Electrical Engineering");
		branchList.put("CIVIL","Civil Engineering");
		profileid = getIntent().getExtras().getString(AppProperties.PROFILE_ID);

		tvName = (TextView)findViewById(R.id.tvName);
		tvBranch = (TextView)findViewById(R.id.tvBranch);
		tvBatch = (TextView)findViewById(R.id.tvBatch);
		tvMobile = (TextView)findViewById(R.id.tvMobile);
		tvEmail = (TextView)findViewById(R.id.tvEmail);
		tvBirthday = (TextView)findViewById(R.id.tvBirthday);
		tvMarriageDay = (TextView)findViewById(R.id.tvMarriageDay);
		tvCompany = (TextView)findViewById(R.id.tvCompany);
		tvCity = (TextView)findViewById(R.id.tvCity);
		new ProfileFetch().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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
	}

	public class ProfileFetch extends AsyncTask<Void, Void, Void>
	{
		ProgressDialog pdialog;

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			pdialog = new ProgressDialog(Profile.this);
			pdialog.setMessage("Loading Profile....");
			pdialog.show();
		}

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			try{
				List<NameValuePair> apiParams = new ArrayList<NameValuePair>();

				apiParams.add(new BasicNameValuePair(AppProperties.ACTION, "malaviyan_profile_fetch"));
				apiParams.add(new BasicNameValuePair(AppProperties.PROFILE_ID, profileid));
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

				if(pdialog.isShowing())
					pdialog.dismiss();

				try {
					Log.e("Code", data.toString());
					if (data != null) {
						JSONObject action = new JSONObject(data.getString(AppProperties.ACTION));
						String name = new JSONObject(data.getString(AppProperties.NAME)).getString(action.getString(AppProperties.PROFILE_ID));
						if(!name.equals("null")) {
							tvName.setText(name);
						}
                        getActionBar().setTitle(name);
						String email = action.getString(AppProperties.PARAM_EMAIL);
						if(!email.equals("null")) {
							tvEmail.setText(email);
							tvEmail.setVisibility(View.VISIBLE);
						}
						else
						{
							tvEmail.setVisibility(View.GONE);
						}
						String mobile = action.getString(AppProperties.MOBILE);
						if(!mobile.equals("null")) {
							tvMobile.setText(mobile);
							tvMobile.setVisibility(View.VISIBLE);
						}
						else
						{
							tvMobile.setVisibility(View.GONE);
						}
						String batch = action.getString(AppProperties.BATCH);
						if(!batch.equals("null") && !batch.equals("0")) {
							tvBatch.setText("Batch - " + batch);
							tvBatch.setVisibility(View.VISIBLE);
						}
						else
						{
							tvBatch.setVisibility(View.GONE);
						}
						String branch = action.getString(AppProperties.BRANCH);
						if(!branch.equals("null")) {
							branch = branchList.get(branch);
							tvBranch.setText(branch);
							tvBranch.setVisibility(View.VISIBLE);

						}
						else
						{
							tvBranch.setVisibility(View.GONE);
						}
						String birthday = action.getString(AppProperties.BIRTHDAY);
						if(!birthday.equals("null")) {
							Date date = new SimpleDateFormat("yyyy-MM-dd").parse(birthday);
							String formattedBday = new SimpleDateFormat("dd-MMM-yyyy").format(date);
							tvBirthday.setText("DOB - "+ formattedBday);
							tvBirthday.setVisibility(View.VISIBLE);
						}
						else
						{
							tvBirthday.setVisibility(View.GONE);
						}
						String marriage = action.getString(AppProperties.MARRIAGEDAY);
						if(!marriage.equals("null")) {
							Date date = new SimpleDateFormat("yyyy-MM-dd").parse(marriage);
							String formattedMday = new SimpleDateFormat("dd-MMM-yyyy").format(date);
							tvBirthday.setText(formattedMday);
							tvMarriageDay.setText(" Marriage Date - "+ marriage);
							tvMarriageDay.setVisibility(View.VISIBLE);
						}
						else
						{
							tvMarriageDay.setVisibility(View.GONE);
						}
						String company = action.getString(AppProperties.COMPANY);
						if(!company.equals("null")) {
							tvCompany.setText(company);
							tvCompany.setVisibility(View.VISIBLE);
						}
						else
						{
							tvCompany.setVisibility(View.GONE);
						}
						String city = action.getString(AppProperties.CITY);
						if(!city.equals("null")) {
							tvCity.setText(city);
							tvCity.setVisibility(View.VISIBLE);
						}
						else
						{
							tvCity.setVisibility(View.GONE);
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
									CommonMethods.ShowInfo(Profile.this,
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
}