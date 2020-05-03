package com.pine.pmedia.models;

import java.util.ArrayList;

public class Filter {

    public int totalDuration;
    public ArrayList<Song> songs;

    public Filter() {

    }

    public Filter(int totalDuration, ArrayList<Song> songs) {
        this.totalDuration = totalDuration;
        this.songs = songs;
    }

    public ArrayList<Song> getSongs() {
        return songs;
    }

    public void setSongs(ArrayList<Song> songs) {
        this.songs = songs;
    }

    public int getTotalDuration() {
        return totalDuration;
    }

    public void setTotalDuration(int totalDuration) {
        this.totalDuration = totalDuration;
    }
}
