package com.rgdgr8.notepad;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class ServiceCallReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        String number = intent.getStringExtra("content");
        Uri call = Uri.parse("tel:" + number);
        Intent surf = new Intent(Intent.ACTION_DIAL, call);
        surf.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(surf);

        Intent it = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        context.sendBroadcast(it);
    }
}
