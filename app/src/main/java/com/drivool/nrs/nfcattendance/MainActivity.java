package com.drivool.nrs.nfcattendance;

import android.app.Fragment;
import android.content.Intent;
import android.os.PersistableBundle;
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
import android.widget.Toast;

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
    android.support.v4.app.Fragment historyList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initilize();
        initilizeDrawer();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (savedInstanceState == null) {
            presentList = new PresentList();
            allList = new AllList();
            historyList = new HistoryList();
            //ft.add(R.id.fragmentContainer,presentList ).commit();
            ft.add(R.id.fragmentContainer,presentList);
            ft.add(R.id.fragmentContainer,allList);
            ft.add(R.id.fragmentContainer,historyList);
            ft.show(presentList);
            ft.commit();
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
                        //ft.replace(R.id.fragmentContainer,new PresentList());
                        ft.show(presentList);
                        ft.hide(allList);
                        ft.hide(historyList);
                        drawerAction(0);
                        break;
                    case R.id.navigationAll:
                        //ft.replace(R.id.fragmentContainer,new AllList());
                        ft.show(allList);
                        ft.hide(presentList);
                        ft.hide(historyList);
                        drawerAction(1);
                        break;
                    case R.id.navigationHistory:
                        //ft.replace(R.id.fragmentContainer,new HistoryList());
                        ft.show(historyList);
                        ft.hide(presentList);
                        ft.hide(allList);
                        drawerAction(2);
                        break;
                    case R.id.navigationSettings:
                        mDrawerLayout.closeDrawers();
                        startActivity(new Intent(MainActivity.this,Prefrences.class));
                        break;
                }
                //ft.addToBackStack(null);
                ft.commit();
                return false;
            }
        });
    }


    private void drawerAction(int key) {
        invalidateOptionsMenu();
        MenuItem menuPresentList = mNavigationView.getMenu().getItem(0).setChecked(false);
        MenuItem menuAllEntities = mNavigationView.getMenu().getItem(1).setChecked(false);
        MenuItem menuHistory = mNavigationView.getMenu().getItem(2).setChecked(false);
        mDrawerLayout.closeDrawers();
        switch (key) {
            case 0:
                menuPresentList.setChecked(true);
                getSupportActionBar().setTitle(getResources().getString(R.string.app_name));
                break;
            case 1:
                menuAllEntities.setChecked(true);
                getSupportActionBar().setTitle(getResources().getString(R.string.titleAllEntities));
                break;
            case 2:
                menuHistory.setChecked(true);
                getSupportActionBar().setTitle(getResources().getString(R.string.titleHistory));
                break;

        }
    }
}
