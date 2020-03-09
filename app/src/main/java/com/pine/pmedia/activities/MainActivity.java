package com.pine.pmedia.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.pine.pmedia.R;
import com.pine.pmedia.adapters.NavigationDrawerAdapter;
import com.pine.pmedia.adapters.TabsPagerAdapter;
import com.pine.pmedia.fragments.AlbumsFragment;
import com.pine.pmedia.fragments.ArtistFragment;
import com.pine.pmedia.fragments.GenresFragment;
import com.pine.pmedia.fragments.SongsFragment;
import com.pine.pmedia.helpers.CommonHelper;
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
    private Toolbar toolbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
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

        // Handle tab on menu screen
        ViewPager viewPager = findViewById(R.id.viewpager);
        TabsPagerAdapter tabsPagerAdapter = initTab();
        viewPager.setAdapter(tabsPagerAdapter);
        viewPager.setOffscreenPageLimit(viewPager.getAdapter().getCount());

        SmartTabLayout smartTabLayout = findViewById(R.id.smartTabLayout);
        smartTabLayout.setViewPager(viewPager);

        // Init view control handle
        initViewControls();

        // Init broadcast actions
        initBroadcastAction();

        // Handle bottom play menu screen.
        initBottomBarPlay();

//        AppBarLayout appBarLayout = findViewById(R.id.appBarLayout);
//        appBarLayout.setBackgroundResource(R.drawable.bk_03);
    }

    private void initBottomBarPlay() {
        bottomPlayMainScreen = findViewById(R.id.hiddenBarMainScreen);
        bottomPlayMainScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent nextActivity = new Intent(getApplicationContext(), PlaySongActivity.class);
                startActivity(nextActivity);
            }
        });

        Bitmap bitmap = CommonHelper.drawableToBitmap(getResources().getDrawable(R.drawable.bk_01));
        Palette p = Palette.from(bitmap).generate();
        int color1 = p.getMutedColor(ContextCompat.getColor(this, R.color.p_background_01));
        int color2 = CommonHelper.manipulateColor(color1, 0.22f);

        GradientDrawable gd = new GradientDrawable(GradientDrawable.Orientation.TR_BL, new int[] {color1, color2});
        gd.setCornerRadius(0f);
        bottomPlayMainScreen.setBackgroundDrawable(gd);
    }

    @Override
    protected void onHandler() {

        mService = getMService();

        onHandlerActionsPlay();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the search; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        getMenuInflater().inflate(R.menu.search, menu);

        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_search) {
            Toast.makeText(this, "Android Menu is Clicked", Toast.LENGTH_LONG).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
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

    private TabsPagerAdapter initTab() {

        TabsPagerAdapter tabsPagerAdapter = new TabsPagerAdapter(getSupportFragmentManager());
        tabsPagerAdapter.addFragment(SongsFragment.getInstance(), this.getResources().getString(R.string.songs));
        tabsPagerAdapter.addFragment(AlbumsFragment.getInstance(), this.getResources().getString(R.string.album));
        tabsPagerAdapter.addFragment(ArtistFragment.getInstance(), this.getResources().getString(R.string.artist));
        tabsPagerAdapter.addFragment(GenresFragment.getInstance(), this.getResources().getString(R.string.genres));

        return tabsPagerAdapter;
    }

    private void initBroadcastAction() {

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

    private void onSongComplete() {
        onUpdateBaseUI();
        playPauseImageButton.setBackgroundResource(R.drawable.play_bottom);
        mService.setNeedPause(true);
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
                onSongComplete();
                break;
            case Constants.SHOW_CONTENT_BOTTOM:
                RelativeLayout contentBottomLayout1 = findViewById(R.id.hiddenBarMainScreen);
                contentBottomLayout1.animate()
                        .translationY(-contentBottomLayout1.getHeight());
                break;
            case Constants.HIDE_CONTENT_BOTTOM:
                RelativeLayout contentBottomLayout = findViewById(R.id.hiddenBarMainScreen);
                contentBottomLayout.animate()
                        .translationY(0)
                        .setInterpolator(new DecelerateInterpolator(2));
                break;
            default:
                System.out.println(data);
                break;
        }
    }
}
