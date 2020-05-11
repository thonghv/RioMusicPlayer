package com.pine.pmedia.helpers;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.BaseColumns;
import android.provider.MediaStore;

import com.pine.pmedia.App;
import com.pine.pmedia.models.Album;
import com.pine.pmedia.models.Artist;
import com.pine.pmedia.models.Filter;
import com.pine.pmedia.models.Genre;
import com.pine.pmedia.models.Song;
import com.pine.pmedia.sqlite.DBManager;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public static Song getSongById(ArrayList<Song> songs, long id) {

        for(Song s: songs) {
            if(s.get_id() == id) {
                return s;
            }
        }

        return null;
    }

    public static ArrayList<Song> getSongs(Activity activity, long albumIdCompare, long artistIdCompare) {

        ArrayList<Song> results = new ArrayList<>();

        final Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        final String[] cursor_cols = { MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.ALBUM_ID,
                MediaStore.Audio.Media.ARTIST_ID,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.DATE_ADDED,
                MediaStore.Audio.Media.SIZE
        };
        final String where = MediaStore.Audio.Media.IS_MUSIC + "=1";
        final Cursor cursor = activity.getContentResolver().query(uri,
                cursor_cols, where, null, null);

        while (cursor.moveToNext()) {
            long id = cursor.getLong(cursor
                    .getColumnIndexOrThrow(MediaStore.Audio.Media._ID));
            String artist = cursor.getString(cursor
                    .getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
            String album = cursor.getString(cursor
                    .getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM));
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

            int size = cursor.getInt(cursor
                    .getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE));

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

            Song song = new Song();
            song.set_id(id);
            song.set_album(album);
            song.set_artist(artist);
            song.set_bitmap(bitmap);
            song.set_title(track);
            song.set_path(data);
            song.set_albumId(albumId);
            song.set_artistId(artistId);
            song.set_size(size);
            song.set_duration(duration);
            song.set_image(albumArtUri.toString());

            results.add(song);

        }

        return results;
    }


    public static final Cursor makeLastAddedCursor(final Activity activity) {

        //four weeks ago
        long fourWeeksAgo = (System.currentTimeMillis() / 1000) - (4 * 3600 * 24 * 7);

        final StringBuilder selection = new StringBuilder();
        selection.append(MediaStore.Audio.AudioColumns.IS_MUSIC + "=1");
        selection.append(" AND " + MediaStore.Audio.AudioColumns.TITLE + " != ''");
        selection.append(" AND " + MediaStore.Audio.Media.DATE_ADDED + ">");
        selection.append(fourWeeksAgo);

        final String[] cursor_cols = {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.ALBUM_ID,
                MediaStore.Audio.Media.ARTIST_ID,
                MediaStore.Audio.Media.DURATION
        };

        return activity.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                cursor_cols, selection.toString(), null,
                MediaStore.Audio.Media.DATE_ADDED + " DESC");
    }

    public static Filter parseSong(Activity activity, Cursor cursor, long albumIdCompare, long artistIdCompare) {

        int totalDuration = 0;
        ArrayList<Song> results = new ArrayList<>();
        while (cursor.moveToNext()) {
            long id = cursor.getLong(cursor
                    .getColumnIndexOrThrow(MediaStore.Audio.Media._ID));

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

            Song song = new Song();
            song.set_id(id);
            song.set_artist(artist);
            song.set_bitmap(bitmap);
            song.set_title(track);
            song.set_path(data);
            song.set_albumId(albumId);
            song.set_artistId(artistId);
            song.set_duration(duration);
            song.set_image(albumArtUri.toString());

            results.add(song);
            totalDuration += duration;

        }

        return new Filter(totalDuration, results);
    }

    public static Filter getSongsByPlayListId(Activity activity, long playListId) {

        int totalDuration = 0;
        ArrayList<Song> results= new ArrayList<>();
        final long [] ids = getSongListForPlaylist(activity, playListId);
        ArrayList<Song> songs = getSongs(activity, 0L, 0L);
        for(int i = 0; i < ids.length; i++){
            for(Song s : songs) {
                if(s.get_id() == ids[i]) {
                    results.add(s);
                    totalDuration += s.get_duration();
                }
            }
        }

        return new Filter(totalDuration, results);
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
            int artistId = cursor.getInt(cursor
                    .getColumnIndexOrThrow(MediaStore.Audio.Artists._ID));
            int numberOfTracks = cursor.getInt(cursor
                    .getColumnIndexOrThrow(MediaStore.Audio.Artists.NUMBER_OF_TRACKS));
            int numberOfAlbums = cursor.getInt(cursor
                    .getColumnIndexOrThrow(MediaStore.Audio.Artists.NUMBER_OF_ALBUMS));

            ArrayList<Album> albums = makeAlbumForArtistCursor(activity, artistId);

            Artist artist = new Artist();
            artist.setId(artistId);
            artist.setName(artistName);
            artist.setNumberOfTracks(numberOfTracks);
            artist.setNumberOfAlbums(numberOfAlbums);
            artist.setAlbumList(albums);

            if(!albums.isEmpty()) {
                artist.setArtUri(albums.get(0).getArtUri());
            }

            results.add(artist);
        }

        return results;
    }

    public static ArrayList<Album> makeAlbumForArtistCursor(Context context, long artistID) {

        if (artistID == -1)
            return new ArrayList<>();

        ArrayList<Album> albums = new ArrayList<>();

        Cursor cursor = context.getContentResolver()
                .query(MediaStore.Audio.Artists.Albums.getContentUri(Constants.EXTERNAL, artistID),
                        new String[]{
                                MediaStore.Audio.Media._ID,
                                MediaStore.Audio.Albums.ALBUM,
                                MediaStore.Audio.Genres._ID,
                                MediaStore.Audio.Artists.ARTIST,
                                MediaStore.Audio.Albums.NUMBER_OF_SONGS,
                                MediaStore.Audio.Albums.FIRST_YEAR},
                        null,
                        null,
                        MediaStore.Audio.Albums.FIRST_YEAR);

        while (cursor.moveToNext()) {
            int albumId = cursor.getInt(cursor
                    .getColumnIndexOrThrow(MediaStore.Audio.Media._ID));
            String albumName = cursor.getString(cursor
                    .getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM));
            int numSongs = cursor.getInt(cursor
                    .getColumnIndexOrThrow(MediaStore.Audio.Albums.NUMBER_OF_SONGS));

            Uri sArtworkUri = Uri.parse(Constants.DIRECTION_ALBUM_IMAGE);
            Uri albumArtUri = ContentUris.withAppendedId(sArtworkUri, albumId);

            Album album = new Album();
            album.setId(albumId);
            album.setName(albumName);
            album.setNumberOfSong(numSongs);
            album.setArtUri(albumArtUri.toString());

            albums.add(album);
        }

        return albums;
    }

    public static ArrayList<Genre> getGenres(Context context) {

        ArrayList<Genre> genres = new ArrayList<>();
        Cursor cursor = context.getContentResolver()
                .query(MediaStore.Audio.Genres.getContentUri(Constants.EXTERNAL),
                        new String[]{
                                MediaStore.Audio.Media._ID,
                                MediaStore.Audio.Genres.NAME,
                        },null, null,null);

        while (cursor.moveToNext()) {
            int _id = cursor.getInt(cursor
                    .getColumnIndexOrThrow(MediaStore.Audio.Media._ID));
            String _name = cursor.getString(cursor
                    .getColumnIndexOrThrow(MediaStore.Audio.Genres.NAME));

            Map<String, Object> baseInfo = getInfoBaseGenre(context, _id);
            int _count = (int) baseInfo.get(Constants.KEY_NUMBER_OF_TRACK);
            int _totalDuration = (int) baseInfo.get(Constants.KEY_DURATION);

            ArrayList<Song> songs = getSongListForGenre(context, _id);
            String _artRepresent = _count > 0 ? songs.get(0).get_image() : "";
            genres.add(new Genre(_id, _name, _count, _artRepresent, _totalDuration));
        }

        return genres;
    }

    public static ArrayList<Song> getSongListForGenre(Context context, long id) {

        ArrayList<Song> songs = new ArrayList<>();

        String[] projection = new String[] {
                BaseColumns._ID,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.ALBUM_ID,
                MediaStore.Audio.Media.ARTIST_ID,
                MediaStore.Audio.Media.DURATION };
        StringBuilder selection = new StringBuilder();
        selection.append(MediaStore.Audio.AudioColumns.IS_MUSIC + "=1");
        selection.append(" AND " + MediaStore.MediaColumns.TITLE + "!=''");
        Uri uri = MediaStore.Audio.Genres.Members.getContentUri(Constants.EXTERNAL, id);

        Cursor cursor = context.getContentResolver().query(uri, projection, selection.toString(), null, null);
        while (cursor.moveToNext()) {
            int _id = cursor.getInt(cursor
                    .getColumnIndexOrThrow(MediaStore.Audio.Media._ID));
            String _artist = cursor.getString(cursor
                    .getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
            String _track = cursor.getString(cursor
                    .getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
            int _albumId = cursor.getInt(cursor
                    .getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID));
            int _artistId = cursor.getInt(cursor
                    .getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST_ID));

            int _duration = cursor.getInt(cursor
                    .getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));

            Uri _sArtworkUri = Uri.parse(Constants.DIRECTION_ALBUM_IMAGE);
            Uri _albumArtUri = ContentUris.withAppendedId(_sArtworkUri, _albumId);

            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(
                        context.getContentResolver(), _albumArtUri);

            } catch (FileNotFoundException exception) {
                exception.printStackTrace();
            } catch (IOException e) {

                e.printStackTrace();
            }

            Song song = new Song();
            song.set_id(_id);
            song.set_artist(_artist);
            song.set_bitmap(bitmap);
            song.set_title(_track);
            song.set_albumId(_albumId);
            song.set_artistId(_artistId);
            song.set_duration(_duration);
            song.set_image(_albumArtUri.toString());

            songs.add(song);
        }

        return songs;
    }

    public static Map<String, Object> getInfoBaseGenre(Context context, long id) {

        ArrayList<Song> songs = getSongListForGenre(context, id);

        int totalDuration = 0;
        for(Song song : songs) {
            totalDuration += song.get_duration();
        }

        Map<String, Object> result = new HashMap<>();
        result.put(Constants.KEY_NUMBER_OF_TRACK, songs.size());
        result.put(Constants.KEY_DURATION, totalDuration);

        return result;
    }

    public static ArrayList<Song> getAllPLayList(Context context) {

        ArrayList<Song> result = new ArrayList<>();

        ContentResolver resolver = context.getContentResolver();
        String[] cols = new String[] {
                MediaStore.Audio.Playlists._ID,
                MediaStore.Audio.Playlists.NAME
        };
        String sortOrder = MediaStore.Audio.Media.DATE_ADDED + " DESC";
        Cursor cursor = resolver.query(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, cols, null,
                null, sortOrder);
        while (cursor.moveToNext()) {
            int _id = cursor.getInt(cursor
                    .getColumnIndexOrThrow(MediaStore.Audio.Playlists._ID));
            String _name = cursor.getString(cursor
                    .getColumnIndexOrThrow(MediaStore.Audio.Playlists.NAME));

            Song song = new Song();
            song.set_id(_id);
            song.set_title(_name);
            song.set_numberOfTrack(countPlaylist(context, _id));

            result.add(song);
        }

        return result;
    }

    public static final long createPlaylist(final Context context, final String name) {

        if (name != null && name.length() > 0) {
            final ContentResolver resolver = context.getContentResolver();
            final String[] projection = new String[]{
                    MediaStore.Audio.PlaylistsColumns.NAME
            };
            final String selection = MediaStore.Audio.PlaylistsColumns.NAME + " = '" + name + "'";
            Cursor cursor = resolver.query(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI,
                    projection, selection, null, null);
            if (cursor.getCount() <= 0) {
                final ContentValues values = new ContentValues(1);
                values.put(MediaStore.Audio.PlaylistsColumns.NAME, name);
                final Uri uri = resolver.insert(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, values);
                return Long.parseLong(uri.getLastPathSegment());
            }
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }

            return -1;
        }

        return -1;
    }

    public static final long updatePlaylist(Context context, long playListId, String playListName) {

        if (playListName != null && playListName.length() > 0) {
            final ContentResolver resolver = context.getContentResolver();
            long idFind = getIdForPlaylist(context, playListName);
            if(idFind > 0 && idFind != playListId) {
                return -1;
            } else {
                String where = MediaStore.Audio.Playlists._ID + "=?";
                String[] whereVal = {String.valueOf(playListId)};

                final ContentValues values = new ContentValues(1);
                values.put(MediaStore.Audio.PlaylistsColumns.NAME, playListName);
                resolver.update(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, values, where, whereVal);
                return 1;
            }
        }

        return -1;
    }

    public static void deletePlaylist(Context context, long playListId) {

        ContentResolver resolver = context.getContentResolver();
        String where = MediaStore.Audio.Playlists._ID + "=?";
        String[] whereVal = {String.valueOf(playListId)};
        resolver.delete(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, where, whereVal);
    }

    public static void addToPlaylist(Context context, long audioId, long playListId) {

        final ContentResolver resolver = context.getContentResolver();

        String[] cols = new String[] {
                "count(*)"
        };
        Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", playListId);
        Cursor cur = resolver.query(uri, cols, null, null, null);
        cur.moveToFirst();
        final int base = cur.getInt(0);
        cur.close();
        ContentValues values = new ContentValues();
        values.put(MediaStore.Audio.Playlists.Members.PLAY_ORDER, Long.valueOf(base + audioId));
        values.put(MediaStore.Audio.Playlists.Members.AUDIO_ID, audioId);
        resolver.insert(uri, values);
    }

    public static void addToPlaylist(Context context, List<Long> audioIds, long playListId) {

        new ExecuteProcessAddToPlaylist(context, audioIds, playListId).execute();
    }

    public static class ExecuteProcessAddToPlaylist extends AsyncTask<String, Void, String> {

        private Context context;
        private List<Long> audioIds;
        private long playListId;

        public ExecuteProcessAddToPlaylist(Context context, List<Long> audioIds, long playListId) {
            this.context = context;
            this.audioIds = audioIds;
            this.playListId = playListId;
        }

        protected String doInBackground(String... params){
            for(long audioId : audioIds) {
                addToPlaylist(context, audioId, playListId);
            }
            return "";
        }

        protected void onPostExecute(String response) {
        }
    }

    public static int countPlaylist(final Context context, final int playlistId) {
        Cursor c = null;
        try {
            c = context.getContentResolver().query(
                    MediaStore.Audio.Playlists.Members.getContentUri("external", playlistId),
                    new String[]{
                            MediaStore.Audio.Playlists.Members.AUDIO_ID,
                    }, null, null,
                    MediaStore.Audio.Playlists.Members.DEFAULT_SORT_ORDER);

            if (c != null) {
                return c.getCount();
            }
        } finally {
            if (c != null) {
                c.close();
                c = null;
            }
        }

        return 0;
    }

    public static final Cursor makePlaylistSongCursor(final Context context, final Long playlistID) {
        final StringBuilder mSelection = new StringBuilder();
        mSelection.append(MediaStore.Audio.AudioColumns.IS_MUSIC + "=1");
        mSelection.append(" AND " + MediaStore.Audio.AudioColumns.TITLE + " != ''");
        return context.getContentResolver().query(
                MediaStore.Audio.Playlists.Members.getContentUri("external", playlistID),
                new String[]{
                        MediaStore.Audio.Playlists.Members._ID,
                        MediaStore.Audio.Playlists.Members.AUDIO_ID,
                        MediaStore.Audio.AudioColumns.TITLE,
                        MediaStore.Audio.AudioColumns.ARTIST,
                        MediaStore.Audio.AudioColumns.ALBUM_ID,
                        MediaStore.Audio.AudioColumns.ARTIST_ID,
                        MediaStore.Audio.AudioColumns.ALBUM,
                        MediaStore.Audio.AudioColumns.DURATION,
                        MediaStore.Audio.AudioColumns.TRACK,
                        MediaStore.Audio.Playlists.Members.PLAY_ORDER,
                }, mSelection.toString(), null,
                MediaStore.Audio.Playlists.Members.DEFAULT_SORT_ORDER);
    }

    public static void removeFromPlaylist(ContentResolver resolver, int audioId, long playListId) {
        String[] cols = new String[] {
                "count(*)"
        };
        Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", playListId);
        Cursor cur = resolver.query(uri, cols, null, null, null);
        cur.moveToFirst();
        final int base = cur.getInt(0);
        cur.close();
        ContentValues values = new ContentValues();

        resolver.delete(uri, MediaStore.Audio.Playlists.Members.AUDIO_ID +" = "+audioId, null);
    }

    public static long getIdForPlaylist(final Context context, final String name) {
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, new String[] {
                        BaseColumns._ID
                }, MediaStore.Audio.PlaylistsColumns.NAME + "=?", new String[] {
                        name
                }, MediaStore.Audio.PlaylistsColumns.NAME);
        int id = -1;
        if (cursor != null) {
            cursor.moveToFirst();
            if (!cursor.isAfterLast()) {
                id = cursor.getInt(0);
            }
            cursor.close();
        }
        return id;
    }

    /**
     * @param context The {@link Context} to sue
     * @param playlistId The playlist Id
     * @return The track list for a playlist
     */
    public static final long[] getSongListForPlaylist(final Context context, final long playlistId) {
        final String[] projection = new String[] {
                MediaStore.Audio.Playlists.Members.AUDIO_ID,
        };

        Cursor cursor = context.getContentResolver().query(
                MediaStore.Audio.Playlists.Members.getContentUri("external",
                        playlistId), projection, null, null,
                MediaStore.Audio.Playlists.Members.DEFAULT_SORT_ORDER);

        long[] list = null;
        if (cursor != null) {
            list = getSongListForCursor(cursor);
            cursor.close();
            return list;
        }

        return list;
    }

    public static long[] getSongListForCursor(Cursor cursor) {
        if (cursor == null) {
            return new long[0];
        }
        int len = cursor.getCount();
        long[] list = new long[len];
        cursor.moveToFirst();
        int colidx;
        try {
            colidx = cursor.getColumnIndexOrThrow(MediaStore.Audio.Playlists.Members.DISPLAY_NAME);
        } catch (IllegalArgumentException ex) {
            colidx = cursor.getColumnIndexOrThrow(MediaStore.Audio.Playlists.Members.AUDIO_ID);
        }
        for (int i = 0; i < len; i++) {
            list[i] = cursor.getLong(colidx);
            cursor.moveToNext();
        }
        return list;
    }

    public static Song getById(ArrayList<Song> songs, long id) {

        for(Song song: songs) {
            if(song.get_id() == id) {
                return song;
            }
        }

        return null;
    }

    public static Filter getFavorites(DBManager dbManager, ArrayList<Song> songsTotal) {

        int totalDuration = 0;
        ArrayList<Song> results = new ArrayList<>();
        ArrayList<Song> songsTemp = dbManager.getFavoritesSong();

        for(Song s : songsTemp) {

            Song songFind = getById(songsTotal, s.get_id());
            if(songFind != null) {
                songFind.set_favoriteId(s.get_favoriteId());
                results.add(songFind);
                totalDuration += songFind.get_duration();
            }
        }

        return new Filter(totalDuration, results);
    }

    public static Filter getHistories(DBManager dbManager, ArrayList<Song> songsTotal) {

        int totalDuration = 0;
        ArrayList<Song> songs = new ArrayList<>();
        ArrayList<Song> songsTemp = dbManager.getHistorySong();

        for(Song s : songsTemp) {

            Song songFind = getById(songsTotal, s.get_id());
            if(songFind != null) {
                songFind.set_historyId(s.get_historyId());
                totalDuration += songFind.get_duration();
                songs.add(songFind);
            }
        }

        return new Filter(totalDuration, songs);
    }


    public static Filter getRecent(DBManager dbManager, ArrayList<Song> songsTotal) {

        int totalDuration = 0;
        ArrayList<Song> songs = new ArrayList<>();
        ArrayList<Song> songsTemp = dbManager.getRecentSong();

        for(Song s : songsTemp) {

            Song songFind = getById(songsTotal, s.get_id());
            if(songFind != null) {
                songFind.set_historyId(s.get_historyId());
                totalDuration += songFind.get_duration();
                songs.add(songFind);
            }
        }

        return new Filter(totalDuration, songs);
    }

    public static Filter getQueue(DBManager dbManager, ArrayList<Song> songsTotal) {

        int totalDuration = 0;
        ArrayList<Song> songs = new ArrayList<>();
        ArrayList<Song> songsTemp = dbManager.getQueueSong();

        for(Song s : songsTemp) {

            Song songFind = getById(songsTotal, s.get_id());
            if(songFind != null) {
                songFind.set_historyId(s.get_historyId());
                totalDuration += songFind.get_duration();
                songs.add(songFind);
            }
        }

        return new Filter(totalDuration, songs);
    }

    public static int getTotalDuration(ArrayList<Song> songs) {

        int totalDuration = 0;
        for(Song s : songs) {
            totalDuration += s.get_duration();
        }

        return totalDuration;
    }

    public static ArrayList<Song> getLastAddRecent(Activity activity) {

        ArrayList<Song> results = new ArrayList<>();
        Cursor cursor = makeLastAddedCursor(activity);
        while (cursor.moveToNext()) {
            long id = cursor.getLong(cursor
                    .getColumnIndexOrThrow(MediaStore.Audio.Media._ID));
            String track = cursor.getString(cursor
                    .getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));

            Song song = new Song();
            song.set_id(id);
            song.set_title(track);

            results.add(song);
        }

        return results;
    }

    public static Song findPreviousSong(DBManager dbManager, ArrayList<Song> songsTotal) {

        String value = dbManager.getSettingByKey(Constants.SETTING_SONG_PLAYING);
        if(value != null) {
            return getById(songsTotal, Long.valueOf(value));
        } else if(!songsTotal.isEmpty()) {
            return songsTotal.get(0);
        }

        return null;
    }
}
