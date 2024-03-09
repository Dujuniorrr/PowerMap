package com.ifbaiano.powermap.schedule;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.ifbaiano.powermap.R;
import com.ifbaiano.powermap.activity.MainActivity;

import java.util.Calendar;

public class NotificationSchedule {

    private static final int NOTIFICATION_ID = 1;
    private static final String CHANNEL_ID = "my_channel";

    public static void scheduleRepeatingNotification(Context context, int dayOfWeek, int hourOfDay, int minute, long repeatInterval, String title, String content) {

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager == null) {
            return;
        }

        Intent notificationIntent = new Intent(context, NotificationReceiver.class);
        notificationIntent.putExtra("title", title);
        notificationIntent.putExtra("content", content);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, NOTIFICATION_ID, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        calendar.set(Calendar.DAY_OF_WEEK, dayOfWeek);

        if (calendar.before(Calendar.getInstance())) {
            if (repeatInterval == AlarmManager.INTERVAL_DAY) {
                while (calendar.before(Calendar.getInstance())) {
                    calendar.add(Calendar.DAY_OF_YEAR, 1);
                }
            } else if (repeatInterval == AlarmManager.INTERVAL_DAY * 7) {
                calendar.add(Calendar.WEEK_OF_YEAR, 1);
            } else if (repeatInterval == AlarmManager.INTERVAL_DAY * 30) {
                calendar.add(Calendar.MONTH, 1);
            } else if (repeatInterval == AlarmManager.INTERVAL_DAY * 365) {
                calendar.add(Calendar.YEAR, 1);
            }
        }

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), repeatInterval, pendingIntent);
    }


    public static void showNotification(Context context, String title, String content) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager == null)
            return;

        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Channel Name", NotificationManager.IMPORTANCE_DEFAULT);
        notificationManager.createNotificationChannel(channel);

        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(content)
                .setSmallIcon(R.drawable.img_byddolphin01)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        Notification notification = builder.build();
        notificationManager.notify(NOTIFICATION_ID, notification);
    }
}
