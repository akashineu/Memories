package com.akash.memories.util;

import android.app.Application;

public class UserApi {
    private String userName;
    private String userId;

    public static UserApi instance;

    public static UserApi getInstance(){
        if(instance == null){
            instance = new UserApi();
        }
        return instance;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
