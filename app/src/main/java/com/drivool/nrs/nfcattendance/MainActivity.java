package com.drivool.nrs.nfcattendance;

import android.app.Fragment;
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
                        if(presentList==null){
                            if(allList!=null){
                                ft.remove(allList);
                                allList = null;
                            }
                            presentList = new PresentList();
                            ft.replace(R.id.fragmentContainer,presentList);
                        }
                        mDrawerLayout.closeDrawers();
                        break;
                    case R.id.navigationAll:
                        if(allList==null){
                            if(presentList!=null){
                                ft.remove(presentList);
                                presentList = null;
                            }
                            allList = new AllList();
                            ft.replace(R.id.fragmentContainer,allList);
                        }
                        mDrawerLayout.closeDrawers();
                        break;
                    case R.id.navigationHistory:
                        mDrawerLayout.closeDrawers();
                        break;
                    case R.id.navigationSettings:
                        mDrawerLayout.closeDrawers();
                        break;
                }
                ft.addToBackStack(null);
                ft.commit();
                return false;
            }
        });
    }


}
