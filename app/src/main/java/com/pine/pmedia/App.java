package com.pine.pmedia;

import android.app.Application;
import android.content.Intent;
import android.os.Build;

import com.pine.pmedia.models.Song;
import com.pine.pmedia.services.MusicService;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class App extends Application {

    private static App _instance;

    private ArrayList<Song> mediaPlayList = new ArrayList<>();
    private ArrayList<Song> queueStore = new ArrayList<>();


    //=================
    // Suggest Screen
    //=================
    public boolean isReloadPlayList;
    public boolean isReloadFavorite;
    public boolean isReloadLastPlayed;
    public boolean isReloadRecentAdd;

    public static App getInstance() {

        if(_instance == null) {
            _instance = new App();
        }

        return _instance;
    }

    public ArrayList<Song> getMediaPlayList() {
        return mediaPlayList;
    }

    public void setMediaPlayList(ArrayList<Song> mediaPlayList) {
        this.mediaPlayList = mediaPlayList;
    }

    public ArrayList<Song> getQueueStore() {
        return queueStore;
    }

    public void setQueueStore(ArrayList<Song> queueStore) {
        this.queueStore = queueStore;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Intent playIntent = new Intent(this, MusicService.class);
        startService(playIntent);
//        try {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                startForegroundService(playIntent);
//            } else {
//                startService(playIntent);
//            }
//        }catch (IllegalStateException e){
//            e.printStackTrace();
//            return ;
//        }
    }
}
