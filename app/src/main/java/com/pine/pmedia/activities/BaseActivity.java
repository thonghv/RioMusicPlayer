package com.pine.pmedia.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;

import androidx.appcompat.app.AppCompatActivity;

import com.pine.pmedia.services.MusicService;

public abstract class BaseActivity extends AppCompatActivity {

    private Intent playIntent;
    private MusicService mService;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inItService();
    }

    protected ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder) service;
            mService = binder.getService();

            onHandler();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbindService(serviceConnection);
    }

    private void inItService() {
        playIntent = new Intent(this, MusicService.class);
        this.bindService(playIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    public MusicService getMService() {
        return mService;
    }

    public void setMService(MusicService mService) {
        this.mService = mService;
    }

    protected abstract void onHandler();
}
