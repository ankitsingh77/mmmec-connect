package com.quipmate2.adapter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.quipmate2.dto.CoWorkers;
import org.apache.james.mime4j.field.datetime.DateTime;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by SAnkit on 10/4/2015.
 */
public class SqliteDBHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "MalaviyanConnect.db";
    public static final String BIO_TABLE_NAME = "Bio";
    public static final String BIO_COLUMN_PROFILEID = "PROFILEID";
    public static final String BIO_COLUMN_EMAIL = "EMAIL";
    public static final String BIO_COLUMN_NAME = "NAME";
    public static final String BIO_COLUMN_SEX = "SEX";
    public static final String BIO_COLUMN_BIRTHDAY = "BIRTHDATE";
    public static final String BIO_COLUMN_BATCH = "BATCH";
    public static final String BIO_COLUMN_MOBILE = "MOBILE";
    public static final String BIO_COLUMN_CITY = "CITY";
    public static final String BIO_COLUMN_COMPANY = "COMPANY";
    public static final String BIO_COLUMN_MARRIAGEDAY = "MARRIAGEDAY";
    public static final String BIO_COLUMN_BRANCH = "BRANCH";
    public static final String BIO_COLUMN_CODE = "CODE";
    public static final String BIO_COLUMN_PHOTO = "PHOTO";
    public static final String CHAT_TABLE_NAME = "GROUP_INBOX";
    public static final String CHAT_COLUMN_ID = "ACTIONID";
    public static final String CHAT_COLUMN_ACTIONBY = "ACTIONBY";
    public static final String CHAT_COLUMN_MESSAGE = "MESSAGE";
    public static final String CHAT_COLUMN_FILE = "FILE";
    public static final String CHAT_COLUMN_TIME = "TIME";
    public static final String CHAT_COLUMN_READBIT  = "READBIT";
    public static final String CHAT_COLUMN_FLAGBY = "FLAGBY";
    public static final String CHAT_COLUMN_FLAGON = "FLAGON";
    private HashMap hp;

    public SqliteDBHelper(Context context)
    {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL(
                "create table bio " +
                        "(PROFILEID int primary key,email text, name text, SEX bit, BIRTHDAY DATETIME, BATCH text, MOBILE text, CITY text, COMPANY text, MARRIAGEDAY text,BRANCH text,CODE text, PHOTO text )"
        );
        db.execSQL(
                "create table GROUP_INBOX " +
                        "(ACTIONID int primary key,ACTIONBY int, MESSAGE text, FILE bit, TIME bigint, READBIT bit, FLAGBY bit, FLAGON bit)"
        );
    }

    public Cursor getBIOData(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from bio where profileid="+id+"", null );
        return res;
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS bio");
        db.execSQL("DROP TABLE IF EXISTS GROUP_INBOX");
        onCreate(db);
    }

    public boolean insertBio(int id,String email, String name, Boolean sex,String birthDay,String batch,String mobile,String city,String company, String marriageDay,String branch, String code, String photo )
    {
        //PROFILEID int primary key,email text, name text, SEX bit, BIRTHDAY DATETIME, BATCH int, MOBILE bigint, CITY text, COMPANY text, MARRIAGEDAY DATETIME,BRANCH text,CODE text, PHOTO text
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(BIO_COLUMN_PROFILEID,id);
        contentValues.put(BIO_COLUMN_EMAIL, email);
        contentValues.put(BIO_COLUMN_NAME, name);
        contentValues.put(BIO_COLUMN_SEX, sex);
        contentValues.put(BIO_COLUMN_BIRTHDAY, birthDay);
        contentValues.put(BIO_COLUMN_BATCH, batch);
        contentValues.put(BIO_COLUMN_MOBILE, mobile);
        contentValues.put(BIO_COLUMN_CITY, city);
        contentValues.put(BIO_COLUMN_COMPANY, company);
        contentValues.put(BIO_COLUMN_MARRIAGEDAY, marriageDay);
        contentValues.put(BIO_COLUMN_BRANCH, branch);
        contentValues.put(BIO_COLUMN_CODE, code);
        contentValues.put(BIO_COLUMN_PHOTO, photo);
        db.insert(BIO_TABLE_NAME, null, contentValues);
        return true;
    }

    public int getBIOnumberOfRows(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, BIO_TABLE_NAME);
        return numRows;
    }

    public boolean updateBio  (int id,String email, String name, Boolean sex,String birthDay,String batch,String mobile,String city,String company, String marriageDay,String branch, String code, String photo )
    {
        //PROFILEID int primary key,email text, name text, SEX bit, BIRTHDAY DATETIME, BATCH int, MOBILE bigint, CITY text, COMPANY text, MARRIAGEDAY DATETIME,BRANCH text,CODE text, PHOTO text
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(BIO_COLUMN_EMAIL, email);
        contentValues.put(BIO_COLUMN_NAME, name);
        contentValues.put(BIO_COLUMN_SEX, sex);
        contentValues.put(BIO_COLUMN_BIRTHDAY, birthDay);
        contentValues.put(BIO_COLUMN_BATCH, batch);
        contentValues.put(BIO_COLUMN_MOBILE, mobile);
        contentValues.put(BIO_COLUMN_CITY, city);
        contentValues.put(BIO_COLUMN_COMPANY, company);
        contentValues.put(BIO_COLUMN_MARRIAGEDAY, marriageDay);
        contentValues.put(BIO_COLUMN_BRANCH, branch);
        contentValues.put(BIO_COLUMN_CODE, code);
        contentValues.put(BIO_COLUMN_PHOTO, photo);
        db.update(BIO_TABLE_NAME, contentValues, "PROFILEID = ? ", new String[]{Integer.toString(id)});
        return true;
    }


    public Integer deleteTransaction (int id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(BIO_TABLE_NAME,
                "PROFILEID = ? ",
                new String[] { Integer.toString(id) });
    }

    public ArrayList<CoWorkers> getAllCoWorkers() {
        ArrayList<CoWorkers> array_list = new ArrayList<CoWorkers>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from BIO", null);
        res.moveToFirst();

        while (res.isAfterLast() == false) {
            CoWorkers temp = new CoWorkers();
            temp.setProfileId(Integer.parseInt(res.getString(res.getColumnIndex(BIO_COLUMN_PROFILEID))));
            temp.setName(res.getString(res.getColumnIndex(BIO_COLUMN_NAME)));
            temp.setEmail(res.getString(res.getColumnIndex(BIO_COLUMN_EMAIL)));
            temp.setBatch(res.getString(res.getColumnIndex(BIO_COLUMN_BATCH)));
            temp.setSex(Boolean.parseBoolean(res.getString(res.getColumnIndex(BIO_COLUMN_SEX))));
            temp.setBirthDay(res.getString(res.getColumnIndex(BIO_COLUMN_BIRTHDAY)));
            temp.setMobile(res.getString(res.getColumnIndex(BIO_COLUMN_MOBILE)));
            temp.setCity(res.getString(res.getColumnIndex(BIO_COLUMN_CITY)));
            temp.setCompany(res.getString(res.getColumnIndex(BIO_COLUMN_COMPANY)));
            temp.setMarriageDay(res.getString(res.getColumnIndex(BIO_COLUMN_MARRIAGEDAY)));
            temp.setBranch(res.getString(res.getColumnIndex(BIO_COLUMN_BRANCH)));
            temp.setCode(res.getString(res.getColumnIndex(BIO_COLUMN_CODE)));
            temp.setPhoto(res.getString(res.getColumnIndex(BIO_COLUMN_PHOTO)));
            array_list.add(temp);
            res.moveToNext();
        }
        return array_list;
    }

    public boolean deleteAllCoWorkers() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("Delete from BIO");
        return true;
    }
}
