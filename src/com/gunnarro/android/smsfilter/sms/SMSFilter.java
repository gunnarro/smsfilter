package com.gunnarro.android.smsfilter.sms;

import android.content.Context;
import android.util.Log;

import com.gunnarro.android.smsfilter.AppPreferences;
import com.gunnarro.android.smsfilter.ListAppPreferencesImpl;
import com.gunnarro.android.smsfilter.domain.Item;

public class SMSFilter {

    private AppPreferences appPreferences;

    public enum FilterTypeEnum {
        ALLOW_ALL("allowAll"), SMS_BLACK_LIST(AppPreferences.SMS_BLACK_LIST), SMS_WHITE_LIST(AppPreferences.SMS_WHITE_LIST), SMS_CONTACTS("contacts");

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
            return this.filterType == AppPreferences.SMS_WHITE_LIST;
        }

        public boolean isBlackList() {
            return this.filterType == AppPreferences.SMS_BLACK_LIST;
        }
    }

    /**
     * default constructor
     */
    public SMSFilter() {
    }

    /**
     * 
     * @param context
     */
    public SMSFilter(Context context) {
        appPreferences = new ListAppPreferencesImpl(context);
    }

    /**
     * Tells whether the sms filter is activated or not.
     * 
     * @return true id activated, false otherwise
     */
    public boolean isActivated() {
        return appPreferences.isFilterActivated();
    }

    /**
     * Method to check if the phone number is blocked or not. If that's the
     * case, the SMS is ignored and only logged to the SMSFilters blocked log.
     * Otherwise, the SMS is handled as normal.
     * 
     * @param phoneNumber
     * @return true if the phone number is blocked false otherwise.
     */
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
            Item item = appPreferences.searchList(activeFilterType.filterType, phoneNumber);
            if (item != null && item.isEnabled()) {
                isBlocked = true;
            }
        } else if (activeFilterType.isWhiteList()) {
            Item item = appPreferences.searchList(activeFilterType.filterType, phoneNumber);
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

    private void logBlockedSMS(String phoneNumber) {
        String blockedSMS = System.currentTimeMillis() + ":" + phoneNumber;
        appPreferences.updateList(AppPreferences.SMS_BLOCKED_LOG, new Item(blockedSMS, true));
    }

    /**
     * Method to get active filter type, which is stored in the internal app
     * preference table.
     * 
     * @return selected filter type
     */
    private FilterTypeEnum getActiveFilterType() {
        String filterType = appPreferences.getValue(AppPreferences.SMS_FILTER_TYPE);
        try {
            return FilterTypeEnum.valueOf(filterType);
        } catch (Exception e) {
            Log.e(this.getClass().getSimpleName(), "Filtertype not found for:" + filterType, e);
            return null;
        }
    }

    /**
     * For unit testing only
     * 
     * @param appPreferences
     */
    public void setAppPreferences(AppPreferences appPreferences) {
        this.appPreferences = appPreferences;
    }
}
