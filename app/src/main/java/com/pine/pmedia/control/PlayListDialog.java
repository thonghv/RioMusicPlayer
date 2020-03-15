package com.pine.pmedia.control;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.pine.pmedia.R;
import com.pine.pmedia.helpers.MediaHelper;

public class PlayListDialog extends DialogFragment {

    private EditText mEditText;
    private LinearLayout cancelDialogLayout;
    private LinearLayout confirmDialogLayout;
    private TextView dialogOKText;

    public interface PlayListDialogListener {
        void onFinishPlayListDialog(String inputText);
    }

    public PlayListDialog() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_create_playlist, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mEditText = view.findViewById(R.id.playListInput);
        cancelDialogLayout = view.findViewById(R.id.dialogCancel);
        dialogOKText = view.findViewById(R.id.dialogOKText);
        dialogOKText.setEnabled(false);

        confirmDialogLayout = view.findViewById(R.id.dialogOK);
        confirmDialogLayout.setEnabled(false);

        mEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() != 0) {
                    dialogOKText.setEnabled(true);
                    confirmDialogLayout.setEnabled(true);
                } else {
                    dialogOKText.setEnabled(false);
                    confirmDialogLayout.setEnabled(false);
                }
            }
        });

        cancelDialogLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        confirmDialogLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onCreatePlayList() > 0) {
                    sendBackResult();
                    dismiss();
                }
            }
        });

        // Show soft keyboard automatically and request focus to field
        mEditText.requestFocus();
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    public void sendBackResult() {
        PlayListDialogListener listener = (PlayListDialogListener) getTargetFragment();
        listener.onFinishPlayListDialog(mEditText.getText().toString());
    }

    public long onCreatePlayList() {
        String playListName = mEditText.getText().toString();
        long playListId = MediaHelper.createPlaylist(getContext(), playListName);
        if(playListId == -1) {
            Toast.makeText(getContext(), R.string.playListNameIsExist, Toast.LENGTH_SHORT).show();
        }

        return playListId;
    }
}
