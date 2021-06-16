package com.rgdgr8.notepad;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Collections;

public class EditActivity extends AppCompatActivity {

    EditText edit;
    TextView textView;
    ImageButton button;
    int positionSaver = -1;
    int positionSaverFav = -1;
    int mainORfav;
    String tag = "";
    String unedit;
    AlertDialog.Builder alert;

    int click;
    boolean visible=false;
    SharedPreferences sharedPreferences;

    Typeface typeface;

    @Override
    public void onBackPressed() {

        if (!tag.equals("") && !tag.equals(unedit)) {

            if (MainActivity.mode.equals("f")) {
                switch (MainActivity.font) {

                    case "0":
                        alert = new AlertDialog.Builder(this, R.style.myDialog0);
                        break;
                    case "1":
                        alert = new AlertDialog.Builder(this, R.style.myDialog1);
                        break;
                    case "2":
                        alert = new AlertDialog.Builder(this, R.style.myDialog2);
                        break;
                    case "3":
                        alert = new AlertDialog.Builder(this, R.style.myDialog3);
                        break;
                    case "4":
                        alert = new AlertDialog.Builder(this, R.style.myDialog4);
                        break;
                    case "5":
                        alert = new AlertDialog.Builder(this, R.style.myDialog5);
                        break;
                    case "6":
                        alert = new AlertDialog.Builder(this, R.style.myDialog6);
                        break;
                    case "7":
                        alert = new AlertDialog.Builder(this, R.style.myDialog7);
                        break;
                }
            }else if (MainActivity.mode.equals("o")){
                switch (MainActivity.font) {

                    case "0":
                        alert = new AlertDialog.Builder(this, R.style.myDialog02);
                        break;
                    case "1":
                        alert = new AlertDialog.Builder(this, R.style.myDialog12);
                        break;
                    case "2":
                        alert = new AlertDialog.Builder(this, R.style.myDialog22);
                        break;
                    case "3":
                        alert = new AlertDialog.Builder(this, R.style.myDialog32);
                        break;
                    case "4":
                        alert = new AlertDialog.Builder(this, R.style.myDialog42);
                        break;
                    case "5":
                        alert = new AlertDialog.Builder(this, R.style.myDialog52);
                        break;
                    case "6":
                        alert = new AlertDialog.Builder(this, R.style.myDialog62);
                        break;
                    case "7":
                        alert = new AlertDialog.Builder(this, R.style.myDialog72);
                        break;
                }
            }

            alert.setTitle("Exit Without Saving?")
                    .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .setNegativeButton("NO", null);
            AlertDialog dialog = alert.create();
            dialog.show();
            dialog.getWindow().setLayout(800, 500);

            Button button1=dialog.findViewById(android.R.id.button1);
            Button button2=dialog.findViewById(android.R.id.button2);
            assert button1 != null;
            button1.setTextColor(getResources().getColor(R.color.colorPrimary));
            assert button2 != null;
            button2.setTextColor(getResources().getColor(R.color.colorPrimary));

        } else {
            finish();
        }
    }

    public void save(View v) {

        click++;

        if (click==1) {
            tag = tag.replace("'", "’");

            if (mainORfav == 1) {

                if (!MainActivity.copies.equals(MainActivity.db.getWholeData())) {
                    MainActivity.copies.clear();
                    MainActivity.copies.addAll(MainActivity.db.getWholeData());
                }

                if (positionSaver > -1) {

                    if (!MainActivity.copies.contains(tag)) {

                        try {

                            tag = tag.trim();
                            MainActivity.copies.set(positionSaver, tag);
                            MainActivity.db.deleteOrUpdateData();

                            Toast.makeText(this, "UPDATED!", Toast.LENGTH_SHORT).show();

                            if (!myADApter.clickPurpose.isEmpty()) {
                                myADApter.clickPurpose.set(myADApter.clickPurpose.indexOf(unedit), tag);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(this, "Couldn't Save!! Try again", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(this, "You already have this item copied in UTILITY PAD!", Toast.LENGTH_SHORT).show();
                    }

                } else {

                    if (!MainActivity.copies.contains(tag)) {
                        tag = tag.trim();
                        if (MainActivity.orderReverser%2==0) {
                            MainActivity.copies.add(tag);
                        }else {
                            Collections.reverse(MainActivity.copies);
                            MainActivity.copies.add(tag);
                            Collections.reverse(MainActivity.copies);
                        }
                        MainActivity.db.deleteOrUpdateData();

                        Toast.makeText(this, "SAVED!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "You already have this item copied in UTILITY PAD!", Toast.LENGTH_SHORT).show();
                    }
                }

            } else if (mainORfav == 2) {

                if (!MainActivity.favs.equals(MainActivity.favDb.getWholeFav())) {
                    MainActivity.favs.clear();
                    MainActivity.favs.addAll(MainActivity.favDb.getWholeFav());
                }

                if (positionSaverFav > -1) {

                    if (!MainActivity.favs.contains(tag)) {

                        tag = tag.trim();
                        MainActivity.favs.set(positionSaverFav, tag);
                        MainActivity.favDb.UpdateFav();

                        Toast.makeText(this, "UPDATED!", Toast.LENGTH_SHORT).show();

                        if (!favAdapter.clickPurposeFav.isEmpty()) {
                            favAdapter.clickPurposeFav.set(favAdapter.clickPurposeFav.indexOf(unedit), tag);
                        }
                    }
                } else {

                    if (!MainActivity.favs.contains(tag)) {
                        tag = tag.trim();
                        if (MainActivity.orderReverser%2==0) {
                            MainActivity.favs.add(tag);
                        }else {
                            Collections.reverse(MainActivity.favs);
                            MainActivity.favs.add(tag);
                            Collections.reverse(MainActivity.favs);
                        }
                        MainActivity.favDb.UpdateFav();

                        Toast.makeText(this, "SAVED!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "You already have this item Added in Favourites!", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            myADApter.clickPurpose.clear();
            favAdapter.clickPurposeFav.clear();

            finish();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_edit);

        /*
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        mInterstitialAd.setAdListener(new AdListener(){

            @Override
            public void onAdLoaded() {
                mInterstitialAd.show();
            }

            @Override
            public void onAdClosed() {
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
            }

            @Override
            public void onAdFailedToLoad(int i) {

            }
        });

         */

        edit = findViewById(R.id.editText);
        textView = findViewById(R.id.textView3);
        button = findViewById(R.id.imageButton2);
        button.setEnabled(false);
        Intent intent = getIntent();
        mainORfav = intent.getIntExtra("mORf", -1);
        unedit = intent.getStringExtra("textPresent");

        sharedPreferences=getSharedPreferences("rgdgr8",MODE_PRIVATE);

        LinearLayout constraintLayout=findViewById(R.id.editLayout);

        click=0;

        switch (MainActivity.font) {

            case "0":
                typeface = ResourcesCompat.getFont(this, R.font.annie_use_your_telescope);
                textView.setTypeface(typeface);
                edit.setTypeface(typeface);
                break;
            case "1":
                typeface = ResourcesCompat.getFont(this, R.font.abeezee);
                textView.setTypeface(typeface);
                edit.setTypeface(typeface);
                break;
            case "2":
                typeface = ResourcesCompat.getFont(this, R.font.amatic_sc);
                textView.setTypeface(typeface);
                edit.setTypeface(typeface);
                break;
            case "3":
                typeface = ResourcesCompat.getFont(this, R.font.amita);
                textView.setTypeface(typeface);
                edit.setTypeface(typeface);
                break;
            case "4":
                typeface = ResourcesCompat.getFont(this, R.font.bad_script);
                textView.setTypeface(typeface);
                edit.setTypeface(typeface);
                break;
            case "5":
                typeface = ResourcesCompat.getFont(this, R.font.butterfly_kids);
                textView.setTypeface(typeface);
                edit.setTypeface(typeface);
                break;
            case "6":
                typeface = ResourcesCompat.getFont(this, R.font.caesar_dressing);
                textView.setTypeface(typeface);
                edit.setTypeface(typeface);
                break;
            case "7":
                typeface = ResourcesCompat.getFont(this, R.font.alegreya_sc_italic);
                textView.setTypeface(typeface);
                edit.setTypeface(typeface);
                break;
        }

        if (MainActivity.mode.equals("f")){
            if (getResources().getConfiguration().orientation== Configuration.ORIENTATION_LANDSCAPE) {
                constraintLayout.setBackgroundResource(R.drawable.trans6);//OK
            }else {
                constraintLayout.setBackgroundResource(R.drawable.edit3);//OK
            }
        }else if (MainActivity.mode.equals("o")){
            constraintLayout.setBackgroundResource(R.drawable.jap);//OK
        }

        if (!unedit.equals("")) {
            edit.setText(unedit);
            textView.setText("EDIT YOUR COPIED TEXT HERE :");
        } else {
            textView.setText("ENTER YOUR TEXT HERE :");
        }

        for (int i = 0; i < MainActivity.copies.size(); i++) {
            if (MainActivity.copies.get(i).equals(unedit)) {
                positionSaver = i;
                break;
            }
        }

        for (int i = 0; i < MainActivity.favs.size(); i++) {
            if (MainActivity.favs.get(i).equals(unedit)) {
                positionSaverFav = i;
                break;
            }
        }

        edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                tag = s.toString();
                if (tag.equals("") || tag.equals(unedit)) {
                    button.setVisibility(View.INVISIBLE);
                    button.setEnabled(false);
                    visible=false;
                } else {
                    button.setVisibility(View.VISIBLE);
                    button.setEnabled(true);
                    visible=true;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (savedInstanceState != null) {
            tag = savedInstanceState.getString("tag");
            visible=savedInstanceState.getBoolean("vis");

            if (visible){
                button.setVisibility(View.VISIBLE);
                button.setEnabled(true);
            }else {
                button.setVisibility(View.INVISIBLE);
                button.setEnabled(false);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("tag", tag);
        outState.putBoolean("vis",visible);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = new Intent(getApplicationContext(), MyService.class);
        stopService(intent);
        click=0;
        sharedPreferences.edit().putBoolean("main",false).apply();
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
