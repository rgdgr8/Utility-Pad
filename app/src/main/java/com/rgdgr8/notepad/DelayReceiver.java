package com.rgdgr8.notepad;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class DelayReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        int id=intent.getIntExtra("id",-1);
        int num=intent.getIntExtra("num",0);
        String content=intent.getStringExtra("content");
        String date=intent.getStringExtra("date");
        String time=intent.getStringExtra("time");
        String period=intent.getStringExtra("period");

        Intent i = new Intent(context,DelayActivity.class);
        i.putExtra("id",id);
        i.putExtra("content",content);
        i.putExtra("date",date);
        i.putExtra("time",time);
        i.putExtra("period",period);
        i.putExtra("num",num);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(i);

    }
}
