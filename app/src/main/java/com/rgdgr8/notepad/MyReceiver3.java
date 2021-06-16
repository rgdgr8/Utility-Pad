package com.rgdgr8.notepad;

import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;

import androidx.core.content.ContextCompat;

public class MyReceiver3 extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        ClipboardManager clipboardManager=(ClipboardManager)context.getSystemService(Context.CLIPBOARD_SERVICE);

        ClipData data=ClipData.newPlainText("","");
        assert clipboardManager != null;
        clipboardManager.setPrimaryClip(data);

        Intent intent1=new Intent(context,MyService.class);
        context.stopService(intent1);
        ContextCompat.startForegroundService(context,intent1);

    }
}
