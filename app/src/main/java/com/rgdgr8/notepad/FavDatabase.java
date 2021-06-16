package com.rgdgr8.notepad;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class FavDatabase extends SQLiteOpenHelper {

    private String DB_NAME;
    private String TB_NAME = "fav";
    private int DB_VERSION;

    Context ctx;
    SQLiteDatabase mydb;

    public FavDatabase(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);

        ctx = context;
        DB_NAME = name;
        DB_VERSION = version;

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table if not exists " + TB_NAME + " (_id integer primary key autoincrement ,content varchar);");
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

    public void UpdateFav() {
        mydb = getWritableDatabase();
        mydb.execSQL("delete from " + TB_NAME);
        for (int j = 0; j < MainActivity.favs.size(); j++) {
            String content = MainActivity.favs.get(j);
            try {
                mydb.execSQL("insert into " + TB_NAME + " (content) values ('" + content + "');");
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(ctx, "Something Went Wrong while resetting Favourites!!!, try deleting again", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
