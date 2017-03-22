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

import com.bumptech.glide.Glide;
import com.drivool.nrs.nfcattendance.data.TableHelper;
import com.drivool.nrs.nfcattendance.data.TableNames;
import com.drivool.nrs.nfcattendance.data.TableNames.table1;
import com.drivool.nrs.nfcattendance.data.TableNames.table2;
import com.drivool.nrs.nfcattendance.data.TableNames.tabletemp;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class EntityDialog extends android.support.v4.app.DialogFragment{

    ImageView picture;
    TextView name,phoneno,address,nfcId,boardtime,endTime;
    private static final String mFolderName = "profilepic";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.entity_dialog,container,false);
        initilize(v);
        String nfci = getArguments().getString(getActivity().getResources().getString(R.string.bundleSelctn));
        query(nfci);
        return v;
    }

    private void initilize(View v) {
        picture = (ImageView)v.findViewById(R.id.dialogPicture);
        name = (TextView)v.findViewById(R.id.dialogName);
        phoneno = (TextView)v.findViewById(R.id.dialogPhoneNo);
        address = (TextView)v.findViewById(R.id.dialogAddress);
        nfcId = (TextView)v.findViewById(R.id.dialogNfcId);
        boardtime = (TextView)v.findViewById(R.id.dialogBoardTime);
        endTime = (TextView)v.findViewById(R.id.dialogExitTime);
    }

    private void query(String aNfcId){
        SQLiteDatabase sqb = new TableHelper(getActivity()).getReadableDatabase();
        String rawQuery = "SELECT * FROM "+ TableNames.mTableTempName +" INNER JOIN "+ TableNames.mTableScheduleName +" ON " + tabletemp.mNfcId+ " = " + table2.mNfcId + " WHERE " + tabletemp.mNfcId+"=?";
        Cursor c = sqb.rawQuery(rawQuery,new String[]{aNfcId});
        if(c.moveToLast()){
            String nm = c.getString(c.getColumnIndex(tabletemp.mName));
            String phn = c.getString(c.getColumnIndex(tabletemp.mPhoneNo));
            String adr = c.getString(c.getColumnIndex(tabletemp.mAddress));
            String nfcId = c.getString(c.getColumnIndex(tabletemp.mNfcId));
            String bTime = c.getString(c.getColumnIndex(TableNames.table2.mGetOnTime));
            String eTime = c.getString(c.getColumnIndex(table2.mGetOffTime));
            String picUrl  =c.getString(c.getColumnIndex(tabletemp.mPhoto));
            setValues(nm,phn,adr,nfcId,bTime,eTime,picUrl);
        }
    }

    public void setValues(String nm,String phn,String adr,String nfc,String brdTm,String endTm,String url){
        name.setText("Name : "+nm);
        phoneno.setText("Phone No : "+phn);
        address.setText("Address : "+adr);
        nfcId.setText("NFC id : "+nfc);
        boardtime.setText("Board time : "+makeTime(brdTm));
        if(endTm.equalsIgnoreCase("")){
            endTime.setText("End time : On Trip ");
        }else {
            endTime.setText("End time : "+endTm);
        }
        setPicture(url);
    }

    private void setPicture(String pic){
        File folder = getActivity().getExternalFilesDir(mFolderName);
        File f = new File(folder,pic);
        Glide.with(this)
                .load(f)
                .centerCrop()
                .placeholder(R.drawable.profile)
                .crossFade()
                .into(picture);
    }


    private String makeTime(String tm)  {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("h:mm a");
        return simpleDateFormat.format(Long.parseLong(tm));
    }
}
