package com.gunnarro.android.smsfilter.service;

import java.util.List;

import android.database.SQLException;

import com.gunnarro.android.smsfilter.domain.Item;
import com.gunnarro.android.smsfilter.domain.SMS;
import com.gunnarro.android.smsfilter.domain.SMSLog;
import com.gunnarro.android.smsfilter.domain.Setting;

public interface FilterService {

    /** Holds current selected filter type */
    public static final String SMS_FILTER_ACTIVATED = "sms_filter_activated";
    public static final String SMS_ACTIVE_FILTER_TYPE = "sms_active_filter_type";

    public static final String APP_SHARED_PREFS = "user_preferences";
    public static final String DEFAULT_VALUE = "";
    public static final String SEPARATOR = ";";

    /**
     * Method to open repository starting leaving activity.
     * 
     * @throws SQLException
     */
//    public void open() throws SQLException;

    /**
     * Method to close repository when leaving or the activity is put on pause.
     */
//    public void close();

    /**
     * return the list items as a string, ref. getList(String type)
     * 
     * @param type
     * @return
     */
    public abstract String getListAsString(String type);

    /**
     * Method remove all items from the list type.
     * 
     * @param type list type which holds the items.
     * @return true if all items was successfully removed, false otherwise.
     */
    public abstract boolean removeAllList(String type);

    /**
     * Method to update a item in the given list type.
     * 
     * @param type list type which holds the item.
     * @param item item to update
     * @return true if the item was successfully updated, false otherwise.
     */
    public abstract boolean updateList(String type, Item item);

    /**
     * Method to remove a item from the given list type.
     * 
     * @param type list type which holds the item.
     * @param item item to remove
     * @return true if the item was removed, false otherwise.
     */
    public abstract boolean removeList(String type, Item item);

    /**
     * Method to search for a given value in a list.
     * 
     * @param type list type which holds the item.
     * @param value value to search after
     * @return true if the list contains the item, false otherwise.
     */
    public abstract Item searchList(String type, String value);

    /**
     * Method to save a item to a resource type.
     * 
     * @param type
     * @param value
     */
    public void save(String type, String value);

    /**
     * Method to read a single vale from a resourceF.
     * 
     * @param type
     * @return
     */
    public String getValue(String type);

    /**
     * 
     * @param groupBy
     * @return
     */
    public List<SMS> getSMSList(String groupBy);

    /**
     * Method to check if the phone number is blocked or not. If that's the
     * case, the SMS is ignored and only logged to the SMSFilters blocked log.
     * Otherwise, the SMS is handled as normal.
     * 
     * @param phoneNumber
     * @return true if the phone number is blocked false otherwise.
     */
    public boolean isBlocked(String phoneNumber);

    /**
     * Tells whether the sms filter is activated or not.
     * 
     * @return true id activated, false otherwise
     */
    public boolean isSMSFilterActivated();

    /**
     * Method to get active filter type, which is stored in the internal app
     * preference table.
     * 
     * @return selected filter type
     */
    public Setting readActiveFilterType();

    /**
     * Method to set active filter type. preference table.
     * 
     */
    public boolean updateActiveFilterType(Setting activeFilter);

    // ******************************************************
    // Filter operations
    // ******************************************************

    /**
     * Returns the list of item for the given list type. Allowed list types are
     * as follows: <li>SMS_BLACK_LIST</li> <li>SMS_WHITE_LIST</li> <li>
     * SMS_BLOCKED_LOG</li>
     * 
     * @param type
     * @return
     */
    public List<Item> getFilterList(String type);

    /**
     * 
     * @param item
     * @return
     */
    public boolean createFilter(Item item);

    /**
     * 
     * @param item
     * @return
     */
    public boolean deleteFilter(Item item);

    /**
     * 
     * @param filterName
     * @return
     */
    public boolean deleteFilterAll(String filterName);

    /**
     * 
     * @param item
     * @return
     */
    public boolean updateFilter(Item item);

    // ******************************************************
    // Item operations
    // ******************************************************
    /**
     * 
     * @param filterType
     * @return
     */
    public List<Item> getItemList(String filterType);

    /**
     * 
     * @param item
     * @return
     */
    public boolean deleteItem(Item item);

    /**
     * 
     * @param item
     * @return
     */
    public boolean updateItem(Item item);

    /**
     * 
     * @param item
     * @return
     */
    public boolean createItem(String filterName, Item item);

    // ******************************************************
    // Log operations
    // ******************************************************

    /**
     * 
     * @param item
     * @return
     */
    public boolean createLog(SMSLog log);

    /**
     * 
     * @param item
     * @return
     */
    public boolean deleteLogAll();

    public void debugList(String type);
}
