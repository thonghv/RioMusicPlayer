package com.pine.pmedia.activities;

import android.os.Bundle;

import com.pine.pmedia.R;
import com.pine.pmedia.services.MusicService;

public class AlbumActivity extends BaseActivity  {

    private MusicService mService;

    @Override
    protected void onHandler() {
        mService = getMService();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.album_detail_screen);

        getActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
