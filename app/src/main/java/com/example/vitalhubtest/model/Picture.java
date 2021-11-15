package com.example.vitalhubtest.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Picture implements Serializable {
    @SerializedName("large")
    private String large;


    public String getLarge() {
        return large;
    }

    public void setLarge(String large) {
        this.large = large;
    }

}
