package com.gunnarro.android.smsfilter.view;

import android.app.DialogFragment;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Switch;
import android.widget.TextView;

import com.gunnarro.android.smsfilter.R;
import com.gunnarro.android.smsfilter.custom.CustomLog;
import com.gunnarro.android.smsfilter.listener.TimePickerSelectedListener;
import com.gunnarro.android.smsfilter.service.FilterService;
import com.gunnarro.android.smsfilter.service.impl.FilterServiceImpl;
import com.gunnarro.android.smsfilter.service.impl.FilterServiceImpl.FilterTypeEnum;

public class SetupFragment extends Fragment implements TimePickerSelectedListener {

	private FilterService filterService;

	/**
	 * FIXME must be in the main activity
	 * 
	 * @param v
	 */
	public void showTimePickerDialog(View v) {
		DialogFragment newFragment = new TimePickerFragmentDialog();
		newFragment.show(getFragmentManager(), "showTimePickerDialog");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onTimeSet(int hourOfDay, int minute) {
		CustomLog.d(this.getClass(), "selected time: " + hourOfDay + ":" + minute);
		this.filterService.updateMsgFilterPeriodFromTime(formatTime(hourOfDay, minute));
		this.filterService.updateMsgFilterPeriodToTime(formatTime(hourOfDay, minute));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.setup_layout, container, false);
		this.filterService = new FilterServiceImpl(view.getContext());
		init(view);
		setupEventHandlers(view);
		return view;
	}

	// FIXME
	private void init(final View view) {
		FilterTypeEnum activeFilterType = filterService.getActiveFilterType();
		if (activeFilterType == null) {
			activeFilterType = FilterTypeEnum.SMS_BLACK_LIST;
			CustomLog.d(this.getClass(), "filter type not set, default it to:" + activeFilterType);
			// have to activate it...
			filterService.activateFilterType(activeFilterType);
		}

		RadioGroup msgFilterOptions = (RadioGroup) view.findViewById(R.id.radio_msg_filter_options);
		CustomLog.d(this.getClass(), "selected filter:" + activeFilterType.name());

		if (activeFilterType.isBlackList()) {
			msgFilterOptions.check(R.id.radio_blacklist);
		} else if (activeFilterType.isWhiteList()) {
			msgFilterOptions.check(R.id.radio_whitelist);
		} else if (activeFilterType.isContacts()) {
			msgFilterOptions.check(R.id.radio_contacts);
		} else {
			CustomLog.e(this.getClass(), "unkown type: " + activeFilterType.name());
		}

		// Read and set correct status for the message filter switch
		Switch msgFilterSwitch = (Switch) view.findViewById(R.id.msg_filter_on_off_switch);
		msgFilterSwitch.setChecked(filterService.isMsgFilterActivated());
		// Set saved status for the radio buttons
		updateMsgFilterRadioButtonsStatus(msgFilterOptions, msgFilterSwitch.isChecked());

		// Read and set correct status for the message filter period switch
		Switch msgFilterPeridoSwitch = (Switch) view.findViewById(R.id.msg_filter_period_on_off_switch);
		msgFilterPeridoSwitch.setChecked(filterService.isMsgFilterPeriodActivated());
		updateMsgFilterTimePickerStatus(view, msgFilterPeridoSwitch.isChecked());

		updateMsgFilterPeriod(view, msgFilterPeridoSwitch.isChecked());
	}

	private void setupEventHandlers(final View view) {
		final RadioButton blacklistBtn = (RadioButton) view.findViewById(R.id.radio_blacklist);
		final RadioButton whitelistBtn = (RadioButton) view.findViewById(R.id.radio_whitelist);
		final RadioButton contactsBtn = (RadioButton) view.findViewById(R.id.radio_contacts);
		RadioGroup msgFilterOptions = (RadioGroup) view.findViewById(R.id.radio_msg_filter_options);
		msgFilterOptions.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(RadioGroup rg, int checkedId) {
				if (checkedId == blacklistBtn.getId()) {
					activateFilter(FilterServiceImpl.FilterTypeEnum.SMS_BLACK_LIST);
				} else if (checkedId == whitelistBtn.getId()) {
					activateFilter(FilterServiceImpl.FilterTypeEnum.SMS_WHITE_LIST);
				} else if (checkedId == contactsBtn.getId()) {
					activateFilter(FilterServiceImpl.FilterTypeEnum.CONTACTS);
				} else {
					CustomLog.e(this.getClass(), "unkown btnId: " + checkedId);
				}
				updateMsgFilterRadioButtonsStatus(rg, true);
			}
		});

		Switch filterSwitch = (Switch) view.findViewById(R.id.msg_filter_on_off_switch);
		filterSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				RadioGroup rg = (RadioGroup) view.findViewById(R.id.radio_msg_filter_options);
				updateMsgFilterRadioButtonsStatus(rg, isChecked);
				if (isChecked) {
					filterService.activateMsgFilter();
				} else {
					filterService.deactivateMsgFilter();
				}
			}
		});

		Switch msgFilterPeriodeSwitch = (Switch) view.findViewById(R.id.msg_filter_period_on_off_switch);
		msgFilterPeriodeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				updateMsgFilterPeriod(view, isChecked);
				if (isChecked) {
					filterService.activateMsgFilterPeriod();
				} else {
					filterService.deactivateMsgFilterPeriod();
				}
			}
		});

		view.findViewById(R.id.msg_filter_from_time_btn).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showTimePickerDialog(view);
			}
		});

		view.findViewById(R.id.msg_filter_to_time_btn).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// note, send in this view, and not the v, which is a reference
				// to the main view.
				showTimePickerDialog(view);
			}
		});
	}

	private void activateFilter(FilterTypeEnum selectedFilterType) {
		CustomLog.d(this.getClass(), "filtertype:" + selectedFilterType);
		// first we have to deactivate current filter type...
		filterService.deActivateFilterType(filterService.getActiveFilterType());
		// then activate the newly selected one.
		filterService.activateFilterType(selectedFilterType);
	}

	private void updateMsgFilterRadioButtonsStatus(RadioGroup rg, boolean isEnableRadioGrp) {
		for (int i = 0; i < rg.getChildCount(); i++) {
			RadioButton radio = (RadioButton) rg.getChildAt(i);
			radio.setEnabled(isEnableRadioGrp);
			if (isEnableRadioGrp) {
				radio.setTextColor(getResources().getColor(R.color.txt_radio));
			} else {
				radio.setTextColor(getResources().getColor(R.color.txt_radio_disabled));
			}
			if (radio.isChecked() && isEnableRadioGrp) {
				radio.setTextColor(getResources().getColor(R.color.txt_radio_selected));
			}
		}
	}

	private void updateMsgFilterPeriod(View view, boolean isEnabled) {
		TextView fromTime = (TextView) view.findViewById(R.id.msg_filter_from_time_value);
		fromTime.setText(this.filterService.getMsgFilterPeriodFromHour());
		fromTime.setEnabled(isEnabled);

		TextView toTime = (TextView) view.findViewById(R.id.msg_filter_to_time_value);
		toTime.setText(this.filterService.getMsgFilterPeriodToHour());
		toTime.setEnabled(isEnabled);

		ImageButton fromTimeBtn = (ImageButton) view.findViewById(R.id.msg_filter_from_time_btn);
		fromTimeBtn.setEnabled(isEnabled);
		ImageButton toTimeBtn = (ImageButton) view.findViewById(R.id.msg_filter_to_time_btn);
		toTimeBtn.setEnabled(isEnabled);
	}

	private void updateMsgFilterTimePickerStatus(View view, boolean isEnabled) {
		// TimePicker fromTimePicker = (TimePicker)
		// view.findViewById(R.id.msg_filter_from_time);
		// TimePicker toTimePicker = (TimePicker)
		// view.findViewById(R.id.msg_filter_to_time);
		// fromTimePicker.setEnabled(isEnabled);
		// fromTimePicker.setIs24HourView(true);
		// fromTimePicker.setCurrentHour(this.filterService.getMsgFilterPeriodFromHour());
		// fromTimePicker.setCurrentMinute(0);
		// toTimePicker.setEnabled(isEnabled);
		// toTimePicker.setIs24HourView(true);
		// toTimePicker.setCurrentHour(this.filterService.getMsgFilterPeriodToHour());
		// toTimePicker.setCurrentMinute(0);
	}

	private static String formatTime(int hour, int minute) {
		StringBuffer time = new StringBuffer();
		time.append(padTime(hour)).append(":").append(padTime(minute));
		return time.toString();
	}

	/** Add padding to numbers less than ten */
	private static String padTime(int n) {
		if (n >= 10) {
			return String.valueOf(n);
		} else {
			return "0" + String.valueOf(n);
		}
	}
}
