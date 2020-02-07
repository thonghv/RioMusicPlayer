package com.pine.pmedia.helpers;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CurrentSongHelper {

    private String songArtist;
    private String songTitle;
    private String songPath;
    private Long songId;
    private int currentPosition;
    private Boolean isPlaying;
    private Boolean isLoop;
    private Boolean isShuffle;
    private int trackPosition;
}
