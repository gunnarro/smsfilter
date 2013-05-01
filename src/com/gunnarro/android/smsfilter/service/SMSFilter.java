package com.gunnarro.android.smsfilter.service;

import java.util.Calendar;

import android.content.Context;
import android.util.Log;

import com.gunnarro.android.smsfilter.custom.CustomLog;
import com.gunnarro.android.smsfilter.domain.Item;
import com.gunnarro.android.smsfilter.domain.SMSLog;
import com.gunnarro.android.smsfilter.service.impl.FilterServiceImpl;
import com.gunnarro.android.smsfilter.service.impl.FilterServiceImpl.FilterTypeEnum;

@Deprecated
public class SMSFilter {

    private FilterService filterService;

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
        filterService = new FilterServiceImpl(context);
    }

    /**
     * Tells whether the sms filter is activated or not.
     * 
     * @return true id activated, false otherwise
     */
    public boolean isActivated() {
        return filterService.isSMSFilterActivated();
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
            Item item = filterService.searchList(activeFilterType.name(), phoneNumber);
            if (item != null && item.isEnabled()) {
                isBlocked = true;
            }
        } else if (activeFilterType.isWhiteList()) {
            Item item = filterService.searchList(activeFilterType.name(), phoneNumber);
            if (item == null || (item != null && !item.isEnabled())) {
                isBlocked = true;
            }
        }
        if (isBlocked) {
            filterService.createLog(new SMSLog(Calendar.getInstance().getTimeInMillis(), phoneNumber, SMSLog.STATUS_SMS_BLOCKED, activeFilterType.name()));
        }
        CustomLog.d(this.getClass(), "Filter type=" + activeFilterType + ", number=" + phoneNumber + ", isBlocked=" + isBlocked);
        return isBlocked;
    }

    /**
     * Method to get active filter type, which is stored in the internal app
     * preference table.
     * 
     * @return selected filter type
     */
    private FilterTypeEnum getActiveFilterType() {
        return filterService.getActiveFilterType();
    }

}
