package com.macormap.lasveglia;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.NotificationCompat;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.ALARM_SERVICE;

/**
 * Created by carlo on 13/09/2017.
 */

public class StartAtBoot extends BroadcastReceiver {

    private List<AlarmObj> alarmObjList = new ArrayList<>();


    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            // settare solo l'alarm e poi chiudere app

            StoreData.readFileAlarms(context,alarmObjList);

            AlarmObj alarmObj =  Functions.NextAlarmAvailable(alarmObjList);

            if (alarmObj!=null) {


                int minutesToNextWakeUp = alarmObj.minutesFromNow();
                AlarmManager alarm = (AlarmManager) context.getSystemService(ALARM_SERVICE);
                Intent launchIntent = new Intent(context, WakeupActivity.class);
                PendingIntent theIntent = PendingIntent.getActivity(context.getApplicationContext(), 0, launchIntent, 0);
                alarm.set(alarm.RTC_WAKEUP, System.currentTimeMillis() - System.currentTimeMillis()%60 + (1000 * minutesToNextWakeUp*60), theIntent); // *60


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

                NotificationManager mNotifyMgr = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
                mNotifyMgr.notify(Functions.NOTIFICATION_ID, notification);

            }

        }

        alarmObjList.clear();
        alarmObjList = null;
    }




}
