package com.gunnarro.android.smsfilter.custom;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.gunnarro.android.smsfilter.R;

public class CustomWindow extends Activity {
    protected TextView title;
    protected ImageView icon;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
//        setContentView(R.layout.main);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.window_title_layout);
//        title = (TextView) findViewById(R.id.title);
//        icon = (ImageView) findViewById(R.id.icon);
    }
}