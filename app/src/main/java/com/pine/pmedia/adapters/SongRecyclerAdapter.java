package com.pine.pmedia.adapters;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.pine.pmedia.R;
import com.pine.pmedia.helpers.ExecuteProcessStartPlay;
import com.pine.pmedia.helpers.CommonHelper;
import com.pine.pmedia.models.Song;
import com.pine.pmedia.helpers.Constants;
import com.pine.pmedia.services.MusicService;
import com.pine.pmedia.sqlite.DBManager;

import java.util.ArrayList;

public class SongRecyclerAdapter extends RecyclerView.Adapter<SongRecyclerAdapter.SongRowHolder>  {

    private MusicService mService;
    private DBManager dbManager;

    private ArrayList<Song> songs;
    private Context mContext;
    private Intent playIntent;
    private ImageLoader imageLoader;

    public SongRecyclerAdapter(ArrayList<Song> songs, Context mContext) {
        this.songs = songs;
        this.mContext = mContext;

        this.dbManager = new DBManager(mContext);
        this.dbManager.open();
    }

    protected ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder) service;
            mService = binder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    private void inItService() {
        playIntent = new Intent(mContext, MusicService.class);
        mContext.bindService(playIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public int getItemViewType(int position) {
//        if(position == 0) {
//            return Constants.ITEM_TYPE_CONTROL;
//        }

        return Constants.ITEM_TYPE_SONG;
    }

    @NonNull
    @Override
    public SongRowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        inItService();
        imageLoader = ImageLoader.getInstance();

        View view = null;
        switch (viewType) {
            case Constants.ITEM_TYPE_SONG:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.song_custum_item, parent, false);
                break;
            case Constants.ITEM_TYPE_CONTROL:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.shuffle_all_controll, parent, false);
                break;
        }

        return new SongRowHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull SongRowHolder holder, final int position) {

        Typeface customFace = Typeface.createFromAsset(mContext.getAssets(), Constants.FONT_ROBOTO_LIGHT);
        switch (holder.getItemViewType()) {
            case Constants.ITEM_TYPE_SONG:

                final Song song = songs.get(position);
                holder.trackTitle.setText(song.get_title());
                holder.trackDuration.setText(CommonHelper.toFormatTimeMS(song.get_duration()));
                holder.trackArtist.setText(song.get_artist());

                this.onLoadImageCover(song.get_image(), holder);

                //holder.trackTitle.setTypeface(customFace);

                holder.contentHolder.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new ExecuteProcessStartPlay(mContext, dbManager, mService, songs, position, -1).execute();
                    }
                });

                break;

                case Constants.ITEM_TYPE_CONTROL:
                    holder.shuffleAll.setTypeface(customFace);
                    break;
        }
    }

    private void onLoadImageCover(String imageUri, final SongRowHolder holder) {

        ImageSize targetSize = new ImageSize(124, 124);
        imageLoader.displayImage(imageUri, new ImageViewAware(holder.trackImage),
                new DisplayImageOptions.Builder()
                        .imageScaleType(ImageScaleType.EXACTLY)
                        .cacheInMemory(true)
                        .resetViewBeforeLoading(true)
                        .bitmapConfig(Bitmap.Config.RGB_565)
                        .build()
                ,targetSize
                , new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {

                    }
                    @Override
                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                    }
                },null);
    }

    private class onProcessStartPlay extends AsyncTask<String, Void, String> {

        private int position;

        public onProcessStartPlay(int position) {
            this.position = position;
        }
        protected String doInBackground(String... params){
            return "";
        }

        protected void onPostExecute(String response) {

            try {
                mService.setPlayingQueue(songs);
                mService.checkAndResetPlay();

                Bundle bundle = new Bundle();
                bundle.putInt(Constants.KEY_POSITION, position);
                mService.onProcess(Constants.PLAY_PAUSE, bundle);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    @Override
    public int getItemCount() {
        return songs == null ? 0 : songs.size();
    }

    public class SongRowHolder extends RecyclerView.ViewHolder {

        public TextView trackTitle;
        public ImageView trackImage;
        public TextView trackArtist;
        public TextView trackDuration;
        public TextView shuffleAll;
        public LinearLayout contentHolder;


        public SongRowHolder(@NonNull View itemView, int itemType) {
            super(itemView);

            switch (itemType) {
                case Constants.ITEM_TYPE_SONG:
                    trackTitle = itemView.findViewById(R.id.trackTitle);
                    trackImage = itemView.findViewById(R.id.trackImage);
                    trackDuration = itemView.findViewById(R.id.trackDuration);
                    trackArtist = itemView.findViewById(R.id.trackArtist);
                    contentHolder = itemView.findViewById(R.id.songLayout);
                    break;
                case Constants.ITEM_TYPE_CONTROL:
                    shuffleAll = itemView.findViewById(R.id.textShuffleAll);
                    break;
            }
        }
    }
}
