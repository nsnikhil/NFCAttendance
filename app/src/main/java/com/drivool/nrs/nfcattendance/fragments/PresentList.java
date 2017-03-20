package com.drivool.nrs.nfcattendance.fragments;


import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import com.drivool.nrs.nfcattendance.CursorAdaptr;
import com.drivool.nrs.nfcattendance.EntityDialog;
import com.drivool.nrs.nfcattendance.R;
import com.drivool.nrs.nfcattendance.data.TableHelper;
import com.drivool.nrs.nfcattendance.data.TableNames.table1;
import com.drivool.nrs.nfcattendance.data.TableNames;



public class PresentList extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    GridView presentList;
    private static final int mPresentLoaderId = 1;
    CursorAdaptr cusrAdaptr;
    ImageView emptyState;

    public PresentList() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_present_list, container, false);
        initilize(v);
        setHasOptionsMenu(true);
        cusrAdaptr = new CursorAdaptr(getActivity(),null);
        presentList.setAdapter(cusrAdaptr);
        loadfData();
        return v;
    }

    private void initilize(View v){
        presentList = (GridView)v.findViewById(R.id.presentList);
        emptyState = (ImageView)v.findViewById(R.id.emptyState);
       presentList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor c = (Cursor) parent.getItemAtPosition(position);
                Bundle args = new Bundle();
                args.putString(getActivity().getResources().getString(R.string.bundleSelctn),c.getString(c.getColumnIndex(table1.mNfcId)));
                EntityDialog entityDialog = new EntityDialog();
                entityDialog.setArguments(args);
                entityDialog.show(getFragmentManager(),"dialog");
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.attendance_menu,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
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
                        getActivity().getContentResolver().delete(TableNames.mContentUri,null,null);
                    }
                });
                confrim.create().show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id){
            case mPresentLoaderId:
                return new CursorLoader(getActivity(),TableNames.mContentUri,null,null,null,table1.mId+" DESC");
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        cusrAdaptr.swapCursor(data);
        checkEmpty(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        cusrAdaptr.swapCursor(null);
    }

    private void loadfData() {
        if (getActivity().getSupportLoaderManager().getLoader(mPresentLoaderId) == null) {
            getActivity().getSupportLoaderManager().initLoader(mPresentLoaderId, null, this).forceLoad();
        } else {
            getActivity().getSupportLoaderManager().restartLoader(mPresentLoaderId, null, this).forceLoad();
        }
    }

    private void checkEmpty(Cursor c){
        if(c.getCount()==0){
            emptyState.setVisibility(View.VISIBLE);
        }else {
            emptyState.setVisibility(View.GONE);
        }
    }
}
