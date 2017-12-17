package com.macormap.lasveglia;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class WakeupActivity extends AppCompatActivity {

    private List<AlarmObj> alarmObjList = new ArrayList<>();
    private AlarmPlayer alarmPlayer;
    AlarmObj alarmObj = new AlarmObj(this) ;

    private int startVolumeLevel;
    private AudioManager audioManager;
    private int maxPossibleValueVolume;
    private Handler mHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wakeup);

        mHandler = new Handler();

        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        startVolumeLevel= audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        maxPossibleValueVolume = audioManager.getStreamMaxVolume(audioManager.STREAM_MUSIC);

        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,maxPossibleValueVolume,0);

        StoreData.readFileWaker(this,alarmObj);
        setTextAndPlaySound();

        StoreData.readFileAlarms(this,alarmObjList);
        StoreData.ReorderListAlarm(alarmObjList);
        StoreData.saveAlarms(this,alarmObjList);
        Functions.SetIntent(this,alarmObjList);


            // Wake up phone
            this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                            WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                            WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON,
                            WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                            WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                            WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);


    }


    public void StopAlarmer(View view) {
        alarmPlayer.StopAll();
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,startVolumeLevel,0);

        this.finish();
        System.exit(0);
    }


    private void setTextAndPlaySound() {
            TextView txt1 = (TextView) findViewById(R.id.idtxtNameWakeup);
            txt1.setText(alarmObj.getName_alarm());
            TextView txt2 = (TextView) findViewById(R.id.idtxtTimeWakeup);
            txt2.setText(alarmObj.getStrTime());

            alarmPlayer = new AlarmPlayer();
            alarmPlayer.PlayAlarm(this,alarmObj.getIndRingtone(),alarmObj.getIndKindSound(),alarmObj.getVolumeAlarm());

            mHandler.postDelayed(runnableCloseSound,1000*60*2);

    }




    Runnable runnableCloseSound = new Runnable() {
        @Override
        public void run() {  alarmPlayer.StopAll(); }
    };

    @Override
    public void onDestroy() {
        alarmPlayer.StopAll();
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,startVolumeLevel,0);
        super.onDestroy();
    }

}
