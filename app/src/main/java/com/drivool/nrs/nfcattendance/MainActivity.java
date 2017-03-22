package com.drivool.nrs.nfcattendance;

import android.app.Fragment;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.drivool.nrs.nfcattendance.data.TableNames;
import com.drivool.nrs.nfcattendance.fragments.AlList;
import com.drivool.nrs.nfcattendance.fragments.HistoryList;
import com.drivool.nrs.nfcattendance.fragments.PresentList;
import com.drivool.nrs.nfcattendance.data.TableNames.table1;
import com.drivool.nrs.nfcattendance.data.TableNames.table2;
import com.drivool.nrs.nfcattendance.data.TableNames.tabletemp;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {


    Toolbar mainToolbar;
    DrawerLayout mDrawerLayout;
    LinearLayout fragmentContainer;
    RelativeLayout mainContainer;
    private NfcAdapter mNfcAdapter;
    NavigationView mNavigationView;
    android.support.v4.app.Fragment presentList;
    android.support.v4.app.Fragment historyList;
    android.support.v4.app.Fragment allList;
    private static final int mNfcRequestCode = 154;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addOnConnection();
        setContentView(R.layout.activity_main);
        initilize();
        checkNfc();
        initilizeDrawer();
        addFragments(savedInstanceState);
    }

    private void checkFirst() {
        if (getContentResolver().query(TableNames.mContentUri, null, null, null, null).getCount() <= 0) {
            startActivity(new Intent(MainActivity.this, DownloadActivity.class));
        }
    }

    private boolean checkConnection() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

    private void addOnConnection() {
        if (checkConnection()) {
            checkFirst();
        } else {
            removeOffConnection();
        }
    }

    private void removeOffConnection() {
        Snackbar.make(mainContainer, "No Internet", BaseTransientBottomBar.LENGTH_INDEFINITE).setAction("Retry", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addOnConnection();
            }
        }).show();
    }

    private void addFragments(Bundle savedInstanceState) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (savedInstanceState == null) {
            presentList = new PresentList();
            historyList = new HistoryList();
            allList = new AlList();
            ft.add(R.id.fragmentContainer, presentList);
            ft.add(R.id.fragmentContainer, historyList);
            ft.add(R.id.fragmentContainer, allList);
            ft.show(presentList);
            ft.hide(historyList);
            ft.hide(allList);
            ft.commit();
        }
    }

    private void checkNfc() {
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mNfcAdapter == null) {
            Snackbar.make(mainContainer, "Nfc Unavaialble", BaseTransientBottomBar.LENGTH_INDEFINITE).show();
        } else if (!mNfcAdapter.isEnabled()) {
            Snackbar.make(mainContainer, "Nfc Disabled", BaseTransientBottomBar.LENGTH_INDEFINITE).setAction("Turn On", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivityForResult(new Intent(Settings.ACTION_NFC_SETTINGS), mNfcRequestCode);
                }
            });
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
                    //test(tagInfo);
                   addStudent(tagInfo);
                } else {
                    MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.error);
                    mediaPlayer.start();
                    Toast.makeText(getApplicationContext(), "Already Scanned", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void test(String tagInfo) {
        Uri u = Uri.withAppendedPath(TableNames.mContentUri,tagInfo);
        Toast.makeText(getApplicationContext(), u.toString(), Toast.LENGTH_SHORT).show();
        String[] tst = new String[]{u.toString().substring(u.toString().lastIndexOf('/')+1)};
        Toast.makeText(getApplicationContext(), tst[0], Toast.LENGTH_SHORT).show();
        Toast.makeText(getApplicationContext(), getContentResolver().query(u,null,null,null,null).getCount()+"", Toast.LENGTH_SHORT).show();

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
        mainToolbar = (Toolbar) findViewById(R.id.mainToolbar);
        setSupportActionBar(mainToolbar);
        fragmentContainer = (LinearLayout) findViewById(R.id.fragmentContainer);
        mainContainer = (RelativeLayout) findViewById(R.id.mainContainer);
    }


    private void initilizeDrawer() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.mainDrawerLayout);
        mNavigationView = (NavigationView) findViewById(R.id.mainNaviagtionView);
        mNavigationView.getMenu().getItem(0).setChecked(true);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mainToolbar, R.string.drawerOpen, R.string.drawerClose) {
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
                        ft.show(presentList);
                        ft.hide(historyList);
                        ft.hide(allList);
                        drawerAction(0);
                        break;
                    case R.id.navigationALL:
                        ft.show(allList);
                        ft.hide(presentList);
                        ft.hide(historyList);
                        drawerAction(1);
                        break;
                    case R.id.navigationHistory:
                        ft.hide(allList);
                        ft.hide(presentList);
                        ft.show(historyList);
                        drawerAction(2);
                        break;
                    case R.id.navigationSettings:
                        mDrawerLayout.closeDrawers();
                        startActivity(new Intent(MainActivity.this, Prefrences.class));
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
        MenuItem menuAllList = mNavigationView.getMenu().getItem(1).setChecked(false);
        MenuItem menuHistory = mNavigationView.getMenu().getItem(2).setChecked(false);
        mDrawerLayout.closeDrawers();
        switch (key) {
            case 0:
                menuPresentList.setChecked(true);
                getSupportActionBar().setTitle(getResources().getString(R.string.app_name));
                break;
            case 1:
                menuAllList.setChecked(true);
                getSupportActionBar().setTitle(getResources().getString(R.string.titleAllEntities));
                break;
            case 2:
                menuHistory.setChecked(true);
                getSupportActionBar().setTitle(getResources().getString(R.string.titleHistory));
                break;

        }
    }
}
