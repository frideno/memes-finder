package com.example.memesfilter.sqlite_local_db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

public class LocalDatabaseManager {
    private LocalDatabaseHelper dbHelper;

    private Context context;

    private SQLiteDatabase database;

    public LocalDatabaseManager(Context c) {
        context = c;
        dbHelper = new LocalDatabaseHelper(c);
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public void insert(String path, String prediction, String imageHash) {
        ContentValues contentValue = new ContentValues();
        contentValue.put(LocalDatabaseHelper._PATH, path);
        contentValue.put(LocalDatabaseHelper.PREDICTION, prediction);
        contentValue.put(LocalDatabaseHelper.IMAGE_HASH, imageHash);
        database.insert(LocalDatabaseHelper.TABLE_NAME, null, contentValue);
    }

    public Cursor fetch() {
        String[] columns = new String[] { LocalDatabaseHelper._PATH, LocalDatabaseHelper.PREDICTION, LocalDatabaseHelper.IMAGE_HASH };
        try {
            Cursor cursor = database.query(LocalDatabaseHelper.TABLE_NAME, columns, null, null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
            }
            return cursor;

        } catch (SQLiteException e) {
            return null;
        }
    }

    public int update(long _id, String name, String desc) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(LocalDatabaseHelper.PREDICTION, name);
        contentValues.put(LocalDatabaseHelper.IMAGE_HASH, desc);
        int i = database.update(LocalDatabaseHelper.TABLE_NAME, contentValues, LocalDatabaseHelper._PATH + " = " + _id, null);
        return i;
    }

    public void delete(long _id) {
        database.delete(LocalDatabaseHelper.TABLE_NAME, LocalDatabaseHelper._PATH + "=" + _id, null);
    }
}

