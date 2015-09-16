package com.quipmate2.utils;

import java.net.HttpURLConnection;
import java.net.URL;

import android.os.AsyncTask;

public class NetworkTimeOut extends AsyncTask<String, String, Boolean> {

	private final int connectionTimeOut = 2000;
	private final int readTimeOut = 30000;
	String URL = null;

	public NetworkTimeOut(String url) {
		this.URL = url;
	}

	@Override
	protected Boolean doInBackground(String... params) {
		boolean isSuccess = true;
		try {
			URL url = new URL(URL);

			HttpURLConnection httpConn = (HttpURLConnection) url
					.openConnection();
			httpConn.setInstanceFollowRedirects(false);
			httpConn.setConnectTimeout(connectionTimeOut);
			httpConn.setReadTimeout(readTimeOut);
			httpConn.setRequestMethod("HEAD");
			try {
				httpConn.connect();
				httpConn.getResponseCode();
			} catch (java.net.ConnectException e) {
				isSuccess = false;
			}
		} catch (Exception e) {
			isSuccess = false;
		}
		return isSuccess;
	}
}
