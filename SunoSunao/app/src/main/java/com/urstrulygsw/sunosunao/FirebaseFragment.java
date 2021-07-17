package com.urstrulygsw.sunosunao;

import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.ArrayList;

public class FirebaseFragment extends Fragment {


    View view;
    TextView text_user_name;
    Button button_sign_inout;
    private FirebaseAuth mAuth;
    GoogleSignInClient googleSignInClient;
    ArrayList<Song> songList;
    RecyclerView recyclerView;
    Context context;
    SongListAdapter songListAdapter;
    RecyclerView.LayoutManager layoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        context=getContext();
        setHasOptionsMenu(true);

        view=inflater.inflate(R.layout.fragment_firebase, container, false);
        text_user_name=view.findViewById(R.id.text_user_name);
        button_sign_inout=view.findViewById(R.id.button_sign_in_out);
        recyclerView=view.findViewById(R.id.recycler_view_song_firebase);
        recyclerView.setHasFixedSize(true);
        songList=new ArrayList<>();
        songListAdapter=new SongListAdapter(songList, Utility.CLOUD_FRAGMENT);
        recyclerView.setAdapter(songListAdapter);
        layoutManager=new LinearLayoutManager(context,RecyclerView.VERTICAL,false);
        recyclerView.setLayoutManager(layoutManager);

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("344051836223-1ie8oapjmbbqnc373u784p1024fh0ak6.apps.googleusercontent.com")
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(getActivity(), gso);


        mAuth = FirebaseAuth.getInstance();
        button_sign_inout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mAuth.getCurrentUser()==null){
                    signIn();
                }
                else{
                    signOut();
                }
            }
        });

        return view;
    }


    @Override
    public void onResume() {
        super.onResume();

        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            userIsNotNull();
        }
        else{
            userIsNull();
        }
    }




    private void userIsNotNull() {
        updateUI(mAuth.getCurrentUser());
    }
    private void userIsNull() {
        text_user_name.setText("Please log In");
        button_sign_inout.setText("Log In");
        songListAdapter.updateSongListOnSearch(new ArrayList<>());
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(getContext(),"Success",Toast.LENGTH_SHORT).show();
                            updateUI(mAuth.getCurrentUser());
                        }
                        else{
                            Toast.makeText(getContext(),"Failed",Toast.LENGTH_SHORT).show();
                            userIsNull();
                        }
                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        text_user_name.setText(mAuth.getCurrentUser().getDisplayName());
        button_sign_inout.setText("Log Out");
        FirebaseStorageAllSongs(getActivity(),user);
        //Toast.makeText(context,songList.size()+" ",Toast.LENGTH_SHORT).show();


    }
    private void signIn() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, Utility.RC_SIGN_IN);
    }
    private void signOut() {
        mAuth.signOut();
        googleSignInClient.signOut();
        userIsNull();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==Utility.RC_SIGN_IN){
            Task<GoogleSignInAccount> task=GoogleSignIn.getSignedInAccountFromIntent(data);
            GoogleSignInAccount googleSignInAccount= null;
            try {
                googleSignInAccount = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(googleSignInAccount.getIdToken());
            } catch (ApiException e) {
                e.printStackTrace();
                Toast.makeText(getActivity(),"Something went wrong",Toast.LENGTH_SHORT).show();
            }

        }
    }
    public void FirebaseStorageAllSongs(Context context, FirebaseUser currentUser){
        StorageReference songListRef=Utility.storageRef.child(currentUser.getUid());
        songListRef.listAll()
                .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(ListResult listResult) {
                        songListAdapter.updateSongListOnSearch(new ArrayList<>());
                        for(StorageReference item:listResult.getItems()){
                            Song song=new Song();
                            Log.e("path",item.getPath());
                            Log.e("bucket",item.getBucket());
                            Log.e("url",item.getDownloadUrl().toString());
                            item.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    song.setPathString(uri.toString());
                                }
                            });

                            item.getMetadata().
                                    addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
                                        @Override
                                        public void onSuccess(StorageMetadata storageMetadata) {
                                            String songName=storageMetadata.getCustomMetadata(Utility.SONG_NAME);
                                            String albumName=storageMetadata.getCustomMetadata(Utility.ALBUM_NAME);
                                            String artistName=storageMetadata.getCustomMetadata(Utility.ARTIST_NAME);
                                            song.setTitleString(songName);
                                            song.setAlbumString(albumName);
                                            song.setArtistString(artistName);
                                            songList.add(song);
                                            if (mAuth.getCurrentUser() != null) {
                                                songListAdapter.updateSongListOnSearch(songList);
                                            }

                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {

                                        }
                                    });
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull  Exception e) {
                        e.printStackTrace();
                        Toast.makeText(context,"Unable to get items",Toast.LENGTH_SHORT).show();
                    }
                });
    }
}