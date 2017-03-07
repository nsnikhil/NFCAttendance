package com.drivool.nrs.nfcattendance.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.drivool.nrs.nfcattendance.data.TableNames.table1;

/**
 * Created by Nikhil on 07-Mar-17.
 */

public class TableHelper extends SQLiteOpenHelper{

    private static final String mCreateTable = "CREATE TABLE " + TableNames.mTableName + " ("
            +table1.mId + " INTEGER(6) PRIMARY KEY NOT NULL AUTO_INCREMENT, "
            +table1.mNfcId + " INTEGER(6) NOT NULL, "
            +table1.mBoadringTime + " DATE NOT NULL, "
            +table1.mExitTime + " DATE NOT NULL, "
            +table1.mDate + " DATE NOT NULL "
            + ");";

    private static final String mDropTable = "DROP TABLE IF EXISTS "+TableNames.mTableName;


    public TableHelper(Context context) {
        super(context, TableNames.mDatabaseName, null, TableNames.mDatabaseVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createTable(db);
    }

    private void createTable(SQLiteDatabase sdb){
        sdb.execSQL(mCreateTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(mDropTable);
        createTable(db);
    }
}
