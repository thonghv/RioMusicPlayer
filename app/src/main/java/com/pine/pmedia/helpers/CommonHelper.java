package com.pine.pmedia.helpers;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;

import androidx.core.content.ContextCompat;

import com.pine.pmedia.R;
import com.pine.pmedia.models.Song;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class CommonHelper {

    public static ArrayList<Song> parseSongs(String jString) {
        ArrayList<Song> result = new ArrayList<>();
        try {
            JSONArray jsonArrays = new JSONArray(jString);
            for (int i = 0; i < jsonArrays.length(); i++) {
                String songId = jsonArrays.getJSONObject(i).getString(Constants.KEY_ID);
                String songTitle = jsonArrays.getJSONObject(i).getString(Constants.KEY_TITLE);
                String songImage = jsonArrays.getJSONObject(i).getString(Constants.KEY_ARTWORK);
                String songDuration = jsonArrays.getJSONObject(i).getString(Constants.KEY_DURATION);
                String songLikeCount = jsonArrays.getJSONObject(i).getString(Constants.KEY_LIKE_COUNT);

                JSONObject objUser = jsonArrays.getJSONObject(i).getJSONObject(Constants.KEY_USER);
                String artist = objUser.getString(Constants.KEY_USERNAME);

                Song song = new Song();
                result.add(song);

                song.set_id(Integer.parseInt(songId));
                song.set_title(songTitle);
                song.set_image(songImage);
                song.set_duration(Integer.parseInt(songDuration));
                song.set_likeCount(Integer.parseInt(songLikeCount));
                song.set_artist(artist);
                song.set_path(CommonHelper.toUrlPlayTrack(song.get_id()));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result;
    }

    public static String toApi(String search, int limit, int offset) {

        return Constants.API + Constants.SEARCH + search
                + Constants.LIMIT + limit
                + Constants.OFFSET + offset
                + Constants.A_CLIENT_ID + Constants.TOKEN;
    }

    public static String toFormatTime(int time) {

        return String.format(Constants.TIME_FOMAT_M_S,
                TimeUnit.MILLISECONDS.toMinutes(time) -
                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(time)),
                TimeUnit.MILLISECONDS.toSeconds(time) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(time)));
    }

    public static String toUrlPlayTrack(int id) {

        return Constants.API + Constants.KEY_TRACKS
                + "/" + id
                + Constants.KEY_STREAM
                + Constants.Q_CLIENT_ID + Constants.TOKEN;
    }

    public static MediaMetadataRetriever getMediaMetadataRetriever(String url) {
        MediaMetadataRetriever mCoverMedia = new MediaMetadataRetriever();
        mCoverMedia.setDataSource(url, new HashMap<String, String>());
        return mCoverMedia;
    }

    public static Bitmap getColoredBitmap(Activity activity, int newColor) {

        Drawable drawable = ContextCompat.getDrawable(activity, R.drawable.demo);
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.setColorFilter(new PorterDuffColorFilter(newColor, PorterDuff.Mode.SRC_IN));
        drawable.draw(canvas);
        return bitmap;
    }

    public static int getTextColor(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("Prefs", Context.MODE_PRIVATE);
        return prefs.getInt("text_color", context.getResources().getColor(R.color.p_background_01));
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    /**
     * https://stackoverflow.com/questions/33072365/how-to-darken-a-given-color-int
     * @param color color provided
     * @param factor factor to make color darker
     * @return int as darker color
     */
    public static int manipulateColor(int color, float factor) {
        int a = Color.alpha(color);
        int r = Math.round(Color.red(color) * factor);
        int g = Math.round(Color.green(color) * factor);
        int b = Math.round(Color.blue(color) * factor);
        return Color.argb(a,
                Math.min(r, 255),
                Math.min(g, 255),
                Math.min(b, 255));
    }

    static public void addRecent(Context context, String streamUrl, String name, String author, String imgUrl) {
        try {
            Map<String, String> map = CommonHelper.getRecent(context);
            if (!map.keySet().contains(streamUrl)) {
                SharedPreferences recent = context.getSharedPreferences(Constants.KEY_RECENT, 0);
                SharedPreferences.Editor editor = recent.edit();
                editor.putString(streamUrl, name + Constants.SPLITSTR + author + Constants.SPLITSTR + imgUrl);
                editor.commit();

                if (map.size() >= Constants.LIMIT_RECENT) {
                    CommonHelper.removeRecent(context, map.keySet().toArray()[0].toString());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    static public Map getRecent(Context context){
        SharedPreferences recent = context.getSharedPreferences(Constants.KEY_RECENT,0);
        return recent.getAll();
    }

    static public void removeRecent(Context context, String id){
        try{
            SharedPreferences recent = context.getSharedPreferences(Constants.KEY_RECENT, 0);
            SharedPreferences.Editor editor = recent.edit();
            editor.remove(id);
            editor.commit();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    static public String getCurrentActivity(Context context) {

        ActivityManager am = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        ComponentName componentName = am.getRunningTasks(1).get(0).topActivity;

        return componentName.getClassName();
    }

    private static final float BLUR_RADIUS = 25f;
    static public Bitmap blur(Context context, Bitmap image) {
        if (null == image) return null;
        Bitmap outputBitmap = Bitmap.createBitmap(image);
        final RenderScript renderScript = RenderScript.create(context);
        Allocation tmpIn = Allocation.createFromBitmap(renderScript, image);
        Allocation tmpOut = Allocation.createFromBitmap(renderScript, outputBitmap);
        //Intrinsic Gausian blur filter
        ScriptIntrinsicBlur theIntrinsic = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript));
        theIntrinsic.setRadius(BLUR_RADIUS);
        theIntrinsic.setInput(tmpIn);
        theIntrinsic.forEach(tmpOut);
        tmpOut.copyTo(outputBitmap);
        return outputBitmap;
    }
}
