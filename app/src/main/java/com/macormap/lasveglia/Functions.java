package com.macormap.lasveglia;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.NotificationCompat;
import android.text.Html;
import android.text.Spanned;
import android.widget.Toast;

import java.util.Calendar;
import java.util.List;

/**
 * Created by carlo on 10/09/2017.
 */

public class Functions {

    public static final int NOTIFICATION_ID = 100;


    public static Spanned getStringColored(String text1,String text2) {
        Spanned result;
        String theHtmlText = "<font color= #888888 >" + text1 + "</font> <font color= #FFFFFF >" + text2 + "</font>";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            result = Html.fromHtml(theHtmlText,Html.FROM_HTML_MODE_LEGACY);
        } else {
            result = Html.fromHtml(theHtmlText);
        }
        return result;
    }


    public static AlarmObj NextAlarmAvailable(List<AlarmObj> alarmObjList) {
        AlarmObj resAlarm = null;
        AlarmObj alarmObj = null;
        for (int i=0; i<alarmObjList.size(); i++ ) {
            alarmObj = alarmObjList.get(i);
            if (!alarmObj.isState_onoff()) {continue; }
            if (!alarmObj.hasDayActive()) {continue;}
            resAlarm = alarmObjList.get(i);
            break;
        }
        return  resAlarm;
    }


    public static String NextDayStr(Context context,  int value) {
        String strmsg = "";
        switch (value) {
            case Calendar.SUNDAY: strmsg   =  context.getResources().getString(R.string.str_Sunday)    +  "  ";  break;
            case Calendar.MONDAY: strmsg   =  context.getResources().getString(R.string.str_Monday)    +  "  ";  break;
            case Calendar.TUESDAY:strmsg   =  context.getResources().getString(R.string.str_Tuesday)   +  "  ";  break;
            case Calendar.WEDNESDAY:strmsg =  context.getResources().getString(R.string.str_Wednesday) +  "  ";  break;
            case Calendar.THURSDAY:strmsg  =  context.getResources().getString(R.string.str_Thursday)  +  "  ";  break;
            case Calendar.FRIDAY:strmsg    =  context.getResources().getString(R.string.str_Friday)    +  "  ";  break;
            case Calendar.SATURDAY:strmsg  =  context.getResources().getString(R.string.str_Saturday ) +  "  ";  break;
        }
        return  strmsg;
    }


    public static void SetIntent(Context context, List<AlarmObj> alarmObjList) {

        boolean toSetAlarmIntent = false;
        AlarmObj alarmObj = null;
        int   minutesToNextWakeUp =0;
        NotificationManager mNotifyMgr = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);

        // remove eventually intent present
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent updateServiceIntent = new Intent(context, WakeupActivity.class);
        PendingIntent pendingUpdateIntent = PendingIntent.getActivity(context.getApplicationContext(), 0, updateServiceIntent, 0);
        try {
            alarmManager.cancel(pendingUpdateIntent);
            mNotifyMgr.cancel(Functions.NOTIFICATION_ID);
        } catch (Exception e) {
            Toast.makeText(context.getApplicationContext(), "Erased Intent", Toast.LENGTH_SHORT).show();
        }

        alarmObj =  Functions.NextAlarmAvailable(alarmObjList);

        if (alarmObj==null) {return;}

        toSetAlarmIntent = true;
        minutesToNextWakeUp = alarmObj.minutesFromNow();

        if (toSetAlarmIntent) {
            AlarmManager alarm = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);
            Intent launchIntent = new Intent(context, WakeupActivity.class);
            PendingIntent intent = PendingIntent.getActivity(context.getApplicationContext(), 0, launchIntent, 0);
            alarm.set(alarm.RTC_WAKEUP, System.currentTimeMillis() - System.currentTimeMillis()%60 + (1000 * minutesToNextWakeUp*60), intent); // *60

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
            String strmsg = Functions.NextDayStr(context,alarmObj.nextDayAvailable);

            mBuilder.setContentTitle(context.getString(R.string.app_name)+"  "+strmsg + " " + alarmObj.getStrTime());
            mBuilder.setSmallIcon(R.mipmap.ic_launcher);

            Intent resultIntent = new Intent(context, MainActivity.class);
            PendingIntent resultPendingIntent = PendingIntent.getActivity(
                    context, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(resultPendingIntent);
            Notification notification = mBuilder.build();
            notification.flags |= Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT;

            mNotifyMgr.notify(Functions.NOTIFICATION_ID, notification);

            StoreData.savewakerup(context,alarmObj);
        }
    }



}
