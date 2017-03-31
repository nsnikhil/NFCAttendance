package com.drivool.nrs.nfcattendance;

import android.content.ContentValues;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.drivool.nrs.nfcattendance.Netwrok.ImageDownload;
import com.drivool.nrs.nfcattendance.data.TableNames;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.concurrent.ExecutionException;

public class DownloadActivity extends AppCompatActivity {

    ProgressBar mDownloadProgress;
    private static final String mFolderName = "profilepic";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        initilize();
        downloadDatabase();
    }

    private void initilize() {
        mDownloadProgress = (ProgressBar)findViewById(R.id.downladProgress);
    }

    private String getUrl() {
        String server = getResources().getString(R.string.urlServer);
        String singleQuery = getResources().getString(R.string.urlAllEntity);
        return server+singleQuery;
    }

    private void downloadDatabase() {
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, getUrl(), null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    getJson(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(jsonArrayRequest);
    }

    private void getJson(JSONArray response) throws JSONException, ExecutionException, InterruptedException {
        if (response.length() > 0) {
            ContentValues cv = new ContentValues();
            for (int i = 0; i < response.length(); i++) {
                JSONObject entity = response.getJSONObject(i);
                String nfcId = entity.getString("nfcid");
                int rollNo = entity.getInt("rollno");
                String name = entity.getString("studentname");
                String address = entity.getString("studentaddress");
                String phoneno = entity.getString("studentphoneno");
                String cls = entity.getString("studentclass");

                String photo = entity.getString("studentphoto");

                if (address.indexOf('\\') != -1) {
                    address.replaceAll("\\/", "/");
                }
                cv.put(TableNames.table1.mNfcId, nfcId);
                cv.put(TableNames.table1.mRoLLNumber, rollNo);
                cv.put(TableNames.table1.mName, name);
                cv.put(TableNames.table1.mAddress, address);
                cv.put(TableNames.table1.mPhoneNo, phoneno);
                cv.put(TableNames.table1.mClass, cls);

                String filename = nfcId+".jpg";
                cv.put(TableNames.table1.mPhoto, filename);
                String url = getResources().getString(R.string.urlBucketHost)+getResources().getString(R.string.urlBucketName)+"/"+photo;
                new ImageDownload(getApplicationContext()).execute(url,filename);

                getContentResolver().insert(TableNames.mContentUri, cv);
            }
        }else {
            Toast.makeText(getApplicationContext(),"Database Empty",Toast.LENGTH_SHORT).show();
            finish();
        }
        finish();
    }



}
