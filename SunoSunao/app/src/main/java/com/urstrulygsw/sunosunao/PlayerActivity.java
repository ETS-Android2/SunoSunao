package com.urstrulygsw.sunosunao;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.GradientDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.preference.PreferenceManager;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.palette.graphics.Palette;

import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.StorageReference;

import java.net.URI;
import java.util.ArrayList;
import java.util.Random;

public class PlayerActivity extends AppCompatActivity{



    private ImageView image_display_songimage,image_shuffle,image_previous,image_replay,
            image_play_pause,image_forward,image_next,image_repeat,image_menu,image_back;
    private TextView text_start_time,text_end_time,text_display_songname,text_display_artistname;
    ConstraintLayout constrain_layout_main_player;
    private SeekBar seek_bar;
    int position=-1;
    private Uri uri;
    private static MediaPlayer mediaPlayer;
    static AudioManager audioManager;
    private Handler handler;
    private Song song;
    int totalSongs;
    private Random random;
    private MediaPlayer.OnCompletionListener onCompletionListener;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    static ArrayList<Song> songArrayList;
    private static String SHUFFLE_VALUE="SHUFFLE_VALUE",REPEAT_VALUE="REPEAT_VALUE";
    //focus listner's variable
    static AudioManager.OnAudioFocusChangeListener onAudioFocusChangeListener;


    /*
    REPEAT_VALUE==0 //no looping
    REPEAT_VALUE==1 //current song on loop
    REPEAT_VALUE==2 //looping

     */
    int shuffle_value=0,repeat_value=0;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        initializeViews();

        //get position of the clicked song from intent
        position=getIntent().getIntExtra("position",-1);
        Bundle bundle=getIntent().getBundleExtra("bundle");

        songArrayList=(ArrayList<Song>)bundle.getSerializable("array");
        totalSongs=songArrayList.size();

        //get song from songArrayList(static) using position
        playSong(position);
        updateRepeatImage();
        updateShuffleImage();


        image_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu=new PopupMenu(getApplicationContext(),v);
                MenuInflater menuInflater=popupMenu.getMenuInflater();
                menuInflater.inflate(R.menu.menu_list,popupMenu.getMenu());
                popupMenu.show();
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @SuppressLint("NonConstantResourceId")
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.menu_item_share:
                                shareSong();
                                break;
                            case R.id.menu_item_exit:
                                finish();
                                break;
                            case R.id.menu_item_upload:
                                if(FirebaseAuth.getInstance().getCurrentUser()==null){
                                    Toast.makeText(getApplicationContext(),"Kindly Login",Toast.LENGTH_SHORT).show();
                                }else{
                                    Utility.FirebaseStorageFunction(getApplicationContext(),uri,song);
                                }


                        }
                        return false;
                    }
                });
            }
        });

        image_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }



    @Override
    protected void onResume() {
        super.onResume();


        image_play_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer.isPlaying()){
                    //pausing the media player
                    //first changing the image icon
                    image_play_pause.setImageResource(R.drawable.play);
                    mediaPlayer.pause();

                }
                else{
                    image_play_pause.setImageResource(R.drawable.pause);
                    mediaPlayer.start();
                }
            }
        });

        image_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(shuffle_value==1){
                    position = random.nextInt(totalSongs);
                }
                else {
                    position = (position + 1) % totalSongs;
                }
                playSong(position);
            }
        });
        image_previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(shuffle_value==1){
                    position = random.nextInt(totalSongs);
                }
                else {
                    position = (position - 1 + totalSongs) % totalSongs;
                }
                playSong(position);
            }
        });
        image_replay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int temp=mediaPlayer.getCurrentPosition()-10*1000;
                mediaPlayer.seekTo(temp);
            }
        });
        image_forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int temp=mediaPlayer.getCurrentPosition()+10*1000;
                mediaPlayer.seekTo(temp);
            }
        });


        image_shuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shuffle_value=1-shuffle_value;
                updateShuffleImage();
                editor.putInt(SHUFFLE_VALUE,shuffle_value);
                editor.commit();

            }
        });
        image_repeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                repeat_value=(repeat_value+1)%3;
                updateRepeatImage();
                editor.putInt(REPEAT_VALUE,repeat_value);
                editor.commit();
            }
        });
    }




    private void updateRepeatImage() {
        /*
        REPEAT_VALUE==0 //no looping
        REPEAT_VALUE==1 //current song on loop
        REPEAT_VALUE==2 //looping
        */
        if(repeat_value==1){
            image_repeat.setImageResource(R.drawable.repeat_one);

        }
        else if(repeat_value==2){
            image_repeat.setImageResource(R.drawable.repeat_on);

        }
        else{
            image_repeat.setImageResource(R.drawable.repeat);

        }
    }

    private void updateShuffleImage() {
        if(shuffle_value==1){

            image_shuffle.setImageResource(R.drawable.shuffle_on);
        }
        else{
            image_shuffle.setImageResource(R.drawable.shuffle);

        }

    }


    private void playSong(int position) {

        if(songArrayList!=null){
            song=songArrayList.get(position);
            //get path of the current song
            if(song.getPathString()!=null){
                uri=Uri.parse(song.getPathString());
                //as current song is playable therefore make pause button visible instead of play button
                image_play_pause.setImageResource(R.drawable.pause);

                //before playing or staring the current song, we must release previous(if exists) resources
                releaseMediaPlayer();

                //int result=audioManager.requestAudioFocus(onAudioFocusChangeListener,AudioManager.STREAM_MUSIC,AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);

                //if(result==AudioManager.AUDIOFOCUS_REQUEST_GRANTED){
                //Toast.makeText(getApplicationContext(),"playSong: access gained",Toast.LENGTH_SHORT).show();
                if(mediaPlayer==null) {
                    mediaPlayer = MediaPlayer.create(this, uri);
                }
                mediaPlayer.start();
                mediaPlayer.setOnCompletionListener(onCompletionListener);
                updateSeekBar();
                updateMetaData();

                //}
            }

        }
    }
    //image,name,artist
    private void updateMetaData() {
        text_display_songname.setText(song.getTitleString());
        text_display_artistname.setText(song.getArtistString());
        Bitmap bm=GetSongs.getImage(song.getPathString());
        if(bm!=null){
            Palette.from(bm).generate(new Palette.PaletteAsyncListener() {
                @Override
                public void onGenerated(@Nullable Palette palette) {
                    Palette.Swatch swatch=palette.getDominantSwatch();
                    if(swatch==null) swatch=palette.getMutedSwatch();
                    else{
                        GradientDrawable gradientDrawable=new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,
                                new int[]{swatch.getRgb(),0x00000000});
                        constrain_layout_main_player.setBackground(gradientDrawable);
                    }
                }
            });
            image_display_songimage.setImageBitmap(bm);
        }
        else{
            constrain_layout_main_player.setBackgroundResource(R.drawable.gradient_background);
            image_display_songimage.setImageResource(R.drawable.music);
        }

    }

    //start_time, end_time, seekbar position
    private void updateSeekBar() {
        //set range for seek_bar
        seek_bar.setMax(mediaPlayer.getDuration()/1000);
        text_end_time.setText(formatedTime(mediaPlayer.getDuration()/1000));//call

        //when user makes changes in seekbar
        seek_bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(mediaPlayer!=null && fromUser==true){
                    if(progress==mediaPlayer.getDuration()/1000){
                        mediaPlayer.setOnCompletionListener(onCompletionListener);
                    }
                    mediaPlayer.seekTo(progress*1000);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        //as song runs, we need to make changes in seekbar and time accordingly
        PlayerActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(mediaPlayer!=null){
                    int currentPosition=mediaPlayer.getCurrentPosition()/1000;
                    seek_bar.setProgress(currentPosition);
                    text_start_time.setText(formatedTime(currentPosition));

                }
                //to run this thread each second
                handler.postDelayed(this,1000);
            }
        });


    }

    //to show users time of the song, we need to convert miliseconds into format of minutes:seconds
    private String formatedTime(int time) {
        String seconds= String.valueOf(time%60);
        String minutes= String.valueOf(time/60);
        String output="";
        output+=minutes+":";
        if(seconds.length()==1) output+="0";
        output+=seconds;
        return output;
    }

    //free resources of MediaPlayer
    static void releaseMediaPlayer(){
        if(mediaPlayer!=null){
            mediaPlayer.release();
            mediaPlayer=null;
            audioManager.abandonAudioFocus(onAudioFocusChangeListener);

        }

    }

    //initializing all views
    private void initializeViews(){
        image_display_songimage=findViewById(R.id.image_display_songimage);
        image_shuffle=findViewById(R.id.image_shuffle);
        image_previous=findViewById(R.id.image_previous);
        image_replay=findViewById(R.id.image_replay);
        image_play_pause=findViewById(R.id.image_play_pause);
        image_forward=findViewById(R.id.image_forward);
        image_next=findViewById(R.id.image_next);
        image_repeat=findViewById(R.id.image_repeat);
        text_start_time=findViewById(R.id.text_start_time);
        text_end_time=findViewById(R.id.text_end_time);
        text_display_songname=findViewById(R.id.text_display_songname);
        text_display_artistname=findViewById(R.id.text_display_artistname);
        seek_bar=findViewById(R.id.seek_bar);
        image_menu=findViewById(R.id.image_menu);
        image_back=findViewById(R.id.image_back);
        constrain_layout_main_player=findViewById(R.id.constrain_layout_main_player);

        audioManager= (AudioManager) this.getSystemService(this.AUDIO_SERVICE);
        handler=new Handler();

        onCompletionListener=new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {

                if (repeat_value == 1) {
                    //pass, dont change position
                    playSong(position);
                }
                //s:0 r:0 --> position+1 (if extreme release()
                //s:0 r:2 --> (position+1)%n
                //s:1 r:0 or 1 -> random
                else if(shuffle_value==1){
                    position = random.nextInt(totalSongs);
                    playSong(position);
                }
                else if (repeat_value == 0) {
                    position = position + 1;
                    if (position == totalSongs) {
                        mediaPlayer.stop();
                    }
                    else {
                        playSong(position);
                    }
                }
                else if (repeat_value == 2) {
                    position=(position+1)%totalSongs;
                    playSong(position);
                }
            }
        };
        random=new Random();

        sharedPreferences=PreferenceManager.getDefaultSharedPreferences(this);
        shuffle_value=sharedPreferences.getInt(SHUFFLE_VALUE,0);
        repeat_value=sharedPreferences.getInt(REPEAT_VALUE,0);
        editor=sharedPreferences.edit();
        audioManager= (AudioManager) this.getSystemService(this.AUDIO_SERVICE);
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                onAudioFocusChangeListener=listenerForFocusChange();
                handler.postDelayed(this,1000);
            }

        });


    }


    //make a listener for AudioFocusChange
    AudioManager.OnAudioFocusChangeListener listenerForFocusChange()
    {
        AudioManager.OnAudioFocusChangeListener onAudioFocusChangeListener=new AudioManager.OnAudioFocusChangeListener() {
            @Override
            public void onAudioFocusChange(int focusChange) {
                if(focusChange==AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK || focusChange==AudioManager.AUDIOFOCUS_LOSS_TRANSIENT){
                    image_play_pause.setImageResource(R.drawable.play);
                    mediaPlayer.pause();
                }
                else if(focusChange==AudioManager.AUDIOFOCUS_GAIN){
                    image_play_pause.setImageResource(R.drawable.pause);
                    Toast.makeText(getApplicationContext(),"Gain focus",Toast.LENGTH_SHORT).show();
                    mediaPlayer.start();
                }
                else if(focusChange==AudioManager.AUDIOFOCUS_LOSS){
                    image_play_pause.setImageResource(R.drawable.play);
                    Toast.makeText(getApplicationContext(),"lost now focus",Toast.LENGTH_SHORT).show();
                    mediaPlayer.pause();
                }
            }
        };
        return onAudioFocusChangeListener;
    }
    void shareSong(){
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setType("audio/*");
        song=songArrayList.get(position);
        //get path of the current song
        uri=Uri.parse(song.getPathString());
        shareIntent.putExtra(Intent.EXTRA_STREAM,uri);
        startActivity(Intent.createChooser(shareIntent,"Share song"));
    }




}
