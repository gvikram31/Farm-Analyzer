package com.example.tsycp.myfarm.network.interfaces;


import com.example.tsycp.myfarm.model.BaseResponse;
import com.example.tsycp.myfarm.model.Image;
import com.example.tsycp.myfarm.network.config.Config;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface SearchInterface {

    @FormUrlEncoded
    @POST(Config.API_SEARCH)
    Call<Image> search(
            @Field("latitude") String latitude,
            @Field("longitude") String longitude,
            @Field("zone") String zone,
            @Field("timestamp") String timestamp);

}
