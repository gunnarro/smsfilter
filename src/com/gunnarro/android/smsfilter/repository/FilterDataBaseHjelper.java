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

    private static FilterDataBaseHjelper instance = null;

    /**
     * Declare your database helper as a static instance variable and use the
     * Abstract Factory pattern to guarantee the singleton property. The static
     * factory getInstance method ensures that only one FilterDataBaseHjelper
     * will ever exist at any given time. If the mInstance object has not been
     * initialized, one will be created. If one has already been created then it
     * will simply be returned. You should not initialize your helper object
     * using with new FilterDataBaseHjelper(context)!. Instead, always use
     * FilterDataBaseHjelper.getInstance(context), as it guarantees that only
     * one database helper will exist across the entire application's lifecycle.
     * {@link <a href=
     * "http://www.androiddesignpatterns.com/2012/05/correctly-managing-your-
     * sqlite-database.html
     * ">http://www.androiddesignpatterns.com/2012/05/correctly-managing-your-sqlite-database.html</a>
     */
    public static FilterDataBaseHjelper getInstance(Context ctx) {
        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        if (instance == null) {
            instance = new FilterDataBaseHjelper(ctx.getApplicationContext());
        }
        return instance;
    }

    private FilterDataBaseHjelper(Context context) {
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
