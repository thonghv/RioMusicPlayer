package com.pine.pmedia.activities;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.pine.pmedia.R;
import com.pine.pmedia.adapters.SongPagerAdapter;
import com.pine.pmedia.helpers.CommonHelper;
import com.pine.pmedia.helpers.Constants;
import com.pine.pmedia.services.MusicService;
import com.tbuonomo.viewpagerdotsindicator.SpringDotsIndicator;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class PlaySongActivity extends BaseActivity {

    private MusicService mService;

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
    private ImageView avatarSong;
    private Timer timerOnReadyPlay;
    private boolean isTimerRunning = false;
    private Handler handler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_song);

        getSupportActionBar().hide();

        handler = new Handler();

        initViews();
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
        downImageButton = findViewById(R.id.downPlaySong);

        // For paper viewer
        pager = findViewById(R.id.viewPager);
        SongPagerAdapter adapter = new SongPagerAdapter();
        adapter.setData(createPageList());
        pager.setAdapter(adapter);
        pager.setPageTransformer(true, new ZoomOutPageTransformer());
        SpringDotsIndicator dotsIndicator = findViewById(R.id.spring_dots_indicator);
        dotsIndicator.setViewPager(pager);

        seekBar.getProgressDrawable().setColorFilter(Color.DKGRAY, PorterDuff.Mode.SRC_IN);

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
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        timerOnReadyPlay.cancel();
        isTimerRunning = false;
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
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

        // Cal background color by image song
        onCalBackgroundColorScreen();
    }

    private void onStartTimer(){
        timerOnReadyPlay = new Timer();
        isTimerRunning = true;
        timerOnReadyPlay.schedule(new TimerTask() {
            @Override
            public void run() {
                PlaySongActivity.this.runOnUiThread(new Runnable(){
                    public void run(){
                        if(mService.isMusicReady) {
                            isTimerRunning= false;
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

    private void onCalBackgroundColorScreen() {

        final LinearLayout layoutSongScreen = findViewById(R.id.layoutSongScreen);
        Bitmap bmp =  ImageLoader.getInstance().loadImageSync(mService.getMCurrSong().get_image());
        Drawable drawable = CommonHelper.createBlurredImageFromBitmap(bmp, this, 10);
        layoutSongScreen.setBackground(drawable);

//        ImageLoader.getInstance().loadImage(mService.getMCurrSong().get_image(), new SimpleImageLoadingListener() {
//            @Override
//            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
//                Bitmap bitmapBlur = CommonHelper.blur(getApplicationContext(), loadedImage);
//                BitmapDrawable ob = new BitmapDrawable(getResources(), bitmapBlur);
//                layoutSongScreen.setBackground(ob);
//            }
//        });
    }

    private void onLoadViewPagerSong() {

        LayoutInflater inflater = LayoutInflater.from(this);
        View viewArtSong = inflater.inflate(R.layout.avatar_song_play, null);
        avatarSong = viewArtSong.findViewById(R.id.avatarSong);

        // Display image song
        String artUrl = mService.getMCurrSong().get_image();
        CommonHelper.onDisplayImage(artUrl, avatarSong);

        List<View> pageList = new ArrayList<>();
        pageList.add(viewArtSong);
        pageList.add(createPageView(R.color.av_orange));
        pageList.add(createPageView(R.color.av_green));

        SongPagerAdapter adapter = new SongPagerAdapter();
        adapter.setData(pageList);

        pager.setAdapter(adapter);
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

        onUpdateSeekbar();
    }

    private void onUpdateSeekbar() {

        int finalTime = getMService().getMPlayer().getDuration();

        endTimeText.setText(CommonHelper.toFormatTime(finalTime));
        this.seekBar.setMax(finalTime);

        int startTime = getMService().getMPlayer().getCurrentPosition();
        startTimeText.setText(CommonHelper.toFormatTime(startTime));
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
                mService.onProcess(Constants.PLAYPAUSE, null);
                onUpdateUI();
            }
        });
    }

    private void onNext() {

        mService.onProcess(Constants.NEXT, null);

        onUpdateBaseUI();
    }

    private void onPrevious() {

        mService.onProcess(Constants.PREVIOUS, null);

        onUpdateBaseUI();
    }
}
