package com.quipmate2.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;

public class FeedData extends SQLiteOpenHelper {

   public static final String DATABASE_NAME = "MyDBName.db";
   public static final String NEWSFEED_TABLE_NAME = "newsfeed";
   public static final String NAME_TABLE_NAME = "name";
   public static final String PIMAGE_TABLE_NAME = "pi_image";
 

   private HashMap<String, String> hp;

   public FeedData(Context context)
   {
      super(context, DATABASE_NAME , null, 1);
   }

   @Override
   public void onCreate(SQLiteDatabase db) {
      // TODO Auto-generated method stub
	   
      db.execSQL(
      "create table " +NEWSFEED_TABLE_NAME+
      "(id integer primary key, actiontype text, actionby text, postby text, time text,actionon text,"
      + "excited text, meta text, title text, life_is_fun text, page text,"
      + "files text, video text, pageid text)"
      );
      
      
      db.execSQL("create table " +NAME_TABLE_NAME+
    	      "(id integer primary key, nameKey text, nameValue text)");
      
      db.execSQL("create table " +PIMAGE_TABLE_NAME+
    	      "(id integer primary key, piimageKey text, piimageValue text)");
      
      
   }

   @Override
   public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
      // TODO Auto-generated method stub
      db.execSQL("DROP TABLE IF EXISTS "+NEWSFEED_TABLE_NAME);
      db.execSQL("DROP TABLE IF EXISTS "+NAME_TABLE_NAME);
      db.execSQL("DROP TABLE IF EXISTS "+PIMAGE_TABLE_NAME);
      onCreate(db);
   }
   
	
   public void insertNewsFeed  (String actiontype, String actionby, String postby, String time ,String actionon,
		   String excited, String meta, String title, String life_is_fun ,String page,
		   String files, String video, String pageid)
   {
      SQLiteDatabase db = this.getWritableDatabase();
      ContentValues contentValues = new ContentValues();

      contentValues.put("actiontype", actiontype);
      contentValues.put("actionby", actionby);
      contentValues.put("postby", postby);	
      contentValues.put("time", time);
      contentValues.put("actionon", actionon);
      
      contentValues.put("excited", excited);
      contentValues.put("meta", meta);
      contentValues.put("title", title);	
      contentValues.put("life_is_fun", life_is_fun);
      contentValues.put("page", page);
      
      contentValues.put("files", files);
      contentValues.put("video", video);
      contentValues.put("pageid", pageid);	
  

      db.insert(NEWSFEED_TABLE_NAME, null, contentValues);
      
   }
   
   
   public void insertName(String nameKey ,String nameValue){
	   SQLiteDatabase db = this.getWritableDatabase();
	      ContentValues contentValues = new ContentValues();

	      contentValues.put("nameKey", nameKey);
	      contentValues.put("nameValue", nameValue);
	  

	      db.insert(NEWSFEED_TABLE_NAME, null, contentValues);
   }
   
 public void insertPImage(String piimageKey , String piimageValue){
	 SQLiteDatabase db = this.getWritableDatabase();
     ContentValues contentValues = new ContentValues();

     contentValues.put("piimageKey", piimageKey);
     contentValues.put("piimageValue", piimageValue);
  
 

     db.insert(NEWSFEED_TABLE_NAME, null, contentValues);
   }
   
   
   public Cursor getData(int id){
      SQLiteDatabase db = this.getReadableDatabase();
      Cursor res =  db.rawQuery( "select * from +NEWSFEED_TABLE_NAME+ where id="+id+"", null );
      return res;
   }
   public int numberOfRows(){
      SQLiteDatabase db = this.getReadableDatabase();
      int numRows = (int) DatabaseUtils.queryNumEntries(db, NEWSFEED_TABLE_NAME);
      return numRows;
   }
   public boolean updateNewsfeed (int id ,String actiontype, String actionby, String postby, String time ,String actionon,
		   String excited, String meta, String title, String life_is_fun ,String page,
		   String files, String video, String pageid)
   {
      SQLiteDatabase db = this.getWritableDatabase();
      ContentValues contentValues = new ContentValues();
      contentValues.put("actiontype", actiontype);
      contentValues.put("actionby", actionby);
      contentValues.put("postby", postby);	
      contentValues.put("time", time);
      contentValues.put("actionon", actionon);
      
      contentValues.put("excited", excited);
      contentValues.put("meta", meta);
      contentValues.put("title", title);	
      contentValues.put("life_is_fun", life_is_fun);
      contentValues.put("page", page);
      
      contentValues.put("files", files);
      contentValues.put("video", video);
      contentValues.put("pageid", pageid);	
  
      db.update("contacts", contentValues, "id = ? ", new String[] { Integer.toString(id) } );
      return true;
   }

   public Integer deleteContact (Integer id)
   {
      SQLiteDatabase db = this.getWritableDatabase();
      return db.delete("contacts", 
      "id = ? ", 
      new String[] { Integer.toString(id) });
   }
   public ArrayList<String> getAllCotacts()
   {
      ArrayList<String> array_list = new ArrayList<String>();
      //hp = new HashMap();
      SQLiteDatabase db = this.getReadableDatabase();
      Cursor res =  db.rawQuery( "select * from "+NEWSFEED_TABLE_NAME, null );
      res.moveToFirst();
      while(res.isAfterLast() == false){
      // array_list.add(res.getString(res.getColumnIndex(CONTACTS_COLUMN_NAME)));
      res.moveToNext();
      }
   return array_list;
   }
}