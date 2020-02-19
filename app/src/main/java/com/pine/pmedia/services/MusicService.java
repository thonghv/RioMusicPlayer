package com.pine.pmedia.services;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
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
import com.pine.pmedia.R;
import com.pine.pmedia.activities.MainActivity;
import com.pine.pmedia.helpers.CommonHelper;
import com.pine.pmedia.helpers.Constants;
import com.pine.pmedia.models.Song;
import com.pine.pmedia.receivers.ControlActionsListener;

import java.util.ArrayList;
import java.util.Random;

public class MusicService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener, AudioManager.OnAudioFocusChangeListener {

    private final String NOTIFICATION_CHANNEL = "music_player_channel";
    private MediaPlayer mPlayer;
    private Song mCurrSong;
    private Activity mActivity;
    private int mPosition = 0;
    private ArrayList<Song> playingQueue = new ArrayList<>();
    public AudioManager audioManager;
    private MediaSessionCompat mMediaSession;
    private Bitmap mCurrSongCover;
    private boolean isPlaying;
    private boolean isLoop;
    private boolean isShuffle;
    public boolean isMusicReady = false;
    public boolean isNeedPause;

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

    public void updatePlayingQueue(ArrayList<Song> playingQueue) {
        this.playingQueue = playingQueue;
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
                String action = intent.getAction();
                Bundle bundle = intent.getExtras();
                switch (action) {
                    case Constants.PLAYPAUSE:
                        break;
                    case Constants.STOP:
                    case Constants.QUIT:
                        handleQuit();
                        break;
                }
            }
        }

        /*Bundle bundle = intent.getExtras();

        String path = bundle.getString(Constants.KEY_PATH);
        this.mIntentUri = Uri.parse(path);

        createNotificationChannel();
        Intent notificationIntent = new Intent(this, PlaySongActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Foreground Service")
                .setContentText("Hello Today")
                .setSmallIcon(R.drawable.favorite_on)
                .setContentIntent(pendingIntent)
                .build();

        startForeground(1, notification);S
*/
//        return super.onStartCommand(intent, flags, startId);

        initNotification();

        return START_NOT_STICKY;
    }


    public void onProcess(String action, Bundle bundle) {

        this.isNeedPause = false;
        switch (action) {
            case Constants.PLAYPAUSE:
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

    private void initNotification() {
        if(mCurrSong != null) {
            int color = CommonHelper.getTextColor(this);
            mCurrSongCover = CommonHelper.getColoredBitmap(mActivity, color);
            setupNotification();
        } else {
            setupFakeNotification();
        }
    }
    private void setupFakeNotification() {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_headset_small)
                .setContentTitle("My notification")
                .setContentText("Hello A")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("Much longer text that cannot fit one line..."))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);


        startForeground(1, builder.build());
    }

    private void setupNotification() {

        String title = mCurrSong.get_title();
        String artist = mCurrSong.get_artist();
        int playPauseIcon = getIsPlaying() ? R.drawable.ic_pause_vector : R.drawable.ic_play_vector;

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_headset_small)
                .setContentTitle(title)
                .setContentText(artist)
                .setLargeIcon(mCurrSongCover)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setShowActionsInCompactView(0, 1, 2)
                        .setMediaSession(mMediaSession.getSessionToken()))
                .addAction(R.drawable.ic_previous_vector, getString(R.string.previous), getIntent(Constants.PREVIOUS))
                .addAction(playPauseIcon, getString(R.string.playpause), getIntent(Constants.PLAYPAUSE))
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
        Intent intent = new Intent(this, ControlActionsListener.class);
        intent.setAction(action);
        return PendingIntent.getBroadcast(this, 0, intent, 0);
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
                System.out.println("D");
                break;
            case AudioManager.AUDIOFOCUS_LOSS:
                System.out.println("D");
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                System.out.println("D");
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                System.out.println("D");
                break;
        }
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

        // Send broadcast data apply for current activity menu
        if(CommonHelper.getCurrentActivity(getBaseContext()).equals(Constants.MAIN_ACTIVITY_NAME)) {
            sendBroadcast(Constants.SONG_COMPLETE);
        }
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
            int position = bundle.getInt(Constants.KEY_POSITION);
            mPosition = position;
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


    }

    private void handlePrevious() {

        isMusicReady = false;

        this.mPosition --;

        if(mPosition <= 0) {
            mPosition = 0;
        }

        playAudio();
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
//            CommonHelper.addRecent(getApplicationContext(), mCurrSong.get_path(), mCurrSong.get_title(), mCurrSong.get_artist(), mCurrSong.get_image());
            isMusicReady = true;
        }
    }

    private void playAudio() {
        if (mPlayer == null) {
            initMusicPlayer();
        }

        this.isPlaying = true;
        mPlayer.reset();

        Song songPlay = this.getSong(mPosition);
        if(songPlay == null) {
            System.out.println("ERROR : " + mPosition);
            return;
        }

        this.mCurrSong = songPlay;

        if(songPlay.get_path().contains("http")){
            new RunBackgroundMusic().execute("");
        } else {
            System.out.println("D");
        }
    }

    private Song getSong(int index) {

        if(index >= this.playingQueue.size()) {
            return null;
        }

        return this.playingQueue.get(index);
    }

    private class RunBackgroundMusic extends AsyncTask<String, Void, String> {

        protected String doInBackground(String... params){
            return "";
        }

        protected void onPostExecute(String response) {
            try {
//                loadBitmapFromFile(mCurrSong.get_image());
                mPlayer.setDataSource(mCurrSong.get_path());
                mPlayer.prepareAsync();
            } catch (Exception e) {
                isPlaying = false;
            }
        }
    }

    public void sendBroadcast(String data) {

        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(MainActivity.mBroadcastAction);
        broadcastIntent.putExtra(Constants.KEY_DATA, data);
        sendBroadcast(broadcastIntent);
    }
}
