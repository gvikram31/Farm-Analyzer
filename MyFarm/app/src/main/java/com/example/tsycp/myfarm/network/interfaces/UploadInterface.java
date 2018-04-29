package com.example.tsycp.myfarm.network.interfaces;


import com.example.tsycp.myfarm.model.BaseResponse;
import com.example.tsycp.myfarm.network.config.Config;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.PUT;
import retrofit2.http.Part;

public interface UploadInterface {

    @Multipart
    @PUT(Config.API_UPLOAD)
    Call<BaseResponse> upload(
            @Part MultipartBody.Part image,
            @Part("latitude") RequestBody latitude,
            @Part("longitude") RequestBody longitude);

}
