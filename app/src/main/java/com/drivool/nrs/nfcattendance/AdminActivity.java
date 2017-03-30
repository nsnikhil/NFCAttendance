package com.drivool.nrs.nfcattendance;


import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.claudiodegio.msv.OnSearchViewListener;
import com.drivool.nrs.nfcattendance.Adapter.CursAdapter;
import com.drivool.nrs.nfcattendance.data.TableNames;


public class AdminActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>,View.OnClickListener{

    GridView mAdminAllList;
    private static final int mPresentLoaderId = 2;
    Toolbar mAdminToolbar;
    CursAdapter mCusrAdaptr;
    FloatingActionButton mAddEntity;
    com.claudiodegio.msv.MaterialSearchView mSearchView;

    public AdminActivity() {

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        initilize();
        mCusrAdaptr = new CursAdapter(getApplicationContext(), null);
        mAdminAllList.setAdapter(mCusrAdaptr);
        loadfList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.admin_menu, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        mSearchView.setMenuItem(item);
        return true;
    }



    private void initilize() {
        mAdminAllList = (GridView) findViewById(R.id.adminAllList);
        mAdminToolbar = (Toolbar)findViewById(R.id.adminToolbar);
        mAddEntity = (FloatingActionButton)findViewById(R.id.adminAddEntity);
        mAddEntity.setOnClickListener(this);
        mSearchView = (com.claudiodegio.msv.MaterialSearchView)findViewById(R.id.sv);
        setSupportActionBar(mAdminToolbar);
        mSearchView.setOnSearchViewListener(new OnSearchViewListener() {
            @Override
            public void onSearchViewShown() {

            }

            @Override
            public void onSearchViewClosed() {

            }

            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public void onQueryTextChange(String s) {

            }
        });
        mAdminAllList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder modifyUser = new AlertDialog.Builder(AdminActivity.this);
                modifyUser.setMessage("Do you want to edit or delete this student")
                        .setNegativeButton("Edit", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getApplicationContext(),"Edit",Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getApplicationContext(),"Delete",Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getApplicationContext(),"Cancel",Toast.LENGTH_SHORT).show();
                            }
                        }).create().show();
            }
        });
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case mPresentLoaderId:
                return new CursorLoader(getApplicationContext(), TableNames.mContentUri, null, null, null, null);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCusrAdaptr.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCusrAdaptr.swapCursor(null);
    }

    private void loadfList() {
        if (getSupportLoaderManager().getLoader(mPresentLoaderId) == null) {
            getSupportLoaderManager().initLoader(mPresentLoaderId, null, this).forceLoad();
        } else {
            getSupportLoaderManager().restartLoader(mPresentLoaderId, null, this).forceLoad();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.adminAddEntity:
                startActivity(new Intent(AdminActivity.this,NewUserActivity.class));
                break;
        }
    }
}
