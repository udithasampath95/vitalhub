package com.example.vitalhubtest.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class UserResponse implements Serializable {
    @SerializedName("results")
    ArrayList<Results> userList;

    public ArrayList<Results> getUserList() {
        return userList;
    }

    public void setUserList(ArrayList<Results> userList) {
        this.userList = userList;
    }
}
