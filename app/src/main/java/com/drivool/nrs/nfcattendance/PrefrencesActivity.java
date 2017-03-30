package com.drivool.nrs.nfcattendance;


import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


public class PrefrencesActivity extends AppCompatActivity {

    android.support.v7.widget.Toolbar mPrefrenceToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prefrences);
        initilize();
        getFragmentManager().beginTransaction().add(R.id.prefContainer,new PrefrenceFragment()).commit();
    }

    private void initilize() {
        mPrefrenceToolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.prefToolbar);
        setSupportActionBar(mPrefrenceToolbar);
    }

    public static class PrefrenceFragment extends PreferenceFragment{

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.prefrences);
        }
    }
}
