package com.rgdgr8.notepad;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.Calendar;

public class SettingsActivity extends AppCompatActivity {

    static String font = "font";
    static String size = "size";
    static String pop = "pop";
    static String swoosh = "swoosh";
    static String mode = "mode";
    static String add = "add";
    static String background = "background";
    static String clicked = "click";
    static String ring = "rings";

    SharedPreferences sharedPreferences;

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        Toolbar toolbar = findViewById(R.id.tb);
        toolbar.setTitle("Settings");
        toolbar.setBackgroundColor(getResources().getColor(R.color.blue));
        getWindow().setStatusBarColor(getResources().getColor(R.color.blue));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.frameLayout, new Settings()).commit();
        }

        sharedPreferences=getSharedPreferences("rgdgr8",MODE_PRIVATE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = new Intent(getApplicationContext(), MyService.class);
        stopService(intent);
        getSharedPreferences("rgdgr8",MODE_PRIVATE).edit().putBoolean("main",false).apply();
    }

    String dead = "*(DEAD)* ";

    @Override
    protected void onPause() {

        if (MainActivity.background) {
            reminderToday();
            Intent intentSer = new Intent(getApplicationContext(), MyService.class);
            startService(intentSer);
        }

        super.onPause();
    }

    private void reminderToday() {

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
            b.set(Calendar.YEAR,MainActivity.remDb.getWholeYear().get(i));
            b.set(Calendar.MONTH,MainActivity.remDb.getWholeMonth().get(i));
            b.set(Calendar.DAY_OF_MONTH,MainActivity.remDb.getWholeDay().get(i));
            b.set(Calendar.HOUR_OF_DAY, MainActivity.remDb.getWholeHour().get(i));
            b.set(Calendar.MINUTE, MainActivity.remDb.getWholeMin().get(i));
            b.set(Calendar.SECOND, 0);

            if ((MainActivity.remDb.getWholeDay().get(i) == dayop && MainActivity.remDb.getWholeMonth().get(i) == monthop && MainActivity.remDb.getWholeYear().get(i) == yearop && !MainActivity.remDb.getWholeFav().get(i).startsWith(dead))) {

                if ((!MainActivity.remDb.getWholePeriod().get(i).equals("") && MainActivity.remDb.getWholeNum().get(i) != 0)) {

                    if (Calendar.getInstance().before(b) || (MainActivity.remDb.getWholePeriod().get(i).equals("Minutes") && (b.getTimeInMillis() + MainActivity.remDb.getWholeNum().get(i) * 1000 * 60 * r) <= a.getTimeInMillis()) || (MainActivity.remDb.getWholePeriod().get(i).equals("Hours") && (b.getTimeInMillis() + MainActivity.remDb.getWholeNum().get(i) * 1000 * 60 * 60 * r) <= a.getTimeInMillis())) {
                        repeaters++;
                    }

                } else {
                    cop++;
                }
            } else if (r > 0) {
                Log.i("TAG", ""+r);
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
