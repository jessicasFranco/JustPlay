package com.example.filip.justplay;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import java.util.ArrayList;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;


public class SongAdapter extends BaseAdapter{

    private ArrayList<Song> songs;
    private LayoutInflater songInf;

    public SongAdapter(Context c, ArrayList<Song> theSongs){
        songs=theSongs;
        songInf=LayoutInflater.from(c);
    }


    @Override
    public int getCount() {
        return songs.size();
    }

    @Override
    public Object getItem(int arg0) {
        return null;
    }

    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //map to song layout
        LinearLayout songLay = (LinearLayout)songInf.inflate
                (R.layout.song_row, parent, false);
        //get title and artist views
        TextView songTitle = (TextView)songLay.findViewById(R.id.titleSong);
        TextView songArtist = (TextView)songLay.findViewById(R.id.artistSong);
        TextView songDuration = (TextView)songLay.findViewById(R.id.songDuration);

        //get song using position
        Song currSong = songs.get(position);
        //get title and artist strings
        songTitle.setText(currSong.getTitle());
        songArtist.setText(currSong.getArtist());
        //get duration and convert
        String duration = getTimeString(currSong.getDuration());
        songDuration.setText(duration);

        //set position as tag
        songLay.setTag(position);
        return songLay;
    }

    public static String getTimeString(long duration) {
        int minutes = (int) Math.floor(duration / 1000 / 60);
        int seconds = (int) ((duration / 1000) - (minutes * 60));
        return minutes + ":" + String.format("%02d", seconds);
    }

}
