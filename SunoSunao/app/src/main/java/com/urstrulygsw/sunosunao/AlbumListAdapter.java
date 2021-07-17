package com.urstrulygsw.sunosunao;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class AlbumListAdapter extends RecyclerView.Adapter<AlbumListAdapter.AlbumListHolder> {

    Context context;
    ArrayList<Song> albumArrayList;
    public AlbumListAdapter(ArrayList<Song> songs) {
        albumArrayList=songs;
    }


    public class AlbumListHolder extends RecyclerView.ViewHolder {
        ImageView image_album_cardview;
        TextView text_album_cardview;

        public AlbumListHolder(@NonNull View itemView) {
            super(itemView);
            image_album_cardview=itemView.findViewById(R.id.image_album_cardview);
            text_album_cardview=itemView.findViewById(R.id.text_album_cardview);
        }
    }

    @NonNull
    @Override
    public AlbumListHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context=viewGroup.getContext();
        View view=LayoutInflater.from(context).inflate(R.layout.album_card_view,viewGroup,false);
        return new AlbumListHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumListHolder albumListHolder, final int position) {
        final Song song=albumArrayList.get(position);
        albumListHolder.text_album_cardview.setText(song.getAlbumString());
        albumListHolder.text_album_cardview.setSelected(true);
        Bitmap bitmap=GetSongs.getImage(song.getPathString());
        if(bitmap!=null) {
            albumListHolder.image_album_cardview.setImageBitmap(bitmap);
        }
        else{
            albumListHolder.image_album_cardview.setImageResource(R.drawable.music);
        }
        albumListHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context,AlbumDetailActivity.class);
                intent.putExtra("album_name",song.getAlbumString());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return albumArrayList.size();
    }


}
