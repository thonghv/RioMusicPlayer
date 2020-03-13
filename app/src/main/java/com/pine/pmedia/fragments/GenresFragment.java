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
import com.pine.pmedia.adapters.GenreRecyclerAdapter;
import com.pine.pmedia.helpers.Constants;
import com.pine.pmedia.helpers.MediaHelper;
import com.pine.pmedia.models.Album;
import com.pine.pmedia.models.Genre;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class GenresFragment extends BaseFragment {

    private static GenresFragment instance = null;

    RecyclerView recyclerView;
    ArrayList<Album> arrayList = new ArrayList<>();

    public GenresFragment() {

    }

    public static GenresFragment getInstance() {

        if(instance == null) {
            instance = new GenresFragment();
        }

        return new GenresFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        super.setmActivity((Activity) context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_genres, container, false);
        recyclerView = view.findViewById(R.id.genresRecycleContent);

        // Load album list
        ArrayList<Genre> genres = MediaHelper.getGenres(getmActivity());
        GenreRecyclerAdapter adapter = new GenreRecyclerAdapter(super.getActivity(), genres);
        recyclerView.setAdapter(adapter);
        GridLayoutManager manager = new GridLayoutManager(super.getContext(), 2, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);

        return view;
    }

    @Override
    protected void onHandler() {

    }
}
