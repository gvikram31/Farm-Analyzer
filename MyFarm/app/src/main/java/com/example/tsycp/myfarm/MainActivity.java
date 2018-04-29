package com.example.tsycp.myfarm;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import com.google.android.gms.location.LocationServices;
import java.text.SimpleDateFormat;
import android.net.Uri;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tsycp.myfarm.model.BaseResponse;
import com.example.tsycp.myfarm.model.User;
import com.example.tsycp.myfarm.network.UploadService;
import com.example.tsycp.myfarm.util.PrefUtil;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private TextView greeting;
    private TextView username;
    private Button btnLogout;
    private Button btnUpload;
    private static final int MY_PERMISSION_ACCESS_COARSE_LOCATION = 11;

    private UploadService uploadService;
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
        btnUpload = (Button) findViewById(R.id.btn_upload);
        btnLogout = (Button) findViewById(R.id.btn_logout);

        User user = PrefUtil.getUser(this, PrefUtil.USER_SESSION);

        greeting.setText(getResources().getString(R.string.greeting, user.getData().getFirstname()));
        username.setText(user.getData().getUsername());

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

    void selectImage(){
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        startActivityForResult(intent, 1);
    }

        /*******************************
        Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(1000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                TextView tdate = (TextView) findViewById(R.id.date);
                                long date = System.currentTimeMillis();
                                SimpleDateFormat sdf = new SimpleDateFormat("MMM dd yyyy\nhh-mm-ss a");
                                String dateString = sdf.format(date);
                                tdate.setText(dateString);
                            }
                        });
                    }
                } catch (InterruptedException e) {
                }
            }
        };
        t.start();

    }****/


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
                Log.v("Image Information",filePath.toString());
                image = new File(filePath);
                Log.v("Image Information",image.toString());
                //LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
                //Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                double longitude = 1;//location.getLongitude();
                double latitude = 1;//location.getLatitude();
                Log.v("Image Information","Running uploadImage");
                uploadImage(uri,image, Double.toString(latitude),Double.toString(longitude));
        }
    }


    private void uploadImage(Uri uri, File image, String lat, String lon){
        RequestBody requestFile  = RequestBody.create(MediaType.parse(getContentResolver().getType(uri)), image);
        Log.v("Image Information",requestFile.toString());
        MultipartBody.Part body = MultipartBody.Part.createFormData("image", image.getName(), requestFile );
        RequestBody latitude = RequestBody.create(MediaType.parse("multipart/form-data"), lat);
        RequestBody longitude = RequestBody.create(MediaType.parse("multipart/form-data"), lon);
        uploadService = new UploadService(this);
        uploadService.doUpload(body, latitude,longitude,new retrofit2.Callback(){
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
