package com.example.tsycp.myfarm.network.interfaces;


import com.example.tsycp.myfarm.model.BaseResponse;
import com.example.tsycp.myfarm.network.config.Config;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface RegisterInterface {

    @FormUrlEncoded
    @POST(Config.API_REGISTER)
    Call<BaseResponse> register(
            @Field("firstname") String firstname,
            @Field("lastname") String lastname,
            @Field("username") String email,
            @Field("password") String password);

}
