package com.pine.pmedia.helpers;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.pine.pmedia.App;
import com.pine.pmedia.R;
import com.pine.pmedia.activities.QueueActivity;
import com.pine.pmedia.control.AddPlayListDialog;
import com.pine.pmedia.control.MediaPlayListDialog;
import com.pine.pmedia.models.Song;
import com.pine.pmedia.services.MusicService;
import com.pine.pmedia.sqlite.DBManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class CommonHelper {

    private static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

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

    public static String toFormatTimeMS(int time) {

        return String.format(Constants.TIME_FOMAT_M_S,
                TimeUnit.MILLISECONDS.toMinutes(time) -
                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(time)),
                TimeUnit.MILLISECONDS.toSeconds(time) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(time)));
    }

    public static String toFormatTimeHMS(int time) {

        return String.format(Constants.TIME_FOMAT_H_M_S,
                TimeUnit.MILLISECONDS.toHours(time),
                TimeUnit.MILLISECONDS.toMinutes(time) -
                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(time)),
                TimeUnit.MILLISECONDS.toSeconds(time) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(time)));
    }

    public static String toFormatSize(int size) {

        String hrSize;

        DecimalFormat dec = new DecimalFormat("0.00");
        double m = size/1024.0;
        double g = size/1048576.0;
        double t = size/1073741824.0;

        if (t > 1) {
            hrSize = dec.format(t).concat("GB");
        } else if (g > 1) {
            hrSize = dec.format(g).concat("MB");
        } else if (m > 1) {
            hrSize = dec.format(m).concat("KB");
        } else {
            hrSize = dec.format(size).concat("B");
        }

        return hrSize;
    }

    public static String toUrlPlayTrack(long id) {

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

    public static int generateImgCover() {

        final int index = new Random().nextInt(5);
        switch (index) {
            case 0:
                return R.drawable.g_02;
            case 1:
                return R.drawable.g_03;
            case 2:
                return R.drawable.g_04;
            case 3:
                return R.drawable.g_05;
            default:return R.drawable.g_01;
        }
    }

    public static Drawable createBlurredImageFromBitmap(Bitmap bitmap, Context context, int inSampleSize) {

        android.renderscript.RenderScript rs = android.renderscript.RenderScript.create(context);
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = inSampleSize;

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] imageInByte = stream.toByteArray();
        ByteArrayInputStream bis = new ByteArrayInputStream(imageInByte);
        Bitmap blurTemplate = BitmapFactory.decodeStream(bis, null, options);

        final android.renderscript.Allocation input = android.renderscript.Allocation.createFromBitmap(rs, blurTemplate);
        final android.renderscript.Allocation output = android.renderscript.Allocation.createTyped(rs, input.getType());
        final android.renderscript.ScriptIntrinsicBlur script = android.renderscript.ScriptIntrinsicBlur.create(rs, android.renderscript.Element.U8_4(rs));
        script.setRadius(8f);
        script.setInput(input);
        script.forEach(output);
        output.copyTo(blurTemplate);

        return new BitmapDrawable(context.getResources(), blurTemplate);
    }

    public static void onDisplayImage(String url, ImageView imageView) {

        ImageLoader.getInstance().displayImage(url, new ImageViewAware(imageView),
                new DisplayImageOptions.Builder()
                        .imageScaleType(ImageScaleType.EXACTLY)
                        .cacheInMemory(true)
                        .resetViewBeforeLoading(true)
                        .bitmapConfig(Bitmap.Config.RGB_565)
                        .build()
                , new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {

                    }
                    @Override
                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                        System.out.println("Error ...");
                    }
                },null);
    }

    public static void onShowKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.SHOW_IMPLICIT);
    }

    public static void onHideKeyboard(Activity activity, EditText editText) {
        InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    public static void showPlayListDialog(Context context, Fragment fragment,
                                          int actionType, String playListName, long playListId) {

        FragmentActivity fragmentActivity = (FragmentActivity) context;
        FragmentManager fm = fragmentActivity.getSupportFragmentManager();
        AddPlayListDialog addPlayListDia = new AddPlayListDialog(actionType, playListName, playListId);

        if(fragment != null) {
            addPlayListDia.setTargetFragment(fragment, 300);
        }

        addPlayListDia.show(fm, Constants.PLAY_LIST_DIALOG_NAME);
    }

    public static String dateToFormat(Date date) {

        DateFormat dateFormat = new SimpleDateFormat(CommonHelper.DATE_TIME_FORMAT);
        return dateFormat.format(date);
    }

    /**
     * Add song into favorite list
     * @param context
     * @param dbManager
     * @param id
     * @param title
     */
    public static void onFavorite(Context context, DBManager dbManager, long id, String title) {

        if(dbManager.isExitsFavorite(id)) {
            Toast.makeText(context , R.string.songIsExitsFavorite, Toast.LENGTH_SHORT).show();
            return;
        }

        dbManager.insertFavorite(id, title);
        Toast.makeText(context, R.string.addSongFavoriteSuccess, Toast.LENGTH_SHORT).show();
        App.getInstance().isReloadFavorite = true;
    }

    /**
     * Add song in play next
     * @param context
     * @param mService
     * @param songs
     * @param id
     */
    public static void addPlayNext(Context context, MusicService mService, ArrayList<Song> songs, long id) {

        Toast.makeText(context, R.string.addInPlayNextSuccess, Toast.LENGTH_SHORT).show();

        Song songFind = MediaHelper.getSongById(songs, id);
        if(songFind != null) {
            int nextIndex = mService.getCurrentPosition() + 1;
            mService.getPlayingQueue().add(nextIndex, songFind);
        }
    }

    /**
     * Show play list
     * @param context
     * @param songIds
     */
    public static void onShowMediaPlayListDialog(Context context, List<Long> songIds) {

        final FragmentManager fm = ((AppCompatActivity) context).getSupportFragmentManager();
        MediaPlayListDialog mediaPlayListDialog =
                new MediaPlayListDialog(context, songIds);
        mediaPlayListDialog.show(fm, Constants.PLAY_LIST_DIALOG_NAME);
    }

    /**
     * Set ring tone
     * @param context
     * @param pathString
     */
    public static void setRingTone(Context context, String pathString){

        // Create File object for the specified ring tone path
        File f = new File(pathString);

        // Insert the ring tone to the content provider
        ContentValues value=new ContentValues();
        value.put(MediaStore.MediaColumns.DATA, f.getAbsolutePath());
        value.put(MediaStore.MediaColumns.TITLE, f.getName());
        value.put(MediaStore.MediaColumns.SIZE, f.length());
        value.put(MediaStore.MediaColumns.MIME_TYPE, "audio/*");
        value.put(MediaStore.Audio.Media.ARTIST, "artist");
        value.put(MediaStore.Audio.Media.DURATION, 500);
        value.put(MediaStore.Audio.Media.IS_ALARM, false);
        value.put(MediaStore.Audio.Media.IS_MUSIC, false);
        value.put(MediaStore.Audio.Media.IS_NOTIFICATION, false);
        value.put(MediaStore.Audio.Media.IS_RINGTONE, true);
        ContentResolver cr = context.getContentResolver();

        //Insert it into the database
        Uri url= MediaStore.Audio.Media.getContentUriForPath(f.getAbsolutePath());
        Uri addedUri = cr.insert(url, value);

        // Set default ring tone
        RingtoneManager.setActualDefaultRingtoneUri(context, RingtoneManager.TYPE_RINGTONE, addedUri);
    }

    public static void updateSongPLaying(DBManager dbManager, long songId) {
        dbManager.updateSettingByKey(Constants.SETTING_SONG_PLAYING, String.valueOf(songId));
    }

    public static void onShare(Context context, String subject, String content) {

        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_SUBJECT,subject);
        shareIntent.putExtra(Intent.EXTRA_TEXT, content);
        shareIntent.setType("text/plain");
        context.startActivity(shareIntent);
    }

    /**
     * Show queue song screen
     */
    public static void onShowScreenQueueSong(Context context) {

        Bundle param = new Bundle();
        param.putInt(Constants.KEY_CAT_TYPE, Constants.VIEW_QUEUE);
        param.putString(Constants.KEY_TITLE_CAT, context.getResources().getString(R.string.nowPlaying));
        param.putString(Constants.KEY_NOTE_CAT, "");

        Intent intent = new Intent(context, QueueActivity.class);
        intent.putExtras(param);
        context.startActivity(intent);
        //overridePendingTransition(R.animator.push_down_out, R.animator.push_down_in);
    }
}
