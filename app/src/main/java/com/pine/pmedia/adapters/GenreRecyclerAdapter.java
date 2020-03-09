package com.pine.pmedia.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
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
import com.pine.pmedia.activities.GenreActivity;
import com.pine.pmedia.helpers.CommonHelper;
import com.pine.pmedia.helpers.Constants;
import com.pine.pmedia.models.Genre;

import java.util.ArrayList;

public class GenreRecyclerAdapter extends RecyclerView.Adapter<GenreRecyclerAdapter.ViewHolder> {

    private ArrayList<Genre> mValues;
    private Context mContext;

    public GenreRecyclerAdapter(Context context, ArrayList values) {

        this.mValues = values;
        this.mContext = context;
    }

    @Override
    public GenreRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.genre_card_item, parent, false);

        return new GenreRecyclerAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GenreRecyclerAdapter.ViewHolder viewHolder, int position) {
        final Genre genre = mValues.get(position);

        viewHolder.setData(mValues.get(position));
        viewHolder.imgCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle param = new Bundle();
                param.putInt(Constants.KEY_ID, genre.getId());
                param.putString(Constants.KEY_NAME, genre.getName());
                param.putString(Constants.KEY_ARTWORK, genre.getArtRepresent());
                param.putInt(Constants.KEY_NUMBER_OF_TRACK, genre.getNumberOfTracks());
                param.putInt(Constants.KEY_DURATION, genre.getTotalDuration());

                Intent intent = new Intent(v.getContext(), GenreActivity.class);
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

        public TextView genreName;
        public TextView numberOfTracks;
        public ImageView imgCover;

        public ViewHolder(@NonNull View v) {

            super(v);

            genreName =  v.findViewById(R.id.genreName);
            imgCover =  v.findViewById(R.id.cardImageView);
            numberOfTracks = v.findViewById(R.id.numberOfTracks);
        }

        public void setData(Genre item) {
            genreName.setText(item.getName());
            numberOfTracks.setText(item.getNumberOfTracks() + Constants.SPACE + Constants.SONGS);

            String artUrl = Constants.DRAWABLE_PATH + CommonHelper.generateImgCover();
            ImageSize targetSize = new ImageSize(120, 125);
            ImageLoader.getInstance().displayImage(artUrl, new ImageViewAware(imgCover),
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
                                }
                            });
                        }
                        @Override
                        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                            System.out.println("Error ...");
                        }
                    },null);
        }
    }
}
