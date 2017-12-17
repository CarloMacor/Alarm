package com.macormap.lasveglia;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Vibrator;


/**
 * Created by carlo on 19/09/2017.
 */

public class AlarmPlayer {

    public MediaPlayer soundPlayer;

    public Vibrator vibrator;

    private long[] pattern = {0, 450, 1000};


    public AlarmPlayer () {}

    public void PlayAlarm(Context context, int indSound,int indkyndsound,int thevolume) {

        int maxVolume = 100;
        StopAll();


        switch (indSound) {
            case 0 : soundPlayer = MediaPlayer.create(context, R.raw.lg_simple_beep);     break;
            case 1 : soundPlayer = MediaPlayer.create(context, R.raw.htc_ring_ring);      break;
            case 2 : soundPlayer = MediaPlayer.create(context, R.raw.despacito_ringtone); break;
            case 3 : soundPlayer = MediaPlayer.create(context, R.raw.apple_ring);         break;
            case 4 : soundPlayer = MediaPlayer.create(context, R.raw.narcos);             break;
            case 5 : soundPlayer = MediaPlayer.create(context, R.raw.nba);                break;
            case 6 : soundPlayer = MediaPlayer.create(context, R.raw.nokia_iphone);       break;
            case 7 : soundPlayer = MediaPlayer.create(context, R.raw.ringtone_plus_ii);   break;
            case 8 : soundPlayer = MediaPlayer.create(context, R.raw.shape_of_you);       break;
            default:
                soundPlayer = MediaPlayer.create(context, R.raw.lg_simple_beep);
                break;
        }



        float setVolume=(float)(1-(Math.log(maxVolume-thevolume)/Math.log(maxVolume)));

        switch (indkyndsound) {
            case 0 :
                soundPlayer.setVolume(setVolume,setVolume);
                soundPlayer.setLooping(true);
                soundPlayer.start();
              break;

            case 1 :
                soundPlayer.setVolume(setVolume,setVolume);
                soundPlayer.setLooping(true);
                soundPlayer.start();

                vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                // Vibrate for 500 milliseconds
                vibrator.vibrate(pattern, 0);

              break;


            case 2 :
                vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                // Vibrate for 500 milliseconds
                vibrator.vibrate(pattern, 0);

                break;


        }


    }


    public void changeVolume(int newVolume) {
        int maxVolume = 100;
        float setVolume=(float)(1-(Math.log(maxVolume-newVolume)/Math.log(maxVolume)));
        soundPlayer.setVolume(setVolume,setVolume);
    }

    public void StopAll() {
        if(soundPlayer != null) {soundPlayer.stop(); soundPlayer.release(); soundPlayer = null;}

        if (vibrator != null) { vibrator.cancel(); vibrator = null;  }
    }




}
