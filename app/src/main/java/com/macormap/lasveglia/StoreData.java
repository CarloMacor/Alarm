package com.macormap.lasveglia;

import android.content.Context;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by carlo on 20/09/2017.
 */

public class StoreData {

    private static final String  nomefilesAlert  ="Alerter";
    private static final String  nomefilesWakeup ="Wakerup";

    public StoreData () {}


    public static void readFileAlarms(Context context, List<AlarmObj> alarmObjList ) {
        BufferedReader input = null;
        int maxnumAlarms =0;
        AlarmObj alarmObj = null;
        try {
            String  line;
            input = new BufferedReader(new InputStreamReader(context.openFileInput(nomefilesAlert)));
            if ((line = input.readLine()) != null) {maxnumAlarms = Integer.parseInt(line); }
            for ( int i = 0; i<maxnumAlarms; i++) {
                alarmObj = new AlarmObj(context);
                alarmObjList.add(alarmObj);
                if ((line = input.readLine()) != null) {alarmObj.setName_alarm(line); }
                if ((line = input.readLine()) != null) {alarmObj.setTimeHour(Integer.parseInt(line)); }
                if ((line = input.readLine()) != null) {alarmObj.setTimeMinute(Integer.parseInt(line));  }
                if ((line = input.readLine()) != null) {alarmObj.setState_alarm(Boolean.parseBoolean(line));  }
                for (int j=0; j<7; j++) {
                    if ((line = input.readLine()) != null) {
                        alarmObj.setDay_onoff(j,Boolean.parseBoolean(line));
                    }
                }
                if ((line = input.readLine()) != null) {alarmObj.setIndRingtone(Integer.parseInt(line)); }
                if ((line = input.readLine()) != null) {alarmObj.setIndKindSound(Integer.parseInt(line)); }
                if ((line = input.readLine()) != null) {alarmObj.setVolumeAlarm(Integer.parseInt(line)); }
                if ((line = input.readLine()) != null) {alarmObj.setInRepeat5min(Boolean.parseBoolean(line)); }
            }
        } catch (Exception e) { e.printStackTrace();}
        finally { if (input != null) { try { input.close(); } catch (IOException e) { e.printStackTrace(); } } }

    }


    public static void saveAlarms(Context context, List<AlarmObj> alarmObjList) {
        String eol = System.getProperty("line.separator");
        BufferedWriter writer = null;
        int numAlertsAll = alarmObjList.size();
        AlarmObj alarmObj = null;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(context.openFileOutput(nomefilesAlert,
                    Context.MODE_PRIVATE)));
            writer.write(Integer.toString(numAlertsAll) + eol);
            for ( int i = 0; i<numAlertsAll; i++) {
                alarmObj = alarmObjList.get(i);
                writer.write(alarmObj.getName_alarm() + eol);
                writer.write(Integer.toString(alarmObj.getTimeHour()) + eol);
                writer.write(Integer.toString(alarmObj.getTimeMinute()) + eol);
                writer.write(Boolean.toString(alarmObj.isState_onoff()) + eol);
                for (int j=0; j<7; j++) {
                    writer.write(Boolean.toString(alarmObj.isDay_onoff(j)) + eol);
                }
                writer.write(Integer.toString(alarmObj.getIndRingtone()) + eol);
                writer.write(Integer.toString(alarmObj.getIndKindSound()) + eol);
                writer.write(Integer.toString(alarmObj.getVolumeAlarm()) + eol);
                writer.write(Boolean.toString(alarmObj.isInRepeat5min()) + eol);
            }
        } catch (Exception e) { e.printStackTrace(); }
        finally { if (writer != null) { try { writer.close(); } catch (IOException e) { e.printStackTrace(); } } }

    }


    public static void ReorderListAlarm(List<AlarmObj> alarmObjList ) {
        List<AlarmObj> MomalarmObjList = new ArrayList<>();
        if (alarmObjList.isEmpty()) {return;}
        AlarmObj alarmObj = alarmObjList.get(0);
        AlarmObj alarmObj2;
        MomalarmObjList.add(alarmObj);

        for (int i=1; i<alarmObjList.size(); i++ ) {
            int pos =-1;
            alarmObj = alarmObjList.get(i);
            for (int j=0; j<MomalarmObjList.size(); j++) {
                alarmObj2 = MomalarmObjList.get(j);
                if (alarmObj.minutesFromNow() < alarmObj2.minutesFromNow() ) {
                    pos = j; break;
                }
            }
            if (pos<0) { MomalarmObjList.add(alarmObj); }
            else { MomalarmObjList.add(pos,alarmObj);}
        }

        alarmObjList.clear();
        for (int j=0; j<MomalarmObjList.size(); j++) {
            alarmObj = MomalarmObjList.get(j);
            alarmObjList.add(alarmObj);
        }
        MomalarmObjList.clear();
    }


    public static void savewakerup(Context context, AlarmObj alarmObj) {
        String eol = System.getProperty("line.separator");
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(context.openFileOutput(nomefilesWakeup, Context.MODE_PRIVATE)));
                writer.write(alarmObj.getName_alarm() + eol);
                writer.write(Integer.toString(alarmObj.getTimeHour()) + eol);
                writer.write(Integer.toString(alarmObj.getTimeMinute()) + eol);
                writer.write(Boolean.toString(alarmObj.isState_onoff()) + eol);
                for (int j=0; j<7; j++) { writer.write(Boolean.toString(alarmObj.isDay_onoff(j)) + eol); }
                writer.write(Integer.toString(alarmObj.getIndRingtone()) + eol);
                writer.write(Integer.toString(alarmObj.getIndKindSound()) + eol);
                writer.write(Integer.toString(alarmObj.getVolumeAlarm()) + eol);
                writer.write(Boolean.toString(alarmObj.isInRepeat5min()) + eol);
        } catch (Exception e) { e.printStackTrace(); }
        finally { if (writer != null) { try { writer.close(); } catch (IOException e) { e.printStackTrace(); } } }
    }



    public static void readFileWaker(Context context, AlarmObj alarmObj ) {
        BufferedReader input = null;
        try {
            String  line;
            input = new BufferedReader(new InputStreamReader(context.openFileInput(nomefilesWakeup)));
                if ((line = input.readLine()) != null) {alarmObj.setName_alarm(line); }
                if ((line = input.readLine()) != null) {alarmObj.setTimeHour(Integer.parseInt(line)); }
                if ((line = input.readLine()) != null) {alarmObj.setTimeMinute(Integer.parseInt(line));  }
                if ((line = input.readLine()) != null) {alarmObj.setState_alarm(Boolean.parseBoolean(line));  }
                for (int j=0; j<7; j++) {
                    if ((line = input.readLine()) != null) {
                        alarmObj.setDay_onoff(j,Boolean.parseBoolean(line));
                    }
                }
                if ((line = input.readLine()) != null) {alarmObj.setIndRingtone(Integer.parseInt(line)); }
                if ((line = input.readLine()) != null) {alarmObj.setIndKindSound(Integer.parseInt(line)); }
                if ((line = input.readLine()) != null) {alarmObj.setVolumeAlarm(Integer.parseInt(line)); }
                if ((line = input.readLine()) != null) {alarmObj.setInRepeat5min(Boolean.parseBoolean(line)); }
        } catch (Exception e) { e.printStackTrace();}
        finally { if (input != null) { try { input.close(); } catch (IOException e) { e.printStackTrace(); } } }

    }


}
