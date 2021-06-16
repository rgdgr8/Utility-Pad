package com.rgdgr8.notepad;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.Calendar;

public class RemChecker extends BroadcastReceiver {

    RemDatabase remDatabase;
    String dead = "*(DEAD)* ";

    @Override
    public void onReceive(Context context, Intent intent) {

        if (context.getSharedPreferences("rgdgr8",Context.MODE_PRIVATE).getBoolean("main",true)) {
            remDatabase = new RemDatabase(context, "Rems", null, 1);

            reminderToday(context);
            context.startService(new Intent(context, MyService.class));
            remDatabase.close();
            remDatabase = null;
        }

        AlarmManager alarmManager= (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Calendar calendar=Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY,23);
        calendar.set(Calendar.MINUTE,59);
        calendar.set(Calendar.SECOND,59);

        Intent intentx = new Intent(context, RemChecker.class);
        intentx.setPackage(context.getPackageName());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 99999999, intentx, PendingIntent.FLAG_UPDATE_CURRENT);
        assert alarmManager != null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis()+3000, pendingIntent);
        } else {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis()+3000, pendingIntent);
        }
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
            b.set(Calendar.YEAR,remDatabase.getWholeYear().get(i));
            b.set(Calendar.MONTH,remDatabase.getWholeMonth().get(i));
            b.set(Calendar.DAY_OF_MONTH,remDatabase.getWholeDay().get(i));
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
                Log.i("Reif",""+r);
                if ((remDatabase.getWholePeriod().get(i).equals("Minutes") && (b.getTimeInMillis() + remDatabase.getWholeNum().get(i) * 1000 * 60 * r) <= a.getTimeInMillis()) || (remDatabase.getWholePeriod().get(i).equals("Hours") && (b.getTimeInMillis() + remDatabase.getWholeNum().get(i) * 1000 * 60 * 60 * r) <= a.getTimeInMillis()) || (remDatabase.getWholePeriod().get(i).equals("Days") && (b.getTimeInMillis() + remDatabase.getWholeNum().get(i) * 1000 * 60 * 60 * 24 * r) <= a.getTimeInMillis()) || (remDatabase.getWholePeriod().get(i).equals("Weeks") && (b.getTimeInMillis() + remDatabase.getWholeNum().get(i) * 1000 * 60 * 60 * 24 * 7 * r) <= a.getTimeInMillis())) {
                    repeaters++;
                    Log.i("Reii","KKK");
                }
            }
        }

        sharedPreferences.edit().putInt("cop", cop).apply();
        sharedPreferences.edit().putInt("rep", repeaters).apply();
    }
}
