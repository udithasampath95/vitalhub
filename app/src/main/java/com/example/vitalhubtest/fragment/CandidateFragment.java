package com.example.vitalhubtest.fragment;

import android.content.Context;
import android.os.Bundle;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vitalhubtest.R;
import com.example.vitalhubtest.adapter.CandidatesRecycleViewAdapter;
import com.example.vitalhubtest.model.Results;
import com.example.vitalhubtest.model.UserResponse;

import java.util.ArrayList;

public class CandidateFragment extends Fragment implements SearchView.OnQueryTextListener{
    private SearchView nameSearchView;
    private RecyclerView listView;
    private LinearLayout notFoundLinearLayout;
    private TextView notFoundTextView;
    private CandidatesRecycleViewAdapter candidatesRecycleViewAdapter;
    private ArrayList<Results> rootResponse;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.candidate_fragment_layout, container, false);
        initViews(view);
        rootResponse = new ArrayList<>();
        rootResponse = (ArrayList<Results>) getArguments().getSerializable("candidates");
        return view;
    }

    private void initRecyclerView() {
        candidatesRecycleViewAdapter = new CandidatesRecycleViewAdapter( rootResponse, getContext());
        listView.setAdapter(candidatesRecycleViewAdapter);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initRecyclerView();
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
}
