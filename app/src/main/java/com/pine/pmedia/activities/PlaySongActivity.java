package com.pine.pmedia.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.pine.pmedia.App;
import com.pine.pmedia.R;
import com.pine.pmedia.adapters.SongPagerAdapter;
import com.pine.pmedia.extensions.ZoomOutPageTransformer;
import com.pine.pmedia.helpers.CommonHelper;
import com.pine.pmedia.helpers.Constants;
import com.pine.pmedia.models.Song;
import com.pine.pmedia.services.MusicService;
import com.pine.pmedia.sqlite.DBManager;
import com.tbuonomo.viewpagerdotsindicator.SpringDotsIndicator;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class PlaySongActivity extends BaseActivity implements IActivity{

    private IntentFilter mIntentFilter;
    private MusicService mService;

    private TextView indexSongControl;
    private ImageButton favoriteControl;
    private ImageButton equalizerControl;
    private ImageButton addControl;
    private ImageButton queueControl;

    private ViewPager pager;
    private TextView songTitleView;
    private TextView songArtistView;
    private SeekBar seekBar;
    private TextView startTimeText;
    private TextView endTimeText;
    private ImageView playPauseImageButton;
    private ImageButton previousImageButton;
    private ImageButton nextImageButton;
    private ImageButton loopImageButton;
    private ImageButton shuffleImageButton;
    private LinearLayout downImageButton;
    private ImageButton imgDownPlaySong;
    private ImageView avatarSong;
    private Timer timerOnReadyPlay;
    private Handler handler;
    private DBManager dbManager;
    final Context context = this;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_song);

        getSupportActionBar().hide();

        handler = new Handler();

        // Init db manager
        initDBManager();

        initViews();

        // Init broadcast actions
        initBroadcast();
    }

    private void initViews() {

        // For control base
        songTitleView = findViewById(R.id.songTitle);
        songArtistView = findViewById(R.id.songArtist);
        seekBar = findViewById(R.id.seekBar);
        startTimeText = findViewById(R.id.startTime);
        endTimeText = findViewById(R.id.endTime);
        playPauseImageButton = findViewById(R.id.playPauseButton);
        previousImageButton = findViewById(R.id.previousButton);
        nextImageButton = findViewById(R.id.nextButton);
        loopImageButton = findViewById(R.id.loopButton);
        shuffleImageButton = findViewById(R.id.shuffleButton);
        downImageButton = findViewById(R.id.layoutDownPlaySong);
        imgDownPlaySong = findViewById(R.id.imgDownPlaySong);

        indexSongControl = findViewById(R.id.indexSongControl);
        favoriteControl = findViewById(R.id.favoriteControl);
        addControl = findViewById(R.id.addControl);
        queueControl = findViewById(R.id.queueControl);
        equalizerControl = findViewById(R.id.equalizerControl);

        // For paper viewer
        pager = findViewById(R.id.viewPager);
        SongPagerAdapter adapter = new SongPagerAdapter();
        adapter.setData(createPageList());
        pager.setAdapter(adapter);
        pager.setPageTransformer(true, new ZoomOutPageTransformer());
        SpringDotsIndicator dotsIndicator = findViewById(R.id.spring_dots_indicator);
        dotsIndicator.setViewPager(pager);

        // Handle for button dropdown
        onHandlerDownPlayScreen();
    }

    private void onHandlerDownPlayScreen() {

        downImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        imgDownPlaySong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        timerOnReadyPlay.cancel();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mReceiver, mIntentFilter);
    }

    @Override
    protected void onPause() {
        unregisterReceiver(mReceiver);
        super.onPause();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    protected void onHandler() {

        mService = getMService();

        // Start timer duration of song play
        onStartTimer();

        // Update title and artist of song
        onUpdateBaseUI();

        // Handle action
        onHandlerActionsPlay();

        // Handle view paper of song screen
        onLoadViewPagerSong();

        // Update image and background color
        onUpdateArtworkUI();

        // Init cal view for user
        onInitCalView();

        // Insert history last play list
        onAddHistory();
    }

    private void onStartTimer(){
        timerOnReadyPlay = new Timer();
        timerOnReadyPlay.schedule(new TimerTask() {
            @Override
            public void run() {
                PlaySongActivity.this.runOnUiThread(new Runnable(){
                    public void run(){
                        if(mService.isMusicReady) {
                            onUpdateUI();
                        }
                    }});
            }
        }, 100, 1000);
    }

    @NonNull
    private View createPageView(int color) {

        View view = new View(this);
        view.setBackgroundColor(getResources().getColor(color));

        return view;
    }

    @NonNull
    private List<View> createPageList() {

        List<View> pageList = new ArrayList<>();

        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.avatar_song_play, null);
        pageList.add(view);

        pageList.add(createPageView(R.color.av_orange));
        pageList.add(createPageView(R.color.av_green));

        return pageList;
    }

    @Override
    public void initBroadcast() {

        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(Constants.ACTION_SONG_COMPLETE);
        Intent serviceIntent = new Intent(this, MusicService.class);
        startService(serviceIntent);
    }

    @Override
    public void initDBManager() {

        dbManager = new DBManager(this);
        dbManager.open();
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals(Constants.ACTION_SONG_COMPLETE)) {
                onUpdateArtworkUI();
            }

            Intent stopIntent = new Intent(PlaySongActivity.this, MusicService.class);
            stopService(stopIntent);
        }
    };

    private void onUpdateArtworkUI() {

        // Update image art for song
        CommonHelper.onDisplayImage(mService.getMCurrSong().get_image(), avatarSong);

        // Update background color for song screen
        final LinearLayout layoutSongScreen = findViewById(R.id.layoutSongScreen);
        Bitmap bmp =  ImageLoader.getInstance().loadImageSync(mService.getMCurrSong().get_image());
        Drawable drawable = CommonHelper.createBlurredImageFromBitmap(bmp, this, 10);
        layoutSongScreen.setBackground(drawable);
    }

    private void onLoadViewPagerSong() {

        LayoutInflater inflater = LayoutInflater.from(this);
        View viewArtSong = inflater.inflate(R.layout.avatar_song_play, null);
        avatarSong = viewArtSong.findViewById(R.id.avatarSong);

        List<View> pageList = new ArrayList<>();
        pageList.add(viewArtSong);
        pageList.add(createPageView(R.color.av_orange));
        pageList.add(createPageView(R.color.av_green));

        SongPagerAdapter adapter = new SongPagerAdapter();
        adapter.setData(pageList);

        pager.setAdapter(adapter);
    }


    private void onInitCalView() {
        if(dbManager.isExitsFavorite(mService.getMCurrSong().get_id())) {
            favoriteControl.setBackgroundResource(R.drawable.favorite_on);
        } else {
            favoriteControl.setBackgroundResource(R.drawable.favorite_off);
        }
    }

    private void onUpdateBaseUI() {

        this.songTitleView.setText(mService.getMCurrSong().get_title());
        this.songArtistView.setText(mService.getMCurrSong().get_artist());
    }

    private void onUpdateUI() {

        if(mService.isPlaying()) {
            playPauseImageButton.setBackgroundResource(R.drawable.pause_icon);
        } else {
            playPauseImageButton.setBackgroundResource(R.drawable.play_icon);
        }

        if(mService.isShuffle()) {
            shuffleImageButton.setBackgroundResource(R.drawable.shuffle_icon);
        } else {
            shuffleImageButton.setBackgroundResource(R.drawable.shuffle_white_icon);
        }

        if(mService.isLoop()) {
            loopImageButton.setBackgroundResource(R.drawable.loop_icon);
        } else {
            loopImageButton.setBackgroundResource(R.drawable.loop_white_icon);
        }

        onUpdateBaseUI();

        onUpdateSeekBar();
    }

    private void onUpdateSeekBar() {

        int finalTime = getMService().getMPlayer().getDuration();

        endTimeText.setText(CommonHelper.toFormatTimeMS(finalTime));
        this.seekBar.setMax(finalTime);

        int startTime = getMService().getMPlayer().getCurrentPosition();
        startTimeText.setText(CommonHelper.toFormatTimeMS(startTime));
        seekBar.setProgress(startTime);
    }

    private void onHandlerActionsPlay() {

        shuffleImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mService.onProcess(Constants.SHUFFLE, null);
            }
        });

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

        loopImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mService.onProcess(Constants.LOOP, null);
            }
        });

        playPauseImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mService.onProcess(Constants.PLAY_PAUSE, null);
                onUpdateUI();
            }
        });

        favoriteControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onFavorite();
                onInitCalView();
            }
        });

        addControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonHelper.onShowMediaPlayListDialog(context, mService.getMCurrSong().get_id());
            }
        });

        equalizerControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        queueControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void onNext() {

        mService.onProcess(Constants.NEXT, null);

        onUpdateBaseUI();

        onUpdateArtworkUI();

        onInitCalView();

        // Insert history last play list
        onAddHistory();
    }

    private void onPrevious() {

        mService.onProcess(Constants.PREVIOUS, null);

        onUpdateBaseUI();

        onUpdateArtworkUI();

        onInitCalView();

        // Insert history last play list
        onAddHistory();
    }

    private void onFavorite() {

        Song currentSong = mService.getMCurrSong();
        if(dbManager.isExitsFavorite(currentSong.get_id())) {
            Toast.makeText(this, R.string.songIsExitsFavorite, Toast.LENGTH_SHORT).show();
            return;
        }

        dbManager.insertFavorite(currentSong.get_id(), currentSong.get_title());
        Toast.makeText(this, R.string.addSongFavoriteSuccess, Toast.LENGTH_SHORT).show();
        App.getInstance().isReloadFavorite = true;
    }

    /*private void onShowMediaPlayListDialog() {

        FragmentManager fm = this.getSupportFragmentManager();
        MediaPlayListDialog mediaPlayListDialog =
                new MediaPlayListDialog(this, mService.getMCurrSong().get_id());
        mediaPlayListDialog.show(fm, Constants.PLAY_LIST_DIALOG_NAME);
    }*/

    private void onAddHistory() {

        new RunBackgroundAddHistory().execute();
    }

    public class RunBackgroundAddHistory extends AsyncTask<String, Void, String> {

        protected String doInBackground(String... params){
            dbManager.insertHistory(mService.getMCurrSong().get_id(),
                    mService.getMCurrSong().get_title());
            App.getInstance().isReloadLastPlayed = true;
            return "";
        }

        protected void onPostExecute(String response) {
        }
    }

    public void sendBroadcast(String action, String data) {

        Intent broadcastIntent = new Intent();
        broadcastIntent.putExtra(Constants.KEY_DATA, data);
        broadcastIntent.setAction(action);

        sendBroadcast(broadcastIntent);
    }
}
