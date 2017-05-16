package com.example.filip.justplay.Fragments;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.filip.justplay.Adapters.SongAdapter;
import com.example.filip.justplay.MainActivity;
import com.example.filip.justplay.R;
import com.example.filip.justplay.Song;

import java.util.ArrayList;

import static com.example.filip.justplay.MainActivity.getContextOfApplication;

public class MyMusic extends Fragment {

    private static final int MY_PERMISSION_REQUEST = 1;

    static ArrayList<Song> songList = new ArrayList<Song>();
    ListView songView;
    Song currentSong;

    public MyMusic() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.my_music, container, false);

        //List View
        songView = (ListView) view.findViewById(R.id.my_music);
        songList = new ArrayList<Song>();

        if (ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSION_REQUEST);
            } else {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSION_REQUEST);
            }
        } else {
            accessFiles();
        }
        return view;
    }

    public void accessFiles() {

        Cursor song = getMusic();
        SongAdapter songAdt = new SongAdapter(this.getContext(), songList);
        songView.setAdapter(songAdt);

        //When you click on the item pass to a new fragment with all the info
        songView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Song currentSong = songList.get(position);
                String path = currentSong.getPathSong();
                ((MainActivity) getActivity()).playerStart(path, currentSong);
            }
        });
    }


    public Cursor getMusic() {
        Context context = getContextOfApplication();
        ContentResolver contentResolver = context.getContentResolver();
        Uri songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor songcursor = contentResolver.query(songUri, null, null, null, null);


        if (songcursor != null && songcursor.moveToFirst()) {
            int songId = songcursor.getColumnIndex(MediaStore.Audio.Media._ID);
            int songTitle = songcursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int songArtist = songcursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int songAlbum = songcursor.getColumnIndex(MediaStore.Audio.Media.ALBUM);
            int songDuration = songcursor.getColumnIndex(MediaStore.Audio.Media.DURATION);
            int pathSong = songcursor.getColumnIndex(MediaStore.Audio.Media.DATA);

            do {
                Long currentId = songcursor.getLong(songId);
                String currentTitle = songcursor.getString(songTitle);
                String currentArtist = songcursor.getString(songArtist);
                String currentAlbum = songcursor.getString(songAlbum);
                Long currentDuration = songcursor.getLong(songDuration);
                String currentPath = songcursor.getString(pathSong);

                Song song = new Song(currentId, currentTitle, currentArtist, currentDuration, currentAlbum, currentPath);
                songList.add(song);

            }
            while (songcursor.moveToNext());
        }
        return songcursor;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_REQUEST: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(getContext(),
                            Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

                        accessFiles();
                    }
                }
                return;
            }
        }
    }

    public static AppCompatActivity mActivity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (AppCompatActivity) getActivity();
    }

    public void getNext(Song currentSong) {

        if (songList.contains(currentSong)) {
            String nextSong;
            Song next;
            int position1 = songList.indexOf(currentSong);
            if (position1 > songList.size()) {
                nextSong = songList.get(0).getPathSong();
                next = songList.get(0);
            } else {
                nextSong = songList.get(position1 + 1).getPathSong();
                next = songList.get(position1 + 1);
            }
            ((MainActivity) mActivity).playerStart(nextSong, next);
        }
    }



    public void getPrevious(Song currentSong) {

        if (songList.contains(currentSong)) {
            String previousSong;
            Song previous;
            int position1 = songList.indexOf(currentSong);
            if (position1 < 0) {
                previousSong = songList.get(songList.size()).getPathSong();
                previous = songList.get(songList.size());
            } else {
                previousSong = songList.get(position1 - 1).getPathSong();
                previous = songList.get(position1 - 1);
            }
            ((MainActivity) mActivity).playerStart(previousSong, previous);
        }
    }
}
