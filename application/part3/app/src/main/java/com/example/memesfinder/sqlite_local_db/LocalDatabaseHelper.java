package com.example.memesfinder.sqlite_local_db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class LocalDatabaseHelper extends SQLiteOpenHelper {
    // Table Name
    public static final String TABLE_NAME = "PREDICTIONS";

    // Table columns
    public static final String _PATH = "_path";
    public static final String PREDICTION = "prediction";
    public static final String IMAGE_HASH = "image_hash";

    // Database Information
    static final String DB_NAME = "PREDICTIONS.DB";

    // database version
    static final int DB_VERSION = 2;

    // Creating table query
    private static final String CREATE_TABLE = "create table " + TABLE_NAME + "(" + _PATH
            + " TEXT PRIMARY KEY, " + PREDICTION + " TEXT NOT NULL, " + IMAGE_HASH + " TEXT);";

    public LocalDatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
