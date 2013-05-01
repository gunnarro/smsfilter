package com.gunnarro.android.smsfilter.contentprovider;

import java.util.Arrays;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import com.gunnarro.android.smsfilter.custom.CustomLog;
import com.gunnarro.android.smsfilter.repository.FilterDataBaseHjelper;
import com.gunnarro.android.smsfilter.repository.table.FilterTable;

/**
 * Use a content provider if you want to expose the data to other android
 * applications.
 * 
 * @author gunnarro
 * 
 */
@Deprecated
public class FilterContentProvider extends ContentProvider {

    public static final String AUTHORITY = FilterContentProvider.class.getPackage().getName();
    public static final String BASE_PATH = FilterTable.TABLE_NAME;
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH);
    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + FilterTable.TABLE_NAME;
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/filter";

    // database
    private FilterDataBaseHjelper database;

    // Used for the UriMacher
    private static final int FILTERS = 10;
    private static final int FILTER_ID = 20;
    private static final UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        URI_MATCHER.addURI(AUTHORITY, BASE_PATH, FILTERS);
        URI_MATCHER.addURI(AUTHORITY, BASE_PATH + "/#", FILTER_ID);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onCreate() {
        database = FilterDataBaseHjelper.getInstance(getContext());
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return queryFilter(uri, projection, selection, selectionArgs, sortOrder);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getType(Uri uri) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return insertFilter(uri, values);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return deleteFilter(uri, selection, selectionArgs);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return updateFilters(uri, values, selection, selectionArgs);
    }

    // ***************************************************************************
    // Filter table crud operations
    // ***************************************************************************

    private Cursor queryFilter(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        // Uisng SQLiteQueryBuilder instead of query() method
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        // Check if the caller has requested a column which does not exists
        FilterTable.checkColumnNames(projection);
        // Set the table
        queryBuilder.setTables(FilterTable.TABLE_NAME);
        int uriType = URI_MATCHER.match(uri);
        switch (uriType) {
        case FILTERS:
            break;
        case FILTER_ID:
            // Adding the ID to the original query
            queryBuilder.appendWhere(FilterTable.COLUMN_ID + "=" + uri.getLastPathSegment());
            break;
        default:
            throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        SQLiteDatabase sqlDB = database.getWritableDatabase();
        Cursor cursor = queryBuilder.query(sqlDB, projection, selection, selectionArgs, null, null, sortOrder);
        CustomLog.d(this.getClass(), buildDebugMsg(uriType, selection, selectionArgs, cursor.getCount()));
        // Make sure that potential listeners are getting notified
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    private Uri insertFilter(Uri uri, ContentValues values) {
        int uriType = URI_MATCHER.match(uri);
        SQLiteDatabase sqlDB = database.getWritableDatabase();
        long id = 0;
        switch (uriType) {
        case FILTERS:
            id = sqlDB.insert(FilterTable.TABLE_NAME, null, values);
            break;
        default:
            throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        CustomLog.d(this.getClass(), buildDebugMsg(uriType, null, null, 0));
        getContext().getContentResolver().notifyChange(uri, null);
        return Uri.parse(BASE_PATH + "/" + id);
    }

    private int deleteFilter(Uri uri, String selection, String[] selectionArgs) {
        int uriType = URI_MATCHER.match(uri);
        SQLiteDatabase sqlDB = database.getWritableDatabase();
        int rowsDeleted = 0;
        switch (uriType) {
        case FILTERS:
            rowsDeleted = sqlDB.delete(FilterTable.TABLE_NAME, selection, selectionArgs);
            break;
        case FILTER_ID:
            String id = uri.getLastPathSegment();
            if (TextUtils.isEmpty(selection)) {
                rowsDeleted = sqlDB.delete(FilterTable.TABLE_NAME, FilterTable.COLUMN_ID + "=" + id, null);
            } else {
                rowsDeleted = sqlDB.delete(FilterTable.TABLE_NAME, FilterTable.COLUMN_ID + "=" + id + " and " + selection, selectionArgs);
            }
            break;
        default:
            throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        CustomLog.d(this.getClass(), buildDebugMsg(uriType, selection, selectionArgs, rowsDeleted));
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    private int updateFilters(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int uriType = URI_MATCHER.match(uri);
        SQLiteDatabase sqlDB = database.getWritableDatabase();
        int rowsUpdated = 0;
        switch (uriType) {
        case FILTERS:
            rowsUpdated = sqlDB.update(FilterTable.TABLE_NAME, values, selection, selectionArgs);
            break;
        case FILTER_ID:
            String id = uri.getLastPathSegment();
            if (TextUtils.isEmpty(selection)) {
                rowsUpdated = sqlDB.update(FilterTable.TABLE_NAME, values, FilterTable.COLUMN_ID + "=" + id, null);
            } else {
                rowsUpdated = sqlDB.update(FilterTable.TABLE_NAME, values, FilterTable.COLUMN_ID + "=" + id + " and " + selection, selectionArgs);
            }
            break;
        default:
            throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        CustomLog.d(this.getClass(), buildDebugMsg(uriType, selection, selectionArgs, rowsUpdated));
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }

    // ***************************************************************************
    // sms_log table crud operations
    // ***************************************************************************

    private String buildDebugMsg(int uriType, String selection, String[] selectionArgs, int result) {
        StringBuffer msg = new StringBuffer();
        msg.append("uriType=").append(uriType == FILTERS ? "FILTERS, " : "FILTER_ID, ");
        if (selection != null) {
            msg.append("selection=").append(selection).append(", ");
        }
        if (selectionArgs != null) {
            msg.append("selectionArgs=").append(Arrays.asList(selectionArgs)).append(", ");
        }
        msg.append("result=" + result);
        return msg.toString();
    }
}
