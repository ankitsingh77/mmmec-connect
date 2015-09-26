package com.quipmate2.features;

import com.quipmate2.constants.AppProperties; 

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class Logout extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Log.e("Deleting Session", "Delteting session");
		Session session = new Session(getApplicationContext());
		session.delValue(AppProperties.PARAM_PASSWORD);
		session.delValue(AppProperties.MY_PROFILE_ID);
		session.delValue(AppProperties.MY_PROFILE_NAME);
		session.delValue(AppProperties.MY_PROFILE_PIC);
		session.delValue(AppProperties.SESSION_ID);
		session.delValue(AppProperties.SESSION_NAME);
		session.delValue(AppProperties.PROFILE_ID);
		session.delValue(AppProperties.PROFILE_IMAGE);
		session.commit();
		
		Intent stopChat = new Intent(Logout.this,ChatService.class);
		stopChat.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
		stopService(stopChat);
		
		Intent stopRealTime = new Intent(Logout.this,RealTimeService.class);
		stopRealTime.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
		stopService(stopRealTime);
		
		Intent login = new Intent(Logout.this,Login.class);
		login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(login);
		finish();
	}
}