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
import com.quipmate2.utils.NetworkHelper;

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
import java.io.Console;

public class VerifyCode extends Activity implements OnClickListener {
    private EditText etVerifyCode;
    private Button btVerifyCode;
    private TextView codehave;
    private JSONArray result;
    private JSONObject data;
    private Session signup;
    private Session session;
    private ProgressBar progressBar;
    private String mobile, code;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        getActionBar().setTitle("Malaviyan Login");
        setContentView(R.layout.code_verify);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        Bundle iData = intent.getExtras();

        mobile = iData.getString(AppProperties.MOBILE);

        signup = new Session(VerifyCode.this);
        etVerifyCode = (EditText) findViewById(R.id.etVerifyCode);
        btVerifyCode = (Button) findViewById(R.id.btVerifyCode);
        session = new Session(getApplicationContext());
        btVerifyCode.setOnClickListener(this);
    }


    public void onClick(View v) {
        if(v.getId() == R.id.btVerifyCode){
            code = etVerifyCode.getText().toString();
            signup.setValue(AppProperties.CODE, code);
            signup.commit();
            new verifyCode().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
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

    class verifyCode extends AsyncTask<Void, Void, Void>{

        ProgressDialog pdialog;

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            pdialog = new ProgressDialog(VerifyCode.this);
            pdialog.setMessage("Verifying the code");
            pdialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try{
                List<NameValuePair> apiParams = new ArrayList<NameValuePair>();

                apiParams.add(new BasicNameValuePair(AppProperties.ACTION, "malaviyan_login"));
                apiParams.add(new BasicNameValuePair("mobile", mobile));
                apiParams.add(new BasicNameValuePair("code", code));

                result = CommonMethods.loadJSONData(AppProperties.URL, AppProperties.METHOD_GET, apiParams);
                if(result != null){
                    data = result.getJSONObject(0);
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
            session = new Session(VerifyCode.this);

            if(pdialog.isShowing())
                pdialog.dismiss();

            try {
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
                        startService(new Intent(VerifyCode.this, RealTimeService.class));
                        startService(new Intent(VerifyCode.this, ChatService.class));
                        Intent intent = new Intent(VerifyCode.this, WelcomeActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        CommonMethods.ShowInfo(VerifyCode.this, "Some problem in Login. Please try again.").show();
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
                                CommonMethods.ShowInfo(VerifyCode.this,
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