package com.gunnarro.android.smsfilter.view;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.gunnarro.android.smsfilter.R;

public class MainActivity extends Activity {

    public static Context appContext;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        appContext = getApplicationContext();

        // ActionBar
        ActionBar actionbar = getActionBar();
        actionbar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        actionbar.addTab(actionbar.newTab().setText(R.string.tab_setup).setTabListener(new MyTabsListener<Fragment>(new SetupFragment())));
        // .setIcon(R.drawable.config_tab));
        actionbar.addTab(actionbar.newTab().setText(R.string.tab_blacklist).setTabListener(new MyTabsListener<Fragment>(new BlackListFragment())));
        // .setIcon(R.drawable.black_list_tab));
        actionbar.addTab(actionbar.newTab().setText(R.string.tab_whitelist).setTabListener(new MyTabsListener<Fragment>(new WhiteListFragment())));
        // .setIcon(R.drawable.white_list_tab));
        actionbar.addTab(actionbar.newTab().setText(R.string.tab_blocked).setTabListener(new MyTabsListener<Fragment>(new SMSStatisticFragment())));
        // .setIcon(R.drawable.blocked_sms_history_tab));

        if (savedInstanceState != null) {
            actionbar.setSelectedNavigationItem(savedInstanceState.getInt("tab", 0));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        // case R.id.menuitem_help:
        // Toast.makeText(appContext, "help", Toast.LENGTH_SHORT).show();
        // return true;
        case R.id.menuitem_about:
            Toast.makeText(appContext, R.string.app_about, Toast.LENGTH_SHORT).show();
            return true;
        case R.id.menuitem_quit:
            finish();
            return true;
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("tab", getActionBar().getSelectedNavigationIndex());
    }

}

class MyTabsListener<T extends Fragment> implements ActionBar.TabListener {
    public Fragment fragment;

    public MyTabsListener(Fragment fragment) {
        this.fragment = fragment;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onTabReselected(Tab tab, FragmentTransaction ft) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onTabSelected(Tab tab, FragmentTransaction ft) {
        ft.replace(R.id.main, fragment);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onTabUnselected(Tab tab, FragmentTransaction ft) {
        ft.remove(fragment);
    }

}
