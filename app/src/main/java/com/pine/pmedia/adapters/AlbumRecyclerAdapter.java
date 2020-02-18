package com.pine.pmedia.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
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
            albumName.setText(item.getName());
            albumArtist.setText(item.getArtist());
            numberOfSong.setText(item.getNumberOfSong() + Constants.SONGS);

//            if(item.getImgCover() != null) {
//                bottomCardLayout.setBackgroundColor(
//                        Palette.from(item.getImgCover()).generate()
//                                .getVibrantColor(Color.parseColor(Constants.PALETTE_ALBUM_COLOR_DEFAUT)));
//            }

            if(item.getImgCover() != null) {
                imgCover.setImageBitmap(item.getImgCover());
                ImageSize targetSize = new ImageSize(120, 125);
                ImageLoader.getInstance().displayImage(item.getArtUri(), new ImageViewAware(imgCover),
                        new DisplayImageOptions.Builder()
                                .imageScaleType(ImageScaleType.EXACTLY)
                                .cacheInMemory(true)
                                .resetViewBeforeLoading(true)
                                .bitmapConfig(Bitmap.Config.RGB_565)
                                .build()
                        , targetSize
                        , new SimpleImageLoadingListener() {
                            @Override
                            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                                new Palette.Builder(loadedImage).generate(new Palette.PaletteAsyncListener() {
                                    @Override
                                    public void onGenerated(@NonNull Palette palette) {
                                        Palette.Swatch swatch = palette.getVibrantSwatch();
                                        if (swatch != null) {
                                            int color = swatch.getRgb();
                                            bottomCardLayout.setBackgroundColor(color);
                                        } else {
                                            Palette.Swatch mutedSwatch = palette.getMutedSwatch();
                                            if (mutedSwatch != null) {
                                                int color = mutedSwatch.getRgb();
                                                bottomCardLayout.setBackgroundColor(color);
                                            }
                                        }
                                    }
                                });
                            }
                            @Override
                            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                                System.out.println("ERRRRRRRRRRRRRRRRRRRRRRRRRRRR");
                            }
                        },null);

            } else {

                String artUrl = Constants.DRAWABLE_PATH + R.drawable.icon_album_custum;
                imgCover.setBackgroundResource(R.drawable.icon_album_custum);

                Drawable drawable = ContextCompat.getDrawable(mContext, R.drawable.icon_album_custum);
                Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
                bottomCardLayout.setBackgroundColor(
                        Palette.from(bitmap).generate()
                                .getVibrantColor(Color.parseColor(Constants.PALETTE_ALBUM_COLOR_DEFAUT)));
            }
     }

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(view.getContext(), AlbumActivity.class);
            intent.putExtras(new Bundle());
            mContext.startActivity(intent);
        }
    }
}
