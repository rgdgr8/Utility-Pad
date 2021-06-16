package com.rgdgr8.notepad;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.Calendar;

public class ReminderActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    Calendar c;
    int saveClick;
    int _id;

    static int singledateClick;
    static int singletimeClick;

    boolean visible;
    boolean saveVis;

    EditText text;
    Button time;
    Button date;
    Button delete;
    Button repeat;
    Button save;

    String dateText = "";
    String timeText = "";
    int yearMy = 0;
    int monthMy = 0;
    int dayMy = 0;
    int hourMy = -1;
    int minuteMy = -1;
    int num = 0;
    String period = "";

    String unedit = "";
    String uneditDate = "";
    String uneditTime = "";

    AlertDialog.Builder alertDialog;
    String[] strings = new String[]{"Minutes", "Hours", "Days", "Weeks"};
    MenuItem once;
    MenuItem hourly;
    MenuItem daily;
    MenuItem weekly;
    MenuItem customly;

    SharedPreferences sharedPreferences;

    @Override
    public void onBackPressed() {

        if (!dateText.equals("") && !timeText.equals("") && !text.getText().toString().equals("") && !(text.getText().toString().equals(unedit) && timeText.equals(uneditTime) && dateText.equals(uneditDate) && !repeatChange())) {
            alertDialog.setTitle("Exit Without Saving?")
                    .setNegativeButton("No", null)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    }).show();
        } else {
            finish();
        }
    }

    private boolean repeatChange() {
        if (!period.equals(MainActivity.periods.get(_id - 1)) || num != MainActivity.nums.get(_id - 1)) {
            return true;
        } else {
            return false;
        }
    }

    String dead = "*(DEAD)* ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_reminder);

        date = findViewById(R.id.date);
        time = findViewById(R.id.time);
        save = findViewById(R.id.remiSave);
        repeat = findViewById(R.id.repeat);
        registerForContextMenu(repeat);
        delete = findViewById(R.id.del);

        text = findViewById(R.id.remiEdit);
        LinearLayout linearLayout = findViewById(R.id.linearLayout);

        if (MainActivity.mode.equals("o")) {
            linearLayout.setBackgroundResource(R.drawable.mygreyrep2);//OK
            alertDialog = new AlertDialog.Builder(this, R.style.myDialog72);
        } else if (MainActivity.mode.equals("f")) {
            linearLayout.setBackgroundResource(R.drawable.beat2);//OK
            alertDialog = new AlertDialog.Builder(this, R.style.myDialog7);
        }

        MainActivity.reminders = MainActivity.remDb.getWholeFav();

        String content = getIntent().getStringExtra("content");

        if (content == null) {
            content = "";
        }

        _id = getIntent().getIntExtra("id", -1);

        if (_id <= 0) {
            _id = MainActivity.reminders.size() + 1;
        } else {
            content = MainActivity.remDb.getWholeFav().get(_id - 1);
        }

        if (content.startsWith(dead)) {
            content = content.substring(dead.length());
        }

        unedit = content;

        text.setText(content);

        c = Calendar.getInstance();

        text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (!dateText.equals("") && !timeText.equals("") && !s.toString().equals("") && !(s.toString().equals(unedit) && timeText.equals(uneditTime) && dateText.equals(uneditDate) && !repeatChange())) {
                    save.setVisibility(View.VISIBLE);
                    saveVis = true;
                } else {
                    save.setVisibility(View.INVISIBLE);
                    saveVis = false;
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        repeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.showContextMenu();
            }
        });

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                singledateClick++;

                if (singledateClick == 1) {
                    com.rgdgr8.notepad.DatePicker date = new com.rgdgr8.notepad.DatePicker();
                    if (visible || (yearMy != 0 && monthMy != 0 && dayMy != 0)) {
                        Bundle bundle = new Bundle();
                        bundle.putInt("year", yearMy);
                        bundle.putInt("month", monthMy);
                        bundle.putInt("day", dayMy);
                        date.setArguments(bundle);
                    }
                    date.show(getSupportFragmentManager(), "date picker");
                }
            }
        });

        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                singletimeClick++;

                if (singletimeClick == 1) {

                    com.rgdgr8.notepad.TimePicker time = new com.rgdgr8.notepad.TimePicker();
                    if (visible || (hourMy != -1 && minuteMy != -1)) {
                        Bundle bundle = new Bundle();
                        bundle.putInt("hour", hourMy);
                        bundle.putInt("min", minuteMy);
                        time.setArguments(bundle);
                    }
                    time.show(getSupportFragmentManager(), "time picker");
                }
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                saveClick++;

                if (saveClick == 1) {
                    if (!text.getText().toString().equals("") && !dateText.equals("") && !timeText.equals("")) {
                        startAlarm(c, text.getText().toString(), _id);
                    } else {
                        Toast.makeText(ReminderActivity.this, "Please set a Proper Date, Time and Reminder Message !", Toast.LENGTH_SHORT).show();
                        saveClick = 0;
                    }
                }
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelAlarm(_id);
                delete.setEnabled(false);
            }
        });

        if (savedInstanceState != null) {
            timeText = savedInstanceState.getString("time");
            dateText = savedInstanceState.getString("date");
            content = savedInstanceState.getString("text");
            unedit = savedInstanceState.getString("un");
            uneditTime = savedInstanceState.getString("untime");
            uneditDate = savedInstanceState.getString("undate");
            text.setText(content);
            visible = savedInstanceState.getBoolean("visible");
            saveVis = savedInstanceState.getBoolean("saveVis");
            period = savedInstanceState.getString("period");
            num = savedInstanceState.getInt("num");
            if (visible) {
                delete.setVisibility(View.VISIBLE);
            } else {
                delete.setVisibility(View.INVISIBLE);
            }

            if (saveVis) {
                save.setVisibility(View.VISIBLE);
            } else {
                save.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString("text", text.getText().toString());
        outState.putString("time", timeText);
        outState.putString("date", dateText);
        outState.putString("untime", uneditTime);
        outState.putString("undate", uneditDate);
        outState.putString("un", unedit);
        outState.putBoolean("visible", visible);
        outState.putBoolean("saveVis", saveVis);
        outState.putInt("num", num);
        outState.putString("period", period);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        menu.setHeaderTitle("Choose Repeating Period");
        getMenuInflater().inflate(R.menu.repeat_menu, menu);
        once = menu.findItem(R.id.clear);
        hourly = menu.findItem(R.id.hourMenu);
        daily = menu.findItem(R.id.dayMenu);
        weekly = menu.findItem(R.id.weekMenu);
        customly = menu.findItem(R.id.customly);

        if (period.equals("") && num == 0) {
            once.setChecked(true);
        } else if (num == 1) {
            switch (period) {
                case "Hours":
                    hourly.setChecked(true);
                    break;
                case "Days":
                    daily.setChecked(true);
                    break;
                case "Weeks":
                    weekly.setChecked(true);
                    break;
            }
        } else if (num > 1) {
            customly.setChecked(true);
        }
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {

        Log.i("period", period);
        Log.i("num", String.valueOf(num));

        switch (item.getItemId()) {
            case R.id.customly:
                show();
                return true;

            case R.id.hourMenu:
                period = "Hours";
                num = 1;
                String s = "Repeat every Hour";
                repeat.setText(s);
                return true;

            case R.id.dayMenu:
                period = "Days";
                num = 1;
                String s2 = "Repeat every Day";
                repeat.setText(s2);
                return true;

            case R.id.weekMenu:
                period = "Weeks";
                num = 1;
                String s3 = "Repeat every Week";
                repeat.setText(s3);
                return true;

            case R.id.clear:
                period = "";
                num = 0;
                repeat.setText("Repeat Once");
                return true;

            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public void onContextMenuClosed(@NonNull Menu menu) {
        super.onContextMenuClosed(menu);

        if (!dateText.equals("") && !timeText.equals("") && !text.getText().toString().equals("") && !(text.getText().toString().equals(unedit) && timeText.equals(uneditTime) && dateText.equals(uneditDate) && !repeatChange())) {
            save.setVisibility(View.VISIBLE);
            saveVis = true;
        } else {
            save.setVisibility(View.INVISIBLE);
            saveVis = false;
        }
    }

    public void show() {

        final Dialog d = new Dialog(this);
        d.setContentView(R.layout.num_dialog);
        Button b1 = d.findViewById(R.id.numDone);
        Button b2 = d.findViewById(R.id.numCan);
        final NumberPicker np = d.findViewById(R.id.numberPickerDia1);
        final NumberPicker np2 = d.findViewById(R.id.numberPickerDia2);

        np2.setMinValue(1);
        np2.setMaxValue(4);
        np2.setDisplayedValues(strings);

        np.setMaxValue(1000);
        np.setMinValue(1);

        if (!period.equals("") && num != 0) {

            switch (period) {
                case "Days":
                    np2.setValue(3);
                    break;
                case "Hours":
                    np2.setValue(2);
                    break;
                case "Minutes":
                    np2.setValue(1);
                    break;
                case "Weeks":
                    np2.setValue(4);
                    break;
            }

            np.setValue(num);
        }

        np2.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                switch (newVal) {
                    case 1:
                        np.setMinValue(1);
                        break;

                    case 2:

                    case 3:

                    case 4:
                        np.setMinValue(2);
                        break;
                }
            }
        });

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                num = np.getValue();
                period = strings[np2.getValue() - 1];
                String s = "Repeat every " + num + " " + period;
                repeat.setText(s);
                customly.setChecked(true);

                if (!dateText.equals("") && !timeText.equals("") && !text.getText().toString().equals("") && !(text.getText().toString().equals(unedit) && timeText.equals(uneditTime) && dateText.equals(uneditDate) && !repeatChange())) {
                    save.setVisibility(View.VISIBLE);
                    saveVis = true;
                } else {
                    save.setVisibility(View.INVISIBLE);
                    saveVis = false;
                }

                d.cancel();
            }
        });

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String s = "Repeat Once";
                repeat.setText(s);
                once.setChecked(true);
                d.cancel();

            }
        });

        d.show();
    }

    @Override
    public void onDateSet(android.widget.DatePicker view, int year, int month, int dayOfMonth) {
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        yearMy = year;
        monthMy = month;
        dayMy = dayOfMonth;

        dateText = DateFormat.getDateInstance(DateFormat.FULL).format(c.getTime());

        date.setText(dateText);

        singledateClick = 0;

        if (!dateText.equals("") && !timeText.equals("") && !text.getText().toString().equals("") && !(text.getText().toString().equals(unedit) && timeText.equals(uneditTime) && dateText.equals(uneditDate) && !repeatChange())) {
            save.setVisibility(View.VISIBLE);
            saveVis = true;
        } else {
            save.setVisibility(View.INVISIBLE);
            saveVis = false;
        }
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

        c.set(Calendar.HOUR_OF_DAY, hourOfDay);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND, 0);

        hourMy = hourOfDay;
        minuteMy = minute;

        int t = minute;
        int k = 0;

        while (t > 0) {
            t = t / 10;
            k++;
        }

        if (k == 1 || minute == 0) {
            if (hourOfDay > 12) {
                timeText = hourOfDay - 12 + ":0" + minute + "pm";
            } else if (hourOfDay == 12) {
                timeText = "12" + ":0" + minute + "pm";
            } else {
                if (hourOfDay != 0) {
                    timeText = hourOfDay + ":0" + minute + "am";
                } else {
                    timeText = "12" + ":0" + minute + "am";
                }
            }
        } else {

            if (hourOfDay > 12) {
                timeText = hourOfDay - 12 + ":" + minute + "pm";
            } else if (hourOfDay == 12) {
                timeText = "12" + ":" + minute + "pm";
            } else {
                if (hourOfDay != 0) {
                    timeText = hourOfDay + ":" + minute + "am";
                } else {
                    timeText = "12" + ":" + minute + "am";
                }
            }
        }

        time.setText(timeText);
        singletimeClick = 0;

        if (!dateText.equals("") && !timeText.equals("") && !text.getText().toString().equals("") && !(text.getText().toString().equals(unedit) && timeText.equals(uneditTime) && dateText.equals(uneditDate) && !repeatChange())) {
            save.setVisibility(View.VISIBLE);
            saveVis = true;
        } else {
            save.setVisibility(View.INVISIBLE);
            saveVis = false;
        }
    }

    public void startAlarm(Calendar c, String content, int _id) {
        AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlertReceiver.class);
        intent.setPackage(getPackageName());
        intent.putExtra("content", content);
        intent.putExtra("id", _id);
        intent.putExtra("date", dateText);
        intent.putExtra("time", timeText);
        intent.putExtra("period", period);
        intent.putExtra("num", num);

        Log.i("startalarm","id: "+ _id);
        Log.i("startalarm","content: "+ content);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, _id, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        c.set(Calendar.YEAR, yearMy);
        c.set(Calendar.MONTH, monthMy);
        c.set(Calendar.DAY_OF_MONTH, dayMy);
        c.set(Calendar.HOUR_OF_DAY, hourMy);
        c.set(Calendar.MINUTE, minuteMy);
        c.set(Calendar.SECOND, 0);

        if (c.before(Calendar.getInstance())) {
            Toast.makeText(ReminderActivity.this, "Please set a Proper Date and Time !", Toast.LENGTH_SHORT).show();
            saveClick = 0;
        } else {
            content = content.replace("'", "’");

            int count = 0;

            for (int i = 0; i < MainActivity.reminders.size(); i++) {
                if (content.equals(MainActivity.reminders.get(i)) && dateText.equals(MainActivity.dateList.get(i)) && timeText.equals(MainActivity.timeList.get(i)) && period.equals(MainActivity.periods.get(i)) && num == MainActivity.nums.get(i)) {
                    break;
                } else {
                    count++;
                }
            }

            if (count == MainActivity.reminders.size()) {

                assert alarmManager != null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
                } else {
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
                }

                if (MainActivity.reminders.size() < _id) {
                    MainActivity.reminders.add(content);
                    MainActivity.year.add(yearMy);
                    MainActivity.month.add(monthMy);
                    MainActivity.day.add(dayMy);
                    MainActivity.hour.add(hourMy);
                    MainActivity.min.add(minuteMy);
                    MainActivity.dateList.add(dateText);
                    MainActivity.timeList.add(timeText);
                    MainActivity.periods.add(period);
                    MainActivity.nums.add(num);

                    int cInter = getSharedPreferences("rgdgr8", MODE_PRIVATE).getInt("interRem", 0);
                    cInter++;

                    if (cInter >= 8) {
                        if (MainActivity.mInterstitialAd.isLoaded()) {
                            cInter = 0;
                            MainActivity.mInterstitialAd.show();
                        }
                    }
                    getSharedPreferences("rgdgr8", MODE_PRIVATE).edit().putInt("interRem", cInter).apply();

                } else {
                    MainActivity.reminders.set(_id - 1, content);
                    MainActivity.dateList.set(_id - 1, dateText);
                    MainActivity.timeList.set(_id - 1, timeText);
                    MainActivity.year.set(_id - 1, yearMy);
                    MainActivity.month.set(_id - 1, monthMy);
                    MainActivity.day.set(_id - 1, dayMy);
                    MainActivity.hour.set(_id - 1, hourMy);
                    MainActivity.min.set(_id - 1, minuteMy);
                    MainActivity.periods.set(_id - 1, period);
                    MainActivity.nums.set(_id - 1, num);

                    Intent myIntent2 = new Intent(this, AlertReceiver2.class);
                    myIntent2.setPackage(getPackageName());
                    int posi = _id + 90000;
                    PendingIntent pendingIntent2 = PendingIntent.getBroadcast(this, posi, myIntent2, PendingIntent.FLAG_UPDATE_CURRENT);
                    alarmManager.cancel(pendingIntent2);
                    NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
                    notificationManagerCompat.cancel(posi);
                    notificationManagerCompat.cancel(_id);

                    int last = getSharedPreferences("rgdgr8", Context.MODE_PRIVATE).getInt("last" + posi, 0);
                    Log.i("lastRemAd", String.valueOf(last));
                    sharedPreferences.edit().putInt("last" + posi, 0).apply();
                    getSharedPreferences("rgdgr8", Context.MODE_PRIVATE).edit().putInt("r" + _id, 0).apply();
                }

                MainActivity.remDb.UpdateFav();

                Toast.makeText(this, "Reminder Set", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Reminder Already Present", Toast.LENGTH_SHORT).show();
            }

            finish();
        }

    }

    private void cancelAlarm(int _id) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent myIntent = new Intent(this, AlertReceiver.class);
        myIntent.setPackage(getPackageName());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, _id, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Log.i("cancelAlarm","id: "+ _id);

        assert alarmManager != null;
        alarmManager.cancel(pendingIntent);
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.cancel(_id);

        Intent myIntent2 = new Intent(this, AlertReceiver2.class);
        myIntent2.setPackage(getPackageName());
        int posi = _id + 90000;
        PendingIntent pendingIntent2 = PendingIntent.getBroadcast(this, posi, myIntent2, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(pendingIntent2);
        notificationManagerCompat.cancel(posi);

        int last = getSharedPreferences("rgdgr8", Context.MODE_PRIVATE).getInt("last" + posi, 0);
        sharedPreferences.edit().putInt("last" + posi, 0).apply();

        MainActivity.reminders.set(_id - 1, "");
        MainActivity.dateList.set(_id - 1, "");
        MainActivity.timeList.set(_id - 1, "");
        MainActivity.year.set(_id - 1, 0);
        MainActivity.month.set(_id - 1, 0);
        MainActivity.day.set(_id - 1, 0);
        MainActivity.hour.set(_id - 1, 0);
        MainActivity.min.set(_id - 1, 0);
        MainActivity.nums.set(_id - 1, 0);
        MainActivity.periods.set(_id - 1, "");
        MainActivity.remDb.UpdateFav();

        getSharedPreferences("rgdgr8", Context.MODE_PRIVATE).edit().putInt("r" + _id, 0).apply();

        Toast.makeText(this, "Reminder Cancelled", Toast.LENGTH_SHORT).show();

        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();

        Intent intent = new Intent(getApplicationContext(), MyService.class);
        stopService(intent);

        sharedPreferences = getSharedPreferences("rgdgr8", MODE_PRIVATE);

        saveClick = 0;
        singledateClick = 0;
        singletimeClick = 0;

        if (MainActivity.remDb.getWholeFav() != null && MainActivity.reminders.size() >= _id) {
            MainActivity.dateList = MainActivity.remDb.getWholeDate();
            MainActivity.timeList = MainActivity.remDb.getWholeTime();
            MainActivity.periods = MainActivity.remDb.getWholePeriod();

            dateText = MainActivity.remDb.getWholeDate().get(_id - 1);
            timeText = MainActivity.remDb.getWholeTime().get(_id - 1);
            period = MainActivity.remDb.getWholePeriod().get(_id - 1);

            uneditDate = dateText;
            uneditTime = timeText;

            MainActivity.reminders = MainActivity.remDb.getWholeFav();
            MainActivity.year = MainActivity.remDb.getWholeYear();
            MainActivity.month = MainActivity.remDb.getWholeMonth();
            MainActivity.day = MainActivity.remDb.getWholeDay();
            MainActivity.hour = MainActivity.remDb.getWholeHour();
            MainActivity.min = MainActivity.remDb.getWholeMin();
            MainActivity.nums = MainActivity.remDb.getWholeNum();

            yearMy = MainActivity.remDb.getWholeYear().get(_id - 1);
            monthMy = MainActivity.remDb.getWholeMonth().get(_id - 1);
            dayMy = MainActivity.remDb.getWholeDay().get(_id - 1);
            hourMy = MainActivity.remDb.getWholeHour().get(_id - 1);
            minuteMy = MainActivity.remDb.getWholeMin().get(_id - 1);
            num = MainActivity.remDb.getWholeNum().get(_id - 1);
        }

        if (!dateText.equals("") && !timeText.equals("")) {
            date.setText(dateText);
            time.setText(timeText);
        } else {
            date.setText("Set Date");
            time.setText("Set Time");
        }

        if (!period.equals("") && num != 0) {
            String s = "Repeat every " + num + " " + period;
            repeat.setText(s);
        } else {
            repeat.setText("Repeat Once");
        }

        delete.setEnabled(true);

        if (yearMy != 0 && monthMy != 0 && dayMy != 0 && hourMy != -1 && minuteMy != -1) {

            delete.setVisibility(View.VISIBLE);
            visible = true;
            c.set(Calendar.YEAR, yearMy);
            c.set(Calendar.MONTH, monthMy);
            c.set(Calendar.DAY_OF_MONTH, dayMy);
            c.set(Calendar.HOUR_OF_DAY, hourMy);
            c.set(Calendar.MINUTE, minuteMy);
            c.set(Calendar.SECOND, 0);
        } else {
            delete.setVisibility(View.INVISIBLE);
            visible = false;
        }

        sharedPreferences.edit().putBoolean("main",false).apply();
    }

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
        SharedPreferences sharedPreferences=getSharedPreferences("rgdgr8",MODE_PRIVATE);

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
                if ((MainActivity.remDb.getWholePeriod().get(i).equals("Minutes") && (b.getTimeInMillis() + MainActivity.remDb.getWholeNum().get(i) * 1000 * 60 * r) <= a.getTimeInMillis()) || (MainActivity.remDb.getWholePeriod().get(i).equals("Hours") && (b.getTimeInMillis() + MainActivity.remDb.getWholeNum().get(i) * 1000 * 60 * 60 * r) <= a.getTimeInMillis()) || (MainActivity.remDb.getWholePeriod().get(i).equals("Days") && (b.getTimeInMillis() + MainActivity.remDb.getWholeNum().get(i) * 1000 * 60 * 60 * 24 * r) <= a.getTimeInMillis()) || (MainActivity.remDb.getWholePeriod().get(i).equals("Weeks") && (b.getTimeInMillis() + MainActivity.remDb.getWholeNum().get(i) * 1000 * 60 * 60 * 24 * 7 * r) <= a.getTimeInMillis())) {
                    repeaters++;
                }
            }
        }

        sharedPreferences.edit().putInt("cop", cop).apply();
        sharedPreferences.edit().putInt("rep", repeaters).apply();
    }

}
