package com.example.filip.justplay;

/**
 * Created by jessi on 09/05/2017.
 */

public class Song {

    private long id;
    private String title;
    private String artist;
    private long duration;
    private String album;
    private String pathSong;

    public Song ( long idSong, String songTitle, String songArtist, long songDuration, String songAlbum, String path)
    {
        id = idSong;
        title = songTitle;
        artist = songArtist;
        duration = songDuration;
        album = songAlbum;
        pathSong = path;
    }

    public long getID(){return id;}
    public String getTitle(){return title;}
    public String getArtist(){return artist;}
    public long getDuration() {return duration;}
    public String getAlbum() {return album;}
    public String getPathSong(){return pathSong;}
}
