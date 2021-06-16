package com.rgdgr8.notepad;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

public class RebootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(new Intent(context, BootService.class));
            } else {
                context.startService(new Intent(context, BootService.class));
            }

        } else {
            Log.e("unexpected error", "Received unexpected intent " + intent.toString());
        }
    }
}

