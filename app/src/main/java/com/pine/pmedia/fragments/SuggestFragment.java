package com.pine.pmedia.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pine.pmedia.R;
import com.pine.pmedia.activities.FilterActivity;
import com.pine.pmedia.adapters.SongCatRecyclerAdapter;
import com.pine.pmedia.control.AddPlayListDialog;
import com.pine.pmedia.helpers.CommonHelper;
import com.pine.pmedia.helpers.Constants;
import com.pine.pmedia.helpers.MediaHelper;
import com.pine.pmedia.services.MusicService;
import com.pine.pmedia.sqlite.DBManager;

import java.util.ArrayList;

public class SuggestFragment extends BaseFragment implements AddPlayListDialog.PlayListDialogListener {

    private static SuggestFragment instance = null;
    private SongCatRecyclerAdapter songCatRecyclerAdapter;
    private RecyclerView recyclePlayList;
    private LinearLayout playListLayout;
    private ImageButton addPlayListSmall;
    private ImageButton addPlayListLarge;

    private TextView numberOfFavoritesControl;
    private TextView numberOfLastPlayedControl;
    private TextView numberOfAddRecentControl;

    private LinearLayout layoutFavoriteSong;
    private LinearLayout layoutLastPlayed;
    private LinearLayout layoutRecentAdded;

    private DBManager dbManager;

    private ArrayList playListData = new ArrayList();


    private int numberOfFavorites = 0;
    private int numberOfLastPlayed = 0;
    private int numberOfAddRecnet = 0;

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

        numberOfFavoritesControl = view.findViewById(R.id.numberOfFavorites);
        numberOfLastPlayedControl = view.findViewById(R.id.numberOfLastPlayed);
        numberOfAddRecentControl = view.findViewById(R.id.numberOfAddRecent);

        layoutFavoriteSong = view.findViewById(R.id.layoutFavoriteSong);
        layoutLastPlayed = view.findViewById(R.id.layoutLastPlayed);
        layoutRecentAdded = view.findViewById(R.id.layoutRecentAdded);

        // Clear background of recycle view
        recyclePlayList.setHasFixedSize(true);
        recyclePlayList.setItemViewCacheSize(20);
        recyclePlayList.setBackgroundResource(0);

        // Handle action on screen
        onHandleActions();

        dbManager = new DBManager(getmActivity());
        dbManager.open();

        return view;
    }

    private void onHandleActions() {

        addPlayListLarge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               CommonHelper.showPlayListDialog(getContext(), SuggestFragment.this,
                       Constants.CREATE_DIALOG, "", -1);
            }
        });

        addPlayListSmall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonHelper.showPlayListDialog(getContext(), SuggestFragment.this,
                        Constants.CREATE_DIALOG, "", -1);
            }
        });

        layoutFavoriteSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), FilterActivity.class);

                Bundle param = new Bundle();
                param.putInt(Constants.KEY_CAT_TYPE, Constants.VIEW_FAVORITE);
                param.putString(Constants.KEY_TITLE_CAT, getResources().getString(R.string.favorite));
                String note = numberOfFavorites + Constants.SPACE + Constants.SONGS
                        + Constants.MINUS + CommonHelper.toFormatTime(0);
                param.putString(Constants.KEY_NOTE_CAT, note);

                intent.putExtras(param);
                getmActivity().startActivity(intent);
            }
        });

        layoutLastPlayed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(v.getContext(), FilterActivity.class);

                Bundle param = new Bundle();
                param.putInt(Constants.KEY_CAT_TYPE, Constants.VIEW_LAST_PLAYED);
                param.putString(Constants.KEY_TITLE_CAT, getResources().getString(R.string.lastPlayed));
                String note = numberOfLastPlayed + Constants.SPACE + Constants.SONGS
                        + Constants.MINUS + CommonHelper.toFormatTime(0);
                param.putString(Constants.KEY_NOTE_CAT, note);

                intent.putExtras(param);
                getmActivity().startActivity(intent);
            }
        });

        layoutRecentAdded.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), FilterActivity.class);

                Bundle param = new Bundle();
                param.putInt(Constants.KEY_CAT_TYPE, Constants.VIEW_RECENT_ADDED);
                param.putString(Constants.KEY_TITLE_CAT, getResources().getString(R.string.recentAdded));
                String note = numberOfAddRecnet + Constants.SPACE + Constants.SONGS
                        + Constants.MINUS + CommonHelper.toFormatTime(0);
                param.putString(Constants.KEY_NOTE_CAT, note);

                intent.putExtras(param);
                getmActivity().startActivity(intent);
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        super.setmActivity((Activity) context);
    }

    @Override
    public void onResume() {
        super.onResume();
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

        // Favorite songs
        onLoadFavorite();

        // Last played
        onLoadCountHistory();

        // Recent add
        onLoadCountAddRecent();
    }

    public void onLoadFavorite() {

        // Count favorite song.
        this.numberOfFavorites = dbManager.getCountFavorite();
        numberOfFavoritesControl.setText(String.valueOf(numberOfFavorites));
    }

    public void onLoadCountHistory() {

        // Count history song.
        this.numberOfLastPlayed = dbManager.getCountHistory();
        numberOfLastPlayedControl.setText(String.valueOf(numberOfLastPlayed));
    }

    public void onLoadCountAddRecent() {

        // Count add recent song.
        this.numberOfAddRecnet = MediaHelper.getCountAddRecent(getmActivity());
        numberOfAddRecentControl.setText(String.valueOf(numberOfAddRecnet));
    }

    private void onLoadPlayList() {
        synchronized(this) {
            this.playListData = MediaHelper.getAllPLayList(getmActivity());
        }
    }

    public void onReloadPlayList() {

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
        AddPlayListDialog editNameDialogFragment = new AddPlayListDialog();
        editNameDialogFragment.setTargetFragment(SuggestFragment.this, 300);
        editNameDialogFragment.show(fm, Constants.PLAY_LIST_DIALOG_NAME);
    }*/

    @Override
    public void onFinishPlayListDialog(String playListName) {
        onReloadPlayList();
    }
}