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

    private String name;
    private String artist;
    private Bitmap imgCover;
    private int numberOfSong;

    protected Album(Parcel in) {
        name = in.readString();
        artist = in.readString();
        imgCover = in.readParcelable(Bitmap.class.getClassLoader());
        numberOfSong = in.readInt();
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
        dest.writeString(name);
        dest.writeString(artist);
        dest.writeParcelable(imgCover, flags);
        dest.writeInt(numberOfSong);
    }
}
