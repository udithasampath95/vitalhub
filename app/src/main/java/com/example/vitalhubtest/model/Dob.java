package com.example.vitalhubtest.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Dob implements Serializable {
    @SerializedName("date")
    private String date;
    @SerializedName("age")
    private int age;

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
