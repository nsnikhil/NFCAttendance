package com.drivool.nrs.nfcattendance;

import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


public class EntityDialog extends android.support.v4.app.DialogFragment{

    ImageView picture;
    TextView name,phoneno,address,nfcId;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.entity_dialog,container,false);
        initilize(v);
        return v;
    }

    private void initilize(View v) {
        picture = (ImageView)v.findViewById(R.id.dialogPicture);
        name = (TextView)v.findViewById(R.id.dialogName);
        phoneno = (TextView)v.findViewById(R.id.dialogPhoneNo);
        address = (TextView)v.findViewById(R.id.dialogAddress);
        nfcId = (TextView)v.findViewById(R.id.dialogNfcId);
    }

    public void setValues(String nm,String phn,String adr,String nfc){
        picture.setImageDrawable(getResources().getDrawable(R.drawable.profile));
        name.setText(nm);
        phoneno.setText(phn);
        address.setText(adr);
        nfcId.setText(nfc);
    }
}
