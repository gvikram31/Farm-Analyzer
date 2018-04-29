package com.example.tsycp.myfarm.network;

import android.content.Context;

import com.example.tsycp.myfarm.network.config.RetrofitBuilder;
import com.example.tsycp.myfarm.network.interfaces.LoginInterface;
import com.example.tsycp.myfarm.network.interfaces.UploadInterface;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Callback;

public class UploadService {

    private UploadInterface uploadInterface;

    public UploadService(Context context) {
        uploadInterface = RetrofitBuilder.builder(context)
                .create(UploadInterface.class);
    }

    public void doUpload(MultipartBody.Part image, RequestBody latitude, RequestBody longitude, Callback callback) {
        uploadInterface.upload(image, latitude, longitude).enqueue(callback);
    }

}
