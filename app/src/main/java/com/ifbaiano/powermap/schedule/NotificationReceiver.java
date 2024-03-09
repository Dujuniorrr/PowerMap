package com.ifbaiano.powermap.schedule;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String title = intent.getStringExtra("title");
        String content = intent.getStringExtra("content");
        new NotificationSchedule(intent.getIntExtra("id", -1)).showNotification(context, title, content);
    }
}
