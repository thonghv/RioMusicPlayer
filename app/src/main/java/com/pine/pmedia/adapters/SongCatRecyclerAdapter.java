package com.pine.pmedia.adapters;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.pine.pmedia.App;
import com.pine.pmedia.R;
import com.pine.pmedia.activities.AlbumActivity;
import com.pine.pmedia.activities.FilterActivity;
import com.pine.pmedia.activities.PlaySongActivity;
import com.pine.pmedia.control.MediaPlayListDialog;
import com.pine.pmedia.fragments.SuggestFragment;
import com.pine.pmedia.helpers.CommonHelper;
import com.pine.pmedia.helpers.Constants;
import com.pine.pmedia.helpers.MediaHelper;
import com.pine.pmedia.models.Filter;
import com.pine.pmedia.models.Song;
import com.pine.pmedia.services.MusicService;
import com.pine.pmedia.sqlite.DBManager;

import java.util.ArrayList;

public class SongCatRecyclerAdapter extends RecyclerView.Adapter<SongCatRecyclerAdapter.ViewHolder> {


    private MusicService mService;
    private DBManager dbManager;

    private ArrayList songs;
    private Context mContext;
    private Intent playIntent;
    private int viewType;
    private BottomSheetDialog dialog;
    private long targetIdTemp;
    private String targetNameTemp;
    private long songCurrentIdTemp;
    private MediaPlayListDialog mediaPlayListDialog;

    private TextView headerSheetDialog;

    public SongCatRecyclerAdapter(Context context, ArrayList values, int viewType) {

        this.songs = values;
        this.mContext = context;
        this.viewType = viewType;

        this.dbManager = new DBManager(context);
        this.dbManager.open();
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
        if(mService != null) {
            playIntent = new Intent(mContext, MusicService.class);
            mContext.bindService(playIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        }
    }

    public void updateData(ArrayList data) {
        this.songs = data;
    }

    public void setSongCurrentIdTemp(long songCurrentIdTemp) {
        this.songCurrentIdTemp = songCurrentIdTemp;
    }

    public void setMediaPlayListDialog(MediaPlayListDialog mediaPlayListDialog) {
        this.mediaPlayListDialog = mediaPlayListDialog;
    }

    @Override
    public int getItemViewType(int position) {
        return viewType;
    }

    @NonNull
    @Override
    public SongCatRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        inItService();

        View view = null;
        switch (viewType) {
            case Constants.VIEW_ALBUM:
            case Constants.VIEW_ARTIST:
            case Constants.VIEW_GENRE:

            case Constants.VIEW_FAVORITE:
            case Constants.VIEW_LAST_PLAYED:
            case Constants.VIEW_RECENT_ADDED:
                view = LayoutInflater.from(mContext).inflate(R.layout.song_custum_item, parent, false);
                break;
            case Constants.VIEW_SUGGEST:
                view = LayoutInflater.from(mContext).inflate(R.layout.play_list_item, parent, false);
                break;
            case Constants.VIEW_PLAYLIST_DIALOG:
                view = LayoutInflater.from(mContext).inflate(R.layout.play_list_item_min, parent, false);
                break;
        }

        return new ViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int position) {
        final Song song = (Song) songs.get(position);
        viewHolder.setData(song);

        // On click to play song
        viewHolder.contentHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle param = new Bundle();
                Intent intent;
                switch (viewType) {

                    case Constants.VIEW_ALBUM:
                    case Constants.VIEW_ARTIST:
                    case Constants.VIEW_GENRE:
                        intent = new Intent(v.getContext(), AlbumActivity.class);
                        param.putLong(Constants.KEY_ID, song.get_id());
                        intent.putExtras(param);
                        mContext.startActivity(intent);
                        break;

                    case Constants.VIEW_SUGGEST:
                        intent = new Intent(v.getContext(), FilterActivity.class);

                        param.putInt(Constants.KEY_CAT_TYPE, Constants.VIEW_SUGGEST);

                        param.putString(Constants.KEY_TITLE_CAT, song.get_title());
                        String note = song.get_numberOfTrack() + Constants.SPACE + Constants.SONGS
                                + Constants.MINUS + CommonHelper.toFormatTime(0);
                        param.putString(Constants.KEY_NOTE_CAT, note);
                        param.putLong(Constants.KEY_ID, song.get_id());
                        intent.putExtras(param);
                        mContext.startActivity(intent);
                        break;

                    case Constants.VIEW_PLAYLIST_DIALOG:
                        MediaHelper.addToPlaylist(mContext, songCurrentIdTemp, song.get_id());
                        mediaPlayListDialog.dismiss();
                        Toast.makeText(mContext, R.string.addSongInPlayListSuccess, Toast.LENGTH_SHORT).show();
                        App.getInstance().isReloadPlayList = true;
                        break;

                    case Constants.VIEW_FAVORITE:
                    case Constants.VIEW_LAST_PLAYED:
                    case Constants.VIEW_RECENT_ADDED:
                        new SongCatRecyclerAdapter.onProcessStartPlay(position).execute();
                        Bundle bundle = new Bundle();
                        intent = new Intent(v.getContext(), PlaySongActivity.class);
                        intent.putExtras(bundle);
                        mContext.startActivity(intent);
                        break;
                }
            }
        });

        // On click show bottom sheet more
        viewHolder.moreRowControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (viewType) {
                    case Constants.VIEW_ALBUM:
                    case Constants.VIEW_ARTIST:
                    case Constants.VIEW_GENRE:

                        break;

                    case Constants.VIEW_RECENT_ADDED:
                    case Constants.VIEW_SUGGEST:
                    case Constants.VIEW_PLAYLIST_DIALOG:
                        onShowBottomSheet(song.get_id(), song.get_title());
                        break;
                    case Constants.VIEW_LAST_PLAYED:
                        onShowBottomSheet(song.get_historyId(), song.get_title());
                        break;
                    case Constants.VIEW_FAVORITE:
                        onShowBottomSheet(song.get_favoriteId(), song.get_title());
                        break;
                }
            }
        });
    }

    /**
     * Handler lick to play song
     */
    private class onProcessStartPlay extends AsyncTask<String, Void, String> {

        private int position;

        public onProcessStartPlay(int position) {
            this.position = position;
        }
        protected String doInBackground(String... params){
            return "";
        }

        protected void onPostExecute(String response) {

            mService.setPlayingQueue(songs);
            mService.checkAndResetPlay();

            Bundle bundle = new Bundle();
            bundle.putInt(Constants.KEY_POSITION, position);
            mService.onProcess(Constants.PLAYPAUSE, bundle);
        }
    }

    //
    //=======================
    // START BOTTOM SHEET
    private void onShowBottomSheet(long targetIdTemp, String targetNameTemp) {

        this.targetIdTemp = targetIdTemp;
        this.targetNameTemp = targetNameTemp;

        LayoutInflater li = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = null;
        switch (this.viewType) {
            case Constants.VIEW_SUGGEST:
                view = li.inflate(R.layout.bottom_dialog_playlist, null);
                onHandleActionBDialogPLayList(view);
                break;
            case Constants.VIEW_FAVORITE:
                view = li.inflate(R.layout.bottom_dialog_favorite,null);
                onHandleActionBDialogFavorite(view);
                break;
            case Constants.VIEW_LAST_PLAYED:
                view = li.inflate(R.layout.bottom_dialog_history,null);
                onHandleActionBDialogHistory(view);
                break;
        }

        headerSheetDialog = view.findViewById(R.id.headerSheetDialog);
        headerSheetDialog.setText(targetNameTemp);

        onCalColorComponentControl(view);

        dialog = new BottomSheetDialog(mContext);
        dialog.setContentView(view);
        dialog.show();
    }

    private void onCalColorComponentControl(View v) {

//        LinearLayout sheetDialog = v.findViewById(R.id.bottomDialogPlayListLayout);

//        Bitmap loadedImage = CommonHelper.drawableToBitmap(mContext.getResources().getDrawable(R.color.p_background_01));
//        Palette p = Palette.from(loadedImage).generate();
//        int color = p.getDarkVibrantColor(ContextCompat.getColor(mContext, R.color.p_background_01));
//        sheetDialog.setBackgroundColor(color);
//
//        int colorR = CommonHelper.manipulateColor(color, 1.3f);
        int colorR = mContext.getResources().getColor(R.color.p_background_04);

        View r01 = v.findViewById(R.id.r_01);
        View r02 = v.findViewById(R.id.r_02);
        View r03 = v.findViewById(R.id.r_03);
        View r04 = v.findViewById(R.id.r_04);
        View r05 = v.findViewById(R.id.r_05);
        View r06 = v.findViewById(R.id.r_06);
        View r07 = v.findViewById(R.id.r_07);

        r01.setBackgroundColor(colorR);
        r02.setBackgroundColor(colorR);
        r03.setBackgroundColor(colorR);
        r04.setBackgroundColor(colorR);

        if(r05 != null) {
            r05.setBackgroundColor(colorR);
        }

        if(r06 != null) {
            r06.setBackgroundColor(colorR);
        }

        if(r07 != null) {
            r07.setBackgroundColor(colorR);
        }
    }

    private void onHandleActionBDialogFavorite(View v) {

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

        LinearLayout setRingToneControl = v.findViewById(R.id.setRingToneControl);
        setRingToneControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        LinearLayout shareControl = v.findViewById(R.id.shareControl);
        shareControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        LinearLayout deleteControl = v.findViewById(R.id.deleteControl);
        deleteControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.hide();
                onOpenDialogConfirm(Constants.VIEW_FAVORITE,
                        R.string.titleDeleteTrack, R.string.messageDeleteFromList);
            }
        });
    }

    private void onHandleActionBDialogHistory(View v) {

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

        LinearLayout setRingToneControl = v.findViewById(R.id.setRingToneControl);
        setRingToneControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        LinearLayout shareControl = v.findViewById(R.id.shareControl);
        shareControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        LinearLayout deleteControl = v.findViewById(R.id.deleteControl);
        deleteControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.hide();
                onOpenDialogConfirm(Constants.VIEW_FAVORITE,
                        R.string.titleDeleteTrack, R.string.messageDeleteFromList);
            }
        });
    }

    private void onHandleActionBDialogPLayList(View v) {

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
                CommonHelper.showPlayListDialog(mContext, SuggestFragment.getInstance(),
                        Constants.UPDATE_DIALOG, targetNameTemp, targetIdTemp);
                onReloadData();
            }
        });

        LinearLayout deleteControl = v.findViewById(R.id.deleteControl);
        deleteControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.hide();
                onOpenDialogConfirm(Constants.VIEW_SUGGEST,
                        R.string.titleDeletePlayList, R.string.messageDeletePlayList);
            }
        });
    }

    private void onOpenDialogConfirm(final int actionType, int title, int message){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (actionType) {
                    case Constants.VIEW_SUGGEST:
                        MediaHelper.deletePlaylist(mContext, targetIdTemp);
                        break;
                    case Constants.VIEW_FAVORITE:
                        dbManager.deleteFavorite(targetIdTemp);
                        App.getInstance().isReloadFavorite = true;
                        break;
                }

                // Reload data at current screen.
                onReloadData();
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

    /**
     * Reload data at current screen.
     */
    private void onReloadData() {
        notifyDataSetChanged();
        switch (this.viewType) {
            case Constants.VIEW_SUGGEST:
                songs = MediaHelper.getAllPLayList(mContext);
                break;
            case Constants.VIEW_FAVORITE:
                Filter filter = MediaHelper.getFavorites(dbManager, App.getInstance().getMediaPlayList());
                songs = filter.getSongs();
                ((FilterActivity)mContext).onUpdateNoteFilter(songs.size(), filter.getTotalDuration());
                break;
        }
    }

    //=======================
    // END BOTTOM SHEET
    //=======================

    @Override
    public int getItemCount() {

        return songs.size();
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
                case Constants.VIEW_FAVORITE:
                case Constants.VIEW_LAST_PLAYED:
                case Constants.VIEW_RECENT_ADDED:

                    trackTitle = itemView.findViewById(R.id.trackTitle);
                    trackImage = itemView.findViewById(R.id.trackImage);
                    trackDuration = itemView.findViewById(R.id.trackDuration);
                    trackArtist = itemView.findViewById(R.id.trackArtist);
                    contentHolder = itemView.findViewById(R.id.songLayout);
                    moreRowControl = itemView.findViewById(R.id.moreRowControl);

                    break;
                case Constants.VIEW_SUGGEST:
                case Constants.VIEW_PLAYLIST_DIALOG:

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
                case Constants.VIEW_FAVORITE:
                case Constants.VIEW_LAST_PLAYED:
                case Constants.VIEW_RECENT_ADDED:

                    trackTitle.setText(song.get_title());
                    trackDuration.setText(CommonHelper.toFormatTime(song.get_duration()));
                    trackArtist.setText(song.get_artist());

                    Drawable drawable = mContext.getResources().getDrawable(R.drawable.ic_music_note_white);
                    trackImage.setBackground(drawable);
                    break;
                case Constants.VIEW_SUGGEST:
                case Constants.VIEW_PLAYLIST_DIALOG:
                    trackTitle.setText(song.get_title());
                    trackArtist.setText(song.get_numberOfTrack() + Constants.SPACE + Constants.SONGS);
                    break;
            }
        }
    }
}
