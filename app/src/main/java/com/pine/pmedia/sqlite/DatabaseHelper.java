package com.pine.pmedia.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Table Name
    public static final String FAVORITE_TABLE = "FAVORITE_SONG";
    public static final String HISTORY_TABLE = "HISTORY_SONG";

    // Table columns
    public static final String _ID = "_id";
    public static final String _NAME = "_name";
    public static final String _SONG_ID = "_song_id";
    public static final String _CREATED_DATE = "_created_date";

    // Database Information
    static final String DB_NAME = "PCODE.DB";

    // database version
    static final int DB_VERSION = 2;

    // Creating table query
    private static final String CREATE_FAVORITE_TABLE = "create table " + FAVORITE_TABLE + "("
            + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + _NAME + " TEXT NOT NULL, "
            + _SONG_ID + " INTEGER, "
            + _CREATED_DATE + " TEXT);";

    private static final String CREATE_HISTORY_TABLE = "create table " + HISTORY_TABLE + "("
            + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + _NAME + " TEXT NOT NULL, "
            + _SONG_ID + " INTEGER, "
            + _CREATED_DATE + " TEXT);";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_FAVORITE_TABLE);
        db.execSQL(CREATE_HISTORY_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + FAVORITE_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + HISTORY_TABLE);
        onCreate(db);
    }
}
