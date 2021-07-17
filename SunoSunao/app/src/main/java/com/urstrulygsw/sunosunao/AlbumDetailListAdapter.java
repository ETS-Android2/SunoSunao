package com.urstrulygsw.sunosunao;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
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

public class AlbumDetailListAdapter extends RecyclerView.Adapter<AlbumDetailListAdapter.AlbumDetailListHolder> {

    ArrayList<Song> songInAlbumArrayList;
    Context context;

    public AlbumDetailListAdapter(ArrayList<Song> songInAlbumArrayList) {
        this.songInAlbumArrayList=songInAlbumArrayList;
    }

    public class AlbumDetailListHolder extends RecyclerView.ViewHolder{
        ImageView image_song,image_delete;
        TextView text_song_name;

        public AlbumDetailListHolder(@NonNull View itemView) {
            super(itemView);
            image_song= itemView.findViewById(R.id.image_song);
            text_song_name=itemView.findViewById(R.id.text_song_name);
            image_delete=itemView.findViewById(R.id.image_delete);
            image_delete.getLayoutParams().width=0;

        }
    }

    @NonNull
    @Override
    public AlbumDetailListHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context=viewGroup.getContext();
        View view=LayoutInflater.from(context).inflate(R.layout.song_card_view,viewGroup,false);
        return new AlbumDetailListHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final AlbumDetailListHolder albumDetailListHolder, final int position) {
        Song song=songInAlbumArrayList.get(position);

        //binding data --> set song title
        albumDetailListHolder.text_song_name.setText(song.getTitleString());
        albumDetailListHolder.text_song_name.setSelected(true);

        //binding data -->set image
        //as out ImageView can set bitmap type of image (and not bytes)
        Bitmap bm=GetSongs.getImage(song.getPathString());
        if(bm!=null){
            albumDetailListHolder.image_song.setImageBitmap(bm);
        }
        else{
            albumDetailListHolder.image_song.setImageResource(R.drawable.music);
        }
        albumDetailListHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context=albumDetailListHolder.itemView.getContext();

                Intent intent=new Intent(context,PlayerActivity.class);
                intent.putExtra("position",position);
                Bundle bundle=new Bundle();
                bundle.putSerializable("array",(Serializable) songInAlbumArrayList);
                intent.putExtra("bundle",bundle);
                PlayerActivity.releaseMediaPlayer();
                context.startActivity(intent);

            }
        });

    }

    @Override
    public int getItemCount() {
        return songInAlbumArrayList.size();
    }


}
