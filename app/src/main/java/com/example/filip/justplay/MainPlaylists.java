package com.example.filip.justplay;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import java.util.ArrayList;

import static com.example.filip.justplay.MainActivity.getContextOfApplication;

public class MainPlaylists extends Fragment {

    GridView artistView;
    GridView genreView;

    ArrayList<Song> caractList;

    public MainPlaylists() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.main_playlists, container, false);

        //Two GridView

        artistView = (GridView) view.findViewById(R.id.gridViewArtist);
        genreView = (GridView) view.findViewById(R.id.gridViewGenre);

        getMusic();

        PlaylistAdapter songAdt = new PlaylistAdapter(this.getContext(), caractList);
        artistView.setAdapter(songAdt);

        return view;
    }

    public void getMusic(){
        Context context = getContextOfApplication();
        ContentResolver contentResolver = context.getContentResolver();
        Uri songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor songcursor = contentResolver.query(songUri, null, null, null, null);

        if (songcursor != null && songcursor.moveToFirst()){
            int songId = songcursor.getColumnIndex(MediaStore.Audio.Media._ID);
            int songTitle = songcursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int songArtist = songcursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int songGenre = songcursor.getColumnIndex(MediaStore.Audio.Genres._ID);
            int songDuration = songcursor.getColumnIndex(MediaStore.Audio.Media.DURATION);

            do{
                Long currentId = songcursor.getLong(songId);
                String currentTitle = songcursor.getString(songTitle);
                String currentArtist = songcursor.getString(songArtist);
                String currentGenre = songcursor.getString(songGenre);
                Long currentDuration = songcursor.getLong(songDuration);
                caractList.add(new Song(currentId, currentTitle, currentArtist, currentDuration, currentGenre ));
            }
            while (songcursor.moveToNext());
        }
    }

}
