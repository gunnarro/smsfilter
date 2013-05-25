package com.gunnarro.android.smsfilter.service.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

import com.gunnarro.android.smsfilter.custom.CustomLog;
import com.gunnarro.android.smsfilter.domain.Filter;
import com.gunnarro.android.smsfilter.domain.Item;
import com.gunnarro.android.smsfilter.domain.Msg;
import com.gunnarro.android.smsfilter.domain.MsgLog;
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
    private Context context;

    public enum FilterTypeEnum {
        SMS_BLACK_LIST, SMS_WHITE_LIST, CONTACTS;

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
     * default constructor, used for unit testing only.
     */
    public FilterServiceImpl() {
    }

    /**
     * 
     * @param context
     */
    public FilterServiceImpl(Context context) {
        // The repository is opened and closed by the activity that use the
        // filter service. Which is done in the onPause() and onResume() methods
        // of the activity.
        this.context = context;
        this.filterRepository = new FilterRepositoryImpl(this.context);
        this.filterRepository.open();
    }

    public static String createSearch(String value) {
        if (value == null || value.isEmpty()) {
            return "";
        }
        String filter = "^" + value.replace("*", "") + ".*";
        if (value.startsWith("+")) {
            filter = "^\\" + value.replace("*", "") + ".*";
        } else if (value.startsWith("hidden")) {
            filter = "[0-9,+]{8,19}";
        }
        return filter;
    }

    /**
     * Method to search for a given value in a list.
     * 
     * @param type list type which holds the item.
     * @param value value to search after
     * @return true if the list contains the item, false otherwise.
     */
    private Item searchList(String type, String value) {
        List<Item> itemList = filterRepository.getItemList(type);
        for (Item item : itemList) {
            String filter = createSearch(item.getValue());
            if (value.matches(filter)) {
                CustomLog.i(FilterServiceImpl.class, "HIT value=" + value + ", filter=" + filter);
                return item;
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isLogMsg() {
        return this.filterRepository.isLogMsg();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isMsgFilterActivated() {
        return this.filterRepository.isMsgFilterActivated();
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
    public boolean isBlocked(Msg msg) {
        boolean isBlocked = false;
        FilterTypeEnum activeFilterType = getActiveFilterType();
        if (activeFilterType == null) {
            CustomLog.e(FilterServiceImpl.class, "isBlocked(): BUG: filter type not found, do not block sms!");
            return false;
        }

        if (activeFilterType.isContacts()) {
            // Check contact list
            if (!isInContactList(msg.getPhoneNumber())) {
                isBlocked = true;
            }
        } else if (activeFilterType.isBlackList()) {
            Item item = searchList(activeFilterType.name(), msg.getPhoneNumber());
            if (item != null && item.isEnabled()) {
                isBlocked = true;
            }
        } else if (activeFilterType.isWhiteList()) {
            Item item = searchList(activeFilterType.name(), msg.getPhoneNumber());
            if (item == null || (item != null && !item.isEnabled())) {
                isBlocked = true;
            }
        }
        if (isBlocked) {
            logBlockedMsg(msg.getPhoneNumber(), activeFilterType.name(), msg.getType());
        }
        CustomLog.d(FilterServiceImpl.class, ".isBlocked(): Filter type=" + activeFilterType + "," + msg.toString() + ", isBlocked=" + isBlocked);
        return isBlocked;
    }

    private void logBlockedMsg(String phoneNumber, String filterType, String msgType) {
        filterRepository.createLog(new SMSLog(Calendar.getInstance().getTimeInMillis(), phoneNumber, MsgLog.STATUS_MSG_BLOCKED, filterType));
    }

    private boolean isInContactList(String phoneNumber) {
        return lookUpContacts(phoneNumber) != null ? true : false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String lookUpContacts(String phoneNumber) {
        String contactDisplayName = null;
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
        String[] projection = new String[] { ContactsContract.PhoneLookup.DISPLAY_NAME };
        // Query the filter URI
        Cursor cursor = this.context.getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
                contactDisplayName = cursor.getString(cursor.getColumnIndex(ContactsContract.Data.DISPLAY_NAME));
            }
            cursor.close();
        }
        return contactDisplayName;
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
            CustomLog.e(FilterServiceImpl.class, "BUG! Filter type was null!");
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
            CustomLog.e(FilterServiceImpl.class, "BUG! Filter type was null!");
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
    public List<MsgLog> getLogsStartDateAndEndDate() {
        List<MsgLog> list = new ArrayList<MsgLog>();
        List<MsgLog> logListOrderByDate = filterRepository.getLogListOrderByDate("%");
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

    public MsgLog getLogsEndDate() {
        List<MsgLog> logListOrderByDate = filterRepository.getLogListOrderByDate("%");
        return logListOrderByDate.size() > 0 ? logListOrderByDate.get(0) : null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<MsgLog> getLogs(String groupBy, String msgType) {
        return filterRepository.getLogList(groupBy, msgType);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean createLog(MsgLog log) {
        return filterRepository.createLog(log);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean removeAllLog(String msgType) {
        return filterRepository.removeAllLog(msgType);
    }

    /**
     * for unit testing only
     */
    public void setFilterRepository(FilterRepository filterRepository) {
        this.filterRepository = filterRepository;
    }

}
