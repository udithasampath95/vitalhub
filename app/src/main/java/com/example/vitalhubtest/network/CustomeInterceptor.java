package com.example.vitalhubtest.network;

import android.util.Log;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

public class CustomeInterceptor implements Interceptor {
    private static final String TAG="CustomeInterceptor ";
    @Override
    public Response intercept(Chain chain) throws IOException {
        Log.i(TAG,"CALL_INTERCEPTOR");
        Response response=chain.proceed(chain.request()) ;
        System.out.println(response.code());
        Log.i(TAG,"RESPONSE_CODE_"+response.code());
        return response;
    }
}
