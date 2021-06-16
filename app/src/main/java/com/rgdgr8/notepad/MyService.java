package com.rgdgr8.notepad;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;

public class MyService extends Service {

    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {

        int pos = 9000000;
        ClipboardManager clipManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        assert clipManager != null;

        //clipManager.addPrimaryClipChangedListener(MainActivity.mOnPrimaryClipChangedListener);

        String content;
        if (clipManager.hasPrimaryClip()) {
            content = clipManager.getPrimaryClip().getItemAt(0).coerceToText(this).toString();
        } else {
            content = "<EMPTY>";
        }

        Notification notification = MainActivity.Serviceotification(this, content);

        startForeground(pos, notification);

        return START_REDELIVER_INTENT;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) { //TODO: Called everytime task removed from recent apps, not on stopping service!!!! On stopping service, ondestroy is called.
        Intent restartServiceTask = new Intent(getApplicationContext(), this.getClass());
        restartServiceTask.setPackage(getPackageName());
        PendingIntent restartPendingIntent = PendingIntent.getService(getApplicationContext(), 1, restartServiceTask, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager myAlarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        assert myAlarmService != null;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            myAlarmService.setExact(
                    AlarmManager.ELAPSED_REALTIME,
                    SystemClock.elapsedRealtime() + 1000,
                    restartPendingIntent);
        } else {
            myAlarmService.setExactAndAllowWhileIdle(
                    AlarmManager.ELAPSED_REALTIME,
                    SystemClock.elapsedRealtime() + 1000,
                    restartPendingIntent);
        }

        super.onTaskRemoved(rootIntent);
    }
}
