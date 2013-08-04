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
import com.gunnarro.android.smsfilter.utility.Utility;
import com.gunnarro.android.smsfilter.view.TimePickerFragmentDialog.ArgsEnum;

public class SetupFragment extends Fragment implements TimePickerSelectedListener {

	private FilterService filterService;

	/**
	 * 
	 */
	private void showTimePickerDialog(View v, TimePickerFragmentDialog.TypeEnum type, int hour, int minute) {
		DialogFragment timePickerFragment = new TimePickerFragmentDialog();
		Bundle agrsBundle = new Bundle();
		agrsBundle.putString(ArgsEnum.TYPE.name(), type.name());
		agrsBundle.putInt(ArgsEnum.HOUR.name(), hour);
		agrsBundle.putInt(ArgsEnum.MINUTE.name(), minute);
		timePickerFragment.setArguments(agrsBundle);
		timePickerFragment.show(getFragmentManager(), "showTimePickerDialog");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setSelectedFromTime(int hourOfDay, int minute) {
		CustomLog.d(this.getClass(), "selected time: " + hourOfDay + ":" + minute);
		this.filterService.updateMsgFilterPeriodFromTime(Utility.formatTime(hourOfDay, minute));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setSelectedToTime(int hourOfDay, int minute) {
		CustomLog.d(this.getClass(), "selected time: " + hourOfDay + ":" + minute);
		this.filterService.updateMsgFilterPeriodToTime(Utility.formatTime(hourOfDay, minute));
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

		view.findViewById(R.id.msg_filter_from_time_value).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// Note, send in this view, and not the v, which is a reference
				// to the main view. Populate it with current from time.
				String fromTime = filterService.getMsgFilterPeriodFromTime();
				showTimePickerDialog(view, TimePickerFragmentDialog.TypeEnum.FROM_TIME, Utility.getHour(fromTime), Utility.getMinute(fromTime));
			}
		});

		view.findViewById(R.id.msg_filter_to_time_value).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// Note, send in this view, and not the v, which is a reference
				// to the main view. Populate it with current to time.
				String toTime = filterService.getMsgFilterPeriodToTime();
				showTimePickerDialog(view, TimePickerFragmentDialog.TypeEnum.TO_TIME, Utility.getHour(toTime), Utility.getMinute(toTime));
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
		fromTime.setText(this.filterService.getMsgFilterPeriodFromTime());
		fromTime.setEnabled(isEnabled);

		TextView toTime = (TextView) view.findViewById(R.id.msg_filter_to_time_value);
		toTime.setText(this.filterService.getMsgFilterPeriodToTime());
		toTime.setEnabled(isEnabled);
	}
}
