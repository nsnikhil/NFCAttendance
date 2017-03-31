package com.drivool.nrs.nfcattendance.Netwrok;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.Toast;

import com.drivool.nrs.nfcattendance.File.FileOperations;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;



public class ImageDownload extends AsyncTask<String,Void,Void> {

    Context mContext;
    private static final String mFolderName = "profilepic";

    public ImageDownload(Context context){
        mContext  = context;
    }

    @Override
    protected Void doInBackground(String... params) {
        new FileOperations(mContext.getApplicationContext()).saveImage(params[1],getImage(makeUrl(params[0])));
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        Toast.makeText(mContext,"File Saved",Toast.LENGTH_SHORT).show();
    }

    private URL makeUrl(String URL){
        URL u = null;
        try {
            u =  new URL(URL);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return u;
    }

    private Bitmap getImage(URL url) {
        HttpURLConnection htpc = null;
        InputStream is = null;
        Bitmap image = null;
        try {
            htpc = (HttpURLConnection) url.openConnection();
            htpc.setRequestMethod("GET");
            htpc.connect();
            is = htpc.getInputStream();
            image = BitmapFactory.decodeStream(is);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(htpc!=null){
                htpc.disconnect();
            }if(is!=null){
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return image;
    }
}
