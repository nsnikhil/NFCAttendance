package com.drivool.nrs.nfcattendance;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by sid on 6/3/17.
 */

public class EntityAdapter extends BaseAdapter{

    ArrayList<EntityObject> list;
    Context mContext;

    EntityAdapter(Context context,ArrayList<EntityObject> arrayList){
        mContext = context;
        list = arrayList;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_layout,parent,false);
        TextView name = (TextView)v.findViewById(R.id.entityName);
        ImageView picture = (ImageView)v.findViewById(R.id.entityImage);
        EntityObject object = list.get(position);
        name.setText(object.getName());
        picture.setImageDrawable(mContext.getResources().getDrawable(R.drawable.profile));
        picture.setScaleType(ImageView.ScaleType.CENTER_CROP);
        return v;
    }
}
