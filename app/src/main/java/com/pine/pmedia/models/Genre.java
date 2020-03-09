package com.pine.pmedia.models;

import android.os.Parcel;
import android.os.Parcelable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Genre implements Parcelable {

    private int id;
    private String name;
    private int numberOfTracks;
    private String artRepresent;
    private int totalDuration;


    protected Genre(Parcel in) {
        id = in.readInt();
        name = in.readString();
        numberOfTracks = in.readInt();
        artRepresent = in.readString();
        totalDuration = in.readInt();
    }

    public static final Creator<Genre> CREATOR = new Creator<Genre>() {
        @Override
        public Genre createFromParcel(Parcel in) {
            return new Genre(in);
        }

        @Override
        public Genre[] newArray(int size) {
            return new Genre[size];
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
        dest.writeInt(numberOfTracks);
        dest.writeString(artRepresent);
        dest.writeInt(totalDuration);
    }
}
