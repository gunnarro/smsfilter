package com.gunnarro.android.smsfilter.service.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.content.Context;

import com.gunnarro.android.smsfilter.custom.CustomLog;
import com.gunnarro.android.smsfilter.domain.Filter;
import com.gunnarro.android.smsfilter.domain.Item;
import com.gunnarro.android.smsfilter.domain.SMSLog;
import com.gunnarro.android.smsfilter.repository.FilterRepository;
import com.gunnarro.android.smsfilter.repository.impl.FilterRepositoryImpl;
import com.gunnarro.android.smsfilter.service.FilterService;

/**
 * Service class to store and read sms filter values and settings from the
 * sqlite DB.
 * 
 * @author gunnarro
 * 
 */
public class FilterServiceImpl implements FilterService {

    private FilterRepository filterRepository;

    public enum FilterTypeEnum {
        ALLOW_ALL, SMS_BLACK_LIST, SMS_WHITE_LIST, CONTACTS;

        public boolean isAllowAll() {
            return this.equals(FilterTypeEnum.ALLOW_ALL);
        }

        public boolean isContacts() {
            return this.equals(FilterTypeEnum.CONTACTS);
        }

        public boolean isWhiteList() {
            return this.equals(FilterTypeEnum.SMS_WHITE_LIST);
        }

        public boolean isBlackList() {
            return this.equals(FilterTypeEnum.SMS_BLACK_LIST);
        }

    }

    /**
     * 
     * @param context
     */
    public FilterServiceImpl(Context context) {
        // The repository is opened and closed by the activity that use the
        // filter service. Which is done in the onPause() and onResume() methods
        // of the activity.
        this.filterRepository = new FilterRepositoryImpl(context);
        this.filterRepository.open();
    }

    private static String createSearch(String value) {
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
    public Item searchList(String type, String value) {
        List<Item> itemList = filterRepository.getItemList(type);
        for (Item item : itemList) {
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
    public boolean isLogSMS() {
        return this.filterRepository.isLogSMS();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isSMSFilterActivated() {
        return this.filterRepository.isSMSFilterActivated();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void activateSMSFilter() {
        this.filterRepository.updateSMSFilterActivated(true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deactivateSMSFilter() {
        this.filterRepository.updateSMSFilterActivated(false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isBlocked(String phoneNumber) {
        boolean isBlocked = false;
        FilterTypeEnum activeFilterType = getActiveFilterType();
        if (activeFilterType == null) {
            CustomLog.e(this.getClass(), "isBlocked(): BUG: filter type not found, do not block sms!");
            return false;
        }

        if (activeFilterType.isAllowAll()) {
            // wide open for everyone
            isBlocked = false;
        } else if (activeFilterType.isContacts()) {
            // TODO
            // Check contact list
            isBlocked = false;
        } else if (activeFilterType.isBlackList()) {
            Item item = searchList(activeFilterType.name(), phoneNumber);
            if (item != null && item.isEnabled()) {
                isBlocked = true;
            }
        } else if (activeFilterType.isWhiteList()) {
            Item item = searchList(activeFilterType.name(), phoneNumber);
            if (item == null || (item != null && !item.isEnabled())) {
                isBlocked = true;
            }
        }
        if (isBlocked) {
            logBlockedSMS(phoneNumber, activeFilterType.name());
        }
        CustomLog.d(this.getClass(), ".isBlocked(): Filter type=" + activeFilterType + ", number=" + phoneNumber + ", isBlocked=" + isBlocked);
        return isBlocked;
    }

    private void logBlockedSMS(String phoneNumber, String filterType) {
        filterRepository.createLog(new SMSLog(Calendar.getInstance().getTimeInMillis(), phoneNumber, SMSLog.STATUS_SMS_BLOCKED, filterType));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FilterTypeEnum getActiveFilterType() {
        Filter activeFilter = this.filterRepository.getActiveFilter();
        if (activeFilter != null) {
            return FilterTypeEnum.valueOf(activeFilter.getName());
        } else {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void activateFilterType(FilterTypeEnum filterType) {
        if (filterType != null) {
            filterRepository.updateFilter(new Filter(filterType.name(), true));
        } else {
            CustomLog.e(this.getClass(), "BUG! Filter type was null!");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deActivateFilterType(FilterTypeEnum filterType) {
        if (filterType != null) {
            filterRepository.updateFilter(new Filter(filterType.name(), false));
        } else {
            CustomLog.e(this.getClass(), "BUG! Filter type was null!");
        }
    }

    // ******************************************************
    // Item operations
    // ******************************************************
    /**
     * {@inheritDoc}
     */
    @Override
    public List<Item> getItemList(String filterType) {
        return this.filterRepository.getItemList(filterType);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean deleteItem(Item item) {
        return this.filterRepository.deleteItem(item);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean updateItem(Item item) {
        return this.filterRepository.updateItem(item);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean createItem(String filterName, Item item) {
        // get filter id for selected filter name
        Filter filter = filterRepository.getFilter(filterName);
        // tie this item to correct filter id
        item.setFkFilterId(filter.getId());
        return this.filterRepository.createItem(item);
    }

    // ******************************************************
    // Log operations
    // ******************************************************

    /**
     * 
     * @return
     */
    @Override
    public List<SMSLog> getLogsStartDateAndEndDate() {
        List<SMSLog> list = new ArrayList<SMSLog>();
        List<SMSLog> logListOrderByDate = filterRepository.getLogListOrderByDate();
        if (logListOrderByDate.size() == 0) {
            return null;
        } else if (logListOrderByDate.size() == 1) {
            list.add(logListOrderByDate.get(0));
            list.add(logListOrderByDate.get(0));
        } else if (logListOrderByDate.size() > 1) {
            list.add(logListOrderByDate.get(0));
            list.add(logListOrderByDate.get(logListOrderByDate.size() - 1));
        }
        return list;
    }

    public SMSLog getLogsEndDate() {
        List<SMSLog> logListOrderByDate = filterRepository.getLogListOrderByDate();
        return logListOrderByDate.size() > 0 ? logListOrderByDate.get(0) : null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<SMSLog> getLogs(String groupBy) {
        return filterRepository.getLogList(groupBy);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean createLog(SMSLog log) {
        return filterRepository.createLog(log);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean removeAllLog() {
        return filterRepository.removeAllLog();
    }

}
