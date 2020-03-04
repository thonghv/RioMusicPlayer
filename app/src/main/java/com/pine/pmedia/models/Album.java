package com.pine.pmedia.models;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Album implements Parcelable {

    private int id;
    private String name;
    private Bitmap imgCover;
    private String artUri;
    private int numberOfSong;

    private int artistId;
    private String artist;

    protected Album(Parcel in) {
        id = in.readInt();
        name = in.readString();
        imgCover = in.readParcelable(Bitmap.class.getClassLoader());
        artUri = in.readString();
        numberOfSong = in.readInt();
        artistId = in.readInt();
        artist = in.readString();
    }

    public static final Creator<Album> CREATOR = new Creator<Album>() {
        @Override
        public Album createFromParcel(Parcel in) {
            return new Album(in);
        }

        @Override
        public Album[] newArray(int size) {
            return new Album[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeParcelable(imgCover, flags);
        dest.writeString(artUri);
        dest.writeInt(numberOfSong);
        dest.writeInt(artistId);
        dest.writeString(artist);
    }
}
