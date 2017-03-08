package com.drivool.nrs.nfcattendance;

import android.app.Fragment;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import com.drivool.nrs.nfcattendance.fragments.AllList;
import com.drivool.nrs.nfcattendance.fragments.HistoryList;
import com.drivool.nrs.nfcattendance.fragments.PresentList;


public class MainActivity extends AppCompatActivity {


    Toolbar mainToolbar;
    DrawerLayout mDrawerLayout;
    LinearLayout fragmentContainer;
    NavigationView mNavigationView;
    android.support.v4.app.Fragment presentList;
    android.support.v4.app.Fragment allList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initilize();
        initilizeDrawer();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (savedInstanceState == null) {
            presentList = new PresentList();
            ft.add(R.id.fragmentContainer,presentList ).commit();
        }
    }

    private void initilize() {
        mainToolbar = (Toolbar)findViewById(R.id.mainToolbar);
        setSupportActionBar(mainToolbar);
        fragmentContainer = (LinearLayout)findViewById(R.id.fragmentContainer);
    }

    private void initilizeDrawer(){
        mDrawerLayout = (DrawerLayout)findViewById(R.id.mainDrawerLayout);
        mNavigationView = (NavigationView)findViewById(R.id.mainNaviagtionView);
        mNavigationView.getMenu().getItem(0).setChecked(true);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this,mDrawerLayout,mainToolbar,R.string.drawerOpen,R.string.drawerClose){
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };
        mDrawerLayout.setDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                switch (item.getItemId()){
                    case R.id.navigationPresent:
                        ft.replace(R.id.fragmentContainer,new PresentList());
                        drawerAction(0);
                        break;
                    case R.id.navigationAll:
                        ft.replace(R.id.fragmentContainer,new AllList());
                        drawerAction(1);
                        break;
                    case R.id.navigationHistory:
                        ft.replace(R.id.fragmentContainer,new HistoryList());
                        drawerAction(2);
                        break;
                    case R.id.navigationSettings:
                        startActivity(new Intent(MainActivity.this,Prefrences.class));
                        break;
                }
                ft.addToBackStack(null);
                ft.commit();
                return false;
            }
        });
    }

    private void drawerAction(int key) {
        invalidateOptionsMenu();
        MenuItem presentList = mNavigationView.getMenu().getItem(0).setChecked(false);
        MenuItem allEntities = mNavigationView.getMenu().getItem(1).setChecked(false);
        MenuItem history = mNavigationView.getMenu().getItem(2).setChecked(false);
        mDrawerLayout.closeDrawers();
        switch (key) {
            case 0:
                presentList.setChecked(true);
                getSupportActionBar().setTitle(getResources().getString(R.string.app_name));
                break;
            case 1:
                allEntities.setChecked(true);
                getSupportActionBar().setTitle(getResources().getString(R.string.titleAllEntities));
                break;
            case 2:
                history.setChecked(true);
                getSupportActionBar().setTitle(getResources().getString(R.string.titleHistory));
                break;

        }
    }


}
