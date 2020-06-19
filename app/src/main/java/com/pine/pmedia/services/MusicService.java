package com.pine.pmedia.services;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.v4.media.session.MediaSessionCompat;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.pine.pmedia.App;
import com.pine.pmedia.R;
import com.pine.pmedia.activities.PlaySongActivity;
import com.pine.pmedia.helpers.Constants;
import com.pine.pmedia.helpers.MediaHelper;
import com.pine.pmedia.models.Song;

import java.util.ArrayList;
import java.util.Random;

public class MusicService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener, AudioManager.OnAudioFocusChangeListener {

    private App app;
    private final String NOTIFICATION_CHANNEL = "music_player_channel";
    private MediaPlayer mPlayer;
    private Song mCurrSong;
    private Activity mActivity;
    private int mPosition = 0;
    private ArrayList<Song> playlistTotal = new ArrayList<>();
    private ArrayList<Song> playingQueue = new ArrayList<>();
    public AudioManager audioManager;
    private MediaSessionCompat mMediaSession;
    private Bitmap mCurrSongCover;
    private boolean isPlaying;
    private boolean isLoop;
    private boolean isShuffle;
    public boolean isMusicReady = false;
    public boolean isNeedPause;

    private boolean mWasPlayingAtFocusLost = false;
    private int mPrevAudioFocusState = 0;

    //=======================
    // GET OR SET
    //=======================
    public MediaPlayer getMPlayer() {
        return mPlayer;
    }

    public Activity getmActivity() {
        return mActivity;
    }

    public Song getMCurrSong() {
        return mCurrSong;
    }

    public void setmActivity(Activity mActivity) {
        this.mActivity = mActivity;
    }

    public ArrayList<Song> getPlayingQueue() {
        return playingQueue;
    }

    public void setPlayingQueue(ArrayList<Song> playingQueue) {
        this.playingQueue = playingQueue;
    }

    public ArrayList<Song> getPlaylistTotal() {
        return playlistTotal;
    }

    public void setPlaylistTotal(ArrayList<Song> playlistTotal) {
        this.playlistTotal = playlistTotal;
    }

    public boolean isLoop() {
        return this.isLoop;
    }

    public boolean isShuffle() {
        return this.isShuffle;
    }

    public boolean isPlaying() {
        return this.isPlaying;
    }

    public class MusicBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }

    public boolean isEndSong() {
        return mPlayer.getCurrentPosition() >= mPlayer.getDuration() - 1000;
    }

    public void setNeedPause(boolean value) {
        this.isNeedPause = value;
    }

    public int getCurrentPosition() {
        return mPosition;
    }

    //=======================
    // FUNCTION
    //=======================
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new MusicBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        app = App.getInstance();

        mMediaSession = new MediaSessionCompat(this, "MusicService");
        audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            createChannelId();
            Notification notification = new NotificationCompat.Builder(this,CHANNEL_ID)
                    .setContentTitle("Welcome back my friend.")
//                    .setSmallIcon(R.drawable.ic_stat_name)
//                    .setAutoCancel(true)
                    .setContentText("A great way to begin relieving your stress").build();
            startForeground(1, notification);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent != null) {
            if (intent.getAction() != null) {
                onProcess(intent.getAction(), null);
            }
        }

        initNotification();

        return START_NOT_STICKY;
    }


    public void onProcess(String action, Bundle bundle) {

        this.isNeedPause = false;
        switch (action) {
            case Constants.PLAY_PAUSE:
                handlePlayPause(bundle);
                break;
            case Constants.PAUSE:
                handlePause(bundle);
                break;
            case Constants.SHUFFLE:
                handleShuffle();
                break;
            case Constants.LOOP:
                handleLoop();
                break;
            case Constants.STOP:
                break;
            case Constants.NEXT:
                handleNext();
                break;
            case Constants.PREVIOUS:
                handlePrevious();
                break;
            case Constants.QUIT:
                handleQuit();
                break;
        }

        initNotification();
    }

    public void checkAndResetPlay() {
        if(mCurrSong != null) {
            mPlayer.reset();
            mPlayer = null;
        }
    }

    private void initNotification() {
        if(mCurrSong != null) {
            mCurrSongCover = ImageLoader.getInstance().loadImageSync(mCurrSong.get_image());
            setupNotification();
        } else {
            setupFakeNotification();
        }
    }
    private void setupFakeNotification() {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_headset_small)
                .setContentTitle("My notification")
                .setContentText("Have nice day")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("Much longer text that cannot fit one line..."))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);


        startForeground(1, builder.build());
    }

    private PendingIntent buildPendingIntent(Context context, final String action, final ComponentName serviceName) {
        Intent intent = new Intent(action);
        intent.setComponent(serviceName);
        return PendingIntent.getService(context, 0, intent, 0);
    }

    private void setupNotification() {

        String title = mCurrSong.get_title();
        String artist = mCurrSong.get_artist();
        int playPauseIcon = isPlaying() ? R.drawable.ic_pause_vector : R.drawable.ic_play_vector;

        Intent action = new Intent(this, PlaySongActivity.class);
        action.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        final PendingIntent clickIntent = PendingIntent.getActivity(this, 0, action, 0);
        final PendingIntent deleteIntent = buildPendingIntent(this, Constants.QUIT, null);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_headset_small)
                .setContentTitle(title)
                .setContentText(artist)
                .setContentIntent(clickIntent)
                .setDeleteIntent(deleteIntent)
                .setLargeIcon(mCurrSongCover)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setShowActionsInCompactView(0, 1, 2)
                        .setMediaSession(mMediaSession.getSessionToken()))
                .addAction(R.drawable.ic_previous_vector, getString(R.string.previous), getIntent(Constants.PREVIOUS))
                .addAction(playPauseIcon, getString(R.string.playPause), getIntent(Constants.PLAY_PAUSE))
                .addAction(R.drawable.ic_next_vector, getString(R.string.next), getIntent(Constants.NEXT));

        startForeground(1, builder.build());
    }

    private void loadBitmapFromFile(final String path) {

        final ImageLoader imageLoader = ImageLoader.getInstance();
        ImageSize targetSize = new ImageSize(80, 50); // result Bitmap will be fit to this size
        imageLoader.loadImage(path, targetSize, DisplayImageOptions.createSimple(), new SimpleImageLoadingListener() {
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                mCurrSongCover = loadedImage;
                setupNotification();
            }
        });
    }

    private PendingIntent getIntent(String action) {
        Intent intent = new Intent(this, MusicService.class);
        intent.setAction(action);
        return PendingIntent.getService(this, 0, intent, 0);
    }

    public boolean getIsPlaying() {

        if(mPlayer  == null) {
            return false;
        }

        return mPlayer.isPlaying();
    }

    public static final String CHANNEL_ID = "ForegroundServiceChannel";

    @RequiresApi(Build.VERSION_CODES.O)
    private void createChannelId(){
        String channelName = "Music Player Background Service";
        NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID,
                channelName, NotificationManager.IMPORTANCE_LOW);
        notificationChannel.enableLights(true);
        notificationChannel.setLightColor(Color.RED);
        notificationChannel.setShowBadge(true);
        notificationChannel.enableVibration(false);
        notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        assert manager != null;
        manager.createNotificationChannel(notificationChannel);
    }
//    private void createNotificationChannel() {
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            NotificationChannel serviceChannel = new NotificationChannel(
//                    CHANNEL_ID,
//                    "Foreground Service Channel",
//                    NotificationManager.IMPORTANCE_DEFAULT
//            );
//
//            NotificationManager manager = getSystemService(NotificationManager.class);
//            manager.createNotificationChannel(serviceChannel);
//        }
//    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForeground(true);
        if(mPlayer!=null){
            mPlayer.stop();
            mPlayer.release();
        }
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_GAIN:
                audioFocusGained();
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                duckAudio();
                break;
            case AudioManager.AUDIOFOCUS_LOSS:
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                audioFocusLost();
                break;
        }

        mPrevAudioFocusState = focusChange;
    }

    private void audioFocusLost() {
        if (getIsPlaying()) {
            mWasPlayingAtFocusLost = true;
            pauseSong();
        } else {
            mWasPlayingAtFocusLost = false;
        }
    }

    private void audioFocusGained() {
        if (mWasPlayingAtFocusLost) {
            if (mPrevAudioFocusState == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
                unduckAudio();
            } else {
                resumeSong();
            }
        }

        mWasPlayingAtFocusLost = false;
    }

    private void pauseSong() {

        if (mPlayer == null) {
            initMusicPlayer();
        }

        isPlaying = false;
        mPlayer.pause();
    }

    private void resumeSong() {

        if (mPlayer == null) {
            initMusicPlayer();
        }

        if (playingQueue.isEmpty()) {
            return;
        }

        isPlaying = true;
        if (mCurrSong == null) {
            handleNext();
        } else {
            mPlayer.start();
        }
    }

    private void duckAudio() {
        mPlayer.setVolume(0.3f, 0.3f);
        mWasPlayingAtFocusLost = getIsPlaying();
    }

    private void unduckAudio() {
        mPlayer.setVolume(1f, 1f);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {

        isMusicReady = false;
        if(isShuffle) {
            onPlayNextRandom();
        } else {
            if(isLoop) {
                playAudio();
                return;
            }

            onPlayNextNormal();
        }

        sendBroadcast(Constants.ACTION_SONG_COMPLETE, null);
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        mp.reset();
        isPlaying = false;
        return false;
    }


    @Override
    public void onTaskRemoved(Intent rootIntent) {
        stopSelf();
    }

    private void handlePlayPause(Bundle bundle) {

        if(mPlayer == null) {
            if(bundle == null) {
                initPlayQueueWhenEmpty();
                mPosition = 0;
            } else {
                int position = bundle.getInt(Constants.KEY_POSITION);
                mPosition = position;
            }

            playAudio();

        } else {
            if(getIsPlaying()) {
                isPlaying = false;
                mPlayer.pause();
                return;
            } else {
                isPlaying = true;
                mPlayer.start();
            }
        }

        setupNotification();
    }

    private void initPlayQueueWhenEmpty() {

        this.setPlayingQueue(app.getMediaPlayList());
    }

    private void handlePause(Bundle bundle) {

        if(mPlayer == null) {
            int position = bundle.getInt(Constants.KEY_POSITION);
            mPosition = position;
            playAudio();

        } else if(getIsPlaying()) {
            isPlaying = true;
            mPlayer.start();
        }
    }

    private void handleShuffle() {
        if(this.isShuffle()) {
            isShuffle = false;
        } else {
            isShuffle = true;
            isLoop = false;
        }
    }

    private void handleLoop() {
        if(this.isLoop()) {
            isLoop = false;
        } else {
            isLoop = true;
            isShuffle = false;
        }
    }

    private void handleNext() {

        isMusicReady = false;

        if(this.isShuffle()) {
            onPlayNextRandom();
        } else {
            onPlayNextNormal();
        }

        setupNotification();
    }

    private void handlePrevious() {

        isMusicReady = false;

        this.mPosition --;

        if(mPosition <= 0) {
            mPosition = 0;
        }

        playAudio();

        setupNotification();
    }

    private void onPlayNextRandom() {

        Random randomObject = new Random();
        this.mPosition = randomObject.nextInt(playingQueue.size());
        if(this.mPosition == playingQueue.size()) {
            this.mPosition = 0;
        }

        playAudio();
    }

    private void onPlayNextNormal() {
        this.mPosition ++;
        playAudio();
    }

    private void handleQuit() {

        if (mCurrSong == null) {
            playAudio();
        }
    }

    private void initMusicPlayer(){

        mPlayer = new MediaPlayer();
        mPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mPlayer.setOnPreparedListener(this);
        mPlayer.setOnCompletionListener(this);
        mPlayer.setOnErrorListener(this);
    }

    @Override
    public void onPrepared(MediaPlayer mp) {

        int result = audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);

        if(result == audioManager.AUDIOFOCUS_REQUEST_GRANTED){
            if(!isNeedPause) {
                mp.start();
            }
            isMusicReady = true;
        }
    }

    private void playAudio() {
        synchronized(this) {
            if (mPlayer == null) {
                initMusicPlayer();
            }

            this.isPlaying = true;
            mPlayer.reset();

            Song songPlay = this.getSong(mPosition);
            if(songPlay == null) {
                return;
            }

            this.mCurrSong = songPlay;

            try {
                mPlayer.setDataSource(mCurrSong.get_path());
                mPlayer.prepareAsync();
            } catch (Exception e) {
                isPlaying = false;
            }
        }
    }

    private Song getSong(int index) {

        if(this.playingQueue.isEmpty()) {
            return null;
        }

        if(index >= this.playingQueue.size()) {
            this.mPosition = 0;
            index = 0;
        }

        return this.playingQueue.get(index);
    }

    private class RunBackgroundMusic extends AsyncTask<String, Void, String> {

        protected String doInBackground(String... params){
            return "";
        }

        protected void onPostExecute(String response) {
            try {
                mPlayer.setDataSource(mCurrSong.get_path());
                mPlayer.prepareAsync();
            } catch (Exception e) {
                isPlaying = false;
            }
        }
    }

    public void sendBroadcast(String action, String data) {

        Intent broadcastIntent = new Intent();
        broadcastIntent.putExtra(Constants.KEY_DATA, data);
        broadcastIntent.setAction(action);

        sendBroadcast(broadcastIntent);
    }
}
