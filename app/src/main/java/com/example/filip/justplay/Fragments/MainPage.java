package com.example.filip.justplay.Fragments;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.example.filip.justplay.Adapters.FolderAdapter;
import com.example.filip.justplay.R;
import com.example.filip.justplay.Song;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import static com.example.filip.justplay.MainActivity.getContextOfApplication;

public class MainPage extends Fragment {

    private static  final int MY_PERMISSION_REQUEST = 1;

    //Artists
    ArrayList<String> artists = new ArrayList<String>();
    GridView artistsGrid;

    //Artists
    ArrayList<String> genre = new ArrayList<String>();
    GridView genreGrid;

    public MainPage() {
        // Required empty public constructor
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.main_page, container, false);

        //List View
        artistsGrid = (GridView) view.findViewById(R.id.artistsFolder);
        genreGrid = (GridView) view.findViewById(R.id.genre_folder);

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
            getArtists(artists);
            getAttributes(artists, artistsGrid);

            getGenre(genre);
            getAttributes(genre, genreGrid);
        }
        return view;
    }

    private void getAttributes(ArrayList<String> array, GridView grid){
        FolderAdapter folderAdt = new FolderAdapter(this.getContext(), array);
        grid.setAdapter(folderAdt);

    }

    public void getArtists(ArrayList<String> array){
        Context context = getContextOfApplication();
        ContentResolver contentResolver = context.getContentResolver();
        //Uri songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Uri artistsUri = android.provider.MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI;
        Cursor songCursor = contentResolver.query(artistsUri, null, null, null, null);

        if (songCursor != null && songCursor.moveToFirst()){
            int songArtist = songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);

            do{
                String currentArtist = songCursor.getString(songArtist);
                if(!Arrays.asList(array).contains(currentArtist)){
                    array.add(currentArtist);
                }
            }
            while (songCursor.moveToNext());
        }
    }

    public void getGenre(ArrayList<String> array){
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

                array.add(genre);
            }
            while(cursor.moveToNext());
        }
    }
}
