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

    private int _albumId;
    private int _artistId;
    private Bitmap _bitmap;

    private long _favoriteId;
    private long _historyId;

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
        _albumId = in.readInt();
        _artistId = in.readInt();
        _size = in.readInt();
        _bitmap = in.readParcelable(Bitmap.class.getClassLoader());
        _numberOfTrack = in.readInt();
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
        dest.writeInt(_albumId);
        dest.writeInt(_artistId);
        dest.writeInt(_size);
        dest.writeParcelable(_bitmap, flags);
        dest.writeInt(_numberOfTrack);
    }
}
