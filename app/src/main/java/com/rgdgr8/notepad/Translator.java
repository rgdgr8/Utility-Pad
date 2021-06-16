package com.rgdgr8.notepad;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Toast;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions;
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslator;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions;

import java.util.Calendar;
import java.util.Collections;

public class Translator extends AppCompatActivity {

    FirebaseTranslator englishGermanTranslator;
    FirebaseTranslatorOptions options;
    FirebaseModelDownloadConditions conditions;

    SharedPreferences sharedPreferences;

    int num1 = 0;
    int num2 = 0;
    boolean cancel;
    String content;
    int mf;
    EditText to;
    EditText from;
    Button transButton;
    Button cancelButton;
    AlertDialog.Builder alertDialog;

    int click;
    boolean vis=false;

    @Override
    public void onBackPressed() {
        if (!cancel && to != null && !to.getText().toString().equals("")) {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_translator);

        conditions = new FirebaseModelDownloadConditions.Builder().build();

        ConstraintLayout constraintLayout = findViewById(R.id.transLayout);

        click = 0;

        cancel = true;
        content = getIntent().getStringExtra("content");
        mf = getIntent().getIntExtra("mf", -1);
        transButton = findViewById(R.id.buttonTrans);

        sharedPreferences=getSharedPreferences("rgdgr8",MODE_PRIVATE);

        cancelButton = findViewById(R.id.buttonSave);
        transButton.setText("Translate");
        from = findViewById(R.id.editTextFrom);
        to = findViewById(R.id.editTextTo);
        from.setText(content);
        to.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f);
        to.setTextColor(Color.RED);
        to.setText(getResources().getString(R.string.translator));

        NumberPicker numberPicker1 = findViewById(R.id.translator);
        NumberPicker numberPicker2 = findViewById(R.id.translator2);
        numberPicker1.setMinValue(0);
        numberPicker1.setMaxValue(10);
        numberPicker1.setDisplayedValues(new String[]{"English", "Bengali", "Japanese", "Spanish", "Arabic", "Hindi", "Chinese", "French", "German", "Italian", "Korean", "Portuguese"});
        numberPicker2.setMinValue(0);
        numberPicker2.setMaxValue(10);
        numberPicker2.setDisplayedValues(new String[]{"English", "Bengali", "Japanese", "Spanish", "Arabic", "Hindi", "Chinese", "French", "German", "Italian", "Korean", "Portuguese"});

        if (MainActivity.mode.equals("f")) {
            constraintLayout.setBackgroundResource(R.drawable.trans);//OK
            alertDialog = new AlertDialog.Builder(this, R.style.myDialog7);
        } else if (MainActivity.mode.equals("o")) {
            if (getResources().getConfiguration().orientation== Configuration.ORIENTATION_LANDSCAPE) {
                constraintLayout.setBackgroundResource(R.drawable.typewriter2);//OK
            }else {
                constraintLayout.setBackgroundResource(R.drawable.typewriter);//OK
            }
            alertDialog = new AlertDialog.Builder(this, R.style.myDialog72);
        }

        from.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                content = s.toString();
                if (content.equals("")) {
                    transButton.setVisibility(View.INVISIBLE);
                    transButton.setEnabled(false);
                    vis = false;
                } else {
                    transButton.setVisibility(View.VISIBLE);
                    transButton.setEnabled(true);
                    vis = true;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        numberPicker1.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                num1 = newVal;
                Log.i("num1", String.valueOf(num1));
                Log.i("num1Picker", String.valueOf(numberPicker1.getValue()));
            }
        });
        numberPicker2.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                num2 = newVal;
                Log.i("num2", String.valueOf(num2));
                Log.i("num2Picker", String.valueOf(numberPicker2.getValue()));
            }
        });


        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cancel) {
                    finish();
                } else {
                    click++;

                    if (click == 1) {
                        assert to != null;
                        if (!to.getText().toString().equals("")) {
                            String toSave = to.getText().toString();
                            toSave = toSave.replace("'", "’");
                            if (mf == 0) {
                                if (!MainActivity.copies.contains(toSave)) {
                                    if (MainActivity.orderReverser % 2 == 0) {
                                        MainActivity.copies.add(toSave);
                                    } else {
                                        Collections.reverse(MainActivity.copies);
                                        MainActivity.copies.add(toSave);
                                        Collections.reverse(MainActivity.copies);
                                    }
                                    MainActivity.db.deleteOrUpdateData();
                                    Toast.makeText(Translator.this, "Saved!", Toast.LENGTH_SHORT).show();
                                    finish();
                                } else {
                                    Toast.makeText(Translator.this, "Item already present MAIN!", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            } else if (mf == 1) {
                                if (!MainActivity.favs.contains(toSave)) {
                                    if (MainActivity.orderReverser % 2 == 0) {
                                        MainActivity.favs.add(toSave);
                                    } else {
                                        Collections.reverse(MainActivity.favs);
                                        MainActivity.favs.add(toSave);
                                        Collections.reverse(MainActivity.favs);
                                    }
                                    MainActivity.favDb.UpdateFav();
                                    Toast.makeText(Translator.this, "Saved!", Toast.LENGTH_SHORT).show();
                                    finish();
                                } else {
                                    Toast.makeText(Translator.this, "Item already present in Favourites!", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            }
                        } else {
                            Toast.makeText(Translator.this, "No Translated content Present!", Toast.LENGTH_SHORT).show();
                            click = 0;
                        }
                    }
                }

                int cTrans=getSharedPreferences("rgdgr8",MODE_PRIVATE).getInt("inter",0);
                cTrans++;

                if (cTrans>=4) {
                    if (MainActivity.mInterstitialAd.isLoaded()) {
                        Log.i("passed", ""+cTrans);
                        cTrans = 0;
                        MainActivity.mInterstitialAd.show();
                    } else {
                        Log.i("TAG", ""+cTrans);
                    }
                }
                getSharedPreferences("rgdgr8",MODE_PRIVATE).edit().putInt("inter",cTrans).apply();
            }
        });

        transButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                assert from != null;
                if (!from.getText().toString().equals("")) {
                    try {

                        if (num1 == 0) {
                            switch (num2) {

                                case 1:
                                    options =
                                            new FirebaseTranslatorOptions.Builder()
                                                    .setSourceLanguage(FirebaseTranslateLanguage.EN)
                                                    .setTargetLanguage(FirebaseTranslateLanguage.BN)
                                                    .build();
                                    transLate(content);
                                    break;

                                case 2:
                                    options =
                                            new FirebaseTranslatorOptions.Builder()
                                                    .setSourceLanguage(FirebaseTranslateLanguage.EN)
                                                    .setTargetLanguage(FirebaseTranslateLanguage.JA)
                                                    .build();
                                    transLate(content);
                                    break;

                                case 3:
                                    options =
                                            new FirebaseTranslatorOptions.Builder()
                                                    .setSourceLanguage(FirebaseTranslateLanguage.EN)
                                                    .setTargetLanguage(FirebaseTranslateLanguage.ES)
                                                    .build();
                                    transLate(content);
                                    break;

                                case 4:
                                    options =
                                            new FirebaseTranslatorOptions.Builder()
                                                    .setSourceLanguage(FirebaseTranslateLanguage.EN)
                                                    .setTargetLanguage(FirebaseTranslateLanguage.AR)
                                                    .build();
                                    transLate(content);
                                    break;

                                case 5:
                                    options =
                                            new FirebaseTranslatorOptions.Builder()
                                                    .setSourceLanguage(FirebaseTranslateLanguage.EN)
                                                    .setTargetLanguage(FirebaseTranslateLanguage.HI)
                                                    .build();
                                    transLate(content);
                                    break;

                                case 6:
                                    options =
                                            new FirebaseTranslatorOptions.Builder()
                                                    .setSourceLanguage(FirebaseTranslateLanguage.EN)
                                                    .setTargetLanguage(FirebaseTranslateLanguage.ZH)
                                                    .build();
                                    transLate(content);
                                    break;
                                case 7:
                                    options =
                                            new FirebaseTranslatorOptions.Builder()
                                                    .setSourceLanguage(FirebaseTranslateLanguage.EN)
                                                    .setTargetLanguage(FirebaseTranslateLanguage.FR)
                                                    .build();
                                    transLate(content);
                                    break;
                                case 8:
                                    options =
                                            new FirebaseTranslatorOptions.Builder()
                                                    .setSourceLanguage(FirebaseTranslateLanguage.EN)
                                                    .setTargetLanguage(FirebaseTranslateLanguage.DE)
                                                    .build();
                                    transLate(content);
                                    break;
                                case 9:
                                    options =
                                            new FirebaseTranslatorOptions.Builder()
                                                    .setSourceLanguage(FirebaseTranslateLanguage.EN)
                                                    .setTargetLanguage(FirebaseTranslateLanguage.IT)
                                                    .build();
                                    transLate(content);
                                    break;
                                case 10:
                                    options =
                                            new FirebaseTranslatorOptions.Builder()
                                                    .setSourceLanguage(FirebaseTranslateLanguage.EN)
                                                    .setTargetLanguage(FirebaseTranslateLanguage.KO)
                                                    .build();
                                    transLate(content);
                                    break;
                                case 11:
                                    options =
                                            new FirebaseTranslatorOptions.Builder()
                                                    .setSourceLanguage(FirebaseTranslateLanguage.EN)
                                                    .setTargetLanguage(FirebaseTranslateLanguage.PT)
                                                    .build();
                                    transLate(content);
                                    break;

                                default:
                                    Toast.makeText(Translator.this, "Same languages chosen for translation From and translation To", Toast.LENGTH_SHORT).show();
                            }

                        }
                        if (num1 == 1) {
                            switch (num2) {

                                case 0:
                                    options =
                                            new FirebaseTranslatorOptions.Builder()
                                                    .setSourceLanguage(FirebaseTranslateLanguage.BN)
                                                    .setTargetLanguage(FirebaseTranslateLanguage.EN)
                                                    .build();
                                    transLate(content);
                                    break;

                                case 2:
                                    options =
                                            new FirebaseTranslatorOptions.Builder()
                                                    .setSourceLanguage(FirebaseTranslateLanguage.BN)
                                                    .setTargetLanguage(FirebaseTranslateLanguage.JA)
                                                    .build();
                                    transLate(content);
                                    break;

                                case 3:
                                    options =
                                            new FirebaseTranslatorOptions.Builder()
                                                    .setSourceLanguage(FirebaseTranslateLanguage.BN)
                                                    .setTargetLanguage(FirebaseTranslateLanguage.ES)
                                                    .build();
                                    transLate(content);
                                    break;

                                case 4:
                                    options =
                                            new FirebaseTranslatorOptions.Builder()
                                                    .setSourceLanguage(FirebaseTranslateLanguage.BN)
                                                    .setTargetLanguage(FirebaseTranslateLanguage.AR)
                                                    .build();
                                    transLate(content);
                                    break;

                                case 5:
                                    options =
                                            new FirebaseTranslatorOptions.Builder()
                                                    .setSourceLanguage(FirebaseTranslateLanguage.BN)
                                                    .setTargetLanguage(FirebaseTranslateLanguage.HI)
                                                    .build();
                                    transLate(content);
                                    break;
                                case 6:
                                    options =
                                            new FirebaseTranslatorOptions.Builder()
                                                    .setSourceLanguage(FirebaseTranslateLanguage.BN)
                                                    .setTargetLanguage(FirebaseTranslateLanguage.ZH)
                                                    .build();
                                    transLate(content);
                                    break;
                                case 7:
                                    options =
                                            new FirebaseTranslatorOptions.Builder()
                                                    .setSourceLanguage(FirebaseTranslateLanguage.BN)
                                                    .setTargetLanguage(FirebaseTranslateLanguage.FR)
                                                    .build();
                                    transLate(content);
                                    break;
                                case 8:
                                    options =
                                            new FirebaseTranslatorOptions.Builder()
                                                    .setSourceLanguage(FirebaseTranslateLanguage.BN)
                                                    .setTargetLanguage(FirebaseTranslateLanguage.DE)
                                                    .build();
                                    transLate(content);
                                    break;
                                case 9:
                                    options =
                                            new FirebaseTranslatorOptions.Builder()
                                                    .setSourceLanguage(FirebaseTranslateLanguage.BN)
                                                    .setTargetLanguage(FirebaseTranslateLanguage.IT)
                                                    .build();
                                    transLate(content);
                                    break;
                                case 10:
                                    options =
                                            new FirebaseTranslatorOptions.Builder()
                                                    .setSourceLanguage(FirebaseTranslateLanguage.BN)
                                                    .setTargetLanguage(FirebaseTranslateLanguage.KO)
                                                    .build();
                                    transLate(content);
                                    break;
                                case 11:
                                    options =
                                            new FirebaseTranslatorOptions.Builder()
                                                    .setSourceLanguage(FirebaseTranslateLanguage.BN)
                                                    .setTargetLanguage(FirebaseTranslateLanguage.PT)
                                                    .build();
                                    transLate(content);
                                    break;

                                default:
                                    Toast.makeText(Translator.this, "Same languages chosen for translation From and translation To", Toast.LENGTH_SHORT).show();
                            }

                        }
                        if (num1 == 2) {
                            switch (num2) {

                                case 1:
                                    options =
                                            new FirebaseTranslatorOptions.Builder()
                                                    .setSourceLanguage(FirebaseTranslateLanguage.JA)
                                                    .setTargetLanguage(FirebaseTranslateLanguage.BN)
                                                    .build();
                                    transLate(content);
                                    break;

                                case 0:
                                    options =
                                            new FirebaseTranslatorOptions.Builder()
                                                    .setSourceLanguage(FirebaseTranslateLanguage.JA)
                                                    .setTargetLanguage(FirebaseTranslateLanguage.EN)
                                                    .build();
                                    transLate(content);
                                    break;

                                case 3:
                                    options =
                                            new FirebaseTranslatorOptions.Builder()
                                                    .setSourceLanguage(FirebaseTranslateLanguage.JA)
                                                    .setTargetLanguage(FirebaseTranslateLanguage.ES)
                                                    .build();
                                    transLate(content);
                                    break;

                                case 4:
                                    options =
                                            new FirebaseTranslatorOptions.Builder()
                                                    .setSourceLanguage(FirebaseTranslateLanguage.JA)
                                                    .setTargetLanguage(FirebaseTranslateLanguage.AR)
                                                    .build();
                                    transLate(content);
                                    break;

                                case 5:
                                    options =
                                            new FirebaseTranslatorOptions.Builder()
                                                    .setSourceLanguage(FirebaseTranslateLanguage.JA)
                                                    .setTargetLanguage(FirebaseTranslateLanguage.HI)
                                                    .build();
                                    transLate(content);
                                    break;
                                case 6:
                                    options =
                                            new FirebaseTranslatorOptions.Builder()
                                                    .setSourceLanguage(FirebaseTranslateLanguage.JA)
                                                    .setTargetLanguage(FirebaseTranslateLanguage.ZH)
                                                    .build();
                                    transLate(content);
                                    break;
                                case 7:
                                    options =
                                            new FirebaseTranslatorOptions.Builder()
                                                    .setSourceLanguage(FirebaseTranslateLanguage.JA)
                                                    .setTargetLanguage(FirebaseTranslateLanguage.FR)
                                                    .build();
                                    transLate(content);
                                    break;
                                case 8:
                                    options =
                                            new FirebaseTranslatorOptions.Builder()
                                                    .setSourceLanguage(FirebaseTranslateLanguage.JA)
                                                    .setTargetLanguage(FirebaseTranslateLanguage.DE)
                                                    .build();
                                    transLate(content);
                                    break;
                                case 9:
                                    options =
                                            new FirebaseTranslatorOptions.Builder()
                                                    .setSourceLanguage(FirebaseTranslateLanguage.JA)
                                                    .setTargetLanguage(FirebaseTranslateLanguage.IT)
                                                    .build();
                                    transLate(content);
                                    break;
                                case 10:
                                    options =
                                            new FirebaseTranslatorOptions.Builder()
                                                    .setSourceLanguage(FirebaseTranslateLanguage.JA)
                                                    .setTargetLanguage(FirebaseTranslateLanguage.KO)
                                                    .build();
                                    transLate(content);
                                    break;
                                case 11:
                                    options =
                                            new FirebaseTranslatorOptions.Builder()
                                                    .setSourceLanguage(FirebaseTranslateLanguage.JA)
                                                    .setTargetLanguage(FirebaseTranslateLanguage.PT)
                                                    .build();
                                    transLate(content);
                                    break;

                                default:
                                    Toast.makeText(Translator.this, "Same languages chosen for translation From and translation To", Toast.LENGTH_SHORT).show();
                            }

                        }
                        if (num1 == 3) {
                            switch (num2) {

                                case 1:
                                    options =
                                            new FirebaseTranslatorOptions.Builder()
                                                    .setSourceLanguage(FirebaseTranslateLanguage.ES)
                                                    .setTargetLanguage(FirebaseTranslateLanguage.BN)
                                                    .build();
                                    transLate(content);
                                    break;

                                case 2:
                                    options =
                                            new FirebaseTranslatorOptions.Builder()
                                                    .setSourceLanguage(FirebaseTranslateLanguage.ES)
                                                    .setTargetLanguage(FirebaseTranslateLanguage.JA)
                                                    .build();
                                    transLate(content);
                                    break;

                                case 0:
                                    options =
                                            new FirebaseTranslatorOptions.Builder()
                                                    .setSourceLanguage(FirebaseTranslateLanguage.ES)
                                                    .setTargetLanguage(FirebaseTranslateLanguage.EN)
                                                    .build();
                                    transLate(content);
                                    break;

                                case 4:
                                    options =
                                            new FirebaseTranslatorOptions.Builder()
                                                    .setSourceLanguage(FirebaseTranslateLanguage.ES)
                                                    .setTargetLanguage(FirebaseTranslateLanguage.AR)
                                                    .build();
                                    transLate(content);
                                    break;

                                case 5:
                                    options =
                                            new FirebaseTranslatorOptions.Builder()
                                                    .setSourceLanguage(FirebaseTranslateLanguage.ES)
                                                    .setTargetLanguage(FirebaseTranslateLanguage.HI)
                                                    .build();
                                    transLate(content);
                                    break;
                                case 6:
                                    options =
                                            new FirebaseTranslatorOptions.Builder()
                                                    .setSourceLanguage(FirebaseTranslateLanguage.ES)
                                                    .setTargetLanguage(FirebaseTranslateLanguage.ZH)
                                                    .build();
                                    transLate(content);
                                    break;
                                case 7:
                                    options =
                                            new FirebaseTranslatorOptions.Builder()
                                                    .setSourceLanguage(FirebaseTranslateLanguage.ES)
                                                    .setTargetLanguage(FirebaseTranslateLanguage.FR)
                                                    .build();
                                    transLate(content);
                                    break;
                                case 8:
                                    options =
                                            new FirebaseTranslatorOptions.Builder()
                                                    .setSourceLanguage(FirebaseTranslateLanguage.ES)
                                                    .setTargetLanguage(FirebaseTranslateLanguage.DE)
                                                    .build();
                                    transLate(content);
                                    break;
                                case 9:
                                    options =
                                            new FirebaseTranslatorOptions.Builder()
                                                    .setSourceLanguage(FirebaseTranslateLanguage.ES)
                                                    .setTargetLanguage(FirebaseTranslateLanguage.IT)
                                                    .build();
                                    transLate(content);
                                    break;
                                case 10:
                                    options =
                                            new FirebaseTranslatorOptions.Builder()
                                                    .setSourceLanguage(FirebaseTranslateLanguage.ES)
                                                    .setTargetLanguage(FirebaseTranslateLanguage.KO)
                                                    .build();
                                    transLate(content);
                                    break;
                                case 11:
                                    options =
                                            new FirebaseTranslatorOptions.Builder()
                                                    .setSourceLanguage(FirebaseTranslateLanguage.ES)
                                                    .setTargetLanguage(FirebaseTranslateLanguage.PT)
                                                    .build();
                                    transLate(content);
                                    break;

                                default:
                                    Toast.makeText(Translator.this, "Same languages chosen for translation From and translation To", Toast.LENGTH_SHORT).show();
                            }

                        }
                        if (num1 == 4) {
                            switch (num2) {

                                case 1:
                                    options =
                                            new FirebaseTranslatorOptions.Builder()
                                                    .setSourceLanguage(FirebaseTranslateLanguage.AR)
                                                    .setTargetLanguage(FirebaseTranslateLanguage.BN)
                                                    .build();
                                    transLate(content);
                                    break;

                                case 2:
                                    options =
                                            new FirebaseTranslatorOptions.Builder()
                                                    .setSourceLanguage(FirebaseTranslateLanguage.AR)
                                                    .setTargetLanguage(FirebaseTranslateLanguage.JA)
                                                    .build();
                                    transLate(content);
                                    break;

                                case 3:
                                    options =
                                            new FirebaseTranslatorOptions.Builder()
                                                    .setSourceLanguage(FirebaseTranslateLanguage.AR)
                                                    .setTargetLanguage(FirebaseTranslateLanguage.ES)
                                                    .build();
                                    transLate(content);
                                    break;

                                case 0:
                                    options =
                                            new FirebaseTranslatorOptions.Builder()
                                                    .setSourceLanguage(FirebaseTranslateLanguage.AR)
                                                    .setTargetLanguage(FirebaseTranslateLanguage.EN)
                                                    .build();
                                    transLate(content);
                                    break;

                                case 5:
                                    options =
                                            new FirebaseTranslatorOptions.Builder()
                                                    .setSourceLanguage(FirebaseTranslateLanguage.AR)
                                                    .setTargetLanguage(FirebaseTranslateLanguage.HI)
                                                    .build();
                                    transLate(content);
                                    break;
                                case 6:
                                    options =
                                            new FirebaseTranslatorOptions.Builder()
                                                    .setSourceLanguage(FirebaseTranslateLanguage.AR)
                                                    .setTargetLanguage(FirebaseTranslateLanguage.ZH)
                                                    .build();
                                    transLate(content);
                                    break;
                                case 7:
                                    options =
                                            new FirebaseTranslatorOptions.Builder()
                                                    .setSourceLanguage(FirebaseTranslateLanguage.AR)
                                                    .setTargetLanguage(FirebaseTranslateLanguage.FR)
                                                    .build();
                                    transLate(content);
                                    break;
                                case 8:
                                    options =
                                            new FirebaseTranslatorOptions.Builder()
                                                    .setSourceLanguage(FirebaseTranslateLanguage.AR)
                                                    .setTargetLanguage(FirebaseTranslateLanguage.DE)
                                                    .build();
                                    transLate(content);
                                    break;
                                case 9:
                                    options =
                                            new FirebaseTranslatorOptions.Builder()
                                                    .setSourceLanguage(FirebaseTranslateLanguage.AR)
                                                    .setTargetLanguage(FirebaseTranslateLanguage.IT)
                                                    .build();
                                    transLate(content);
                                    break;
                                case 10:
                                    options =
                                            new FirebaseTranslatorOptions.Builder()
                                                    .setSourceLanguage(FirebaseTranslateLanguage.AR)
                                                    .setTargetLanguage(FirebaseTranslateLanguage.KO)
                                                    .build();
                                    transLate(content);
                                    break;
                                case 11:
                                    options =
                                            new FirebaseTranslatorOptions.Builder()
                                                    .setSourceLanguage(FirebaseTranslateLanguage.AR)
                                                    .setTargetLanguage(FirebaseTranslateLanguage.PT)
                                                    .build();
                                    transLate(content);
                                    break;

                                default:
                                    Toast.makeText(Translator.this, "Same languages chosen for translation From and translation To", Toast.LENGTH_SHORT).show();
                            }

                        }
                        if (num1 == 5) {
                            switch (num2) {

                                case 1:
                                    options =
                                            new FirebaseTranslatorOptions.Builder()
                                                    .setSourceLanguage(FirebaseTranslateLanguage.HI)
                                                    .setTargetLanguage(FirebaseTranslateLanguage.BN)
                                                    .build();
                                    transLate(content);
                                    break;

                                case 2:
                                    options =
                                            new FirebaseTranslatorOptions.Builder()
                                                    .setSourceLanguage(FirebaseTranslateLanguage.HI)
                                                    .setTargetLanguage(FirebaseTranslateLanguage.JA)
                                                    .build();
                                    transLate(content);
                                    break;

                                case 3:
                                    options =
                                            new FirebaseTranslatorOptions.Builder()
                                                    .setSourceLanguage(FirebaseTranslateLanguage.HI)
                                                    .setTargetLanguage(FirebaseTranslateLanguage.ES)
                                                    .build();
                                    transLate(content);
                                    break;

                                case 4:
                                    options =
                                            new FirebaseTranslatorOptions.Builder()
                                                    .setSourceLanguage(FirebaseTranslateLanguage.HI)
                                                    .setTargetLanguage(FirebaseTranslateLanguage.AR)
                                                    .build();
                                    transLate(content);
                                    break;

                                case 0:
                                    options =
                                            new FirebaseTranslatorOptions.Builder()
                                                    .setSourceLanguage(FirebaseTranslateLanguage.HI)
                                                    .setTargetLanguage(FirebaseTranslateLanguage.EN)
                                                    .build();
                                    transLate(content);
                                    break;
                                case 6:
                                    options =
                                            new FirebaseTranslatorOptions.Builder()
                                                    .setSourceLanguage(FirebaseTranslateLanguage.HI)
                                                    .setTargetLanguage(FirebaseTranslateLanguage.ZH)
                                                    .build();
                                    transLate(content);
                                    break;
                                case 7:
                                    options =
                                            new FirebaseTranslatorOptions.Builder()
                                                    .setSourceLanguage(FirebaseTranslateLanguage.HI)
                                                    .setTargetLanguage(FirebaseTranslateLanguage.FR)
                                                    .build();
                                    transLate(content);
                                    break;
                                case 8:
                                    options =
                                            new FirebaseTranslatorOptions.Builder()
                                                    .setSourceLanguage(FirebaseTranslateLanguage.HI)
                                                    .setTargetLanguage(FirebaseTranslateLanguage.DE)
                                                    .build();
                                    transLate(content);
                                    break;
                                case 9:
                                    options =
                                            new FirebaseTranslatorOptions.Builder()
                                                    .setSourceLanguage(FirebaseTranslateLanguage.HI)
                                                    .setTargetLanguage(FirebaseTranslateLanguage.IT)
                                                    .build();
                                    transLate(content);
                                    break;
                                case 10:
                                    options =
                                            new FirebaseTranslatorOptions.Builder()
                                                    .setSourceLanguage(FirebaseTranslateLanguage.HI)
                                                    .setTargetLanguage(FirebaseTranslateLanguage.KO)
                                                    .build();
                                    transLate(content);
                                    break;
                                case 11:
                                    options =
                                            new FirebaseTranslatorOptions.Builder()
                                                    .setSourceLanguage(FirebaseTranslateLanguage.HI)
                                                    .setTargetLanguage(FirebaseTranslateLanguage.PT)
                                                    .build();
                                    transLate(content);
                                    break;

                                default:
                                    Toast.makeText(Translator.this, "Same languages chosen for translation From and translation To", Toast.LENGTH_SHORT).show();
                            }
                        }
                        if (num1 == 7) {
                            switch (num2) {

                                case 1:
                                    options =
                                            new FirebaseTranslatorOptions.Builder()
                                                    .setSourceLanguage(FirebaseTranslateLanguage.FR)
                                                    .setTargetLanguage(FirebaseTranslateLanguage.BN)
                                                    .build();
                                    transLate(content);
                                    break;

                                case 2:
                                    options =
                                            new FirebaseTranslatorOptions.Builder()
                                                    .setSourceLanguage(FirebaseTranslateLanguage.FR)
                                                    .setTargetLanguage(FirebaseTranslateLanguage.JA)
                                                    .build();
                                    transLate(content);
                                    break;

                                case 3:
                                    options =
                                            new FirebaseTranslatorOptions.Builder()
                                                    .setSourceLanguage(FirebaseTranslateLanguage.FR)
                                                    .setTargetLanguage(FirebaseTranslateLanguage.ES)
                                                    .build();
                                    transLate(content);
                                    break;

                                case 4:
                                    options =
                                            new FirebaseTranslatorOptions.Builder()
                                                    .setSourceLanguage(FirebaseTranslateLanguage.FR)
                                                    .setTargetLanguage(FirebaseTranslateLanguage.AR)
                                                    .build();
                                    transLate(content);
                                    break;

                                case 5:
                                    options =
                                            new FirebaseTranslatorOptions.Builder()
                                                    .setSourceLanguage(FirebaseTranslateLanguage.FR)
                                                    .setTargetLanguage(FirebaseTranslateLanguage.HI)
                                                    .build();
                                    transLate(content);
                                    break;

                                case 6:
                                    options =
                                            new FirebaseTranslatorOptions.Builder()
                                                    .setSourceLanguage(FirebaseTranslateLanguage.FR)
                                                    .setTargetLanguage(FirebaseTranslateLanguage.ZH)
                                                    .build();
                                    transLate(content);
                                    break;
                                case 0:
                                    options =
                                            new FirebaseTranslatorOptions.Builder()
                                                    .setSourceLanguage(FirebaseTranslateLanguage.FR)
                                                    .setTargetLanguage(FirebaseTranslateLanguage.EN)
                                                    .build();
                                    transLate(content);
                                    break;
                                case 8:
                                    options =
                                            new FirebaseTranslatorOptions.Builder()
                                                    .setSourceLanguage(FirebaseTranslateLanguage.FR)
                                                    .setTargetLanguage(FirebaseTranslateLanguage.DE)
                                                    .build();
                                    transLate(content);
                                    break;
                                case 9:
                                    options =
                                            new FirebaseTranslatorOptions.Builder()
                                                    .setSourceLanguage(FirebaseTranslateLanguage.FR)
                                                    .setTargetLanguage(FirebaseTranslateLanguage.IT)
                                                    .build();
                                    transLate(content);
                                    break;
                                case 10:
                                    options =
                                            new FirebaseTranslatorOptions.Builder()
                                                    .setSourceLanguage(FirebaseTranslateLanguage.FR)
                                                    .setTargetLanguage(FirebaseTranslateLanguage.KO)
                                                    .build();
                                    transLate(content);
                                    break;
                                case 11:
                                    options =
                                            new FirebaseTranslatorOptions.Builder()
                                                    .setSourceLanguage(FirebaseTranslateLanguage.FR)
                                                    .setTargetLanguage(FirebaseTranslateLanguage.PT)
                                                    .build();
                                    transLate(content);
                                    break;

                                default:
                                    Toast.makeText(Translator.this, "Same languages chosen for translation From and translation To", Toast.LENGTH_SHORT).show();
                            }

                        }
                        if (num1 == 6) {
                            switch (num2) {

                                case 1:
                                    options =
                                            new FirebaseTranslatorOptions.Builder()
                                                    .setSourceLanguage(FirebaseTranslateLanguage.ZH)
                                                    .setTargetLanguage(FirebaseTranslateLanguage.BN)
                                                    .build();
                                    transLate(content);
                                    break;

                                case 2:
                                    options =
                                            new FirebaseTranslatorOptions.Builder()
                                                    .setSourceLanguage(FirebaseTranslateLanguage.ZH)
                                                    .setTargetLanguage(FirebaseTranslateLanguage.JA)
                                                    .build();
                                    transLate(content);
                                    break;

                                case 3:
                                    options =
                                            new FirebaseTranslatorOptions.Builder()
                                                    .setSourceLanguage(FirebaseTranslateLanguage.ZH)
                                                    .setTargetLanguage(FirebaseTranslateLanguage.ES)
                                                    .build();
                                    transLate(content);
                                    break;

                                case 4:
                                    options =
                                            new FirebaseTranslatorOptions.Builder()
                                                    .setSourceLanguage(FirebaseTranslateLanguage.ZH)
                                                    .setTargetLanguage(FirebaseTranslateLanguage.AR)
                                                    .build();
                                    transLate(content);
                                    break;

                                case 5:
                                    options =
                                            new FirebaseTranslatorOptions.Builder()
                                                    .setSourceLanguage(FirebaseTranslateLanguage.ZH)
                                                    .setTargetLanguage(FirebaseTranslateLanguage.HI)
                                                    .build();
                                    transLate(content);
                                    break;

                                case 0:
                                    options =
                                            new FirebaseTranslatorOptions.Builder()
                                                    .setSourceLanguage(FirebaseTranslateLanguage.ZH)
                                                    .setTargetLanguage(FirebaseTranslateLanguage.EN)
                                                    .build();
                                    transLate(content);
                                    break;
                                case 7:
                                    options =
                                            new FirebaseTranslatorOptions.Builder()
                                                    .setSourceLanguage(FirebaseTranslateLanguage.ZH)
                                                    .setTargetLanguage(FirebaseTranslateLanguage.FR)
                                                    .build();
                                    transLate(content);
                                    break;
                                case 8:
                                    options =
                                            new FirebaseTranslatorOptions.Builder()
                                                    .setSourceLanguage(FirebaseTranslateLanguage.ZH)
                                                    .setTargetLanguage(FirebaseTranslateLanguage.DE)
                                                    .build();
                                    transLate(content);
                                    break;
                                case 9:
                                    options =
                                            new FirebaseTranslatorOptions.Builder()
                                                    .setSourceLanguage(FirebaseTranslateLanguage.ZH)
                                                    .setTargetLanguage(FirebaseTranslateLanguage.IT)
                                                    .build();
                                    transLate(content);
                                    break;
                                case 10:
                                    options =
                                            new FirebaseTranslatorOptions.Builder()
                                                    .setSourceLanguage(FirebaseTranslateLanguage.ZH)
                                                    .setTargetLanguage(FirebaseTranslateLanguage.KO)
                                                    .build();
                                    transLate(content);
                                    break;
                                case 11:
                                    options =
                                            new FirebaseTranslatorOptions.Builder()
                                                    .setSourceLanguage(FirebaseTranslateLanguage.ZH)
                                                    .setTargetLanguage(FirebaseTranslateLanguage.PT)
                                                    .build();
                                    transLate(content);
                                    break;

                                default:
                                    Toast.makeText(Translator.this, "Same languages chosen for translation From and translation To", Toast.LENGTH_SHORT).show();
                            }

                        }
                        if (num1 == 8) {
                            switch (num2) {

                                case 1:
                                    options =
                                            new FirebaseTranslatorOptions.Builder()
                                                    .setSourceLanguage(FirebaseTranslateLanguage.DE)
                                                    .setTargetLanguage(FirebaseTranslateLanguage.BN)
                                                    .build();
                                    transLate(content);
                                    break;

                                case 2:
                                    options =
                                            new FirebaseTranslatorOptions.Builder()
                                                    .setSourceLanguage(FirebaseTranslateLanguage.DE)
                                                    .setTargetLanguage(FirebaseTranslateLanguage.JA)
                                                    .build();
                                    transLate(content);
                                    break;

                                case 3:
                                    options =
                                            new FirebaseTranslatorOptions.Builder()
                                                    .setSourceLanguage(FirebaseTranslateLanguage.DE)
                                                    .setTargetLanguage(FirebaseTranslateLanguage.ES)
                                                    .build();
                                    transLate(content);
                                    break;

                                case 4:
                                    options =
                                            new FirebaseTranslatorOptions.Builder()
                                                    .setSourceLanguage(FirebaseTranslateLanguage.DE)
                                                    .setTargetLanguage(FirebaseTranslateLanguage.AR)
                                                    .build();
                                    transLate(content);
                                    break;

                                case 5:
                                    options =
                                            new FirebaseTranslatorOptions.Builder()
                                                    .setSourceLanguage(FirebaseTranslateLanguage.DE)
                                                    .setTargetLanguage(FirebaseTranslateLanguage.HI)
                                                    .build();
                                    transLate(content);
                                    break;

                                case 6:
                                    options =
                                            new FirebaseTranslatorOptions.Builder()
                                                    .setSourceLanguage(FirebaseTranslateLanguage.DE)
                                                    .setTargetLanguage(FirebaseTranslateLanguage.ZH)
                                                    .build();
                                    transLate(content);
                                    break;
                                case 7:
                                    options =
                                            new FirebaseTranslatorOptions.Builder()
                                                    .setSourceLanguage(FirebaseTranslateLanguage.DE)
                                                    .setTargetLanguage(FirebaseTranslateLanguage.FR)
                                                    .build();
                                    transLate(content);
                                    break;
                                case 0:
                                    options =
                                            new FirebaseTranslatorOptions.Builder()
                                                    .setSourceLanguage(FirebaseTranslateLanguage.DE)
                                                    .setTargetLanguage(FirebaseTranslateLanguage.EN)
                                                    .build();
                                    transLate(content);
                                    break;
                                case 9:
                                    options =
                                            new FirebaseTranslatorOptions.Builder()
                                                    .setSourceLanguage(FirebaseTranslateLanguage.DE)
                                                    .setTargetLanguage(FirebaseTranslateLanguage.IT)
                                                    .build();
                                    transLate(content);
                                    break;
                                case 10:
                                    options =
                                            new FirebaseTranslatorOptions.Builder()
                                                    .setSourceLanguage(FirebaseTranslateLanguage.DE)
                                                    .setTargetLanguage(FirebaseTranslateLanguage.KO)
                                                    .build();
                                    transLate(content);
                                    break;
                                case 11:
                                    options =
                                            new FirebaseTranslatorOptions.Builder()
                                                    .setSourceLanguage(FirebaseTranslateLanguage.DE)
                                                    .setTargetLanguage(FirebaseTranslateLanguage.PT)
                                                    .build();
                                    transLate(content);
                                    break;

                                default:
                                    Toast.makeText(Translator.this, "Same languages chosen for translation From and translation To", Toast.LENGTH_SHORT).show();
                            }

                        }
                        if (num1 == 9) {
                            switch (num2) {

                                case 1:
                                    options =
                                            new FirebaseTranslatorOptions.Builder()
                                                    .setSourceLanguage(FirebaseTranslateLanguage.IT)
                                                    .setTargetLanguage(FirebaseTranslateLanguage.BN)
                                                    .build();
                                    transLate(content);
                                    break;

                                case 2:
                                    options =
                                            new FirebaseTranslatorOptions.Builder()
                                                    .setSourceLanguage(FirebaseTranslateLanguage.IT)
                                                    .setTargetLanguage(FirebaseTranslateLanguage.JA)
                                                    .build();
                                    transLate(content);
                                    break;

                                case 3:
                                    options =
                                            new FirebaseTranslatorOptions.Builder()
                                                    .setSourceLanguage(FirebaseTranslateLanguage.IT)
                                                    .setTargetLanguage(FirebaseTranslateLanguage.ES)
                                                    .build();
                                    transLate(content);
                                    break;

                                case 4:
                                    options =
                                            new FirebaseTranslatorOptions.Builder()
                                                    .setSourceLanguage(FirebaseTranslateLanguage.IT)
                                                    .setTargetLanguage(FirebaseTranslateLanguage.AR)
                                                    .build();
                                    transLate(content);
                                    break;

                                case 5:
                                    options =
                                            new FirebaseTranslatorOptions.Builder()
                                                    .setSourceLanguage(FirebaseTranslateLanguage.IT)
                                                    .setTargetLanguage(FirebaseTranslateLanguage.HI)
                                                    .build();
                                    transLate(content);
                                    break;

                                case 6:
                                    options =
                                            new FirebaseTranslatorOptions.Builder()
                                                    .setSourceLanguage(FirebaseTranslateLanguage.IT)
                                                    .setTargetLanguage(FirebaseTranslateLanguage.ZH)
                                                    .build();
                                    transLate(content);
                                    break;
                                case 7:
                                    options =
                                            new FirebaseTranslatorOptions.Builder()
                                                    .setSourceLanguage(FirebaseTranslateLanguage.IT)
                                                    .setTargetLanguage(FirebaseTranslateLanguage.FR)
                                                    .build();
                                    transLate(content);
                                    break;
                                case 8:
                                    options =
                                            new FirebaseTranslatorOptions.Builder()
                                                    .setSourceLanguage(FirebaseTranslateLanguage.IT)
                                                    .setTargetLanguage(FirebaseTranslateLanguage.DE)
                                                    .build();
                                    transLate(content);
                                    break;
                                case 0:
                                    options =
                                            new FirebaseTranslatorOptions.Builder()
                                                    .setSourceLanguage(FirebaseTranslateLanguage.IT)
                                                    .setTargetLanguage(FirebaseTranslateLanguage.EN)
                                                    .build();
                                    transLate(content);
                                    break;
                                case 10:
                                    options =
                                            new FirebaseTranslatorOptions.Builder()
                                                    .setSourceLanguage(FirebaseTranslateLanguage.IT)
                                                    .setTargetLanguage(FirebaseTranslateLanguage.KO)
                                                    .build();
                                    transLate(content);
                                    break;
                                case 11:
                                    options =
                                            new FirebaseTranslatorOptions.Builder()
                                                    .setSourceLanguage(FirebaseTranslateLanguage.IT)
                                                    .setTargetLanguage(FirebaseTranslateLanguage.PT)
                                                    .build();
                                    transLate(content);
                                    break;

                                default:
                                    Toast.makeText(Translator.this, "Same languages chosen for translation From and translation To", Toast.LENGTH_SHORT).show();
                            }

                        }
                        if (num1 == 10) {
                            switch (num2) {

                                case 1:
                                    options =
                                            new FirebaseTranslatorOptions.Builder()
                                                    .setSourceLanguage(FirebaseTranslateLanguage.KO)
                                                    .setTargetLanguage(FirebaseTranslateLanguage.BN)
                                                    .build();
                                    transLate(content);
                                    break;

                                case 2:
                                    options =
                                            new FirebaseTranslatorOptions.Builder()
                                                    .setSourceLanguage(FirebaseTranslateLanguage.KO)
                                                    .setTargetLanguage(FirebaseTranslateLanguage.JA)
                                                    .build();
                                    transLate(content);
                                    break;

                                case 3:
                                    options =
                                            new FirebaseTranslatorOptions.Builder()
                                                    .setSourceLanguage(FirebaseTranslateLanguage.KO)
                                                    .setTargetLanguage(FirebaseTranslateLanguage.ES)
                                                    .build();
                                    transLate(content);
                                    break;

                                case 4:
                                    options =
                                            new FirebaseTranslatorOptions.Builder()
                                                    .setSourceLanguage(FirebaseTranslateLanguage.KO)
                                                    .setTargetLanguage(FirebaseTranslateLanguage.AR)
                                                    .build();
                                    transLate(content);
                                    break;

                                case 5:
                                    options =
                                            new FirebaseTranslatorOptions.Builder()
                                                    .setSourceLanguage(FirebaseTranslateLanguage.KO)
                                                    .setTargetLanguage(FirebaseTranslateLanguage.HI)
                                                    .build();
                                    transLate(content);
                                    break;

                                case 6:
                                    options =
                                            new FirebaseTranslatorOptions.Builder()
                                                    .setSourceLanguage(FirebaseTranslateLanguage.KO)
                                                    .setTargetLanguage(FirebaseTranslateLanguage.ZH)
                                                    .build();
                                    transLate(content);
                                    break;
                                case 7:
                                    options =
                                            new FirebaseTranslatorOptions.Builder()
                                                    .setSourceLanguage(FirebaseTranslateLanguage.KO)
                                                    .setTargetLanguage(FirebaseTranslateLanguage.FR)
                                                    .build();
                                    transLate(content);
                                    break;
                                case 8:
                                    options =
                                            new FirebaseTranslatorOptions.Builder()
                                                    .setSourceLanguage(FirebaseTranslateLanguage.KO)
                                                    .setTargetLanguage(FirebaseTranslateLanguage.DE)
                                                    .build();
                                    transLate(content);
                                    break;
                                case 9:
                                    options =
                                            new FirebaseTranslatorOptions.Builder()
                                                    .setSourceLanguage(FirebaseTranslateLanguage.KO)
                                                    .setTargetLanguage(FirebaseTranslateLanguage.IT)
                                                    .build();
                                    transLate(content);
                                    break;
                                case 0:
                                    options =
                                            new FirebaseTranslatorOptions.Builder()
                                                    .setSourceLanguage(FirebaseTranslateLanguage.KO)
                                                    .setTargetLanguage(FirebaseTranslateLanguage.EN)
                                                    .build();
                                    transLate(content);
                                    break;
                                case 11:
                                    options =
                                            new FirebaseTranslatorOptions.Builder()
                                                    .setSourceLanguage(FirebaseTranslateLanguage.KO)
                                                    .setTargetLanguage(FirebaseTranslateLanguage.PT)
                                                    .build();
                                    transLate(content);
                                    break;

                                default:
                                    Toast.makeText(Translator.this, "Same languages chosen for translation From and translation To", Toast.LENGTH_SHORT).show();
                            }

                        }
                        if (num1 == 11) {
                            switch (num2) {

                                case 1:
                                    options =
                                            new FirebaseTranslatorOptions.Builder()
                                                    .setSourceLanguage(FirebaseTranslateLanguage.PT)
                                                    .setTargetLanguage(FirebaseTranslateLanguage.BN)
                                                    .build();
                                    transLate(content);
                                    break;

                                case 2:
                                    options =
                                            new FirebaseTranslatorOptions.Builder()
                                                    .setSourceLanguage(FirebaseTranslateLanguage.PT)
                                                    .setTargetLanguage(FirebaseTranslateLanguage.JA)
                                                    .build();
                                    transLate(content);
                                    break;

                                case 3:
                                    options =
                                            new FirebaseTranslatorOptions.Builder()
                                                    .setSourceLanguage(FirebaseTranslateLanguage.PT)
                                                    .setTargetLanguage(FirebaseTranslateLanguage.ES)
                                                    .build();
                                    transLate(content);
                                    break;

                                case 4:
                                    options =
                                            new FirebaseTranslatorOptions.Builder()
                                                    .setSourceLanguage(FirebaseTranslateLanguage.PT)
                                                    .setTargetLanguage(FirebaseTranslateLanguage.AR)
                                                    .build();
                                    transLate(content);
                                    break;

                                case 5:
                                    options =
                                            new FirebaseTranslatorOptions.Builder()
                                                    .setSourceLanguage(FirebaseTranslateLanguage.PT)
                                                    .setTargetLanguage(FirebaseTranslateLanguage.HI)
                                                    .build();
                                    transLate(content);
                                    break;

                                case 6:
                                    options =
                                            new FirebaseTranslatorOptions.Builder()
                                                    .setSourceLanguage(FirebaseTranslateLanguage.PT)
                                                    .setTargetLanguage(FirebaseTranslateLanguage.ZH)
                                                    .build();
                                    transLate(content);
                                    break;
                                case 7:
                                    options =
                                            new FirebaseTranslatorOptions.Builder()
                                                    .setSourceLanguage(FirebaseTranslateLanguage.PT)
                                                    .setTargetLanguage(FirebaseTranslateLanguage.FR)
                                                    .build();
                                    transLate(content);
                                    break;
                                case 8:
                                    options =
                                            new FirebaseTranslatorOptions.Builder()
                                                    .setSourceLanguage(FirebaseTranslateLanguage.PT)
                                                    .setTargetLanguage(FirebaseTranslateLanguage.DE)
                                                    .build();
                                    transLate(content);
                                    break;
                                case 9:
                                    options =
                                            new FirebaseTranslatorOptions.Builder()
                                                    .setSourceLanguage(FirebaseTranslateLanguage.PT)
                                                    .setTargetLanguage(FirebaseTranslateLanguage.IT)
                                                    .build();
                                    transLate(content);
                                    break;
                                case 10:
                                    options =
                                            new FirebaseTranslatorOptions.Builder()
                                                    .setSourceLanguage(FirebaseTranslateLanguage.PT)
                                                    .setTargetLanguage(FirebaseTranslateLanguage.KO)
                                                    .build();
                                    transLate(content);
                                    break;
                                case 0:
                                    options =
                                            new FirebaseTranslatorOptions.Builder()
                                                    .setSourceLanguage(FirebaseTranslateLanguage.PT)
                                                    .setTargetLanguage(FirebaseTranslateLanguage.EN)
                                                    .build();
                                    transLate(content);
                                    break;

                                default:
                                    Toast.makeText(Translator.this, "Same languages chosen for translation From and translation To", Toast.LENGTH_SHORT).show();
                            }

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(Translator.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(Translator.this, "Text to be translated couldn't be found", Toast.LENGTH_SHORT).show();
                }
            }
        });

        if (savedInstanceState != null) {
            num1 = savedInstanceState.getInt("num1");
            num2 = savedInstanceState.getInt("num2");
            numberPicker1.setValue(num1);
            numberPicker2.setValue(num2);
            content = savedInstanceState.getString("edit1");
            from.setText(content);
            to.setText(savedInstanceState.getString("edit2"));
            vis = savedInstanceState.getBoolean("vis");

            if (vis) {
                transButton.setVisibility(View.VISIBLE);
                transButton.setEnabled(true);
            } else {
                transButton.setEnabled(false);
                transButton.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString("edit1", from.getText().toString());
        outState.putString("edit2", to.getText().toString());
        outState.putInt("num1", num1);
        outState.putInt("num2", num2);
        outState.putBoolean("vis", vis);
    }

    public void transLate(String contentfinal) {


        to.setTextColor(getResources().getColor(R.color.green));
        to.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f);
        to.setText(getResources().getString(R.string.translateTo));

        contentfinal = contentfinal.replace("~", "-");
        contentfinal = contentfinal.replace("\n", "~");

        englishGermanTranslator =
                FirebaseNaturalLanguage.getInstance().getTranslator(options);

        String finalContentfinal = contentfinal;

        englishGermanTranslator.downloadModelIfNeeded(conditions)
                .addOnSuccessListener(
                        new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void v) {
                                Toast.makeText(Translator.this, "Translation Started!", Toast.LENGTH_SHORT).show();
                                englishGermanTranslator.translate(finalContentfinal)
                                        .addOnSuccessListener(
                                                new OnSuccessListener<String>() {
                                                    @Override
                                                    public void onSuccess(@NonNull String translatedText) {
                                                        Toast.makeText(Translator.this, "Translation Finished!", Toast.LENGTH_SHORT).show();
                                                        cancel = false;
                                                        cancelButton.setText("SAVE");
                                                        translatedText = translatedText.replace("~", "\n");
                                                        translatedText = translatedText.replace("-", "~");

                                                        to.setText(translatedText);
                                                        to.setTextColor(Color.BLACK);
                                                        to.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f);
                                                    }
                                                })
                                        .addOnFailureListener(
                                                new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(Translator.this, "Oops, Couldn't Translate! ", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                to.setText(getResources().getString(R.string.translatorFail));
                                Toast.makeText(Translator.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = new Intent(getApplicationContext(), MyService.class);
        stopService(intent);

        if (!content.equals("")) {
            transButton.setVisibility(View.VISIBLE);
            transButton.setEnabled(true);
            vis=true;
        } else {
            transButton.setEnabled(false);
            transButton.setVisibility(View.INVISIBLE);
            vis=false;
        }

        click = 0;

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
