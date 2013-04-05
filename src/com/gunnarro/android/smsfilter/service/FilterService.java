package com.gunnarro.android.smsfilter.service;

import java.util.List;

import com.gunnarro.android.smsfilter.domain.Item;
import com.gunnarro.android.smsfilter.domain.SMS;
import com.gunnarro.android.smsfilter.service.FilterServiceImpl.FilterTypeEnum;

public interface FilterService {

    /** Holds current selected filter type */
    public static final String SMS_FILTER_ACTIVATED = "sms_filter_activated";
    public static final String SMS_FILTER_TYPE = "sms_filter_type";
    public static final String SMS_BLACK_LIST = "sms_black_list";
    public static final String SMS_WHITE_LIST = "sms_white_list";
    public static final String SMS_BLOCKED_LOG = "sms_blocked_log";

    public static final String APP_SHARED_PREFS = "user_preferences";
    public static final String DEFAULT_VALUE = "";
    public static final String SEPARATOR = ";";

    /**
     * Returns the list of item for the given list type. Allowed list types are
     * as follows: <li>SMS_BLACK_LIST</li> <li>SMS_WHITE_LIST</li> <li>
     * SMS_BLOCKED_LOG</li>
     * 
     * @param type
     * @return
     */
    public abstract List<Item> getList(String type);

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
     * Method to get active filter type, which is stored in the internal app
     * preference table.
     * 
     * @return selected filter type
     */
    public FilterTypeEnum getActiveFilterType();

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

}
