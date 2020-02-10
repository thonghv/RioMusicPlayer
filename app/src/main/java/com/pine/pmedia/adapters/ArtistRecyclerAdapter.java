package com.pine.pmedia.adapters;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Typeface;
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
import com.pine.pmedia.helpers.Constants;
import com.pine.pmedia.models.Artist;
import com.pine.pmedia.services.MusicService;

import java.util.ArrayList;

public class ArtistRecyclerAdapter extends RecyclerView.Adapter<ArtistRecyclerAdapter.MyViewHolder>  {

    private ArrayList<Artist> artistsDetails;
    private Context mContext;

    private Intent playIntent;
    private MusicService mService;

    public ArtistRecyclerAdapter(ArrayList<Artist> artistsDetails, Context mContext) {
        this.artistsDetails = artistsDetails;
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

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.artist_custum_item, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {

        final Artist artist = artistsDetails.get(position);
        holder.artistName.setText(artist.getName());
        holder.artistNumberOfSong.setText(artist.getNumberOfSong() + Constants.SONGS);
        holder.artistAvatar.setImageBitmap(artist.getImgCover());

        Typeface customFace = Typeface.createFromAsset(mContext.getAssets(), Constants.FONT_ROBOTO_LIGHT);
        holder.artistName.setTypeface(customFace);

        holder.contentHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return artistsDetails == null ? 0 : artistsDetails.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView artistName;
        public ImageView artistAvatar;
        public TextView artistNumberOfSong;
        public RelativeLayout contentHolder;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            artistName = itemView.findViewById(R.id.artistName);
            artistAvatar = itemView.findViewById(R.id.artistAvatar);
            artistNumberOfSong = itemView.findViewById(R.id.artistNumberOfSong);
            contentHolder = itemView.findViewById(R.id.artistItemRow);
        }
    }
}
