package com.rgdgr8.notepad;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.Calendar;

public class BootService extends IntentService {

    private PowerManager.WakeLock wakeLock;
    RemDatabase remDatabase;
    SharedPreferences sharedPreferences;
    int last = 0;

    public BootService() {
        super("name");
        setIntentRedelivery(true);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Log.i("createdIntentService", "KKKKKKKKKKKKKKKKKKKKKKKKKKKK");
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        assert powerManager != null;
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                getResources().getString(R.string.app_name) + " :Wakelock");
        wakeLock.acquire(3 * 60 * 1000L);

        remDatabase = new RemDatabase(getApplicationContext(), "Rems", null, 1);
        sharedPreferences = getSharedPreferences("rgdgr8", MODE_PRIVATE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification notification = new NotificationCompat.Builder(this, App.channelID2)
                    .setContentTitle(getResources().getString(R.string.app_name) + " service")
                    .setContentText("Restarting Reminders...")
                    .setSmallIcon(R.drawable.ic_content_copy_white_24dp)
                    .setColor(getResources().getColor(R.color.org_colorAccent))
                    .build();
            startForeground(10000000, notification);
        }
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        int count = 0;
        Toast.makeText(this, "Resetting Reminders from " + getResources().getString(R.string.app_name), Toast.LENGTH_LONG).show();

        Log.i("handleIntentService", "KKKKKKKKKKKKKKKKKKKKKKKKKKKK");

        for (int i = 0; i < remDatabase.getWholeFav().size(); i++) {

            if (!remDatabase.getWholeFav().get(i).equals("")) {

                int pos = (1 + i) + 90000;
                int idd = i + 1;
                last = sharedPreferences.getInt("last" + pos, 0);
                int r = sharedPreferences.getInt("r" + idd, 0);

                Calendar c = Calendar.getInstance();
                c.set(Calendar.YEAR, remDatabase.getWholeYear().get(i));
                c.set(Calendar.MONTH, remDatabase.getWholeMonth().get(i));
                c.set(Calendar.DAY_OF_MONTH, remDatabase.getWholeDay().get(i));
                c.set(Calendar.HOUR_OF_DAY, remDatabase.getWholeHour().get(i));
                c.set(Calendar.MINUTE, remDatabase.getWholeMin().get(i));
                c.set(Calendar.SECOND, 0);

                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                Intent intenta = new Intent(this, AlertReceiver.class);
                intenta.setPackage(getPackageName());
                intenta.putExtra("content", remDatabase.getWholeFav().get(i));
                intenta.putExtra("id", i + 1);
                intenta.putExtra("date", remDatabase.getWholeDate().get(i));
                intenta.putExtra("time", remDatabase.getWholeTime().get(i));
                intenta.putExtra("period", remDatabase.getWholePeriod().get(i));
                intenta.putExtra("num", remDatabase.getWholeNum().get(i));
                PendingIntent pendingIntent = PendingIntent.getBroadcast(this, i + 1, intenta, PendingIntent.FLAG_UPDATE_CURRENT);

                Intent intentb = new Intent(this, AlertReceiver2.class);
                intentb.setPackage(getPackageName());
                intentb.putExtra("content", remDatabase.getWholeFav().get(i));
                intentb.putExtra("id", pos);
                intentb.putExtra("date", remDatabase.getWholeDate().get(i));
                intentb.putExtra("time", remDatabase.getWholeTime().get(i));
                intentb.putExtra("period", remDatabase.getWholePeriod().get(i));
                intentb.putExtra("num", remDatabase.getWholeNum().get(i));
                PendingIntent pendingIntentb = PendingIntent.getBroadcast(this, pos, intentb, PendingIntent.FLAG_UPDATE_CURRENT);
                assert alarmManager != null;

                String period = remDatabase.getWholePeriod().get(i);
                int num = remDatabase.getWholeNum().get(i);

                if (!c.before(Calendar.getInstance())) {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
                    } else {
                        alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
                    }
                } else if (remDatabase.getWholeNum().get(i) != 0 && !remDatabase.getWholePeriod().get(i).equals("")) {

                    switch (period) {
                        case "Minutes":
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, c.getTimeInMillis() + (1000 * 60 * num * r), pendingIntent);
                            } else {
                                alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis() + (1000 * 60 * num * r), pendingIntent);
                            }
                            break;
                        case "Hours":
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, c.getTimeInMillis() + (1000 * 60 * 60 * num * r), pendingIntent);
                            } else {
                                alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis() + (1000 * 60 * 60 * num * r), pendingIntent);
                            }
                            break;
                        case "Days":
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, c.getTimeInMillis() + (1000 * 60 * 60 * 24 * num * r), pendingIntent);
                            } else {
                                alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis() + (1000 * 60 * 60 * 24 * num * r), pendingIntent);
                            }
                            break;
                        case "Weeks":
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, c.getTimeInMillis() + (1000 * 60 * 60 * 24 * num * 7 * r), pendingIntent);
                            } else {
                                alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis() + (1000 * 60 * 60 * 24 * num * 7 * r), pendingIntent);
                            }
                            break;
                    }

                    if (last > 0) {

                        Log.i("last>0", "kkkkkk");
                        Log.i("last>0", String.valueOf(c.getTimeInMillis() + last));
                        Log.i("last>0", String.valueOf(Calendar.getInstance()));


                        if (Calendar.getInstance().getTimeInMillis() < (c.getTimeInMillis() + last)) {

                            Log.i("next", "kkkkkk");

                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, c.getTimeInMillis() + last, pendingIntentb);
                            } else {
                                alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis() + last, pendingIntentb);
                            }
                        }
                    }

                } else {

                    if (last > 0) {

                        Log.i("last>0", "kkkkkk");
                        Log.i("last>0", String.valueOf(c.getTimeInMillis() + last));
                        Log.i("last>0", String.valueOf(Calendar.getInstance()));

                        if (Calendar.getInstance().getTimeInMillis() < (c.getTimeInMillis() + last)) {

                            Log.i("next", "kkkkkk");

                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, c.getTimeInMillis() + last, pendingIntentb);
                            } else {
                                alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis() + last, pendingIntentb);
                            }
                        }
                    } else if (!remDatabase.getWholeFav().get(i).startsWith("*(DEAD)* ")) {

                        count++;

                        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

                        Intent intent4 = new Intent(this, NewTaskActivity.class);
                        intent4.putExtra("notiContent", remDatabase.getWholeFav().get(i));
                        intent4.putExtra("date", remDatabase.getWholeDate().get(i));
                        intent4.putExtra("time", remDatabase.getWholeTime().get(i));
                        intent4.putExtra("period", period);
                        intent4.putExtra("num", num);
                        intent4.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        PendingIntent pendingIntent4 = PendingIntent.getActivity(this, i + 1, intent4, PendingIntent.FLAG_UPDATE_CURRENT);

                        NotificationCompat.Builder notification2 = new NotificationCompat.Builder(this, App.channelID3);
                        notification2.setSmallIcon(R.drawable.ic_alarm_black_24dp);
                        notification2.setContentTitle("Missed Reminder " + count);
                        notification2.setContentText(remDatabase.getWholeFav().get(i));
                        notification2.setStyle(new NotificationCompat.BigTextStyle().bigText(remDatabase.getWholeFav().get(i)));
                        notification2.setColor(getResources().getColor(R.color.lighter_red_for_pink));
                        notification2.setVibrate(new long[]{0, 2000, 1000, 2000, 1000, 2000, 1000, 2000});
                        notification2.setSound(sound);
                        notification2.setPriority(NotificationCompat.PRIORITY_HIGH);
                        notification2.setContentIntent(pendingIntent4);
                        notification2.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

                        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
                        notificationManagerCompat.notify(i + 1, notification2.build());
                    }
                }
            }
        }
    }

    @Override
    public void onDestroy() {

        remDatabase.close();
        remDatabase = null;

        Log.i("destroyedIntentService", "KKKKKKKKKKKKKKKKKKKKKKKKKKKK");

        super.onDestroy();
    }
}
