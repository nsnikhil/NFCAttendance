package com.drivool.nrs.nfcattendance.fragments;


import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.drivool.nrs.nfcattendance.Adapter.CursAdapter;
import com.drivool.nrs.nfcattendance.fragments.dialog.EntityDialogFragment;
import com.drivool.nrs.nfcattendance.R;
import com.drivool.nrs.nfcattendance.data.TableNames.tabletemp;
import com.drivool.nrs.nfcattendance.data.TableNames;

import java.util.Calendar;


public class PresentListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    GridView mPresentList;
    private static final int mPresentLoaderId = 1;
    CursAdapter mCusrAdaptr;
    ImageView mEmptyState;

    public PresentListFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_present_list, container, false);
        initilize(v);
        setHasOptionsMenu(true);
        mCusrAdaptr = new CursAdapter(getActivity(), null);
        mPresentList.setAdapter(mCusrAdaptr);
        loadfData();
        return v;
    }

    private void initilize(View v) {
        mPresentList = (GridView) v.findViewById(R.id.presentList);
        mEmptyState = (ImageView) v.findViewById(R.id.emptyState);
        mPresentList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor c = (Cursor) parent.getItemAtPosition(position);
                Bundle args = new Bundle();
                args.putString(getActivity().getResources().getString(R.string.bundleSelctn), c.getString(c.getColumnIndex(tabletemp.mNfcId)));
                EntityDialogFragment entityDialogFragment = new EntityDialogFragment();
                entityDialogFragment.setArguments(args);
                entityDialogFragment.show(getFragmentManager(), "dialog");
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.attendance_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuFinish:
                AlertDialog.Builder confrim = new AlertDialog.Builder(getActivity());
                confrim.setTitle(getActivity().getResources().getString(R.string.dialogWarning)).setMessage(getActivity().getResources().getString(R.string.dialogConfirmText));
                confrim.setNegativeButton(getActivity().getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                confrim.setPositiveButton(getActivity().getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(getActivity().getContentResolver().query(TableNames.mTempContentUri,null,null,null,null).getCount()<=0){
                            Toast.makeText(getActivity(), "Trip not started", Toast.LENGTH_SHORT).show();
                        }else if(getActivity().getContentResolver().query(TableNames.mScheduleContentUri,null,null,null,null).getCount()<=0) {
                            Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
                        }else {
                            Calendar cal  = Calendar.getInstance();
                            ContentValues cv = new ContentValues();
                            cv.put(TableNames.table2.mGetOffTime,String.valueOf(cal.getTimeInMillis()));
                            getActivity().getContentResolver().update(TableNames.mScheduleContentUri,cv,null,null);
                            getActivity().getContentResolver().delete(TableNames.mTempContentUri, null, null);
                            updateToServer();
                        }
                    }
                });
                confrim.create().show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateToServer() {
        Cursor c = getActivity().getContentResolver().query(TableNames.mScheduleContentUri,null,null,null,null);
        String server = getResources().getString(R.string.urlServer);
        String insertSchedule = getResources().getString(R.string.urlInsertSchedule);
        String url  = server+insertSchedule;
        while (c.moveToNext()){
            String u = buildUrl(c,url);
            RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
            StringRequest stringRequest = new StringRequest(Request.Method.GET, u, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Toast.makeText(getActivity(),response,Toast.LENGTH_SHORT).show();
                    getActivity().getContentResolver().delete(TableNames.mScheduleContentUri, null, null);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getActivity(),"Error",Toast.LENGTH_SHORT).show();
                }
            });
            requestQueue.add(stringRequest);
        }
    }

    private String buildUrl(Cursor c,String insUrl){
        String idQuery = "nfc";
        String idvalue = c.getString(c.getColumnIndex(TableNames.table2.mNfcId));
        String getontimeQuery = "dt";
        String getontimeValue = c.getString(c.getColumnIndex(TableNames.table2.mGetOnTime));
        String getofftimeQuery = "dte";
        String getofftimeValue = c.getString(c.getColumnIndex(TableNames.table2.mGetOffTime));
        return Uri.parse(insUrl).buildUpon()
                .appendQueryParameter(idQuery,idvalue)
                .appendQueryParameter(getontimeQuery,getontimeValue)
                .appendQueryParameter(getofftimeQuery,getofftimeValue)
                .build().toString();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case mPresentLoaderId:
                return new CursorLoader(getActivity(), TableNames.mTempContentUri, null, null, null, tabletemp.mId + " DESC");
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCusrAdaptr.swapCursor(data);
        checkEmpty(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCusrAdaptr.swapCursor(null);
    }

    private void loadfData() {
        if (getActivity().getSupportLoaderManager().getLoader(mPresentLoaderId) == null) {
            getActivity().getSupportLoaderManager().initLoader(mPresentLoaderId, null, this).forceLoad();
        } else {
            getActivity().getSupportLoaderManager().restartLoader(mPresentLoaderId, null, this).forceLoad();
        }
    }

    private void checkEmpty(Cursor c) {
        if (c.getCount() == 0) {
            mEmptyState.setVisibility(View.VISIBLE);
        } else {
            mEmptyState.setVisibility(View.GONE);
        }
    }
}
