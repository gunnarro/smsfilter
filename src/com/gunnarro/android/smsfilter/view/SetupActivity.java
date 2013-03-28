package com.gunnarro.android.smsfilter.view;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.gunnarro.android.smsfilter.AppPreferences;
import com.gunnarro.android.smsfilter.ListAppPreferencesImpl;
import com.gunnarro.android.smsfilter.R;
import com.gunnarro.android.smsfilter.sms.SMSFilter;
import com.gunnarro.android.smsfilter.sms.SMSFilter.FilterTypeEnum;

/**
 * class which holds sms filter setup options.
 * 
 * @author gunnarro
 * 
 */
public class SetupActivity extends Activity {

    private AppPreferences appPreferences;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.setup_layout);
        this.appPreferences = new ListAppPreferencesImpl(this);
        init();
        setupEventHandlers();
    }

    private void init() {
        String filterType = appPreferences.getValue(AppPreferences.SMS_FILTER_TYPE);
        Log.i(SetupActivity.class.getSimpleName(), "selected filter:" + filterType);
        if (filterType == null || filterType.length() < 2) {
            filterType = FilterTypeEnum.SMS_BLACK_LIST.name();
        }
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radio_filter);
        Log.i(SetupActivity.class.getSimpleName(), "selected filter:" + filterType);
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
            Log.e(SetupActivity.class.getSimpleName(), "unkown type: " + filterType);
        }
    }

    private void setupEventHandlers() {
        final RadioButton allowAllBtn = (RadioButton) findViewById(R.id.radio_allow_all);
        final RadioButton blacklistBtn = (RadioButton) findViewById(R.id.radio_blacklist);
        final RadioButton whitelistBtn = (RadioButton) findViewById(R.id.radio_whitelist);
        final RadioButton contactsBtn = (RadioButton) findViewById(R.id.radio_contacts);
        RadioGroup typeRG = (RadioGroup) findViewById(R.id.radio_filter);
        typeRG.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup rg, int checkedId) {
                Log.i(SetupActivity.class.getSimpleName(), "selected filter type: " + checkedId);
                if (checkedId == allowAllBtn.getId()) {
                    saveFilterType(SMSFilter.FilterTypeEnum.ALLOW_ALL.name());
                } else if (checkedId == blacklistBtn.getId()) {
                    saveFilterType(SMSFilter.FilterTypeEnum.SMS_BLACK_LIST.name());
                } else if (checkedId == whitelistBtn.getId()) {
                    saveFilterType(SMSFilter.FilterTypeEnum.SMS_WHITE_LIST.name());
                } else if (checkedId == contactsBtn.getId()) {
                    saveFilterType(SMSFilter.FilterTypeEnum.SMS_CONTACTS.name());
                } else {
                    Log.e(SetupActivity.class.getSimpleName(), "unkown btnId: " + checkedId);
                }
            }
        });
    }

    private void saveFilterType(String value) {
        appPreferences.save(AppPreferences.SMS_FILTER_TYPE, new Item(value, true));
    }
}