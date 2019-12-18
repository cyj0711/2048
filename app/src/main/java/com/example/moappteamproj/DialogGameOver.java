package com.example.moappteamproj;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class DialogGameOver extends DialogFragment {

    public static final String TAG_EVENT_DIALOG = "game_over";

    public DialogGameOver() {}

    public static DialogGameOver getInstance(){
        DialogGameOver e = new DialogGameOver();
        return e;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.activity_dialog_game_over,container);

        return v;
    }
}
