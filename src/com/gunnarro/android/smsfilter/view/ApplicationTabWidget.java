package com.gunnarro.android.smsfilter.view;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.TabHost;

import com.gunnarro.android.smsfilter.R;

public class ApplicationTabWidget extends TabActivity {

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Load the main.xml layout file.
        super.requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        super.setContentView(R.layout.application_layout);
        super.getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.window_title_layout);
        this.tabSetup();
    }

    private void tabSetup() {
        TabHost tabHost = getTabHost();
        // Initialize a TabSpec for each tab and add it to the TabHost
        tabHost.addTab(tabHost.newTabSpec("Config").setIndicator("Config", getResources().getDrawable(R.drawable.config_tab))
                .setContent(new Intent().setClass(this, SetupActivity.class)));
        tabHost.addTab(tabHost.newTabSpec("BlackList").setIndicator("BlackList", getResources().getDrawable(R.drawable.black_list_tab))
                .setContent(new Intent().setClass(this, BlackListConfigurationActivity.class)));
        tabHost.addTab(tabHost.newTabSpec("WhiteList").setIndicator("WhiteList", getResources().getDrawable(R.drawable.white_list_tab))
                .setContent(new Intent().setClass(this, WhiteListConfigurationActivity.class)));
        tabHost.addTab(tabHost.newTabSpec("Blocked").setIndicator("Blocked", getResources().getDrawable(R.drawable.blocked_sms_history_tab))
                .setContent(new Intent().setClass(this, SMSStatisticActivity.class)));
        tabHost.setCurrentTab(0);
    }
}
