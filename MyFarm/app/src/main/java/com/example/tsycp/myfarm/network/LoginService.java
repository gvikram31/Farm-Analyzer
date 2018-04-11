package com.example.tsycp.myfarm.network;

import android.content.Context;

import com.example.tsycp.myfarm.network.config.RetrofitBuilder;
import com.example.tsycp.myfarm.network.interfaces.LoginInterface;

import retrofit2.Callback;

public class LoginService {

    private LoginInterface loginInterface;

    public LoginService(Context context) {
        loginInterface = RetrofitBuilder.builder(context)
                .create(LoginInterface.class);
    }

    public void doLogin(String email, String password, Callback callback) {
        loginInterface.login(email, password).enqueue(callback);
    }

}
