package com.drivool.nrs.nfcattendance.fragments;


import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.drivool.nrs.nfcattendance.Adapter.CursorAdaptr;
import com.drivool.nrs.nfcattendance.EntityDialog;
import com.drivool.nrs.nfcattendance.R;
import com.drivool.nrs.nfcattendance.data.TableNames;


public class AlList extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    GridView allList;
    private static final int mPresentLoaderId = 2;
    CursorAdaptr cusrAdaptr;
    ImageView emptyState;

    public AlList() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_present_list, container, false);
        initilize(v);
        cusrAdaptr = new CursorAdaptr(getActivity(), null);
        allList.setAdapter(cusrAdaptr);
        loadfList();
        return v;
    }

    private void initilize(View v) {
        allList = (GridView) v.findViewById(R.id.presentList);
        emptyState = (ImageView) v.findViewById(R.id.emptyState);
        emptyState.setVisibility(View.GONE);
        allList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor c = (Cursor) parent.getItemAtPosition(position);
                if(c.moveToPosition(position)){
                    Toast.makeText(getActivity(),c.getString(c.getColumnIndex(TableNames.table1.mNfcId)),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case mPresentLoaderId:
                return new CursorLoader(getActivity(), TableNames.mContentUri, null, null, null, null);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        cusrAdaptr.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        cusrAdaptr.swapCursor(null);
    }

    private void loadfList() {
        if (getActivity().getSupportLoaderManager().getLoader(mPresentLoaderId) == null) {
            getActivity().getSupportLoaderManager().initLoader(mPresentLoaderId, null, this).forceLoad();
        } else {
            getActivity().getSupportLoaderManager().restartLoader(mPresentLoaderId, null, this).forceLoad();
        }
    }
}
