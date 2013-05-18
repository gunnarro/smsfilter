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

    // FIXME
    private void init(final View view) {
        FilterTypeEnum activeFilterType = filterService.getActiveFilterType();
        if (activeFilterType == null) {
            activeFilterType = FilterTypeEnum.SMS_BLACK_LIST;
            CustomLog.d(this.getClass(), "filter type not set, default it to:" + activeFilterType);
            // have to activate it...
            filterService.activateFilterType(activeFilterType);
        }

        RadioGroup radioGroup = (RadioGroup) view.findViewById(R.id.radio_filter);
        CustomLog.d(this.getClass(), "selected filter:" + activeFilterType.name());

        if (activeFilterType.isBlackList()) {
            radioGroup.check(R.id.radio_blacklist);
        } else if (activeFilterType.isWhiteList()) {
            radioGroup.check(R.id.radio_whitelist);
        } else if (activeFilterType.isContacts()) {
            radioGroup.check(R.id.radio_contacts);
        } else {
            CustomLog.e(this.getClass(), "unkown type: " + activeFilterType.name());
        }

        // Read and set correct status for the activated filter switch
        Switch sw = (Switch) view.findViewById(R.id.sms_filter_on_off_switch);
        boolean isActivated = filterService.isMsgFilterActivated();
        sw.setChecked(isActivated);
        // Set saved status for the radio buttons
        setRadioButtonsStatus(radioGroup, sw.isChecked());
    }

    private void setupEventHandlers(final View view) {
        final RadioButton blacklistBtn = (RadioButton) view.findViewById(R.id.radio_blacklist);
        final RadioButton whitelistBtn = (RadioButton) view.findViewById(R.id.radio_whitelist);
        final RadioButton contactsBtn = (RadioButton) view.findViewById(R.id.radio_contacts);
        RadioGroup typeRG = (RadioGroup) view.findViewById(R.id.radio_filter);
        typeRG.setOnCheckedChangeListener(new OnCheckedChangeListener() {
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
                setRadioButtonsStatus(rg, true);
            }
        });

        Switch sw = (Switch) view.findViewById(R.id.sms_filter_on_off_switch);
        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                RadioGroup rg = (RadioGroup) view.findViewById(R.id.radio_filter);
                setRadioButtonsStatus(rg, isChecked);
                if (isChecked) {
                    filterService.activateSMSFilter();
                } else {
                    filterService.deactivateSMSFilter();
                }
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

    private void setRadioButtonsStatus(RadioGroup rg, boolean isEnableRadioGrp) {
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

}
