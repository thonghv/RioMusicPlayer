package com.pine.pmedia.activities;

public interface IActivity {

    void initBroadcast();

    void initDBManager();

    void initControlsSongPlayBottom();
    void initActionsSongPlayBottom();
    void onUpdateUISongPlayBottom();
}
