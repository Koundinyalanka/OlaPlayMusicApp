package com.example.venka.musicapp;

/**
 * Created by venka on 12/17/2017.
 */

public class Song {
    int id;
    int total;
    String song;
    String artists;
    String imgurl;
    String songurl;
    public int getId(){return id;}
    public String getSong() {
        return song;
    }
    public String getArtists() {
        return artists;
    }
    public String getSongUrl() {
        return songurl;
    }
    public String getImgUrl() {
        return imgurl;
    }
    public int getTotal() {return total;}
    public Song(int id,String song,String artists,String imgurl,String songurl,int total)
    {
        this.id=id;
        this.song=song;
        this.artists=artists;
        this.imgurl=imgurl;
        this.songurl=songurl;
        this.total=total;
    }
}
