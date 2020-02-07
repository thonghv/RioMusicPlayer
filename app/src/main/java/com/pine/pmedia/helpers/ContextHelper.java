package com.pine.pmedia.helpers;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.core.content.ContextCompat;

import com.pine.pmedia.services.MusicService;

public abstract class ContextHelper {

    public static void sendIntent(Context context, String action, Bundle bundle) {

        Intent intent = new Intent(context, MusicService.class);
        intent.setAction(action);
        intent.putExtras(bundle);
        ContextCompat.startForegroundService(context, intent);
    }

    public static Boolean hasPermission(Context context, int permId) {

        int check = context.checkCallingOrSelfPermission(getPermission(permId));
        if(check != PackageManager.PERMISSION_GRANTED) {
            return false;
        }

        return true;
    }

    private static String getPermission(int id) {

        switch (id) {
            case Constants.PERMISSION_READ_STORAGE:
                return Manifest.permission.READ_EXTERNAL_STORAGE;
            case Constants.PERMISSION_WRITE_STORAGE:
                return Manifest.permission.WRITE_EXTERNAL_STORAGE;
            case Constants.PERMISSION_CAMERA:
                return Manifest.permission.CAMERA;
            case Constants.PERMISSION_RECORD_AUDIO:
                return Manifest.permission.RECORD_AUDIO;
            case Constants.PERMISSION_READ_CONTACTS:
                return Manifest.permission.READ_CONTACTS;
            case Constants.PERMISSION_WRITE_CONTACTS:
                return Manifest.permission.WRITE_CONTACTS;
            case Constants.PERMISSION_READ_CALENDAR:
                return Manifest.permission.READ_CALENDAR;
            case Constants.PERMISSION_WRITE_CALENDAR:
                return Manifest.permission.WRITE_CALENDAR;
            case Constants.PERMISSION_CALL_PHONE:
                return Manifest.permission.CALL_PHONE;
            case Constants.PERMISSION_READ_CALL_LOG:
                return Manifest.permission.READ_CALL_LOG;
            case Constants.PERMISSION_WRITE_CALL_LOG:
                return Manifest.permission.WRITE_CALL_LOG;
            case Constants.PERMISSION_GET_ACCOUNTS:
                return Manifest.permission.GET_ACCOUNTS;
        }

        return "";
    }
}
