package com.drivool.nrs.nfcattendance;

import android.app.DialogFragment;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.drivool.nrs.nfcattendance.data.TableHelper;
import com.drivool.nrs.nfcattendance.data.TableNames;
import com.drivool.nrs.nfcattendance.data.TableNames.table1;
import com.drivool.nrs.nfcattendance.data.TableNames.table2;



public class EntityDialog extends android.support.v4.app.DialogFragment{

    ImageView picture;
    TextView name,phoneno,address,nfcId,boardtime,endTime;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.entity_dialog,container,false);
        initilize(v);
        int nfci = getArguments().getInt(getActivity().getResources().getString(R.string.bundleSelctn));
        query(nfci);
        return v;
    }

    private void initilize(View v) {
        picture = (ImageView)v.findViewById(R.id.dialogPicture);
        picture.setImageDrawable(getResources().getDrawable(R.drawable.profile));
        name = (TextView)v.findViewById(R.id.dialogName);
        phoneno = (TextView)v.findViewById(R.id.dialogPhoneNo);
        address = (TextView)v.findViewById(R.id.dialogAddress);
        nfcId = (TextView)v.findViewById(R.id.dialogNfcId);
        boardtime = (TextView)v.findViewById(R.id.dialogBoardTime);
        endTime = (TextView)v.findViewById(R.id.dialogExitTime);
    }

    private void query(int aNfcId){
        SQLiteDatabase sqb = new TableHelper(getActivity()).getReadableDatabase();
        String rawQuery = "SELECT * FROM "+ TableNames.mTableName +" INNER JOIN "+ TableNames.mTableScheduleName +" ON " + table1.mNfcId+ " = " + table2.mNfcId + " WHERE " + table1.mNfcId+"=?";
        Cursor c = sqb.rawQuery(rawQuery,new String[]{String.valueOf(aNfcId)});
        if(c.moveToLast()){
            String nm = c.getString(c.getColumnIndex(table1.mName));
            int phn = c.getInt(c.getColumnIndex(table1.mPhoneNo));
            String adr = c.getString(c.getColumnIndex(table1.mAddress));
            int nfcId = c.getInt(c.getColumnIndex(table1.mNfcId));
            String bTime = c.getString(c.getColumnIndex(TableNames.table2.mGetOnTime));
            setValues(nm,phn,adr,nfcId,bTime);
        }
    }

    public void setValues(String nm,int phn,String adr,int nfc,String brdTm){
        name.setText(nm);
        phoneno.setText(String.valueOf(phn));
        address.setText(adr);
        nfcId.setText(String.valueOf(nfc));
        boardtime.setText(makeTime(brdTm));
        endTime.setText("End time : " );
    }


    private String makeTime(String tm){
        tm.trim();
        String time = tm.substring(tm.indexOf(" "),tm.length());
        String hr = time.substring(0,time.indexOf(':'));
        hr = hr.trim();
        String min = time.substring(time.indexOf(':'),time.lastIndexOf(':'));
        if(Integer.parseInt(hr)>12){
            hr = String.valueOf(Integer.parseInt(hr) - 12);
            return "Boarded at : "+ hr+min+" p.m";
        }else {
            return "Boarded at : "+hr+min+" p.m";
        }
    }
}
