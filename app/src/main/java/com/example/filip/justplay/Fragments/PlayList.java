package com.example.filip.justplay.Fragments;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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

public class PlayList extends Fragment {

    private final int value = 1;

    private static final String[] genreResting = {"Jazz", "Classic"};
    private static final String[] genreWalking = {"Pop", "Hip"};
    private static final String[] genreJogging = {"Blues", "Rap"};

    private static final int MY_PERMISSION_REQUEST = 1;
    private String currentFolder;

    static ArrayList<Song> songList = new ArrayList<Song>();
    ListView songView;

    public PlayList(){
        // Required empty public constructor
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.my_music, container, false);

        Bundle bundle = getArguments();
        if (bundle != null) {
            currentFolder = bundle.getString("folder");
        }

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

        getMusicByGenre();
        SongAdapter songAdt = new SongAdapter(this.getContext(), songList);
        songView.setAdapter(songAdt);

        /*
        if(songList.size() != 0){
            Song song = songList.get(0);
            String songPath = song.getPathSong();
            ((MainActivity) getActivity()).playerStart(songPath, song, value);
        }
        */

        //When you click on the item pass to a new fragment with all the info
        songView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String songTitle = ((TextView) view.findViewById(R.id.artist_label)).getText().toString();
                String songArtist = ((TextView) view.findViewById(R.id.artistSong)).getText().toString();
                String durationSong = ((TextView) view.findViewById(R.id.songDuration)).getText().toString();
                Song currentSong = songList.get(position);
                String songAlbum = currentSong.getAlbum();

                //Path have the path of the song
                String path = currentSong.getPathSong();
                ((MainActivity) getActivity()).playerStart(path, currentSong, value);
            }
        });
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

    public void getMusicByGenre(){

        String[] genreName = new String[3];
        switch (currentFolder){
            case "Resting":
                genreName = genreResting.clone();
                break;
            case "Jogging":
                genreName = genreJogging.clone();
                break;
            case "Walking":
                genreName = genreWalking.clone();
                break;
        }

        String[] variable = new String[]{
                MediaStore.Audio.Genres.NAME,
                MediaStore.Audio.Genres._ID
        };
        Context context = getContextOfApplication();
        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = contentResolver.query(MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI, variable, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            do{
                int index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Genres.NAME);
                String genre = cursor.getString(index);
                index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Genres._ID);
                long genreId = Long.parseLong(cursor.getString(index));

                Uri uri = MediaStore.Audio.Genres.Members.getContentUri("external", genreId);
                Cursor musicCursor = contentResolver.query(uri, null, null, null, null);

                if (musicCursor != null && musicCursor.moveToFirst()) {

                    int songId = musicCursor.getColumnIndex(MediaStore.Audio.Media._ID);
                    int songTitle = musicCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
                    int songArtist = musicCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
                    int songAlbum = musicCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM);
                    int songDuration = musicCursor.getColumnIndex(MediaStore.Audio.Media.DURATION);
                    int pathSong = musicCursor.getColumnIndex(MediaStore.Audio.Media.DATA);

                    do {
                        Long currentId = musicCursor.getLong(songId);
                        String currentTitle = musicCursor.getString(songTitle);
                        String currentArtist = musicCursor.getString(songArtist);
                        String currentAlbum = musicCursor.getString(songAlbum);
                        Long currentDuration = musicCursor.getLong(songDuration);
                        String currentPath = musicCursor.getString(pathSong);

                        Song song = new Song(currentId, currentTitle, currentArtist, currentDuration, currentAlbum, currentPath);

                        if(genre.contains(genreName[0])
                                || genre.contains(genreName[1])){
                            Log.i("Genre", genre);
                            songList.add(song);
                        }


                    }
                    while (musicCursor.moveToNext());
                }
            }
            while(cursor.moveToNext());
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
            Log.i("TESTE", nextSong+ " " + next);
            ((MainActivity) mActivity).playerStart(nextSong, next, value);
        }
    }

    public void getPrevious(Song currentSong) {

        if (songList.contains(currentSong)) {
            String path;
            Song previous;
            int position = songList.indexOf(currentSong);
            if(position > 0){
                path = songList.get(position - 1).getPathSong();
                previous = songList.get(position - 1);
            }
            else{
                path = currentSong.getPathSong();
                previous = currentSong;
            }
            ((MainActivity) mActivity).playerStart(path, previous, value);
        }
    }
}