package com.drivool.nrs.nfcattendance.fragments;


import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import android.widget.Toast;

import com.drivool.nrs.nfcattendance.CursorAdaptr;
import com.drivool.nrs.nfcattendance.EntityDialog;
import com.drivool.nrs.nfcattendance.R;
import com.drivool.nrs.nfcattendance.data.TableNames.table1;
import com.drivool.nrs.nfcattendance.data.TableNames.table2;
import com.drivool.nrs.nfcattendance.data.TableNames;

import java.text.SimpleDateFormat;


public class PresentList extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    GridView presentList;
    private static final int mPresentLoaderId = 1;
    int[] nfcIds = {151,152,153,154,155,156,157,158,159,160};
    CursorAdaptr cusrAdaptr;
    int  k = 0;
    String numbers;
    String selction = table1.mNfcId+" IN ( " + numbers +" )";
    StringBuilder stringBuilder = new StringBuilder();
    ImageView emptyState;

    public PresentList() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_present_list, container, false);
        initilize(v);
        checkTable();
        setHasOptionsMenu(true);
        cusrAdaptr = new CursorAdaptr(getActivity(),null);
        presentList.setAdapter(cusrAdaptr);
        if(savedInstanceState!=null){
            selction = savedInstanceState.getString(getActivity().getResources().getString(R.string.bundleSelctn));
            Toast.makeText(getActivity(),"restoring "+selction,Toast.LENGTH_SHORT).show();
        }
        loadfData();
        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState!=null){
            selction = savedInstanceState.getString(getActivity().getResources().getString(R.string.bundleSelctn));
            loadfData();
        }
    }

    private void checkTable() {
        if(getActivity().getContentResolver().query(TableNames.mContentUri,null,null,null,null).getCount()==0){
            final AlertDialog.Builder alerDialog = new AlertDialog.Builder(getActivity());
            alerDialog.setMessage("Please wait while a fake database of students is being created");
            alerDialog.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            alerDialog.create().show();
            if(insertVal()){
                Toast.makeText(getActivity(),"Value Inserted",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean insertVal() {
        ContentValues cv = new ContentValues();
        for(int i=0;i<10;i++){
            cv.put(table1.mNfcId,nfcIds[i]);
            cv.put(table1.mName,"Student "+i);
            cv.put(table1.mPhoneNo,Integer.parseInt(801341007+i+""));
            cv.put(table1.mAddress,i+ " Death Star");
            cv.put(table1.mRoLLNumber,Integer.parseInt(105+i+06+""));
            cv.put(table1.mClass,i+1);
            cv.put(table1.mPhoto,"");
            Uri count = getActivity().getContentResolver().insert(TableNames.mContentUri,cv);
            if(count==null){
                Toast.makeText(getActivity(),"Insertion Failed",Toast.LENGTH_SHORT).show();
                return false;
            }else {
                Toast.makeText(getActivity(),i+"/9",Toast.LENGTH_SHORT).show();
            }
        }
        return true;
    }

    private void initilize(View v){
        presentList = (GridView)v.findViewById(R.id.presentList);
        emptyState = (ImageView)v.findViewById(R.id.emptyState);
        presentList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor c = (Cursor) parent.getItemAtPosition(position);
                Bundle args = new Bundle();
                args.putInt(getActivity().getResources().getString(R.string.bundleSelctn),c.getInt(c.getColumnIndex(table1.mNfcId)));
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
            case R.id.menuScan:
                if(k<nfcIds.length){
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(getActivity().getResources().getString(R.string.sqlDateTimeFormat));
                    java.util.Calendar calendar1 = java.util.Calendar.getInstance();
                    String date  = simpleDateFormat.format(calendar1.getTime());
                    insertDate(nfcIds[k],date);
                    k++;
                }else {
                    MediaPlayer mediaPlayer = MediaPlayer.create(getActivity(), R.raw.error);
                    mediaPlayer.start();
                    Toast.makeText(getActivity(),"Every Student has been scanned",Toast.LENGTH_SHORT).show();
                }
                break;
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
                        selction =  table1.mNfcId+" IN ( )";
                        k = 0;
                        loadfData();
                        Toast.makeText(getActivity(),"Trip Finished",Toast.LENGTH_SHORT).show();
                    }
                });
                confrim.create().show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void insertDate(int nfcId,String date) {
        ContentValues cv = new ContentValues();
        cv.put(table2.mNfcId,nfcId);
        cv.put(table2.mGetOnTime,date);
        cv.put(table2.mGetOffTime,"");
        Toast.makeText(getActivity(),nfcId+" "+date,Toast.LENGTH_SHORT).show();
        Uri u = getActivity().getContentResolver().insert(TableNames.mScheduleContentUri, cv);
        if (u == null) {
            MediaPlayer mediaPlayer = MediaPlayer.create(getActivity(), R.raw.error);
            mediaPlayer.start();
            Toast.makeText(getActivity(), "Error while scanning student id", Toast.LENGTH_SHORT).show();
        } else {
            MediaPlayer mediaPlayer = MediaPlayer.create(getActivity(), R.raw.correct);
            if(stringBuilder.toString().isEmpty()){
                stringBuilder.append(nfcId);
            }else {
                stringBuilder.append(","+nfcId);
            }
            numbers = stringBuilder.toString();
            selction = table1.mNfcId+" IN ( " + numbers +" )";
            loadfData();
            mediaPlayer.start();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id){
            case mPresentLoaderId:
                return new CursorLoader(getActivity(),TableNames.mContentUri,null,selction,null,null);
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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Toast.makeText(getActivity(),"saving "+selction,Toast.LENGTH_SHORT).show();
        outState.putString(getActivity().getResources().getString(R.string.bundleSelctn),selction);
    }

    private void checkEmpty(Cursor c){
        if(c.getCount()==0){
            emptyState.setVisibility(View.VISIBLE);
        }else {
            emptyState.setVisibility(View.GONE);
        }
    }
}
