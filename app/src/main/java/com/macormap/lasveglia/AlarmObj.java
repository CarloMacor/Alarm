package com.macormap.lasveglia;

import android.content.Context;
import android.content.res.Resources;
import android.text.Html;
import android.text.Spanned;

import java.sql.Time;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by carlo on 10/09/2017.
 */

public class AlarmObj {

   private int       timeHour;
   private int       timeMinute;
   private boolean   state_onoff;
   private boolean[] days_activated = new boolean[7];
   private String    name_alarm;
   private int       indSuoneria;
   private int       indtipoSuono;
   private int       volumeAlarm;
   private boolean   inRepeat5min;
   public  int       nextDayAvailable;
   private Context   context;
    // the others .....


   public AlarmObj(Context context) {
       this.context = context;
       state_onoff = true;
       for (int i=0; i<7; i++) {
           days_activated[i] = true;
       }
       name_alarm = "Alarm";
       indSuoneria  = 0;
       indtipoSuono = 0;
       volumeAlarm  = 100;  // max 100 min 10
       inRepeat5min = true;
       nextDayAvailable = -1;
   }

   public void setName_alarm(String str)     { name_alarm  = str; }

   public String getName_alarm()             { return name_alarm; }

   public void setState_alarm(boolean state) { state_onoff = state;}

   public boolean isState_onoff()            { return  state_onoff;}

   public boolean[] getDays_activated()      { return days_activated;}

   public void setDays_activated(boolean[] activated )  {
       for (int i=0; i<activated.length; i++) { days_activated[i] = activated[i];}
   }

   public void setTimeHour(int hour)         { timeHour = hour; }

   public int getTimeHour()                  { return timeHour;}

   public void setTimeMinute(int minute)     { timeMinute = minute; }

   public int getTimeMinute()                { return timeMinute;}

   public String getStrTime()             {
        return String.format("%02d", timeHour) + ":"+ String.format("%02d", timeMinute) ;
    }

   public Spanned getWhenColored() {
        Spanned result;
       String theHtmlText = "";
       String locharDay = "";
       // costruirla con gli stati dei giorni  ......
        for (int i=0; i<7; i++) {
            switch (i) {
                case 0 : locharDay = context.getResources().getString(R.string.day_s_0) +  "  "; break;
                case 1 : locharDay = context.getResources().getString(R.string.day_s_1) +  "  "; break;
                case 2 : locharDay = context.getResources().getString(R.string.day_s_2) +  "  "; break;
                case 3 : locharDay = context.getResources().getString(R.string.day_s_3) +  "  "; break;
                case 4 : locharDay = context.getResources().getString(R.string.day_s_4) +  "  "; break;
                case 5 : locharDay = context.getResources().getString(R.string.day_s_5) +  "  "; break;
                case 6 : locharDay = context.getResources().getString(R.string.day_s_6) +  "  "; break;
            }
            if (days_activated[i]) { theHtmlText = theHtmlText + "<font color= #000000 >" +locharDay + "</font>";  }
                  else             {  theHtmlText = theHtmlText + "<font color= #BBBBBB >" +locharDay + "</font>"; }
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            result = Html.fromHtml(theHtmlText,Html.FROM_HTML_MODE_LEGACY);
        } else {
            result = Html.fromHtml(theHtmlText);
        }
       return result;
   }

   public String getStrState() {
       String res ="Off";
       if (state_onoff) {res ="On";}
       return res;
    }

   public boolean isDay_onoff(int ind)           { return days_activated[ind];}

   public void setDay_onoff(int ind, boolean state) {days_activated[ind] = state;}


   public int getIndRingtone() {return indSuoneria;}

   public void setIndRingtone(int ind) {indSuoneria = ind;}

   public int getIndKindSound() {return indtipoSuono;}

   public void setIndKindSound(int ind) {indtipoSuono= ind;}

   public int getVolumeAlarm() {return volumeAlarm;}

   public void setVolumeAlarm(int vol) {volumeAlarm = vol;}

   public boolean isInRepeat5min() {return inRepeat5min;}

   public void setInRepeat5min(boolean state) {inRepeat5min = state;}

   public int minutesFromNow() {
       nextDayAvailable = -1;
      int res = -1;
       int locres = 0;
       Calendar calendar = Calendar.getInstance();
       int indToday = calendar.get(Calendar.DAY_OF_WEEK);
       int  currentHour   = calendar.get(Calendar.HOUR_OF_DAY);
       int  currentMinute = calendar.get(Calendar.MINUTE);
       int  currentFullMinutes = currentHour*60 + currentMinute;
       int locMoreDays = -1;

       for (int j = indToday; j < (indToday + 7); j++) {
           locMoreDays++;
           int locminuteAlarm = timeHour*60 + timeMinute;
           // start from today and arrive to the next week before today
           // j2 e' il giorno java della settimana in analisi
           int j2 = j;   if (j2 > 7) {j2 = j2 - 7;}
           // sfasamento tra Day_of_week a mio indice
           int myInd = j2-2; if (myInd<0) {myInd = myInd+7;}

           if (isDay_onoff(myInd)) {
               locres = locminuteAlarm - currentFullMinutes;
               locres = locres + locMoreDays*24*60;
               if (locres>0) {
                  if (res>0) { if (res>locres) {res = locres; nextDayAvailable = j2;} }
                         else { res = locres; nextDayAvailable = j2; }
               }
           }
       }

       if (res <0) {res = 8*24*60 + timeHour*60 + timeMinute; } // set on end of list
      return res;
   }

   public boolean hasDayActive() {
       boolean res = false;
       for (int i=0; i<7; i++ ) {
           if (days_activated[i]) { res = true; break; }
       }
       return res;
   }

}
