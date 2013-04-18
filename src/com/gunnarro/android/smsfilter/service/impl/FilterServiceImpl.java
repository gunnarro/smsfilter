package com.gunnarro.android.smsfilter.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.SQLException;

import com.gunnarro.android.smsfilter.contentprovider.FilterContentProvider;
import com.gunnarro.android.smsfilter.custom.CustomLog;
import com.gunnarro.android.smsfilter.domain.Filter;
import com.gunnarro.android.smsfilter.domain.Item;
import com.gunnarro.android.smsfilter.domain.SMS;
import com.gunnarro.android.smsfilter.domain.SMSLog;
import com.gunnarro.android.smsfilter.domain.Setting;
import com.gunnarro.android.smsfilter.repository.FilterRepository;
import com.gunnarro.android.smsfilter.repository.impl.FilterRepositoryImpl;
import com.gunnarro.android.smsfilter.repository.table.FilterTable;
import com.gunnarro.android.smsfilter.service.FilterService;

/**
 * Class to store and read values from the android shared preferences.
 * 
 * @author gunnarro
 * 
 */
public class FilterServiceImpl implements FilterService {

    private FilterRepository filterRepository;

    private Context context;
    private SharedPreferences appSharedPrefs;
    private Editor prefsEditor;

    public enum FilterTypeEnum {
        ALLOW_ALL("allow_all"), SMS_BLACK_LIST("sms_black_list"), SMS_WHITE_LIST("sms_white_list"), SMS_CONTACTS("contacts");

        public String filterType;

        FilterTypeEnum(String filterType) {
            this.filterType = filterType;
        }

        public boolean isAllowAll() {
            return this.equals(FilterTypeEnum.ALLOW_ALL);
        }

        public boolean isContacts() {
            return this.equals(FilterTypeEnum.SMS_CONTACTS);
        }

        public boolean isWhiteList() {
            return this.equals(FilterTypeEnum.SMS_WHITE_LIST);
        }

        public boolean isBlackList() {
            return this.equals(FilterTypeEnum.SMS_BLACK_LIST);
        }

    }

    /**
     * Default constructor
     */
    public FilterServiceImpl() {
    }

    /**
     * 
     * @param context
     */
    public FilterServiceImpl(Context context) {
        this.context = context;
        this.appSharedPrefs = context.getSharedPreferences(APP_SHARED_PREFS, Activity.MODE_PRIVATE);
        this.prefsEditor = appSharedPrefs.edit();
        // The repository is opened and closed by the activity that use the
        // filter service. Which is done in the onPause() and onResume() methods
        // of the activity.
        this.filterRepository = new FilterRepositoryImpl(context);
        this.filterRepository.open();
    }

    public static String createSearch(String value) {
        String filter = "^" + value.replace("*", "") + ".*";
        if (value.startsWith("+")) {
            filter = "^\\" + value.replace("*", "") + ".*";
        }
        return filter;
    }

//    /**
//     * {@inheritDoc}
//     */
//    @Override
//    public void open() throws SQLException {
//        filterRepository.open();
//    }
//
//    /**
//     * {@inheritDoc}
//     */
//    @Override
//    public void close() {
//        filterRepository.close();
//    }

    public List<Item> getList(String type) {
        List<Item> list = new ArrayList<Item>();
        // The items are stored as comma separated values
        String storedValues = getListAsString(type);
        if (storedValues != null && storedValues.length() > 1) {
            for (String valuePair : storedValues.split(SEPARATOR)) {
                Item item = Item.createItem(valuePair);
                if (item != null) {
                    list.add(item);
                    CustomLog.i(this.getClass(), "value=" + item.toValuePair());
                } else {
                    CustomLog.e(this.getClass(), "Error creating item for: " + valuePair);
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
        String list = getListAsString("SMS_BLOCKED_LOG");
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
            CustomLog.i(this.getClass(), "empty value=" + item.toValuePair());
        } else if (item.getValue().length() > 1 && !listStr.toString().contains(item.getValue())) {
            // this was a new item, add it to the list
            listStr.append(SEPARATOR).append(item.toValuePair());
            CustomLog.i(this.getClass(), "new value=" + item.toValuePair());
        } else if (item.getValue().length() > 1) {
            // this was an existing item, update the items enabled value
            listStr = new StringBuffer();
            for (Item currItem : getList(type)) {
                if (currItem.getValue().equals(item.getValue())) {
                    currItem.setEnabled(item.isEnabled());
                    CustomLog.i(this.getClass(), "update value=" + currItem.toValuePair());
                }
                listStr.append(currItem.toValuePair()).append(SEPARATOR);
            }
            // Strip of last separator
            listStr.delete(listStr.toString().length() - 1, listStr.toString().length());
        }
        prefsEditor.putString(type, listStr.toString());
        boolean updated = prefsEditor.commit();
        CustomLog.i(this.getClass(), "value=" + listStr.toString() + " updated=" + updated);
        debugList(type);
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
        CustomLog.d(this.getClass(), ".isBlocked(): Filter type=" + activeFilterType + ", number=" + phoneNumber + ", isBlocked=" + isBlocked);
        return isBlocked;
    }

    /**
     * {@inheritDoc}
     * 
     * @deprecated
     */
    // @Override
    public FilterTypeEnum getActiveFilterType() {
        String filterType = getValue(FilterService.SMS_ACTIVE_FILTER_TYPE);
        try {
            return FilterTypeEnum.valueOf(filterType);
        } catch (Exception e) {
            CustomLog.e(this.getClass(), "Filtertype not found for:" + filterType + ", " + e.getMessage());
            return null;
        }
    }

    private void logBlockedSMS(String phoneNumber) {
        String blockedSMS = System.currentTimeMillis() + ":" + phoneNumber;
        updateList("SMS_BLOCKED_LOG", new Item(blockedSMS, true));
    }

    public void debugList(String type) {
        for (Item item : getList(type)) {
            CustomLog.d(this.getClass(), item.toValuePair());
        }
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

    // ******************************' with sqllite *********************

    /**
     * {@inheritDoc}
     */
    @Override
    public Setting readActiveFilterType() {
        return this.filterRepository.readActiveFilterType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean updateActiveFilterType(Setting activeFilter) {
        return this.filterRepository.updateActiveFilterType(activeFilter);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Item> getFilterList(String type) {
        List<Item> list = new ArrayList<Item>();
        String[] projection = FilterTable.TABLE_COLUMNS;
        Cursor cursor = context.getContentResolver().query(FilterContentProvider.CONTENT_URI, projection, getSelection(), new String[] { type }, getOrderBy());
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            // list.add(mapCursorToItem(cursor));
            cursor.moveToNext();
        }
        // Make sure to close the cursor
        cursor.close();
        CustomLog.d(this.getClass(), "type=" + type);
        return list;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean deleteFilter(Item item) {
        StringBuffer where = new StringBuffer();
        where.append(FilterTable.COLUMN_FILTER_NAME).append(" LIKE ?");
        // FIXME
        String[] selectionArgs = null;// { item.getType() };
        int deleted = context.getContentResolver().delete(FilterContentProvider.CONTENT_URI, where.toString(), selectionArgs);
        CustomLog.d(this.getClass(), item.toValuePair() + "; deleted id=" + deleted);
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean deleteFilterAll(String filterName) {
        StringBuffer where = new StringBuffer();
        where.append(FilterTable.COLUMN_FILTER_NAME).append(" LIKE ?");
        String[] selectionArgs = { filterName };
        int deleted = context.getContentResolver().delete(FilterContentProvider.CONTENT_URI, where.toString(), selectionArgs);
        CustomLog.d(this.getClass(), filterName + "; deleted all id=" + deleted);
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean updateFilter(Item item) {
        // StringBuffer where = new StringBuffer();
        // where.append(FilterTable.COLUMN_FILTER_NAME).append(" LIKE ?");
        // String[] selectionArgs = { item.getType() };
        // ContentValues values =
        // FilterTable.createContentValues(item.getType(),
        // Boolean.TRUE.toString(), item.getValue(),
        // item.isEnabled().toString());
        // int update =
        // context.getContentResolver().update(FilterContentProvider.CONTENT_URI,
        // values, where.toString(), selectionArgs);
        // CustomLog.d(this.getClass(), item.toValuePair() + "; updated id=" +
        // update);
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean createFilter(Item item) {
        // Uri insert =
        // context.getContentResolver().insert(FilterContentProvider.CONTENT_URI,
        // FilterTable.createContentValues(item.getType(), "true",
        // item.getValue(), item.isEnabled().toString()));
        // CustomLog.d(this.getClass(), item.toValuePair());
        return true;
    }

    private String getSelection() {
        return "filter_name LIKE ?";
    }

    private String getOrderBy() {
        return "filter_name ASC";
    }

    public Filter turnFilterOnOff(Filter filter) {
        if (filter.isActivated()) {
            // Then we have to check that only on filter is activated at time.
        }
        this.filterRepository.updateFilter(filter);
        return null;
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
     * @param item
     * @return
     */
    public boolean createLog(SMSLog log) {
        return filterRepository.createLog(log);
    }

    /**
     * 
     * @param item
     * @return
     */
    public boolean deleteLogAll() {
        return filterRepository.deleteLog(null);
    }

    /**
     * 
     * @return
     */
    public List<SMSLog> getLogList() {
        return filterRepository.getLogList();
    }

}
