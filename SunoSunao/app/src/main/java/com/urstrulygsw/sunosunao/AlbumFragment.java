package com.urstrulygsw.sunosunao;


import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class AlbumFragment extends Fragment {


    RecyclerView recycler_view_album;
    AlbumListAdapter albumListAdapter;
    ArrayList<Song> songArrayList;

    Context context;

    public AlbumFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_album, container, false);
        context=getContext();
        recycler_view_album=view.findViewById(R.id.recycler_view_album);
        recycler_view_album.setHasFixedSize(true);
        songArrayList=GetSongs.getUniqueAlbum();
        albumListAdapter=new AlbumListAdapter(songArrayList);
        recycler_view_album.setAdapter(albumListAdapter);
        recycler_view_album.setLayoutManager(new GridLayoutManager(context,2));
        return view;

    }


}
