package com.pine.pmedia.models;

import android.os.Parcel;
import android.os.Parcelable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Song implements Parcelable {

    private Long _id;
    private String _image;
    private String _title;
    private String _artist;
    private String _path;
    private int _duration;
    private int _likeCount;

    public Song(Long id, String image, String title, String path, int duration) {
        this._id = id;
        this._image = image;
        this._title = title;
        this._path = path;
        this._duration = duration;
    }

    public Song(Long id, String title, String path, int duration) {
        this._id = id;
        this._title = title;
        this._path = path;
        this._duration = duration;
    }

    protected Song(Parcel in) {
        if (in.readByte() == 0) {
            _id = null;
        } else {
            _id = in.readLong();
        }
        _image = in.readString();
        _title = in.readString();
        _artist = in.readString();
        _path = in.readString();
        _duration = in.readInt();
        _likeCount = in.readInt();
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
        if (_id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(_id);
        }
        dest.writeString(_image);
        dest.writeString(_title);
        dest.writeString(_artist);
        dest.writeString(_path);
        dest.writeInt(_duration);
        dest.writeInt(_likeCount);
    }
}
