package com.gunnarro.android.smsfilter.repository;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.gunnarro.android.smsfilter.custom.CustomLog;
import com.gunnarro.android.smsfilter.repository.table.FilterTable;
import com.gunnarro.android.smsfilter.repository.table.ItemTable;
import com.gunnarro.android.smsfilter.repository.table.MsgLogTable;
import com.gunnarro.android.smsfilter.repository.table.SettingTable;
import com.gunnarro.android.smsfilter.service.impl.FilterServiceImpl.FilterTypeEnum;

public class FilterDataBaseHjelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "smsfilter.db";
    private static final int DATABASE_VERSION = 50;

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
        MsgLogTable.onCreate(database);
        SettingTable.onCreate(database);
        insertDefaultData(database);
        CustomLog.i(this.getClass(), "created and initialized DB tables");
    }

    // Method is called during an upgrade of the database,
    // e.g. if you increase the database version
    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        FilterTable.onUpgrade(database, oldVersion, newVersion);
        ItemTable.onUpgrade(database, oldVersion, newVersion);
        MsgLogTable.onUpgrade(database, oldVersion, newVersion);
        SettingTable.onUpgrade(database, oldVersion, newVersion);
        insertDefaultData(database);
        // for testing only
        insertTestData(database);
        CustomLog.i(this.getClass(), "upgraded DB tables");

    }

    private void insertDefaultData(SQLiteDatabase database) {
        // init settings
        database.execSQL("insert into settings (_id, key, value) values(1,'" + SettingTable.SMS_FILTER_ACTIVATED + "','false')");
        database.execSQL("insert into settings (_id, key, value) values(2,'" + SettingTable.SMS_FILTER_PERIOD_ACTIVATED + "','false')");
        database.execSQL("insert into settings (_id, key, value) values(3,'" + SettingTable.SMS_FILTER_PERIOD_FROM_TIME + "','0')");
        database.execSQL("insert into settings (_id, key, value) values(4,'" + SettingTable.SMS_FILTER_PERIOD_TO_TIME + "','0')");
        database.execSQL("insert into settings (_id, key, value) values(5,'" + SettingTable.LOG_MSG + "','false')");

        // Init. available filter types, which are all deactivated as default.
        database.execSQL("insert into filters (_id, filter_name, activated) values(1,'" + FilterTypeEnum.SMS_BLACK_LIST.name() + "','true')");
        database.execSQL("insert into filters (_id, filter_name, activated) values(2,'" + FilterTypeEnum.SMS_WHITE_LIST.name() + "','false')");
        database.execSQL("insert into filters (_id, filter_name, activated) values(3,'" + FilterTypeEnum.CONTACTS.name() + "','false')");
        CustomLog.i(this.getClass(), "inserted default test data");
    }

    /**
     * For testing only
     * 
     * @param database
     */
    private void insertTestData(SQLiteDatabase database) {
        database.execSQL("insert into msg_logs (_id, received_time, phone_number, status, filter_type, msg_type) values(1,"
                + (System.currentTimeMillis() / 1000) + ", '45465504', 'blocked', '" + FilterTypeEnum.SMS_WHITE_LIST.name() + "', 'MMS')");
        database.execSQL("insert into msg_logs (_id, received_time, phone_number, status, filter_type, msg_type) values(2,"
                + (System.currentTimeMillis() / 1000) + ", '45465501', 'blocked', '" + FilterTypeEnum.SMS_WHITE_LIST.name() + "', 'MMS')");
        database.execSQL("insert into msg_logs (_id, received_time, phone_number, status, filter_type, msg_type) values(3,"
                + (System.currentTimeMillis() / 1000) + ", '45465504', 'blocked', '" + FilterTypeEnum.CONTACTS.name() + "', 'SMS')");
        database.execSQL("insert into msg_logs (_id, received_time, phone_number, status, filter_type, msg_type) values(4,"
                + (System.currentTimeMillis() / 1000) + ", '45465504', 'blocked', '" + FilterTypeEnum.SMS_BLACK_LIST.name() + "', 'SMS')");
        database.execSQL("insert into msg_logs (_id, received_time, phone_number, status, filter_type, msg_type) values(5," + System.currentTimeMillis() / 1000
                + ", '45465501', 'blocked', '" + FilterTypeEnum.SMS_BLACK_LIST.name() + "', 'SMS')");
        database.execSQL("insert into msg_logs (_id, received_time, phone_number, status, filter_type, msg_type) values(6," + System.currentTimeMillis() / 1000
                + ", '45465503', 'blocked', '" + FilterTypeEnum.SMS_BLACK_LIST.name() + "', 'SMS')");

    }
}
