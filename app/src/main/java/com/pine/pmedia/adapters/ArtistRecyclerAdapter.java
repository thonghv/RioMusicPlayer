package com.pine.pmedia.adapters;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.Typeface;
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

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.pine.pmedia.R;
import com.pine.pmedia.activities.AlbumActivity;
import com.pine.pmedia.activities.ArtistActivity;
import com.pine.pmedia.helpers.Constants;
import com.pine.pmedia.models.Artist;
import com.pine.pmedia.services.MusicService;

import java.util.ArrayList;

public class ArtistRecyclerAdapter extends RecyclerView.Adapter<ArtistRecyclerAdapter.MyViewHolder>  {

    private ArrayList<Artist> artistsDetails;
    private Context mContext;

    private Intent playIntent;
    private MusicService mService;
    private ImageLoader imageLoader;

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
        imageLoader = ImageLoader.getInstance();

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.artist_custum_item, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {

        final Artist artist = artistsDetails.get(position);
        holder.artistName.setText(artist.getName());

        String artistInfo = artist.getNumberOfAlbums() + Constants.SPACE + Constants.ALBUMS
                + " - " + artist.getNumberOfTracks() + Constants.SPACE + Constants.SONGS;
        holder.artistNumberOfSong.setText(artistInfo);

        Typeface customFace = Typeface.createFromAsset(mContext.getAssets(), Constants.FONT_ROBOTO_LIGHT);
        holder.artistName.setTypeface(customFace);

        this.onLoadImageCover(artist.getArtUri(), holder);

        holder.contentHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle param = new Bundle();
                param.putInt(Constants.KEY_ID, artist.getId());
                param.putString(Constants.KEY_NAME, artist.getName());
                param.putString(Constants.KEY_ARTWORK, artist.getArtUri());
                param.putInt(Constants.KEY_NUMBER_OF_ALBUM, artist.getNumberOfAlbums());
                param.putInt(Constants.KEY_NUMBER_OF_TRACK, artist.getNumberOfTracks());

                param.putParcelableArrayList(Constants.KEY_SONG_LIST, artist.getAlbumList());

                Intent intent = new Intent(v.getContext(), ArtistActivity.class);
                intent.putExtras(param);

                mContext.startActivity(intent);
            }
        });
    }

    private void onLoadImageCover(String imageUri, final ArtistRecyclerAdapter.MyViewHolder holder) {

        ImageSize targetSize = new ImageSize(124, 124);
        imageLoader.displayImage(imageUri, new ImageViewAware(holder.artistAvatar),
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
