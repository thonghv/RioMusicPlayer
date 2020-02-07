package com.pine.pmedia.extensions;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class BitmapAsync extends AsyncTask<String, String, Bitmap> {

    private Bitmap bitmap;

    public BitmapAsync(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    @Override
    protected Bitmap doInBackground(String... params) {

        Bitmap bm = loadImageFromUrl(params[0]);
        return bm;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    private Bitmap loadImageFromUrl(String targetUrl){
        Bitmap bm = null;

        try {
            URL url = new URL(targetUrl);
            URLConnection connection = url.openConnection();
            InputStream inputStream = connection.getInputStream();
            bm = BitmapFactory.decodeStream(inputStream);
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return bm;
    }
}
