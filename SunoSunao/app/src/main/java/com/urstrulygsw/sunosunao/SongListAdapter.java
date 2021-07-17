package com.urstrulygsw.sunosunao;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;

public class SongListAdapter  extends RecyclerView.Adapter<SongListAdapter.SongListHolder>{
    ArrayList<Song> songArrayList;
    String fragment;
    SongListAdapter songListAdapter;
    public SongListAdapter(ArrayList<Song> songArrayList, String fragment) {
        this.songArrayList = songArrayList;
        this.fragment=fragment;
        songListAdapter=this;

    }



    public class SongListHolder extends RecyclerView.ViewHolder{
         ImageView image_song,image_delete;
         TextView text_song_name;
         TextView text_album;


        public SongListHolder(@NonNull View itemView) {
            super(itemView);
            image_song= itemView.findViewById(R.id.image_song);
            text_song_name=itemView.findViewById(R.id.text_song_name);
            text_album=itemView.findViewById(R.id.text_album);
            image_delete=itemView.findViewById(R.id.image_delete);
            if(fragment==Utility.CLOUD_FRAGMENT){
                image_delete.getLayoutParams().width=100;

            }
            else{
                image_delete.getLayoutParams().width=0;
            }

        }
    }

    @NonNull
    @Override
    public SongListHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.song_card_view,viewGroup,false);
        return new SongListHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final SongListHolder songListHolder, final int position) {
        Song song=songArrayList.get(position);

        //binding data --> set song title
        songListHolder.text_song_name.setText(song.getTitleString());
        songListHolder.text_album.setText(song.getAlbumString());

        //binding data -->set image
        //as out ImageView can set bitmap type of image (and not bytes)
        if(song.getPathString()!=null){
            Bitmap bm=GetSongs.getImage(song.getPathString());
            if(bm!=null){
                songListHolder.image_song.setImageBitmap(bm);
            }
        }

        else{
            songListHolder.image_song.setImageResource(R.drawable.music);
        }
        songListHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context=songListHolder.itemView.getContext();
                Intent intent=new Intent(context,PlayerActivity.class);
                Bundle bundle=new Bundle();
                bundle.putSerializable("array",(Serializable) songArrayList);
                intent.putExtra("position",position);
                intent.putExtra("bundle",bundle);
                PlayerActivity.releaseMediaPlayer();
                context.startActivity(intent);

            }
        });
        songListHolder.image_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utility.DeleteFile(v.getContext(),song,songListAdapter);
                notifyDataSetChanged();
            }
        });

    }

    @Override
    public int getItemCount() {
        return songArrayList.size();
    }


    public void updateSongListOnSearch(ArrayList<Song> searchedSongArrayList) {
        songArrayList=new ArrayList<>();
        songArrayList.addAll(searchedSongArrayList);
        notifyDataSetChanged();
    }

}
