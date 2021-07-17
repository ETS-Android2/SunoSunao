package com.urstrulygsw.sunosunao;

import java.io.Serializable;

public class Song implements Serializable {
    private String pathString;
    private  String titleString;
    private String artistString;
    private String albumString;
    private String durationString;
    private String contentTypeString;



    public Song(String pathString, String titleString, String artistString, String albumString, String durationString,String contentTypeString) {
        this.pathString = pathString;
        this.titleString = titleString;
        this.artistString = artistString;
        this.albumString = albumString;
        this.durationString = durationString;
        this.contentTypeString=contentTypeString;
    }

    public Song() {
    }
    public String getContentTypeString() {
        return contentTypeString;
    }

    public void setContentTypeString(String contentTypeString) {
        this.contentTypeString = contentTypeString;
    }

    public String getPathString() {
        return pathString;
    }

    public void setPathString(String pathString) {
        this.pathString = pathString;
    }

    public String getTitleString() {
        return titleString;
    }

    public void setTitleString(String titleString) {
        this.titleString = titleString;
    }

    public String getArtistString() {
        return artistString;
    }

    public void setArtistString(String artistString) {
        this.artistString = artistString;
    }

    public String getAlbumString() {
        return albumString;
    }

    public void setAlbumString(String albumString) {
        this.albumString = albumString;
    }

    public String getDurationString() {
        return durationString;
    }

    public void setDurationString(String durationString) {
        this.durationString = durationString;
    }

    public String toString()
    {
        return pathString+" "+titleString + " " + artistString + " " + albumString + " " + durationString + " " + contentTypeString;
    }

}
