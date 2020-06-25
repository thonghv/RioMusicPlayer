package com.pine.pmedia.helpers;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;

import com.pine.pmedia.App;
import com.pine.pmedia.activities.AlbumActivity;
import com.pine.pmedia.activities.ArtistActivity;
import com.pine.pmedia.activities.FilterActivity;
import com.pine.pmedia.activities.GenreActivity;
import com.pine.pmedia.activities.MainActivity;
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
    private int viewType;

    public ExecuteProcessStartPlay(Context context, DBManager dbManager, MusicService mService,
                                   ArrayList<Song> songs, int position, int viewType) {
        this.context = context;
        this.dbManager = dbManager;
        this.mService = mService;
        this.songs = songs;
        this.position = position;
        this.viewType = viewType;
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
//            if(index == 0) {
//                index ++;
//                continue;
//            }

            dbManager.insertQueue(s.get_id(), s.get_title(), 0, 0);
            index ++;
        }

        CommonHelper.updateSettingSongPLaying(dbManager, mService.getMCurrSong().get_id(), position);

        // Insert history last play
        dbManager.insertHistory(mService.getMCurrSong().get_id(),
                mService.getMCurrSong().get_title());
        App.getInstance().isReloadLastPlayed = true;

        return null;
    }

    protected void onPostExecute(String response) {
        switch (viewType) {
            case Constants.VIEW_FAVORITE:
            case Constants.VIEW_RECENT_ADDED:
            case Constants.VIEW_LAST_PLAYED:
            case Constants.VIEW_PLAYLIST:
                ((FilterActivity) context).onUpdateUISongPlayBottom(true);
                return;
            case Constants.VIEW_ALBUM:
                ((AlbumActivity) context).onUpdateUISongPlayBottom(true);
                return;
            case Constants.VIEW_ARTIST:
                ((ArtistActivity) context).onUpdateUISongPlayBottom(true);
                return;
            case Constants.VIEW_GENRE:
                ((GenreActivity) context).onUpdateUISongPlayBottom(true);
                return;
        }
        ((MainActivity) context).onUpdateUISongPlayBottom(true);
    }
}
