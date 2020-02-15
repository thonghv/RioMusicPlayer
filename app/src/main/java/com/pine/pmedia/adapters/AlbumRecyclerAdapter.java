package com.pine.pmedia.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.RecyclerView;

import com.pine.pmedia.R;
import com.pine.pmedia.activities.AlbumActivity;
import com.pine.pmedia.helpers.Constants;
import com.pine.pmedia.models.Album;

import java.util.ArrayList;

public class AlbumRecyclerAdapter extends RecyclerView.Adapter<AlbumRecyclerAdapter.ViewHolder> {

    private ArrayList<Album> mValues;
    private Context mContext;

    public AlbumRecyclerAdapter(Context context, ArrayList values) {

        mValues = values;
        mContext = context;
    }

    @Override
    public AlbumRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.album_card_item, parent, false);

        // Clear background of card view
        CardView cardView = view.findViewById(R.id.cardView);
        cardView.setBackgroundResource(0);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        viewHolder.setData(mValues.get(position));
    }

    @Override
    public int getItemCount() {

        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView albumName;
        public TextView albumArtist;
        public TextView numberOfSong;
        public ImageView imgCover;
        public RelativeLayout bottomCardLayout;

        public ViewHolder(View v) {

            super(v);

            v.setOnClickListener(this);
            albumName =  v.findViewById(R.id.cardAlbumName);
            albumArtist =  v.findViewById(R.id.cardAlbumArtist);
            imgCover =  v.findViewById(R.id.cardImageView);
            numberOfSong = v.findViewById(R.id.cardNumberOfSong);
            bottomCardLayout = v.findViewById(R.id.bottomCardLayout);

            // Set font text
            Typeface customFace = Typeface.createFromAsset(mContext.getAssets(), Constants.FONT_ROBOTO_REGULAR);
            albumName.setTypeface(customFace);
            albumArtist.setTypeface(customFace);
        }

        public void setData(Album item) {

            imgCover.setImageBitmap(item.getImgCover());
            albumName.setText(item.getName());
            albumArtist.setText(item.getArtist());
            numberOfSong.setText(item.getNumberOfSong() + Constants.SONGS);

            bottomCardLayout.setBackgroundColor(
                    Palette.from(item.getImgCover()).generate().getVibrantColor(Color.parseColor(Constants.PALETTE_ALBUM_COLOR_DEFAUT)));
        }

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(view.getContext(), AlbumActivity.class);
            intent.putExtras(new Bundle());
            mContext.startActivity(intent);
        }
    }
}
