package com.gunnarro.android.smsfilter.repository.table;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.gunnarro.android.smsfilter.custom.CustomLog;

/**
 * Use MsgLogTable
 *
 */
@Deprecated
public class MsgLogTable {

    // Database table
    public static final String TABLE_NAME = "msg_log";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_RECEIVED_TIME = "received_time";
    public static final String COLUMN_PHONE_NUMBER = "phone_number";
    public static final String COLUMN_STATUS = "status";
    public static final String COLUMN_FILTER_TYPE = "filter_type";
    public static final String COLUMN_MSG_TYPE = "msg_type";

    public static String[] TABLE_COLUMNS = { COLUMN_ID, COLUMN_RECEIVED_TIME, COLUMN_PHONE_NUMBER, COLUMN_STATUS, COLUMN_FILTER_TYPE, COLUMN_MSG_TYPE };

    // Database creation SQL statement
    private static final StringBuffer DATABASE_CREATE_QUERY;
    static {
        DATABASE_CREATE_QUERY = new StringBuffer();
        DATABASE_CREATE_QUERY.append("create table ");
        DATABASE_CREATE_QUERY.append(TABLE_NAME);
        DATABASE_CREATE_QUERY.append("(").append(COLUMN_ID).append(" INTEGER PRIMARY KEY AUTOINCREMENT");
        DATABASE_CREATE_QUERY.append(",").append(COLUMN_RECEIVED_TIME).append(" INTEGER");
        DATABASE_CREATE_QUERY.append(",").append(COLUMN_PHONE_NUMBER).append(" TEXT NOT NULL");
        DATABASE_CREATE_QUERY.append(",").append(COLUMN_STATUS).append(" TEXT NOT NULL");
        DATABASE_CREATE_QUERY.append(",").append(COLUMN_FILTER_TYPE).append(" TEXT NOT NULL");
        DATABASE_CREATE_QUERY.append(",").append(COLUMN_MSG_TYPE).append(" TEXT NOT NULL);");
    }

    public static void onCreate(SQLiteDatabase database) {
        CustomLog.i(MsgLogTable.class, DATABASE_CREATE_QUERY.toString());
        database.execSQL(DATABASE_CREATE_QUERY.toString());
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        CustomLog.i(MsgLogTable.class, "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(database);
    }

    public static void checkColumnNames(String[] projection) {
        if (projection != null) {
            HashSet<String> requestedColumns = new HashSet<String>(Arrays.asList(projection));
            HashSet<String> availableColumns = new HashSet<String>(Arrays.asList(TABLE_COLUMNS));
            // Check if all columns which are requested are available
            if (!availableColumns.containsAll(requestedColumns)) {
                throw new IllegalArgumentException("Unknown columns in projection");
            }
        }
    }

    public static ContentValues createContentValues(long receivedTime, String phoneNumber, String status, String filterType, String msgType) {
        ContentValues values = new ContentValues();
        // Note! date are stores as seconds since 1.1.1970
        CustomLog.i(MsgLogTable.class, "date=" + new Date(receivedTime) + " receivedTimeMs=" + receivedTime + " receivedTime=" + (int) (receivedTime / 1000));
        values.put(COLUMN_RECEIVED_TIME, (int) (receivedTime / 1000));
        values.put(COLUMN_PHONE_NUMBER, phoneNumber);
        values.put(COLUMN_STATUS, status);
        values.put(COLUMN_FILTER_TYPE, filterType);
        values.put(COLUMN_MSG_TYPE, msgType);
        return values;
    }

}
