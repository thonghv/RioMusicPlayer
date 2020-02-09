package com.pine.pmedia.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.palette.graphics.Palette;
import androidx.palette.graphics.Target;
import androidx.recyclerview.widget.RecyclerView;

import com.pine.pmedia.R;
import com.pine.pmedia.helpers.Constants;
import com.pine.pmedia.models.Album;

import java.util.ArrayList;

public class AlbumRecyclerAdapter extends RecyclerView.Adapter<AlbumRecyclerAdapter.ViewHolder> {

    ArrayList<Album> mValues = new ArrayList();
    Context mContext;

    public AlbumRecyclerAdapter(Context context, ArrayList values) {

        mValues = values;
        mContext = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView albumName;
        public TextView albumArtist;
        public ImageView imgCover;
        public RelativeLayout bottomCardLayout;
        Album item;

        public ViewHolder(View v) {

            super(v);

            v.setOnClickListener(this);
            albumName =  v.findViewById(R.id.cardAlbumName);
            albumArtist =  v.findViewById(R.id.cardAlbumArtist);
            imgCover =  v.findViewById(R.id.cardImageView);
            bottomCardLayout = v.findViewById(R.id.bottomCardLayout);

            Typeface customFace = Typeface.createFromAsset(mContext.getAssets(), Constants.FONT_ROBOTO);
            albumName.setTypeface(customFace);
            albumArtist.setTypeface(customFace);
        }

        public void setData(Album item) {
            this.item = item;

            imgCover.setImageBitmap(item.getImgCover());
            albumName.setText(item.getName());
            albumArtist.setText(item.getArtist());

            bottomCardLayout.setBackgroundColor(
                    Palette.from(item.getImgCover()).generate().getVibrantColor(Color.parseColor("#403f4d")));
        }

        @Override
        public void onClick(View view) {

        }
    }

    @Override
    public AlbumRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.album_card_item, parent, false);
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

    public interface ItemListener {
        void onItemClick(Album item);
    }
}
