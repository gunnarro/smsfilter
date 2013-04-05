package com.gunnarro.android.smsfilter.service;

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

import com.gunnarro.android.smsfilter.custom.CustomLog;
import com.gunnarro.android.smsfilter.domain.Item;
import com.gunnarro.android.smsfilter.domain.SMS;

/**
 * Class to store and read values from the android shared preferences.
 * 
 * @author gunnarro
 * 
 */
public class FilterServiceImpl implements FilterService {

    private SharedPreferences appSharedPrefs;
    private Editor prefsEditor;

    public enum FilterTypeEnum {
        ALLOW_ALL("allowAll"), SMS_BLACK_LIST(FilterService.SMS_BLACK_LIST), SMS_WHITE_LIST(FilterService.SMS_WHITE_LIST), SMS_CONTACTS("contacts");

        private String filterType;

        FilterTypeEnum(String filterType) {
            this.filterType = filterType;
        }

        public boolean isAllowAll() {
            return this.filterType == "allowAll";
        }

        public boolean isContacts() {
            return this.filterType == "contacts";
        }

        public boolean isWhiteList() {
            return this.filterType == FilterService.SMS_WHITE_LIST;
        }

        public boolean isBlackList() {
            return this.filterType == FilterService.SMS_BLACK_LIST;
        }
    }

    /**
     * Default constructor
     */
    public FilterServiceImpl() {
    }

    public FilterServiceImpl(Context context) {
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
                CustomLog.i(this.getClass(), "type=" + type + ", value=" + valuePair);
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
        String list = getListAsString(FilterService.SMS_BLOCKED_LOG);
        if (list != null && list.length() > 1) {
            for (String blocked : list.split(FilterService.SEPARATOR)) {
                String[] split = blocked.split(":");
                SMS blockedSMS = new SMS(Long.valueOf(split[0]).longValue(), split[1]);
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
        CustomLog.i(this.getClass(), "type=" + type + ", removed all=" + removed);
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
        CustomLog.i(this.getClass(), "type=" + type + ", value=" + item.toValuePair() + ", updated=" + updated);
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
        CustomLog.i(this.getClass(), "type=" + type + ", value=" + item.toValuePair() + ", removed=" + removed);
        return removed;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Item searchList(String type, String value) {
        for (Item item : getList(type)) {
            String filter = createSearch(item.getValue());
            if (value.matches(filter)) {
                CustomLog.i(this.getClass(), "HIT value=" + value + ", filter=" + filter);
                return item;
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void save(String type, String value) {
        prefsEditor.putString(type, value);
        boolean saved = prefsEditor.commit();
        CustomLog.i(this.getClass(), "type=" + type + ", value=" + value + ", saved=" + saved);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getValue(String type) {
        return appSharedPrefs.getString(type, DEFAULT_VALUE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isSMSFilterActivated() {
        String isActivated = getValue(FilterService.SMS_FILTER_ACTIVATED);
        if (!isActivated.equalsIgnoreCase(Boolean.TRUE.toString()) && !isActivated.equalsIgnoreCase(Boolean.FALSE.toString())) {
            // activated not set, default it to false, and save it.
            isActivated = Boolean.FALSE.toString();
            save(FilterService.SMS_FILTER_ACTIVATED, isActivated);
        }
        return Boolean.parseBoolean(isActivated);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isBlocked(String phoneNumber) {
        boolean isBlocked = false;
        FilterTypeEnum activeFilterType = getActiveFilterType();
        if (activeFilterType == null) {
            Log.e(this.getClass().getSimpleName(), "isBlocked(): BUG: filter type not found, do not block sms!");
            return false;
        }

        if (activeFilterType.isAllowAll()) {
            // wide open for everyone
            isBlocked = false;
        } else if (activeFilterType.isContacts()) {
            // Check contact list
            isBlocked = false;
        } else if (activeFilterType.isBlackList()) {
            Item item = searchList(activeFilterType.filterType, phoneNumber);
            if (item != null && item.isEnabled()) {
                isBlocked = true;
            }
        } else if (activeFilterType.isWhiteList()) {
            Item item = searchList(activeFilterType.filterType, phoneNumber);
            if (item == null || (item != null && !item.isEnabled())) {
                isBlocked = true;
            }
        }
        if (isBlocked) {
            logBlockedSMS(phoneNumber);
        }
        Log.d(this.getClass().getSimpleName(), ".isBlocked(): Filter type=" + activeFilterType + ", number=" + phoneNumber + ", isBlocked=" + isBlocked);
        return isBlocked;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FilterTypeEnum getActiveFilterType() {
        String filterType = getValue(FilterService.SMS_FILTER_TYPE);
        try {
            return FilterTypeEnum.valueOf(filterType);
        } catch (Exception e) {
            Log.e(this.getClass().getSimpleName(), "Filtertype not found for:" + filterType, e);
            return null;
        }
    }

    private void logBlockedSMS(String phoneNumber) {
        String blockedSMS = System.currentTimeMillis() + ":" + phoneNumber;
        updateList(FilterService.SMS_BLOCKED_LOG, new Item(blockedSMS, true));
    }

    /**
     * For unit testing only. Used to mock the service.
     * 
     */
    public void setAppSharedPrefs(SharedPreferences appSharedPrefs) {
        this.appSharedPrefs = appSharedPrefs;
    }

    /**
     * For unit testing only. Used to mock the service.
     * 
     */
    public void setPrefsEditor(Editor prefsEditor) {
        this.prefsEditor = prefsEditor;
    }

}
