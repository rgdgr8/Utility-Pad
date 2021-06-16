package com.rgdgr8.notepad;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

public class DatePicker extends DialogFragment {

    private int month;
    private int day;
    private int yr;

    @NonNull
    @Override
    public DatePickerDialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        Calendar c = Calendar.getInstance();

        if (getArguments()==null) {
            day = c.get(Calendar.DAY_OF_MONTH);
            month = c.get(Calendar.MONTH);
            yr = c.get(Calendar.YEAR);
        }else {
            day=getArguments().getInt("day",c.get(Calendar.DAY_OF_MONTH));
            month=getArguments().getInt("month",c.get(Calendar.MONTH));
            yr=getArguments().getInt("year",c.get(Calendar.YEAR));
        }

        return new DatePickerDialog(requireActivity(), (DatePickerDialog.OnDateSetListener) getActivity(), yr, month, day);
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {

        ReminderActivity.singledateClick=0;

        super.onDismiss(dialog);
    }
}
