package com.rgdgr8.notepad;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class Main2Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        getWindow().setStatusBarColor(getResources().getColor(R.color.yellowish_white));

        stopService(new Intent(getApplicationContext(),MyService.class));

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                }catch (Exception e){
                    e.printStackTrace();
                }
                finally {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent1=new Intent(Main2Activity.this,MainActivity.class);
                            startActivity(intent1);
                            finish();
                        }
                    });
                }
            }
        }).start();
    }
}
