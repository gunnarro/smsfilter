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

import com.gunnarro.android.smsfilter.R;
import com.gunnarro.android.smsfilter.custom.CustomLog;
import com.gunnarro.android.smsfilter.service.FilterService;
import com.gunnarro.android.smsfilter.service.impl.FilterServiceImpl;
import com.gunnarro.android.smsfilter.service.impl.FilterServiceImpl.FilterTypeEnum;

public class SetupFragment extends Fragment {

    private FilterService filterService;

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

    /**
     * {@inheritDoc}
     */
    @Override
    public void onPause() {
        this.filterService.close();
        super.onPause();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onResume() {
        // onResume happens after onStart and onActivityCreate
        this.filterService.open();
        super.onResume();
    }

    // FIXME
    private void init(final View view) {
        FilterTypeEnum activeFilterType = null;// filterService.readActiveFilterType();
        if (activeFilterType == null) {
            activeFilterType = FilterTypeEnum.SMS_BLACK_LIST;
            CustomLog.d(this.getClass(), "filter type not set, default it to:" + activeFilterType);
        }

        RadioGroup radioGroup = (RadioGroup) view.findViewById(R.id.radio_filter);
        CustomLog.d(this.getClass(), "selected filter:" + activeFilterType.name());
        if (activeFilterType.isAllowAll()) {
            radioGroup.check(R.id.radio_allow_all);
        } else if (activeFilterType.isBlackList()) {
            radioGroup.check(R.id.radio_blacklist);
        } else if (activeFilterType.isWhiteList()) {
            radioGroup.check(R.id.radio_whitelist);
        } else if (activeFilterType.isContacts()) {
            radioGroup.check(R.id.radio_contacts);
        } else {
            radioGroup.check(R.id.radio_allow_all);
            CustomLog.e(this.getClass(), "unkown type: " + activeFilterType.name());
        }

        // Read and set correct status for the activated filter switch
        Switch sw = (Switch) view.findViewById(R.id.sms_filter_on_off_switch);
        boolean isActivated = filterService.isSMSFilterActivated();
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
                    activateFilter(FilterServiceImpl.FilterTypeEnum.ALLOW_ALL.name());
                } else if (checkedId == blacklistBtn.getId()) {
                    activateFilter(FilterServiceImpl.FilterTypeEnum.SMS_BLACK_LIST.name());
                } else if (checkedId == whitelistBtn.getId()) {
                    activateFilter(FilterServiceImpl.FilterTypeEnum.SMS_WHITE_LIST.name());
                } else if (checkedId == contactsBtn.getId()) {
                    activateFilter(FilterServiceImpl.FilterTypeEnum.SMS_CONTACTS.name());
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
                filterService.save(FilterService.SMS_FILTER_ACTIVATED, Boolean.toString(isChecked));
            }
        });
    }

    private void activateFilter(String filterType) {
        CustomLog.d(this.getClass(), "filtertype:" + filterType);
        saveFilterType(filterType);
    }

    private void saveFilterType(String value) {
        filterService.save(FilterService.SMS_ACTIVE_FILTER_TYPE, value);
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
