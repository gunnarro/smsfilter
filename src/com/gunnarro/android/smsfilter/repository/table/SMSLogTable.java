package com.gunnarro.android.smsfilter.repository.table;

import java.util.Arrays;
import java.util.HashSet;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.gunnarro.android.smsfilter.custom.CustomLog;

public class SMSLogTable {

    // Database table
    public static final String TABLE_NAME = "sms_log";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_RECEIVED_DATE = "received_date";
    public static final String COLUMN_PHONE_NUMBER = "phone_number";
    public static final String COLUMN_STATUS = "status";

    public static String[] TABLE_COLUMNS = { COLUMN_ID, COLUMN_RECEIVED_DATE, COLUMN_PHONE_NUMBER, COLUMN_STATUS };

    // Database creation SQL statement
    private static final StringBuffer DATABASE_CREATE_QUERY;
    static {
        DATABASE_CREATE_QUERY = new StringBuffer();
        DATABASE_CREATE_QUERY.append("create table ");
        DATABASE_CREATE_QUERY.append(TABLE_NAME);
        DATABASE_CREATE_QUERY.append("(").append(COLUMN_ID).append(" INTEGER PRIMARY KEY AUTOINCREMENT");
        DATABASE_CREATE_QUERY.append(",").append(COLUMN_RECEIVED_DATE).append(" DATETIME");
        DATABASE_CREATE_QUERY.append(",").append(COLUMN_PHONE_NUMBER).append(" TEXT NOT NULL");
        DATABASE_CREATE_QUERY.append(",").append(COLUMN_STATUS).append(" TEXT NOT NULL);");
    }

    public static void onCreate(SQLiteDatabase database) {
        CustomLog.i(SMSLogTable.class, DATABASE_CREATE_QUERY.toString());
        database.execSQL(DATABASE_CREATE_QUERY.toString());
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        CustomLog.i(SMSLogTable.class, "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
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

    public static ContentValues createContentValues(String receivedDate, String phoneNumber, String status) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_RECEIVED_DATE, receivedDate);
        values.put(COLUMN_PHONE_NUMBER, phoneNumber);
        values.put(COLUMN_STATUS, status);
        return values;
    }

}
