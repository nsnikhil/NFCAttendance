package com.drivool.nrs.nfcattendance.Adapter;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.drivool.nrs.nfcattendance.R;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by Nikhil on 11-Mar-17.
 */

public class HistoryAdapter extends ArrayAdapter<String> {

    Context mContext;
    ArrayList<String> mDates;

    public HistoryAdapter(@NonNull Context context, ArrayList<String> list) {
        super(context, 0);
        mContext = context;
        mDates = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        HistoryViewHolder historyViewHolder;
        if(convertView==null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.history_layout,parent,false);
            historyViewHolder = new HistoryViewHolder(convertView);
            convertView.setTag(historyViewHolder);
        }else {
            historyViewHolder = (HistoryViewHolder) convertView.getTag();
        }
        historyViewHolder.date.setText(mDates.get(position));
        return convertView;
    }

    @Override
    public int getCount() {
        return mDates.size();
    }

    @Nullable
    @Override
    public String getItem(int position) {
        return mDates.get(position);
    }

    public  class HistoryViewHolder{
        TextView date;
        HistoryViewHolder(View v){
            date = (TextView) v.findViewById(R.id.historyDate);
        }
    }
}
