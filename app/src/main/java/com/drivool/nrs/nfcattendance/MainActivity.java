package com.drivool.nrs.nfcattendance;


import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    GridView bottomSheetList;
    Toolbar mainToolbar;
    TextView countText,scanText;
    ImageView scanImage;

    int[] nfcIds = {154,158,184,161,175,185,181,101,156,178,115};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initilize();
        setAdapter();
        setUpBottomSheet();
    }

    private void setAdapter() {
        ArrayList<EntityObject> list = new ArrayList<>();
        for(int i=0;i<10;i++){
            list.add(new EntityObject("Student "+i,null,0));
        }
        EntityAdapter adapter = new EntityAdapter(getApplicationContext(),list);
        bottomSheetList.setAdapter(adapter);
        bottomSheetList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(),bottomSheetList.getFirstVisiblePosition()+"",Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setUpBottomSheet() {
        View bottomSheet = findViewById(R.id.design_bottom_sheet);
        final BottomSheetBehavior behavior = BottomSheetBehavior.from(bottomSheet);
        behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_DRAGGING:
                        if (bottomSheetList.getFirstVisiblePosition()!=0){
                            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                        }
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                        countText.setBackground(getResources().getDrawable(R.color.colorPrimary));
                        countText.setTextColor(getResources().getColor(R.color.white));
                        bottomSheetList.setEnabled(true);

                        break;
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        countText.setBackground(getResources().getDrawable(R.color.cardview_light_background));
                        countText.setTextColor(getResources().getColor(R.color.cardview_dark_background));
                        bottomSheetList.setEnabled(false);

                        break;
                    case BottomSheetBehavior.STATE_HIDDEN:
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

    }

    private void initilize() {
        bottomSheetList = (GridView)findViewById(R.id.bootmSheetList);
        mainToolbar = (Toolbar)findViewById(R.id.mainToolbar);
        setSupportActionBar(mainToolbar);
        countText = (TextView)findViewById(R.id.countText);
        scanText = (TextView)findViewById(R.id.scanText);
        scanImage = (ImageView)findViewById(R.id.scanImage);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.countText:
                break;
            case R.id.scanText:
            case R.id.scanImage:
                Random random = new Random();
                int num  = nfcIds[random.nextInt(nfcIds.length)];
                Toast.makeText(getApplicationContext(),num+"",Toast.LENGTH_LONG).show();
                break;
        }
    }
}
