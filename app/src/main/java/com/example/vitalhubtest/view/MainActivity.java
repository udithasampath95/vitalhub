package com.example.vitalhubtest.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vitalhubtest.fragment.CandidateFragment;
import com.example.vitalhubtest.R;
import com.example.vitalhubtest.fragment.SelectedFragment;
import com.example.vitalhubtest.adapter.SectionPageAdapter;
import com.example.vitalhubtest.model.Results;
import com.example.vitalhubtest.model.UserResponse;
import com.example.vitalhubtest.network.APIService;
import com.example.vitalhubtest.network.RetrofitInstance;
import com.example.vitalhubtest.utill.ApplicationSharedPreferences;
import com.example.vitalhubtest.utill.ViewDetailsCallBack;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements ViewDetailsCallBack {
    private static final String TAG = "MainActivity ";
    private Toolbar toolbar;
    private TextView titleName;
    private TabLayout tabs;
    private ViewPager container;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        checkInternetPermission();
    }

    @Override
    protected void onStart() {
        super.onStart();
        System.out.println("DDDD");

    }


    private void checkInternetPermission() {
        Log.i(TAG, "CALL_CHECK_INTERNET_PERMISSION");
        ApplicationSharedPreferences applicationSharedPreferences = new ApplicationSharedPreferences(MainActivity.this);
        if (isOnline()) {
            Log.i(TAG, "ONLINE");
            if (applicationSharedPreferences.getUserList() == null) {
                getUserResult();
            } else {
                Log.i(TAG, "GET_DATA_FROM_SHARED");
                setUpViewPager(applicationSharedPreferences.getUserList());
            }
        } else {
            Log.i(TAG, "OFFLINE");
            if (applicationSharedPreferences.getUserList() == null) {
                Toast.makeText(this, "Device is offline!", Toast.LENGTH_LONG).show();
            } else {
                Log.i(TAG, "GET_DATA_FROM_SHARED");
                setUpViewPager(applicationSharedPreferences.getUserList());
            }

        }
    }

    private void getUserResult() {
        Log.i(TAG, "CALL_GET_USER_RESULT");
        showProgress();
        APIService getNoticeDataService = RetrofitInstance.getRetroClient().create(APIService.class);
        Call<UserResponse> getUsers = getNoticeDataService.getUserList(50);
        getUsers.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                dismissProgress();
                if (response != null) {
                    if (response.body() == null) {
                        Log.i(TAG, "CALL_GET_USER_RESULT_ON_RESPONSE_BODY_NULL");

                        Toast.makeText(MainActivity.this, "Server temporary down!", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.i(TAG, "CALL_GET_USER_RESULT_ON_RESPONSE_" + response.code());
                        if (response.code() == 200) {
                            Log.i(TAG, "CALL_GET_USER_RESULT_ON_RESPONSE_VALUE" + response.body().toString());
                            UserResponse rootResponse = response.body();
                            ApplicationSharedPreferences sharedPreferences = new ApplicationSharedPreferences(MainActivity.this);
                            sharedPreferences.setUserList(rootResponse.getUserList());
                            setUpViewPager(rootResponse.getUserList());
                        } else {
                            Log.i(TAG, "CALL_GET_USER_RESULT_ON_RESPONSE_BAD_REQUEST_" + response.code());
                            Toast.makeText(MainActivity.this, response.message() + " " + response.code(), Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Log.i(TAG, "CALL_GET_USER_RESULT_ON_RESPONSE_INVALID_REQUEST");
                    Toast.makeText(MainActivity.this, "Invalid request", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                dismissProgress();
                Log.i(TAG, "CALL_GET_USER_RESULT_FAILURE_" + t.getMessage());
                Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void setUpViewPager(ArrayList<Results> rootResponse) {
        ApplicationSharedPreferences sp=new ApplicationSharedPreferences(MainActivity.this);
        SectionPageAdapter sectionPageAdapter = new SectionPageAdapter(getSupportFragmentManager());
        //set Argument here
        Bundle bundle = new Bundle();
        bundle.putSerializable("candidates", rootResponse);
        sectionPageAdapter.addFragment(new CandidateFragment(), "Candidates", bundle);
        //set Argument here
        Bundle bundle1 = new Bundle();
        bundle1.putSerializable("selected",sp.getSelectedUserList());
        sectionPageAdapter.addFragment(new SelectedFragment(), "Selected", bundle1);

        tabs.setSelectedTabIndicatorColor(Color.rgb(36, 87, 155));
        tabs.setVisibility(View.VISIBLE);
        container.setAdapter(sectionPageAdapter);
    }

    protected boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            return true;
        } else {
            return false;
        }
    }


    private void initViews() {
        Log.i(TAG, "CALL_INIT_VIEWS");
        toolbar = findViewById(R.id.toolbar);
        titleName = findViewById(R.id.titleName);
        tabs = findViewById(R.id.tabs);
        container = findViewById(R.id.container);
        tabs.setupWithViewPager(container);
    }

    private void showProgress() {
        Log.i(TAG, "CALL_SHOW_PROGRESS");
        progressDialog = new ProgressDialog(this, R.style.CustomDialogTheme);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMax(100);
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(true);
        progressDialog.show();
    }

    private void dismissProgress() {
        Log.i(TAG, "CALL_DISMISS_PROGRESS");
        progressDialog.dismiss();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "CALL_ON_DESTROY");
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void viewDetails(Results results) {
        Log.i(TAG, "CALL_VIEW_DETAILS");
        Intent i = new Intent(MainActivity.this, ShowDetailsActivity.class);
        i.putExtra("results", results);
        startActivity(i);
    }
}