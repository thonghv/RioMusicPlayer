package com.pine.pmedia.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.pine.pmedia.R;
import com.pine.pmedia.activities.AlbumActivity;
import com.pine.pmedia.fragments.SuggestFragment;
import com.pine.pmedia.helpers.CommonHelper;
import com.pine.pmedia.helpers.Constants;
import com.pine.pmedia.helpers.MediaHelper;
import com.pine.pmedia.models.Song;

import java.util.ArrayList;

public class SongCatRecyclerAdapter extends RecyclerView.Adapter<SongCatRecyclerAdapter.ViewHolder> {

    private ArrayList data;
    private Context mContext;
    private int viewType;
    private BottomSheetDialog dialog;
    private long playListIdTemp;

    private TextView headerSheetDialog;

    public SongCatRecyclerAdapter(Context context, ArrayList values, int viewType) {

        this.data = values;
        this.mContext = context;
        this.viewType = viewType;
    }

    public void updateData(ArrayList data) {
        this.data = data;
    }

    @Override
    public int getItemViewType(int position) {
        return viewType;
    }

    @NonNull
    @Override
    public SongCatRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = null;
        switch (viewType) {
            case Constants.VIEW_ALBUM:
            case Constants.VIEW_ARTIST:
            case Constants.VIEW_GENRE:
                view = LayoutInflater.from(mContext).inflate(R.layout.song_custum_item, parent, false);
                break;
            case Constants.VIEW_SUGGEST:
                view = LayoutInflater.from(mContext).inflate(R.layout.play_list_item, parent, false);
                break;
        }

        return new ViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        final Song song = (Song) data.get(position);
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

        viewHolder.moreRowControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (viewType) {
                    case Constants.VIEW_ALBUM:
                    case Constants.VIEW_ARTIST:
                    case Constants.VIEW_GENRE:

                        break;
                    case Constants.VIEW_SUGGEST:
                        onShowBottomSheetDialog(song.get_id(), song.get_title());
                        break;
                }
            }
        });
    }

    //=======================
    // START SUGGEST
    //=======================
    private void onShowBottomSheetDialog(long playListId, String playListName) {

        this.playListIdTemp = playListId;

        LayoutInflater li = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = li.inflate(R.layout.bottom_sheet_dialog, null);

        headerSheetDialog = view.findViewById(R.id.headerSheetDialog);
        headerSheetDialog.setText(playListName);

        onCalColorComponentControl(view);
        onHandleActionControl(view);

        dialog = new BottomSheetDialog(mContext);
        dialog.setContentView(view);
        dialog.show();
    }

    private void onCalColorComponentControl(View v) {

        LinearLayout sheetDialog = v.findViewById(R.id.bottomSheetDialogLayout);

        Bitmap loadedImage = CommonHelper.drawableToBitmap(mContext.getResources().getDrawable(R.color.p_background_01));
        Palette p = Palette.from(loadedImage).generate();
        int color = p.getDarkVibrantColor(ContextCompat.getColor(mContext, R.color.p_background_01));
        sheetDialog.setBackgroundColor(color);

        int colorR = CommonHelper.manipulateColor(color, 1.3f);

        View r01 = v.findViewById(R.id.r_01);
        View r02 = v.findViewById(R.id.r_02);
        View r03 = v.findViewById(R.id.r_03);
        View r04 = v.findViewById(R.id.r_04);

        r01.setBackgroundColor(colorR);
        r02.setBackgroundColor(colorR);
        r03.setBackgroundColor(colorR);
        r04.setBackgroundColor(colorR);

        headerSheetDialog.setTextColor(colorR);
    }

    private void onHandleActionControl(View v) {

        LinearLayout playNextControl = v.findViewById(R.id.playNextControl);
        playNextControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        LinearLayout addPlayNextControl = v.findViewById(R.id.addPlayNextControl);
        addPlayNextControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        LinearLayout updateControl = v.findViewById(R.id.updateControl);
        updateControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.hide();
                CommonHelper.showPlayListDialog(mContext, SuggestFragment.getInstance(), null);
                onReloadData();
            }
        });

        LinearLayout deleteControl = v.findViewById(R.id.deleteControl);
        deleteControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.hide();
                MediaHelper.deletePlaylist(mContext, playListIdTemp);
                onReloadData();
            }
        });
    }

    private void onReloadData() {
        data = MediaHelper.getAllPLayList(mContext);
        notifyDataSetChanged();
    }

    //=======================
    // END SUGGEST
    //=======================


    @Override
    public int getItemCount() {

        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView trackTitle;
        public ImageView trackImage;
        public TextView trackArtist;
        public TextView trackDuration;
        public LinearLayout contentHolder;
        public RelativeLayout moreRowControl;
        public int viewType;

        public ViewHolder(View v, int viewType) {

            super(v);
            this.viewType = viewType;
            switch (viewType) {
                case Constants.VIEW_ALBUM:
                case Constants.VIEW_ARTIST:
                case Constants.VIEW_GENRE:

                    trackTitle = itemView.findViewById(R.id.trackTitle);
                    trackImage = itemView.findViewById(R.id.trackImage);
                    trackDuration = itemView.findViewById(R.id.trackDuration);
                    trackArtist = itemView.findViewById(R.id.trackArtist);
                    contentHolder = itemView.findViewById(R.id.songLayout);

                    break;
                case Constants.VIEW_SUGGEST:

                    trackTitle = itemView.findViewById(R.id.trackTitle);
                    trackArtist = itemView.findViewById(R.id.trackArtist);
                    contentHolder = itemView.findViewById(R.id.songLayout);
                    moreRowControl = itemView.findViewById(R.id.moreRowControl);

                    break;
            }
        }

        public void setData(Song song) {
            switch (viewType) {
                case Constants.VIEW_ALBUM:
                case Constants.VIEW_ARTIST:
                case Constants.VIEW_GENRE:

                    trackTitle.setText(song.get_title());
                    trackDuration.setText(CommonHelper.toFormatTime(song.get_duration()));
                    trackArtist.setText(song.get_artist());

                    Drawable drawable = mContext.getResources().getDrawable(R.drawable.ic_music_note_white);
                    trackImage.setBackground(drawable);
                    break;
                case Constants.VIEW_SUGGEST:
                    trackTitle.setText(song.get_title());
                    trackArtist.setText(song.get_numberOfTrack() + Constants.SPACE + Constants.SONGS);
                    break;
            }
        }
    }
}
