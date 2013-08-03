package com.gunnarro.android.smsfilter.repository;

import java.util.List;

import android.database.SQLException;

import com.gunnarro.android.smsfilter.domain.Filter;
import com.gunnarro.android.smsfilter.domain.Item;
import com.gunnarro.android.smsfilter.domain.MsgLog;

public interface FilterRepository {

    /**
     * The repository should be opened by the activity that use the repository.
     * This should be done in the onCreate() and onResume() methods of the
     * activity.
     * 
     * @throws SQLException
     */
    public void open() throws SQLException;

    /**
     * The repository should be closed by the activity that use the repository.
     * This should be done in the onPause() method of the activity.
     * 
     * @throws SQLException
     */
    public void close();

    // ******************************************************
    // Settings operations
    // ******************************************************

    /**
     * Method to check if the message filter is turned on or off.
     * 
     * @return true if the filter is activated, false otherwise
     */
    public boolean isMsgFilterActivated();

    /**
     * Method to check if the message filter period is turned on or off.
     * 
     * @return true if the filter period is activated, false otherwise
     */
    public boolean isMsgFilterPeriodActivated();

    /**
     * Method to activate or deactivate the message filter.
     * 
     * @param isActivated true if the filter is activated, false otherwise
     */
    public void updateMsgFilterActivated(boolean isActivated);

    /**
     * Method to activate or deactivate the message filter time period.
     * 
     * @param isActivated true if the filter is activated, false otherwise
     */
    public void updateMsgFilterPeriodActivated(boolean isActivated);

    /**
     * 
     */
    public void updateMsgFilterPeriodFromTime(String fromTime);

    /**
     * 
     */
    public void updateMsgFilterPeriodToTime(String toTime);

    /**
     * 
     */
    public String getMsgFilterPeriodFromTime();

    /**
     * 
     */
    public String getMsgFilterPeriodToTime();

    /**
     * 
     * @return
     */
    public boolean isLogMsg();

    // ******************************************************
    // Filter operations
    // ******************************************************

    /**
     * Method to get active filter type, which is stored in the internal app
     * preference table.
     * 
     * @return selected filter type
     */
    public Filter getActiveFilter();

    /**
     * 
     * @param filterName
     * @return
     */
    public Filter getFilter(String filterName);

    /**
     * 
     * @param filterName
     * @return
     */
    public Filter getFilter(Integer id);

    /**
     * 
     * @param type
     * @return
     */
    public List<Filter> getFilterList();

    /**
     * 
     * @param item
     * @return
     */
    public boolean deleteFilter(String filterName);

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
    public boolean updateFilter(Filter filter);

    /**
     * 
     * @param item
     * @return
     */
    public boolean createFilter(Filter filter);

    // ******************************************************
    // Item operations
    // ******************************************************

    /**
     * 
     * @param type
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
    public boolean createItem(Item item);

    // ******************************************************
    // Log operations
    // ******************************************************

    public List<MsgLog> getLogListOrderByDate(String msgType);

    public List<MsgLog> getLogList(String groupBy, String msgType);

    /**
     * 
     * @param item
     * @return
     */
    public boolean createLog(MsgLog log);

    /**
     * 
     * @return
     */
    public boolean removeAllLog(String msgType);

}
