package com.rgdgr8.notepad;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.Calendar;


public class DelayActivity extends AppCompatActivity {

    int last = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.delay);

        setTitle("Delay Reminder By");
        int id = getIntent().getIntExtra("id", -1);
        int num = getIntent().getIntExtra("num", 0);
        String content = getIntent().getStringExtra("content");
        String date = getIntent().getStringExtra("date");
        String time = getIntent().getStringExtra("time");
        String period = getIntent().getStringExtra("period");

        int pos;

        if (id<=90000) {
             pos = id + 90000;
        }else {
            pos=id;
        }

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.cancel(id);

        Intent it = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        sendBroadcast(it);

        Button t1 = findViewById(R.id.option1);
        Button t2 = findViewById(R.id.option2);
        Button t3 = findViewById(R.id.option3);
        Button t4 = findViewById(R.id.option4);

        SharedPreferences sharedPreferences = getSharedPreferences("rgdgr8", MODE_PRIVATE);
        last = sharedPreferences.getInt("last" + pos, 0);

        AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlertReceiver2.class);
        intent.setPackage(getPackageName());
        intent.putExtra("content", content);
        intent.putExtra("id", pos);
        intent.putExtra("date", date);
        intent.putExtra("time", time);
        intent.putExtra("period", period);
        intent.putExtra("num", num);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(DelayActivity.this, pos, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        assert alarmManager != null;

        t1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis() + 300000, pendingIntent);
                sharedPreferences.edit().putInt("last" + pos, last + 300000).apply();

                Log.i("last", String.valueOf(last));

                finish();
            }
        });

        t2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis() + 600000, pendingIntent);
                sharedPreferences.edit().putInt("last" + pos, last + 600000).apply();

                Log.i("last", String.valueOf(last));

                finish();
            }
        });

        t3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis() + 900000, pendingIntent);
                sharedPreferences.edit().putInt("last" + pos, last + 900000).apply();

                Log.i("last", String.valueOf(last));

                finish();
            }
        });

        t4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis() + 1200000, pendingIntent);
                sharedPreferences.edit().putInt("last" + pos, last + 1200000).apply();

                Log.i("last", String.valueOf(last));

                finish();
            }
        });

    }
}
