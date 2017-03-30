package com.drivool.nrs.nfcattendance.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.drivool.nrs.nfcattendance.Adapter.HistoryAdapter;
import com.drivool.nrs.nfcattendance.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;


public class HistoryListFragment extends Fragment {


    ListView mHistoryList;

    public HistoryListFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_history_list, container, false);
        initilize(v);
        getListItems();
        return v;
    }

    private void initilize(View v) {
        mHistoryList = (ListView)v.findViewById(R.id.historyList);
    }

    private void getListItems(){
        ArrayList<String> dates = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, MMM d");
        dates.add(getActivity().getResources().getString(R.string.dateToday));
        for(int i=0;i<10;i++){
            calendar.add(Calendar.DATE, -1);
            if(i==0){
                dates.add(getActivity().getResources().getString(R.string.dateYesterday));
            }else {
                dates.add(simpleDateFormat.format(calendar.getTime()));
            }
        }
        HistoryAdapter adapter = new HistoryAdapter(getActivity(),dates);
        mHistoryList.setAdapter(adapter);
        mHistoryList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getActivity(),"Will show history for "+parent.getItemAtPosition(position).toString(),Toast.LENGTH_LONG).show();
            }
        });
    }



}
