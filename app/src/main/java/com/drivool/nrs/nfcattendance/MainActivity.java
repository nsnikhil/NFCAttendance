package com.drivool.nrs.nfcattendance;

import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.drivool.nrs.nfcattendance.data.TableNames;
import com.drivool.nrs.nfcattendance.fragments.HistoryListFragment;
import com.drivool.nrs.nfcattendance.fragments.PresentListFragment;
import com.drivool.nrs.nfcattendance.data.TableNames.table1;
import com.drivool.nrs.nfcattendance.data.TableNames.table2;
import com.drivool.nrs.nfcattendance.data.TableNames.tabletemp;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {


    Toolbar mToolbar;
    DrawerLayout mDrawerLayout;
    LinearLayout mFragmentContainer;
    RelativeLayout mContainer;
    private NfcAdapter mNfcAdapter;
    NavigationView mNavigationView;
    android.support.v4.app.Fragment mPresentList;
    android.support.v4.app.Fragment mHistoryList;
    private static final int mNfcRequestCode = 154;
    PendingIntent mPendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setTheme(R.style.tranparentStatusBar);
        }
        setContentView(R.layout.activity_main);
        initilize();
        checkNfc();
        initilizeDrawer();
        addOnConnection(savedInstanceState);
    }

    private void checkFirst() {
        if (getContentResolver().query(TableNames.mContentUri, null, null, null, null).getCount() <= 0) {
            if(mNfcAdapter!=null){
                startActivity(new Intent(MainActivity.this, DownloadActivity.class));
            }
        }
    }

    private boolean checkConnection() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

    private void addOnConnection(Bundle savedInstanceState) {
        if (checkConnection()) {
            checkFirst();
            addFragments(savedInstanceState);
        } else {
            removeOffConnection(savedInstanceState);
        }
    }

    private void removeOffConnection(final Bundle s) {
        Snackbar.make(mFragmentContainer, "No Internet", BaseTransientBottomBar.LENGTH_INDEFINITE).setAction("Retry", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addOnConnection(s);
            }
        }).setActionTextColor(getResources().getColor(R.color.white)).show();
    }

    private void addFragments(Bundle savedInstanceState) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (savedInstanceState == null) {
            mPresentList = new PresentListFragment();
            mHistoryList = new HistoryListFragment();
            ft.add(R.id.fragmentContainer, mPresentList);
            ft.add(R.id.fragmentContainer, mHistoryList);
            ft.show(mPresentList);
            ft.hide(mHistoryList);
            ft.commit();
        }
    }

    private void checkNfc() {
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mNfcAdapter == null) {
            finish();
            startActivity(new Intent(MainActivity.this,ErrorActivity.class));
        } else if (!mNfcAdapter.isEnabled()) {
            Snackbar.make(mFragmentContainer, "Nfc Disabled", BaseTransientBottomBar.LENGTH_INDEFINITE).setAction("Turn On", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivityForResult(new Intent(Settings.ACTION_NFC_SETTINGS), mNfcRequestCode);
                }
            }).setActionTextColor(getResources().getColor(R.color.white)).show();

        }
        mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mNfcAdapter!=null){
            mNfcAdapter.enableForegroundDispatch(this, mPendingIntent, null, null);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mNfcAdapter!=null){
            mNfcAdapter.disableForegroundDispatch(this);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        getId(intent);
    }

    private void getId(Intent intent) {
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            if (tag == null) {
                Toast.makeText(getApplicationContext(), "NULL TAG", Toast.LENGTH_SHORT).show();
            } else {
                String tagInfo = "";
                byte[] tagId = tag.getId();
                for (int i = 0; i < tagId.length; i++) {
                    tagInfo += Integer.toHexString(tagId[i] & 0xFF) + " ";
                }
                tagInfo = tagInfo.replace(" ", "");
                Cursor c = getContentResolver().query(TableNames.mTempContentUri, null, null, null, null);
                if (!checkExists(c, tagInfo)) {
                   addStudent(tagInfo);
                } else {
                    MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.error);
                    mediaPlayer.start();
                    Toast.makeText(getApplicationContext(), "Already Scanned", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }



    private void addStudent(String tagInfo) {
        Cursor c = getContentResolver().query(Uri.withAppendedPath(TableNames.mContentUri, tagInfo), null, null, null, null);
        if (c.moveToNext()) {
            Calendar cal = Calendar.getInstance();

            ContentValues cv = new ContentValues();
            ContentValues tcv = new ContentValues();

            cv.put(tabletemp.mNfcId, c.getString(c.getColumnIndex(table1.mNfcId)));
            cv.put(tabletemp.mRoLLNumber, c.getInt(c.getColumnIndex(table1.mRoLLNumber)));
            cv.put(tabletemp.mName, c.getString(c.getColumnIndex(table1.mName)));
            cv.put(tabletemp.mAddress, c.getString(c.getColumnIndex(table1.mAddress)));
            cv.put(tabletemp.mPhoneNo, c.getString(c.getColumnIndex(table1.mPhoneNo)));
            cv.put(tabletemp.mClass, c.getString(c.getColumnIndex(table1.mClass)));
            cv.put(tabletemp.mPhoto, c.getString(c.getColumnIndex(table1.mPhoto)));

            tcv.put(table2.mNfcId, c.getString(c.getColumnIndex(table1.mNfcId)));
            tcv.put(table2.mGetOnTime, String.valueOf(cal.getTimeInMillis()));
            tcv.put(table2.mGetOffTime, "");

            getContentResolver().insert(TableNames.mTempContentUri, cv);
            getContentResolver().insert(TableNames.mScheduleContentUri, tcv);

        } else {
            MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.error);
            mediaPlayer.start();
            Toast.makeText(getApplicationContext(), "Entry not found in database", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean checkExists(Cursor c, String tagId) {
        if (c.getCount() == 0) {
            return false;
        } else {
            while (c.moveToNext()) {
                if (c.getString(c.getColumnIndex(TableNames.tabletemp.mNfcId)).equalsIgnoreCase(tagId)) {
                    return true;
                }
            }
        }
        return false;
    }


    private void initilize() {
        mToolbar = (Toolbar) findViewById(R.id.mainToolbar);
        setSupportActionBar(mToolbar);
        mFragmentContainer = (LinearLayout) findViewById(R.id.fragmentContainer);
        mContainer = (RelativeLayout) findViewById(R.id.mainContainer);
    }


    private void initilizeDrawer() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.mainDrawerLayout);
        mNavigationView = (NavigationView) findViewById(R.id.mainNaviagtionView);
        mNavigationView.getMenu().getItem(0).setChecked(true);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.drawerOpen, R.string.drawerClose) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };
        mDrawerLayout.setDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                switch (item.getItemId()) {
                    case R.id.navigationPresent:
                        ft.show(mPresentList);
                        ft.hide(mHistoryList);
                        drawerAction(0);
                        break;
                    case R.id.navigationAdmin:
                        mDrawerLayout.closeDrawers();
                        startActivity(new Intent(MainActivity.this, AdminActivity.class));
                        break;
                    case R.id.navigationHistory:
                        ft.hide(mPresentList);
                        ft.show(mHistoryList);
                        drawerAction(1);
                        break;
                    case R.id.navigationSettings:
                        mDrawerLayout.closeDrawers();
                        startActivity(new Intent(MainActivity.this, PrefrencesActivity.class));
                        break;
                }
                ft.commit();
                return false;
            }
        });
    }


    private void drawerAction(int key) {
        invalidateOptionsMenu();
        MenuItem menuPresentList = mNavigationView.getMenu().getItem(0).setChecked(false);
        MenuItem menuHistory = mNavigationView.getMenu().getItem(2).setChecked(false);
        mDrawerLayout.closeDrawers();
        switch (key) {
            case 0:
                menuPresentList.setChecked(true);
                getSupportActionBar().setTitle(getResources().getString(R.string.app_name));
                break;
            case 1:
                menuHistory.setChecked(true);
                getSupportActionBar().setTitle(getResources().getString(R.string.titleHistory));
                break;
        }
    }
}
