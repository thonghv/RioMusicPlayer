package com.pine.pmedia.helpers;

import android.app.Activity;
import android.content.ContentUris;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;

import com.pine.pmedia.models.Album;
import com.pine.pmedia.models.Artist;
import com.pine.pmedia.models.Song;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class MediaHelper {

    public static ArrayList<Album> getAlbums(Activity activity, Integer artistIdCompare) {

        String[] projection = new String[] {
                MediaStore.Audio.Albums._ID,
                MediaStore.Audio.Albums.ALBUM,
                MediaStore.Audio.Albums.ARTIST,
                MediaStore.Audio.Albums.ARTIST_ID,
                MediaStore.Audio.Albums.ALBUM_ART,
                MediaStore.Audio.Albums.NUMBER_OF_SONGS };
        String selection = null;
        String[] selectionArgs = null;
        String sortOrder = MediaStore.Audio.Media.ALBUM + " ASC";
        Cursor cursor =  activity.getContentResolver().query(
                MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, projection, selection, selectionArgs, sortOrder);

        ArrayList<Album> albums = new ArrayList<>();
        while (cursor.moveToNext()) {
            int artistId = cursor.getInt(cursor
                    .getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST_ID));
            String artist = cursor.getString(cursor
                    .getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
            String album = cursor.getString(cursor
                    .getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM));
            int albumId = cursor.getInt(cursor
                    .getColumnIndexOrThrow(MediaStore.Audio.Media._ID));

            int numberOfSong = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.NUMBER_OF_SONGS));

            if(artistIdCompare != 0 && artistIdCompare != artistId) {
                continue;
            }

            Uri sArtworkUri = Uri.parse(Constants.DIRECTION_ALBUM_IMAGE);
            Uri albumArtUri = ContentUris.withAppendedId(sArtworkUri, albumId);

            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(
                        activity.getContentResolver(), albumArtUri);

            } catch (FileNotFoundException exception) {
                exception.printStackTrace();
            } catch (IOException e) {

                e.printStackTrace();
            }

            Album albumObject = new Album();
            albumObject.setId(albumId);
            albumObject.setName(album);
            albumObject.setArtistId(artistId);
            albumObject.setArtist(artist);
            albumObject.setImgCover(bitmap);
            albumObject.setArtUri(albumArtUri.toString());
            albumObject.setNumberOfSong(numberOfSong);

            albums.add(albumObject);
        }

        return albums;
    }

    public static ArrayList<Song> getSongs(Activity activity, Integer albumIdCompare, Integer artistIdCompare) {

        ArrayList<Song> results = new ArrayList<>();

        final Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        final String[] cursor_cols = { MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.ALBUM_ID,
                MediaStore.Audio.Media.ARTIST_ID,
                MediaStore.Audio.Media.DURATION };
        final String where = MediaStore.Audio.Media.IS_MUSIC + "=1";
        final Cursor cursor = activity.getContentResolver().query(uri,
                cursor_cols, where, null, null);

        while (cursor.moveToNext()) {
            String artist = cursor.getString(cursor
                    .getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
            String track = cursor.getString(cursor
                    .getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
            String data = cursor.getString(cursor
                    .getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
            int albumId = cursor.getInt(cursor
                    .getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID));
            int artistId = cursor.getInt(cursor
                    .getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST_ID));

            int duration = cursor.getInt(cursor
                    .getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));

            Uri sArtworkUri = Uri.parse(Constants.DIRECTION_ALBUM_IMAGE);
            Uri albumArtUri = ContentUris.withAppendedId(sArtworkUri, albumId);

            if(albumIdCompare != 0 && albumIdCompare != albumId) {
                continue;
            }

            if(artistIdCompare != 0 && artistIdCompare != artistId) {
                continue;
            }

            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(
                        activity.getContentResolver(), albumArtUri);

            } catch (FileNotFoundException exception) {
                exception.printStackTrace();
            } catch (IOException e) {

                e.printStackTrace();
            }

            Song audioListModel = new Song();
            audioListModel.set_artist(artist);
            audioListModel.set_bitmap(bitmap);
            audioListModel.set_title(track);
            audioListModel.set_path(data);
            audioListModel.set_albumId(albumId);
            audioListModel.set_artistId(artistId);
            audioListModel.set_duration(duration);
            audioListModel.set_uri(albumArtUri);

            results.add(audioListModel);

        }

        return results;
    }

    public static ArrayList<Artist> getArtist(Activity activity) {

        ArrayList<Artist> results = new ArrayList<>();

        final Uri uri = MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI;
        final String[] cursor_cols = { MediaStore.Audio.Media._ID,
                MediaStore.Audio.Artists.ARTIST,
                MediaStore.Audio.Artists.NUMBER_OF_ALBUMS,
                MediaStore.Audio.Artists.NUMBER_OF_TRACKS,};
        final Cursor cursor = activity.getContentResolver().query(uri,
                cursor_cols, null, null, null);

        while (cursor.moveToNext()) {

            String artistName = cursor.getString(cursor
                    .getColumnIndexOrThrow(MediaStore.Audio.Artists.ARTIST));
            Long artistId = cursor.getLong(cursor
                    .getColumnIndexOrThrow(MediaStore.Audio.Artists._ID));
//            Long albumId = cursor.getLong(cursor
//                    .getColumnIndexOrThrow(MediaStore.Audio.Artists.Albums.ALBUM));
            int numberOfTracks = cursor.getInt(cursor
                    .getColumnIndexOrThrow(MediaStore.Audio.Artists.NUMBER_OF_TRACKS));
            int numberOfAlbums = cursor.getInt(cursor
                    .getColumnIndexOrThrow(MediaStore.Audio.Artists.NUMBER_OF_ALBUMS));


            Uri sArtworkUri = Uri.parse(Constants.DIRECTION_ALBUM_IMAGE);
            Uri albumArtUri = ContentUris.withAppendedId(sArtworkUri, artistId);

            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(
                        activity.getContentResolver(), albumArtUri);

            } catch (FileNotFoundException exception) {
                exception.printStackTrace();
            } catch (IOException e) {

                e.printStackTrace();
            }

            Artist artist = new Artist();
            artist.setName(artistName);
            artist.setNumberOfTracks(numberOfTracks);
            artist.setNumberOfAlbums(numberOfAlbums);
            artist.setImgCover(bitmap);
            artist.setUri(albumArtUri);

            results.add(artist);
        }

        return results;
    }
}
