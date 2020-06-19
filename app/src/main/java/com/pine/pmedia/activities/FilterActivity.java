package com.pine.pmedia.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.pine.pmedia.App;
import com.pine.pmedia.R;
import com.pine.pmedia.adapters.SongCatRecyclerAdapter;
import com.pine.pmedia.control.MusicVisualizer;
import com.pine.pmedia.helpers.CommonHelper;
import com.pine.pmedia.helpers.Constants;
import com.pine.pmedia.helpers.MediaHelper;
import com.pine.pmedia.models.Filter;
import com.pine.pmedia.models.Song;
import com.pine.pmedia.services.MusicService;
import com.pine.pmedia.sqlite.DBManager;

import java.util.ArrayList;

public class FilterActivity extends BaseActivity implements IActivity {

    private App app;
    private MusicService mService;
    private Context mContext;
    private ArrayList<Song> songs;
    private int totalDuration;
    private DBManager dbManager;
    private RecyclerView recyclerSongsView;
    private TextView titleCatControl;
    private TextView noteCatControl;
    private long playListId;
    private int catType;

    //==============================
    // For Play Song Bottom
    private RelativeLayout bottomPlayMainScreen;
    private ImageView songAvatarBottomPlayControl;
    private TextView songTitleBottomPlayControl;
    private TextView songArtistBottomPlayControl;
    private LinearLayout playPauseControl;
    private LinearLayout queueSongListControl;
    private ImageView imgPlayPauseBottomControl;
    private MusicVisualizer musicVisualizerControl;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.app = App.getInstance();
        this.mContext = this;

        setContentView(R.layout.activity_filter);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Init db manager
        initDBManager();

        // Init control layout
        initControl();
        initControlsSongPlayBottom();

        // On load data
        onLoadDataBundle();

        // Load songs by album
        onLoadSongs();
    }

    @Override
    protected void onHandler() {

        mService = super.getMService();

        // Init handle actions song play bottom controls
        this.initActionsSongPlayBottom();
    }

    @Override
    public void initDBManager() {

        dbManager = new DBManager(this);
        dbManager.open();
    }

    /**
     * Init control basic
     */
    private void initControl() {

        recyclerSongsView = findViewById(R.id.recycleViewSongsCat);
        titleCatControl = findViewById(R.id.titleCatControl);
        noteCatControl = findViewById(R.id.noteCatControl);
    }

    /**
     * Init control for play song bottom
     */
    @Override
    public void initControlsSongPlayBottom() {

        bottomPlayMainScreen = findViewById(R.id.hiddenBarMainScreen);
        songAvatarBottomPlayControl = this.findViewById(R.id.songAvatarBottomPlay);
        songTitleBottomPlayControl = this.findViewById(R.id.songTitleBottomPlay);
        songArtistBottomPlayControl = this.findViewById(R.id.songArtistBottomPlay);

        playPauseControl = this.findViewById(R.id.playPauseButtonBottom);
        queueSongListControl = this.findViewById(R.id.queueSongList);

        imgPlayPauseBottomControl = this.findViewById(R.id.imgPlayPauseBottom);
        musicVisualizerControl = this.findViewById(R.id.queueVisualizer);
    }

    /**
     * Init handle actions
     */
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
                if(mService.isMusicReady) {
                    if(mService.isPlaying()) {
                        imgPlayPauseBottomControl.setImageResource(R.drawable.pause_bottom);
                        musicVisualizerControl.setVisibility(View.VISIBLE);
                    } else {
                        imgPlayPauseBottomControl.setImageResource(R.drawable.play_bottom);
                        musicVisualizerControl.setVisibility(View.GONE);
                    }
                }
            }
        });
        queueSongListControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonHelper.onShowScreenQueueSong(mContext);
            }
        });
    }

    /**
     * Update UI Play Song Bottom
     */
    @SuppressLint("ResourceType")
    @Override
    public void onUpdateUISongPlayBottom() {

        if(mService != null && mService.isMusicReady) {

            bottomPlayMainScreen.startAnimation(AnimationUtils.loadAnimation(this, R.animator.flip_in_left));

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

    private void onLoadDataBundle() {

        synchronized(this) {
            setTitle(null);

            Bundle data = getIntent().getExtras();

            this.catType = data.getInt(Constants.KEY_CAT_TYPE);

            String titleCat = data.getString(Constants.KEY_TITLE_CAT);
            String noteCat = data.getString(Constants.KEY_NOTE_CAT);
            playListId = data.getLong(Constants.KEY_ID);

            titleCatControl.setText(titleCat);
            noteCatControl.setText(noteCat);
        }
    }

    public void onUpdateNoteFilter(long number, int totalDuration) {

        String note = number + Constants.SPACE + Constants.SONGS
                + Constants.MINUS + CommonHelper.toFormatTimeMS(totalDuration);
        noteCatControl.setText(note);
    }

    private void onLoadSongs() {

        recyclerSongsView.setItemAnimator(new DefaultItemAnimator());
        recyclerSongsView.setLayoutManager(new LinearLayoutManager(this));

        Filter filter = getFilter();
        ArrayList<?> songs = filter != null ? filter.getSongs() : new ArrayList<>();

        this.songs = (ArrayList<Song>) songs;
        this.totalDuration = filter.getTotalDuration();
        this.onUpdateNoteFilter(this.songs.size(), this.totalDuration);

        SongCatRecyclerAdapter songCatRecyclerAdapter =
                new SongCatRecyclerAdapter(this, songs, catType);
        recyclerSongsView.setAdapter(songCatRecyclerAdapter);
    }

    private Filter getFilter() {

        switch (catType) {
            case Constants.VIEW_SUGGEST:
                return MediaHelper.getSongsByPlayListId(this, playListId);
            case Constants.VIEW_FAVORITE:
                return MediaHelper.getFavorites(dbManager, App.getInstance().getMediaPlayList());
            case Constants.VIEW_LAST_PLAYED:
                return  MediaHelper.getHistories(dbManager, App.getInstance().getMediaPlayList());
            case Constants.VIEW_RECENT_ADDED:
                return MediaHelper.getRecent(dbManager, App.getInstance().getMediaPlayList());
        }

        return null;
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
    public void initBroadcast() {

    }
}
