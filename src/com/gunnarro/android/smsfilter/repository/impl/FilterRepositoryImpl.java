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

public class FilterRepositoryImpl implements FilterRepository {

    // Database fields
    private SQLiteDatabase database;
    private FilterDataBaseHjelper dbHelper;

    public FilterRepositoryImpl(Context context) {
        dbHelper = FilterDataBaseHjelper.getInstance(context);
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
    public boolean isSMSFilterActivated() {
        Setting setting = null;
        String selection = SettingTable.COLUMN_KEY + " LIKE ?";
        String[] selectionArgs = { SettingTable.SMS_FILTER_ACTIVATED };
        String groupBy = null;
        String orderBy = null;
        Cursor cursor = database.query(SettingTable.TABLE_NAME, SettingTable.TABLE_COLUMNS, selection, selectionArgs, groupBy, null, orderBy);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            setting = mapCursorToSetting(cursor);
        }
        if (setting == null) {
            throw new RuntimeException("DB not initialize! Please report bug!");
        }
        // Make sure to close the cursor
        cursor.close();
        CustomLog.d(this.getClass(), "type=" + setting.getName() + " value=" + setting.getValue());
        boolean isActivated = setting != null ? Boolean.parseBoolean(setting.getValue()) : false;
        CustomLog.i(this.getClass(), "is sms Filter activated=" + isActivated);
        return isActivated;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateSMSFilterActivated(boolean isActivated) {
        StringBuffer where = new StringBuffer();
        where.append(SettingTable.COLUMN_KEY).append(" LIKE ?");
        String[] selectionArgs = { SettingTable.SMS_FILTER_ACTIVATED };
        ContentValues values = SettingTable.createContentValues(SettingTable.SMS_FILTER_ACTIVATED, Boolean.toString(isActivated));
        database.update(SettingTable.TABLE_NAME, values, where.toString(), selectionArgs);
        CustomLog.d(this.getClass(), "Updated Activated sms filter: " + isActivated);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isLogSMS() {
        Setting setting = null;
        String selection = SettingTable.COLUMN_KEY + " LIKE ?";
        String[] selectionArgs = { SettingTable.LOG_SMS };
        Cursor cursor = database.query(SettingTable.TABLE_NAME, SettingTable.TABLE_COLUMNS, selection, selectionArgs, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            setting = mapCursorToSetting(cursor);
        }
        // Make sure to close the cursor
        cursor.close();
        CustomLog.d(this.getClass(), "type=" + setting + " value=" + setting);
        if (setting != null)
            return Boolean.getBoolean(setting.getValue());
        else
            // FIXME
            return false;
    }

    private Setting mapCursorToSetting(Cursor cursor) {
        return new Setting(cursor.getString(cursor.getColumnIndex(SettingTable.COLUMN_KEY)), cursor.getString(cursor.getColumnIndex(SettingTable.COLUMN_VALUE)));
    }

    // ******************************************************
    // Filter operations
    // ******************************************************

    /**
     * {@inheritDoc}
     */
    @Override
    public Filter getActiveFilter() {
        Filter filter = null;
        String selection = FilterTable.COLUMN_ACTIVATED + " LIKE ?";
        String[] selectionArgs = { "true" };
        String groupBy = null;
        String orderBy = FilterTable.COLUMN_FILTER_NAME + " ASC";
        Cursor cursor = database.query(FilterTable.TABLE_NAME, FilterTable.TABLE_COLUMNS, selection, selectionArgs, groupBy, null, orderBy);
        // CustomLog.d(this.getClass(), "active filter hits=" +
        // cursor.getCount());
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            // CustomLog.d(this.getClass(), "active filter hits=" +
            // cursor.getString(0));
            filter = mapCursorToFilter(cursor);
        }
        // Make sure to close the cursor
        cursor.close();
        CustomLog.d(this.getClass(), "active filter=" + filter);
        return filter;
    }

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
        String orderBy = null;
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
        String orderBy = FilterTable.COLUMN_FILTER_NAME + " ASC";
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
        ContentValues values = FilterTable.createContentValues(filter.getName(), Boolean.toString(filter.isActivated()));
        database.update(FilterTable.TABLE_NAME, values, where.toString(), selectionArgs);
        CustomLog.d(this.getClass(), "filter=" + filter.toString());
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean createFilter(Filter filter) {
        ContentValues values = FilterTable.createContentValues(filter.getName(), filter.isActivated().toString());
        database.insert(FilterTable.TABLE_NAME, null, values);
        CustomLog.d(this.getClass(), filter.toString());
        return true;
    }

    private Filter mapCursorToFilter(Cursor cursor) {
        return new Filter(cursor.getInt(cursor.getColumnIndex(FilterTable.COLUMN_ID)), cursor.getString(cursor.getColumnIndex(FilterTable.COLUMN_FILTER_NAME)),
                Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex(FilterTable.COLUMN_ACTIVATED))));
    }

    // ******************************************************
    // Item operations
    // ******************************************************

    private Item mapCursorToItem(Cursor cursor) {
        return new Item(cursor.getInt(cursor.getColumnIndex(ItemTable.COLUMN_ID)), cursor.getInt(cursor.getColumnIndex(ItemTable.COLUMN_FK_FILTER_ID)),
                cursor.getString(cursor.getColumnIndex(ItemTable.COLUMN_VALUE)), Boolean.parseBoolean(cursor.getString(cursor
                        .getColumnIndex(ItemTable.COLUMN_SELECTED))));
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
        if (cursor != null && cursor.getCount() > 0) {
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
        database.insert(ItemTable.TABLE_NAME, null, values);
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
    public List<SMSLog> getLogListOrderByDate() {
        List<SMSLog> list = new ArrayList<SMSLog>();
        Cursor cursor = database.query(SMSLogTable.TABLE_NAME, SMSLogTable.TABLE_COLUMNS, null, null, null, null, SMSLogTable.COLUMN_RECEIVED_TIME + " ASC");
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            list.add(mapCursorToSMSLog(cursor));
            cursor.moveToLast();
            list.add(mapCursorToSMSLog(cursor));
        }
        // Make sure to close the cursor
        cursor.close();
        return list;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<SMSLog> getLogList(String groupBy) {
        String selectClause = SMSLogTable.COLUMN_RECEIVED_TIME;
        if (groupBy.equalsIgnoreCase("year")) {
            selectClause = "strftime('%Y', datetime(received_time, 'unixepoch'))";
        } else if (groupBy.equalsIgnoreCase("month")) {
            selectClause = "strftime('%m.%Y', datetime(received_time, 'unixepoch'))";
        } else if (groupBy.equalsIgnoreCase("week")) {
            selectClause = "strftime('%W', datetime(received_time, 'unixepoch'))";
        } else if (groupBy.equalsIgnoreCase("day")) {
            selectClause = "strftime('%d.%m', datetime(received_time, 'unixepoch'))";
        } else if (groupBy.equalsIgnoreCase("number")) {
            selectClause = SMSLogTable.COLUMN_PHONE_NUMBER;
        } else if (groupBy.equalsIgnoreCase("filter")) {
            selectClause = SMSLogTable.COLUMN_FILTER_TYPE;
        }
        List<SMSLog> list = new ArrayList<SMSLog>();
        StringBuffer query = new StringBuffer();
        query.append("SELECT ").append(selectClause).append(" AS value");
        query.append(", count(").append(SMSLogTable.COLUMN_RECEIVED_TIME).append(") AS count");
        query.append(" FROM ").append(SMSLogTable.TABLE_NAME);
        query.append(" GROUP BY ").append(selectClause);
        query.append(" ORDER BY ").append(selectClause);

        CustomLog.i(this.getClass(), query.toString());
        Cursor cursor = database.rawQuery(query.toString(), null);
        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                list.add(new SMSLog(cursor.getString(cursor.getColumnIndex("value")), cursor.getInt(cursor.getColumnIndex("count"))));
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
        ContentValues values = SMSLogTable.createContentValues(log.getReceivedTime(), log.getPhoneNumber(), log.getStatus(), log.getFilterType());
        database.insert(SMSLogTable.TABLE_NAME, null, values);
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean removeAllLog() {
        database.delete(SMSLogTable.TABLE_NAME, "_id LIKE ?", new String[] { "%" });
        return true;
    }

    private SMSLog mapCursorToSMSLog(Cursor cursor) {
        // Have to convert the received time to milliseconds, since it it stored
        // in seconds.
        long received_time_ms = ((long) cursor.getInt(cursor.getColumnIndex(SMSLogTable.COLUMN_RECEIVED_TIME))) * 1000L;
        return new SMSLog(received_time_ms, cursor.getString(cursor.getColumnIndex(SMSLogTable.COLUMN_PHONE_NUMBER)), cursor.getString(cursor
                .getColumnIndex(SMSLogTable.COLUMN_STATUS)), cursor.getString(cursor.getColumnIndex(SMSLogTable.COLUMN_FILTER_TYPE)));
    }
}
