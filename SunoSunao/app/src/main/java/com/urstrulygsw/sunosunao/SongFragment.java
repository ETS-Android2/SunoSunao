package com.urstrulygsw.sunosunao;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import java.util.ArrayList;


public class SongFragment extends Fragment implements SearchView.OnQueryTextListener{

    RecyclerView recycler_view_song;
    static SongListAdapter songListAdapter;
    RecyclerView.LayoutManager layoutManager;
    SharedPreferences sharedPreferences;
    Context context;
    int sort_preference_value=0;

    public SongFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


//        getActivity().getActionBar().setIcon(R.mipmap.ic_launcher);

        context=getContext();
        setHasOptionsMenu(true);
        sharedPreferences=PreferenceManager.getDefaultSharedPreferences(context);
        sort_preference_value=sharedPreferences.getInt(MainActivity.SORT_PREFERENCE,0);

        // Inflate the layout for this fragment

        View view=inflater.inflate(R.layout.fragment_song, container, false);
        recycler_view_song=view.findViewById(R.id.recycler_view_song);
        recycler_view_song.setHasFixedSize(true);
        // 17.7 change getsongs to mainactivity.song
        songListAdapter=new SongListAdapter(MainActivity.songArrayList,Utility.SONG_FRAGMENT);
        recycler_view_song.setAdapter(songListAdapter);
        layoutManager=new LinearLayoutManager(context,RecyclerView.VERTICAL,false);
        recycler_view_song.setLayoutManager(layoutManager);
        return view;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.tmp_menu,menu);
        MenuItem menuItem=menu.findItem(R.id.menu_item_search_tmp);
        SearchView searchView= (SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(this);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        ArrayList<Song> searchedSongArrayList=new ArrayList<>();
        String searchedSong=newText.toLowerCase();


        for(Song song: MainActivity.songArrayList){
            if((song.getTitleString().toLowerCase().contains(searchedSong)) || (song.getAlbumString().toLowerCase().contains(searchedSong)) || (song.getArtistString().toLowerCase().contains(searchedSong))){
                searchedSongArrayList.add(song);
            }
        }
        songListAdapter.updateSongListOnSearch(searchedSongArrayList);
        return true;
    }
}
