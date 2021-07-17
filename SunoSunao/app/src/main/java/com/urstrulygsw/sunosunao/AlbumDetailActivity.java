package com.urstrulygsw.sunosunao;

import android.graphics.Bitmap;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class AlbumDetailActivity extends AppCompatActivity {
    private ImageView image_album_detail;
    private TextView text_display_albumname;
    private RecyclerView recycler_view_album_detail;
    private String album_name;
    private AlbumDetailListAdapter albumDetailListAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<Song> songInAlbumArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_detail);

        initializeViews();

    }

    @Override
    protected void onResume() {
        super.onResume();
        ConnectLayoutDataAndAdapter();
    }

    private void ConnectLayoutDataAndAdapter(){
        //initionlization of array
        songInAlbumArrayList=GetSongs.getSongsOfAlbum(album_name);

        //initialize adapter
        albumDetailListAdapter=new AlbumDetailListAdapter(songInAlbumArrayList);

        //Connect Adapter RecyclerView and LayoutManager
        recycler_view_album_detail.setAdapter(albumDetailListAdapter);


        //be cautious
        layoutManager=new LinearLayoutManager(this,RecyclerView.VERTICAL,false);

        recycler_view_album_detail.setLayoutManager(layoutManager);

        //set Image to Album Detail Layout
        Bitmap bitmap=GetSongs.getImage(songInAlbumArrayList.get(0).getPathString());
        if(bitmap!=null){
            image_album_detail.setImageBitmap(bitmap);
        }
        else{
            image_album_detail.setImageResource(R.drawable.music);
        }

        //set album name to Album Detail Layout
        text_display_albumname.setText(album_name);

    }
    private void initializeViews(){

        image_album_detail=findViewById(R.id.image_album_detail);
        text_display_albumname=findViewById(R.id.text_display_albumname);
        recycler_view_album_detail=findViewById(R.id.recycler_view_album_detail);

        //get album name from intent extra
        album_name=getIntent().getStringExtra("album_name");

    }


}
