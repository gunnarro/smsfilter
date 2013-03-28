package com.gunnarro.android.smsfilter.view;

import android.os.Bundle;

import com.gunnarro.android.smsfilter.AppPreferences;
import com.gunnarro.android.smsfilter.ListAppPreferencesImpl;
import com.gunnarro.android.smsfilter.R;

/**
 * Dedicated class for handling black listed phone numbers
 * 
 * @author gunnarro
 * 
 */
public class BlackListConfigurationActivity extends ListConfigurationActivity {

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.black_list_layout);
        super.setType(AppPreferences.SMS_BLACK_LIST);
        super.setAddBtnId(R.id.black_list_add_btn);
        super.setDelBtnId(R.id.black_list_del_btn);
        super.setRefreshBtnId(R.id.black_list_refresh_btn);
        super.setInputFieldId(R.id.black_list_input_field);
        super.setAppPreferences(new ListAppPreferencesImpl(this));
        super.setupEventHandlers();
    }
}