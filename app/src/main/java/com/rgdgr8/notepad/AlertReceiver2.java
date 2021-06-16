package com.rgdgr8.notepad;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.preference.PreferenceManager;

public class AlertReceiver2 extends BroadcastReceiver {

    private static final String delay2 = "rgdgr8.com.delay.delay.reminder";

    @Override
    public void onReceive(Context context, Intent intent) {
        String content = intent.getStringExtra("content");
        int _id = intent.getIntExtra("id", -1);
        String date = intent.getStringExtra("date");
        String time = intent.getStringExtra("time");
        String period = intent.getStringExtra("period");
        int num = intent.getIntExtra("num", 0);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String ring = preferences.getString(SettingsActivity.ring, context.getResources().getString(R.string.defaultRing));

        Intent intent4 = new Intent(context, NewTaskActivity.class);
        intent4.putExtra("notiContent", content);
        intent4.putExtra("date", date);
        intent4.putExtra("time", time);
        intent4.putExtra("period", period);
        intent4.putExtra("num", num);
        intent4.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent4 = PendingIntent.getActivity(context, _id, intent4, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent intent3 = new Intent(context, DelayReceiver2.class);
        intent3.setAction(delay2);
        intent3.setPackage(context.getPackageName());
        intent3.putExtra("id", _id);
        intent3.putExtra("num", num);
        intent3.putExtra("content", content);
        intent3.putExtra("date", date);
        intent3.putExtra("time", time);
        intent3.putExtra("period", period);
        PendingIntent pendingIntent3 = PendingIntent.getBroadcast(context, _id, intent3, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent intent5 = new Intent(context, DoneReceiver2.class);
        intent5.setPackage(context.getPackageName());
        intent5.putExtra("id", _id);
        PendingIntent pendingIntent5 = PendingIntent.getBroadcast(context, _id, intent5, PendingIntent.FLAG_UPDATE_CURRENT);

        Uri sound;

        switch (ring) {
            case "0":
                sound = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.sound_of_silence);
                break;

            case "1":
                sound = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.reminderspoken);
                break;

            case "2":
                sound = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.remindervibrate);
                break;

            case "3":
                sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
                break;

            case "4":
                sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                break;

            default:
                throw new IllegalStateException("Unexpected value: " + ring);
        }

        NotificationCompat.Builder notification2 = new NotificationCompat.Builder(context, App.channelID3);
        notification2.setSmallIcon(R.drawable.ic_alarm_black_24dp);
        notification2.setContentTitle("Utility Pad Reminder");
        notification2.setContentText(content);
        notification2.setStyle(new NotificationCompat.BigTextStyle().bigText(content));
        notification2.setColor(context.getResources().getColor(R.color.yellowish_white));
        notification2.setVibrate(new long[]{0, 3000, 1000, 2000, 1000, 2000, 1000, 2000});
        notification2.setSound(sound);
        notification2.setPriority(NotificationCompat.PRIORITY_HIGH);
        notification2.setContentIntent(pendingIntent4);
        notification2.setOngoing(true);
        notification2.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
        notification2.addAction(R.drawable.ic_alarm_on_black_24dp, "Delay", pendingIntent3);
        notification2.addAction(R.drawable.ic_done_black_24dp, "Done", pendingIntent5);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(_id, notification2.build());
    }
}
