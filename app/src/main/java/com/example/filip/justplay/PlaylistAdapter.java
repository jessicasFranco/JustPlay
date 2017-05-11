package com.example.filip.justplay;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by jessi on 11/05/2017.
 */

public class PlaylistAdapter extends BaseAdapter {

    private ArrayList<Song> songs;
    private LayoutInflater songInf;

    public PlaylistAdapter(Context c, ArrayList<Song> theSongs){
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
                (R.layout.playlist, parent, false);
        //get title and artist views
        TextView category = (TextView)songLay.findViewById(R.id.category);

        //get song using position
        Song currSong = songs.get(position);
        //get title and artist strings
        category.setText(currSong.getArtist());

        //set position as tag
        songLay.setTag(position);
        return songLay;
    }
}
