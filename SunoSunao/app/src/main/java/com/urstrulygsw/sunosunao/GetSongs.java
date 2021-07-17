package com.urstrulygsw.sunosunao;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;

public class GetSongs {


    //get all the audio files from device using MediaStore( android provider)
    static public ArrayList<Song> getSongs(Context context, int sort_preference_value){
        ArrayList<Song> songArrayList=new ArrayList<>();
        String sortOrder=null;
        Uri uri=MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        switch (sort_preference_value){
            case MainActivity.BY_NAME:
                sortOrder=MediaStore.MediaColumns.TITLE+" ASC";
                break;
            case MainActivity.BY_DATE:
                sortOrder=MediaStore.MediaColumns.DATE_ADDED+" ASC";
                break;
            case MainActivity.BY_SIZE:
                sortOrder=MediaStore.MediaColumns.SIZE+" DESC";
                break;
        }
        String[] projection={
                MediaStore.Audio.Media.DATA,//Path
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.DURATION,
//                MediaStore.Audio.Media.CONTENT_TYPE

        };

        //Cursor points to the current row of the virtual table
        //virtual table is the table which is returned by our query
        Cursor cursor=context.getContentResolver().query(uri,projection,null,null,sortOrder);
        if(cursor!=null){
            while(cursor.moveToNext()) {
                //public Song(String pathString, String titleString, String artistString, String albumString, String durationString) {
                Song song = new Song(cursor.getString(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        "abc"
                );
                Log.e("Song Data:", cursor.getString(1) + " ");
                if(song.getAlbumString().toLowerCase().equals("call")) continue;
                songArrayList.add(song);
            }
            cursor.close();
        }
        return songArrayList;
    }


    //get Image from path of the song in Bitmap form
    static public Bitmap getImage(String uri){
        try{
            Bitmap bm=null;
            MediaMetadataRetriever retriever=new MediaMetadataRetriever();
            retriever.setDataSource(uri);
            byte[] image=retriever.getEmbeddedPicture();
            if(image!=null) {
                InputStream is = new ByteArrayInputStream(image);
                bm = BitmapFactory.decodeStream(is);

            }
            return bm;
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }



    //using songs of song adapter (because when we search for a song through searchView, the album should be visible of that songArrayList from adapter
    static ArrayList<Song> getUniqueAlbum() {
        ArrayList<Song> albumArrayList = new ArrayList<>();
        HashSet<String> uniqueAlbumSet = new HashSet<>();
        for (int i = 0; i < MainActivity.songArrayList.size(); i++) {
            Song song = MainActivity.songArrayList.get(i);
            if (!uniqueAlbumSet.contains(song.getAlbumString())) {
                albumArrayList.add(song);
                uniqueAlbumSet.add(song.getAlbumString());
            }
        }
        return albumArrayList;
    }


        //To get all songs of this album
    //here too, using Song
    static ArrayList<Song> getSongsOfAlbum(String album_name){
        ArrayList<Song> songInAlbumArrayList=new ArrayList<>();
        for(int s=0;s<MainActivity.songArrayList.size();s++){
            Song song=MainActivity.songArrayList.get(s);
            if(album_name.equals(song.getAlbumString())){
                songInAlbumArrayList.add(song);
            }
        }
        return songInAlbumArrayList;
    }









}
