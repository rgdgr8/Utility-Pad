package com.rgdgr8.notepad;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.core.app.NotificationManagerCompat;

public class DoneReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        int id=intent.getIntExtra("id",-1);

        if (id!=-1){
            NotificationManagerCompat notificationManagerCompat=NotificationManagerCompat.from(context);
            notificationManagerCompat.cancel(id);
        } else {
            Toast.makeText(context, "Notification Id not present", Toast.LENGTH_SHORT).show();
            throw new RuntimeException();
        }
    }
}
