package com.pine.pmedia.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.view.MotionEventCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.pine.pmedia.R;
import com.pine.pmedia.activities.QueueActivity;
import com.pine.pmedia.control.MusicVisualizer;
import com.pine.pmedia.helpers.CommonHelper;
import com.pine.pmedia.helpers.ItemTouchHelperAdapter;
import com.pine.pmedia.helpers.ItemTouchHelperViewHolder;
import com.pine.pmedia.helpers.OnStartDragListener;
import com.pine.pmedia.helpers.Constants;
import com.pine.pmedia.models.Song;
import com.pine.pmedia.services.MusicService;
import com.pine.pmedia.sqlite.DBManager;

import java.util.ArrayList;
import java.util.Collections;

public class SongQueueRecyclerAdapter extends RecyclerView.Adapter<SongQueueRecyclerAdapter.ViewHolder>
        implements ItemTouchHelperAdapter {


    private MusicService mService;
    private DBManager dbManager;

    private ArrayList songs;
    private Context mContext;
    private long currentId;
    private boolean isPlaying;

    private final OnStartDragListener mDragStartListener;

    public SongQueueRecyclerAdapter(Context context, MusicService mService, ArrayList values,
                                    OnStartDragListener dragStartListener, long currentId, boolean isPlaying) {

        this.mContext = context;
        this.mService = mService;
        this.songs = values;
        this.mDragStartListener = dragStartListener;
        this.currentId = currentId;
        this.isPlaying = isPlaying;

        this.dbManager = new DBManager(context);
        this.dbManager.open();
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @NonNull
    @Override
    public SongQueueRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

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
                new ExecuteProcessStartPlay(position).execute();
            }
        });

        // Update UI Visualizer of song playing
        viewHolder.updateSongPlayingUI(song.get_id());

        viewHolder.swapItemControl.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEventCompat.getActionMasked(event) ==
                        MotionEvent.ACTION_DOWN) {
                    mDragStartListener.onStartDrag(viewHolder);
                }
                return false;
            }
        });

        viewHolder.removeItemControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                songs.remove(position);
                notifyDataSetChanged();
                updateDataQueueUI();
            }
        });
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        Collections.swap(songs, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);

        //((QueueActivity)mContext).onUpdateQueueSongDB();

        return true;
    }

    @Override
    public void onItemDismiss(int position) {
        songs.remove(position);
//        notifyItemRemoved(position);
        notifyDataSetChanged();

        updateDataQueueUI();

        //((QueueActivity)mContext).onUpdateQueueSongDB();
    }

    private void updateDataQueueUI() {
        ((QueueActivity)mContext).onUpdateHeaderData(-1);
    }

    /**
     * Handler lick to play song
     */
    private class ExecuteProcessStartPlay extends AsyncTask<String, Void, String> {

        private int position;

        public ExecuteProcessStartPlay(int position) {
            this.position = position;
        }

        protected String doInBackground(String... params){

            mService.setPlayingQueue(songs);
            mService.checkAndResetPlay();

            Bundle bundle = new Bundle();
            bundle.putInt(Constants.KEY_POSITION, position);
            mService.onProcess(Constants.PLAY_PAUSE, bundle);

            CommonHelper.updateSettingSongPLaying(dbManager, mService.getMCurrSong().get_id(), position);

            return "";
        }

        protected void onPostExecute(String response) {
            currentId = mService.getMCurrSong().get_id();
            isPlaying = true;
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
        public ImageView swapItemControl;
        public MusicVisualizer musicVisualizerControl;

        public ViewHolder(View itemView) {

            super(itemView);

            trackTitle = itemView.findViewById(R.id.trackTitle);
            trackArtist = itemView.findViewById(R.id.trackArtist);
            contentHolder = itemView.findViewById(R.id.songLayout);
            removeItemControl = itemView.findViewById(R.id.removeItem);
            swapItemControl = itemView.findViewById(R.id.swapItem);
            musicVisualizerControl = itemView.findViewById(R.id.queueVisualizer);
        }

        public void setData(Song song) {

            trackTitle.setText(song.get_title());
            trackArtist.setText(song.get_artist());
        }

        public void updateSongPlayingUI(long songId) {
            if(currentId == songId) {
                trackTitle.setTextColor(mContext.getResources().getColor(R.color.md_yellow_700));
                trackArtist.setTextColor(mContext.getResources().getColor(R.color.md_yellow_800));

                if(isPlaying) {
                    musicVisualizerControl.setVisibility(View.VISIBLE);
                }
            } else {
                trackTitle.setTextColor(mContext.getResources().getColor(R.color.p_song_01));
                trackArtist.setTextColor(mContext.getResources().getColor(R.color.p_gray));
                musicVisualizerControl.setVisibility(View.GONE);
            }
        }

        @Override
        public void onItemSelected() {
            itemView.setBackgroundColor(mContext.getResources().getColor(R.color.md_grey_800));
        }

        @Override
        public void onItemClear() {
            itemView.setBackgroundColor(0);
        }
    }
}
