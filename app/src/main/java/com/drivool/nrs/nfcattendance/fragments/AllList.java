package com.drivool.nrs.nfcattendance.fragments;


import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.GridView;

import com.drivool.nrs.nfcattendance.CursorAdaptr;
import com.drivool.nrs.nfcattendance.R;
import com.drivool.nrs.nfcattendance.data.TableNames;


public class AllList extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    GridView allEntityList;
    private static final int mLoaderId = 5784;
    CursorAdaptr cursAdptr;

    public AllList() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_all_list, container, false);
        initilize(v);
        getLoaderManager().initLoader(mLoaderId,null,this);
        return v;
    }

    private void initilize(View v) {
        allEntityList = (GridView)v.findViewById(R.id.allEntityList);
        cursAdptr = new CursorAdaptr(getActivity(),null);
        allEntityList.setAdapter(cursAdptr);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id){
            case mLoaderId:
                return new CursorLoader(getActivity(),TableNames.mContentUri,null,null,null,null);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        cursAdptr.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        cursAdptr.swapCursor(null);
    }
}
