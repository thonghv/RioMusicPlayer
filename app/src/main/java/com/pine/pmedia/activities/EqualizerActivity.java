package com.pine.pmedia.activities;

import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;

import com.pine.pmedia.R;
import com.pine.pmedia.equalizer.EqualizerFragment;
import com.pine.pmedia.services.MusicService;

public class EqualizerActivity extends BaseActivity implements IActivity {

    private MusicService mService;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_equalizer);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    protected void onHandler() {

        mService = super.getMService();
        int sessionId = mService.getMPlayer().getAudioSessionId();
        mService.getMPlayer().setLooping(true);
        EqualizerFragment equalizerFragment = EqualizerFragment.newBuilder()
                .setAccentColor(Color.parseColor("#4caf50"))
                .setAudioSessionId(sessionId)
                .build();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.eqFrame, equalizerFragment)
                .commit();

        // Init handle actions song play bottom controls
        this.initActionsSongPlayBottom();

        this.onUpdateUISongPlayBottom(false);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void initDBManager() {

    }

    @Override
    public void initControlsSongPlayBottom() {

    }

    @Override
    public void initActionsSongPlayBottom() {

    }

    @Override
    public void onUpdateUISongPlayBottom(boolean isAnimation) {

    }

    @Override
    public void initBroadcast() {

    }
}
