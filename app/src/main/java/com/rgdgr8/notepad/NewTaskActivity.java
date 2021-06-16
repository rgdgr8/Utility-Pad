package com.rgdgr8.notepad;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class NewTaskActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_task);

        String date = getIntent().getStringExtra("date");
        String time = getIntent().getStringExtra("time");
        String content = getIntent().getStringExtra("notiContent");
        String period = getIntent().getStringExtra("period");
        int num = getIntent().getIntExtra("num",0);

        String repeat="";

        assert period != null;
        if (num==0 && period.equals("")){
            repeat="One time Reminder";
        }else {
            repeat="Repeat every "+num+" "+period;
        }

        TextView textViewText = findViewById(R.id.tvText);
        TextView textViewDate = findViewById(R.id.tvDate);
        TextView textViewTime = findViewById(R.id.tvTime);
        TextView textViewRep = findViewById(R.id.tvrep);
        Button b1 = findViewById(R.id.doneNoti);

        textViewText.setText(content);
        textViewDate.setText(date);
        textViewTime.setText(time);
        textViewRep.setText(repeat);

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
}
