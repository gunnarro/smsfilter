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
import com.gunnarro.android.smsfilter.domain.MMSLog;
import com.gunnarro.android.smsfilter.domain.MsgLog;
import com.gunnarro.android.smsfilter.domain.MsgStatistic;
import com.gunnarro.android.smsfilter.domain.SMSLog;
import com.gunnarro.android.smsfilter.domain.Setting;
import com.gunnarro.android.smsfilter.repository.FilterDataBaseHjelper;
import com.gunnarro.android.smsfilter.repository.FilterRepository;
import com.gunnarro.android.smsfilter.repository.table.FilterTable;
import com.gunnarro.android.smsfilter.repository.table.ItemTable;
import com.gunnarro.android.smsfilter.repository.table.MsgLogTable;
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
    public boolean isMsgFilterActivated() {
        Setting setting = null;
        String selection = SettingTable.COLUMN_KEY + " LIKE ?";
        String[] selectionArgs = { SettingTable.SMS_FILTER_ACTIVATED };
        String groupBy = null;
        String orderBy = null;
        this.database = dbHelper.getReadableDatabase();
        Cursor cursor = database.query(SettingTable.TABLE_NAME, SettingTable.TABLE_COLUMNS, selection, selectionArgs, groupBy, null, orderBy);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            setting = mapCursorToSetting(cursor);
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        if (setting == null) {
            throw new RuntimeException("DB not initialize! Please report bug!");
        }
        CustomLog.d(this.getClass(), "type=" + setting.getName() + " value=" + setting.getValue());
        boolean isActivated = setting != null ? Boolean.parseBoolean(setting.getValue()) : false;
        CustomLog.i(this.getClass(), "is msg Filter activated=" + isActivated);
        return isActivated;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isMsgFilterPeriodActivated() {
        Setting setting = null;
        String selection = SettingTable.COLUMN_KEY + " LIKE ?";
        String[] selectionArgs = { SettingTable.SMS_FILTER_PERIOD_ACTIVATED };
        String groupBy = null;
        String orderBy = null;
        this.database = dbHelper.getReadableDatabase();
        Cursor cursor = database.query(SettingTable.TABLE_NAME, SettingTable.TABLE_COLUMNS, selection, selectionArgs, groupBy, null, orderBy);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            setting = mapCursorToSetting(cursor);
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        if (setting == null) {
            throw new RuntimeException("Message Filter period setting not initialized! Please report bug!");
        }
        CustomLog.d(this.getClass(), "type=" + setting.getName() + " value=" + setting.getValue());
        boolean isActivated = setting != null ? Boolean.parseBoolean(setting.getValue()) : false;
        CustomLog.i(this.getClass(), "is msg Filter period activated=" + isActivated);
        return isActivated;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateMsgFilterActivated(boolean isActivated) {
        StringBuffer where = new StringBuffer();
        where.append(SettingTable.COLUMN_KEY).append(" LIKE ?");
        String[] selectionArgs = { SettingTable.SMS_FILTER_ACTIVATED };
        ContentValues values = SettingTable.createContentValues(SettingTable.SMS_FILTER_ACTIVATED, Boolean.toString(isActivated));
        this.database = dbHelper.getWritableDatabase();
        database.update(SettingTable.TABLE_NAME, values, where.toString(), selectionArgs);
        CustomLog.d(this.getClass(), "Updated Activated msg filter: " + isActivated);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateMsgFilterPeriodActivated(boolean isActivated) {
        StringBuffer where = new StringBuffer();
        where.append(SettingTable.COLUMN_KEY).append(" LIKE ?");
        String[] selectionArgs = { SettingTable.SMS_FILTER_PERIOD_ACTIVATED };
        ContentValues values = SettingTable.createContentValues(SettingTable.SMS_FILTER_PERIOD_ACTIVATED, Boolean.toString(isActivated));
        this.database = dbHelper.getWritableDatabase();
        database.update(SettingTable.TABLE_NAME, values, where.toString(), selectionArgs);
        CustomLog.d(this.getClass(), "Updated Activated msg filter period: " + isActivated);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateMsgFilterPeriodFromTime(long fromTime) {
        StringBuffer where = new StringBuffer();
        where.append(SettingTable.COLUMN_KEY).append(" LIKE ?");
        String[] selectionArgs = { SettingTable.SMS_FILTER_PERIOD_FROM_TIME };
        ContentValues values = SettingTable.createContentValues(SettingTable.SMS_FILTER_PERIOD_FROM_TIME, Long.toString(fromTime));
        this.database = dbHelper.getWritableDatabase();
        database.update(SettingTable.TABLE_NAME, values, where.toString(), selectionArgs);
        CustomLog.d(this.getClass(), "Updated Activated msg filter period from time: " + fromTime);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateMsgFilterPeriodToTime(long toTime) {
        StringBuffer where = new StringBuffer();
        where.append(SettingTable.COLUMN_KEY).append(" LIKE ?");
        String[] selectionArgs = { SettingTable.SMS_FILTER_PERIOD_TO_TIME };
        ContentValues values = SettingTable.createContentValues(SettingTable.SMS_FILTER_PERIOD_TO_TIME, Long.toString(toTime));
        this.database = dbHelper.getWritableDatabase();
        database.update(SettingTable.TABLE_NAME, values, where.toString(), selectionArgs);
        CustomLog.d(this.getClass(), "Updated Activated msg filter period to time: " + toTime);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getMsgFilterPeriodFromTime() {
        Setting setting = null;
        String selection = SettingTable.COLUMN_KEY + " LIKE ?";
        String[] selectionArgs = { SettingTable.SMS_FILTER_PERIOD_FROM_TIME };
        String groupBy = null;
        String orderBy = null;
        this.database = dbHelper.getReadableDatabase();
        Cursor cursor = database.query(SettingTable.TABLE_NAME, SettingTable.TABLE_COLUMNS, selection, selectionArgs, groupBy, null, orderBy);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            setting = mapCursorToSetting(cursor);
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        if (setting == null) {
            throw new RuntimeException("Message Filter from time setting not initialized! Please report bug!");
        }
        CustomLog.d(this.getClass(), "type=" + setting.getName() + " value=" + setting.getValue());
        int fromTime = setting != null ? Integer.parseInt(setting.getValue()) : 0;
        CustomLog.i(this.getClass(), "msg Filter from time=" + fromTime);
        return fromTime;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getMsgFilterPeriodToTime() {
        Setting setting = null;
        String selection = SettingTable.COLUMN_KEY + " LIKE ?";
        String[] selectionArgs = { SettingTable.SMS_FILTER_PERIOD_TO_TIME };
        String groupBy = null;
        String orderBy = null;
        this.database = dbHelper.getReadableDatabase();
        Cursor cursor = database.query(SettingTable.TABLE_NAME, SettingTable.TABLE_COLUMNS, selection, selectionArgs, groupBy, null, orderBy);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            setting = mapCursorToSetting(cursor);
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        if (setting == null) {
            throw new RuntimeException("Message Filter to time setting not initialized! Please report bug!");
        }
        CustomLog.d(this.getClass(), "type=" + setting.getName() + " value=" + setting.getValue());
        int fromTime = setting != null ? Integer.parseInt(setting.getValue()) : 0;
        CustomLog.i(this.getClass(), "msg Filter to time=" + fromTime);
        return fromTime;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isLogMsg() {
        Setting setting = null;
        String selection = SettingTable.COLUMN_KEY + " LIKE ?";
        String[] selectionArgs = { SettingTable.LOG_MSG };
        this.database = dbHelper.getReadableDatabase();
        Cursor cursor = database.query(SettingTable.TABLE_NAME, SettingTable.TABLE_COLUMNS, selection, selectionArgs, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            setting = mapCursorToSetting(cursor);
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
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
        this.database = dbHelper.getReadableDatabase();
        Cursor cursor = database.query(FilterTable.TABLE_NAME, FilterTable.TABLE_COLUMNS, selection, selectionArgs, groupBy, null, orderBy);
        // CustomLog.d(this.getClass(), "active filter hits=" +
        // cursor.getCount());
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            // CustomLog.d(this.getClass(), "active filter hits=" +
            // cursor.getString(0));
            filter = mapCursorToFilter(cursor);
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
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
        this.database = dbHelper.getReadableDatabase();
        Cursor cursor = database.query(FilterTable.TABLE_NAME, FilterTable.TABLE_COLUMNS, selection, selectionArgs, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            filter = mapCursorToFilter(cursor);
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
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
        this.database = dbHelper.getReadableDatabase();
        Cursor cursor = database.query(FilterTable.TABLE_NAME, FilterTable.TABLE_COLUMNS, selection, selectionArgs, null, null, orderBy);
        if (cursor != null) {
            cursor.moveToFirst();
            filter = mapCursorToFilter(cursor);
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
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
        this.database = dbHelper.getReadableDatabase();
        Cursor cursor = database.query(FilterTable.TABLE_NAME, FilterTable.TABLE_COLUMNS, null, null, null, null, orderBy);
        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                list.add(mapCursorToFilter(cursor));
                cursor.moveToNext();
            }
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
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
        this.database = dbHelper.getWritableDatabase();
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
        this.database = dbHelper.getWritableDatabase();
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
        this.database = dbHelper.getWritableDatabase();
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
        this.database = dbHelper.getWritableDatabase();
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
        this.database = dbHelper.getReadableDatabase();
        Cursor cursor = database.rawQuery(sqlQuery.toString(), selectionArgs);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                list.add(mapCursorToItem(cursor));
                cursor.moveToNext();
            }
        }
        CustomLog.d(this.getClass(), "for filter=" + filterName + ", hits=" + cursor.getCount());
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
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
        this.database = dbHelper.getWritableDatabase();
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
        this.database = dbHelper.getWritableDatabase();
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
        this.database = dbHelper.getWritableDatabase();
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
    public List<MsgLog> getLogListOrderByDate(String msgType) {
        List<MsgLog> list = new ArrayList<MsgLog>();
        this.database = dbHelper.getReadableDatabase();
        Cursor cursor = database.query(MsgLogTable.TABLE_NAME, MsgLogTable.TABLE_COLUMNS, null, null, null, null, MsgLogTable.COLUMN_RECEIVED_TIME + " ASC");
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            list.add(mapCursorToMsgLog(cursor));
            cursor.moveToLast();
            list.add(mapCursorToMsgLog(cursor));
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return list;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<MsgLog> getLogList(String groupBy, String msgType) {
        String selectClause = MsgLogTable.COLUMN_RECEIVED_TIME;
        if (groupBy.equalsIgnoreCase("year")) {
            selectClause = "strftime('%Y', datetime(received_time, 'unixepoch'))";
        } else if (groupBy.equalsIgnoreCase("month")) {
            selectClause = "strftime('%m.%Y', datetime(received_time, 'unixepoch'))";
        } else if (groupBy.equalsIgnoreCase("week")) {
            selectClause = "strftime('%W', datetime(received_time, 'unixepoch'))";
        } else if (groupBy.equalsIgnoreCase("day")) {
            selectClause = "strftime('%d.%m', datetime(received_time, 'unixepoch'))";
        } else if (groupBy.equalsIgnoreCase("number")) {
            selectClause = MsgLogTable.COLUMN_PHONE_NUMBER;
        } else if (groupBy.equalsIgnoreCase("filter")) {
            selectClause = MsgLogTable.COLUMN_FILTER_TYPE;
        }
        List<MsgLog> list = new ArrayList<MsgLog>();
        StringBuffer query = new StringBuffer();
        query.append("SELECT ").append(selectClause).append(" AS value");
        query.append(", count(").append(MsgLogTable.COLUMN_RECEIVED_TIME).append(") AS total_count");
        query.append(", count(msg_type = 'SMS') as sms_count");
        query.append(", count(msg_type = 'MMS') as mms_count");
        query.append(", msg_type");
        query.append(" FROM ").append(MsgLogTable.TABLE_NAME);
        // query.append(" WHERE ").append(MsgLogTable.COLUMN_MSG_TYPE).append(" LIKE '").append(msgType).append("'");
        query.append(" GROUP BY ").append(selectClause).append(", msg_type");
        query.append(" ORDER BY ").append(selectClause);

        /**
         * 
         * select phone_number, count(msg_type) as msg_count from msg_log where
         * ( select phone_number, msg_type, count(msg_type) sms_count, from
         * msg_log
         * 
         * group by id,userID ) GROUP BY phone_number
         **/
        CustomLog.i(this.getClass(), query.toString());
        this.database = dbHelper.getReadableDatabase();
        Cursor cursor = database.rawQuery(query.toString(), null);
        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                String type = cursor.getString(cursor.getColumnIndex("msg_type"));
                CustomLog.i(
                        FilterRepositoryImpl.class,
                        new MsgStatistic(type, cursor.getString(cursor.getColumnIndex("value")), cursor.getInt(cursor.getColumnIndex("sms_count")), cursor
                                .getInt(cursor.getColumnIndex("mms_count")), cursor.getInt(cursor.getColumnIndex("total_count"))).toString());
                if (type.equals("SMS")) {
                    list.add(new SMSLog(cursor.getString(cursor.getColumnIndex("value")), cursor.getInt(cursor.getColumnIndex("sms_count"))));
                } else if (type.equals("MMS")) {
                    list.add(new MMSLog(cursor.getString(cursor.getColumnIndex("value")), cursor.getInt(cursor.getColumnIndex("mms_count"))));
                } else {
                    CustomLog.e(FilterRepositoryImpl.class, "BUG: Unkown msgType: " + type);
                }
                cursor.moveToNext();
            }
        }
        // Make sure to close the cursor
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        this.dbHelper.close();
        return list;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean createLog(MsgLog log) {
        ContentValues values = MsgLogTable.createContentValues(log.getReceivedTime(), log.getPhoneNumber(), log.getStatus(), log.getFilterType(),
                log.getMsgType());
        this.database = dbHelper.getWritableDatabase();
        database.insert(MsgLogTable.TABLE_NAME, null, values);
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean removeAllLog(String msgType) {
        this.database = dbHelper.getWritableDatabase();
        database.delete(MsgLogTable.TABLE_NAME, "_id LIKE ? AND msg_type LIKE ?", new String[] { "%", msgType });
        return true;
    }

    private SMSLog mapCursorToMsgLog(Cursor cursor) {
        // Have to convert the received time to milliseconds, since it it stored
        // in seconds.
        long received_time_ms = ((long) cursor.getInt(cursor.getColumnIndex(MsgLogTable.COLUMN_RECEIVED_TIME))) * 1000L;
        return new SMSLog(received_time_ms, cursor.getString(cursor.getColumnIndex(MsgLogTable.COLUMN_PHONE_NUMBER)), cursor.getString(cursor
                .getColumnIndex(MsgLogTable.COLUMN_STATUS)), cursor.getString(cursor.getColumnIndex(MsgLogTable.COLUMN_FILTER_TYPE)));
    }

}
