package com.drivool.nrs.nfcattendance.File;

import android.content.Context;
import android.graphics.Bitmap;

import com.drivool.nrs.nfcattendance.Netwrok.UploadImage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Nikhil on 31-Mar-17.
 */

public class FileOperations {

     Context mContext;

    public FileOperations(Context context){
        mContext = context;
    }

    public void saveImage(String fileName,Bitmap image){
        if(image!=null) {
            File folder = mContext.getExternalCacheDir();
            File f = new File(folder, fileName);
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(f);
                image.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
