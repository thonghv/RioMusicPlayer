package com.pine.pmedia.control;

import android.content.Intent;
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
import com.pine.pmedia.helpers.Constants;
import com.pine.pmedia.helpers.MediaHelper;

public class AddPlayListDialog extends DialogFragment implements IDialogFragment{

    private EditText mEditText;
    private LinearLayout cancelDialogLayout;
    private LinearLayout confirmDialogLayout;
    private TextView dialogOKText;

    private int actionType;
    private long playListId;
    private String playListName;

    public interface PlayListDialogListener {
        void onFinishPlayListDialog(String inputText);
    }

    public AddPlayListDialog() {
    }

    public AddPlayListDialog(int actionType, String playListName, long playListId) {
        this.actionType = actionType;
        this.playListName = playListName;
        this.playListId = playListId;
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
        dialogOKText.setTextColor(getContext().getResources().getColor(R.color.p_gray));

        confirmDialogLayout = view.findViewById(R.id.dialogOK);
        confirmDialogLayout.setEnabled(false);

        if(playListName!= null && !playListName.isEmpty()) {
            mEditText.setText(playListName);
        }
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
                    dialogOKText.setTextColor(getContext().getResources().getColor(R.color.white));
                } else {
                    dialogOKText.setEnabled(false);
                    confirmDialogLayout.setEnabled(false);
                    dialogOKText.setTextColor(getContext().getResources().getColor(R.color.p_gray));
                }

                if(actionType == Constants.UPDATE_DIALOG) {
                    if(s.toString().equals(playListName)) {
                        dialogOKText.setEnabled(false);
                        confirmDialogLayout.setEnabled(false);
                        dialogOKText.setTextColor(getContext().getResources().getColor(R.color.p_gray));
                    } else {
                        dialogOKText.setEnabled(true);
                        confirmDialogLayout.setEnabled(true);
                        dialogOKText.setTextColor(getContext().getResources().getColor(R.color.white));
                    }
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
                switch (actionType) {
                    case Constants.CREATE_DIALOG:
                        if(onCreatePlayList() > 0) {
                            dismiss();
                            sendBackResult();
                            Toast.makeText(getContext(), R.string.addPlayListSuccess, Toast.LENGTH_SHORT).show();
                            sendBroadcast(Constants.RELOAD_ADAPTER_PLAYLIST, null);
                        }
                        break;
                    case Constants.UPDATE_DIALOG:
                        if(onUpdatePlayList() > 0) {
                            dismiss();
                            sendBackResult();
                            Toast.makeText(getContext(), R.string.updatePlayListSuccess, Toast.LENGTH_SHORT).show();
                            sendBroadcast(Constants.RELOAD_ADAPTER_PLAYLIST, null);
                        }
                        break;
                }
            }
        });

        // Show soft keyboard automatically and request focus to field
        mEditText.requestFocus();
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    public void sendBackResult() {
//        PlayListDialogListener listener = (PlayListDialogListener) getTargetFragment();
//        if(listener != null) {
//            listener.onFinishPlayListDialog(mEditText.getText().toString());
//        }
    }

    public long onCreatePlayList() {

        String playListName = mEditText.getText().toString();
        long playListId = MediaHelper.createPlaylist(getContext(), playListName);
        if(playListId == -1) {
            Toast.makeText(getContext(), R.string.playListNameIsExist, Toast.LENGTH_SHORT).show();
        }

        return playListId;
    }

    public long onUpdatePlayList() {
        String playListName = mEditText.getText().toString();
        long playListId = MediaHelper.updatePlaylist(getContext(), this.playListId, playListName);
        if(playListId == -1) {
            Toast.makeText(getContext(), R.string.playListNameIsExist, Toast.LENGTH_SHORT).show();
        }

        return playListId;
    }

    @Override
    public void sendBroadcast(String action, String data) {

        Intent broadcastIntent = new Intent();
        broadcastIntent.putExtra(Constants.KEY_DATA, data);
        broadcastIntent.setAction(action);

        getActivity().sendBroadcast(broadcastIntent);
    }
}
