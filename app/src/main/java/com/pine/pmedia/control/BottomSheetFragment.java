package com.pine.pmedia.control;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.pine.pmedia.App;
import com.pine.pmedia.R;
import com.pine.pmedia.adapters.SongCatRecyclerAdapter;
import com.pine.pmedia.helpers.Constants;
import com.pine.pmedia.helpers.MediaHelper;
import com.pine.pmedia.models.Filter;
import com.pine.pmedia.models.Song;
import com.pine.pmedia.sqlite.DBManager;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;

public class BottomSheetFragment extends BottomSheetDialogFragment {

    private Context context;
    private DBManager dbManager;
    private RecyclerView recyclerQueueSong;
    private ArrayList<Song> queueSong;
    private SongCatRecyclerAdapter queueSongRecyclerAdapter;

    public BottomSheetFragment(Context context, DBManager dbManager,
                               RecyclerView recyclerQueueSong, ArrayList<Song> queueSong,
                               SongCatRecyclerAdapter queueSongRecyclerAdapter) {
        this.context = context;
        this.dbManager = dbManager;
        this.recyclerQueueSong = recyclerQueueSong;
        this.queueSong = queueSong;
        this.queueSongRecyclerAdapter = queueSongRecyclerAdapter;
    }

    @Override
    public void setupDialog(Dialog dialog, int style) {

/*        LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = li.inflate(R.layout.bottom_dialog_queue, null);

        ImageButton shuffleNowPlaying = view.findViewById(R.id.shuffleNowPlaying);
        ImageButton addSongNowPlaying = view.findViewById(R.id.addSongNowPlaying);
        ImageButton deleteNowPlaying = view.findViewById(R.id.deleteNowPlaying);

        recyclerQueueSong = view.findViewById(R.id.recycleViewSongsCat);
        recyclerQueueSong.setItemAnimator(new DefaultItemAnimator());
        recyclerQueueSong.setLayoutManager(new LinearLayoutManager(context));

        shuffleNowPlaying.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shuffleQueueSong();
            }
        });

        addSongNowPlaying.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        deleteNowPlaying.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        Filter filter = MediaHelper.getQueue(dbManager, App.getInstance().getMediaPlayList());
        queueSong = filter.getSongs();

        TextView headerSheetDialogControl = view.findViewById(R.id.headerSheetDialog);
        headerSheetDialogControl.setText(getResources().getString(R.string.nowPlaying)
                + Constants.SPACE + "(" + queueSong.size() + ")");

        queueSongRecyclerAdapter = new SongCatRecyclerAdapter(context, queueSong, Constants.VIEW_QUEUE);
        recyclerQueueSong.setAdapter(queueSongRecyclerAdapter);*/

        BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) dialog;
        bottomSheetDialog.setContentView(R.layout.bottom_dialog_queue);

        recyclerQueueSong = bottomSheetDialog.findViewById(R.id.recycleViewSongsCat);
        recyclerQueueSong.setItemAnimator(new DefaultItemAnimator());
        recyclerQueueSong.setLayoutManager(new LinearLayoutManager(context));

        Filter filter = MediaHelper.getQueue(dbManager, App.getInstance().getMediaPlayList());
        queueSong = filter.getSongs();

        TextView headerSheetDialogControl = bottomSheetDialog.findViewById(R.id.headerSheetDialog);
        headerSheetDialogControl.setText(getResources().getString(R.string.nowPlaying)
                + Constants.SPACE + "(" + queueSong.size() + ")");

        queueSongRecyclerAdapter = new SongCatRecyclerAdapter(context, queueSong, Constants.VIEW_QUEUE);
        recyclerQueueSong.setAdapter(queueSongRecyclerAdapter);

        try {
            Field behaviorField = bottomSheetDialog.getClass().getDeclaredField("behavior");
            behaviorField.setAccessible(true);
            final BottomSheetBehavior behavior = (BottomSheetBehavior) behaviorField.get(bottomSheetDialog);
            behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {

                @Override
                public void onStateChanged(@NonNull View bottomSheet, int newState) {
                    if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                        //behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                        bottomSheet.requestLayout();
                        bottomSheet.invalidate();
                    }
                }

                @Override
                public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                }
            });
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }


    /**
     * Shuffle queue list song
     */
    private void shuffleQueueSong() {

        Collections.shuffle(queueSong);
        queueSongRecyclerAdapter.updateData(queueSong);
        queueSongRecyclerAdapter.notifyDataSetChanged();
    }
}
