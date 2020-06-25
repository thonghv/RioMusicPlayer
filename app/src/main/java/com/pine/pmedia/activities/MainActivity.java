package com.pine.pmedia.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.pine.pmedia.App;
import com.pine.pmedia.R;
import com.pine.pmedia.adapters.NavigationDrawerAdapter;
import com.pine.pmedia.adapters.SongCatRecyclerAdapter;
import com.pine.pmedia.adapters.TabsPagerAdapter;
import com.pine.pmedia.control.MusicVisualizer;
import com.pine.pmedia.fragments.AlbumsFragment;
import com.pine.pmedia.fragments.ArtistFragment;
import com.pine.pmedia.fragments.GenresFragment;
import com.pine.pmedia.fragments.SongsFragment;
import com.pine.pmedia.fragments.SuggestFragment;
import com.pine.pmedia.helpers.CommonHelper;
import com.pine.pmedia.helpers.Constants;
import com.pine.pmedia.helpers.MediaHelper;
import com.pine.pmedia.models.Filter;
import com.pine.pmedia.models.Song;
import com.pine.pmedia.services.MusicService;
import com.pine.pmedia.sqlite.DBManager;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends BaseActivity implements IActivity{

    private IntentFilter mIntentFilter;

    //=====================
    // Variable Common
    //=====================
    private App app;
    private DBManager dbManager;
    private Context mContext;
    public static DrawerLayout drawerLayout = null;
    private ArrayList<String> navigationDrawerIconList = new ArrayList<>();
    private int[] imageForDrawer = new int[]{
            R.drawable.navigation_allsongs, R.drawable.navigation_favorites,
            R.drawable.navigation_settings, R.drawable.navigation_aboutus };

    private MusicService mService;
    private SmartTabLayout smartTabLayout;
    private RelativeLayout bottomPlayMainScreen;
    private ImageView songAvatarBottomPlayControl;
    private TextView songTitleBottomPlayControl;
    private TextView songArtistBottomPlayControl;
    private LinearLayout playPauseControl;
    private LinearLayout queueSongListControl;
    private ImageView imgPlayPauseBottomControl;
    private ImageView imgQueueBottomControl;
    private Toolbar toolbar;
    private MusicVisualizer musicVisualizerControl;
    private BottomSheetDialog queueSongsDialog;
    private RecyclerView recyclerQueueSong;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        app = App.getInstance();
        mContext = this;

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

        smartTabLayout = findViewById(R.id.smartTabLayout);
        smartTabLayout.setViewPager(viewPager);

        // Init handle action
        initActionsForBase();

        // Init view control handle
        initControlsSongPlayBottom();

        // Init broadcast actions
        initBroadcast();

        // Handle bottom play menu screen.
        // initBottomBarPlay();

        // Init db manager
        initDBManager();

//        AppBarLayout appBarLayout = findViewById(R.id.appBarLayout);
//        appBarLayout.setBackgroundResource(R.drawable.bk_03);
    }

    @Override
    protected void onHandler() {

        mService = getMService();
        mService.setmActivity(this);

        initActionsSongPlayBottom();

        // On init playlist total data
        onInitPLayListTotal();

        // On init recent song list
        onInitRecentSong();
    }

    @Override
    public void initControlsSongPlayBottom() {

        bottomPlayMainScreen = this.findViewById(R.id.hiddenBarMainScreen);
        songAvatarBottomPlayControl = this.findViewById(R.id.songAvatarBottomPlay);
        songTitleBottomPlayControl = this.findViewById(R.id.songTitleBottomPlay);
        songArtistBottomPlayControl = this.findViewById(R.id.songArtistBottomPlay);

        playPauseControl = this.findViewById(R.id.playPauseButtonBottom);
        queueSongListControl = this.findViewById(R.id.queueSongList);

        imgPlayPauseBottomControl = this.findViewById(R.id.imgPlayPauseBottom);
        imgQueueBottomControl = this.findViewById(R.id.imgQueueSongBottom);
        musicVisualizerControl = this.findViewById(R.id.queueVisualizer);
    }

    @Override
    public void initActionsSongPlayBottom() {
        bottomPlayMainScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent nextActivity = new Intent(getApplicationContext(), PlaySongActivity.class);
                startActivity(nextActivity);
            }
        });

        playPauseControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mService.onProcess(Constants.PLAY_PAUSE, null);
                if(mService.isPlaying()) {
                    imgPlayPauseBottomControl.setImageResource(R.drawable.pause_bottom);
                    musicVisualizerControl.setVisibility(View.VISIBLE);
                } else {
                    imgPlayPauseBottomControl.setImageResource(R.drawable.play_bottom);
                    musicVisualizerControl.setVisibility(View.GONE);
                }
            }
        });

        queueSongListControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // onShowQueueSong();
                CommonHelper.onShowScreenQueueSong(mContext);
            }
        });
    }

    /**
     * Update UI Song Play Bottom
     */
    @SuppressLint("ResourceType")
    @Override
    public void onUpdateUISongPlayBottom(boolean isAnimation) {

        if(mService != null && mService.isMusicReady) {

            if(isAnimation) {
                bottomPlayMainScreen.startAnimation(AnimationUtils.loadAnimation(this, R.animator.flip_in_left));
            }

            songTitleBottomPlayControl.setText(mService.getMCurrSong().get_title());
            songArtistBottomPlayControl.setText(mService.getMCurrSong().get_artist());

            // Load song avatar
            ImageSize targetSize = new ImageSize(124, 124);
            ImageLoader.getInstance().displayImage(mService.getMCurrSong().get_image(), new ImageViewAware(songAvatarBottomPlayControl),
                    new DisplayImageOptions.Builder()
                            .imageScaleType(ImageScaleType.EXACTLY)
                            .cacheInMemory(true)
                            .resetViewBeforeLoading(true)
                            .bitmapConfig(Bitmap.Config.RGB_565)
                            .build()
                    , targetSize,
                    new SimpleImageLoadingListener() {
                        @Override
                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {

                        }
                        @Override
                        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                            System.out.println("Error ...");
                        }
                    },null);

            if(mService.isPlaying()) {
                imgPlayPauseBottomControl.setImageResource(R.drawable.pause_bottom);
                musicVisualizerControl.setVisibility(View.VISIBLE);
            } else {
                imgPlayPauseBottomControl.setImageResource(R.drawable.play_bottom);
                musicVisualizerControl.setVisibility(View.GONE);
            }
        }
    }

    private void initActionsForBase() {

        smartTabLayout.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(position == Constants.SUGGEST_INDEX) {
                    reloadData();
                }

                if(position == Constants.SONGS_INDEX && App.getInstance().isReloadSongs) {
                    reloadData();
                }

                if(position == Constants.ALBUM_INDEX && App.getInstance().isReloadAlbums) {
                    reloadData();
                }

                if(position == Constants.ARTIST_INDEX && App.getInstance().isReloadArtists) {
                    reloadData();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    private void initBottomBarPlay() {

        Bitmap bitmap = CommonHelper.drawableToBitmap(getResources().getDrawable(R.drawable.bk_01));
        Palette p = Palette.from(bitmap).generate();
        int color1 = p.getMutedColor(ContextCompat.getColor(this, R.color.p_background_01));
        int color2 = CommonHelper.manipulateColor(color1, 0.22f);

        GradientDrawable gd = new GradientDrawable(GradientDrawable.Orientation.TR_BL, new int[] {color1, color2});
        gd.setCornerRadius(0f);
        //bottomPlayMainScreen.setBackgroundDrawable(gd);
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if(intent.getAction().equals(Constants.ACTION_SONG_COMPLETE)) {

            }

            if(intent.getAction().equals(Constants.RELOAD_ADAPTER_PLAYLIST)) {
                SuggestFragment.getInstance().onReloadPlayList();
            }

            Intent stopIntent = new Intent(MainActivity.this, MusicService.class);
            stopService(stopIntent);
        }
    };

    private void onInitPLayListTotal() {

        if(mService.getPlaylistTotal().isEmpty()) {
            new initMediaPlayList(this, dbManager).execute();
        }
    }

    private void onInitQueueSong() {

        ArrayList<Song> playQueue = CommonHelper.getQueueSong(dbManager).getSongs();
        mService.setPlayingQueue(playQueue);
    }

    private void onInitRecentSong() {

        new initRecentPlayList(this, dbManager).execute();
    }

    private void onInitSettingSongStore() {

        String settingSongPlay = dbManager.getSettingByKey(Constants.SETTING_SONG_PLAYING);
        if(settingSongPlay == null) {
            ArrayList<Song> songs = App.getInstance().getMediaPlayList();
            if(!songs.isEmpty()) {
                dbManager.insertSetting(Constants.SETTING_SONG_PLAYING, String.valueOf(songs.get(0).get_id()));
            }
        }

        String settingSongPosition = dbManager.getSettingByKey(Constants.SETTING_SONG_POSITION);
        if(settingSongPosition == null) {
            dbManager.insertSetting(Constants.SETTING_SONG_POSITION, "-1");
        }
    }

    @Override
    public void initBroadcast() {

        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(Constants.RELOAD_ADAPTER_PLAYLIST);
        mIntentFilter.addAction(Constants.ACTION_SONG_COMPLETE);
        Intent serviceIntent = new Intent(this, MusicService.class);
        startService(serviceIntent);
    }

    @Override
    public void initDBManager() {
        dbManager = new DBManager(this);
        dbManager.open();
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

        onUpdateUISongPlayBottom(false);
    }

    @Override
    public void onResume() {

        super.onResume();
        reloadData();
        registerReceiver(mReceiver, mIntentFilter);
    }

    @Override
    protected void onPause() {
        unregisterReceiver(mReceiver);
        super.onPause();
    }

    private TabsPagerAdapter initTab() {

        TabsPagerAdapter tabsPagerAdapter = new TabsPagerAdapter(getSupportFragmentManager());
        tabsPagerAdapter.addFragment(SuggestFragment.getInstance(), this.getResources().getString(R.string.suggest));
        tabsPagerAdapter.addFragment(SongsFragment.getInstance(), this.getResources().getString(R.string.songs));
        tabsPagerAdapter.addFragment(AlbumsFragment.getInstance(), this.getResources().getString(R.string.album));
        tabsPagerAdapter.addFragment(ArtistFragment.getInstance(), this.getResources().getString(R.string.artist));
        tabsPagerAdapter.addFragment(GenresFragment.getInstance(), this.getResources().getString(R.string.genres));

        return tabsPagerAdapter;
    }

    private void updateControlUI() {

        if(mService != null && mService.isMusicReady) {
            if(mService.isPlaying()) {
                imgPlayPauseBottomControl.setImageResource(R.drawable.pause_bottom);
                musicVisualizerControl.setVisibility(View.VISIBLE);
            } else {
                imgPlayPauseBottomControl.setImageResource(R.drawable.play_bottom);
                musicVisualizerControl.setVisibility(View.GONE);
            }
        }
    }

    private void onSongComplete() {

        onUpdateUISongPlayBottom(false);
        imgPlayPauseBottomControl.setImageResource(R.drawable.play_bottom);
        mService.setNeedPause(true);
    }

    private void reloadData() {

        if(app.isReloadPlayList) {
            SuggestFragment.getInstance().onReloadPlayList();
            app.isReloadPlayList = false;
        }

        if (app.isReloadFavorite) {
            SuggestFragment.getInstance().onLoadFavorite();
            app.isReloadFavorite = false;
        }

        if (app.isReloadLastPlayed) {
            SuggestFragment.getInstance().onLoadCountHistory();
            app.isReloadLastPlayed = false;
        }

        if (app.isReloadRecentAdd) {
            SuggestFragment.getInstance().onLoadCountRecentAdd();
            app.isReloadRecentAdd = false;
        }

        if (app.isReloadSongs) {
            SongsFragment.getInstance().onReloadSongList();
            app.isReloadSongs = false;
        }

        if (app.isReloadAlbums) {
            AlbumsFragment.getInstance().onReloadAlbumList();
            app.isReloadAlbums = false;
        }
    }

    /**
     * Init all data track from resource media
     */
    private class initMediaPlayList extends AsyncTask<String, Void, Activity> {

        private Activity activity;
        private DBManager dbManager;

        public initMediaPlayList(Activity activity, DBManager dbManager) {
            this.activity = activity;
            this.dbManager = dbManager;
        }

        @Override
        protected Activity doInBackground(String... strings) {

            ArrayList<Song> mediaSongs = MediaHelper.getSongs(activity, 0L, 0L);
            App.getInstance().setMediaPlayList(mediaSongs);

            ArrayList<Song> queueStore = MediaHelper.getQueue(dbManager, mediaSongs).getSongs();
            App.getInstance().setQueueStore(queueStore);

            return null;
        }

        @Override
        protected void onPostExecute(Activity activity) {

            // On save song playing previous if null
            onInitSettingSongStore();

            // On load previous song
            onLoadPreviousData();

            // On load queue song
            onInitQueueSong();
        }
    }

    /**
     * Init all data track recent added from resource media
     */
    private class initRecentPlayList extends AsyncTask<String, Void, String> {

        private Activity activity;
        private DBManager dbManager;

        public initRecentPlayList(Activity activity, DBManager dbManager) {
            this.activity = activity;
            this.dbManager = dbManager;
        }

        protected String doInBackground(String... params){
            dbManager.deleteAllRecentSong();
            ArrayList<Song> songs = MediaHelper.getLastAddRecent(activity);
            for(Song s : songs) {
                dbManager.insertRecent(s.get_id(), s.get_title());
            }
            return "";
        }

        protected void onPostExecute(String response) {
            App.getInstance().isReloadRecentAdd = true;
            reloadData();
        }
    }


    /**
     * Show bottom sheet queue song
     */
    private SongCatRecyclerAdapter queueSongRecyclerAdapter;
    private ArrayList<Song> queueSong = null;
    private void onShowQueueSong() {

        LayoutInflater li = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = li.inflate(R.layout.bottom_dialog_queue, null);

        ImageButton shuffleNowPlaying = view.findViewById(R.id.shuffleNowPlaying);
        ImageButton addSongNowPlaying = view.findViewById(R.id.addSongNowPlaying);
        ImageButton deleteNowPlaying = view.findViewById(R.id.deleteNowPlaying);

        recyclerQueueSong = view.findViewById(R.id.recycleViewSongsCat);
        recyclerQueueSong.setItemAnimator(new DefaultItemAnimator());
        recyclerQueueSong.setLayoutManager(new LinearLayoutManager(this));

        shuffleNowPlaying.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shuffleQueueSong();
            }
        });

        addSongNowPlaying.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        deleteNowPlaying.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        Filter filter = MediaHelper.getQueue(dbManager, App.getInstance().getMediaPlayList());
        queueSong = filter.getSongs();

        TextView headerSheetDialogControl = view.findViewById(R.id.headerSheetDialog);
        headerSheetDialogControl.setText(getResources().getString(R.string.nowPlaying)
                + Constants.SPACE + "(" + queueSong.size() + ")");

        queueSongRecyclerAdapter = new SongCatRecyclerAdapter(this, queueSong, Constants.VIEW_QUEUE);
        recyclerQueueSong.setAdapter(queueSongRecyclerAdapter);

        queueSongsDialog = new BottomSheetDialog(this);
        queueSongsDialog.setContentView(view);

        try {
            Field behaviorField = queueSongsDialog.getClass().getDeclaredField("behavior");
            behaviorField.setAccessible(true);
            final BottomSheetBehavior behavior = (BottomSheetBehavior) behaviorField.get(queueSongsDialog);
            behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {

                @Override
                public void onStateChanged(@NonNull View bottomSheet, int newState) {
                    if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    }
                }

                @Override
                public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                }
            });
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        queueSongsDialog.show();
    }

    private void shuffleQueueSong() {

        Collections.shuffle(queueSong);
        queueSongRecyclerAdapter.updateData(queueSong);
        queueSongRecyclerAdapter.notifyDataSetChanged();
    }

    public static class MyBottomSheetFragment extends BottomSheetDialogFragment {

        @Override
        public void setupDialog(Dialog dialog, int style) {


            BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) dialog;
            bottomSheetDialog.setContentView(R.layout.bottom_dialog_queue);

            try {
                Field behaviorField = bottomSheetDialog.getClass().getDeclaredField("behavior");
                behaviorField.setAccessible(true);
                final BottomSheetBehavior behavior = (BottomSheetBehavior) behaviorField.get(bottomSheetDialog);
                behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {

                    @Override
                    public void onStateChanged(@NonNull View bottomSheet, int newState) {
                        if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                        }
                    }

                    @Override
                    public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                    }
                });
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    private void onLoadPreviousData(){

        Song songFind  = MediaHelper.findPreviousSong(dbManager, App.getInstance().getMediaPlayList());
        if(songFind != null) {

            int storePosition = MediaHelper.findPreviousPosition(dbManager);
            if(storePosition != -1) {
                mService.setCurrentPosition(storePosition);
            }

            songTitleBottomPlayControl.setText(songFind.get_title());
            songArtistBottomPlayControl.setText(songFind.get_artist());

            // Load song avatar
            ImageSize targetSize = new ImageSize(124, 124);
            ImageLoader.getInstance().displayImage(songFind.get_image(), new ImageViewAware(songAvatarBottomPlayControl),
                    new DisplayImageOptions.Builder()
                            .imageScaleType(ImageScaleType.EXACTLY)
                            .cacheInMemory(true)
                            .resetViewBeforeLoading(true)
                            .bitmapConfig(Bitmap.Config.RGB_565)
                            .build()
                    , targetSize,
                    new SimpleImageLoadingListener() {
                        @Override
                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {

                        }
                        @Override
                        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                            System.out.println("Error ...");
                        }
                    },null);

            imgPlayPauseBottomControl.setImageResource(R.drawable.play_bottom);
            musicVisualizerControl.setVisibility(View.GONE);
        }
    }
}
