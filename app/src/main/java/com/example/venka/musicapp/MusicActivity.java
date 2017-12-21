package com.example.venka.musicapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.Allocator;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultAllocator;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.util.Util;

import java.io.IOException;

public class MusicActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener {
TextView song,artists;
ImageView imageView;
    private SeekBar songProgressBar;
    private Handler mHandler = new Handler();;
    private Utilities utils;
    private TextView songCurrentDurationLabel;
    private TextView songTotalDurationLabel;
    private int seekForwardTime = 5000; // 5000 milliseconds
    private int seekBackwardTime = 5000; // 5000 milliseconds
ImageButton play,prev,next;
    private boolean playPause;
    boolean init=true;
    int idi,total,ip,cp=0;
    String s[]=new String[5];
    private MediaPlayer mediaPlayer;
    SqliteDB db=new SqliteDB(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);
        songProgressBar = (SeekBar) findViewById(R.id.songProgressBar);
        songCurrentDurationLabel = (TextView) findViewById(R.id.songCurrentDurationLabel);
        songTotalDurationLabel = (TextView) findViewById(R.id.songTotalDurationLabel);
        song=(TextView)findViewById(R.id.songname);
        artists=(TextView)findViewById(R.id.artistsname);
        imageView=(ImageView)findViewById(R.id.imagesong);
        play=(ImageButton)findViewById(R.id.play);
        prev=(ImageButton)findViewById(R.id.backward);
        next=(ImageButton)findViewById(R.id.forward);
        utils = new Utilities();

        // Listeners
        songProgressBar.setOnSeekBarChangeListener(this); // Important
        Intent intent=getIntent();
        String id=intent.getStringExtra("EXTRA_SESSION_ID");
        total=intent.getIntExtra("total",10);
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        idi=Integer.parseInt(id);
        Toast.makeText(this,id,Toast.LENGTH_LONG).show();
        s=db.getUser(id);
        play.setBackgroundResource(R.drawable.pause);
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.start();
                ip = mediaPlayer.getDuration();
                cp=mediaPlayer.getCurrentPosition();
                cp=1;
            }
        });
        if(init)
            playSong(idi);
        playPause = true;
        song.setText(s[1]);
        artists.setText(s[3]);
        Glide.with(this)
                .load(s[4])
                .into(imageView);
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 if (!playPause) {
                    play.setBackgroundResource(R.drawable.pause);
                    if(init)
                    playSong(idi);
                    else
                    {
                        mediaPlayer.start();
                    }
                    playPause = true;
                } else {
                    play.setBackgroundResource(R.drawable.play);
                    if (mediaPlayer.isPlaying()){
                        mediaPlayer.pause();
                    }
                    playPause = false;
                }
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MusicActivity.class);
                mediaPlayer.release();
                intent.putExtra("EXTRA_SESSION_ID", (idi+1)%total+"");
                intent.putExtra("total",total);
                startActivity(intent);
                finish();
            }
        });
        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(idi==0)
                {
                    Intent intent = new Intent(getApplicationContext(), MusicActivity.class);
                    mediaPlayer.release();
                    intent.putExtra("EXTRA_SESSION_ID", total-1+"");
                    intent.putExtra("total",total);
                    mediaPlayer.release();
                    startActivity(intent);
                    finish();
                }
                else
                {
                    Intent intent = new Intent(getApplicationContext(), MusicActivity.class);
                    intent.putExtra("EXTRA_SESSION_ID", (idi-1)%total+"");
                    intent.putExtra("total",total);
                    mediaPlayer.release();
                    startActivity(intent);
                    finish();
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        Intent i=new Intent(this,GetData.class);
        startActivity(i);
        mediaPlayer.release();
        finish();
        super.onBackPressed();
    }

    public void updateProgressBar() {
        mHandler.postDelayed(mUpdateTimeTask, 100);
    }

    /**
     * Background Runnable thread
     * */
    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            long totalDuration =ip;
            long currentDuration=cp;
            if(cp>0) {
                try{
                currentDuration = mediaPlayer.getCurrentPosition();}
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

            songTotalDurationLabel.setText(""+utils.milliSecondsToTimer(totalDuration));
            // Displaying time completed playing
            songCurrentDurationLabel.setText(""+utils.milliSecondsToTimer(currentDuration));

            // Updating progress bar
            int progress = (int)(utils.getProgressPercentage(currentDuration, totalDuration));
            //Log.d("Progress", ""+progress);
            songProgressBar.setProgress(progress);

            // Running this thread after 100 milliseconds
            mHandler.postDelayed(this, 100);
        }
    };

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        mHandler.removeCallbacks(mUpdateTimeTask);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mHandler.removeCallbacks(mUpdateTimeTask);
        int totalDuration = mediaPlayer.getDuration();
        int currentPosition = utils.progressToTimer(seekBar.getProgress(), totalDuration);

        // forward or backward to certain seconds
        mediaPlayer.seekTo(currentPosition);

        // update timer progress again
        updateProgressBar();
    }

    class Player extends AsyncTask<String, Void, Boolean> {
        private ProgressDialog progress;

        @Override
        protected Boolean doInBackground(String... params) {
            // TODO Auto-generated method stub
            Boolean prepared;
            try {

                mediaPlayer.setDataSource(params[0]);

                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        // TODO Auto-generated method stub

                        playPause=false;
                        //btn.setBackgroundResource(R.drawable.button_play);
                        mediaPlayer.stop();
                        mediaPlayer.reset();
                    }
                });
                mediaPlayer.prepare();
                prepared = true;
            } catch (IllegalArgumentException e) {
                // TODO Auto-generated catch block
                Log.d("IllegarArgument", e.getMessage());
                prepared = false;
                e.printStackTrace();
            } catch (SecurityException e) {
                // TODO Auto-generated catch block
                prepared = false;
                e.printStackTrace();
            } catch (IllegalStateException e) {
                // TODO Auto-generated catch block
                prepared = false;
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                prepared = false;
                e.printStackTrace();
            }
            return prepared;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            Log.d("Prepared", "//" + result);
            mediaPlayer.start();
        }

        public Player() {
            //progress = new ProgressDialog(SongCustomAdapter.this);
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();


        }
    }
    public void playSong(int id)
    {
        new Player()
                .execute(s[2]);
        songProgressBar.setProgress(0);
        songProgressBar.setMax(100);

        // Updating progress bar
        updateProgressBar();
    }

}
