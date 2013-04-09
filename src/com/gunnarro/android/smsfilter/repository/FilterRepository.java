package com.gunnarro.android.smsfilter.repository;

import java.util.List;

import android.database.SQLException;

import com.gunnarro.android.smsfilter.domain.Filter;
import com.gunnarro.android.smsfilter.domain.Item;
import com.gunnarro.android.smsfilter.domain.SMSLog;
import com.gunnarro.android.smsfilter.domain.Setting;

public interface FilterRepository {

    /**
     * The repository should be opened by the activity that use the repository.
     * This should be done in the onCreate() and onResume() methods of the activity.
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
    public boolean updateActiveFilterType(Setting filterSetting);

    // ******************************************************
    // Filter operations
    // ******************************************************

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

    public List<SMSLog> getLogList();

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
    public boolean deleteLog(SMSLog log);
}
