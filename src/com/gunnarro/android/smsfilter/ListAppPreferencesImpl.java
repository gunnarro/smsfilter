package com.gunnarro.android.smsfilter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import com.gunnarro.android.smsfilter.sms.SMS;
import com.gunnarro.android.smsfilter.view.Item;

/**
 * Class to store and read values from the android shared preferences.
 * 
 * @author gunnarro
 * 
 */
public class ListAppPreferencesImpl implements AppPreferences {

    private SharedPreferences appSharedPrefs;
    private Editor prefsEditor;

    /**
     * Default constructor
     */
    public ListAppPreferencesImpl() {
    }

    public ListAppPreferencesImpl(Context context) {
        this.appSharedPrefs = context.getSharedPreferences(APP_SHARED_PREFS, Activity.MODE_PRIVATE);
        this.prefsEditor = appSharedPrefs.edit();
    }

    public static String createSearch(String value) {
        String filter = "^" + value.replace("*", "") + ".*";
        if (value.startsWith("+")) {
            filter = "^\\" + value.replace("*", "") + ".*";
        }
        return filter;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Item> getList(String type) {
        List<Item> list = new ArrayList<Item>();
        // The items are stored as comma separated values
        String storedValues = getListAsString(type);
        if (storedValues != null && storedValues.length() > 1) {
            for (String valuePair : storedValues.split(SEPARATOR)) {
                Log.i(this.getClass().getSimpleName(), "getList parse: type=" + type + ", value=" + valuePair);
                Item item = Item.createItem(valuePair);
                if (item != null) {
                    list.add(item);
                }
            }
        }
        return list;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<SMS> getSMSList(String groupBy) {
        Map<String, SMS> map = new HashMap<String, SMS>();
        String list = getListAsString(AppPreferences.SMS_BLOCKED_LOG);
        if (list != null && list.length() > 1) {
            for (String blocked : list.split(AppPreferences.SEPARATOR)) {
                String[] split = blocked.split(":");
                SMS blockedSMS = new SMS(new Long(split[0]).longValue(), split[1]);
                blockedSMS.increaseNumberOfBlocked();
                String key = null;
                String format = "yyyy.MM.dd hh:mm:ss";
                SimpleDateFormat formatter = new SimpleDateFormat(format);
                if (groupBy.equalsIgnoreCase("default")) {
                    // default, none grouping at all
                    key = formatter.format(blockedSMS.getTimeMilliSecound());
                } else if (groupBy.equalsIgnoreCase("number")) {
                    key = blockedSMS.getNumber();
                } else {
                    // group by time
                    if (groupBy.equalsIgnoreCase("year")) {
                        format = "yyyy";
                    } else if (groupBy.equalsIgnoreCase("month")) {
                        format = "yyyy.MM";
                    } else if (groupBy.equalsIgnoreCase("day")) {
                        format = "yyyy.MM.dd";
                    }
                    formatter.applyPattern(format);
                    key = formatter.format(blockedSMS.getTimeMilliSecound());
                }
                blockedSMS.setKey(key);
                SMS sms = map.get(blockedSMS.getKey());
                if (sms == null) {
                    map.put(blockedSMS.getKey(), blockedSMS);
                } else {
                    sms.increaseNumberOfBlocked();
                }
            }
        }
        return new ArrayList<SMS>(map.values());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getListAsString(String type) {
        return appSharedPrefs.getString(type, DEFAULT_VALUE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean removeAllList(String type) {
        prefsEditor.putString(type, DEFAULT_VALUE);
        boolean removed = prefsEditor.commit();
        Log.i(this.getClass().getSimpleName(), "removeAllList: type=" + type + ", removed all=" + removed);
        return removed;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean updateList(String type, Item item) {
        StringBuffer listStr = new StringBuffer(getListAsString(type));
        if (listStr.length() == 0) {
            listStr.append(item.toValuePair());
        } else if (!listStr.toString().contains(item.getValue()) && item.getValue().length() > 1) {
            listStr.append(SEPARATOR).append(item.toValuePair());
        }
        prefsEditor.putString(type, listStr.toString());
        boolean updated = prefsEditor.commit();
        Log.i(this.getClass().getSimpleName(), "updateList: type=" + type + ", value=" + item.toValuePair() + ", updated=" + updated);
        return updated;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean removeList(String type, Item item) {
        boolean removed = false;
        List<Item> list = getList(type);
        if (!list.isEmpty() && list.contains(item)) {
            StringBuffer valuePairs = new StringBuffer();
            int i = list.size();
            for (Item thisItem : list) {
                i--;
                if (thisItem.getValue().equals(item.getValue())) {
                    // remove it by simply skip adding it to the value string.
                    removed = true;
                } else {
                    valuePairs.append(thisItem.toValuePair());
                    if (i > 0) {
                        valuePairs.append(SEPARATOR);
                    }
                }
            }
            prefsEditor.putString(type, valuePairs.toString());
            prefsEditor.commit();
        }
        Log.i(this.getClass().getSimpleName(), "removeList: type=" + type + ", value=" + item.toValuePair() + ", removed=" + removed);
        return removed;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean listContains(String type, String value) {
        String filter = createSearch(value);
        Log.i(this.getClass().getSimpleName(), "SMS filter: " + filter);
        for (Item item : getList(type)) {
            if (item.getValue().matches(filter)) {
                return true;
            }
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void save(String type, Item item) {
        prefsEditor.putString(type, item.toValuePair());
        boolean saved = prefsEditor.commit();
        Log.i(this.getClass().getSimpleName(), "save: type=" + type + ", value=" + item.toValuePair() + ", saved=" + saved);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getValue(String type) {
        return appSharedPrefs.getString(type, DEFAULT_VALUE);
    }

    public void setAppSharedPrefs(SharedPreferences appSharedPrefs) {
        this.appSharedPrefs = appSharedPrefs;
    }

}
