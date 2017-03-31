package com.drivool.nrs.nfcattendance;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.claudiodegio.msv.OnSearchViewListener;
import com.drivool.nrs.nfcattendance.Adapter.CursAdapter;
import com.drivool.nrs.nfcattendance.data.TableHelper;
import com.drivool.nrs.nfcattendance.data.TableNames;

public class SearchActivity extends AppCompatActivity {

    GridView searchList;
    CursAdapter cursAdapter;
    Toolbar searchToolbar;
    com.claudiodegio.msv.MaterialSearchView mSerachView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        initilize();
        if(getIntent().getExtras()!=null){
            String query = getIntent().getExtras().getString(getResources().getString(R.string.intentExtraSearchQuery));
            performSearch(query);
        }
    }

    private void initilize() {
        searchList = (GridView)findViewById(R.id.serachList);
        cursAdapter = new CursAdapter(getApplicationContext(),null);
        searchList.setAdapter(cursAdapter);
        searchToolbar = (Toolbar)findViewById(R.id.serachToolbar);
        setSupportActionBar(searchToolbar);
        getSupportActionBar().setTitle("Search");
        mSerachView = (com.claudiodegio.msv.MaterialSearchView)findViewById(R.id.searchSearchView);
        mSerachView.showSearch();
        mSerachView.setOnSearchViewListener(new OnSearchViewListener() {
            @Override
            public void onSearchViewShown() {

            }

            @Override
            public void onSearchViewClosed() {

            }

            @Override
            public boolean onQueryTextSubmit(String s) {
                performSearch(s);
                return false;
            }

            @Override
            public void onQueryTextChange(String s) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.admin_menu, menu);
        MenuItem item = menu.findItem(R.id.menuAdminSearch);
        mSerachView.setMenuItem(item);
        return true;
    }

    private void performSearch(String query){
        //String sqlQuery = "SELECT * FROM "+ TableNames.mTableName + " WHERE "+ TableNames.table1.mName +" LIKE '%"+query+"%' ";
        String sqlQuery = "SELECT * FROM "+ TableNames.mTableName + " WHERE "+ TableNames.table1.mName +" LIKE '"+query+"%' or "
                + TableNames.table1.mClass+ " LIKE '"+query+"%'";
        SQLiteDatabase sdb = new TableHelper(getApplicationContext()).getReadableDatabase();
        Cursor c = sdb.rawQuery(sqlQuery,null);
        if(c.moveToNext()){
            cursAdapter.swapCursor(c);
        }
    }

}
