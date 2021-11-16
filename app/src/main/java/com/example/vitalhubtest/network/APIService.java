package com.example.vitalhubtest.network;

import com.example.vitalhubtest.model.UserResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface APIService {
    @GET(".")
    Call<UserResponse> getUserList(@Query("results") int val);
}
