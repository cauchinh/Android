package com.example.to_do_list;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class JobSqlite extends SQLiteOpenHelper {
    private static String DBName = "job.db";
    private static int VERSION = 2;
    private static String TABLE_NAME = "Job";
    private static String ID = "_id";
    private static String NAME = "name";
    private SQLiteDatabase mydb;

    public JobSqlite(@Nullable Context context) {
        super(context, DBName, null, VERSION);
    }

    public static String getID() {
        return ID;
    }

    public static String getNAME() {
        return NAME;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String queryTable = "CREATE TABLE " +TABLE_NAME + " ( "
                + ID + " INTEGER PRIMARY KEY, "
                + NAME + " TEXT NOT NULL" + ")";
        db.execSQL(queryTable);
    }
    public long insertData(int id, String name){
        ContentValues values = new ContentValues();
        values.put(ID, id);
        values.put(NAME, name);
        return mydb.insert(TABLE_NAME, null, values);
    }

    public Cursor DisplayAll(){
        if (mydb == null){
            openDB();
        }
        String query = "SELECT * FROM " + TABLE_NAME;
        return  mydb.rawQuery(query, null);
    }

    public void Delete(int id){
        String where = ID + " = " + id;
        mydb.delete(TABLE_NAME, where, null);
    }
    public long Update(int id, String name){
        ContentValues values = new ContentValues();
        values.put(ID, id);
        values.put(NAME, name);
        String where = ID + " = " + id;
        return mydb.update(TABLE_NAME, values, where, null);
    }
    // kiểm tra id trùng
    public boolean isIdExists(int id) {
        if (mydb == null) {
            openDB();
        }
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + ID + " = " + id;
        Cursor cursor = mydb.rawQuery(query, null);
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }
    // kiểm tra tên trùng
    public boolean isNameExists(String name) {
        if (mydb == null) {
            openDB();
        }
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + NAME + " = ?";
        Cursor cursor = mydb.rawQuery(query, new String[]{name});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }
    public void openDB(){
        mydb = getWritableDatabase();
    }
    public void closeDB(){
        if (mydb != null && mydb.isOpen()){
            mydb.close();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
