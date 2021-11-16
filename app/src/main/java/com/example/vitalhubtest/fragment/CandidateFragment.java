package com.example.vitalhubtest.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vitalhubtest.R;
import com.example.vitalhubtest.adapter.CandidatesRecycleViewAdapter;
import com.example.vitalhubtest.adapter.SelectedRecyclerViewAdapter;
import com.example.vitalhubtest.model.Results;
import com.example.vitalhubtest.model.UserResponse;
import com.example.vitalhubtest.network.APIService;
import com.example.vitalhubtest.network.RetrofitInstance;
import com.example.vitalhubtest.utill.ApplicationSharedPreferences;
import com.example.vitalhubtest.view.MainActivity;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CandidateFragment extends Fragment implements SearchView.OnQueryTextListener,CandidatesRecycleViewAdapter.OnLoadMoreListener{
    private static final String TAG = "CandidateFragment ";
    private SearchView nameSearchView;
    private RecyclerView listView;
    private LinearLayout notFoundLinearLayout;
    private TextView notFoundTextView;
    private CandidatesRecycleViewAdapter candidatesRecycleViewAdapter;
    private ArrayList<Results> rootResponse;
    CandidatesRecycleViewAdapter.OnLoadMoreListener onLoadMoreListener;
    private int pageCount=1;
    private ArrayList<Results> list;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.candidate_fragment_layout, container, false);
        initViews(view);
        rootResponse = new ArrayList<>();
        rootResponse = (ArrayList<Results>) getArguments().getSerializable("candidates");
        onLoadMoreListener = this;
        return view;
    }

    private void initRecyclerView() {
        candidatesRecycleViewAdapter = new CandidatesRecycleViewAdapter(onLoadMoreListener, rootResponse, getContext());
        listView.setAdapter(candidatesRecycleViewAdapter);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initRecyclerView();
        listView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager llManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (dy > 0 && llManager.findLastCompletelyVisibleItemPosition() == (candidatesRecycleViewAdapter.getItemCount() - 2)) {
                    candidatesRecycleViewAdapter.showLoading();
                }
            }
        });

    }

    private void initViews(View view) {
        nameSearchView = view.findViewById(R.id.nameSearchView);
        listView = view.findViewById(R.id.listView);
        notFoundLinearLayout = view.findViewById(R.id.notFoundLinearLayout);
        notFoundTextView = view.findViewById(R.id.notFoundTextView);
        nameSearchView.setOnQueryTextListener(this);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (candidatesRecycleViewAdapter == null) {
            Log.i("CandidateFragment ","Filter null")  ;
        } else {
            candidatesRecycleViewAdapter.filter(newText);
        }
        return false;
    }

    @Override
    public void onLoadMore() {
        Log.i(TAG, "CALL)LOAD_MORE");
        getUserResult();
    }

     private void getUserResult() {
         Log.i(TAG, "CALL_GET_USER_RESULT");
         pageCount = pageCount + 1;
         list = new ArrayList<>();
        APIService getNoticeDataService = RetrofitInstance.getRetroClient().create(APIService.class);
        Call<UserResponse> getUsers = getNoticeDataService.getUserListPagination(pageCount,10,"abc");
        getUsers.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {

                if (response != null) {
                    if (response.body() == null) {
                        Log.i(TAG, "CALL_GET_USER_RESULT_ON_RESPONSE_BODY_NULL");

                        Toast.makeText(getContext(), "Server temporary down!", Toast.LENGTH_SHORT).show();
                    } else {
                        list = new ArrayList<>();
                        Log.i(TAG, "CALL_GET_USER_RESULT_ON_RESPONSE_" + response.code());
                        if (response.code() == 200) {
                            Log.i(TAG, "CALL_GET_USER_RESULT_ON_RESPONSE_VALUE" + response.body().toString());
                            UserResponse rootResponses = response.body();
                            ApplicationSharedPreferences sharedPreferences = new ApplicationSharedPreferences(getContext());
                            ArrayList<Results> userResponsesTempArray=new ArrayList<>();
                            for(int i=0;i<rootResponses.getUserList().size();i++){
                                userResponsesTempArray.add(rootResponses.getUserList().get(i));
                            }
                            sharedPreferences.setUserList(userResponsesTempArray);
//                            setUpViewPager(rootResponses.getUserList());
                            list = new ArrayList<>();
                            if (userResponsesTempArray != null) {
                                for (int j = 0; j < userResponsesTempArray.size(); j++) {
                                    list.add(userResponsesTempArray.get(j));
                                }
                                candidatesRecycleViewAdapter.notifyItemInserted(rootResponse.size());
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        candidatesRecycleViewAdapter.dismissLoading();
                                        candidatesRecycleViewAdapter.addItemMore(list);
                                        candidatesRecycleViewAdapter.setMore(true);
                                    }
                                }, 1000);

                            } else {
                                Toast.makeText(getContext(), "Bad connection", Toast.LENGTH_SHORT).show();
                            }



                        } else {
                            Log.i(TAG, "CALL_GET_USER_RESULT_ON_RESPONSE_BAD_REQUEST_" + response.code());
                            Toast.makeText(getContext(), response.message() + " " + response.code(), Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Log.i(TAG, "CALL_GET_USER_RESULT_ON_RESPONSE_INVALID_REQUEST");
                    Toast.makeText(getContext(), "Invalid request", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                Log.i(TAG, "CALL_GET_USER_RESULT_FAILURE_" + t.getMessage());
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

}
