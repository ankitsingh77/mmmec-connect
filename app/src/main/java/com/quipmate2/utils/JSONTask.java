package com.quipmate2.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.quipmate2.constants.AppProperties;

import android.os.AsyncTask;
import android.util.Log;

public class JSONTask extends AsyncTask<String, String, JSONArray> {

	List<NameValuePair> postparams = new ArrayList<NameValuePair>();
	String URL = null;
	String method = null;
	
	static InputStream is = null;
	static JSONObject jObj = null;
	static JSONArray jArray = null;

	static String json = "";
	

	

	public JSONTask(String url, String method, List<NameValuePair> params) {
		this.URL = url;
		this.postparams = params;
		this.method = method;
		
	}
	
	
	@Override
	protected JSONArray doInBackground(String... params) {

		is = null;
		jObj = null;
		jArray = null;
		json = "";
		// Making HTTP request
		try {
			// Making HTTP request
			// check for request method
			if (method.equals("POST")) {

				// request method is POST
				DefaultHttpClient httpClient = AppProperties.appUserClient;
				HttpPost httpPost = new HttpPost(URL);
				httpPost.setEntity(new UrlEncodedFormEntity(postparams));

				HttpResponse httpResponse = httpClient.execute(httpPost);
				HttpEntity httpEntity = httpResponse.getEntity();
				is = httpEntity.getContent();

			} else if (method == "GET") {
				// request method is GET
				DefaultHttpClient httpClient = AppProperties.appUserClient;
				String paramString = URLEncodedUtils
						.format(postparams, "utf-8");
				URL += "?" + paramString;
				HttpGet httpGet = new HttpGet(URL);

				HttpResponse httpResponse = httpClient.execute(httpGet);
				HttpEntity httpEntity = httpResponse.getEntity();
				
				is = httpEntity.getContent();
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					is, "utf-8"), 8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			is.close();
			json = sb.toString();
		} catch (Exception e) {
			Log.e("Buffer Error", "Error converting result " + e.toString());
		}

		// try parse the string to a JSON object
		try {
			if (!json.substring(0, 1).equalsIgnoreCase("[")) {
				jObj = new JSONObject(json);
			}
		} catch (JSONException e) {
			Log.e("JSON Parser", "Error parsing data " + e.toString());
		}

		// try parse the string to a JSON array
		try {
			// if string parsed as JSON object then convert it to be parsed as
			// array
			if (jObj != null) {
				json = "[" + json + "]";
			}
			jArray = new JSONArray(json);
		} catch (JSONException e) {
			Log.e("JSON Parser", "Error parsing data " + e.toString());
		}

		// return JSON String
		return jArray;
	}
}