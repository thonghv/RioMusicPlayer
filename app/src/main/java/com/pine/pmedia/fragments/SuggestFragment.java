package com.pine.pmedia.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.pine.pmedia.R;
import com.pine.pmedia.adapters.SongCatRecyclerAdapter;
import com.pine.pmedia.control.PlayListDialog;
import com.pine.pmedia.helpers.CommonHelper;
import com.pine.pmedia.helpers.Constants;
import com.pine.pmedia.helpers.MediaHelper;
import com.pine.pmedia.services.MusicService;

import java.util.ArrayList;

public class SuggestFragment extends BaseFragment implements PlayListDialog.PlayListDialogListener {

    private static SuggestFragment instance = null;
    private SongCatRecyclerAdapter songCatRecyclerAdapter;
    private RecyclerView recyclePlayList;
    private LinearLayout playListLayout;
    private ImageButton addPlayListSmall;
    private ImageButton addPlayListLarge;

    private ArrayList playListData = new ArrayList();

    public static SuggestFragment getInstance() {

        if(instance == null) {
            instance = new SuggestFragment();
        }

        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        onLoadPlayList();
    }

    @Override
    protected void onHandler() {

        MusicService mService = super.getmService();
        mService.setmActivity(getmActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_suggest, container, false);
        recyclePlayList = view.findViewById(R.id.recyclePlayList);
        playListLayout = view.findViewById(R.id.playListLayout);
        addPlayListSmall = view.findViewById(R.id.addPlayListSmall);
        addPlayListLarge = view.findViewById(R.id.addPlayListLarge);

        // Clear background of recycle view
        recyclePlayList.setHasFixedSize(true);
        recyclePlayList.setItemViewCacheSize(20);
        recyclePlayList.setBackgroundResource(0);

        // Handle action on screen
        onHandleActions();

        return view;
    }

    private void onHandleActions() {

        addPlayListLarge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               CommonHelper.showPlayListDialog(getContext(), SuggestFragment.this, null);
            }
        });

        addPlayListSmall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonHelper.showPlayListDialog(getContext(), SuggestFragment.this, null);
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        super.setmActivity((Activity) context);
    }


    @Override
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);

        if(playListData.isEmpty()) {
            addPlayListSmall.setVisibility(View.INVISIBLE);
            recyclePlayList.setVisibility(View.INVISIBLE);
            playListLayout.setVisibility(View.VISIBLE);

        } else {
            recyclePlayList.setItemAnimator(new DefaultItemAnimator());
            recyclePlayList.setLayoutManager(new LinearLayoutManager(super.getmActivity()));

            songCatRecyclerAdapter = new SongCatRecyclerAdapter(super.getmActivity(), playListData, Constants.VIEW_SUGGEST);
            recyclePlayList.setAdapter(songCatRecyclerAdapter);
        }
    }

    private void onLoadPlayList() {
        synchronized(this) {
            this.playListData = MediaHelper.getAllPLayList(getmActivity());
        }
    }

    private void onReloadPlayList() {

        onLoadPlayList();
        songCatRecyclerAdapter.updateData(playListData);
        songCatRecyclerAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

/*    private void showPlayListDialog(Context context) {

        FragmentActivity fragmentActivity = (FragmentActivity) context;
        FragmentManager fm = fragmentActivity.getSupportFragmentManager();
        PlayListDialog editNameDialogFragment = new PlayListDialog();
        editNameDialogFragment.setTargetFragment(SuggestFragment.this, 300);
        editNameDialogFragment.show(fm, Constants.PLAY_LIST_DIALOG_NAME);
    }*/

    @Override
    public void onFinishPlayListDialog(String playListName) {
        onReloadPlayList();
    }
}