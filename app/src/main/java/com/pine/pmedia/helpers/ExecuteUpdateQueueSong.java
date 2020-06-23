package com.pine.pmedia.helpers;

import android.os.AsyncTask;

import com.pine.pmedia.App;
import com.pine.pmedia.models.Song;
import com.pine.pmedia.sqlite.DBManager;

import java.util.ArrayList;

public class ExecuteUpdateQueueSong extends AsyncTask<String, Void, String> {

    private DBManager dbManager;
    private ArrayList<Song> songs;

    public ExecuteUpdateQueueSong(DBManager dbManager, ArrayList<Song> songs) {
        this.dbManager = dbManager;
        this.songs = songs;
    }

    protected String doInBackground(String... params){
        if(songs.isEmpty()) {
            songs = App.getInstance().getMediaPlayList();
        }
        dbManager.deleteAllQueueSong();
        for(Song s: songs) {
            dbManager.insertQueue(s.get_id(), s.get_title(), 0, 0);
        }

        App.getInstance().getMService().setPlayingQueue(songs);
        return null;
    }

    protected void onPostExecute(String response) {

    }
}
