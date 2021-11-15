package com.example.vitalhubtest.utill;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.vitalhubtest.model.Results;
import com.example.vitalhubtest.model.UserResponse;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

public class ApplicationSharedPreferences {
    Context context;
    SharedPreferences sharedPreferences;
    private ArrayList<Results> userResponses;
    private HashMap<String,Results> selectedUserMap;
    private String email;

    public ApplicationSharedPreferences(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences("vitalhub_app", context.MODE_PRIVATE);
    }
    public void removeUser() {
        sharedPreferences.edit().clear().commit();
    }
    public String getEmail() {
        email = sharedPreferences.getString("email", "");
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
        sharedPreferences.edit().putString("email", email).commit();
    }

    public void setUserList(ArrayList<Results> userResponses) {
        this.userResponses = userResponses;
        Gson gson = new Gson();
        String json = gson.toJson(userResponses);
        sharedPreferences.edit().putString("userResponse", json).commit();
    }

    public ArrayList<Results> getUserList() {
        Gson gson = new Gson();
        String json = sharedPreferences.getString("userResponses", null);
        Type type = new TypeToken<ArrayList<Results>>() {
        }.getType();
        return gson.fromJson(json, type);
    }


    public void setSelectedUserList(HashMap<String,Results> selectedUserMap) {
        this.selectedUserMap = selectedUserMap;
        Gson gson = new Gson();
        String json = gson.toJson(selectedUserMap);
        sharedPreferences.edit().putString("selectedUserMap", json).commit();
    }

    public HashMap<String,Results> getSelectedUserList() {
        Gson gson = new Gson();
        String json = sharedPreferences.getString("selectedUserMap", null);
        Type type = new TypeToken<HashMap<String,Results>>() {
        }.getType();
        return gson.fromJson(json, type);
    }

}
