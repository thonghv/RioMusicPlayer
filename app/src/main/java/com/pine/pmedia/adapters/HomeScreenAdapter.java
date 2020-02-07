package com.pine.pmedia.adapters;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pine.pmedia.R;
import com.pine.pmedia.activities.PlaySongActivity;
import com.pine.pmedia.helpers.CommonHelper;
import com.pine.pmedia.models.Song;
import com.pine.pmedia.helpers.Constants;
import com.pine.pmedia.services.MusicService;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class HomeScreenAdapter extends RecyclerView.Adapter<HomeScreenAdapter.MyViewHolder>  {

    private ArrayList<Song> songDetails;
    private Context mContext;

    private Intent playIntent;
    private MusicService mService;

    public HomeScreenAdapter(ArrayList<Song> songDetails, Context mContext) {
        this.songDetails = songDetails;
        this.mContext = mContext;
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

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        inItService();
        View layoutInflater = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_custum_mainscreen_adapter, parent, false);
        return new MyViewHolder(layoutInflater);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {

        final Song song = songDetails.get(position);
        holder.trackTitle.setText(song.get_title());
        holder.trackDuration.setText(CommonHelper.toFormatTime(song.get_duration()));

        Typeface customFace = Typeface.createFromAsset(mContext.getAssets(), Constants.FONT_ROBOTO);
        holder.trackTitle.setTypeface(customFace);

        if(!song.get_image().isEmpty()) {
            Picasso.get().load(song.get_image()).into(holder.trackImage);
        }

        holder.contentHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new onProcessStartPlay(position).execute();

                Bundle bundle = new Bundle();
//                bundle.putLong(Constants.KEY_ID, song.get_id());
//                bundle.putString(Constants.KEY_IMAGE, song.get_image());
//                bundle.putString(Constants.KEY_TITLE, song.get_title());
//                bundle.putString(Constants.KEY_ARTIST, song.get_artist());
//                bundle.putInt(Constants.KEY_POSITION, position);
//                bundle.putInt(Constants.KEY_DURATION, song.get_duration());
//                bundle.putString(Constants.KEY_PATH, toUrlPlayTrack(song.get_id()));
//                bundle.putParcelableArrayList(Constants.KEY_SONG_LIST, songDetails);

                Intent intent = new Intent(v.getContext(), PlaySongActivity.class);
                intent.putExtras(bundle);

                mContext.startActivity(intent);
            }
        });
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

                Bundle bundleS = new Bundle();
                bundleS.putInt(Constants.KEY_POSITION, position);
                if(mService.getPlayingQueue().isEmpty()) {
                    mService.updatePlayingQueue(songDetails);
                }

                mService.onProcess(Constants.PLAYPAUSE, bundleS);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    @Override
    public int getItemCount() {
        return songDetails == null ? 0 : songDetails.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView trackTitle;
        public ImageView trackImage;
        public TextView trackDuration;
        public RelativeLayout contentHolder;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            trackTitle = itemView.findViewById(R.id.trackTitle);
            trackImage = itemView.findViewById(R.id.trackImage);
            trackDuration = itemView.findViewById(R.id.trackDuration);
            contentHolder = itemView.findViewById(R.id.contentItemRow);
        }
    }
}
