package com.pine.pmedia.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Table Name
    public static final String FAVORITE_SONG = "FAVORITE_SONG";
    public static final String HISTORY_SONG = "HISTORY_SONG";
    public static final String RECENT_SONG = "RECENT_SONG";
    public static final String QUEUE_SONG = "QUEUE_SONG";

    // Table columns
    public static final String _ID = "_id";
    public static final String _NAME = "_name";
    public static final String _SONG_ID = "_songId";
    public static final String _CREATED_DATE = "_createdDate";
    public static final String _IS_PLAY = "_isPlay";
    public static final String _DURATION_CURRENT = "_durationCurrent";

    // Database Information
    static final String DB_NAME = "PCODE.DB";

    // database version
    static final int DB_VERSION = 1;

    // Creating table query
    private static final String CREATE_FAVORITE_SONG = "create table " + FAVORITE_SONG + "("
            + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + _NAME + " TEXT NOT NULL, "
            + _SONG_ID + " INTEGER, "
            + _CREATED_DATE + " TEXT);";

    private static final String CREATE_HISTORY_SONG = "create table " + HISTORY_SONG + "("
            + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + _NAME + " TEXT NOT NULL, "
            + _SONG_ID + " INTEGER, "
            + _CREATED_DATE + " TEXT);";

    private static final String CREATE_RECENT_SONG = "create table " + RECENT_SONG + "("
            + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + _NAME + " TEXT NOT NULL, "
            + _SONG_ID + " INTEGER, "
            + _CREATED_DATE + " TEXT);";

    private static final String CREATE_QUEUE_SONG = "create table " + QUEUE_SONG + "("
            + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + _NAME + " TEXT NOT NULL, "
            + _SONG_ID + " INTEGER, "
            + _IS_PLAY + " INTEGER, "
            + _DURATION_CURRENT + " INTEGER, "
            + _CREATED_DATE + " TEXT);";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_FAVORITE_SONG);
        db.execSQL(CREATE_HISTORY_SONG);
        db.execSQL(CREATE_RECENT_SONG);
        db.execSQL(CREATE_QUEUE_SONG);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + FAVORITE_SONG);
        db.execSQL("DROP TABLE IF EXISTS " + HISTORY_SONG);
        db.execSQL("DROP TABLE IF EXISTS " + RECENT_SONG);
        db.execSQL("DROP TABLE IF EXISTS " + QUEUE_SONG);
        onCreate(db);
    }
}
