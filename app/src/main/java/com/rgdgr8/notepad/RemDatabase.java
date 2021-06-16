package com.rgdgr8.notepad;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class RemDatabase extends SQLiteOpenHelper {

    private String DB_NAME;
    private String TB_NAME = "Rem";
    private int DB_VERSION;

    Context ctx;
    SQLiteDatabase mydb;

    public RemDatabase(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);

        ctx = context;
        DB_NAME = name;
        DB_VERSION = version;

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table if not exists " + TB_NAME + " (_id integer primary key autoincrement ,content varchar, year integer, month integer, day integer, hour integer, min integer, date varchar, time varchar, period varchar, num integer);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + TB_NAME);
        onCreate(db);
    }

    public ArrayList<String> getWholeFav() {
        mydb = getReadableDatabase();
        Cursor cr = mydb.rawQuery("select * from " + TB_NAME, null);
        ArrayList<String> data = new ArrayList<>();

        while (cr.moveToNext()) {
            data.add(cr.getString(1));
        }

        cr.close();
        return data;
    }

    public ArrayList<Integer> getWholeYear() {
        mydb = getReadableDatabase();
        Cursor cr = mydb.rawQuery("select * from " + TB_NAME, null);
        ArrayList<Integer> data = new ArrayList<>();

        while (cr.moveToNext()) {
            data.add(cr.getInt(2));
        }

        cr.close();
        return data;
    }

    public ArrayList<Integer> getWholeMonth() {
        mydb = getReadableDatabase();
        Cursor cr = mydb.rawQuery("select * from " + TB_NAME, null);
        ArrayList<Integer> data = new ArrayList<>();

        while (cr.moveToNext()) {
            data.add(cr.getInt(3));
        }

        cr.close();
        return data;
    }

    public ArrayList<Integer> getWholeDay() {
        mydb = getReadableDatabase();
        Cursor cr = mydb.rawQuery("select * from " + TB_NAME, null);
        ArrayList<Integer> data = new ArrayList<>();

        while (cr.moveToNext()) {
            data.add(cr.getInt(4));
        }

        cr.close();
        return data;
    }

    public ArrayList<Integer> getWholeHour() {
        mydb = getReadableDatabase();
        Cursor cr = mydb.rawQuery("select * from " + TB_NAME, null);
        ArrayList<Integer> data = new ArrayList<>();

        while (cr.moveToNext()) {
            data.add(cr.getInt(5));
        }

        cr.close();
        return data;
    }

    public ArrayList<Integer> getWholeMin() {
        mydb = getReadableDatabase();
        Cursor cr = mydb.rawQuery("select * from " + TB_NAME, null);
        ArrayList<Integer> data = new ArrayList<>();

        while (cr.moveToNext()) {
            data.add(cr.getInt(6));
        }

        cr.close();
        return data;
    }

    public ArrayList<String> getWholeDate() {
        mydb = getReadableDatabase();
        Cursor cr = mydb.rawQuery("select * from " + TB_NAME, null);
        ArrayList<String> data = new ArrayList<>();

        while (cr.moveToNext()) {
            data.add(cr.getString(7));
        }

        cr.close();
        return data;
    }

    public ArrayList<String> getWholeTime() {
        mydb = getReadableDatabase();
        Cursor cr = mydb.rawQuery("select * from " + TB_NAME, null);
        ArrayList<String> data = new ArrayList<>();

        while (cr.moveToNext()) {
            data.add(cr.getString(8));
        }

        cr.close();
        return data;
    }

    public ArrayList<String> getWholePeriod() {
        mydb = getReadableDatabase();
        Cursor cr = mydb.rawQuery("select * from " + TB_NAME, null);
        ArrayList<String> data = new ArrayList<>();

        while (cr.moveToNext()) {
            data.add(cr.getString(9));
        }

        cr.close();
        return data;
    }

    public ArrayList<Integer> getWholeNum() {
        mydb = getReadableDatabase();
        Cursor cr = mydb.rawQuery("select * from " + TB_NAME, null);
        ArrayList<Integer> data = new ArrayList<>();

        while (cr.moveToNext()) {
            data.add(cr.getInt(10));
        }

        cr.close();
        return data;
    }

    public void UpdateFav() {
        mydb = getWritableDatabase();
        mydb.execSQL("delete from " + TB_NAME);
        for (int j = 0; j < MainActivity.reminders.size(); j++) {
            String content = MainActivity.reminders.get(j);
            String date = MainActivity.dateList.get(j);
            String time = MainActivity.timeList.get(j);
            int year = MainActivity.year.get(j);
            int month = MainActivity.month.get(j);
            int day = MainActivity.day.get(j);
            int hour = MainActivity.hour.get(j);
            int min = MainActivity.min.get(j);
            int num = MainActivity.nums.get(j);
            String period = MainActivity.periods.get(j);
            try {
                mydb.execSQL("insert into " + TB_NAME + " (content, year, month, day, hour, min, date, time, period, num) values ('" + content + "'," + year + "," + month + "," + day + "," + hour + "," + min + ",'" + date + "','" + time + "','" + period + "'," + num + ");");
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(ctx, "Something Went Wrong while resetting Reminders!!!, Try Updating again", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
