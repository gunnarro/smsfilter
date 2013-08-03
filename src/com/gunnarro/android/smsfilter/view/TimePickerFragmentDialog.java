package com.gunnarro.android.smsfilter.view;

import java.util.Calendar;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TimePicker;

import com.gunnarro.android.smsfilter.custom.CustomLog;
import com.gunnarro.android.smsfilter.listener.TimePickerSelectedListener;

public class TimePickerFragmentDialog extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

	public enum TypeEnum {
		FROM_TIME, TO_TIME;
	}

	public enum ArgsEnum {
		TYPE, HOUR, MINUTE;
	}

	private TypeEnum type = TypeEnum.FROM_TIME;
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
		Bundle args = this.getArguments();
		type = TypeEnum.valueOf(args.getString(ArgsEnum.TYPE.name()));
		// Use the current time as the default values for the picker
		final Calendar c = Calendar.getInstance();
		int hour = c.get(Calendar.HOUR_OF_DAY);
		int minute = c.get(Calendar.MINUTE);

		if (args.containsKey(ArgsEnum.HOUR.name()) && args.containsKey(ArgsEnum.MINUTE.name())) {
			hour = args.getInt(ArgsEnum.HOUR.name());
			minute = args.getInt(ArgsEnum.MINUTE.name());
		}

		// Create a new instance of TimePickerDialog and return it
		return new TimePickerDialog(getActivity(), this, hour, minute, DateFormat.is24HourFormat(getActivity()));
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			Fragment fragment = activity.getFragmentManager().findFragmentByTag(SetupFragment.class.getSimpleName());
			this.listener = (TimePickerSelectedListener) fragment;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement TimePickerSelectedListener");
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
		if (type == TypeEnum.FROM_TIME) {
			this.listener.setSelectedFromTime(hourOfDay, minute);
		} else if (type == TypeEnum.TO_TIME) {
			this.listener.setSelectedToTime(hourOfDay, minute);
		} else {
			CustomLog.e(this.getClass(), "invalid type: " + type);
		}
	}
}