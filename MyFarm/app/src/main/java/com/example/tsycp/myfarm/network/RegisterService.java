package com.example.tsycp.myfarm.network;

import android.content.Context;


import com.example.tsycp.myfarm.network.config.RetrofitBuilder;
import com.example.tsycp.myfarm.network.interfaces.RegisterInterface;

import retrofit2.Callback;

public class RegisterService {

    private RegisterInterface registerInterface;

    public RegisterService(Context context) {
        registerInterface = RetrofitBuilder.builder(context)
                .create(RegisterInterface.class);
    }

    public void doRegister(String firstname, String lastname, String username, String password, Callback callback) {
        registerInterface.register(firstname, lastname, username, password).enqueue(callback);
    }

}
