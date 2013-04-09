package com.gunnarro.android.smsfilter.repository;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.gunnarro.android.smsfilter.custom.CustomLog;
import com.gunnarro.android.smsfilter.repository.table.FilterTable;
import com.gunnarro.android.smsfilter.repository.table.ItemTable;
import com.gunnarro.android.smsfilter.repository.table.SMSLogTable;
import com.gunnarro.android.smsfilter.repository.table.SettingTable;

public class FilterDataBaseHjelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "smsfiltertest.db";
    private static final int DATABASE_VERSION = 1;

    public FilterDataBaseHjelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Method is called during creation of the database
    @Override
    public void onCreate(SQLiteDatabase database) {
        FilterTable.onCreate(database);
        ItemTable.onCreate(database);
        SMSLogTable.onCreate(database);
        SettingTable.onCreate(database);
        // Init. available filter types, which are all deactivated as default.
        database.execSQL("insert into filters (_id, filter_name, activated) values(1,'sms_black_list','false')");
        database.execSQL("insert into filters (_id, filter_name, activated) values(2,'sms_white_list','false')");
        database.execSQL("insert into filters (_id, filter_name, activated) values(3,'contact_list','false')");
        CustomLog.d(this.getClass(), "created and initialized DB tables");
    }

    // Method is called during an upgrade of the database,
    // e.g. if you increase the database version
    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        FilterTable.onUpgrade(database, oldVersion, newVersion);
        ItemTable.onUpgrade(database, oldVersion, newVersion);
        SMSLogTable.onUpgrade(database, oldVersion, newVersion);
        SettingTable.onUpgrade(database, oldVersion, newVersion);
    }

}
