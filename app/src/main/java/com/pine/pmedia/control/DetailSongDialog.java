package com.pine.pmedia.control;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.pine.pmedia.R;

public class DetailSongDialog extends DialogFragment implements IDialogFragment{

    private LinearLayout cancelDialogLayout;
    private LinearLayout confirmDialogLayout;

    private String nameSong;
    private String nameArtist;
    private String nameAlbum;
    private String duration;
    private String size;
    private String path;

    public DetailSongDialog() {
    }

    public DetailSongDialog(String nameSong, String nameArtist, String nameAlbum, String duration,
                            String size, String path) {
        this.nameSong = nameSong;
        this.nameArtist = nameArtist;
        this.nameAlbum = nameAlbum;
        this.duration = duration;
        this.size = size;
        this.path = path;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_detail_song, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        cancelDialogLayout = view.findViewById(R.id.dialogCancel);
        confirmDialogLayout = view.findViewById(R.id.dialogOK);

        EditText detailNameControl = view.findViewById(R.id.detailName);
        detailNameControl.setText(nameSong);

        EditText detailArtistControl = view.findViewById(R.id.detailArtist);
        detailArtistControl.setText(nameArtist);

        EditText detailAlbumControl = view.findViewById(R.id.detailAlbum);
        detailAlbumControl.setText(nameAlbum);

        TextView detailDurationControl = view.findViewById(R.id.detailDuration);
        detailDurationControl.setText(duration);

        TextView detailSizeControl = view.findViewById(R.id.detailSize);
        detailSizeControl.setText(size);

        TextView detailPathControl = view.findViewById(R.id.detailPath);
        detailPathControl.setText(path);

        cancelDialogLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        confirmDialogLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    @Override
    public void sendBroadcast(String action, String data) {
    }
}
