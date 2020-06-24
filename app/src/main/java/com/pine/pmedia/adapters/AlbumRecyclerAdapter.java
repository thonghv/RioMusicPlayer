package com.pine.pmedia.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
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
    private int viewType;

    private BottomSheetDialog bottomSheetdialog;
    private long songId;
    private long targetIdTemp;
    private String targetNameTemp;
    private long songCurrentIdTemp;
    private TextView headerSheetDialog;

    public AlbumRecyclerAdapter(Context context, ArrayList values, int viewType) {

        this.mValues = values;
        this.mContext = context;
        this.viewType = viewType;
    }

    @Override
    public int getItemViewType(int position) {
        return viewType;
    }

    @Override
    public AlbumRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = null;
        switch (viewType) {
            case Constants.VIEW_ALBUM:
                view = LayoutInflater.from(mContext).inflate(R.layout.album_card_item, parent, false);

                // Clear background of card view
                CardView cardView = view.findViewById(R.id.cardView);
                cardView.setBackgroundResource(0);

                break;
            case Constants.VIEW_ARTIST:
                view = LayoutInflater.from(mContext).inflate(R.layout.album_card_item_min, parent, false);
                break;
        }

        return new ViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        final Album album = mValues.get(position);

        if(viewType == Constants.VIEW_ARTIST) {
            viewHolder.setDataAlbumArtist(album);
        }

        if(viewType == Constants.VIEW_ALBUM) {
            viewHolder.setData(album);
            viewHolder.albumCardItemLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Bundle param = new Bundle();
                    param.putInt(Constants.KEY_ID, album.getId());
                    param.putString(Constants.KEY_NAME, album.getName());
                    param.putString(Constants.KEY_ARTWORK, album.getArtUri());
                    param.putString(Constants.KEY_ARTIST, album.getArtist());
                    param.putInt(Constants.KEY_NUMBER_OF_TRACK, album.getNumberOfSong());

                    Intent intent = new Intent(v.getContext(), AlbumActivity.class);
                    intent.putExtras(param);

                    mContext.startActivity(intent);
                }
            });

            // On click show bottom sheet more
            viewHolder.moreRowControl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onShowBottomSheet(album.getId(), album.getId(), album.getName());
                }
            });
        }

    }

    //
    //=======================
    // START BOTTOM SHEET
    private void onShowBottomSheet(long targetIdTemp, long songId, String targetNameTemp) {

        this.targetIdTemp = targetIdTemp;
        this.targetNameTemp = targetNameTemp;
        this.songId = songId;

        LayoutInflater li = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view =  li.inflate(R.layout.bottom_dialog_album, null);
        onHandleActionBDialog(view);

        headerSheetDialog = view.findViewById(R.id.headerSheetDialog);
        headerSheetDialog.setText(targetNameTemp);

        CommonHelper.onCalColorBottomSheetDialog(mContext, view);

        bottomSheetdialog = new BottomSheetDialog(mContext);
        bottomSheetdialog.setContentView(view);
        bottomSheetdialog.show();
    }

    /**
     * Handler action for bottom sheet bottom sheet dialog album
     * @param v
     */
    private void onHandleActionBDialog(View v) {

        LinearLayout playControl = v.findViewById(R.id.playControl);
        playControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetdialog.hide();
                // TODO:
            }
        });

        LinearLayout addToPlayListControl = v.findViewById(R.id.addToPlayListControl);
        addToPlayListControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetdialog.hide();
                // TODO:
            }
        });

        LinearLayout changeAlbumArtControl = v.findViewById(R.id.changeAlbumArtControl);
        changeAlbumArtControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetdialog.hide();
                // TODO:
            }
        });

        LinearLayout deleteControl = v.findViewById(R.id.deleteControl);
        deleteControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetdialog.hide();
                onOpenDialogConfirm(R.string.titleDeletePlayList, R.string.messageDeletePlayList);
            }
        });
    }

    private void onOpenDialogConfirm(int title, int message){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO: delete artist
            }
        });
        alertDialogBuilder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @Override
    public int getItemCount() {

        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView albumName;
        public TextView albumArtist;
        public TextView numberOfSong;
        public ImageView imgCover;
        public RelativeLayout bottomCardLayout;
        public LinearLayout albumCardItemLayout;
        public LinearLayout moreRowControl;

        public ViewHolder(@NonNull View v, int itemType) {

            super(v);

            switch (itemType) {
                case Constants.VIEW_ALBUM:
                    albumName =  v.findViewById(R.id.cardAlbumName);
                    albumArtist =  v.findViewById(R.id.cardAlbumArtist);
                    imgCover =  v.findViewById(R.id.cardImageView);
                    numberOfSong = v.findViewById(R.id.cardNumberOfSong);
                    bottomCardLayout = v.findViewById(R.id.bottomCardLayout);
                    albumCardItemLayout = v.findViewById(R.id.albumCardItemLayout);
                    moreRowControl = v.findViewById(R.id.moreRowControl);

                    // Set font text
                    Typeface customFace = Typeface.createFromAsset(mContext.getAssets(), Constants.FONT_ROBOTO_REGULAR);
                    albumName.setTypeface(customFace);
                    albumArtist.setTypeface(customFace);
                    break;
                case Constants.VIEW_ARTIST:
                    albumName =  v.findViewById(R.id.cardAlbumName);
                    imgCover =  v.findViewById(R.id.cardImageView);
                    break;
            }
        }

        public void setData(Album item) {
            albumName.setText(item.getName());

            albumArtist.setText(item.getArtist());
            numberOfSong.setText(item.getNumberOfSong() + Constants.SPACE + Constants.SONGS);

           /* String artUrl = Constants.DRAWABLE_PATH + R.drawable.icon_album_custum;
            if(item.getImgCover() != null) {
                artUrl = item.getArtUri();
            }*/

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
                            System.out.println("Error ...");
                        }
                    },null);
        }

        public void setDataAlbumArtist(Album item) {
            albumName.setText(item.getName());

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
                        }
                        @Override
                        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                            System.out.println("Error ...");
                        }
                    },null);
        }
    }
}
