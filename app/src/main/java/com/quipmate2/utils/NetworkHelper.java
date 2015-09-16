package com.quipmate2.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.quipmate2.constants.AppProperties;

public class NetworkHelper {

	/**
	 * Check connection.
	 * 
	 * @param context
	 *            the context
	 * @return true, if successful
	 */
	public static boolean checkNetworkConnection(Context context) {
		boolean status = false;
		ConnectivityManager connMgr = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
			status = true;
		}

		return status;
	}

	public static boolean checkConnection(Context context) {
		boolean isSuccess = false;

		if (checkNetworkConnection(context)) {

			NetworkTimeOut networkTimeOut = new NetworkTimeOut(
					AppProperties.URL);
			try {
				isSuccess = networkTimeOut.execute().get();
			} catch (Exception e) {
				e.printStackTrace();
				isSuccess = false;
			}
		}
		return isSuccess;
	}
}
