package com.gunnarro.android.smsfilter.view;

import java.util.Calendar;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TimePicker;

import com.gunnarro.android.smsfilter.listener.TimePickerSelectedListener;

public class TimePickerFragmentDialog extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    private TimePickerSelectedListener listener;

    /**
     * default constructor
     */
    public TimePickerFragmentDialog() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute, DateFormat.is24HourFormat(getActivity()));
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
//            listener = (TimePickerSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement TimePickerSelectedListener");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        listener.onTimeSet(hourOfDay, minute);
        System.out.println("voila");
    }
}