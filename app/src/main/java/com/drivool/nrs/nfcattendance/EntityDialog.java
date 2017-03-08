package com.drivool.nrs.nfcattendance;

import android.app.DialogFragment;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.drivool.nrs.nfcattendance.data.TableNames.table1;


public class EntityDialog extends android.support.v4.app.DialogFragment{

    ImageView picture;
    TextView name,phoneno,address,nfcId;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.entity_dialog,container,false);
        initilize(v);
        Uri b = Uri.parse(getArguments().getString("urd"));
        Log.d("",b.toString());
        query(b);
        return v;
    }

    private void initilize(View v) {
        picture = (ImageView)v.findViewById(R.id.dialogPicture);
        picture.setImageDrawable(getResources().getDrawable(R.drawable.profile));
        name = (TextView)v.findViewById(R.id.dialogName);
        phoneno = (TextView)v.findViewById(R.id.dialogPhoneNo);
        address = (TextView)v.findViewById(R.id.dialogAddress);
        nfcId = (TextView)v.findViewById(R.id.dialogNfcId);
    }

    private void query(Uri u){
        Cursor c = getActivity().getContentResolver().query(u,null,null,null,null);
        if(c.moveToNext()){
            String nm = c.getString(c.getColumnIndex(table1.mName));
            int phn = c.getInt(c.getColumnIndex(table1.mPhoneNo));
            String adr = c.getString(c.getColumnIndex(table1.mAddress));
            int nfcId = c.getInt(c.getColumnIndex(table1.mNfcId));
            setValues(nm,phn,adr,nfcId);
        }
    }

    public void setValues(String nm,int phn,String adr,int nfc){
        name.setText(nm);
        phoneno.setText(String.valueOf(phn));
        address.setText(adr);
        nfcId.setText(String.valueOf(nfc));
    }
}
