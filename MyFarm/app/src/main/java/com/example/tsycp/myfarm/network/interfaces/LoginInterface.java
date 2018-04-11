package com.example.tsycp.myfarm.network.interfaces;

import com.example.tsycp.myfarm.model.User;
import com.example.tsycp.myfarm.network.config.Config;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface LoginInterface {

    @FormUrlEncoded
    @POST(Config.API_LOGIN)
    Call<User> login(
            @Field("username") String email,
            @Field("password") String password);

}
