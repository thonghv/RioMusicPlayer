package com.pine.pmedia.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.pine.pmedia.helpers.Constants;

public class ControlActionsListener extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();
        switch (action) {
            case Constants.PREVIOUS:
                break;
            case Constants.PLAY_PAUSE:
                break;
            case Constants.NEXT:
                break;
        }
    }
}
