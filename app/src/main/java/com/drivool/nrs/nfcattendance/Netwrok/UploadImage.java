package com.drivool.nrs.nfcattendance.Netwrok;

import android.content.Context;
import android.os.AsyncTask;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;

import java.io.File;
import java.io.IOException;

/**
 * Created by Nikhil on 31-Mar-17.
 */

public class UploadImage extends AsyncTask<File, Void, Void> {

    Context mContext;
    String mFileName;

    public UploadImage(Context context,String filename){
        mContext = context;
        mFileName = filename;
    }

    @Override
    protected Void doInBackground(File... params) {
        try {
            uploadImage(params[0]);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
    }

    private void uploadImage(File f) throws IOException {
        AmazonS3 s3 = new AmazonS3Client(getCredentials());
        TransferUtility transferUtility = new TransferUtility(s3, mContext);
        TransferObserver observer = transferUtility.upload("nfcattensnimages", mFileName, f);
    }

    private CognitoCachingCredentialsProvider getCredentials() {
        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                mContext,
                "ap-northeast-1:64c4f251-c480-4c79-b192-c3719a29f949",
                Regions.AP_NORTHEAST_1
        );
        return credentialsProvider;
    }
}
