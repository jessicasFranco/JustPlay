package com.example.filip.justplay;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class SongScreen extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.song_screen, container, false);

       Bundle bundle = getArguments();

        String songTitle = bundle.getString("songTitle");
        String songArtist = bundle.getString("songArtist");
        String durationSong = bundle.getString("durationSong");

        TextView titleSong = (TextView) view.findViewById(R.id.titleSong);
        TextView artistSong = (TextView) view.findViewById(R.id.artistSong);
        TextView songDuration = (TextView) view.findViewById(R.id.songDuration);

        titleSong.setText(songTitle);
        artistSong.setText(songArtist);
        songDuration.setText(durationSong);

        return view;
    }
}
