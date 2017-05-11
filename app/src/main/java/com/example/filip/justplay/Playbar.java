package com.example.filip.justplay;

/**
 Class of PlayBar
 */
import android.media.MediaPlayer;
import android.media.session.MediaController;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatDelegate;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

public class Playbar extends Fragment{

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    private static ImageView play;
    private static ImageView forward;
    private static ImageView rewind;
    private static ImageView favorite;
    private static ImageView block;
    private static TextView  textView;

    private SeekBar seekbar;

    private MediaPlayer mediaPlayer;

    private double startTime = 0;
    private double finalTime = 0;

    private Handler myHandler = new Handler();;
    private int forwardTime = 5000;
    private int backwardTime = 5000;

    public static int oneTimeOnly = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.playbar, container, false);

        play = (ImageView) view.findViewById(R.id.play);
        forward = (ImageView) view.findViewById(R.id.forward);
        rewind = (ImageView) view.findViewById(R.id.rewind);
        seekbar = (SeekBar)view.findViewById(R.id.seekbar);


        play.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mediaPlayer.setLooping(true);
                        mediaPlayer.start();
                       /* finalTime = mediaPlayer.getDuration();
                        startTime = mediaPlayer.getCurrentPosition();

                        if (oneTimeOnly == 0) {
                            seekbar.setMax((int) finalTime);
                            oneTimeOnly = 1;
                        }

                        textView.setText(String.format("%d min, %d sec",
                                TimeUnit.MILLISECONDS.toMinutes((long) finalTime),
                                TimeUnit.MILLISECONDS.toSeconds((long) finalTime) -
                                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long)
                                                finalTime)))
                        );

                        textView.setText(String.format("%d min, %d sec",
                                TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                                TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
                                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long)
                                                startTime)))
                        );

                        seekbar.setProgress((int)startTime);
                        myHandler.postDelayed(UpdateSongTime,100);
                        play.setEnabled(false);*/

                    }
                });
        forward.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        /*int temp = (int)startTime;
                        if((temp+forwardTime)<=finalTime){
                            startTime = startTime + forwardTime;
                            mediaPlayer.seekTo((int) startTime);
                            Toast.makeText(getApplicationContext(),"You have Jumped forward 5 seconds",Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(getApplicationContext(),"Cannot jump forward 5 seconds",Toast.LENGTH_SHORT).show();
                        }*/
                    }
                });
        rewind.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        /*int temp = (int) startTime;

                        if ((temp - backwardTime) > 0) {
                            startTime = startTime - backwardTime;
                            mediaPlayer.seekTo((int) startTime);
                            Toast.makeText(getApplicationContext(), "You have Jumped backward 5 seconds", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Cannot jump backward 5 seconds", Toast.LENGTH_SHORT).show();
                        }*/
                    }
                });


        return view;
    }
    /*private Runnable UpdateSongTime = new Runnable() {
        public void run() {
            startTime = mediaPlayer.getCurrentPosition();
            textView.setText(String.format("%d min, %d sec",
                    TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                    TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                    toMinutes((long) startTime)))
            );
            seekbar.setProgress((int)startTime);
            myHandler.postDelayed(this, 100);
        }
    };*/


}
