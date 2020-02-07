package com.pine.pmedia.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.pine.pmedia.R;
import com.pine.pmedia.adapters.NavigationDrawerAdapter;
import com.pine.pmedia.adapters.TabsPagerAdapter;
import com.pine.pmedia.fragments.FavoriteFragment;
import com.pine.pmedia.fragments.HomeFragment;
import com.pine.pmedia.helpers.Constants;
import com.pine.pmedia.services.MusicService;

import java.util.ArrayList;

public class MainActivity extends BaseActivity {

    public static final String mBroadcastAction = "STRING_BROADCAST_ACTION";
    private IntentFilter mIntentFilter;

    //=====================
    // Variable Common
    //=====================
    public static DrawerLayout drawerLayout = null;
    private ArrayList<String> navigationDrawerIconList = new ArrayList<>();
    private int[] imageForDrawer = new int[]{
            R.drawable.navigation_allsongs, R.drawable.navigation_favorites,
            R.drawable.navigation_settings, R.drawable.navigation_aboutus };

    private MusicService mService;
    private RelativeLayout bottomPlayMainScreen;
    private TextView songTitleView;
    private TextView songArtistView;
    private ImageButton playPauseImageButton;
    private ImageButton nextImageButton;
    private ImageButton previousImageButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);

        navigationDrawerIconList.add(Constants.ITEM_ALL_SONG);
        navigationDrawerIconList.add(Constants.ITEM_FAVORITE_LIST);
        navigationDrawerIconList.add(Constants.ITEM_SETTING);
        navigationDrawerIconList.add(Constants.ITEM_ABOUT);

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);

        actionBarDrawerToggle.syncState();

        NavigationDrawerAdapter navigationDrawerAdapter =
                new NavigationDrawerAdapter(navigationDrawerIconList, imageForDrawer, this);
        navigationDrawerAdapter.notifyDataSetChanged();

        RecyclerView recyclerView = findViewById(R.id.navigation_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(navigationDrawerAdapter);
        recyclerView.setHasFixedSize(true);

        // Handle tab on main screen
        TabsPagerAdapter tabsPagerAdapter = new TabsPagerAdapter( getSupportFragmentManager());

        tabsPagerAdapter.addFragment(HomeFragment.getInstance(),"Home");
        tabsPagerAdapter.addFragment(new FavoriteFragment(),"Home");


        ViewPager viewPager = findViewById(R.id.viewpager);
        viewPager.setAdapter(tabsPagerAdapter);

        SmartTabLayout viewPagerTab = findViewById(R.id.viewpagertab);
        viewPagerTab.setViewPager(viewPager);

        // Init view control handle
        initViewControls();

        // Init broadcast actions
        initBrocastAction();

        // Handle bottom play main screen.
        bottomPlayMainScreen = findViewById(R.id.hiddenBarMainScreen);
        bottomPlayMainScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent nextActivity = new Intent(getApplicationContext(), PlaySongActivity.class);
                startActivity(nextActivity);
            }
        });
    }

    @Override
    protected void onHandler() {

        mService = getMService();

        onHandlerActionsPlay();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();

        onUpdateUISong();
    }

    @Override
    public void onResume() {
        super.onResume();
        registerReceiver(mReceiver, mIntentFilter);
    }

    @Override
    protected void onPause() {
        unregisterReceiver(mReceiver);
        super.onPause();
    }

    private void initViewControls() {

        songTitleView = this.findViewById(R.id.songTitleMainScreen);
        songArtistView = this.findViewById(R.id.songArtistMainScreen);

        playPauseImageButton = this.findViewById(R.id.playPauseButtonBottom);
        nextImageButton = this.findViewById(R.id.nextButtonBottom);
        previousImageButton = this.findViewById(R.id.previousButtonBottom);
    }

    private void initBrocastAction() {

        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(mBroadcastAction);
        Intent serviceIntent = new Intent(this, MusicService.class);
        startService(serviceIntent);
    }

    private void onUpdateBaseUI() {

        this.songTitleView.setText(mService.getMCurrSong().get_title());
        this.songArtistView.setText(mService.getMCurrSong().get_artist());
    }

    private void onUpdateUISong() {

        if(mService != null && mService.isMusicReady) {

            onUpdateBaseUI();

            if(mService.isPlaying()) {
                playPauseImageButton.setBackgroundResource(R.drawable.pause_bottom);
            } else {
                playPauseImageButton.setBackgroundResource(R.drawable.play_bottom);
            }
        }
    }

    private void onHandlerActionsPlay() {

        nextImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onNext();
            }
        });

        previousImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPrevious();
            }
        });


        playPauseImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mService.onProcess(Constants.PLAYPAUSE, null);
                onUpdateUISong();
            }
        });
    }

    private void onNext() {

        mService.onProcess(Constants.NEXT, null);

        onUpdateBaseUI();

        playPauseImageButton.setBackgroundResource(R.drawable.pause_bottom);
    }

    private void onPrevious() {

        mService.onProcess(Constants.PREVIOUS, null);

        onUpdateBaseUI();

        playPauseImageButton.setBackgroundResource(R.drawable.pause_bottom);
    }


    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals(mBroadcastAction)) {

                String data = intent.getStringExtra(Constants.KEY_DATA);
                handleBroadcastAction(data);
                Intent stopIntent = new Intent(MainActivity.this,
                        MusicService.class);
                stopService(stopIntent);
            }
        }
    };

    private void handleBroadcastAction(String data) {

        switch (data) {
            case Constants.SONG_COMPLETE:
                onUpdateBaseUI();
                playPauseImageButton.setBackgroundResource(R.drawable.play_bottom);
                mService.setNeedPause(true);
            break;
            default:
                System.out.println(data);
                break;
        }
    }
}
