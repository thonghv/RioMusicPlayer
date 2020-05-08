package com.pine.pmedia.adapters;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.view.MotionEventCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.pine.pmedia.R;
import com.pine.pmedia.control.MusicVisualizer;
import com.pine.pmedia.helpers.ItemTouchHelperAdapter;
import com.pine.pmedia.helpers.ItemTouchHelperViewHolder;
import com.pine.pmedia.helpers.OnStartDragListener;
import com.pine.pmedia.helpers.Constants;
import com.pine.pmedia.models.Song;
import com.pine.pmedia.services.MusicService;
import com.pine.pmedia.sqlite.DBManager;

import java.util.ArrayList;
import java.util.Collections;

public class SongQueueRecyclerAdapter extends RecyclerView.Adapter<SongQueueRecyclerAdapter.ViewHolder> implements ItemTouchHelperAdapter {


    private MusicService mService;
    private DBManager dbManager;

    private ArrayList songs;
    private Context mContext;
    private Intent playIntent;

    private final OnStartDragListener mDragStartListener;

    public SongQueueRecyclerAdapter(Context context, ArrayList values, OnStartDragListener dragStartListener) {

        this.songs = values;
        this.mContext = context;
        this.mDragStartListener = dragStartListener;

        this.dbManager = new DBManager(context);
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
        if(mService != null) {
            playIntent = new Intent(mContext, MusicService.class);
            mContext.bindService(playIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @NonNull
    @Override
    public SongQueueRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        inItService();

        View view =  LayoutInflater.from(mContext).inflate(R.layout.song_queue_item, parent, false);;
        return new ViewHolder(view);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int position) {
        final Song song = (Song) songs.get(position);
        viewHolder.setData(song);

        // On click to play song
        viewHolder.contentHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SongQueueRecyclerAdapter.onProcessStartPlay(position).execute();
            }
        });

        // Update UI Visualizer of song playing
        viewHolder.updateVisualizerUI(position);

        viewHolder.musicVisualizerControl.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEventCompat.getActionMasked(event) ==
                        MotionEvent.ACTION_DOWN) {
                    mDragStartListener.onStartDrag(viewHolder);
                }
                return false;
            }
        });
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        Collections.swap(songs, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public void onItemDismiss(int position) {

    }

    /**
     * Handler lick to play song
     */
    private class onProcessStartPlay extends AsyncTask<String, Void, String> {

        private int position;

        public onProcessStartPlay(int position) {
            this.position = position;
        }

        protected String doInBackground(String... params){

            mService.setPlayingQueue(songs);
            mService.checkAndResetPlay();

            Bundle bundle = new Bundle();
            bundle.putInt(Constants.KEY_POSITION, position);
            mService.onProcess(Constants.PLAY_PAUSE, bundle);
            return "";
        }

        protected void onPostExecute(String response) {
            notifyDataSetChanged();
        }
    }

    @Override
    public int getItemCount() {

        return songs.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements ItemTouchHelperViewHolder {

        public TextView trackTitle;
        public TextView trackArtist;
        public LinearLayout contentHolder;

        public LinearLayout removeItemControl;
        public RelativeLayout swapItemControl;
        public MusicVisualizer musicVisualizerControl;

        public ViewHolder(View itemView) {

            super(itemView);

            trackTitle = itemView.findViewById(R.id.trackTitle);
            trackArtist = itemView.findViewById(R.id.trackArtist);
            contentHolder = itemView.findViewById(R.id.songLayout);
            removeItemControl = itemView.findViewById(R.id.removeItem);
            swapItemControl = itemView.findViewById(R.id.swapItem);
            musicVisualizerControl = itemView.findViewById(R.id.visualizerItem);
        }

        public void setData(Song song) {

            trackTitle.setText(song.get_title());
            trackArtist.setText(song.get_artist());
        }

        public void updateVisualizerUI(int position) {
            if(mService.getCurrentPosition() == position) {
                trackTitle.setTextColor(mContext.getResources().getColor(R.color.md_yellow_800));
                trackArtist.setTextColor(mContext.getResources().getColor(R.color.md_yellow_700));
                musicVisualizerControl.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onItemSelected() {
            itemView.setBackgroundColor(Color.LTGRAY);
        }

        @Override
        public void onItemClear() {
            itemView.setBackgroundColor(0);
        }
    }
}
