package com.pine.pmedia.adapters;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.pine.pmedia.R;
import com.pine.pmedia.activities.PlaySongActivity;
import com.pine.pmedia.helpers.CommonHelper;
import com.pine.pmedia.models.Song;
import com.pine.pmedia.helpers.Constants;
import com.pine.pmedia.services.MusicService;

import java.util.ArrayList;

public class SongRecyclerAdapter extends RecyclerView.Adapter<SongRecyclerAdapter.SongRowHolder>  {

    private ArrayList<Song> songDetails;
    private Context mContext;

    private Intent playIntent;
    private MusicService mService;
    private ImageLoader imageLoader;

    public SongRecyclerAdapter(ArrayList<Song> songDetails, Context mContext) {
        this.songDetails = songDetails;
        this.mContext = mContext;
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
        playIntent = new Intent(mContext, MusicService.class);
        mContext.bindService(playIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @NonNull
    @Override
    public SongRowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        inItService();
        imageLoader = ImageLoader.getInstance();

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.song_custum_item, parent, false);

        return new SongRowHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SongRowHolder holder, final int position) {

        final Song song = songDetails.get(position);
        holder.trackTitle.setText(song.get_title());
        holder.trackDuration.setText(CommonHelper.toFormatTime(song.get_duration()));
        holder.trackArtist.setText(song.get_artist());

//        Typeface customFace = Typeface.createFromAsset(mContext.getAssets(), Constants.FONT_ROBOTO_LIGHT);
//        holder.trackTitle.setTypeface(customFace);
//
        String urlPath;
        if(song.get_bitmap() != null) {
            urlPath = song.get_uri().toString();
        } else {
            urlPath = Constants.DRAWABLE_PATH + R.drawable.icons_musical_white;
        }

        onLoadImageCover(urlPath, holder);

//        if(!song.get_image().isEmpty()) {
////            Picasso.get().load(song.get_image()).into(holder.trackImage)
////            final ImageLoader imageLoader = ImageLoader.getInstance();
////            imageLoader.displayImage(song.getUri().toString(), holder.trackImage);
//            holder.trackImage.setImageBitmap(song.getBitmap());
//        }

        holder.contentHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new onProcessStartPlay(position).execute();

                Bundle bundle = new Bundle();
//                bundle.putLong(Constants.KEY_ID, song.get_id());
//                bundle.putString(Constants.KEY_IMAGE, song.get_image());
//                bundle.putString(Constants.KEY_TITLE, song.get_title());
//                bundle.putString(Constants.KEY_ARTIST, song.get_artist());
//                bundle.putInt(Constants.KEY_POSITION, position);
//                bundle.putInt(Constants.KEY_DURATION, song.get_duration());
//                bundle.putString(Constants.KEY_PATH, toUrlPlayTrack(song.get_id()));
//                bundle.putParcelableArrayList(Constants.KEY_SONG_LIST, songDetails);

                Intent intent = new Intent(v.getContext(), PlaySongActivity.class);
                intent.putExtras(bundle);

                mContext.startActivity(intent);
            }
        });
    }

    private void onLoadImageCover(String imageUri, final SongRowHolder holder) {

        ImageSize targetSize = new ImageSize(48, 48);
        imageLoader.displayImage(imageUri, new ImageViewAware(holder.trackImage),
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

//        imageLoader.loadImage(imageUri, targetSize, DisplayImageOptions.createSimple(), new SimpleImageLoadingListener() {
//            @Override
//            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
//                holder.trackImage.setImageBitmap(loadedImage);
//            }
//        });
    }

    private class onProcessStartPlay extends AsyncTask<String, Void, String> {

        private int position;

        public onProcessStartPlay(int position) {
            this.position = position;
        }
        protected String doInBackground(String... params){
            return "";
        }

        protected void onPostExecute(String response) {
            try {

                Bundle bundle = new Bundle();
                bundle.putInt(Constants.KEY_POSITION, position);
                if(mService.getPlayingQueue().isEmpty()) {
                    mService.updatePlayingQueue(songDetails);
                }

                mService.onProcess(Constants.PLAYPAUSE, bundle);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    @Override
    public int getItemCount() {
        return songDetails == null ? 0 : songDetails.size();
    }

    public class SongRowHolder extends RecyclerView.ViewHolder {

        public TextView trackTitle;
        public ImageView trackImage;
        public TextView trackArtist;
        public TextView trackDuration;
        public ConstraintLayout contentHolder;

        public SongRowHolder(@NonNull View itemView) {
            super(itemView);

            trackTitle = itemView.findViewById(R.id.trackTitle);
            trackImage = itemView.findViewById(R.id.trackImage);
            trackDuration = itemView.findViewById(R.id.trackDuration);
            trackArtist = itemView.findViewById(R.id.trackArtist);
            contentHolder = itemView.findViewById(R.id.contentItemRow);
        }
    }

    public class ControlRowHolder extends RecyclerView.ViewHolder {

        public TextView trackTitle;
        public ImageView trackImage;
        public TextView trackArtist;
        public TextView trackDuration;
        public ConstraintLayout contentHolder;

        public ControlRowHolder(@NonNull View itemView) {
            super(itemView);

            trackTitle = itemView.findViewById(R.id.trackTitle);
            trackImage = itemView.findViewById(R.id.trackImage);
            trackDuration = itemView.findViewById(R.id.trackDuration);
            trackArtist = itemView.findViewById(R.id.trackArtist);
            contentHolder = itemView.findViewById(R.id.contentItemRow);
        }
    }
}
