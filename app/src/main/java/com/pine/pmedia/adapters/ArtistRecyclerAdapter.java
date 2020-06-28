package com.pine.pmedia.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.Typeface;
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

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.pine.pmedia.App;
import com.pine.pmedia.R;
import com.pine.pmedia.activities.ArtistActivity;
import com.pine.pmedia.helpers.CommonHelper;
import com.pine.pmedia.helpers.Constants;
import com.pine.pmedia.helpers.ExecuteProcessStartPlay;
import com.pine.pmedia.helpers.MediaHelper;
import com.pine.pmedia.models.Artist;
import com.pine.pmedia.models.Song;
import com.pine.pmedia.services.MusicService;
import com.pine.pmedia.sqlite.DBManager;

import java.util.ArrayList;
import java.util.List;

public class ArtistRecyclerAdapter extends RecyclerView.Adapter<ArtistRecyclerAdapter.MyViewHolder>  {

    private ArrayList<Artist> artists;
    private Activity activity;
    private DBManager dbManager;
    private Intent playIntent;
    private MusicService mService;
    private ImageLoader imageLoader;

    private BottomSheetDialog bottomSheetdialog;
    private long artistIdTemp;
    private int numberOfSongsTemp;
    private int positionTemp;

    private long targetIdTemp;
    private String targetNameTemp;
    private long songCurrentIdTemp;
    private TextView headerSheetDialog;

    public ArtistRecyclerAdapter(ArrayList<Artist> artists, Activity activity) {
        this.artists = artists;
        this.activity = activity;

        this.dbManager = new DBManager(activity);
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
        playIntent = new Intent(activity, MusicService.class);
        activity.bindService(playIntent, serviceConnection, Context.BIND_AUTO_CREATE);
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

        final Artist artist = artists.get(position);

        holder.artistName.setText(artist.getName());
        Typeface customFace = Typeface.createFromAsset(activity.getAssets(), Constants.FONT_ROBOTO_LIGHT);
        holder.artistName.setTypeface(customFace);

        String artistInfo = artist.getNumberOfAlbums() + Constants.SPACE + Constants.ALBUMS
                + " - " + artist.getNumberOfTracks() + Constants.SPACE + Constants.SONGS;
        holder.artistNumberOfSong.setText(artistInfo);

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

                activity.startActivity(intent);
            }
        });

        // On click show bottom sheet more
        holder.moreRowControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                numberOfSongsTemp = artist.getNumberOfTracks();
                positionTemp = position;
                onShowBottomSheet(artist.getId(), artist.getId(), artist.getName());
            }
        });
    }

    //
    //=======================
    // START BOTTOM SHEET
    private void onShowBottomSheet(long targetIdTemp, long artistId, String targetNameTemp) {

        this.targetIdTemp = targetIdTemp;
        this.targetNameTemp = targetNameTemp;
        this.artistIdTemp = artistId;

        LayoutInflater li = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view =  li.inflate(R.layout.bottom_dialog_artist, null);
        onHandleActionBDialog(view);

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
    private void onHandleActionBDialog(View v) {

        LinearLayout playControl = v.findViewById(R.id.playControl);
        playControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetdialog.hide();
                ArrayList<Song> songs = MediaHelper.getSongs(activity, 0L, artistIdTemp);
                new ExecuteProcessStartPlay(activity, dbManager, App.getInstance().getMService(),
                        songs, 0, -1).execute();
            }
        });

        LinearLayout addToPlayListControl = v.findViewById(R.id.addToPlayListControl);
        addToPlayListControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetdialog.hide();
                ArrayList<Song> songs = MediaHelper.getSongs(activity, 0L, artistIdTemp);
                List<Long> songIds = new ArrayList<>();
                for(Song s : songs) {
                    songIds.add(s.get_id());
                }
                CommonHelper.onShowMediaPlayListDialog(activity, songIds);
            }
        });

        LinearLayout deleteControl = v.findViewById(R.id.deleteControl);
        deleteControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetdialog.hide();
                if(numberOfSongsTemp > 1) {
                    onOpenDialogConfirm(R.string.titleDeleteAlbum, R.string.messageDeleteAlbumTheseSongs);
                } else {
                    onOpenDialogConfirm(R.string.titleDeleteAlbum, R.string.messageDeleteAlbumThisSongs);
                }
            }
        });
    }

    private void onOpenDialogConfirm(int title, int message){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO: delete all song of artist
                artists.remove(positionTemp);
                notifyDataSetChanged();
                App.getInstance().isReloadSongs = true;

                new ExecuteRemoveAllSongArtist(activity).execute();
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
     * Process remove all songs of artist
     */
    private class ExecuteRemoveAllSongArtist extends AsyncTask<String, Void, Activity> {

        private Activity activity;

        public ExecuteRemoveAllSongArtist(Activity activity) {
            this.activity = activity;
        }

        @Override
        protected Activity doInBackground(String... strings) {
            ArrayList<Song> songs = MediaHelper.getSongs(activity, 0L, artistIdTemp);
            List<Long> songIds = new ArrayList<>();
            for(Song s : songs) {
                songIds.add(s.get_id());
            }

            MediaHelper.deleteSongs(activity, songIds);
            return null;
        }

        @Override
        protected void onPostExecute(Activity activity) {
        }
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
        return artists == null ? 0 : artists.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView artistName;
        public ImageView artistAvatar;
        public TextView artistNumberOfSong;
        public RelativeLayout contentHolder;
        public LinearLayout moreRowControl;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            artistName = itemView.findViewById(R.id.artistName);
            artistAvatar = itemView.findViewById(R.id.artistAvatar);
            artistNumberOfSong = itemView.findViewById(R.id.artistNumberOfSong);
            contentHolder = itemView.findViewById(R.id.artistItemRow);
            moreRowControl = itemView.findViewById(R.id.moreRowControl);
        }
    }
}
