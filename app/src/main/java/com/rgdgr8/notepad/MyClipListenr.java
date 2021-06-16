package com.rgdgr8.notepad;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Collections;

public class MyClipListenr implements ClipboardManager.OnPrimaryClipChangedListener {

    Context context;
    ClipboardManager clipboardManager;

    public MyClipListenr(Context context) {
        this.context = context;
        clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
    }

    @Override
    public void onPrimaryClipChanged() {

        Log.i("onClipChange", String.valueOf(MainActivity.background));

        if (MainActivity.background) {
            if (clipboardManager.hasPrimaryClip() && !clipboardManager.getPrimaryClip().getItemAt(0).coerceToText(context).toString().equals("")) {
                ClipData.Item data = clipboardManager.getPrimaryClip().getItemAt(0);
                String text = data.coerceToText(context).toString();
                text = text.replace("'", "’");

                if (!MainActivity.copies.equals(MainActivity.db.getWholeData())) {
                    MainActivity.copies.clear();
                    MainActivity.copies.addAll(MainActivity.db.getWholeData());
                }

                if (!MainActivity.copies.contains(text)) {
                    if (MainActivity.orderReverser % 2 == 0) {
                        MainActivity.copies.add(text);
                    } else {
                        Collections.reverse(MainActivity.copies);
                        MainActivity.copies.add(text);
                        Collections.reverse(MainActivity.copies);
                    }
                    MainActivity.db.deleteOrUpdateData();

                    Toast.makeText(context, "Saved to Utility Pad MAIN!", Toast.LENGTH_SHORT).show();

                    if (MainActivity.mA != null) {
                        MainActivity.mA.notifyDataSetChanged();
                    }

                    if (MainActivity.background) {
                        reminderToday();
                        Intent intentSer = new Intent(context, MyService.class);
                        context.startService(intentSer);
                    }
                }
            }
        } else {
            Log.i("onClipChangeclass", String.valueOf(false));
        }
    }

    private void reminderToday() {

        SharedPreferences sharedPreferences = context.getSharedPreferences("rgdgr8", Context.MODE_PRIVATE);
        Calendar c = Calendar.getInstance();

        int cop = 0;
        int repeaters = 0;
        int dayop = c.get(Calendar.DAY_OF_MONTH);
        int monthop = c.get(Calendar.MONTH);
        int yearop = c.get(Calendar.YEAR);

        for (int i = 0; i < MainActivity.remDb.getWholeFav().size(); i++) {

            int id = i + 1;
            int r = sharedPreferences.getInt("r" + id, 0);

            Calendar a = Calendar.getInstance();
            a.set(Calendar.HOUR_OF_DAY, 23);
            a.set(Calendar.MINUTE, 59);
            a.set(Calendar.SECOND, 57);

            Calendar b = Calendar.getInstance();
            b.set(Calendar.YEAR, MainActivity.remDb.getWholeYear().get(i));
            b.set(Calendar.MONTH, MainActivity.remDb.getWholeMonth().get(i));
            b.set(Calendar.DAY_OF_MONTH, MainActivity.remDb.getWholeDay().get(i));
            b.set(Calendar.HOUR_OF_DAY, MainActivity.remDb.getWholeHour().get(i));
            b.set(Calendar.MINUTE, MainActivity.remDb.getWholeMin().get(i));
            b.set(Calendar.SECOND, 0);

            if ((MainActivity.remDb.getWholeDay().get(i) == dayop && MainActivity.remDb.getWholeMonth().get(i) == monthop && MainActivity.remDb.getWholeYear().get(i) == yearop && !MainActivity.remDb.getWholeFav().get(i).startsWith(MainActivity.dead))) {

                if ((!MainActivity.remDb.getWholePeriod().get(i).equals("") && MainActivity.remDb.getWholeNum().get(i) != 0)) {

                    if (Calendar.getInstance().before(b) || (MainActivity.remDb.getWholePeriod().get(i).equals("Minutes") && (b.getTimeInMillis() + MainActivity.remDb.getWholeNum().get(i) * 1000 * 60 * r) <= a.getTimeInMillis()) || (MainActivity.remDb.getWholePeriod().get(i).equals("Hours") && (b.getTimeInMillis() + MainActivity.remDb.getWholeNum().get(i) * 1000 * 60 * 60 * r) <= a.getTimeInMillis())) {
                        repeaters++;
                    }

                } else {
                    cop++;
                }
            } else if (r > 0) {
                Log.i("TAG", "" + r);
                if ((MainActivity.remDb.getWholePeriod().get(i).equals("Minutes") && (b.getTimeInMillis() + MainActivity.remDb.getWholeNum().get(i) * 1000 * 60 * r) <= a.getTimeInMillis()) || (MainActivity.remDb.getWholePeriod().get(i).equals("Hours") && (b.getTimeInMillis() + MainActivity.remDb.getWholeNum().get(i) * 1000 * 60 * 60 * r) <= a.getTimeInMillis()) || (MainActivity.remDb.getWholePeriod().get(i).equals("Days") && (b.getTimeInMillis() + MainActivity.remDb.getWholeNum().get(i) * 1000 * 60 * 60 * 24 * r) <= a.getTimeInMillis()) || (MainActivity.remDb.getWholePeriod().get(i).equals("Weeks") && (b.getTimeInMillis() + MainActivity.remDb.getWholeNum().get(i) * 1000 * 60 * 60 * 24 * 7 * r) <= a.getTimeInMillis())) {
                    repeaters++;
                    Log.i("TAGi", "kkkkk");
                }
            }
        }

        sharedPreferences.edit().putInt("cop", cop).apply();
        sharedPreferences.edit().putInt("rep", repeaters).apply();
    }
}
