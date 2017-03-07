package com.drivool.nrs.nfcattendance.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Nikhil on 07-Mar-17.
 */

public class TableNames {

    public static final String mDatabaseName = "dailyRecord";
    public static final int mDatabaseVersion = 1;
    public static final String mTableName = "attendance";

    public static final String mScheme = "content://";
    public static final String mAuthority = "com.drivool.nrs.nfcattendance";

    public static final Uri mBaseUri = Uri.parse(mScheme+mAuthority);
    public static final Uri mContentUri = Uri.withAppendedPath(mBaseUri,mTableName);

    public class table1 implements BaseColumns{
        public static final String mId = BaseColumns._ID;
        public static final String mNfcId = "nfcId";
        public static final String mBoadringTime = "startTime";
        public static final String mExitTime = "exitTime";
        public static final String mDate = "attendanceDate";
    }
}
