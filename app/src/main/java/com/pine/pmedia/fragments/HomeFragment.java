package com.pine.pmedia.fragments;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
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
import com.pine.pmedia.adapters.HomeScreenAdapter;
import com.pine.pmedia.helpers.CommonHelper;
import com.pine.pmedia.models.Song;
import com.pine.pmedia.services.MusicService;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class HomeFragment extends BaseFragment {

    private static HomeFragment instance;
    private HomeScreenAdapter homeScreenAdapter;
    private RecyclerView recyclerView;

    public static HomeFragment getInstance() {

        if(instance == null) {
            instance = new HomeFragment();
        }

        return new HomeFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

         // callData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home_screen, container, false);
        recyclerView = view.findViewById(R.id.contentMain);
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

        ArrayList<Song> songs = getSongsFromPhone();
        ArrayList<Song> songs2 = initSongs();

        homeScreenAdapter = new HomeScreenAdapter(songs2, super.getmActivity());
        recyclerView.setAdapter(homeScreenAdapter);

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

    private ArrayList<Song> initSongs() {

        ArrayList<Song> results = new ArrayList<>();

        final Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        final String[] cursor_cols = { MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.ALBUM_ID,
                MediaStore.Audio.Media.DURATION };
        final String where = MediaStore.Audio.Media.IS_MUSIC + "=1";
        final Cursor cursor = getmActivity().getContentResolver().query(uri,
                cursor_cols, where, null, null);

        while (cursor.moveToNext()) {
            String artist = cursor.getString(cursor
                    .getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
            String album = cursor.getString(cursor
                    .getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM));
            String track = cursor.getString(cursor
                    .getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
            String data = cursor.getString(cursor
                    .getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
            Long albumId = cursor.getLong(cursor
                    .getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID));

            int duration = cursor.getInt(cursor
                    .getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));

            Uri sArtworkUri = Uri
                    .parse("content://media/external/audio/albumart");
            Uri albumArtUri = ContentUris.withAppendedId(sArtworkUri, albumId);

            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(
                        getmActivity().getContentResolver(), albumArtUri);
//                bitmap = Bitmap.createScaledBitmap(bitmap, 30, 30, true);

            } catch (FileNotFoundException exception) {
                exception.printStackTrace();
            } catch (IOException e) {

                e.printStackTrace();
            }

            Song audioListModel = new Song();
            audioListModel.set_artist(artist);
            audioListModel.setBitmap(bitmap);
            audioListModel.set_title(track);
            audioListModel.set_path(data);
            audioListModel.setAlbumId(albumId);
            audioListModel.set_duration(duration);
            audioListModel.setUri(albumArtUri);

            results.add(audioListModel);

        }

        return results;
    }
}
