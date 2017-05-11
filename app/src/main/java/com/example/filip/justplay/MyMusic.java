package com.example.filip.justplay;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import static android.R.id.message;
import static com.example.filip.justplay.MainActivity.getContextOfApplication;

public class MyMusic extends Fragment {

    private static  final int MY_PERMISSION_REQUEST = 1;

    ArrayList<Song> songList;
    ListView songView;

    public MyMusic() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.my_music, container, false);

        //List View
        songView = (ListView) view.findViewById(R.id.my_music);

        if (ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    android.Manifest.permission.READ_EXTERNAL_STORAGE)){
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSION_REQUEST);
            }else {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSION_REQUEST);
            }
        } else {
            accessfiles();
    }

        return view;

    }
    public void accessfiles(){

        songList = new ArrayList<Song>();

        getMusic();

        SongAdapter songAdt = new SongAdapter(this.getContext(), songList);
        songView.setAdapter(songAdt);

        //When you click on the item pass to a new fragment with all the info
        songView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //Get the song by the position of it is display
                //String song = String.valueOf(parent.getItemAtPosition(position));

                String songTitle = ((TextView) view.findViewById(R.id.titleSong)).getText().toString();
                String songArtist = ((TextView) view.findViewById(R.id.artistSong)).getText().toString();
                String durationSong = ((TextView) view.findViewById(R.id.songDuration)).getText().toString();

                Bundle bundle = new Bundle();
                bundle.putString("songTitle", songTitle);
                bundle.putString("songArtist", songArtist);
                bundle.putString("durationSong", durationSong);

                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                SongScreen songScreen = new SongScreen();
                songScreen.setArguments(bundle);
                fragmentTransaction.replace(R.id.layoutCM, songScreen,songScreen.getTag());
                fragmentTransaction.commit();
            }
        });
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
                songList.add(new Song(currentId, currentTitle, currentArtist, currentDuration, currentGenre));
            }
            while (songcursor.moveToNext());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode){
            case MY_PERMISSION_REQUEST: {
                if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                   if(ContextCompat.checkSelfPermission(getContext(),
                           Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){

                       accessfiles();
                   }
                }
                return;
            }
        }
    }
}
