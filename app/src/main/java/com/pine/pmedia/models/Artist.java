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
public class Artist implements Parcelable {

    private String name;
    private Bitmap imgCover;
    private int numberOfSong;


    protected Artist(Parcel in) {
        name = in.readString();
        imgCover = in.readParcelable(Bitmap.class.getClassLoader());
        numberOfSong = in.readInt();
    }

    public static final Creator<Artist> CREATOR = new Creator<Artist>() {
        @Override
        public Artist createFromParcel(Parcel in) {
            return new Artist(in);
        }

        @Override
        public Artist[] newArray(int size) {
            return new Artist[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeParcelable(imgCover, flags);
        dest.writeInt(numberOfSong);
    }
}
