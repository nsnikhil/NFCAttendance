package com.drivool.nrs.nfcattendance;


import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.drivool.nrs.nfcattendance.data.TableNames;

import org.json.JSONArray;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;


public class NewUserActivity extends AppCompatActivity {


    EditText mName,mPhone,mAddres,mClassname,mRoll;
    TextView mNfcId;
    FloatingActionButton mSave;
    private NfcAdapter mNfcAdapter;
    ImageView mPicture;
    private static final int CAMERA_REQUEST_CODE = 155;
    private static final int GALLERY_REQUEST_CODE = 156;
    Bitmap mImage = null;
    Toolbar mUserToolbar;
    String mIdPresent = "Id Already Present";
    RelativeLayout mEntityContainer;
    String mFileName = null;
    private static final String mNullValue = "N/A";
    private static final int mNfcRequestCode = 155;
    String mNcfIdValue = null;
    PendingIntent mPendingIntent;


    public NewUserActivity() {
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);
        initilize();
        checkNfc();
    }


    @Override
    protected void onResume() {
        super.onResume();
        mNfcAdapter.enableForegroundDispatch(this, mPendingIntent, null, null);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mNfcAdapter.disableForegroundDispatch(this);
    }

    private void checkNfc() {
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mNfcAdapter == null) {
            Snackbar.make(mEntityContainer, "Nfc Unavaialble", BaseTransientBottomBar.LENGTH_INDEFINITE).setActionTextColor(getResources().getColor(R.color.white)).show();
        } else if (!mNfcAdapter.isEnabled()) {
            Snackbar.make(mEntityContainer, "Nfc Disabled", BaseTransientBottomBar.LENGTH_INDEFINITE).setAction("Turn On", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivityForResult(new Intent(Settings.ACTION_NFC_SETTINGS), mNfcRequestCode);
                }
            }).setActionTextColor(getResources().getColor(R.color.white)).show();
        }
        mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
    }

    private void initilize() {
        mUserToolbar = (Toolbar)findViewById(R.id.newUserToolbar);
        mEntityContainer = (RelativeLayout)findViewById(R.id.newEntityContainer);
        setSupportActionBar(mUserToolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.addentity));
        mName = (EditText)findViewById(R.id.newEntityName);
        mPhone = (EditText)findViewById(R.id.newEntityPhone);
        mAddres = (EditText)findViewById(R.id.newEntityAddress);
        mClassname = (EditText)findViewById(R.id.newEntityClass);
        mRoll = (EditText)findViewById(R.id.newEntityRoll);
        mNfcId = (TextView)findViewById(R.id.newEntityNfcId);
        mSave = (FloatingActionButton)findViewById(R.id.newEntitySave);
        mPicture = (ImageView)findViewById(R.id.newEntityImage);
        mSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(verifyFields()){
                    if (mImage!=null) {
                        mFileName = mNfcId.getText().toString()+".jpg";
                        if(checkConnection()){
                            saveTemp(mFileName);
                            insertToDatabase();
                        }else {
                            Toast.makeText(getApplicationContext(),"Check you internet connection",Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        Toast.makeText(getApplicationContext(), "Take a picture", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        mPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImageAction();
            }
        });
    }

    private void insertToDatabase(){
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, buildInsertUri(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getApplicationContext(),response,Toast.LENGTH_SHORT).show();
                cacheLocally();
                clearFields();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"Error",Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(stringRequest);
    }

    private void cacheLocally(){
        ContentValues cv = new ContentValues();
        cv.put(TableNames.table1.mNfcId, mNfcId.getText().toString());
        cv.put(TableNames.table1.mRoLLNumber, mRoll.getText().toString());
        cv.put(TableNames.table1.mName, mName.getText().toString());
        cv.put(TableNames.table1.mAddress, mAddres.getText().toString());
        cv.put(TableNames.table1.mPhoneNo, mPhone.getText().toString());
        cv.put(TableNames.table1.mClass, mClassname.getText().toString());
        cv.put(TableNames.table1.mPhoto,mFileName );
        getContentResolver().insert(TableNames.mContentUri, cv);
    }

    private void clearFields() {
        mFileName = null;
        mNcfIdValue = null;
        mName.setText("");
        mPhone.setText("");
        mAddres.setText("");
        mClassname.setText("");
        mRoll.setText("");
        mPicture.setImageDrawable(getResources().getDrawable(R.drawable.profile));
        mNfcId.setText(getResources().getString(R.string.entitynfcid));
    }

    private String buildInsertUri(){
        String host = getResources().getString(R.string.urlServer);
        String insertPhp = getResources().getString(R.string.urlInsertNew);
        String url = host+insertPhp;
        String nameQuery = "nm";
        String nameValue = mName.getText().toString();
        String rollQuery = "rln";
        String rollValue = mRoll.getText().toString();
        String addressQuery = "adr";
        String addressValue = mAddres.getText().toString();
        String phoneQuery = "phn";
        String phoneValue = mPhone.getText().toString();
        String classQuery = "cls";
        String classValue = mClassname.getText().toString();
        String nfcQuery = "nfc";
        String nfcValue = mNcfIdValue;
        String photoQuery = "pic";
        String photoValue = mFileName;
        return Uri.parse(url).buildUpon()
                .appendQueryParameter(nameQuery,nameValue)
                .appendQueryParameter(rollQuery,rollValue)
                .appendQueryParameter(addressQuery,addressValue)
                .appendQueryParameter(phoneQuery,phoneValue)
                .appendQueryParameter(classQuery,classValue)
                .appendQueryParameter(nfcQuery,nfcValue)
                .appendQueryParameter(photoQuery,photoValue)
                .build().toString();
    }

    private boolean checkConnection() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

    private void chooseImageAction() {
        AlertDialog.Builder choosePath = new AlertDialog.Builder(NewUserActivity.this);
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(NewUserActivity.this, android.R.layout.simple_list_item_1);
        arrayAdapter.add("Take a picture");
        arrayAdapter.add("Choose from galley");
        choosePath.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int position) {
                if (position == 0) {
                    Intent intentcam = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intentcam, CAMERA_REQUEST_CODE);
                }
                if (position == 1) {
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*");
                    startActivityForResult(intent, GALLERY_REQUEST_CODE);
                }
            }
        });
        choosePath.create().show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == GALLERY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                InputStream is = null;
                try {
                    is = getContentResolver().openInputStream(data.getData());
                    mImage = BitmapFactory.decodeStream(is);
                    mPicture.setImageBitmap(mImage);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Bundle extras = data.getExtras();
                mImage = (Bitmap) extras.get("data");
                mPicture.setImageBitmap(mImage);
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        getId(intent);
    }

    private void getId(Intent intent) {
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            if (tag == null) {
                Toast.makeText(getApplicationContext(), "NULL TAG", Toast.LENGTH_SHORT).show();
            } else {
                String tagInfo = "";
                byte[] tagId = tag.getId();
                for (int i = 0; i < tagId.length; i++) {
                    tagInfo += Integer.toHexString(tagId[i] & 0xFF) + " ";
                }
                mNcfIdValue = tagInfo.replace(" ", "");
                checkExists(mNcfIdValue);
            }
        }
    }

    private String buildUrl(String nfcId){
        String host = getResources().getString(R.string.urlServer);
        String queryPhp =  getResources().getString(R.string.urlSingleEntity);
        String url = host+queryPhp;
        String nfcQuery = "nfc";
        String s =  Uri.parse(url).buildUpon().appendQueryParameter(nfcQuery,nfcId).build().toString();
        return s;
    }

    private void checkExists(final String id){

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, buildUrl(id), null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                if(response.length()>0){
                    Toast.makeText(getApplicationContext(),mIdPresent,Toast.LENGTH_SHORT).show();
                }else {
                    mNfcId.setText(id);
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

    private void saveTemp(String fileName){
        File folder = getExternalCacheDir();
        File f = new File(folder,fileName);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(f);
            mImage.compress(Bitmap.CompressFormat.JPEG,100,fos);
            new uploadAsync().execute(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(fos!=null){
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void uploadImage(File f) throws IOException {
        SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        AmazonS3 s3 = new AmazonS3Client(getCredentials());
        TransferUtility transferUtility = new TransferUtility(s3, getApplicationContext());
        TransferObserver observer = transferUtility.upload("nfcattensnimages", mNfcId.getText().toString()+".jpg", f);
    }


    private CognitoCachingCredentialsProvider getCredentials() {
        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                getApplicationContext(),
                "ap-northeast-1:64c4f251-c480-4c79-b192-c3719a29f949",
                Regions.AP_NORTHEAST_1
        );
        return credentialsProvider;
    }

    public class uploadAsync extends AsyncTask<File, Void, Void> {

        @Override
        protected Void doInBackground(File... params) {
            try {
                uploadImage(params[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }


    private boolean verifyFields(){
        if(mName.getText().toString().isEmpty()||mName.getText().toString().length()==0){
            Toast.makeText(getApplicationContext(), "Enter the name", Toast.LENGTH_SHORT).show();
            return false;
        }if(mRoll.getText().toString().isEmpty()||mRoll.getText().toString().length()==0){
            Toast.makeText(getApplicationContext(), "Enter the roll no", Toast.LENGTH_SHORT).show();
            return false;
        }if(mAddres.getText().toString().isEmpty()||mAddres.getText().toString().length()==0){
            Toast.makeText(getApplicationContext(), "Enter the address", Toast.LENGTH_SHORT).show();
            return false;
        }if(mPhone.getText().toString().isEmpty()||mPhone.getText().toString().length()==0){
            Toast.makeText(getApplicationContext(), "Enter the phone no", Toast.LENGTH_SHORT).show();
            return false;
        }if(mClassname.getText().toString().isEmpty()||mClassname.getText().toString().length()==0){
            Toast.makeText(getApplicationContext(), "Enter the class name", Toast.LENGTH_SHORT).show();
            return false;
        }if(mNfcId.getText().toString().equalsIgnoreCase(getResources().getString(R.string.entitynfcid))){
            Toast.makeText(getApplicationContext(), "Scan the nfc tag", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

}
