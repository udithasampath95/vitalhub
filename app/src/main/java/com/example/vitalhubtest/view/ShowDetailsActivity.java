package com.example.vitalhubtest.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.vitalhubtest.R;
import com.example.vitalhubtest.adapter.SelectedRecyclerViewAdapter;
import com.example.vitalhubtest.model.Results;
import com.example.vitalhubtest.utill.ApplicationSharedPreferences;
import com.example.vitalhubtest.utill.ViewDetailsCallBack;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ShowDetailsActivity extends AppCompatActivity implements ViewDetailsCallBack {
    private static final String TAG = "ShowDetailsActivity ";
    private CircleImageView circleImageView;
    private TextView firstName, address, tp, dob, email;
    private Results results;
    private Toolbar toolbar;
    private ImageButton selectedUserButton, nonSelectedUserButton;
    AlertDialog alertDialog;
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

    public void showAlertDialogBox(String title,String message,String type){
        alertDialog = new AlertDialog.Builder(ShowDetailsActivity.this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int item) {
                        if(type.equalsIgnoreCase("select")){
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
                            dialogInterface.dismiss();
                        }else{
                            ArrayList<String> emailList = new ArrayList<>();
                            ArrayList<Results> resultsArrayList = new ArrayList<>();

                            ApplicationSharedPreferences applicationSharedPreferences = new ApplicationSharedPreferences(ShowDetailsActivity.this);
                            Iterator<Map.Entry<String, Results>> iterator = applicationSharedPreferences.getSelectedUserList().entrySet().iterator();
                            while (iterator.hasNext()) {
                                Map.Entry<String, Results> entry = iterator.next();
//                    System.out.println("=====Iterate Value Start====>>>>>>");
//                    System.out.println(results.getEmail());
//                    System.out.println(entry.getKey());
//                    System.out.println("<<<<<<<=====Iterate Value End======");
                                if (entry.getKey().toString().trim().equals(results.getEmail().toString().trim())) {
                                    //                        applicationSharedPreferences.getSelectedUserList().remove(entry.getKey().toString().trim());
//                        iterator.remove();
//                        String value = entry.getKey();
//                        applicationSharedPreferences.getSelectedUserList().remove(value);
//                        System.out.println("List Size after removing " + applicationSharedPreferences.getSelectedUserList().size());
//                        nonSelectedUserButton.setVisibility(View.VISIBLE);
//                        selectedUserButton.setVisibility(View.GONE);
//                        return;
                                } else {
                                    emailList.add(entry.getKey());
                                    resultsArrayList.add(entry.getValue());
//                        nonSelectedUserButton.setVisibility(View.VISIBLE);
//                        selectedUserButton.setVisibility(View.GONE);
                                }
                            }

                            if (emailList.size() == resultsArrayList.size()) {
                                applicationSharedPreferences.getSelectedUserList().clear();
                                HashMap<String, Results> hashMap = new HashMap<>();
                                for (int i = 0; i < emailList.size(); i++) {
                                    hashMap.put(emailList.get(i), resultsArrayList.get(i));
                                }
                                applicationSharedPreferences.setSelectedUserList(hashMap);
                                nonSelectedUserButton.setVisibility(View.VISIBLE);
                                selectedUserButton.setVisibility(View.GONE);
//                    System.out.println("List Size after removing " + applicationSharedPreferences.getSelectedUserList().size());

                            } else {
                                nonSelectedUserButton.setVisibility(View.GONE);
                                selectedUserButton.setVisibility(View.VISIBLE);
                            }
                            dialogInterface.dismiss();
                        }

                    }
                })

                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .show();

    }

    private void setListners() {
        Log.i(TAG, "CALL_LISTNERS");
        nonSelectedUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "CLICK_NON_SELECTED_BUTTON");
                String title="Are you sure to select ";
                String msg="You can add "+ results.getName().getFirst()+" "+results.getName().getLast() +" to selected list";
                showAlertDialogBox(title,msg,"select");
            }

        });

        selectedUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "CLICK_SELECTED_BUTTON");
                String title="Are you sure to deselect ";
                String msg="You can remove "+ results.getName().getFirst()+" "+results.getName().getLast() +" from selected list";
                showAlertDialogBox(title,msg,"deselect");
            }
        });

    }

    private void setData() {
        Log.i(TAG, "CALL_SET_DATA");
        ApplicationSharedPreferences applicationSharedPreferences = new ApplicationSharedPreferences(ShowDetailsActivity.this);
        Glide.with(this)
                .load(results.getPicture().getLarge())
                .placeholder(R.drawable.place_holder)
                .apply(RequestOptions.centerInsideTransform())
                .skipMemoryCache(true)
                .into(circleImageView);
        firstName.setText(results.getName().getFirst() + " " + results.getName().getLast());
        address.setText(results.getLocation().getCity() + " , " + results.getLocation().getState());
        tp.setText(results.getPhone());

        dob.setText(results.getDateOfBirth().getDate());

        SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        SimpleDateFormat output = new SimpleDateFormat("MMM d, y",Locale.ENGLISH);
        Date d = null;
        try
        {
            d = input.parse(results.getDateOfBirth().getDate());
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }

        String formattedDate = output.format(d);
        dob.setText(formattedDate);

        email.setText(results.getEmail());
        if (applicationSharedPreferences.getSelectedUserList() == null) {
            selectedUserButton.setVisibility(View.GONE);
            nonSelectedUserButton.setVisibility(View.VISIBLE);
        } else {
            if(applicationSharedPreferences.getSelectedUserList().size()<=0){
                nonSelectedUserButton.setVisibility(View.VISIBLE);
                selectedUserButton.setVisibility(View.GONE);
            }else {
                Iterator<Map.Entry<String, Results>> iterator = applicationSharedPreferences.getSelectedUserList().entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<String, Results> entry = iterator.next();
//                    System.out.println("=====Iterate Value Start====>>>>>>");
//                    System.out.println(results.getEmail());
//                    System.out.println(entry.getKey());
//                    System.out.println("<<<<<<<=====Iterate Value End======");
                    if (results.getEmail().trim().equalsIgnoreCase(entry.getKey())) {
                        nonSelectedUserButton.setVisibility(View.GONE);
                        selectedUserButton.setVisibility(View.VISIBLE);
                        return;
                    } else {
                        nonSelectedUserButton.setVisibility(View.VISIBLE);
                        selectedUserButton.setVisibility(View.GONE);
                    }
                }
            }
        }
    }

    private void getExtras() {
        Log.i(TAG, "CALL_GET_EXTRAS");
        results = (Results) getIntent().getExtras().getSerializable("results");
    }

    private void initViews() {
        Log.i(TAG, "CALL_INIT_VIEWS");
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
                ApplicationSharedPreferences applicationSharedPreferences = new ApplicationSharedPreferences(ShowDetailsActivity.this);
                ArrayList<Results> resultsArrayList = new ArrayList<>();
                if (applicationSharedPreferences.getSelectedUserList() != null) {
                    for (Map.Entry<String, Results> set : applicationSharedPreferences.getSelectedUserList().entrySet()) {
                        resultsArrayList.add(set.getValue());
                    }
                    SelectedRecyclerViewAdapter selectedRecyclerViewAdapter = new SelectedRecyclerViewAdapter(resultsArrayList, this);
                    selectedRecyclerViewAdapter.notifyDataSetChanged();
                }
                finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void viewDetails(Results result) {
        results = new Results();
        results = result;
        setData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "CALL_ON_DESTROY");
        if (alertDialog != null) {
            alertDialog.dismiss();
        }
    }

}