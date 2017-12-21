package com.example.venka.musicapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by venka on 12/19/2017.
 */

public class SqliteDB extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "SongsData";
    private static final String USERS_TABLE = "SongsInfo";
    public SqliteDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USERS_TABLE="CREATE TABLE "+ USERS_TABLE +"( UID NUMBER , NAME TEXT , SONGURL TEXT , ARTISTS TEXT , IMGURL TEXT , SIZE NUMBER )";
        db.execSQL(CREATE_USERS_TABLE);
    }
    public void insertUser(int uid,String name,String songurl,String artists,String imgurl)
    {
        SQLiteDatabase db=getWritableDatabase();
        ContentValues insertValues = new ContentValues();
        insertValues.put("UID", uid);
        insertValues.put("NAME", name);
        insertValues.put("SONGURL", songurl);
        insertValues.put("ARTISTS", artists);
        insertValues.put("IMGURL", imgurl);
        db.insert(USERS_TABLE, null, insertValues);

    }
    public String[] getUser(String uid)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        String s[]=new String[5];
        String selectQuery = "SELECT * FROM " + USERS_TABLE+" WHERE UID = "+uid;
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                s[0]=cursor.getString(0);
                s[1]=cursor.getString(1);
                s[2]=cursor.getString(2);
                s[3]=cursor.getString(3);
                s[4]=cursor.getString(4);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return s;
    }
    public String[] getUserFromName(String uid)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        String s[]=new String[5];
        String selectQuery = "SELECT * FROM " + USERS_TABLE+" WHERE NAME = "+uid;
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                s[0]=cursor.getString(0);
                s[1]=cursor.getString(1);
                s[2]=cursor.getString(2);
                s[3]=cursor.getString(3);
                s[4]=cursor.getString(4);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return s;
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
