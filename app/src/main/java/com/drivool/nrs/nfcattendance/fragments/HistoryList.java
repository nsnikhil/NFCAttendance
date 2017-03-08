package com.drivool.nrs.nfcattendance.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.drivool.nrs.nfcattendance.R;


public class HistoryList extends Fragment {


    public HistoryList() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_history_list, container, false);
        return v;
    }

}
