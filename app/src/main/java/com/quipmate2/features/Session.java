package com.quipmate2.features;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class Session {
	
	SharedPreferences store;
	Editor edit;
	Context _context;
	
	@SuppressLint("CommitPrefEdits")
	public Session(Context context){
		this._context = context;
		store = _context.getSharedPreferences("SessionStore", 0);
		edit = store.edit();
	}
	
	public Boolean hasKey(String key){
		return store.contains(key);
	}
	
	public Boolean commit(){
		return edit.commit();
	}
	
	public void setValue(String key, String value){
		System.out.println(key+" "+value);
		edit.putString(key, value);
	}
	
	public String getValue(String key){
		return store.getString(key, null);
	}
	
	public void delValue(String key){
		edit.remove(key);
	}

}
