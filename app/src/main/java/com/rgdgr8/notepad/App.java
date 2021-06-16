package com.rgdgr8.notepad;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

public class App extends Application {

    static String channelID2="channel2";
    static String channelID3="channel3";

    @Override
    public void onCreate() {
        super.onCreate();

        createChannel();

    }

    public void createChannel(){
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){

            NotificationChannel channel2=new NotificationChannel(
                    channelID2,
                    "Service Notification Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            channel2.setDescription("Channel for Foreground service Notification");

            NotificationChannel channel3=new NotificationChannel(
                    channelID3,
                    "Reminder Notification Channel",
                    NotificationManager.IMPORTANCE_HIGH
            );

            channel3.setDescription("Channel for Reminder Notifications");

            Uri sound = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.sound_of_silence);

            AudioAttributes att = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();

            channel3.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            channel3.setVibrationPattern(new long[]{0,3000,1000,2000,1000,2000,1000,2000});
            channel3.enableVibration(true);
            channel3.setSound(sound, att);
            channel3.setLightColor(R.color.blue);

            NotificationManager notificationManager= (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            assert notificationManager != null;

            notificationManager.createNotificationChannel(channel2);
            notificationManager.createNotificationChannel(channel3);
        }
    }
}
