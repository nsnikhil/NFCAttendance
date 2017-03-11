package com.drivool.nrs.nfcattendance.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;


public class TableProvider extends ContentProvider{

    private static final int uAllEntities = 5460;
    private static final int uSingleNfcEntity = 5462;
    private static final int uSingleEntry = 5463;
    private static final int uAllEntries = 5464;

    static UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(TableNames.mAuthority,TableNames.mTableName,uAllEntities);
        sUriMatcher.addURI(TableNames.mAuthority,TableNames.mTableName+"/#",uSingleNfcEntity);
        sUriMatcher.addURI(TableNames.mAuthority,TableNames.mTableScheduleName,uSingleEntry);
        sUriMatcher.addURI(TableNames.mAuthority,TableNames.mTableScheduleName+"/#",uAllEntries);
    }

    TableHelper helper;

    @Override
    public boolean onCreate() {
        helper = new TableHelper(getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase sdb = helper.getReadableDatabase();
        Cursor c = null;
        switch (sUriMatcher.match(uri)){
            case uAllEntities:
                c = sdb.query(TableNames.mTableName,projection,selection,selectionArgs,null,null,sortOrder);
                break;
            case uSingleNfcEntity:
                selection = TableNames.table1.mNfcId +"=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                c = sdb.query(TableNames.mTableName,projection,selection,selectionArgs,null,null,sortOrder);
                break;
            case uAllEntries:
                c = sdb.query(TableNames.mTableScheduleName,projection,selection,selectionArgs,null,null,sortOrder);
                break;
            case uSingleEntry:
                selection = TableNames.table2.mNfcId +"=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                c = sdb.query(TableNames.mTableScheduleName,projection,selection,selectionArgs,null,null,sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Invalid Uri : "+uri);
        }
        c.setNotificationUri(getContext().getContentResolver(),uri);
        return c;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        switch (sUriMatcher.match(uri)){
            case uAllEntities:
                return insertEntity(uri,values,TableNames.mTableName);
            case uAllEntries:
                return insertEntity(uri,values,TableNames.mTableScheduleName);
            default:
                throw new IllegalArgumentException("Invalid Uri : "+uri);
        }
    }

    private Uri insertEntity(Uri u,ContentValues cv,String tableName){
        SQLiteDatabase sdb = helper.getWritableDatabase();
        long count = sdb.insert(tableName,null,cv);
        if(count==0){
            return null;
        }else {
            getContext().getContentResolver().notifyChange(u,null);
            return Uri.withAppendedPath(u,String.valueOf(count));
        }
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        switch (sUriMatcher.match(uri)){
            case uAllEntities:
                return deleteVal(uri,selection,selectionArgs,TableNames.mTableName);
            case uSingleNfcEntity:
                selection = TableNames.table1.mNfcId +"=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return deleteVal(uri,selection,selectionArgs,TableNames.mTableName);
            case uAllEntries:
                return deleteVal(uri,selection,selectionArgs,TableNames.mTableScheduleName);
            case uSingleEntry:
                selection = TableNames.table2.mNfcId +"=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return deleteVal(uri,selection,selectionArgs,TableNames.mTableScheduleName);
            default:
                throw new IllegalArgumentException("Invalid Uri : "+uri);
        }
    }

    private int deleteVal(Uri u,String sel,String selArgs[],String tableName){
        SQLiteDatabase sdb = helper.getWritableDatabase();
        int count = sdb.delete(tableName,sel,selArgs);
        if(count==0){
            return 0;
        }else {
            getContext().getContentResolver().notifyChange(u,null);
            return count;
        }
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
