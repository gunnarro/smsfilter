package com.gunnarro.android.smsfilter.view;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Switch;

import com.gunnarro.android.smsfilter.AppPreferences;
import com.gunnarro.android.smsfilter.ListAppPreferencesImpl;
import com.gunnarro.android.smsfilter.R;
import com.gunnarro.android.smsfilter.custom.CustomLog;
import com.gunnarro.android.smsfilter.sms.SMSFilter;
import com.gunnarro.android.smsfilter.sms.SMSFilter.FilterTypeEnum;

public class SetupFragment extends Fragment {

    private AppPreferences appPreferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.setup_layout, container, false);
        this.appPreferences = new ListAppPreferencesImpl(view.getContext());
        init(view);
        setupEventHandlers(view);
        return view;
    }

    private void init(final View view) {
        String filterType = appPreferences.getValue(AppPreferences.SMS_FILTER_TYPE);
        if (filterType == null || filterType.length() < 2) {
            filterType = FilterTypeEnum.SMS_BLACK_LIST.name();
            CustomLog.d(this.getClass(), "filter type not set, default it to:" + filterType);
        }

        RadioGroup radioGroup = (RadioGroup) view.findViewById(R.id.radio_filter);
        CustomLog.d(this.getClass(), "selected filter:" + filterType);
        if (filterType.equals(SMSFilter.FilterTypeEnum.ALLOW_ALL.name())) {
            radioGroup.check(R.id.radio_allow_all);
        } else if (filterType.equals(SMSFilter.FilterTypeEnum.SMS_BLACK_LIST.name())) {
            radioGroup.check(R.id.radio_blacklist);
        } else if (filterType.equals(SMSFilter.FilterTypeEnum.SMS_WHITE_LIST.name())) {
            radioGroup.check(R.id.radio_whitelist);
        } else if (filterType.equals(SMSFilter.FilterTypeEnum.SMS_CONTACTS.name())) {
            radioGroup.check(R.id.radio_contacts);
        } else {
            radioGroup.check(R.id.radio_allow_all);
            CustomLog.e(this.getClass(), "unkown type: " + filterType);
        }

        // Read and set correct status for the activated filter switch
        Switch sw = (Switch) view.findViewById(R.id.sms_filter_on_off_switch);
        boolean isActivated = appPreferences.isFilterActivated();
        sw.setChecked(isActivated);
        // Set saved status for the radio buttons
        disableRadioButtons(radioGroup, sw.isChecked());
    }

    private void setupEventHandlers(final View view) {
        final RadioButton allowAllBtn = (RadioButton) view.findViewById(R.id.radio_allow_all);
        final RadioButton blacklistBtn = (RadioButton) view.findViewById(R.id.radio_blacklist);
        final RadioButton whitelistBtn = (RadioButton) view.findViewById(R.id.radio_whitelist);
        final RadioButton contactsBtn = (RadioButton) view.findViewById(R.id.radio_contacts);
        RadioGroup typeRG = (RadioGroup) view.findViewById(R.id.radio_filter);
        typeRG.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup rg, int checkedId) {
                CustomLog.d(this.getClass(), "selected filter type: " + checkedId);
                if (checkedId == allowAllBtn.getId()) {
                    activateFilter(SMSFilter.FilterTypeEnum.ALLOW_ALL.name());
                } else if (checkedId == blacklistBtn.getId()) {
                    activateFilter(SMSFilter.FilterTypeEnum.SMS_BLACK_LIST.name());
                } else if (checkedId == whitelistBtn.getId()) {
                    activateFilter(SMSFilter.FilterTypeEnum.SMS_WHITE_LIST.name());
                } else if (checkedId == contactsBtn.getId()) {
                    activateFilter(SMSFilter.FilterTypeEnum.SMS_CONTACTS.name());
                } else {
                    CustomLog.e(this.getClass(), "unkown btnId: " + checkedId);
                }
            }
        });

        Switch sw = (Switch) view.findViewById(R.id.sms_filter_on_off_switch);
        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                RadioGroup rg = (RadioGroup) view.findViewById(R.id.radio_filter);
                disableRadioButtons(rg, isChecked);
                appPreferences.save(AppPreferences.SMS_FILTER_ACTIVATED, Boolean.toString(isChecked));
            }
        });
    }

    private void activateFilter(String filterType) {
        CustomLog.d(this.getClass(), "filtertype:" + filterType);
        saveFilterType(filterType);
    }

    private void saveFilterType(String value) {
        appPreferences.save(AppPreferences.SMS_FILTER_TYPE, value);
    }

    private void disableRadioButtons(RadioGroup rg, boolean isDisabled) {
        for (int i = 0; i < rg.getChildCount(); i++) {
            RadioButton radio = (RadioButton) rg.getChildAt(i);
            radio.setEnabled(isDisabled);
            if (!radio.isEnabled()) {
                radio.setTextColor(getResources().getColor(R.color.txt_disabled));
            } else {
                radio.setTextColor(getResources().getColor(R.color.txt_radio));
            }
        }
    }

}
