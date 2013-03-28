package com.gunnarro.android.smsfilter;

import java.util.List;

import com.gunnarro.android.smsfilter.sms.SMS;
import com.gunnarro.android.smsfilter.view.Item;

public interface AppPreferences {

    /** Holds current selected filter type */
    public static final String SMS_FILTER_TYPE = "sms_filter_type";
    public static final String SMS_BLACK_LIST = "sms_black_list";
    public static final String SMS_WHITE_LIST = "sms_white_list";
    public static final String SMS_BLOCKED_LOG = "sms_blocked_log";

    public static final String APP_SHARED_PREFS = "user_preferences";
    public static final String DEFAULT_VALUE = "";
    public static final String SEPARATOR = ";";

    /**
     * 
     * @param type
     * @return
     */
    public abstract List<Item> getList(String type);

    /**
     * 
     * @param type
     * @return
     */
    public abstract String getListAsString(String type);

    /**
     * Generic method remove all items from the list type.
     * 
     * @param type list type which holds the items.
     * @return true if all items was successfully removed, false otherwise.
     */
    public abstract boolean removeAllList(String type);

    /**
     * Generic method to update a item in the given list type.
     * 
     * @param type list type which holds the item.
     * @param item item to update
     * @return true if the item was successfully updated, false otherwise.
     */
    public abstract boolean updateList(String type, Item item);

    /**
     * Generic method to remove a item from the given list type.
     * 
     * @param type list type which holds the item.
     * @param item item to remove
     * @return true if the item was removed, false otherwise.
     */
    public abstract boolean removeList(String type, Item item);

    /**
     * Generic method the check if the given list type contains a item.
     * 
     * @param type list type which holds the item.
     * @param value item to check
     * @return true if the list contains the item, false otherwise.
     */
    public abstract boolean listContains(String type, String value);

    /**
     * Generic method to save a item to a resource type.
     * 
     * @param type
     * @param value
     */
    public void save(String type, Item value);

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

}
