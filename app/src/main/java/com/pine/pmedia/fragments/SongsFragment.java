package com.pine.pmedia.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
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
import com.pine.pmedia.App;
import com.pine.pmedia.R;
import com.pine.pmedia.adapters.SongRecyclerAdapter;
import com.pine.pmedia.helpers.MediaHelper;
import com.pine.pmedia.models.Song;
import com.pine.pmedia.services.MusicService;
import com.pine.pmedia.sqlite.DBManager;

import java.util.ArrayList;

public class SongsFragment extends BaseFragment {

    private static SongsFragment instance = null;
    private SongRecyclerAdapter songRecyclerAdapter;
    private RecyclerView recyclerView;
    private ArrayList<Song> songs = new ArrayList();

    public static SongsFragment getInstance() {

        if(instance == null) {
            instance = new SongsFragment();
        }

        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        onLoadSongList();
    }

    @Override
    protected void onHandler() {

        MusicService mService = super.getmService();
        mService.setmActivity(getmActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_songs, container, false);
        recyclerView = view.findViewById(R.id.recycleViewSongs);

        // Clear background of recycle view
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setBackgroundResource(0);

/*        shuffleLabel = view.findViewById(R.id.shuffleLabel);
        Typeface customFace = Typeface.createFromAsset(getmActivity().getAssets(), Constants.FONT_ROBOTO_LIGHT);
        shuffleLabel.setTypeface(customFace);*/

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

        //ArrayList<Song> songsLibrary = MediaHelper.getSongs(getmActivity(), 0L, 0L);

        ArrayList<Song> songs = new ArrayList<>();
        //songs.add(new Song());
        songs.addAll(this.songs);

        songRecyclerAdapter = new SongRecyclerAdapter(songs, super.getmActivity());
        recyclerView.setAdapter(songRecyclerAdapter);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void onLoadSongList() {
        songs = MediaHelper.getSongs(getmActivity(), 0L, 0L);
        System.out.println("");
    }

    public void onReloadSongList() {
        new ExecuteReloadSongList().execute();
    }

    private class ExecuteReloadSongList extends AsyncTask<String, Void, Activity> {

        public ExecuteReloadSongList() {
        }

        @Override
        protected Activity doInBackground(String... strings) {
            onLoadSongList();
            songRecyclerAdapter.updateSongs(songs);
            return null;
        }

        @Override
        protected void onPostExecute(Activity activity) {
            songRecyclerAdapter.notifyDataSetChanged();
        }
    }
}

