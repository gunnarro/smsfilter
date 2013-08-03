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
import com.gunnarro.android.smsfilter.custom.CustomLog;

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

		actionbar.addTab(actionbar.newTab().setText(R.string.tab_setup)
				.setTabListener(new TabListener<SetupFragment>(this, SetupFragment.class.getSimpleName(), SetupFragment.class)));
		actionbar.addTab(actionbar.newTab().setText(R.string.tab_blacklist)
				.setTabListener(new TabListener<BlackListFragment>(this, BlackListFragment.class.getSimpleName(), BlackListFragment.class)));
		actionbar.addTab(actionbar.newTab().setText(R.string.tab_whitelist)
				.setTabListener(new TabListener<WhiteListFragment>(this, WhiteListFragment.class.getSimpleName(), WhiteListFragment.class)));
		actionbar.addTab(actionbar.newTab().setText(R.string.tab_blocked)
				.setTabListener(new TabListener<MsgStatisticFragment>(this, MsgStatisticFragment.class.getSimpleName(), MsgStatisticFragment.class)));

		// actionbar.addTab(actionbar.newTab().setText(R.string.tab_setup).setTabListener(new
		// MyTabsListener<Fragment>(new SetupFragment())));
		// // .setIcon(R.drawable.config_tab));
		// actionbar.addTab(actionbar.newTab().setText(R.string.tab_blacklist).setTabListener(new
		// MyTabsListener<Fragment>(new BlackListFragment())));
		// // .setIcon(R.drawable.black_list_tab));
		// actionbar.addTab(actionbar.newTab().setText(R.string.tab_whitelist).setTabListener(new
		// MyTabsListener<Fragment>(new WhiteListFragment())));
		// // .setIcon(R.drawable.white_list_tab));
		// actionbar.addTab(actionbar.newTab().setText(R.string.tab_blocked).setTabListener(new
		// MyTabsListener<Fragment>(new MsgStatisticFragment())));
		// // .setIcon(R.drawable.blocked_sms_history_tab));

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

	public Fragment getCurrentFragment(Fragment fragment) {
		return null;
	}

	// /**
	// * Custom implementation of tab listener
	// */
	// class MyTabsListener<T extends Fragment> implements ActionBar.TabListener
	// {
	// public Fragment fragment;
	//
	// public MyTabsListener(Fragment fragment) {
	// this.fragment = fragment;
	// }
	//
	// /**
	// * {@inheritDoc}
	// */
	// @Override
	// public void onTabReselected(Tab tab, FragmentTransaction ft) {
	// }
	//
	// /**
	// * {@inheritDoc}
	// */
	// @Override
	// public void onTabSelected(Tab tab, FragmentTransaction ft) {
	// ft.replace(R.id.main, fragment);
	// CustomLog.d(this.getClass(), "id=" + fragment.getId() + ", " +
	// fragment.toString());
	// }
	//
	// /**
	// * {@inheritDoc}
	// */
	// @Override
	// public void onTabUnselected(Tab tab, FragmentTransaction ft) {
	// ft.remove(fragment);
	// }
	//
	// } // end class MyTabListener

	public static class TabListener<T extends Fragment> implements ActionBar.TabListener {
		private final Activity activity;
		private final String tag;
		private final Class<T> clazz;
		private final Bundle args;
		private Fragment fragment;

		public TabListener(Activity activity, String tag, Class<T> clz) {
			this(activity, tag, clz, null);
		}

		public TabListener(Activity activity, String tag, Class<T> clz, Bundle args) {
			this.activity = activity;
			this.tag = tag;
			this.clazz = clz;
			this.args = args;

			// Check to see if we already have a fragment for this tab, probably
			// from a previously saved state. If so, deactivate it, because our
			// initial state is that a tab isn't shown.
			this.fragment = this.activity.getFragmentManager().findFragmentByTag(this.tag);
			if (this.fragment != null && !this.fragment.isDetached()) {
				FragmentTransaction ft = this.activity.getFragmentManager().beginTransaction();
				ft.detach(this.fragment);
				ft.commit();
			}
		}

		public void onTabSelected(Tab tab, FragmentTransaction ft) {
			if (this.fragment == null) {
				this.fragment = Fragment.instantiate(this.activity, this.clazz.getName(), this.args);
				ft.add(android.R.id.content, this.fragment, this.tag);
			} else {
				ft.attach(this.fragment);
			}
		}

		public void onTabUnselected(Tab tab, FragmentTransaction ft) {
			if (this.fragment != null) {
				ft.detach(this.fragment);
			}
		}

		public void onTabReselected(Tab tab, FragmentTransaction ft) {
			Toast.makeText(this.activity, "Reselected!", Toast.LENGTH_SHORT).show();
		}

	} // end class TabListener

} // end class MainActivity