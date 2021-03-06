package com.pine.pmedia.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.pine.pmedia.helpers.CommonHelper;
import com.pine.pmedia.models.Song;

import java.util.ArrayList;
import java.util.Date;

public class DBManager {

    private DatabaseHelper dbHelper;

    private Context context;

    private SQLiteDatabase database;

    public DBManager(Context c) {
        context = c;
    }

    public DBManager open() throws SQLException {
        dbHelper = new DatabaseHelper(context);
        database = dbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        dbHelper.close();
    }

    public Cursor fetchFavoriteSong() {
        String[] columns = new String[] { DatabaseHelper._ID, DatabaseHelper._NAME, DatabaseHelper._SONG_ID};
        Cursor cursor = database.query(DatabaseHelper.FAVORITE_SONG, columns, null,
                null, null, null, DatabaseHelper._CREATED_DATE + " DESC");
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    public Cursor fetchHistorySong() {
        String[] columns = new String[] { DatabaseHelper._ID,
                DatabaseHelper._NAME, DatabaseHelper._SONG_ID, DatabaseHelper._CREATED_DATE};
        Cursor cursor = database.query(DatabaseHelper.HISTORY_SONG, columns, null,
                null, null, null, DatabaseHelper._CREATED_DATE + " DESC");
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    public Cursor fetchRecentSong() {
        String[] columns = new String[] { DatabaseHelper._ID,
                DatabaseHelper._NAME, DatabaseHelper._SONG_ID, DatabaseHelper._CREATED_DATE};
        Cursor cursor = database.query(DatabaseHelper.RECENT_SONG, columns, null,
                null, null, null, DatabaseHelper._CREATED_DATE + " DESC");
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    public Cursor fetchQueueSong() {
        String[] columns = new String[] { DatabaseHelper._ID,
                DatabaseHelper._NAME, DatabaseHelper._SONG_ID, DatabaseHelper._CREATED_DATE};
        Cursor cursor = database.query(DatabaseHelper.QUEUE_SONG, columns, null,
                null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }


    public Cursor fetchPreviousSong() {

        String[] columns = new String[] { DatabaseHelper._ID,
                DatabaseHelper._NAME, DatabaseHelper._SONG_ID, DatabaseHelper._CREATED_DATE};
        Cursor cursor = database.query(DatabaseHelper.QUEUE_SONG, columns, DatabaseHelper._IS_PLAY + "=?",
                new String[] { "1" }, null, null, DatabaseHelper._CREATED_DATE + " DESC");
        return cursor;
    }

    public Cursor fetchSetting(String key) {

        String[] columns = new String[] { DatabaseHelper._ID, DatabaseHelper._KEY, DatabaseHelper._VALUE};
        Cursor cursor = database.query(DatabaseHelper.SETTING, columns, DatabaseHelper._KEY + "=?",
                new String[] { key }, null, null, null);
        return cursor;
    }

    public int update(long _id, String name, String desc) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper._NAME, name);
        int i = database.update(DatabaseHelper.FAVORITE_SONG, contentValues, DatabaseHelper._ID + " = " + _id, null);
        return i;
    }

    public void deleteFavoriteById(long _id) {
        database.delete(DatabaseHelper.FAVORITE_SONG, DatabaseHelper._ID + "=" + _id, null);
    }

    public void deleteHistoryById(long _id) {
        database.delete(DatabaseHelper.HISTORY_SONG, DatabaseHelper._ID + "=" + _id, null);
    }

    public void deleteRecentById(long _id) {
        database.delete(DatabaseHelper.RECENT_SONG, DatabaseHelper._ID + "=" + _id, null);
    }

    public void deleteAllRecentSong() {
        database.delete(DatabaseHelper.RECENT_SONG, null, null);
    }

    public void deleteAllQueueSong() {
        database.delete(DatabaseHelper.QUEUE_SONG, null, null);
    }

    public void insertFavorite(long id, String name) {
        ContentValues contentValue = new ContentValues();
        contentValue.put(DatabaseHelper._NAME, name);
        contentValue.put(DatabaseHelper._SONG_ID, id);
        contentValue.put(DatabaseHelper._CREATED_DATE, CommonHelper.dateToFormat(new Date()));
        database.insert(DatabaseHelper.FAVORITE_SONG, null, contentValue);
    }

    public void insertHistory(long id, String name) {

        // Check is exits song in history list
        Song songFind = getSongFromHistory(id);
        if(songFind != null) {
            deleteHistoryById(songFind.get_historyId());
        }

        ContentValues contentValue = new ContentValues();
        contentValue.put(DatabaseHelper._NAME, name);
        contentValue.put(DatabaseHelper._SONG_ID, id);
        contentValue.put(DatabaseHelper._CREATED_DATE, CommonHelper.dateToFormat(new Date()));
        database.insert(DatabaseHelper.HISTORY_SONG, null, contentValue);
    }

    public void insertRecent(long id, String name) {
        ContentValues contentValue = new ContentValues();
        contentValue.put(DatabaseHelper._NAME, name);
        contentValue.put(DatabaseHelper._SONG_ID, id);
        contentValue.put(DatabaseHelper._CREATED_DATE, CommonHelper.dateToFormat(new Date()));
        database.insert(DatabaseHelper.RECENT_SONG, null, contentValue);
    }

    public void insertQueue(long id, String name, int isPlay, int durationCurrent) {
        ContentValues contentValue = new ContentValues();
        contentValue.put(DatabaseHelper._NAME, name);
        contentValue.put(DatabaseHelper._SONG_ID, id);
        contentValue.put(DatabaseHelper._IS_PLAY, isPlay);
        contentValue.put(DatabaseHelper._DURATION_CURRENT, durationCurrent);
        contentValue.put(DatabaseHelper._CREATED_DATE, CommonHelper.dateToFormat(new Date()));
        database.insert(DatabaseHelper.QUEUE_SONG, null, contentValue);
    }

    public int getCountFavorite() {

        String countQuery = "SELECT  * FROM " + DatabaseHelper.FAVORITE_SONG;
        Cursor cursor = database.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    public int getCountHistory() {

        String countQuery = "SELECT  * FROM " + DatabaseHelper.HISTORY_SONG;
        Cursor cursor = database.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    public int getCountRecent() {

        String countQuery = "SELECT  * FROM " + DatabaseHelper.RECENT_SONG;
        Cursor cursor = database.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }


    public boolean isExitsFavorite(long _id) {

        String countQuery = "SELECT  * FROM " + DatabaseHelper.FAVORITE_SONG + " WHERE " + DatabaseHelper._SONG_ID + " = ?";
        Cursor cursor = database.rawQuery(countQuery, new String[] { String.valueOf(_id) });
        int count = cursor.getCount();
        cursor.close();
        return count > 0;
    }

    public ArrayList<Song> getFavoritesSong() {

        ArrayList<Song> results = new ArrayList<>();
        Cursor cursor = fetchFavoriteSong();
        while (!cursor.isAfterLast()) {
            int songId = cursor.getInt(cursor.getColumnIndex(DatabaseHelper._SONG_ID));
            long favoriteId = cursor.getInt(cursor.getColumnIndex(DatabaseHelper._ID));

            Song temp = new Song();
            temp.set_id(songId);
            temp.set_favoriteId(favoriteId);

            results.add(temp);
            cursor.moveToNext();
        }

        return results;
    }

    public ArrayList<Song> getHistorySong() {

        ArrayList<Song> results = new ArrayList<>();
        Cursor cursor = fetchHistorySong();
        while (!cursor.isAfterLast()) {
            int songId = cursor.getInt(cursor.getColumnIndex(DatabaseHelper._SONG_ID));
            long historyId = cursor.getInt(cursor.getColumnIndex(DatabaseHelper._ID));

            Song temp = new Song();
            temp.set_id(songId);
            temp.set_historyId(historyId);

            results.add(temp);
            cursor.moveToNext();
        }

        return results;
    }

    public ArrayList<Song> getQueueSong() {

        ArrayList<Song> results = new ArrayList<>();
        Cursor cursor = fetchQueueSong();
        while (!cursor.isAfterLast()) {
            int songId = cursor.getInt(cursor.getColumnIndex(DatabaseHelper._SONG_ID));
            long queueId = cursor.getInt(cursor.getColumnIndex(DatabaseHelper._ID));

            Song temp = new Song();
            temp.set_id(songId);
            temp.set_queueId(queueId);

            results.add(temp);
            cursor.moveToNext();
        }

        return results;
    }

    public ArrayList<Song> getRecentSong() {

        ArrayList<Song> results = new ArrayList<>();
        Cursor cursor = fetchRecentSong();
        while (!cursor.isAfterLast()) {
            int songId = cursor.getInt(cursor.getColumnIndex(DatabaseHelper._SONG_ID));
            long recentId = cursor.getInt(cursor.getColumnIndex(DatabaseHelper._ID));

            Song temp = new Song();
            temp.set_id(songId);
            temp.set_historyId(recentId);

            results.add(temp);
            cursor.moveToNext();
        }

        return results;
    }

    public Song getSongFromHistory(long songIdFind) {

        Song result = new Song();
        Cursor cursor = fetchHistorySong();

        while (!cursor.isAfterLast()) {

            long songId = cursor.getInt(cursor.getColumnIndex(DatabaseHelper._SONG_ID));
            long historyId = cursor.getInt(cursor.getColumnIndex(DatabaseHelper._ID));

            if(songIdFind == songId) {
                result.set_historyId(historyId);
                result.set_id(songId);
                return result;
            }
            cursor.moveToNext();
        }

        return null;
    }

    public long findPreviousSong() {

        Cursor cursor = fetchPreviousSong();
        if (cursor.moveToFirst()){
            do{
                long songId = cursor.getLong(cursor.getColumnIndex(DatabaseHelper._SONG_ID));
                return songId;
            }while(cursor.moveToNext());
        }

        return -1;
    }

    public void insertSetting(String key, String value) {

        ContentValues contentValue = new ContentValues();
        contentValue.put(DatabaseHelper._KEY, key);
        contentValue.put(DatabaseHelper._VALUE, value);
        database.insert(DatabaseHelper.SETTING, null, contentValue);
    }

    public void updateSettingByKey(String key, String value) {

        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper._VALUE, value);
        database.update(DatabaseHelper.SETTING, contentValues, DatabaseHelper._KEY + " = '" + key + "'", null);
    }


    public String getSettingByKey(String key) {

        Cursor cursor = fetchSetting(key);
        if (cursor.moveToFirst()){
            do{
                String value = cursor.getString(cursor.getColumnIndex(DatabaseHelper._VALUE));
                return value;
            }while(cursor.moveToNext());
        }

        return null;
    }
}
