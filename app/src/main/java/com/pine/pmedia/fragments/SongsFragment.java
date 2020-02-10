package com.pine.pmedia.fragments;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pine.pmedia.R;
import com.pine.pmedia.adapters.SongScreenAdapter;
import com.pine.pmedia.helpers.CommonHelper;
import com.pine.pmedia.helpers.MediaHelper;
import com.pine.pmedia.models.Song;
import com.pine.pmedia.services.MusicService;

import java.util.ArrayList;

public class SongsFragment extends BaseFragment {

    private static SongsFragment instance = null;
    private SongScreenAdapter songScreenAdapter;
    private RecyclerView recyclerView;

    public static SongsFragment getInstance() {

        if(instance == null) {
            instance = new SongsFragment();
        }

        return new SongsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

         // callData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_songs, container, false);
        recyclerView = view.findViewById(R.id.recycleViewSongs);

        // Clear background of recycle view
        recyclerView.setBackgroundResource(0);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        super.setmActivity((Activity) context);
    }

    @Override
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new LinearLayoutManager(super.getmActivity()));

        ArrayList<Song> songs = MediaHelper.getSongs(getmActivity());

        songScreenAdapter = new SongScreenAdapter(songs, super.getmActivity());
        recyclerView.setAdapter(songScreenAdapter);

        initData(songs);
    }

    private void initData(ArrayList<Song> songs) {

        MusicService mService = super.getmService();
        if(mService == null) {
            return;
        }

        mService.setmActivity(getmActivity());
        mService.updatePlayingQueue(songs);
    }

    private ArrayList<Song> getSongsFromPhone() {

        ArrayList<Song> results = new ArrayList<>();

        ContentResolver contentResolver = getmActivity().getContentResolver();


        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor songCursor = contentResolver.query(uri, null, null, null, null);
        if(songCursor != null && !songCursor.moveToFirst()) {
            Toast.makeText(getmActivity(), "Not found song ...!", Toast.LENGTH_SHORT).show();
        } else {
            int songId = songCursor.getColumnIndex(MediaStore.Audio.Media._ID);
            int songTitle = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int songArtist = songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int songData = songCursor.getColumnIndex(MediaStore.Audio.Media.DATA);
            int songAvatar = songCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM);

            int albumId = songCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID);

            Cursor cursor = contentResolver.query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                    new String[] {MediaStore.Audio.Albums._ID, MediaStore.Audio.Albums.ALBUM_ART},
                    MediaStore.Audio.Albums._ID+ "=?",
                    new String[] {String.valueOf(albumId)},
                    null);

            String pathImg = "";
            if (cursor.moveToFirst()) {
                pathImg = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART));
                // do whatever you need to do
            }
            System.out.println(pathImg);
            do {
                Long curId = songCursor.getLong(songId);
                String curTitle = songCursor.getString(songTitle);
                String curArtist = songCursor.getString(songArtist);
                String curData = songCursor.getString(songData);
                Long curAlbumId = songCursor.getLong(albumId);

                Song song = new Song();
                results.add(song);

                song.set_id(curId);
                song.set_title(curTitle);

                song.set_artist(curArtist);
                song.set_path(CommonHelper.toUrlPlayTrack(song.get_id()));
                song.set_image(pathImg);

//                results.add(new Song(curId, curTitle, curArtist, curData, 0));
            } while (songCursor.moveToNext());
        }

        return results;
    }
}
