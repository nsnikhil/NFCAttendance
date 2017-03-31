package com.drivool.nrs.nfcattendance;


import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.claudiodegio.msv.OnSearchViewListener;
import com.drivool.nrs.nfcattendance.Adapter.CursAdapter;
import com.drivool.nrs.nfcattendance.Netwrok.ImageDownload;
import com.drivool.nrs.nfcattendance.data.TableNames;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.concurrent.ExecutionException;


public class AdminActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>,View.OnClickListener{

    GridView mAdminAllList;
    private static final int mPresentLoaderId = 2;
    Toolbar mAdminToolbar;
    CursAdapter mCusrAdaptr;
    FloatingActionButton mAddEntity;
    SwipeRefreshLayout mRefresh;
    com.claudiodegio.msv.MaterialSearchView mSerachView;


    public AdminActivity() {

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        initilize();
        mRefresh.setRefreshing(true);
        mCusrAdaptr = new CursAdapter(getApplicationContext(), null);
        mAdminAllList.setAdapter(mCusrAdaptr);
        loadfList();
        checkDatabase();
    }

    private void initilize() {
        mAdminAllList = (GridView) findViewById(R.id.adminAllList);
        mAdminToolbar = (Toolbar)findViewById(R.id.adminToolbar);
        mAddEntity = (FloatingActionButton)findViewById(R.id.adminAddEntity);
        mAddEntity.setOnClickListener(this);
        mRefresh = (SwipeRefreshLayout)findViewById(R.id.adminAllRefresh);
        mRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                checkDatabase();
            }
        });
        setSupportActionBar(mAdminToolbar);
        getSupportActionBar().setTitle("Admin Panel");
        mSerachView = (com.claudiodegio.msv.MaterialSearchView)findViewById(R.id.adminSearchView);
        mSerachView.setOnSearchViewListener(new OnSearchViewListener() {
            @Override
            public void onSearchViewShown() {

            }

            @Override
            public void onSearchViewClosed() {

            }

            @Override
            public boolean onQueryTextSubmit(String s) {
                Intent search = new Intent(AdminActivity.this,SearchActivity.class);
                search.putExtra(getResources().getString(R.string.intentExtraSearchQuery),s);
                startActivity(search);
                return false;
            }

            @Override
            public void onQueryTextChange(String s) {

            }
        });
        mAdminAllList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, View view, final int position, long id) {
                buildDialog("","Do you want to edit or delete this student")
                        .setNegativeButton("Edit", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Cursor c = (Cursor) parent.getItemAtPosition(position);
                                Intent edit = new Intent(AdminActivity.this,NewUserActivity.class);
                                edit.putExtra(getResources().getString(R.string.intentExtraNfcId),c.getString(c.getColumnIndex(TableNames.table1.mNfcId)));
                                startActivity(edit);
                            }
                        })
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                buildDialog("Warning","Are you sure you want to delete this user")
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                }).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Cursor c = (Cursor) parent.getItemAtPosition(position);
                                        deleteEntityFromServer(c.getString(c.getColumnIndex(TableNames.table1.mNfcId)));
                                    }
                                }).create().show();
                            }
                        })
                        .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).create().show();
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

    private void deleteEntityFromServer(final String nfcId) {
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, buildDeleteEntityUri(nfcId), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getApplicationContext(), response, Toast.LENGTH_SHORT).show();
                deleteEntityFromCache(nfcId);
                deleteImageFile(nfcId);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(stringRequest);
    }

    private void deleteImageFile(String nfcId) {
        File f =   new File(getExternalCacheDir(),nfcId);
        if(f.exists()){
            f.delete();
        }
    }


    private void deleteEntityFromCache(String nfcId) {
        getContentResolver().delete(Uri.withAppendedPath(TableNames.mContentUri,nfcId),null,null);
    }

    private String buildDeleteEntityUri(String nfcId){
        String host = getResources().getString(R.string.urlServer);
        String deletePhp = getResources().getString(R.string.urlNfcDelete);
        String url = host + deletePhp;
        String nfcQuery = "nfcid";
        String s = Uri.parse(url).buildUpon().appendQueryParameter(nfcQuery, nfcId).build().toString();
        return s;
    }

    private AlertDialog.Builder buildDialog(String title,String message){
        AlertDialog.Builder dialog = new AlertDialog.Builder(AdminActivity.this);
        if(!title.equalsIgnoreCase("")){
            dialog.setTitle(title);
        }
        if(!message.equalsIgnoreCase("")){
            dialog.setTitle(message);
        }
        return dialog;
    }

    private int addIfNotExists(String nfcId){
        Cursor c = getContentResolver().query(TableNames.mContentUri,null,null,null,null);
        int count = 0;
        while (c.moveToNext()){
            if(nfcId.equalsIgnoreCase(c.getString(c.getColumnIndex(TableNames.table1.mNfcId)))){
                count++;
                break;
            }
        }
        return count;
    }

    private String getUrl() {
        String server = getResources().getString(R.string.urlServer);
        String singleQuery = getResources().getString(R.string.urlAllEntity);
        return server+singleQuery;
    }

    private void checkDatabase() {
        mRefresh.setRefreshing(true);
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, getUrl(), null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    getJson(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
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

    private void getJson(JSONArray response) throws JSONException, ExecutionException, InterruptedException {
        if (response.length() > 0) {
            ContentValues cv = new ContentValues();
            for (int i = 0; i < response.length(); i++) {
                JSONObject entity = response.getJSONObject(i);
                String nfcId = entity.getString("nfcid");
                if(addIfNotExists(nfcId)==0){
                    int rollNo = entity.getInt("rollno");
                    String name = entity.getString("studentname");
                    String address = entity.getString("studentaddress");
                    String phoneno = entity.getString("studentphoneno");
                    String cls = entity.getString("studentclass");

                    String photo = entity.getString("studentphoto");

                    if (address.indexOf('\\') != -1) {
                        address.replaceAll("\\/", "/");
                    }
                    cv.put(TableNames.table1.mNfcId, nfcId);
                    cv.put(TableNames.table1.mRoLLNumber, rollNo);
                    cv.put(TableNames.table1.mName, name);
                    cv.put(TableNames.table1.mAddress, address);
                    cv.put(TableNames.table1.mPhoneNo, phoneno);
                    cv.put(TableNames.table1.mClass, cls);

                    String filename = nfcId+".jpg";
                    cv.put(TableNames.table1.mPhoto, filename);
                    String url = getResources().getString(R.string.urlBucketHost)+getResources().getString(R.string.urlBucketName)+photo;
                    new ImageDownload(getApplicationContext()).execute(url,filename);
                    getContentResolver().insert(TableNames.mContentUri, cv);
                }
            }
            mRefresh.setRefreshing(false);
        }else {
            mRefresh.setRefreshing(false);
            Toast.makeText(getApplicationContext(),"Database Empty",Toast.LENGTH_SHORT).show();
        }
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
        mRefresh.setRefreshing(false);
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
