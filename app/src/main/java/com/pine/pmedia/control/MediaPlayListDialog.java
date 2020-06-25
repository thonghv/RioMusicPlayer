package com.pine.pmedia.control;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pine.pmedia.R;
import com.pine.pmedia.adapters.SongCatRecyclerAdapter;
import com.pine.pmedia.helpers.CommonHelper;
import com.pine.pmedia.helpers.Constants;
import com.pine.pmedia.helpers.MediaHelper;

import java.util.ArrayList;
import java.util.List;

public class MediaPlayListDialog extends DialogFragment implements IDialogFragment {

    private Context mConText;
    private List<Long> songIds;

    private ImageButton addPlayListControl;
    private RecyclerView recyclerView;

    public interface PlayListDialogListener {
        void onFinishPlayListDialog(String inputText);
    }

    public MediaPlayListDialog() {
    }

    public MediaPlayListDialog(Context mConText, List<Long> songIds) {
        this.mConText = mConText;
        this.songIds = songIds;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_media_playlist, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.recyclePlayList);
        addPlayListControl = view.findViewById(R.id.addPlayListLarge);

        addPlayListControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                CommonHelper.showPlayListDialog(getContext(), null, Constants.CREATE_DIALOG, "", -1);
            }
        });

        onLoadMediaPlayList();
    }

    private void onLoadMediaPlayList() {

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        ArrayList playList = MediaHelper.getAllPLayList(mConText);

        SongCatRecyclerAdapter adapter = new SongCatRecyclerAdapter(getActivity(), playList, Constants.VIEW_PLAYLIST_DIALOG);
        adapter.setListSongCurrentIdTemp(songIds);
        adapter.setMediaPlayListDialog(this);
        recyclerView.setAdapter(adapter);
    }


    @Override
    public void sendBroadcast(String action, String data) {
        Intent broadcastIntent = new Intent();
        broadcastIntent.putExtra(Constants.KEY_DATA, data);
        broadcastIntent.setAction(action);

        getActivity().sendBroadcast(broadcastIntent);
    }
}
