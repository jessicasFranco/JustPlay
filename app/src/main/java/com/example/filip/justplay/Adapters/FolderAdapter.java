package com.example.filip.justplay.Adapters;

import android.content.Context;
import android.os.Debug;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.filip.justplay.R;
import com.example.filip.justplay.Song;
import com.example.filip.justplay.Utility.Utility;

import java.util.ArrayList;

/**
 * Created by filip on 15/05/2017.
 */

public class FolderAdapter extends BaseAdapter {

    private ArrayList<String> folder;
    private LayoutInflater folderInflater;

    public FolderAdapter(Context c, ArrayList<String> folderName){
        folder=folderName;
        folderInflater = LayoutInflater.from(c);
    }

    @Override
    public int getCount() {
        return folder.size();
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
        LinearLayout songLay = (LinearLayout)folderInflater.inflate (R.layout.folder_layout, parent, false);
        TextView label = (TextView)songLay.findViewById(R.id.name);

        label.setText(folder.get(position));
        songLay.setTag(position);
        return songLay;
    }

}