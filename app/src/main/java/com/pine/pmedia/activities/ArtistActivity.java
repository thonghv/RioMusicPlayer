package com.pine.pmedia.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.pine.pmedia.App;
import com.pine.pmedia.R;
import com.pine.pmedia.adapters.AlbumRecyclerAdapter;
import com.pine.pmedia.adapters.SongCatRecyclerAdapter;
import com.pine.pmedia.control.MusicVisualizer;
import com.pine.pmedia.helpers.CommonHelper;
import com.pine.pmedia.helpers.Constants;
import com.pine.pmedia.helpers.MediaHelper;
import com.pine.pmedia.models.Album;
import com.pine.pmedia.models.Song;
import com.pine.pmedia.services.MusicService;
import com.pine.pmedia.sqlite.DBManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collection;

public class ArtistActivity extends BaseActivity implements IActivity{

    private App app;
    private MusicService mService;
    private DBManager dbManager;
    private Context mContext;
    private RecyclerView recyclerSongsView;
    private RecyclerView recyclerAlbumsView;
    private ImageView coverImage;
    private TextView nameControl;
    private TextView numberOfAlbumsControl;
    private TextView numberOfTracksControl;

    private ArrayList<Album> albums;
    private int artistId;

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
        setContentView(R.layout.activity_artist);
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

        // Load albums
        onLoadAlbums();
    }

    @Override
    protected void onHandler() {
        mService = super.getMService();

        // Init handle actions song play bottom controls
        this.initActionsSongPlayBottom();

        this.onUpdateUISongPlayBottom(false);
    }

    @Override
    public void initBroadcast() {

    }

    @Override
    public void initDBManager() {

        dbManager = new DBManager(this);
        dbManager.open();
    }

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
                CommonHelper.onShowScreenQueueSong(mContext);
            }
        });
    }

    /**
     * Update UI Play Song Bottom
     */
    @SuppressLint("ResourceType")
    @Override
    public void onUpdateUISongPlayBottom(boolean isAnimation) {

        CommonHelper.onUpdateBottomPlayUI(this, mService, dbManager,
                this.findViewById(R.id.contentBottomLayout), isAnimation);
    }

    private void initControl() {

        recyclerSongsView = findViewById(R.id.recycleViewSongsCat);
        recyclerAlbumsView = findViewById(R.id.albumRecycleArtist);

        coverImage = findViewById(R.id.artistCoverImage);
        nameControl = findViewById(R.id.artistName);
        numberOfAlbumsControl = findViewById(R.id.numberOfAlbums);
        numberOfTracksControl = findViewById(R.id.numberOfTracks);
    }

    private void onLoadDataBundle() {

        Bundle data = getIntent().getExtras();

        String albumName = data.getString(Constants.KEY_NAME);
        String artUrl = data.getString(Constants.KEY_ARTWORK);

        int numberOfSong = data.getInt(Constants.KEY_NUMBER_OF_TRACK);
        int numberOfAlbum = data.getInt(Constants.KEY_NUMBER_OF_ALBUM);

        albums = data.getParcelableArrayList(Constants.KEY_SONG_LIST);

        artistId = data.getInt(Constants.KEY_ID);

        setTitle(null);
        nameControl.setText(albumName);
        numberOfAlbumsControl.setText(numberOfAlbum + Constants.SPACE + Constants.ALBUMS);
        numberOfTracksControl.setText(numberOfSong + Constants.SPACE + Constants.SONGS);

        ImageLoader.getInstance().displayImage(artUrl, new ImageViewAware(coverImage),
                new DisplayImageOptions.Builder()
                        .imageScaleType(ImageScaleType.EXACTLY)
                        .cacheInMemory(true)
                        .resetViewBeforeLoading(true)
                        .bitmapConfig(Bitmap.Config.RGB_565)
                        .build()
                , new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {

                    }
                    @Override
                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                        System.out.println("Error ...");
                    }
                },null);
    }

    private void onLoadSongs() {

        recyclerSongsView.setItemAnimator(new DefaultItemAnimator());
        recyclerSongsView.setLayoutManager(new LinearLayoutManager(this));

        ArrayList<Song> songs = MediaHelper.getSongs(this, 0L, artistId);
        SongCatRecyclerAdapter songCatRecyclerAdapter = new SongCatRecyclerAdapter(this, songs, Constants.VIEW_ARTIST);
        recyclerSongsView.setAdapter(songCatRecyclerAdapter);
    }

    private void onLoadAlbums() {

        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerAlbumsView.setLayoutManager(layoutManager);

        AlbumRecyclerAdapter adapter = new AlbumRecyclerAdapter(this, albums, Constants.VIEW_ARTIST);
        recyclerAlbumsView.setAdapter(adapter);

//        GridLayoutManager manager = new GridLayoutManager(this, 2, GridLayoutManager.HORIZONTAL, false);
//        recyclerAlbumsView.setLayoutManager(manager);
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
}
