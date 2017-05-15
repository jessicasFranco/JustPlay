package com.example.filip.justplay.Fragments;

/**
 Class of PlayBar
 */
import android.media.MediaPlayer;
import android.media.session.MediaController;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatDelegate;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.MediaController.MediaPlayerControl;
import android.widget.Toast;

import com.example.filip.justplay.R;

import java.util.concurrent.TimeUnit;

public class Playbar extends Fragment{

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  Bundle savedInstanceState)
    {
       View rootView = inflater.inflate(R.layout.playbar, container, false);

        return rootView;
    }
}
