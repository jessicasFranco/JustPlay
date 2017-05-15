package com.example.filip.justplay.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.test.suitebuilder.TestMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.filip.justplay.R;

import org.w3c.dom.Text;

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
        String songAlbum = bundle.getString("songAlbum");

        TextView titleSong = (TextView) view.findViewById(R.id.titleSong);
        TextView artistSong = (TextView) view.findViewById(R.id.artistSong);
        TextView songDuration = (TextView) view.findViewById(R.id.songDuration);
        TextView albumSong = (TextView) view.findViewById(R.id.albumSong);


        // Text Views of Playbar Fragment
        TextView song = (TextView) getActivity().findViewById(R.id.song);
        TextView duration = (TextView) getActivity().findViewById(R.id.songDuration);

        // Set the text to all textViews in SongScreen Fragment
        titleSong.setText(songTitle);
        artistSong.setText(songArtist);
        songDuration.setText(durationSong);
        albumSong.setText(songAlbum);

        //Set the values to the textViews in Playbar Fragment;
        song.setText(songTitle);
        duration.setText(durationSong);



        return view;
    }
}
