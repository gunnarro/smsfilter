package com.gunnarro.android.smsfilter.service;

import java.util.List;

import com.gunnarro.android.smsfilter.domain.Item;
import com.gunnarro.android.smsfilter.domain.SMSLog;
import com.gunnarro.android.smsfilter.service.impl.FilterServiceImpl.FilterTypeEnum;

public interface FilterService {

    public static final String DEFAULT_VALUE = "";

    /**
     * Method to search for a given value in a list.
     * 
     * @param type list type which holds the item.
     * @param value value to search after
     * @return true if the list contains the item, false otherwise.
     */
    public abstract Item searchList(String type, String value);

    /**
     * Method to check if the phone number is blocked or not. If that's the
     * case, the SMS is ignored and only logged to the SMSFilters blocked log.
     * Otherwise, the SMS is handled as normal.
     * 
     * @param phoneNumber
     * @return true if the phone number is blocked false otherwise.
     */
    public boolean isBlocked(String phoneNumber);

    // ******************************************************
    // Filter operations
    // ******************************************************

    /**
     * Method to get active filter type, which is stored in the internal app
     * preference table.
     * 
     * @return selected filter type
     */
    public FilterTypeEnum getActiveFilterType();

    /**
     * Method to activate filter type
     * 
     */
    public void activateFilterType(FilterTypeEnum filterType);

    /**
     * Method to de-activate filter type
     * 
     */
    public void deActivateFilterType(FilterTypeEnum filterType);

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

    public List<SMSLog> getLogsStartDateAndEndDate();

    /**
     * 
     * @param groupBy
     * @return
     */
    public List<SMSLog> getLogs(String groupBy);

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
    public boolean removeAllLog();

    // ******************************************************
    // Settings operations
    // ******************************************************

    /**
     * Tells whether the sms filter is activated or not.
     * 
     * @return true id activated, false otherwise
     */
    public boolean isSMSFilterActivated();

    /**
     * Method to turn sms filter on
     */
    public void activateSMSFilter();

    /**
     * Method to turn sms filter off
     */
    public void deactivateSMSFilter();

    /**
     * Method to check if all sms should be logged or not.
     * 
     * @return
     */
    public boolean isLogSMS();
}
