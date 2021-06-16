package com.rgdgr8.notepad;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class MyDatabase extends SQLiteOpenHelper {

    private String DB_NAME;
    private String TB_NAME="data";
    private int DB_VERSION;

    Context ctx;
    SQLiteDatabase mydb;

    public MyDatabase(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        ctx=context;
        DB_NAME=name;
        DB_VERSION=version;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table if not exists "+TB_NAME+" (_id integer,content varchar);");
        String first=ctx.getResources().getString(R.string.tip_2);
        try {
            db.execSQL("insert into "+TB_NAME+" (_id,content) values (1,'"+first+"');");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists "+TB_NAME);
        onCreate(db);
    }

    public ArrayList<String> getWholeData(){
        mydb=getReadableDatabase();
        Cursor cr=mydb.rawQuery("select * from "+TB_NAME,null);
        ArrayList<String> data=new ArrayList<>();

        while (cr.moveToNext()){
            data.add(cr.getString(1));
        }

        cr.close();
        return data;
    }

    public void deleteOrUpdateData() {
        mydb = getWritableDatabase();
        mydb.execSQL("delete from " + TB_NAME);
        for (int j = 0; j < MainActivity.copies.size(); j++) {
                int k = j+1;
                String content = MainActivity.copies.get(j);
                try {
                    mydb.execSQL("insert into " + TB_NAME + " (_id,content) values (" + k + ",'" + content + "');");
                }catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(ctx, "Something Went Wrong while deleting!!!, try deleting again", Toast.LENGTH_SHORT).show();
                }
        }
    }
}
