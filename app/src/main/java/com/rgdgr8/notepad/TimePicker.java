package com.rgdgr8.notepad;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.format.DateFormat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

public class TimePicker extends DialogFragment {

    private int minute;
    private int hour;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        Calendar c = Calendar.getInstance();
        if (getArguments()!=null) {
            hour = getArguments().getInt("hour", c.get(Calendar.HOUR_OF_DAY));
            minute = getArguments().getInt("min", c.get(Calendar.MINUTE));
        }else {
            hour=c.get(Calendar.HOUR_OF_DAY);
            minute=c.get(Calendar.MINUTE);
        }

        return new TimePickerDialog(getActivity(), (TimePickerDialog.OnTimeSetListener) getActivity(), hour, minute, DateFormat.is24HourFormat(getActivity()));
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {

        ReminderActivity.singletimeClick=0;

        super.onDismiss(dialog);
    }
}
