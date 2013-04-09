package com.gunnarro.android.smsfilter.repository.table;

import java.util.Arrays;
import java.util.HashSet;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.gunnarro.android.smsfilter.custom.CustomLog;

public class ItemTable {

    // Database table
    public static final String TABLE_NAME = "items";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_FK_FILTER_ID = "fk_filter_id";
    public static final String COLUMN_VALUE = "value";
    public static final String COLUMN_SELECTED = "selected";

    public static String[] TABLE_COLUMNS = { COLUMN_ID, COLUMN_FK_FILTER_ID, COLUMN_VALUE, COLUMN_SELECTED };

    // Database creation SQL statement
    private static final StringBuffer DATABASE_CREATE_QUERY;
    static {
        DATABASE_CREATE_QUERY = new StringBuffer();
        DATABASE_CREATE_QUERY.append("create table ");
        DATABASE_CREATE_QUERY.append(TABLE_NAME);
        DATABASE_CREATE_QUERY.append("(").append(COLUMN_ID).append(" INTEGER PRIMARY KEY AUTOINCREMENT");
        DATABASE_CREATE_QUERY.append(",").append(COLUMN_FK_FILTER_ID).append(" INTEGER NOT NULL");
        DATABASE_CREATE_QUERY.append(",").append(COLUMN_VALUE).append(" TEXT NOT NULL");
        DATABASE_CREATE_QUERY.append(",").append(COLUMN_SELECTED).append(" TEXT NOT NULL");
        DATABASE_CREATE_QUERY.append(", FOREIGN KEY(").append(COLUMN_FK_FILTER_ID).append(") REFERENCES ").append(FilterTable.TABLE_NAME).append("(")
                .append(FilterTable.COLUMN_ID).append("));");
    }

    public static void onCreate(SQLiteDatabase database) {
        CustomLog.i(ItemTable.class, DATABASE_CREATE_QUERY.toString());
        database.execSQL(DATABASE_CREATE_QUERY.toString());
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        CustomLog.i(ItemTable.class, "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
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

    public static ContentValues createContentValues(Integer fkFilterId, String value, String isSelected) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_FK_FILTER_ID, fkFilterId);
        values.put(COLUMN_VALUE, value);
        values.put(COLUMN_SELECTED, isSelected);
        return values;
    }

}
