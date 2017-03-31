package com.drivool.nrs.nfcattendance.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Nikhil on 07-Mar-17.
 */

public class TableNames {

    public static final String mDatabaseName = "dailyRecord";
    public static final int mDatabaseVersion = 15;
    public static final String mTableName = "entity";
    public static final String mTableTempName = "temp";
    public static final String mTableScheduleName = "schedule";

    public static final String mScheme = "content://";
    public static final String mAuthority = "com.drivool.nrs.nfcattendance";

    public static final Uri mBaseUri = Uri.parse(mScheme+mAuthority);

    public static final Uri mContentUri = Uri.withAppendedPath(mBaseUri,mTableName);
    public static final Uri mScheduleContentUri = Uri.withAppendedPath(mBaseUri,mTableScheduleName);
    public static final Uri mTempContentUri = Uri.withAppendedPath(mBaseUri,mTableTempName);

    public class table1 implements BaseColumns{
        public static final String mId = BaseColumns._ID;
        public static final String mNfcId = "cardNfcId";
        public static final String mRoLLNumber = "rollNo";
        public static final String mName = "name";
        public static final String mAddress = "address";
        public static final String mPhoneNo = "phoneNo";
        public static final String mClass = "class";
        public static final String mPhoto = "photo";
    }

    public class tabletemp implements BaseColumns{
        public static final String mId = BaseColumns._ID;
        public static final String mNfcId = "cardNfcId";
        public static final String mRoLLNumber = "rollNo";
        public static final String mName = "name";
        public static final String mAddress = "address";
        public static final String mPhoneNo = "phoneNo";
        public static final String mClass = "class";
        public static final String mPhoto = "photo";
    }

    public class table2 implements BaseColumns{
        public static final String mId = BaseColumns._ID;
        public static final String mNfcId = "nfcId";
        public static final String mGetOnTime = "geton";
        public static final String mGetOffTime = "getoff";
    }
}
