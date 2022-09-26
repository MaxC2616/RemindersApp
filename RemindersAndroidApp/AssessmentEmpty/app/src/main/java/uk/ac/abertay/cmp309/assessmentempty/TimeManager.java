package uk.ac.abertay.cmp309.assessmentempty;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TimeManager extends BroadcastReceiver {

    //CLASS FOR SETTING UP THE NOTIFICATIONS ONCE ALARM IS TRIGGERED
    final static int NOTIFICATION_ID_TIMER = 2;

    @Override
    public void onReceive(Context context, Intent intent) {

        //Getting all the extras from the intent and assigning to values
       Bundle bundle = intent.getExtras();
       String sid = bundle.getString("id");
       String type = bundle.getString("type");
       String text = bundle.getString("info");
       String date = bundle.getString("date");
       String time = bundle.getString("time");
       String location = bundle.getString("location");
       int inotification = bundle.getInt("notification");


       //new instance of notification manager and a Builder
       NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
       NotificationCompat.Builder notifBuilder = new NotificationCompat.Builder(context, "assessmentempty_normal");

       //Set up a new intent. This is the intent that will be called once the notification is clicked. It will take the user to the required record in DeleteRecords
        Intent openintent = new Intent(context, DeleteRecord.class);
        openintent.putExtra("id", sid);
        openintent.putExtra("type", type);
        openintent.putExtra("info", text);
        openintent.putExtra("date", date);
        openintent.putExtra("time", time);
        openintent.putExtra("location", location);
        openintent.putExtra("notification", inotification);
        openintent.setFlags(Intent.FLAG_ACTIVITY_TASK_ON_HOME);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntent(openintent);

        //Pending Intent with the above intent in it
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);


        //Setting values to the Builder
        notifBuilder.setContentTitle("Reminder!");
       notifBuilder.setContentText(text);
       notifBuilder.setContentIntent(resultPendingIntent);
       notifBuilder.setAutoCancel(true);
       notifBuilder.setSmallIcon(android.R.drawable.ic_lock_idle_alarm);

       //Setting up a notification channel depending on the users API level
       if (Build.VERSION.SDK_INT >= 26) {
           String channelid = "channelid";
           NotificationChannel channel = new NotificationChannel(channelid, "assessmentempty_normal", NotificationManager.IMPORTANCE_HIGH);
           channel.enableVibration(true);
           notificationManager.createNotificationChannel(channel);
       }

        //Final setup and building of the notification
        Notification notification = notifBuilder.build();
        notificationManager.notify(NOTIFICATION_ID_TIMER, notification);

    }
}

