package com.urstrulygsw.sunosunao;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;
import com.google.android.material.tabs.TabLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity  {


    TabLayout tab_layout;
    ViewPager view_pager;
    static ArrayList<Song> songArrayList;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    public static final int BY_NAME=1,BY_DATE=2,BY_SIZE=3;
    public static final String SORT_PREFERENCE="SORT_PREFERENCE";
    int sort_preference_value;



    //Constants
    public static final int REQUEST_CODE=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //set icon for activity in action bar
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);
        getSupportActionBar().setTitle("  SunoSunao");

        //permission call to access storage
        permissionToAccessStorage();

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        permissionToAccessStorage();
    }

    private void initializeViews(){
        tab_layout=findViewById(R.id.tab_layout);
        view_pager=findViewById(R.id.view_pager);

        //Creating instance of adapter
        ViewPagerAdapter viewPagerAdapter=new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragement(new SongFragment(),"Songs");
        viewPagerAdapter.addFragement(new AlbumFragment(),"Albums");
        viewPagerAdapter.addFragement(new FirebaseFragment(),"Cloud");
        view_pager.setAdapter(viewPagerAdapter);

        //Without this line we won't be able to see tab_layout on the screen
        tab_layout.setupWithViewPager(view_pager);

        sharedPreferences=PreferenceManager.getDefaultSharedPreferences(this);
        sort_preference_value=sharedPreferences.getInt(SORT_PREFERENCE,0);
        editor=sharedPreferences.edit();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode==REQUEST_CODE){
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                songArrayList=GetSongs.getSongs(this, sort_preference_value);
                initializeViews();
            }
            else{
                //again ask for permission
                rationlePermission();

            }
        }
        else{
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }




    }

    //check for permission
    void permissionToAccessStorage() {
        //ContextCompat's this method checks permission and returns value
        //if it is not returning 0 (Granted)
        //then ask for access permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            //ask permission
            askForPermission();

        }
        else{
            initializeViews();
            songArrayList=GetSongs.getSongs(this,sort_preference_value);
        }

    }

    //show permission pop up
    void askForPermission(){
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUEST_CODE);

    }

    //show rationale message on alert box
    //if user agrees then ask for permission pop up
    //else get out of the application
    void rationlePermission(){
            new AlertDialog.Builder(this)
                    .setTitle("Permission Required")
                    .setMessage("Permission required to load and play songs of your device")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //askForPermission();
                            Intent intent=new Intent();
                            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri=Uri.fromParts("package",MainActivity.this.getPackageName(),null);
                            intent.setData(uri);
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            finish();
                            System.exit(0);
                        }
                    }).show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.menu_item_sort_by_name:
                editor.putInt(SORT_PREFERENCE,BY_NAME);
                editor.commit();
                //as we want sorted songs in activity/fragment therefore we must restart this activity
                this.onRestart();
                break;

            case R.id.menu_item_sort_by_date:
                editor.putInt(SORT_PREFERENCE,BY_DATE);
                editor.commit();
                //as we want sorted songs in activity/fragment therefore we must restart this activity
                this.onRestart();
                break;

            case R.id.menu_item_sort_by_size:
                editor.putInt(SORT_PREFERENCE,BY_SIZE);
                editor.commit();
                //as we want sorted songs in activity/fragment therefore we must restart this activity
                this.onRestart();
                break;
        }

        return super.onOptionsItemSelected(item);
    }


}
