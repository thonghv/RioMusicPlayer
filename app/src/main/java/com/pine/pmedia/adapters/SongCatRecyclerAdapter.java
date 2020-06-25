package com.pine.pmedia.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.pine.pmedia.App;
import com.pine.pmedia.R;
import com.pine.pmedia.activities.FilterActivity;
import com.pine.pmedia.control.DetailSongDialog;
import com.pine.pmedia.control.MediaPlayListDialog;
import com.pine.pmedia.fragments.SuggestFragment;
import com.pine.pmedia.helpers.CommonHelper;
import com.pine.pmedia.helpers.Constants;
import com.pine.pmedia.helpers.ExecuteProcessStartPlay;
import com.pine.pmedia.helpers.MediaHelper;
import com.pine.pmedia.models.Filter;
import com.pine.pmedia.models.Song;
import com.pine.pmedia.sqlite.DBManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SongCatRecyclerAdapter extends RecyclerView.Adapter<SongCatRecyclerAdapter.ViewHolder> {

    private DBManager dbManager;
    private ArrayList songs;
    private Activity activity;
    private Intent playIntent;
    private int viewType;
    private BottomSheetDialog bottomSheetdialog;
    private long songId;
    private long targetIdTemp;
    private String targetNameTemp;
    private long songCurrentIdTemp;
    private List<Long> listSongCurrentIdTemp;
    private MediaPlayListDialog mediaPlayListDialog;
    private TextView headerSheetDialog;
    private long playListIdTemp;

    public SongCatRecyclerAdapter(Activity activity, ArrayList values, int viewType) {

        this.songs = values;
        this.activity = activity;
        this.viewType = viewType;

        this.dbManager = new DBManager(activity);
        this.dbManager.open();
    }

    public void updateData(ArrayList data) {
        this.songs = data;
    }

    public void setSongCurrentIdTemp(long songCurrentIdTemp) {
        this.songCurrentIdTemp = songCurrentIdTemp;
    }

    public void setListSongCurrentIdTemp(List<Long> listSongCurrentIdTemp) {
        this.listSongCurrentIdTemp = listSongCurrentIdTemp;
    }

    public void setMediaPlayListDialog(MediaPlayListDialog mediaPlayListDialog) {
        this.mediaPlayListDialog = mediaPlayListDialog;
    }

    public long getPlayListIdTemp() {
        return playListIdTemp;
    }

    public void setPlayListIdTemp(long playListIdTemp) {
        this.playListIdTemp = playListIdTemp;
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

            case Constants.VIEW_FAVORITE:
            case Constants.VIEW_LAST_PLAYED:
            case Constants.VIEW_RECENT_ADDED:
            case Constants.VIEW_PLAYLIST:
                view = LayoutInflater.from(activity).inflate(R.layout.song_custum_item, parent, false);
                break;
            case Constants.VIEW_SUGGEST:
                view = LayoutInflater.from(activity).inflate(R.layout.play_list_item, parent, false);
                break;
            case Constants.VIEW_PLAYLIST_DIALOG:
                view = LayoutInflater.from(activity).inflate(R.layout.play_list_item_min, parent, false);
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
                    case Constants.VIEW_PLAYLIST:
                        new ExecuteProcessStartPlay(activity, dbManager, App.getInstance().getMService(),
                                songs, position, viewType).execute();
                        break;

                    case Constants.VIEW_SUGGEST:
                        intent = new Intent(v.getContext(), FilterActivity.class);
                        param.putInt(Constants.KEY_CAT_TYPE, Constants.VIEW_SUGGEST);
                        param.putString(Constants.KEY_TITLE_CAT, song.get_title());
                        param.putLong(Constants.KEY_ID, song.get_id());
                        intent.putExtras(param);
                        activity.startActivity(intent);
                        break;

                    case Constants.VIEW_PLAYLIST_DIALOG:
                        // ID of playlist: song.get_id()
                        MediaHelper.addToPlaylist(activity, listSongCurrentIdTemp, song.get_id());
                        mediaPlayListDialog.dismiss();
                        Toast.makeText(activity, R.string.addSongInPlayListSuccess, Toast.LENGTH_SHORT).show();
                        App.getInstance().isReloadPlayList = true;
                        break;

                    case Constants.VIEW_FAVORITE:
                    case Constants.VIEW_LAST_PLAYED:
                    case Constants.VIEW_RECENT_ADDED:
                        new ExecuteProcessStartPlay(activity, dbManager,
                                App.getInstance().getMService(), songs, position, viewType).execute();
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
                        onShowBottomSheet(song.get_id(), -1L, song.get_title());
                        break;
                    case Constants.VIEW_GENRE:
                        break;
                    case Constants.VIEW_PLAYLIST:
                        onShowBottomSheet(song.get_audioInPlayListId(), song.get_id(), song.get_title());
                        break;
                    case Constants.VIEW_SUGGEST:
                    case Constants.VIEW_PLAYLIST_DIALOG:
                        onShowBottomSheet(song.get_id(), -1L, song.get_title());
                        break;
                    case Constants.VIEW_LAST_PLAYED:
                        onShowBottomSheet(song.get_historyId(), song.get_id(), song.get_title());
                        break;
                    case Constants.VIEW_FAVORITE:
                        onShowBottomSheet(song.get_favoriteId(), song.get_id(), song.get_title());
                        break;
                    case Constants.VIEW_RECENT_ADDED:
                        onShowBottomSheet(song.get_recentId(), song.get_id(), song.get_title());
                        break;
                }
            }
        });
    }

    //
    //=======================
    // START BOTTOM SHEET
    private void onShowBottomSheet(long targetIdTemp, long songId, String targetNameTemp) {

        this.targetIdTemp = targetIdTemp;
        this.targetNameTemp = targetNameTemp;
        this.songId = songId;

        LayoutInflater li = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = null;
        switch (this.viewType) {
            case Constants.VIEW_ARTIST:
                view = li.inflate(R.layout.bottom_dialog_favorite, null);
                onHandleActionBDialogFavorite(view);
                break;
            case Constants.VIEW_ALBUM:
                view = li.inflate(R.layout.bottom_dialog_favorite, null);
                onHandleActionBDialogFavorite(view);
                break;
            case Constants.VIEW_SUGGEST:
                view = li.inflate(R.layout.bottom_dialog_playlist, null);
                onHandleActionBDialogPLayList(view);
                break;
            case Constants.VIEW_PLAYLIST:
            case Constants.VIEW_FAVORITE:
                view = li.inflate(R.layout.bottom_dialog_favorite,null);
                onHandleActionBDialogFavorite(view);
                break;
            case Constants.VIEW_RECENT_ADDED:
            case Constants.VIEW_LAST_PLAYED:
                view = li.inflate(R.layout.bottom_dialog_history,null);
                onHandleActionBDialogHistory(view);
                break;
        }

        headerSheetDialog = view.findViewById(R.id.headerSheetDialog);
        headerSheetDialog.setText(targetNameTemp);

        CommonHelper.onCalColorBottomSheetDialog(activity, view);

        bottomSheetdialog = new BottomSheetDialog(activity);
        bottomSheetdialog.setContentView(view);
        bottomSheetdialog.show();
    }

    /**
     * Handler action for bottom sheet bottom sheet dialog list favorite song
     * @param v
     */
    private void onHandleActionBDialogFavorite(View v) {

        LinearLayout playNextControl = v.findViewById(R.id.playNextControl);
        playNextControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetdialog.hide();
                CommonHelper.addPlayNext(dbManager, activity, songs, songId);
            }
        });

        LinearLayout addToPlayListControl = v.findViewById(R.id.addToPlayListControl);
        addToPlayListControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetdialog.hide();
                CommonHelper.onShowMediaPlayListDialog(activity, Arrays.asList(songId));
            }
        });

        LinearLayout viewDetailControl = v.findViewById(R.id.viewDetailControl);
        viewDetailControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetdialog.hide();
                onShowDetailSong();
            }
        });

        LinearLayout setRingToneControl = v.findViewById(R.id.setRingToneControl);
        setRingToneControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Song songFind = MediaHelper.getById(App.getInstance().getMediaPlayList(), songId);
                CommonHelper.setRingTone(activity, songFind.get_path());
            }
        });

        LinearLayout shareControl = v.findViewById(R.id.shareControl);
        shareControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Song songFind = MediaHelper.getById(App.getInstance().getMediaPlayList(), songId);
                CommonHelper.onShare(activity, activity.getResources().getString(R.string.action_settings), songFind.get_path());
                bottomSheetdialog.hide();
            }
        });

        LinearLayout deleteControl = v.findViewById(R.id.deleteControl);
        deleteControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetdialog.hide();
                onOpenDialogConfirm(R.string.titleDeleteTrack, R.string.messageDeleteFromList);
            }
        });
    }

    /**
     * Handler action for bottom sheet bottomSheetdialog list last play song
     * @param v
     */
    private void onHandleActionBDialogHistory(View v) {

        LinearLayout playNextControl = v.findViewById(R.id.playNextControl);
        playNextControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetdialog.hide();
                CommonHelper.addPlayNext(dbManager, activity, songs, songId);
            }
        });

        LinearLayout addPlayNextControl = v.findViewById(R.id.addToPlayListControl);
        addPlayNextControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetdialog.hide();
                CommonHelper.onShowMediaPlayListDialog(activity, Arrays.asList(songId));
            }
        });

        LinearLayout addFavoriteControl = v.findViewById(R.id.addFavoriteControl);
        addFavoriteControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetdialog.hide();
                CommonHelper.onFavorite(activity, dbManager, songId, targetNameTemp);
            }
        });

        LinearLayout viewDetailControl = v.findViewById(R.id.viewDetailControl);
        viewDetailControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetdialog.hide();
                onShowDetailSong();
            }
        });

        LinearLayout setRingToneControl = v.findViewById(R.id.setRingToneControl);
        setRingToneControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Song songFind = MediaHelper.getById(App.getInstance().getMediaPlayList(), songId);
                CommonHelper.setRingTone(activity, songFind.get_path());
            }
        });

        LinearLayout shareControl = v.findViewById(R.id.shareControl);
        shareControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Song songFind = MediaHelper.getById(App.getInstance().getMediaPlayList(), songId);
                CommonHelper.onShare(activity, activity.getResources().getString(R.string.action_settings), songFind.get_path());
                bottomSheetdialog.hide();
            }
        });

        LinearLayout deleteControl = v.findViewById(R.id.deleteControl);
        deleteControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetdialog.hide();
                onOpenDialogConfirm(R.string.titleDeleteTrack, R.string.messageDeleteFromList);
            }
        });
    }

    /**
     * Handler action for bottom sheet bottom sheet dialog playlist
     * @param v
     */
    private void onHandleActionBDialogPLayList(View v) {

        LinearLayout updateControl = v.findViewById(R.id.updateControl);
        updateControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetdialog.hide();
                CommonHelper.showPlayListDialog(activity, SuggestFragment.getInstance(),
                        Constants.UPDATE_DIALOG, targetNameTemp, targetIdTemp);
                onReloadDataUI();
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

    private void onShowDetailSong(){

        Song songFind = MediaHelper.getSongById(songs, songId);
        if(songFind == null) {
            Toast.makeText(activity, R.string.errorPleaseAgain, Toast.LENGTH_SHORT).show();
            return;
        }

        DetailSongDialog detailSongDialog = new DetailSongDialog(songFind.get_title(), songFind.get_artist(),
                songFind.get_album(), CommonHelper.toFormatTimeMS(songFind.get_duration()),
                CommonHelper.toFormatSize(songFind.get_size()), songFind.get_path());
        FragmentActivity fragmentActivity = (FragmentActivity) activity;
        FragmentManager fm = fragmentActivity.getSupportFragmentManager();
        detailSongDialog.show(fm, Constants.PLAY_LIST_DIALOG_NAME);
    }

    private void onOpenDialogConfirm(int title, int message){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (viewType) {
                    case Constants.VIEW_SUGGEST:
                        MediaHelper.deletePlaylist(activity, targetIdTemp);
                        break;
                    case Constants.VIEW_FAVORITE:
                        dbManager.deleteFavoriteById(targetIdTemp);
                        App.getInstance().isReloadFavorite = true;
                        break;
                    case Constants.VIEW_LAST_PLAYED:
                        dbManager.deleteHistoryById(targetIdTemp);
                        App.getInstance().isReloadLastPlayed = true;
                        break;
                    case Constants.VIEW_RECENT_ADDED:
                        dbManager.deleteRecentById(targetIdTemp);
                        App.getInstance().isReloadRecentAdd = true;
                        break;
                    case Constants.VIEW_PLAYLIST:
                        MediaHelper.removeFromPlaylist(activity, targetIdTemp, playListIdTemp);
                        App.getInstance().isReloadPlayList = true;
                        break;
                }

                // Reload data at current screen.
                onReloadDataUI();
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
    private void onReloadDataUI() {
        notifyDataSetChanged();
        Filter filter = null;
        switch (this.viewType) {
            case Constants.VIEW_SUGGEST:
                songs = MediaHelper.getAllPLayList(activity);
                return;
            case Constants.VIEW_FAVORITE:
                filter = MediaHelper.getFavorites(dbManager, App.getInstance().getMediaPlayList());
                songs = filter.getSongs();
                break;
            case Constants.VIEW_LAST_PLAYED:
                filter = MediaHelper.getHistories(dbManager, App.getInstance().getMediaPlayList());
                songs = filter.getSongs();
                break;
            case Constants.VIEW_RECENT_ADDED:
                filter = MediaHelper.getRecent(dbManager, App.getInstance().getMediaPlayList());
                songs = filter.getSongs();
                break;
            case Constants.VIEW_PLAYLIST:
                filter = MediaHelper.getSongsByPlayListId(activity, playListIdTemp);
                songs = filter.getSongs();
                break;
        }

        ((FilterActivity)activity).onUpdateNoteFilter(songs.size(), filter.getTotalDuration());
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
                case Constants.VIEW_PLAYLIST:
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
                case Constants.VIEW_PLAYLIST:
                    trackTitle.setText(song.get_title());
                    trackDuration.setText(CommonHelper.toFormatTimeMS(song.get_duration()));
                    trackArtist.setText(song.get_artist());

                    Drawable drawable = activity.getResources().getDrawable(R.drawable.ic_music_note_white);
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
