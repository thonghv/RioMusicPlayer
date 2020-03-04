package com.pine.pmedia.models;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Artist implements Parcelable {

    private int id;
    private String name;
    private String artUri;
    private Bitmap imgCover;
    private Uri uri;
    private int numberOfTracks;
    private int numberOfAlbums;

    protected Artist(Parcel in) {
        id = in.readInt();
        name = in.readString();
        artUri = in.readString();
        imgCover = in.readParcelable(Bitmap.class.getClassLoader());
        uri = in.readParcelable(Uri.class.getClassLoader());
        numberOfTracks = in.readInt();
        numberOfAlbums = in.readInt();
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
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(artUri);
        dest.writeParcelable(imgCover, flags);
        dest.writeParcelable(uri, flags);
        dest.writeInt(numberOfTracks);
        dest.writeInt(numberOfAlbums);
    }
}

