package com.pine.pmedia.models;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
public class Song implements Parcelable {

    private long _id;
    private String _image;
    private String _title;
    private String _artist;
    private String _album;
    private String _path;
    private int _size;
    private int _duration;
    private int _likeCount;

    private long _albumId;
    private long _artistId;
    private Bitmap _bitmap;

    private long _favoriteId;
    private long _historyId;
    private long _recentId;
    private long _queueId;
    private long _audioInPlayListId;

    private int _numberOfTrack;

    public Song() {
    }

    protected Song(Parcel in) {
        _id = in.readLong();
        _image = in.readString();
        _title = in.readString();
        _artist = in.readString();
        _path = in.readString();
        _duration = in.readInt();
        _likeCount = in.readInt();
        _albumId = in.readLong();
        _artistId = in.readLong();
        _size = in.readInt();
        _bitmap = in.readParcelable(Bitmap.class.getClassLoader());
        _numberOfTrack = in.readInt();
        _favoriteId = in.readLong();
        _historyId = in.readLong();
        _recentId = in.readLong();
        _audioInPlayListId = in.readLong();
    }

    public static final Creator<Song> CREATOR = new Creator<Song>() {
        @Override
        public Song createFromParcel(Parcel in) {
            return new Song(in);
        }

        @Override
        public Song[] newArray(int size) {
            return new Song[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(_id);
        dest.writeString(_image);
        dest.writeString(_title);
        dest.writeString(_artist);
        dest.writeString(_path);
        dest.writeInt(_duration);
        dest.writeInt(_likeCount);
        dest.writeLong(_albumId);
        dest.writeLong(_artistId);
        dest.writeInt(_size);
        dest.writeParcelable(_bitmap, flags);
        dest.writeInt(_numberOfTrack);
        dest.writeLong(_favoriteId);
        dest.writeLong(_historyId);
        dest.writeLong(_recentId);
        dest.writeLong(_audioInPlayListId);
    }
}
