package com.example.vitalhubtest.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.vitalhubtest.R;
import com.example.vitalhubtest.adapter.CandidatesRecycleViewAdapter;
import com.example.vitalhubtest.adapter.SelectedRecyclerViewAdapter;
import com.example.vitalhubtest.constants.Constants;
import com.example.vitalhubtest.model.Results;
import com.example.vitalhubtest.utill.ApplicationSharedPreferences;
import com.example.vitalhubtest.utill.ViewDetailsCallBack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;

public class ShowDetailsActivity extends AppCompatActivity implements ViewDetailsCallBack {
    private CircleImageView circleImageView;
    private TextView firstName, address, tp, dob, email;
    private Results results;
    private Toolbar toolbar;
    private ImageButton selectedUserButton, nonSelectedUserButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_details);
        initViews();
        getExtras();

        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        // add back arrow to toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        setListners();
        setData();
    }

    private void setListners() {
        nonSelectedUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ApplicationSharedPreferences applicationSharedPreferences = new ApplicationSharedPreferences(ShowDetailsActivity.this);
                HashMap<String, Results> hashMap = new HashMap<>();
                if (applicationSharedPreferences.getSelectedUserList() == null) {
                    hashMap.put(results.getEmail().toString().trim(), results);
                } else {
                    for (Map.Entry<String, Results> set : applicationSharedPreferences.getSelectedUserList().entrySet()) {
                        hashMap.put(set.getKey(), set.getValue());
                    }
                    hashMap.put(results.getEmail().toString().trim(), results);
                }

                applicationSharedPreferences.setSelectedUserList(hashMap);
                nonSelectedUserButton.setVisibility(View.GONE);
                selectedUserButton.setVisibility(View.VISIBLE);
            }

        });

        selectedUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ApplicationSharedPreferences applicationSharedPreferences = new ApplicationSharedPreferences(ShowDetailsActivity.this);
                Iterator<Map.Entry<String, Results>> iterator = applicationSharedPreferences.getSelectedUserList().entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<String, Results> entry = iterator.next();
                    System.out.println("=====Iterate Value====>>>>>>");
                    System.out.println(results.getEmail());
                    System.out.println(entry.getKey());
                    System.out.println("<<<<<<<=====Iterate Value======||");
                    if (results.getEmail().toString().trim().equalsIgnoreCase(entry.getKey().toString().trim())) {
                        System.out.println("call3333");
                        iterator.remove();
                        applicationSharedPreferences.getSelectedUserList().remove(entry.getKey());
                        nonSelectedUserButton.setVisibility(View.VISIBLE);
                        selectedUserButton.setVisibility(View.GONE);
                        return;
                    }else{
                        nonSelectedUserButton.setVisibility(View.GONE);
                        selectedUserButton.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

    }

    private void setData() {
        ApplicationSharedPreferences applicationSharedPreferences = new ApplicationSharedPreferences(ShowDetailsActivity.this);
        Glide.with(this)
                .load(results.getPicture().getLarge())
                .apply(RequestOptions.centerCropTransform())
                .into(circleImageView);
        firstName.setText(results.getName().getFirst() + " " + results.getName().getLast());
        address.setText(results.getLocation().getCity() + " , " + results.getLocation().getState());
        tp.setText(results.getPhone());
        dob.setText(results.getDateOfBirth().getDate());
        email.setText(results.getEmail());

        if (applicationSharedPreferences.getSelectedUserList() == null) {
            selectedUserButton.setVisibility(View.GONE);
            nonSelectedUserButton.setVisibility(View.VISIBLE);
        } else {
            Iterator<Map.Entry<String, Results>> iterator = applicationSharedPreferences.getSelectedUserList().entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, Results> entry = iterator.next();
                System.out.println("=====Iterate Value====>>>>>>");
                System.out.println(results.getEmail());
                System.out.println(entry.getKey());
                System.out.println("<<<<<<<=====Iterate Value======");
                if (results.getEmail().trim().equalsIgnoreCase( entry.getKey())) {
                    System.out.println("call true");
                    nonSelectedUserButton.setVisibility(View.GONE);
                    selectedUserButton.setVisibility(View.VISIBLE);
                    return;
                }else{
                    System.out.println("call false");
                    nonSelectedUserButton.setVisibility(View.VISIBLE);
                    selectedUserButton.setVisibility(View.GONE);
                }
            }
        }
    }

    private void getExtras() {
        results = (Results) getIntent().getExtras().getSerializable("results");
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        circleImageView = findViewById(R.id.circleImageView);
        firstName = findViewById(R.id.firstName);
        address = findViewById(R.id.address);
        tp = findViewById(R.id.tp);
        dob = findViewById(R.id.dob);
        email = findViewById(R.id.email);
        selectedUserButton = findViewById(R.id.selectedUserButton);
        nonSelectedUserButton = findViewById(R.id.nonSelectedUserButton);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                ApplicationSharedPreferences applicationSharedPreferences=new ApplicationSharedPreferences(ShowDetailsActivity.this);
                ArrayList<Results>resultsArrayList=new ArrayList<>();
                for (Map.Entry<String, Results> set : applicationSharedPreferences.getSelectedUserList().entrySet()) {
                    resultsArrayList.add(set.getValue());
                }
                SelectedRecyclerViewAdapter selectedRecyclerViewAdapter=new SelectedRecyclerViewAdapter(resultsArrayList, this);
                selectedRecyclerViewAdapter.notifyDataSetChanged();
                finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void viewDetails(Results result) {
        results=new Results();
        results=result;
        setData();
    }
}