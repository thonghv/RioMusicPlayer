package com.pine.pmedia.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.SyncStateContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.palette.graphics.Palette;
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
import com.pine.pmedia.helpers.CommonHelper;
import com.pine.pmedia.helpers.Constants;
import com.pine.pmedia.models.Song;

import java.util.ArrayList;

public class SongCatRecyclerAdapter extends RecyclerView.Adapter<SongCatRecyclerAdapter.ViewHolder> {

    private ArrayList<Song> mValues;
    private Context mContext;

    public SongCatRecyclerAdapter(Context context, ArrayList values) {

        mValues = values;
        mContext = context;
    }

    @NonNull
    @Override
    public SongCatRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.song_custum_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        final Song song = mValues.get(position);
        viewHolder.setData(song);
        viewHolder.contentHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle param = new Bundle();
                Intent intent = new Intent(v.getContext(), AlbumActivity.class);
                intent.putExtras(param);

                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {

        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView trackTitle;
        public ImageView trackImage;
        public TextView trackArtist;
        public TextView trackDuration;
        public ConstraintLayout contentHolder;

        public ViewHolder(View v) {

            super(v);

            trackTitle = itemView.findViewById(R.id.trackTitle);
            trackImage = itemView.findViewById(R.id.trackImage);
            trackDuration = itemView.findViewById(R.id.trackDuration);
            trackArtist = itemView.findViewById(R.id.trackArtist);
            contentHolder = itemView.findViewById(R.id.contentItemRow);
        }

        public void setData(Song song) {
            trackTitle.setText(song.get_title());
            trackDuration.setText(CommonHelper.toFormatTime(song.get_duration()));
            trackArtist.setText(song.get_artist());

            Drawable drawable = mContext.getResources().getDrawable(R.drawable.ic_music_note_white);
            trackImage.setBackground(drawable);
        }
    }
}
