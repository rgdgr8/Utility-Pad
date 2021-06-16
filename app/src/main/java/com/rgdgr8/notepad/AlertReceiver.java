package com.rgdgr8.notepad;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.preference.PreferenceManager;

import java.util.Calendar;

public class AlertReceiver extends BroadcastReceiver {

    RemDatabase remDatabase;
    String dead = "*(DEAD)* ";
    int _id;

    private static final String delay = "rgdgr8.com.delay";

    @Override
    public void onReceive(Context context, Intent intent) {

        remDatabase = new RemDatabase(context, "Rems", null, 1);

        String content = intent.getStringExtra("content");
        _id = intent.getIntExtra("id", -1);
        String date = intent.getStringExtra("date");
        String time = intent.getStringExtra("time");
        String period = intent.getStringExtra("period");
        int num = intent.getIntExtra("num", 0);
        String[] strings = new String[]{"Minutes", "Hours", "Days", "Weeks"};

        Log.i("alertREceiver_before_if","id: "+ _id);
        Log.i("alertREceiver_before_if","content: "+ content);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String ring = preferences.getString(SettingsActivity.ring, context.getResources().getString(R.string.defaultRing));

        assert period != null;
        if (!period.equals("") && num != 0) {

            int r = context.getSharedPreferences("rgdgr8", Context.MODE_PRIVATE).getInt("r" + _id, 0);
            r++;
            context.getSharedPreferences("rgdgr8", Context.MODE_PRIVATE).edit().putInt("r" + _id, r).apply();

            Calendar c = Calendar.getInstance();
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent intenta = new Intent(context, AlertReceiver.class);
            intenta.setPackage(context.getPackageName());
            intenta.putExtra("content", content);
            intenta.putExtra("id", _id);
            intenta.putExtra("date", date);
            intenta.putExtra("time", time);
            intenta.putExtra("period", period);
            intenta.putExtra("num", num);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, _id, intenta, PendingIntent.FLAG_UPDATE_CURRENT);
            assert alarmManager != null;
            if (period.equals(strings[0])) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, c.getTimeInMillis() + (1000 * 60 * num), pendingIntent);
                } else {
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis() + (1000 * 60 * num), pendingIntent);
                }
            } else if (period.equals(strings[1])) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, c.getTimeInMillis() + (1000 * 60 * 60 * num), pendingIntent);
                } else {
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis() + (1000 * 60 * 60 * num), pendingIntent);
                }
            } else if (period.equals(strings[2])) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, c.getTimeInMillis() + (1000 * 60 * 60 * 24 * num), pendingIntent);
                } else {
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis() + (1000 * 60 * 60 * 24 * num), pendingIntent);
                }
            } else if (period.equals(strings[3])) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, c.getTimeInMillis() + (1000 * 60 * 60 * 24 * 7 * num), pendingIntent);
                } else {
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis() + (1000 * 60 * 60 * 24 * 7 * num), pendingIntent);
                }
            }
        }

        Intent intent4 = new Intent(context, NewTaskActivity.class);
        intent4.putExtra("notiContent", content);
        intent4.putExtra("date", date);
        intent4.putExtra("time", time);
        intent4.putExtra("period", period);
        intent4.putExtra("num", num);
        intent4.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent4 = PendingIntent.getActivity(context, _id, intent4, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent intent3 = new Intent(context, DelayReceiver.class);
        intent3.setAction(delay);
        intent3.setPackage(context.getPackageName());
        intent3.putExtra("id", _id);
        intent3.putExtra("num", num);
        intent3.putExtra("content", content);
        intent3.putExtra("date", date);
        intent3.putExtra("time", time);
        intent3.putExtra("period", period);
        PendingIntent pendingIntent3 = PendingIntent.getBroadcast(context, _id, intent3, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent intent5 = new Intent(context, DoneReceiver.class);
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
        notification2.setColor(context.getResources().getColor(R.color.org_colorAccent));
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

        deader();
        reminderToday(context);
        context.startService(new Intent(context, MyService.class));

        remDatabase.close();
        remDatabase = null;
    }

    private void reminderToday(Context context) {

        SharedPreferences sharedPreferences = context.getSharedPreferences("rgdgr8", Context.MODE_PRIVATE);
        Calendar c = Calendar.getInstance();

        int cop = 0;
        int repeaters = 0;
        int dayop = c.get(Calendar.DAY_OF_MONTH);
        int monthop = c.get(Calendar.MONTH);
        int yearop = c.get(Calendar.YEAR);

        for (int i = 0; i < remDatabase.getWholeFav().size(); i++) {

            int id = i + 1;
            int r = sharedPreferences.getInt("r" + id, 0);

            Calendar a = Calendar.getInstance();
            a.set(Calendar.HOUR_OF_DAY, 23);
            a.set(Calendar.MINUTE, 59);
            a.set(Calendar.SECOND, 57);

            Calendar b = Calendar.getInstance();
            b.set(Calendar.YEAR, remDatabase.getWholeYear().get(i));
            b.set(Calendar.MONTH, remDatabase.getWholeMonth().get(i));
            b.set(Calendar.DAY_OF_MONTH, remDatabase.getWholeDay().get(i));
            b.set(Calendar.HOUR_OF_DAY, remDatabase.getWholeHour().get(i));
            b.set(Calendar.MINUTE, remDatabase.getWholeMin().get(i));
            b.set(Calendar.SECOND, 0);

            if ((remDatabase.getWholeDay().get(i) == dayop && remDatabase.getWholeMonth().get(i) == monthop && remDatabase.getWholeYear().get(i) == yearop && !remDatabase.getWholeFav().get(i).startsWith(dead))) {

                if ((!remDatabase.getWholePeriod().get(i).equals("") && remDatabase.getWholeNum().get(i) != 0)) {

                    if (Calendar.getInstance().before(b) || (remDatabase.getWholePeriod().get(i).equals("Minutes") && (b.getTimeInMillis() + remDatabase.getWholeNum().get(i) * 1000 * 60 * r) <= a.getTimeInMillis()) || (remDatabase.getWholePeriod().get(i).equals("Hours") && (b.getTimeInMillis() + remDatabase.getWholeNum().get(i) * 1000 * 60 * 60 * r) <= a.getTimeInMillis())) {
                        repeaters++;
                    }

                } else {
                    cop++;
                }
            } else if (r > 0) {
                Log.i("aReif", "" + r);
                if ((remDatabase.getWholePeriod().get(i).equals("Minutes") && (b.getTimeInMillis() + remDatabase.getWholeNum().get(i) * 1000 * 60 * r) <= a.getTimeInMillis()) || (remDatabase.getWholePeriod().get(i).equals("Hours") && (b.getTimeInMillis() + remDatabase.getWholeNum().get(i) * 1000 * 60 * 60 * r) <= a.getTimeInMillis()) || (remDatabase.getWholePeriod().get(i).equals("Days") && (b.getTimeInMillis() + remDatabase.getWholeNum().get(i) * 1000 * 60 * 60 * 24 * r) <= a.getTimeInMillis()) || (remDatabase.getWholePeriod().get(i).equals("Weeks") && (b.getTimeInMillis() + remDatabase.getWholeNum().get(i) * 1000 * 60 * 60 * 24 * 7 * r) <= a.getTimeInMillis())) {
                    repeaters++;
                    Log.i("aReii", "KKK");
                }
            }
        }

        sharedPreferences.edit().putInt("cop", cop).apply();
        sharedPreferences.edit().putInt("rep", repeaters).apply();
    }

    private void deader() {

        if (remDatabase.getWholeFav() != null) {
            MainActivity.reminders = remDatabase.getWholeFav();

            int i = _id - 1;

            try {
                if (remDatabase.getWholePeriod().get(i).equals("") && remDatabase.getWholeNum().get(i) == 0 && !remDatabase.getWholeFav().get(i).equals("")) {
                    if (!MainActivity.reminders.get(i).startsWith(dead)) {
                        MainActivity.reminders.set(i, dead + MainActivity.reminders.get(i));
                        remDatabase.UpdateFav();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
