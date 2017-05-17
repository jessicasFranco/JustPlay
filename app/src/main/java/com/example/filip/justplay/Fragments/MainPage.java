package com.example.filip.justplay.Fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;

import com.example.filip.justplay.R;
import com.example.filip.justplay.Song;

import java.util.ArrayList;

public class MainPage extends Fragment {

    private ImageButton playSeatedFolder;
    private ImageButton playWalkingFolder;
    private ImageButton playJoggingFolder;

    public MainPage() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_page, container, false);

        playSeatedFolder =  (ImageButton) view.findViewById(R.id.playButtonR);
        playSeatedFolder.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                setView("Resting");
            }
        });

        playWalkingFolder =  (ImageButton) view.findViewById(R.id.playButtonW);
        playWalkingFolder.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                setView("Walking");
            }
        });

        playJoggingFolder =  (ImageButton) view.findViewById(R.id.playButtonJ);
        playWalkingFolder.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                setView("Jogging");
            }
        });

        return view;
    }

    private void setView(String folder){

        PlayList playList = new PlayList();
        Bundle bundle = new Bundle();
        bundle.putString("folder", folder);
        playList.setArguments(bundle);
        FragmentManager manager = getActivity().getSupportFragmentManager();
        manager.beginTransaction().replace(
                R.id.layoutCM,
                playList,
                playList.getTag()).commit();
    }
}