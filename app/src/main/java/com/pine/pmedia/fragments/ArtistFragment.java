package com.pine.pmedia.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pine.pmedia.R;
import com.pine.pmedia.adapters.ArtistRecyclerAdapter;
import com.pine.pmedia.helpers.MediaHelper;
import com.pine.pmedia.models.Album;
import com.pine.pmedia.models.Artist;

import java.util.ArrayList;

public class ArtistFragment extends BaseFragment {

    private static ArtistFragment instance = null;

    RecyclerView recyclerView;
    ArrayList<Album> arrayList = new ArrayList<>();
    private ArtistRecyclerAdapter artistRecyclerAdapter;

    public ArtistFragment() {
    }

    public static ArtistFragment getInstance() {

        if(instance == null) {
            instance = new ArtistFragment();
        }

        return new ArtistFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        super.setmActivity((Activity) context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_artist, container, false);
        recyclerView = view.findViewById(R.id.recycleViewArtist);

        // Clear background of recycle view
        recyclerView.setBackgroundResource(0);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new LinearLayoutManager(super.getmActivity()));

        ArrayList<Artist> artists = MediaHelper.getArtist(getmActivity());

        artistRecyclerAdapter = new ArtistRecyclerAdapter(artists, super.getmActivity());
        recyclerView.setAdapter(artistRecyclerAdapter);
    }
}
