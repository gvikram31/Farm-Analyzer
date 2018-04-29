package com.example.tsycp.myfarm;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;

import com.example.tsycp.myfarm.model.Image;
import com.example.tsycp.myfarm.network.SearchService;
import com.google.android.gms.location.LocationServices;

import android.net.Uri;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tsycp.myfarm.model.BaseResponse;
import com.example.tsycp.myfarm.model.User;
import com.example.tsycp.myfarm.network.UploadService;
import com.example.tsycp.myfarm.util.PrefUtil;
import com.example.tsycp.myfarm.model.Image;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private TextView greeting;
    private TextView username;
    private EditText latitudeText;
    private EditText longitudeText;
    private EditText zoneText;
    private EditText timestampText;
    private Button btnLogout;
    private Button btnUpload;
    private Button btnSearch;
    private static final int MY_PERMISSION_ACCESS_COARSE_LOCATION = 11;

    private UploadService uploadService;
    private SearchService searchService;
    private File image;


    public static void start(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        greeting = (TextView) findViewById(R.id.greeting);
        username = (TextView) findViewById(R.id.username);
        latitudeText = (EditText) findViewById(R.id.latitude);
        longitudeText = (EditText) findViewById(R.id.longitude);
        zoneText = (EditText) findViewById(R.id.zone);
        timestampText = (EditText) findViewById(R.id.timestamp);
        btnUpload = (Button) findViewById(R.id.btn_upload);
        btnLogout = (Button) findViewById(R.id.btn_logout);
        btnSearch = (Button) findViewById(R.id.btn_search);


        User user = PrefUtil.getUser(this, PrefUtil.USER_SESSION);

        greeting.setText(getResources().getString(R.string.greeting, user.getData().getFirstname()));
        username.setText(user.getData().getUsername());


        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchImage();
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.v("Image Information","Click Upload! Running selectImage");
                if (ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            1);
                }
                else {
                    selectImage();
                }
            }
        });
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logoutAct();
                LoginActivity.start(MainActivity.this);
                MainActivity.this.finish();
            }
        });
    }

    void searchImage(){
        String latitude = latitudeText.getText().toString();
        String longitude = longitudeText.getText().toString();
        String zone = zoneText.getText().toString();
        String timestamp = timestampText.getText().toString();

        if(TextUtils.isEmpty(latitude) && TextUtils.isEmpty(longitude) && TextUtils.isEmpty(zone) && TextUtils.isEmpty(timestamp)){
            Toast.makeText(MainActivity.this, "You should set at least one filter!", Toast.LENGTH_SHORT).show();
        }

        searchService = new SearchService(this);
        searchService.doSearch(latitude, longitude, zone, timestamp, new Callback() {
            @Override
            public void onResponse(Call call, retrofit2.Response response) {
                Image tmp = (Image) response.body();


                if(tmp != null){
                    if(!tmp.isError()){
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setIcon(android.R.drawable.alert_dark_frame);
                        builder.setTitle("Result is " + tmp.getMessage());
                        builder.setMessage(tmp.getData().getImagename() + "\nLatitude is " + tmp.getData().getLatitude() + "\nLongitude is " + tmp.getData().getLongitude() + "\nZone is " + tmp.getData().getZone() + "\nTime is " + tmp.getData().getTimestamp());

                        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(MainActivity.this, "Test1", Toast.LENGTH_SHORT).show();
                            }
                        });

//                        builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                Toast.makeText(MainActivity.this, "Finish Searching", Toast.LENGTH_SHORT).show();
//                            }
//                        });

                        AlertDialog ad = builder.create();
                        ad.show();
                    }
                    else{
                        Toast.makeText(MainActivity.this, tmp.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Toast.makeText(MainActivity.this, "TMP is NULL!", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Toast.makeText(MainActivity.this, "An error occurred!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    void selectImage(){
        String latitude = latitudeText.getText().toString();
        String longitude = longitudeText.getText().toString();
        String zone = zoneText.getText().toString();
        String timestamp = timestampText.getText().toString();

        if(TextUtils.isEmpty(latitude)) {
            latitudeText.setError("latitude cannot be empty !");
            return;
        }
        if(TextUtils.isEmpty(longitude)) {
            longitudeText.setError("longitude cannot be empty !");
            return;
        }
        if(TextUtils.isEmpty(zone)) {
            zoneText.setError("zone cannot be empty !");
            return;
        }
        if(TextUtils.isEmpty(timestamp)) {
            timestampText.setError("timestamp cannot be empty !");
            return;
        }
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        startActivityForResult(intent, 1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {

        if (requestCode == 1)
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                selectImage();
            } else
            {
                // Permission Denied
                Toast.makeText(MainActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
                Uri uri = data.getData();
                String wholeID = DocumentsContract.getDocumentId(uri);
                String id = wholeID.split(":")[1];
                String[] column = { MediaStore.Images.Media.DATA };
                String sel = MediaStore.Images.Media._ID + "=?";
                Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, column,
                        sel, new String[] { id }, null);
                String filePath = "";
                int columnIndex = cursor.getColumnIndex(column[0]);
                if (cursor.moveToFirst()) {
                    filePath = cursor.getString(columnIndex);
                }
                cursor.close();
                image = new File(filePath);
                //LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
                //Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                String latitude = latitudeText.getText().toString();
                String longitude = longitudeText.getText().toString();
                String zone = zoneText.getText().toString();
                String timestamp = timestampText.getText().toString();
                Log.v("latitude Information",latitude);
                Log.v("longitude Information",longitude);
                Log.v("zone Information",zone);
                Log.v("timestamp Information",timestamp);
                uploadImage(uri,image, latitude,longitude, zone, timestamp);
        }
    }


    private void uploadImage(Uri uri, File image, String lat, String lon,String zo, String tim){
        Log.v("Content Type", getContentResolver().getType(uri));
        RequestBody requestFile  = RequestBody.create(MediaType.parse("multipart/form-data"), image);
        MultipartBody.Part body = MultipartBody.Part.createFormData("image", image.getName(), requestFile );
        RequestBody latitude = RequestBody.create(MediaType.parse("multipart/form-data"), lat);
        RequestBody longitude = RequestBody.create(MediaType.parse("multipart/form-data"), lon);
        RequestBody zone = RequestBody.create(MediaType.parse("multipart/form-data"), zo);
        RequestBody timestamp = RequestBody.create(MediaType.parse("multipart/form-data"), tim);
        uploadService = new UploadService(this);
        uploadService.doUpload(body,latitude,longitude,zone,timestamp,new retrofit2.Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                BaseResponse baseResponse = (BaseResponse) response.body();

                if(baseResponse != null) {
                    if(!baseResponse.isError()) {
                        Toast.makeText(MainActivity.this, baseResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    Toast.makeText(MainActivity.this, baseResponse.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call call, Throwable t) {
                Log.v("error Information",t.getMessage());
                Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
    void logoutAct() {
        PrefUtil.clear(this);
    }
}
