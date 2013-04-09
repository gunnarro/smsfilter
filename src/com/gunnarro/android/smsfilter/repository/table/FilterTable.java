package com.gunnarro.android.smsfilter.repository.table;

import java.util.Arrays;
import java.util.HashSet;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.gunnarro.android.smsfilter.custom.CustomLog;

public class FilterTable {

    // Database table
    public static final String TABLE_NAME = "filters";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_FILTER_NAME = "filter_name";
    public static final String COLUMN_ACTIVATED = "activated";

    public static String[] TABLE_COLUMNS = { COLUMN_ID, COLUMN_FILTER_NAME, COLUMN_ACTIVATED };

    // Database creation SQL statement
    private static final StringBuffer DATABASE_CREATE_QUERY;
    static {
        DATABASE_CREATE_QUERY = new StringBuffer();
        DATABASE_CREATE_QUERY.append("create table ");
        DATABASE_CREATE_QUERY.append(TABLE_NAME);
        DATABASE_CREATE_QUERY.append("(").append(COLUMN_ID).append(" INTEGER PRIMARY KEY AUTOINCREMENT");
        DATABASE_CREATE_QUERY.append(",").append(COLUMN_FILTER_NAME).append(" TEXT NOT NULL");
        DATABASE_CREATE_QUERY.append(",").append(COLUMN_ACTIVATED).append(" TEXT NOT NULL);");
    }

    public static void onCreate(SQLiteDatabase database) {
        CustomLog.i(FilterTable.class, DATABASE_CREATE_QUERY.toString());
        database.execSQL(DATABASE_CREATE_QUERY.toString());
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        CustomLog.i(FilterTable.class, "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
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

    public static ContentValues createContentValues(String filterName, String isActivated) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_FILTER_NAME, filterName);
        values.put(COLUMN_ACTIVATED, isActivated);
        return values;
    }

}
