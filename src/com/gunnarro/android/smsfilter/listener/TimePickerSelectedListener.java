package com.gunnarro.android.smsfilter.listener;

public interface TimePickerSelectedListener {

	public void setSelectedFromTime(int hourOfDay, int minute);

	public void setSelectedToTime(int hourOfDay, int minute);
}
