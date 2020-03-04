package com.pine.pmedia.helpers;

public class Constants {

    public static final String MAIN_ACTIVITY_NAME       = "com.pine.pmedia.activities.MainActivity";
    public static final String DIRECTION_ALBUM_IMAGE    = "content://media/external/audio/albumart";
    public static final String API                      = "https://api.soundcloud.com";
    public static final String TOKEN                    = "0d4392665746d1ccedc7193e7338617e";
    public static final String TIME_FOMAT_M_S           = "%02d:%02d";
    public static final String TIME_FOMAT_H_M_S         = "%02d:%02d:%02d";
    public static final String ITEM_ALL_SONG            = "All Song";
    public static final String ITEM_FAVORITE_LIST       = "Favorite List";
    public static final String ITEM_SETTING             = "Settings";
    public static final String ITEM_ABOUT               = "About Us";
    public static final String ACTION                   = "action";
    public static final String KEY_RECENT               = "key_recent";
    public static final String KEY_SETTING              = "key_setting";
    public static final String KEY_LIKE                 = "key_like";
    public static final String SPLITSTR                 = "|*|*|";
    public static final String AVATAR_DEFAULT           = "large";
    public static final String AVATAR_DEFAULT_X500      = "t500x500";
    public static final String ALBUMS                   = "albums";
    public static final String SONGS                    = "songs";
    public static final String SPACE                    = " ";
    public static final String DRAWABLE_PATH            = "drawable://";
    public static final String EXTERNAL                 = "external";

    public static final int LIMIT_RECENT                = 50;
    public static final int LIMIT_NUMBER                = 50;
    public static final int OFFSET_NUMBER               = 0;

    public static final int ITEM_TYPE_SONG              = 1;
    public static final int ITEM_TYPE_CONTROL           = 2;
    public static final int ITEM_TYPE_ADS               = 3;

    public static final int VIEW_ALBUM                  = 1;
    public static final int VIEW_ARTIST                 = 2;

    //=================
    // API KEY
    //=================
    public static final String KEY_STREAM               = "/stream";
    public static final String KEY_TRACKS               = "/tracks";
    public static final String A_CLIENT_ID              = "&client_id=";
    public static final String Q_CLIENT_ID              = "?client_id=";
    public static final String SEARCH                   = "/tracks.json?q=";
    public static final String LIMIT                    = "&limit=";
    public static final String OFFSET                   = "&offset=";
    public static final String KEY_DATA                 = "data";
    public static final String KEY_ID                   = "id";
    public static final String KEY_TITLE                = "title";
    public static final String KEY_NAME                 = "name";
    public static final String KEY_ARTIST               = "artist";
    public static final String KEY_ARTWORK              = "artwork_url";
    public static final String KEY_PATH                 = "path";
    public static final String KEY_DURATION             = "duration";
    public static final String KEY_LIKE_COUNT           = "likes_count";
    public static final String KEY_POSITION             = "position";
    public static final String KEY_SONG_LIST            = "songList";
    public static final String KEY_NUMBER_OF_TRACK      = "songsNumber";
    public static final String KEY_NUMBER_OF_ALBUM      = "numberOfAlbum";
    public static final String KEY_USER                 = "user";
    public static final String KEY_USERNAME             = "username";
    public static final String SONG_COMPLETE            = "songComplete";
    public static final String HIDE_CONTENT_BOTTOM      = "hideContentBottom";
    public static final String SHOW_CONTENT_BOTTOM      = "showContentBottom";

    //=================
    // API SEARCH
    //=================
    public static final String FOR_REMIX                = "/remix viet 2019";
    public static final String FOR_NONSTOP              = "/nonstop 2019";
    public static final String FOR_EDM                  = "/edm 2019";

    //=================
    // PERMISSIONS
    //=================
    public static final int PERMISSION_READ_STORAGE     = 1;
    public static final int PERMISSION_WRITE_STORAGE    = 2;
    public static final int PERMISSION_CAMERA           = 3;
    public static final int PERMISSION_RECORD_AUDIO     = 4;
    public static final int PERMISSION_READ_CONTACTS    = 5;
    public static final int PERMISSION_WRITE_CONTACTS   = 6;
    public static final int PERMISSION_READ_CALENDAR    = 7;
    public static final int PERMISSION_WRITE_CALENDAR   = 8;
    public static final int PERMISSION_CALL_PHONE       = 9;
    public static final int PERMISSION_READ_CALL_LOG    = 10;
    public static final int PERMISSION_WRITE_CALL_LOG   = 11;
    public static final int PERMISSION_GET_ACCOUNTS     = 12;

    //=================
    // ACTIONS
    //=================
    public static final String INIT                     = "INIT";
    public static final String INIT_PATH                = "INIT_PATH";
    public static final String SETUP                    = "SETUP";
    public static final String FINISH                   = "FINISH";
    public static final String PREVIOUS                 = "PREVIOUS";
    public static final String PLAYPAUSE                = "PLAYPAUSE";
    public static final String PAUSE                    = "PAUSE";
    public static final String LOOP                     = "LOOP";
    public static final String SHUFFLE                  = "SHUFFLE";
    public static final String PLAY                     = "PLAY";
    public static final String NEXT                     = "NEXT";
    public static final String RESET                    = "RESET";
    public static final String EDIT                     = "EDIT";
    public static final String PLAYPOS                  = "PLAYPOS";
    public static final String REFRESH_LIST             = "REFRESH_LIST";
    public static final String SET_PROGRESS             = "SET_PROGRESS";
    public static final String SET_EQUALIZER            = "SET_EQUALIZER";
    public static final String SKIP_BACKWARD            = "SKIP_BACKWARD";
    public static final String SKIP_FORWARD             = "SKIP_FORWARD";
    public static final String REMOVE_CURRENT_SONG      = "REMOVE_CURRENT_SONG";
    public static final String REMOVE_SONG_IDS          = "REMOVE_SONG_IDS";
    public static final String STOP                     = "STOP";
    public static final String QUIT                     = "QUIT";

    //=================
    // FONTS
    //=================
    public static final String FONT_ROBOTO_REGULAR      = "fonts/Roboto-Regular.ttf";
    public static final String FONT_ROBOTO_LIGHT        = "fonts/Roboto-Medium.ttf";
    public static final String FONT_OPENSANS            = "fonts/OpenSans-Regular.ttf";


    //=================
    // COLORS
    //=================
    public static final String PALETTE_ALBUM_COLOR_DEFAUT           = "#403f4d";
}
