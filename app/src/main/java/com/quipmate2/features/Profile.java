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

import java.util.ArrayList;
import java.util.List;

public class Profile extends Activity{
	private JSONArray result;
	private JSONObject data;
	private ProgressBar progressBar;
	private TextView tvName, tvBranch, tvBatch, tvMobile, tvEmail, tvBirthday, tvMarriageDay, tvCompany, tvCity;
	private String profileid;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		getActionBar().setTitle("Malaviyan Login");
		setContentView(R.layout.profile);
		getActionBar().setDisplayHomeAsUpEnabled(true);

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
		Log.e("Stay On SignUp Page", "Stay on SignUP page back Pressed");
	}

	public class ProfileFetch extends AsyncTask<Void, Void, Void>
	{
		ProgressDialog pdialog;

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			pdialog = new ProgressDialog(Profile.this);
			pdialog.setMessage("Signing in...");
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
					if (data != null && data.has(AppProperties.ACK)) {

						tvName.setText(data.getString(AppProperties.NAME));
						tvEmail.setText(data.getString(AppProperties.PARAM_EMAIL));
						tvMobile.setText(data.getString(AppProperties.MOBILE));
						tvBatch.setText(data.getString(AppProperties.BATCH));
						tvBranch.setText(data.getString(AppProperties.BRANCH));
						tvBirthday.setText(data.getString(AppProperties.BIRTHDAY));
						tvMarriageDay.setText(data.getString(AppProperties.MARRIAGEDAY));
						tvCompany.setText(data.getString(AppProperties.COMPANY));
						tvCity.setText(data.getString(AppProperties.CITY));

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