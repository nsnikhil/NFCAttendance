package com.drivool.nrs.nfcattendance.Adapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.drivool.nrs.nfcattendance.R;
import com.drivool.nrs.nfcattendance.data.TableNames.table1;

import java.io.File;

public class CursAdapter extends android.widget.CursorAdapter {

    private static final String mFolderName = "profilepic";

    public CursAdapter(Context context, Cursor c) {
        super(context, c);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_layout,parent,false);
        MyViewHolder myViewHolder = new MyViewHolder(v);
        v.setTag(myViewHolder);
        return v;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        MyViewHolder myViewHolder = (MyViewHolder) view.getTag();
        myViewHolder.name.setText(cursor.getString(cursor.getColumnIndex(table1.mName)));
        if(!cursor.getString(cursor.getColumnIndex(table1.mPhoto)).equalsIgnoreCase("null")){
            myViewHolder.picture.setImageBitmap(BitmapFactory.decodeFile(new File(context.getExternalCacheDir(),cursor.getString(cursor.getColumnIndex(table1.mPhoto))).toString()));
            myViewHolder.picture.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }else {
            myViewHolder.picture.setImageDrawable(context.getResources().getDrawable(R.drawable.profile));
        }
    }

    public class MyViewHolder{
        TextView name;
        ImageView picture;
        MyViewHolder(View v){
            name = (TextView)v.findViewById(R.id.entityName);
            picture = (ImageView)v.findViewById(R.id.entityImage);
        }
    }
}
