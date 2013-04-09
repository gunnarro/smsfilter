package com.gunnarro.android.smsfilter.repository.impl;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.gunnarro.android.smsfilter.custom.CustomLog;
import com.gunnarro.android.smsfilter.domain.Filter;
import com.gunnarro.android.smsfilter.domain.Item;
import com.gunnarro.android.smsfilter.domain.SMSLog;
import com.gunnarro.android.smsfilter.domain.Setting;
import com.gunnarro.android.smsfilter.repository.FilterDataBaseHjelper;
import com.gunnarro.android.smsfilter.repository.FilterRepository;
import com.gunnarro.android.smsfilter.repository.table.FilterTable;
import com.gunnarro.android.smsfilter.repository.table.ItemTable;
import com.gunnarro.android.smsfilter.repository.table.SMSLogTable;
import com.gunnarro.android.smsfilter.repository.table.SettingTable;
import com.gunnarro.android.smsfilter.service.FilterService;

public class FilterRepositoryImpl implements FilterRepository {

    // Database fields
    private SQLiteDatabase database;
    private FilterDataBaseHjelper dbHelper;

    public FilterRepositoryImpl(Context context) {
        dbHelper = new FilterDataBaseHjelper(context);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
        dbHelper.close();
    }

    // ******************************************************
    // Settings operations
    // ******************************************************

    /**
     * {@inheritDoc}
     */
    @Override
    public Setting readActiveFilterType() {
        Setting setting = null;
        String selection = SettingTable.COLUMN_NAME + " LIKE ?";
        String[] selectionArgs = { FilterService.SMS_ACTIVE_FILTER_TYPE };
        String groupBy = null;
        String orderBy = null;
        Cursor cursor = database.query(SettingTable.TABLE_NAME, FilterTable.TABLE_COLUMNS, selection, selectionArgs, groupBy, null, orderBy);
        if (cursor != null) {
            cursor.moveToFirst();
            setting = mapCursorToSetting(cursor);
        }
        // Make sure to close the cursor
        cursor.close();
        CustomLog.d(this.getClass(), "type=" + setting);
        return setting;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean updateActiveFilterType(Setting filterSetting) {
        StringBuffer where = new StringBuffer();
        where.append(SettingTable.COLUMN_NAME).append(" LIKE ?");
        where.append(" AND ").append(SettingTable.COLUMN_VALUE).append(" LIKE ? ");
        String[] selectionArgs = { filterSetting.getValue() };
        ContentValues values = SettingTable.createContentValues(filterSetting.getName(), filterSetting.getValue());
        int update = database.update(FilterTable.TABLE_NAME, values, where.toString(), selectionArgs);
        CustomLog.d(this.getClass(), filterSetting + "; updated id=" + update);
        return false;
    }

    private Setting mapCursorToSetting(Cursor cursor) {
        return new Setting(cursor.getString(0), cursor.getString(2));
    }

    // ******************************************************
    // Filter operations
    // ******************************************************

    /**
     * {@inheritDoc}
     */
    @Override
    public Filter getFilter(String filterName) {
        Filter filter = null;
        String selection = FilterTable.COLUMN_FILTER_NAME + " LIKE ?";
        String[] selectionArgs = { filterName };
        Cursor cursor = database.query(FilterTable.TABLE_NAME, FilterTable.TABLE_COLUMNS, selection, selectionArgs, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            filter = mapCursorToFilter(cursor);
        }
        // Make sure to close the cursor
        cursor.close();
        CustomLog.d(this.getClass(), "filter=" + filter);
        return filter;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Filter getFilter(Integer id) {
        Filter filter = null;
        String orderBy = " value ASC";
        String selection = FilterTable.COLUMN_ID + " = ?";
        String[] selectionArgs = { id.toString() };
        Cursor cursor = database.query(FilterTable.TABLE_NAME, FilterTable.TABLE_COLUMNS, selection, selectionArgs, null, null, orderBy);
        if (cursor != null) {
            cursor.moveToFirst();
            filter = mapCursorToFilter(cursor);
        }
        // Make sure to close the cursor
        cursor.close();
        CustomLog.d(this.getClass(), "filter=" + filter);
        return filter;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Filter> getFilterList() {
        List<Filter> list = new ArrayList<Filter>();
        String orderBy = " value ASC";
        Cursor cursor = database.query(FilterTable.TABLE_NAME, FilterTable.TABLE_COLUMNS, null, null, null, null, orderBy);
        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                list.add(mapCursorToFilter(cursor));
                cursor.moveToNext();
            }
        }
        // Make sure to close the cursor
        cursor.close();
        CustomLog.d(this.getClass(), "filters=" + cursor.getCount());
        return list;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean deleteFilter(String filterName) {
        StringBuffer where = new StringBuffer();
        where.append(FilterTable.COLUMN_FILTER_NAME).append(" LIKE ?");
        String[] selectionArgs = { filterName };
        int deleted = database.delete(FilterTable.TABLE_NAME, where.toString(), selectionArgs);
        CustomLog.d(this.getClass(), filterName + "; deleted id=" + deleted);
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
        int deleted = database.delete(FilterTable.TABLE_NAME, where.toString(), selectionArgs);
        CustomLog.d(this.getClass(), filterName + "; deleted all id=" + deleted);
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean updateFilter(Filter filter) {
        StringBuffer where = new StringBuffer();
        where.append(FilterTable.COLUMN_FILTER_NAME).append(" LIKE ?");
        String[] selectionArgs = { filter.getName() };
        ContentValues values = FilterTable.createContentValues(filter.getName(), filter.isActivated().toString());
        int update = database.update(FilterTable.TABLE_NAME, values, where.toString(), selectionArgs);
        CustomLog.d(this.getClass(), filter + "; updated id=" + update);
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean createFilter(Filter filter) {
        ContentValues values = FilterTable.createContentValues(filter.getName(), filter.isActivated().toString());
        long insert = database.insert(FilterTable.TABLE_NAME, null, values);
        CustomLog.d(this.getClass(), filter.toString());
        return true;
    }

    private Filter mapCursorToFilter(Cursor cursor) {
        return new Filter(cursor.getInt(0), cursor.getString(1), Boolean.parseBoolean(cursor.getString(2)));
    }

    // ******************************************************
    // Item operations
    // ******************************************************

    private Item mapCursorToItem(Cursor cursor) {
        return new Item(cursor.getInt(0), cursor.getInt(1), cursor.getString(2), Boolean.parseBoolean(cursor.getString(3)));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Item> getItemList(String filterName) {
        List<Item> list = new ArrayList<Item>();
        StringBuffer sqlQuery = new StringBuffer();
        sqlQuery.append("SELECT i._id, i.fk_filter_id, i.value, i.selected");
        sqlQuery.append(" FROM items i, filters f");
        sqlQuery.append(" WHERE f.filter_name LIKE ?");
        sqlQuery.append(" AND i.fk_filter_id = f._id");
        String[] selectionArgs = { filterName };
        Cursor cursor = database.rawQuery(sqlQuery.toString(), selectionArgs);
        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                list.add(mapCursorToItem(cursor));
                cursor.moveToNext();
            }
        }
        CustomLog.d(this.getClass(), "for filter=" + filterName + ", hits=" + cursor.getCount());
        // Make sure to close the cursor
        cursor.close();
        return list;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean deleteItem(Item item) {
        StringBuffer where = new StringBuffer();
        where.append(ItemTable.COLUMN_FK_FILTER_ID).append(" LIKE ?");
        where.append(" AND ").append(ItemTable.COLUMN_VALUE).append(" LIKE ?");
        String[] selectionArgs = { item.getFkFilterId().toString(), item.getValue() };
        int deleted = database.delete(ItemTable.TABLE_NAME, where.toString(), selectionArgs);
        CustomLog.d(this.getClass(), item.toValuePair() + "; deleted id=" + deleted);
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean updateItem(Item item) {
        StringBuffer where = new StringBuffer();
        where.append(ItemTable.COLUMN_FK_FILTER_ID).append(" LIKE ?");
        where.append(" AND ").append(ItemTable.COLUMN_VALUE).append(" LIKE ?");
        String[] selectionArgs = { item.getFkFilterId().toString(), item.getValue() };
        ContentValues values = ItemTable.createContentValues(item.getFkFilterId(), item.getValue(), item.isEnabled().toString());
        int update = database.update(ItemTable.TABLE_NAME, values, where.toString(), selectionArgs);
        CustomLog.d(this.getClass(), item.toValuePair() + "; updated id=" + update);
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean createItem(Item item) {
        ContentValues values = ItemTable.createContentValues(item.getFkFilterId(), item.getValue(), item.isEnabled().toString());
        long insert = database.insert(ItemTable.TABLE_NAME, null, values);
        CustomLog.d(this.getClass(), item.toValuePair());
        return true;
    }

    // ******************************************************
    // Log operations
    // ******************************************************

    /**
     * {@inheritDoc}
     */
    @Override
    public List<SMSLog> getLogList() {
        List<SMSLog> list = new ArrayList<SMSLog>();
        String groupBy = null;
        String orderBy = "received_date ASC";
        Cursor cursor = database.query(SMSLogTable.TABLE_NAME, SMSLogTable.TABLE_COLUMNS, null, null, groupBy, null, orderBy);
        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                list.add(mapCursorToLog(cursor));
                cursor.moveToNext();
            }
        }
        // Make sure to close the cursor
        cursor.close();
        return list;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean createLog(SMSLog log) {
        ContentValues values = SMSLogTable.createContentValues(log.getType(), log.getValue(), log.getType());
        long insertId = database.insert(SMSLogTable.TABLE_NAME, null, values);
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean deleteLog(SMSLog log) {
        int delete = database.delete(SMSLogTable.TABLE_NAME, null, null);
        return true;
    }

    private SMSLog mapCursorToLog(Cursor cursor) {
        return new SMSLog(cursor.getString(1), cursor.getString(3), Boolean.parseBoolean(cursor.getString(4)));
    }
}
