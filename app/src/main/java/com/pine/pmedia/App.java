package com.pine.pmedia;

import android.app.Application;
import android.content.Intent;
import android.os.Build;

import com.pine.pmedia.services.MusicService;

public class App extends Application {

    private static App _instance;

    public static App getInstance() {

        if(_instance == null) {
            _instance = new App();
        }

        return _instance;
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
