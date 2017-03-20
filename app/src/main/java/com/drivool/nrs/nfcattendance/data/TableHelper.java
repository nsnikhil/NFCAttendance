package com.drivool.nrs.nfcattendance.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.drivool.nrs.nfcattendance.data.TableNames.table1;
import com.drivool.nrs.nfcattendance.data.TableNames.table2;

/**
 * Created by Nikhil on 07-Mar-17.
 */

public class TableHelper extends SQLiteOpenHelper{

    private static final String mCreateTable = "CREATE TABLE " + TableNames.mTableName + " ("
            +table1.mId + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            +table1.mNfcId + " TEXT NOT NULL, "
            +table1.mRoLLNumber + " INTEGER NOT NULL, "
            +table1.mName + " TEXT NOT NULL, "
            +table1.mPhoneNo + " TEXT NOT NULL, "
            +table1.mAddress + " TEXT NOT NULL, "
            +table1.mClass + " TEXT NOT NULL,"
            +table1.mPhoto + " TEXT NOT NULL"
            + ");";

    private static final String mCreateScheduleTable = "CREATE TABLE " + TableNames.mTableScheduleName + " ("
            +table2.mId + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            +table2.mNfcId + " TEXT NOT NULL, "
            +table2.mGetOnTime + " TEXT NOT NULL, "
            +table2.mGetOffTime + " TEXT NOT NULL "
            + ");";


    private static final String mDropTable = "DROP TABLE IF EXISTS "+TableNames.mTableName;

    private static final String mDropScheduleTable = "DROP TABLE IF EXISTS "+TableNames.mTableScheduleName;


    public TableHelper(Context context) {
        super(context, TableNames.mDatabaseName, null, TableNames.mDatabaseVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createTable(db);
    }

    private void createTable(SQLiteDatabase sdb){
        sdb.execSQL(mCreateTable);
        sdb.execSQL(mCreateScheduleTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(mDropTable);
        db.execSQL(mDropScheduleTable);
        createTable(db);
    }
}
