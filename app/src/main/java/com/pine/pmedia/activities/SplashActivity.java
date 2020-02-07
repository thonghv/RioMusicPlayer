package com.pine.pmedia.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.pine.pmedia.R;

public class SplashActivity extends AppCompatActivity {

    private final String [] permissions = new String[] {
            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.MODIFY_AUDIO_SETTINGS,
            Manifest.permission.READ_PHONE_STATE, Manifest.permission.RECORD_AUDIO };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        if(this.hasPermission(this, permissions)) {
            ActivityCompat.requestPermissions(this, permissions, 131);
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                }
            }, 1000);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == 131) {
            if(grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[2] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[3] == PackageManager.PERMISSION_GRANTED) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        finish();
                    }
                }, 1000);
            } else {
                Toast.makeText(this, "Please grant all permission to continue", Toast.LENGTH_SHORT).show();
                finish();
            }

            return;

        } else {
            Toast.makeText(this, "Something wrong", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
    }

    /**
     * Check has permission
     * @param context
     * @param permissions
     * @return true/false
     */
    private Boolean hasPermission(Context context, String[] permissions) {

        boolean isCheck = true;
        for(String permission : permissions) {
            int check = context.checkCallingOrSelfPermission(permission);
            if(check != PackageManager.PERMISSION_GRANTED) {
                isCheck = false;
            }
        }

        return isCheck;
    }
}
