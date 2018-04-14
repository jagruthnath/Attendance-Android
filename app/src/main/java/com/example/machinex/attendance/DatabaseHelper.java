package com.example.machinex.attendance;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.Date;



public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Student.db";
    static final String TABLE_NAME = "attendence";
    static final String COLUMN_TIMESTAMP = "timestamp";
    static final String COLUMN_SID = "sid";
    static final String COLUMN_STATUS = "penalty";
    static final String COLUMN_LATITUDE = "latitude";
    static final String COLUMN_LONGITUDE = "longitude";

    // Database Version
    private static final int DATABASE_VERSION = 1;

    DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {

        // create student table
        final String SQL_CREATE_ATTENDANCE_TABLE = " CREATE TABLE " + TABLE_NAME + "(ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_SID + " INTEGER NOT NULL, " +
                COLUMN_STATUS + " FLOAT NOT NULL, " +
                COLUMN_TIMESTAMP + " DATETIME DEFAULT (datetime('now','localtime')),"+
                COLUMN_LATITUDE + " DOUBLE NOT NULL, " +
                COLUMN_LONGITUDE + " DOUBLE NOT NULL " +
                "); ";
        db.execSQL(SQL_CREATE_ATTENDANCE_TABLE);
    }
    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);
    }
    public Cursor getAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("select * from "+TABLE_NAME,null);
    }
    public boolean updateData(Date date,String checkIn,String checkout,String afternoon) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        // updating row
        int a=db.update(TABLE_NAME, values, "Date = ?", new String[]{String.valueOf(date)});
        System.out.print(a);
        if (a==0)
        {
            db.insert(TABLE_NAME,null,values);
        }
        else
        {
            a=db.update(TABLE_NAME, values, "Date = ?", new String[]{String.valueOf(date)});
        }
        return true;
    }

    public void deleteData(String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, "Date = ?",
                new String[]{date});
    }

}