package com.example.venka.musicapp;

/**
 * Created by venka on 12/17/2017.
 */

import java.io.IOException;
import java.util.ArrayList;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

public class SongCustomAdapter extends ArrayAdapter<Song> {


    Context context;
    int layoutResourceId;
    private boolean playPause;
    private MediaPlayer mediaPlayer;
    ArrayList<Song> data = new ArrayList<Song>();

    public SongCustomAdapter(Context context, int layoutResourceId,
                             ArrayList<Song> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View row = convertView;
        UserHolder holder = null;
        if (row == null) {
            LayoutInflater inflater = ((GetData) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new UserHolder();
            holder.textName = (TextView) row.findViewById(R.id.textView1);
            holder.textAddress = (TextView) row.findViewById(R.id.textView2);
            //holder.textLocation = (TextView) row.findViewById(R.id.textView3);
            holder.btnEdit = (ImageButton) row.findViewById(R.id.button1);
            holder.btnDelete = (ImageButton) row.findViewById(R.id.button2);
            holder.imageView = (ImageView) row.findViewById(R.id.imageview1);
            holder.btnEdit.setBackgroundResource(R.drawable.play);
            row.setTag(holder);
        }
        else {
            holder = (UserHolder) row.getTag();
        }
        final Song user = data.get(position);
        holder.textName.setText(user.getSong());
        holder.textAddress.setText("Artists: "+user.getArtists());
        Glide.with(context)
                .load(user.getImgUrl())
                .into(holder.imageView);
        final UserHolder finalHolder = holder;

        holder.btnEdit.setOnClickListener(new OnClickListener() {
            int check;
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MusicActivity.class);
                intent.putExtra("EXTRA_SESSION_ID", user.getId()+"");
                intent.putExtra("total",user.getTotal());
                context.startActivity(intent);
               /* if (!playPause) {
                    check=position;
                    finalHolder.btnEdit.setBackgroundResource(R.drawable.pause);
                    playSong(check);
                    playPause = true;
                } else {
                    finalHolder.btnEdit.setBackgroundResource(R.drawable.play);
                    if (mediaPlayer.isPlaying()){
                        mediaPlayer.pause();
                    }
                    playPause = false;
                }*/
            }
        });
        holder.btnDelete.setBackgroundResource(R.drawable.download);
        holder.btnDelete.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Log.i("Delete Button Clicked", "**********");
                Toast.makeText(context, "Delete button Clicked"+user.getId(),
                        Toast.LENGTH_LONG).show();
            }
        });

        holder.imageView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Log.i("image Clicked", "**********");
                Toast.makeText(context, "image Clicked",
                        Toast.LENGTH_LONG).show();
            }
        });
        return row;

    }

    static class UserHolder {
        TextView textName;
        TextView textAddress;
        TextView textLocation;
        ImageButton btnEdit;
        ImageButton btnDelete;
        ImageView imageView;
    }

}
