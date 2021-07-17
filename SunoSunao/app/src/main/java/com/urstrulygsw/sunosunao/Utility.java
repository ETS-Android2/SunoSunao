package com.urstrulygsw.sunosunao;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;

import androidx.annotation.NonNull;

public class Utility {
    public static final int RC_SIGN_IN=100;
    public static FirebaseStorage storage = FirebaseStorage.getInstance();
    public static StorageReference storageRef = storage.getReference();
    public static final String SONG_NAME="SONG_NAME";
    public static final String ARTIST_NAME="ARTIST_NAME";
    public static final String ALBUM_NAME="ALBUM_NAME";
    public static final String SONG_FRAGMENT="SONG_FRAGMENT";
    public static final String ALBUM_FRAGMENT="ALBUM_FRAGMENT";
    public static final String CLOUD_FRAGMENT="CLOUD_FRAGMENT";
    public static final String FRAGMENT_NAME="FRAGMENT_NAME";



    //upload song on cloud
    public static void FirebaseStorageFunction(Context context, Uri uri,Song song){

        FirebaseAuth  mAuth=FirebaseAuth.getInstance();
        StorageReference songRef = storageRef.child(mAuth.getCurrentUser().getUid()).child(song.getTitleString());

        StorageMetadata metadata = new StorageMetadata.Builder()
                .setCustomMetadata(ARTIST_NAME,song.getArtistString())
                .setCustomMetadata(SONG_NAME,song.getTitleString())
                .setCustomMetadata(ALBUM_NAME,song.getAlbumString())
                .setContentType("audio/mpeg")
                .build();
        UploadTask uploadTask=songRef.putFile(Uri.fromFile(new File(song.getPathString())),metadata);
        uploadTask
                .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
                Toast.makeText(context,"Unable to upload",Toast.LENGTH_SHORT).show();
            }
        })
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(context,"Uploaded Successfully",Toast.LENGTH_SHORT).show();
                    }
                });

        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull  UploadTask.TaskSnapshot snapshot) {
                double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                String processString=String.format("%.2f",progress);
                Log.d("Upload","Upload is " + progress + "% done" );

                Toast.makeText(context,"Upload is " + processString + "% done",Toast.LENGTH_SHORT).show();
            }
        });
    }
    public static void DeleteFile(Context context, Song song, SongListAdapter songListAdapter){

        FirebaseAuth  mAuth=FirebaseAuth.getInstance();
        StorageReference songRef = storageRef.child(mAuth.getCurrentUser().getUid()).child(song.getTitleString());

        // Delete the file
        songRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // File deleted successfully
                Toast.makeText(context,"File Deleted successfully",Toast.LENGTH_SHORT).show();
                songListAdapter.songArrayList.remove(song);
                songListAdapter.notifyDataSetChanged();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Uh-oh, an error occurred!
                Toast.makeText(context,"Unable to deleted a file",Toast.LENGTH_SHORT).show();
            }
        });
    }



}
