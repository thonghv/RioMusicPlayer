package com.pine.pmedia.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.pine.pmedia.R;
import com.pine.pmedia.adapters.SongRecyclerAdapter;
import com.pine.pmedia.control.HidingScrollListener;
import com.pine.pmedia.helpers.Constants;
import com.pine.pmedia.helpers.MediaHelper;
import com.pine.pmedia.models.Song;
import com.pine.pmedia.services.MusicService;

import java.util.ArrayList;

public class SongsFragment extends BaseFragment {

    private static SongsFragment instance = null;
    private SongRecyclerAdapter songRecyclerAdapter;
    private RecyclerView recyclerView;
    private TextView shuffleLabel;
    private Toolbar toolbar;

    public static SongsFragment getInstance() {

        if(instance == null) {
            instance = new SongsFragment();
        }

        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

         // callData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_songs, container, false);

        View viewActivityMain = inflater.inflate(R.layout.app_bar_main, container, false);
        toolbar = viewActivityMain.findViewById(R.id.toolbar);

        recyclerView = view.findViewById(R.id.recycleViewSongs);
        recyclerView.setOnScrollListener(new HidingScrollListener() {
            @Override
            public void onHide() {
                //hideViews();
            }
            @Override
            public void onShow() {
                //showViews();
            }
        });

        // Clear background of recycle view
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setBackgroundResource(0);

//        shuffleLabel = view.findViewById(R.id.shuffleLabel);
//        Typeface customFace = Typeface.createFromAsset(getmActivity().getAssets(), Constants.FONT_ROBOTO_LIGHT);
//        shuffleLabel.setTypeface(customFace);

        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(getActivity()));

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        super.setmActivity((Activity) context);
    }

    @Override
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new LinearLayoutManager(super.getmActivity()));

        ArrayList<Song> songs = new ArrayList<>();
        songs.add(new Song());
        songs.addAll(MediaHelper.getSongs(getmActivity(), 0 , 0));

        songRecyclerAdapter = new SongRecyclerAdapter(songs, super.getmActivity());
        recyclerView.setAdapter(songRecyclerAdapter);

        initData(songs);
    }

    private void initData(ArrayList<Song> songs) {

        MusicService mService = super.getmService();
        if(mService == null) {
            return;
        }

        mService.setmActivity(getmActivity());
        mService.updatePlayingQueue(songs);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void hideViews() {
        super.getmService().sendBroadcast(Constants.SHOW_CONTENT_BOTTOM);
    }

    private void showViews() {
        super.getmService().sendBroadcast(Constants.HIDE_CONTENT_BOTTOM);
    }
}