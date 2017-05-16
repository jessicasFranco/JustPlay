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
    SongScreen songScreen = new SongScreen();
    Bundle bundle = new Bundle();
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

                String songTitle = ((TextView) view.findViewById(R.id.artist_label)).getText().toString();
                String songArtist = ((TextView) view.findViewById(R.id.artistSong)).getText().toString();
                String durationSong = ((TextView) view.findViewById(R.id.songDuration)).getText().toString();
                Song currentSong = songList.get(position);
                String songAlbum = currentSong.getAlbum();
                passingToScreen(songTitle, songArtist, durationSong, songAlbum);
                //Path have the path of the song
                String path = currentSong.getPathSong();
                ((MainActivity) getActivity()).playerStart(path, currentSong);
            }
        });
    }

    private void passingToScreen(String songTitle, String songArtist, String durationSong, String songAlbum) {
        Bundle bundle = new Bundle();
        bundle.putString("songTitle", songTitle);
        bundle.putString("songArtist", songArtist);
        bundle.putString("durationSong", durationSong);
        bundle.putString("songAlbum", songAlbum);

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        SongScreen songScreen = new SongScreen();
        songScreen.setArguments(bundle);
        fragmentTransaction.replace(R.id.layoutCM,
                songScreen,
                songScreen.getTag()).commit();

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
            int position1 = songList.indexOf(currentSong);
            if (position1 > songList.size()) {
                String nextSong = songList.get(0).getPathSong();
                Song next = songList.get(0);
                String title = next.getTitle();
                String artist = next.getArtist();
                String album = next.getAlbum();
                Long duration = next.getDuration();
                passToScreen(title, artist, album, duration);
                ((MainActivity) mActivity).playerStart(nextSong, next);


            } else {
                String nextSong = songList.get(position1 + 1).getPathSong();
                Song next = songList.get(position1 + 1);
                String title = next.getTitle();
                String artist = next.getArtist();
                String album = next.getAlbum();
                Long duration = next.getDuration();
                passToScreen(title, artist, album, duration);
                ((MainActivity) mActivity).playerStart(nextSong, next);
            }
        }
    }

    private void passToScreen(String title, String artist, String album, Long duration) {


        bundle.putString("songTitle", title);
        bundle.putString("songArtist", artist);
        bundle.putString("songAlbum", album);
        bundle.putString("durationSong", duration.toString());
        // UpdateFragment uf = new UpdateFragment(bundle);
        songScreen.setArguments(bundle);

        mActivity.getSupportFragmentManager()
                .beginTransaction()
                .detach(songScreen)
                .attach(songScreen)
                .commit();

    }

    public void getPrevious(Song currentSong) {

        if (songList.contains(currentSong)) {

            int position1 = songList.indexOf(currentSong);

            if (position1 < 0) {

                String previousSong = songList.get(songList.size()).getPathSong();
                Song previous = songList.get(songList.size());
                String title = previous.getTitle();
                String artist = previous.getArtist();
                String album = previous.getAlbum();
                Long duration = previous.getDuration();
                passToScreen(title, artist, album, duration);
                ((MainActivity) mActivity).playerStart(previousSong, previous);

            } else {
                String previousSong = songList.get(position1 - 1).getPathSong();
                Song previous = songList.get(position1 - 1);
                String title = previous.getTitle();
                String artist = previous.getArtist();
                String album = previous.getAlbum();
                Long duration = previous.getDuration();
                passToScreen(title, artist, album, duration);
                ((MainActivity) mActivity).playerStart(previousSong, previous);
            }
        }
    }
}
