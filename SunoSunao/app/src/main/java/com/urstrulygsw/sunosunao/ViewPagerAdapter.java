package com.urstrulygsw.sunosunao;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class ViewPagerAdapter extends FragmentPagerAdapter {


    //These arraylists represent our all fragements and their corresponding visible title
    private ArrayList<Fragment> fragmentArrayList;
    private ArrayList<String> titleArrayList;

    //Create a function for adding fragments into arraylist
    void addFragement(Fragment fragment,String title){
        fragmentArrayList.add(fragment);
        titleArrayList.add(title);
    }

    //constructor which has manager of fragment
    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);

        //assigning memory to array lists
        this.fragmentArrayList=new ArrayList<>();
        this.titleArrayList=new ArrayList<>();
    }


    //2 overrided methods of FragmentPagerAdapter
    @Override
    public Fragment getItem(int position) {
        return fragmentArrayList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentArrayList.size();
    }

    //add one more method of this adapter class
    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return titleArrayList.get(position);
    }


}
