package com.pine.pmedia.extensions;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;

import com.pine.pmedia.activities.MainActivity;
import com.pine.pmedia.helpers.Constants;
import com.pine.pmedia.models.Song;
import com.pine.pmedia.services.MusicService;
import com.pine.pmedia.sqlite.DBManager;

import java.util.ArrayList;

public class ExecuteProcessStartPlay extends AsyncTask<String, Void, String> {

    private Context context;
    private MusicService mService;
    private DBManager dbManager;
    private ArrayList<Song> songs;
    private int position;

    public ExecuteProcessStartPlay(Context context, DBManager dbManager, MusicService mService,
                                   ArrayList<Song> songs, int position) {
        this.context = context;
        this.dbManager = dbManager;
        this.mService = mService;
        this.songs = songs;
        this.position = position;
    }

    @Override
    protected String doInBackground(String... strings) {

        mService.setPlayingQueue(songs);
        mService.checkAndResetPlay();

        Bundle bundle = new Bundle();
        bundle.putInt(Constants.KEY_POSITION, position);
        mService.onProcess(Constants.PLAY_PAUSE, bundle);

        // Add db queue songs
        dbManager.deleteAllQueueSong();
        int index = 0;
        for(Song s: songs) {
            if(index == 0) {
                index ++;
                continue;
            }
            int isPlay = index == position ? 1 : 0;
            dbManager.insertQueue(s.get_id(), s.get_title(), isPlay, 0);
            index ++;
        }

        return null;
    }

    protected void onPostExecute(String response) {
        ((MainActivity) context).onUpdateBottomPlayUI();
    }
}
