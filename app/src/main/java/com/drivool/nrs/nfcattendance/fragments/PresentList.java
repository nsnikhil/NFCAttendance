package com.drivool.nrs.nfcattendance.fragments;


import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import com.drivool.nrs.nfcattendance.EntityDialog;
import com.drivool.nrs.nfcattendance.R;
import com.drivool.nrs.nfcattendance.data.TableNames.table1;
import com.drivool.nrs.nfcattendance.data.TableNames;


public class PresentList extends Fragment implements View.OnClickListener{

    GridView presentList;
    Button scan,endTrip;

    int[] nfcIds = {151,152,153,154,155,156,157,158,159,160};

    public PresentList() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_present_list, container, false);
        initilize(v);
        checkTable();
        return v;
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
        scan = (Button) v.findViewById(R.id.scan);
        endTrip = (Button)v.findViewById(R.id.finish);
        scan.setOnClickListener(this);
        endTrip.setOnClickListener(this);
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.scan:
                EntityDialog entityDialog = new EntityDialog();
                Bundle args = new Bundle();
                args.putString("urd", TableNames.mContentUri+"/"+nfcIds[1]);
                entityDialog.setArguments(args);
                entityDialog.show(getFragmentManager(),"dialog");
                break;
            case R.id.finish:
                break;
        }
    }
}
