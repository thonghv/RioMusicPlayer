package com.pine.pmedia.fragments;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pine.pmedia.R;
import com.pine.pmedia.adapters.AlbumRecyclerAdapter;
import com.pine.pmedia.helpers.MediaHelper;
import com.pine.pmedia.models.Album;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class AlbumsFragment extends BaseFragment {

    private static AlbumsFragment instance = null;

    RecyclerView recyclerView;
    ArrayList<Album> arrayList = new ArrayList<>();

    public AlbumsFragment() {

    }

    public static AlbumsFragment getInstance() {

        if(instance == null) {
            instance = new AlbumsFragment();
        }

        return new AlbumsFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        super.setmActivity((Activity) context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_albums, container, false);
        recyclerView = view.findViewById(R.id.albumContent);

        // Load album list
        ArrayList<Album> arrayList = MediaHelper.getAlbums(getmActivity());
        AlbumRecyclerAdapter adapter = new AlbumRecyclerAdapter(super.getActivity(), arrayList);
        recyclerView.setAdapter(adapter);
        GridLayoutManager manager = new GridLayoutManager(super.getContext(), 3, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);

        return view;
    }
}
