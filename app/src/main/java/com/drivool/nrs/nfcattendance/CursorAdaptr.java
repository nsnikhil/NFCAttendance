package com.drivool.nrs.nfcattendance;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.drivool.nrs.nfcattendance.data.TableNames.table1;

/**
 * Created by sid on 8/3/17.
 */

public class CursorAdaptr extends CursorAdapter{


    public CursorAdaptr(Context context, Cursor c) {
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
        String url = cursor.getString(cursor.getColumnIndex(table1.mPhoto));
        Glide.with(context).load(url)
                .centerCrop()
                .placeholder(R.drawable.profile)
                .crossFade()
                .into(myViewHolder.picture);
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
