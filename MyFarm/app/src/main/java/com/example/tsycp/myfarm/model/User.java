package com.example.tsycp.myfarm.model;

public class User extends BaseResponse {

    private UserData data;

    public User() {
    }

    public UserData getData() {
        return data;
    }

    public void setData(UserData data) {
        this.data = data;
    }
}
