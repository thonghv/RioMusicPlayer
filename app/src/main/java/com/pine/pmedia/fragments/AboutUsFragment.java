package com.pine.pmedia.fragments;


import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pine.pmedia.R;
import com.pine.pmedia.adapters.AlbumRecyclerAdapter;
import com.pine.pmedia.models.Album;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class AboutUsFragment extends BaseFragment {

    RecyclerView recyclerView;
    ArrayList<Album> arrayList = new ArrayList<>();

    private static AboutUsFragment instance;

    public AboutUsFragment() {
        // Required empty public constructor
    }

    public static AboutUsFragment getInstance() {

        if(instance == null) {
            instance = new AboutUsFragment();
        }

        return new AboutUsFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        super.setmActivity((Activity) context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_about_us, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.albumContent);

        ArrayList<Album> arrayList = getAlbums();

//        arrayList.add(new Album("Item 1", "Artist 1", "#09A9FF"));
//        arrayList.add(new Album("Item 2", "Artist 2", "#09A9FF"));
//        arrayList.add(new Album("Item 3", "Artist 3", "#09A9FF"));
//        arrayList.add(new Album("Item 4", "Artist 4", "#09A9FF"));
//        arrayList.add(new Album("Item 5", "Artist 5", "#09A9FF"));

        AlbumRecyclerAdapter adapter = new AlbumRecyclerAdapter(super.getActivity(), arrayList);
        recyclerView.setAdapter(adapter);

        GridLayoutManager manager = new GridLayoutManager(super.getContext(), 3, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);

        return view;
    }

    private ArrayList<Album> getAlbums() {

        String[] projection = new String[] {
                MediaStore.Audio.Albums._ID,
                MediaStore.Audio.Albums.ALBUM,
                MediaStore.Audio.Albums.ARTIST,
                MediaStore.Audio.Albums.ALBUM_ART,
                MediaStore.Audio.Albums.NUMBER_OF_SONGS };
        String selection = null;
        String[] selectionArgs = null;
        String sortOrder = MediaStore.Audio.Media.ALBUM + " ASC";
        Cursor cursor =  getmActivity().getContentResolver().query(
                MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, projection, selection, selectionArgs, sortOrder);

        ArrayList<Album> albums = new ArrayList<>();
        while (cursor.moveToNext()) {
            String artist = cursor.getString(cursor
                    .getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
            String album = cursor.getString(cursor
                    .getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM));
            Long albumId = cursor.getLong(cursor
                    .getColumnIndexOrThrow(MediaStore.Audio.Media._ID));

            String albumArt = cursor.getString(cursor
                    .getColumnIndexOrThrow(MediaStore.Audio.Albums.ALBUM_ART));

            int numberOfSong = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.NUMBER_OF_SONGS));


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

            Album albumObject = new Album();
            albumObject.setName(album);
            albumObject.setArtist(artist);
            albumObject.setImgCover(bitmap);
            albumObject.setAlbumArt(albumArt);
            albumObject.setNumberOfSong(numberOfSong);

            albums.add(albumObject);
        }

        return albums;
    }

}
