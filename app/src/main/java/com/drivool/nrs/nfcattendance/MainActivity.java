package com.drivool.nrs.nfcattendance;

import android.app.Fragment;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.drivool.nrs.nfcattendance.data.TableNames;
import com.drivool.nrs.nfcattendance.fragments.HistoryList;
import com.drivool.nrs.nfcattendance.fragments.PresentList;
import com.drivool.nrs.nfcattendance.data.TableNames.table1;
import com.drivool.nrs.nfcattendance.data.TableNames.table2;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
    private static final int mNfcRequestCode = 154;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initilize();
        checkNfc();
        initilizeDrawer();
        addFragments(savedInstanceState);

    }

    private void addFragments(Bundle savedInstanceState) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (savedInstanceState == null) {
            presentList = new PresentList();
            historyList = new HistoryList();
            ft.add(R.id.fragmentContainer, presentList);
            ft.add(R.id.fragmentContainer, historyList);
            ft.show(presentList);
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
                Cursor c = getContentResolver().query(TableNames.mContentUri, null, null, null, null);
                if(!checkExists(c,tagInfo)){
                    addStudent(tagInfo);
                }else {
                    MediaPlayer mediaPlayer=MediaPlayer.create(this,R.raw.error);
                    mediaPlayer.start();
                    Toast.makeText(getApplicationContext(),"Already Scanned",Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private boolean checkExists(Cursor c,String tagId){
        if(c.getCount()==0){
            return false;
        }else {
            while (c.moveToNext()){
                if(c.getString(c.getColumnIndex(table1.mNfcId)).equalsIgnoreCase(tagId.replace(" ",""))){
                    return true;
                }
            }
        }
        return false;
    }

    private String getUrl(String id) {
        id = id.replace(" ", "");
        String server = getResources().getString(R.string.urlServer);
        String singleQuery = getResources().getString(R.string.urlSingleEntity);
        String url = server + singleQuery;
        String nfcQuery = "nfc";
        String nfcQueryValue = id;
        return Uri.parse(url).buildUpon()
                .appendQueryParameter(nfcQuery, nfcQueryValue)
                .build()
                .toString();
    }

    private void addStudent(String id) {
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, getUrl(id), null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    getJson(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(jsonArrayRequest);
    }

    private void getJson(JSONArray response) throws JSONException {
        if (response.length() > 0) {

            Calendar c = Calendar.getInstance();
            long time = c.getTimeInMillis();

            ContentValues cv = new ContentValues();
            ContentValues timecv = new ContentValues();

            for (int i = 0; i < response.length(); i++) {
                JSONObject entity = response.getJSONObject(i);
                String nfcId = entity.getString("nfcid");
                int rollNo = entity.getInt("rollno");
                String name = entity.getString("studentname");
                String address = entity.getString("studentaddress");
                String phoneno = entity.getString("studentphoneno");
                String cls = entity.getString("studentclass");
                String photo = entity.getString("studentphoto");
                if (address.indexOf('\\') != -1) {
                    address.replaceAll("\\/", "/");
                }
                if (photo.indexOf('\\') != -1) {
                    photo.replaceAll("\\/", "/");
                }
                cv.put(table1.mNfcId, nfcId);
                cv.put(table1.mRoLLNumber, rollNo);
                cv.put(table1.mName, name);
                cv.put(table1.mAddress, address);
                cv.put(table1.mPhoneNo, phoneno);
                cv.put(table1.mClass, cls);
                cv.put(table1.mPhoto, photo);
                timecv.put(table2.mNfcId, nfcId);
                timecv.put(table2.mGetOnTime, String.valueOf(time));
                timecv.put(table2.mGetOffTime, "");
                Uri u = getContentResolver().insert(TableNames.mContentUri, cv);
                Uri tu = getContentResolver().insert(TableNames.mScheduleContentUri, timecv);
            }
        }else {
            MediaPlayer mediaPlayer=MediaPlayer.create(this,R.raw.error);
            mediaPlayer.start();
            Toast.makeText(getApplicationContext(),"Student Not Found in database",Toast.LENGTH_SHORT).show();
        }
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
                        drawerAction(0);
                        break;
                    case R.id.navigationHistory:
                        ft.show(historyList);
                        ft.hide(presentList);
                        drawerAction(1);
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
        MenuItem menuHistory = mNavigationView.getMenu().getItem(1).setChecked(false);
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
