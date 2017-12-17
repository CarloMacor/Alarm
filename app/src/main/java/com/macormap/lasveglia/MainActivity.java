package com.macormap.lasveglia;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Spanned;
import android.view.KeyEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity implements
        RadioGroup.OnCheckedChangeListener {

    private RecyclerView recyclerAlarmView;
    private AlarmAdapter mAlarmAdapter;
    private List<AlarmObj> alarmObjList = new ArrayList<>();
    private boolean inEditPage = false;
    private int alarmInEdit = -1;
    private TimePicker timePicker;

 //   private static final String  nomefilesAlert ="Alerter";
    private AlarmPlayer alarmPlayer = null;



    boolean[] localdays_activated = new boolean[7];

    Button[] textViewsDaysweekActive = new Button[7];

    Menu menu;
    AlarmObj lastErasedAlarmObj = null;

    private final Handler mHandler = new Handler();

    private ImageButton[] butChoiseSoundType = new ImageButton[3];
    private int indChoiseSoundType =0;

    private int startVolumeLevel;
    private AudioManager audioManager;
    private int maxPossibleValueVolume;

    private RadioGroup radioGroupRingotes;
    private RadioButton[] arrayRadioButRingtone = new RadioButton[9];
    private int indLocalRingtone =0;

    private SeekBar volumeBar;

    private Animation animFromCenter;
    private Animation animToCenter;
    private Animation animChangedAlarm;

    private int      ind_alarmToAnimate = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FromCreate_SetToolbar();
        FromCreate_SetFab();
        FromCreate_SetAnimations();
        FromCreate_SetRecycler();
        setInfo2Lines();
        FromCreate_VolumeControl();
        FromCreate_SetButtons();
        FromCreate_VolumeBar();
    }

    // sub module of onCreate

    private void FromCreate_SetToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.app_name);
    }

    private void FromCreate_SetFab() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { alarmInEdit = -1; ChangePage(); }
        });
    }

    private void FromCreate_SetRecycler() {

        recyclerAlarmView = (RecyclerView) findViewById(R.id.recycler_alarms);
        mAlarmAdapter = new AlarmAdapter(alarmObjList, new AlarmAdapter.MyAdapterListener() {
          @Override
          public void btnEditAlarmViewOnClick(View v, int position) {EditAlarm(position);}

          @Override
          public void btnOnOffViewOnClick(View v, int position) {SetOnOffAlarm(position);}

          public void ErasedAlarm(View v, int position) { ErasedAlarmatPosition(position); }
        });

        //     recyclerAlarmView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerAlarmView.setLayoutManager(mLayoutManager);

        // recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        RecyclerView.ItemAnimator itemAnimator  = new DefaultItemAnimator();

//        itemAnimator.setRemoveDuration(2000);
        itemAnimator.setAddDuration(4000);
        recyclerAlarmView.setItemAnimator(itemAnimator);

        recyclerAlarmView.setAdapter(mAlarmAdapter);

        MainReadFileAlarms();
        mAlarmAdapter.notifyDataSetChanged();
    }

    private void FromCreate_VolumeBar() {
        volumeBar = (SeekBar) findViewById(R.id.idSeekBarVolume);
        volumeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (alarmPlayer!=null) { alarmPlayer.changeVolume(progress); }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {  }
        });

        volumeBar.getProgressDrawable().setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN);
    }

    private void FromCreate_SetButtons() {

        timePicker = (TimePicker) findViewById(R.id.idtimePicker);
        timePicker.setIs24HourView(true);

        Button butCancel = (Button) findViewById(R.id.idCancelSetup);
        butCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {ChangePage();}
        } );

        Button butSave = (Button) findViewById(R.id.idSaveSetup);
        butSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { AddEditAlarm();  ChangePage();}
        } );

        Button button = (Button) findViewById(R.id.idAnteprima);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlarmPreview();
            }
        });

        // setup week days
        for (int i=0; i<7; i++) { localdays_activated[i]=true; }
        textViewsDaysweekActive[0] = (Button) findViewById(R.id.idDay1);
        textViewsDaysweekActive[1] = (Button) findViewById(R.id.idDay2);
        textViewsDaysweekActive[2] = (Button) findViewById(R.id.idDay3);
        textViewsDaysweekActive[3] = (Button) findViewById(R.id.idDay4);
        textViewsDaysweekActive[4] = (Button) findViewById(R.id.idDay5);
        textViewsDaysweekActive[5] = (Button) findViewById(R.id.idDay6);
        textViewsDaysweekActive[6] = (Button) findViewById(R.id.idDay7);

        for (int i=0; i<7; i++) {
            textViewsDaysweekActive[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ChangeDayState(view);
                }
            });
        }

        butChoiseSoundType[0] = (ImageButton) findViewById(R.id.idkind1);
        butChoiseSoundType[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {SetButChoiseSoundType(0);     }
        });
        butChoiseSoundType[1] = (ImageButton) findViewById(R.id.idkind2);
        butChoiseSoundType[1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {SetButChoiseSoundType(1);     }
        });
        butChoiseSoundType[2] = (ImageButton) findViewById(R.id.idkind3);
        butChoiseSoundType[2].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {SetButChoiseSoundType(2);     }
        });

        radioGroupRingotes = (RadioGroup) findViewById(R.id.idRadioRingtones);
        radioGroupRingotes.setOnCheckedChangeListener(this);

        arrayRadioButRingtone[0] = (RadioButton) findViewById(R.id.id_ring0);
        arrayRadioButRingtone[1] = (RadioButton) findViewById(R.id.id_ring1);
        arrayRadioButRingtone[2] = (RadioButton) findViewById(R.id.id_ring2);
        arrayRadioButRingtone[3] = (RadioButton) findViewById(R.id.id_ring3);
        arrayRadioButRingtone[4] = (RadioButton) findViewById(R.id.id_ring4);
        arrayRadioButRingtone[5] = (RadioButton) findViewById(R.id.id_ring5);
        arrayRadioButRingtone[6] = (RadioButton) findViewById(R.id.id_ring6);
        arrayRadioButRingtone[7] = (RadioButton) findViewById(R.id.id_ring7);
        arrayRadioButRingtone[8] = (RadioButton) findViewById(R.id.id_ring8);
    }

    private void FromCreate_VolumeControl() {
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        startVolumeLevel= audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        maxPossibleValueVolume = audioManager.getStreamMaxVolume(audioManager.STREAM_MUSIC);

        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,maxPossibleValueVolume,0);
    }

    private void FromCreate_SetAnimations() {
        animFromCenter   = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.anim_from_center);
        animToCenter     = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.anim_to_center);
        animChangedAlarm = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.anim_changedalarm);
        animToCenter.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {  }

            @Override
            public void onAnimationEnd(Animation animation) {
                LinearLayout mainPage = (LinearLayout) findViewById(R.id.idMainPage);
                mainPage.setVisibility(View.VISIBLE);
                FloatingActionButton fabber = (FloatingActionButton) findViewById(R.id.fab);
                fabber.setVisibility(View.VISIBLE);
                LinearLayout editPage = (LinearLayout) findViewById(R.id.idSetupAlarm);
                editPage.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) { }
        });
    }

    // sub module of onCreate  END


    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int ind) {
        switch (ind) {
            case R.id.id_ring0 : indLocalRingtone =0; break;
            case R.id.id_ring1 : indLocalRingtone =1; break;
            case R.id.id_ring2 : indLocalRingtone =2; break;
            case R.id.id_ring3 : indLocalRingtone =3; break;
            case R.id.id_ring4 : indLocalRingtone =4; break;
            case R.id.id_ring5 : indLocalRingtone =5; break;
            case R.id.id_ring6 : indLocalRingtone =6; break;
            case R.id.id_ring7 : indLocalRingtone =7; break;
            case R.id.id_ring8 : indLocalRingtone =8; break;
        }
        if (alarmPlayer!=null) { stopAlarmPreview(); }
        if (inEditPage) {AlarmPreview();}
    }

    private void ChangeDayState(View view ) {
        int index =0;
        int id = view.getId();
        switch (id) {
            case R.id.idDay1 : index =0; break;
            case R.id.idDay2 : index =1; break;
            case R.id.idDay3 : index =2; break;
            case R.id.idDay4 : index =3; break;
            case R.id.idDay5 : index =4; break;
            case R.id.idDay6 : index =5; break;
            case R.id.idDay7 : index =6; break;
        }

        localdays_activated[index] = ! localdays_activated[index];
        if (localdays_activated[index]) { textViewsDaysweekActive[index].setAlpha(1f); }
                                  else  { textViewsDaysweekActive[index].setAlpha(0.25f); }
    }

    private void SetOnOffAlarm(int ind) {
        AlarmObj alarmObj = alarmObjList.get(ind);
        alarmObj.setState_alarm(!alarmObj.isState_onoff());
        mAlarmAdapter.notifyDataSetChanged();
        MainSaveAlarms();
    }

    private void ErasedAlarmatPosition(int ind) {
       lastErasedAlarmObj = alarmObjList.get(ind);
       mAlarmAdapter.notifyDataSetChanged();

        MenuItem item = menu.findItem(R.id.action_undo);
        item.setTitle("Undo "+lastErasedAlarmObj.getStrTime());
        item.setVisible(true);
        alarmObjList.remove(ind);

        mAlarmAdapter.notifyItemRemoved(ind);
//        mAlarmAdapter.notifyDataSetChanged();
        MainSaveAlarms();
    }

    private void EditAlarm(int ind) {
        alarmInEdit = ind;
        ChangePage();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_undo) {
            if (lastErasedAlarmObj !=null ) {
                MenuItem locitem = menu.findItem(R.id.action_undo);
                locitem.setVisible(false);
                alarmObjList.add(lastErasedAlarmObj);
                lastErasedAlarmObj = null;
                StoreData.ReorderListAlarm(alarmObjList);
                mAlarmAdapter.notifyDataSetChanged();
                MainSaveAlarms();
            }
            return true;
        }

        if (id == R.id.action_infoapp) {
            final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
            } catch (android.content.ActivityNotFoundException anfe) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
            }
            return true;
        }
        if (id == R.id.action_closeapp) {
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,startVolumeLevel,0);
            this.finish();
            System.exit(0);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            if  (inEditPage) {ChangePage();}
            else {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,startVolumeLevel,0);
                this.finish();
                System.exit(0);
                return false;
            }
        } else { return super.onKeyDown(keyCode, event);}
        return true;
    }

    public void setInfo2Lines() {
        TextView loctxt = (TextView) findViewById(R.id.textView1);
        TextView loctxt2 = (TextView) findViewById(R.id.textView2);

        if (alarmObjList.size()<=0) {
            String mystring_1 = getResources().getString(R.string.str_no_alarm_present);
            Spanned result = Functions.getStringColored("",mystring_1);
            loctxt.setText(result);

            String mystring_2 = getResources().getString(R.string.str_no_alarm_present2);
            Spanned result2 = Functions.getStringColored("",mystring_2);
            loctxt2.setText(result2);
        } else
        {
            AlarmObj alarmObj= null;
            boolean foundOnOne = false;
            alarmObj = Functions.NextAlarmAvailable(alarmObjList);
            if (alarmObj != null) {
                foundOnOne = true;
                String strmsg1 = "";
                int theminutesLeft = alarmObj.minutesFromNow();

                Calendar calendar = Calendar.getInstance();
                int indToday = calendar.get(Calendar.DAY_OF_WEEK);

                strmsg1 = Functions.NextDayStr(this,alarmObj.nextDayAvailable);

                if (indToday==alarmObj.nextDayAvailable) { strmsg1   = getResources().getString(R.string.str_Today) +  "  "; }
                int nextindToday= indToday+1; if (nextindToday>=7) { nextindToday = nextindToday-7;}
                if (nextindToday==alarmObj.nextDayAvailable) { strmsg1   = getResources().getString(R.string.str_Tomorrow) +  "  "; }


                String strmsg0 =  getResources().getString(R.string.str_NextAlarm);
                String strmsg2 = strmsg1+alarmObj.getStrTime();
                Spanned result = Functions.getStringColored(strmsg0,strmsg2);
                loctxt.setText(result);

                int hh =  (int)(theminutesLeft / 60);
                int mm =        theminutesLeft % 60;
                String strHour = "";
                if (hh==1) {strHour = getResources().getString(R.string.str_Hour) +" " + getResources().getString(R.string.str_and) + " "; }
                if (hh>1)  {strHour = getResources().getString(R.string.str_Hours) +" " + getResources().getString(R.string.str_and) + " "; }

                String strMinute ="";
                if (mm==0) {strMinute = getResources().getString(R.string.str_Minutes); }
                if (mm==1) {strMinute = getResources().getString(R.string.str_Minute); }
                if (mm>1)  {strMinute = getResources().getString(R.string.str_Minutes); }

                String strtimeleft;
                if (hh>0) { strtimeleft = Integer.toString(hh)+ " "+ strHour + " " + Integer.toString(mm) + " " + strMinute;}
                     else { strtimeleft = Integer.toString(mm) + " " + strMinute;}

                String strTimeRemaining = getResources().getString(R.string.str_time_remaning);
                Spanned result2 = Functions.getStringColored(strTimeRemaining,strtimeleft);
                loctxt2.setText(result2);
            }
            else {
                String strNoAlarm = getResources().getString(R.string.str_no_alarm_active);
                Spanned result = Functions.getStringColored("",strNoAlarm);
                loctxt.setText(result);
                loctxt2.setText("");
            }
        }


  //      loctxt.setText(Integer.toString(startVolumeLevel));
  //      loctxt2.setText(Integer.toString(maxPossibleValueVolume));

    }

    private void SetButChoiseSoundType(int newind) {
        for (int j=0; j<3; j++) { butChoiseSoundType[j].setAlpha(0.3f); }
        indChoiseSoundType = newind;
        butChoiseSoundType[indChoiseSoundType].setAlpha(1f);
        if (alarmPlayer!=null) { stopAlarmPreview();  }
        if (inEditPage) { AlarmPreview(); }
    }

    private void ChangePage() {
        hideSoftKeyboard();
        stopAlarmPreview();

        if (!inEditPage) {

            LinearLayout editPage = (LinearLayout) findViewById(R.id.idSetupAlarm);
            editPage.setVisibility(View.VISIBLE);
            // here the animation of the edit page in enter fase .
            editPage.startAnimation(animFromCenter);

            LinearLayout mainPage = (LinearLayout) findViewById(R.id.idMainPage);
            mainPage.setVisibility(View.GONE);
            FloatingActionButton fabber = (FloatingActionButton) findViewById(R.id.fab);
            fabber.setVisibility(View.GONE);
            TextView textViewNome = (TextView) findViewById(R.id.id_editnamealarm);

            if (alarmInEdit <0) {   // newAlarm
              SetButChoiseSoundType(0);
              volumeBar.setProgress(80);
              arrayRadioButRingtone[0].setChecked(true);
              if (Build.VERSION.SDK_INT >= 23 ) { timePicker.setHour(7); timePicker.setMinute(30); }
                                          else { timePicker.setCurrentHour(7); timePicker.setCurrentMinute(30);}

              getSupportActionBar().setTitle(R.string.str_add_alarm);
              textViewNome.setText(R.string.str_alarm);
              for (int i=0; i<7; i++) {
                 localdays_activated[i]=true;
                 textViewsDaysweekActive[i].setAlpha(1f);
              }
              inEditPage = true;
            }
            else  {                   // edit alarm
              getSupportActionBar().setTitle(R.string.str_edit_alarm);
              AlarmObj alarmObj = alarmObjList.get(alarmInEdit);

              volumeBar.setProgress(alarmObj.getVolumeAlarm());
              arrayRadioButRingtone[alarmObj.getIndRingtone()].setChecked(true);
              if (Build.VERSION.SDK_INT >= 23 ) {
                 timePicker.setHour(alarmObj.getTimeHour());
                 timePicker.setMinute(alarmObj.getTimeMinute()); }
               else {
                 timePicker.setCurrentHour(alarmObj.getTimeHour());
                 timePicker.setCurrentMinute(alarmObj.getTimeMinute()); }

              textViewNome.setText(alarmObj.getName_alarm());
              for (int i=0; i<7; i++) {
                 localdays_activated[i]=alarmObj.isDay_onoff(i);
                 if (localdays_activated[i]) { textViewsDaysweekActive[i].setAlpha(1f); }
                                       else  { textViewsDaysweekActive[i].setAlpha(0.25f); }
              }
              SetButChoiseSoundType(alarmObj.getIndKindSound());
              inEditPage = true;
            }
        }
        else {  // back to list of all alarms
            inEditPage = false;
            getSupportActionBar().setTitle(R.string.app_name);
            LinearLayout editPage = (LinearLayout) findViewById(R.id.idSetupAlarm);
            StoreData.ReorderListAlarm(alarmObjList);
            mAlarmAdapter.notifyDataSetChanged();
            editPage.startAnimation(animToCenter);
        }
    }

    private void AddEditAlarm() {

        if (alarmInEdit <0) {
            AlarmObj alarmObj = new AlarmObj(this);
            alarmObjList.add(alarmObj);
            EditText editText = (EditText) findViewById(R.id.id_editnamealarm);
            alarmObj.setName_alarm(editText.getText().toString());
            if (Build.VERSION.SDK_INT >= 23 ) {
                   alarmObj.setTimeHour   (timePicker.getHour());
                   alarmObj.setTimeMinute (timePicker.getMinute()); }
            else { alarmObj.setTimeHour   (timePicker.getCurrentHour());
                   alarmObj.setTimeMinute (timePicker.getCurrentMinute()); }
            for (int i=0; i<7; i++) {alarmObj.setDay_onoff(i,localdays_activated[i]);}
            alarmObj.setIndKindSound(indChoiseSoundType);
            alarmObj.setIndRingtone(indLocalRingtone);
            alarmObj.setVolumeAlarm( volumeBar.getProgress());
        } else {
            AlarmObj alarmObj = alarmObjList.get(alarmInEdit);
            EditText editText = (EditText) findViewById(R.id.id_editnamealarm);
            alarmObj.setName_alarm(editText.getText().toString());
            if (Build.VERSION.SDK_INT >= 23 ) {
                alarmObj.setTimeHour(timePicker.getHour());
                alarmObj.setTimeMinute(timePicker.getMinute());
            }
            else {
                alarmObj.setTimeHour(timePicker.getCurrentHour());
                alarmObj.setTimeMinute(timePicker.getCurrentMinute());
            }
            for (int i=0; i<7; i++) {alarmObj.setDay_onoff(i,localdays_activated[i]);}
            alarmObj.setIndKindSound(indChoiseSoundType);
            alarmObj.setIndRingtone(indLocalRingtone);
            alarmObj.setVolumeAlarm( volumeBar.getProgress());
        }
        // reorder AlarmObj
        StoreData.ReorderListAlarm(alarmObjList);
        mAlarmAdapter.notifyDataSetChanged();
        MainSaveAlarms();
    }

    private void animateAlarmByIndex(int indexToAnimate) {
       View view = recyclerAlarmView.findViewHolderForAdapterPosition(indexToAnimate).itemView;
       view.startAnimation(animChangedAlarm);
    }



    public void hideSoftKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow( getCurrentFocus().getWindowToken(), 0);
    }

    private void MainSaveAlarms() {
       StoreData.saveAlarms(this,alarmObjList);
       Functions.SetIntent(this,alarmObjList);
       setInfo2Lines();
    }

    private void MainReadFileAlarms() {
        StoreData.readFileAlarms(this,alarmObjList);
        StoreData.ReorderListAlarm(alarmObjList);
        Functions.SetIntent(this,alarmObjList);
    }

    private void stopAlarmPreview() {
        if (alarmPlayer!=null) {
            alarmPlayer.StopAll();
            alarmPlayer = null;
            Button button = (Button) findViewById(R.id.idAnteprima);
            button.setText(R.string.str_preview);
        }
    }

    private void AlarmPreview() {
        hideSoftKeyboard();
        int indkind = indChoiseSoundType;
        int thePreviewVolume = volumeBar.getProgress();

        if (alarmPlayer==null) {
          alarmPlayer = new AlarmPlayer();
          alarmPlayer.PlayAlarm(this,indLocalRingtone,indkind,thePreviewVolume);
          Button button = (Button) findViewById(R.id.idAnteprima);
          button.setText(R.string.str_Stop);
        }
        else { stopAlarmPreview(); }
    }

    @Override
    public void onDestroy() {
        stopAlarmPreview();
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,startVolumeLevel,0);
        super.onDestroy();
    }

    @Override
    public void onStart() {
        super.onStart();
        mHandler.post(runnableInfoNextAlarm);
    }


    @Override
    public void onStop() {
        super.onStop();
        stopAlarmPreview();
        mHandler.removeCallbacks(runnableInfoNextAlarm);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,startVolumeLevel,0);
    }


    Runnable runnableInfoNextAlarm = new Runnable() {
        @Override
        public void run() { setInfo2Lines(); mHandler.postDelayed(this, 4000); }
    };



}

