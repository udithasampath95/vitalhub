package com.example.vitalhubtest.fragment;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vitalhubtest.R;
import com.example.vitalhubtest.adapter.SelectedRecyclerViewAdapter;
import com.example.vitalhubtest.model.Results;
import com.example.vitalhubtest.utill.ApplicationSharedPreferences;
import com.google.gson.internal.bind.util.ISO8601Utils;

import org.w3c.dom.ls.LSOutput;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SelectedFragment extends Fragment implements SearchView.OnQueryTextListener, SwipeRefreshLayout.OnRefreshListener {
    private SearchView nameSearchView;
    private RecyclerView listView;
    private LinearLayout notFoundLinearLayout;
    private TextView notFoundTextView;
    private SelectedRecyclerViewAdapter candidatesRecycleViewAdapter;
    private HashMap<String, Results> mapUser;
    private ArrayList<Results> rootResponse;
    private SwipeRefreshLayout swipe;
    private ItemTouchHelper mItemTouchHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_selected, container, false);
        initViews(view);
        rootResponse = new ArrayList<>();
        mapUser = new HashMap<>();
        if (getArguments().getSerializable("selected") != null) {
            mapUser = (HashMap<String, Results>) getArguments().getSerializable("selected");
            if (mapUser != null) {
                if (mapUser.size() <= 0) {
                    listView.setVisibility(View.GONE);
                    notFoundLinearLayout.setVisibility(View.VISIBLE);
                } else {
                    for (Map.Entry<String, Results> set : mapUser.entrySet()) {
                        rootResponse.add(set.getValue());
                    }
                }
            } else {
                listView.setVisibility(View.GONE);
                notFoundLinearLayout.setVisibility(View.VISIBLE);
            }
        } else {
            listView.setVisibility(View.GONE);
            notFoundLinearLayout.setVisibility(View.VISIBLE);
        }
        swipe.setOnRefreshListener(this);
        return view;
    }

    private void initRecyclerView() {
        System.out.println("Caca");
        candidatesRecycleViewAdapter = new SelectedRecyclerViewAdapter(rootResponse, getContext());
        listView.setAdapter(candidatesRecycleViewAdapter);
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());

        // setting layout manager for our recycler view.
        listView.setLayoutManager(manager);
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            private Drawable deleteIcon = ContextCompat.getDrawable(getActivity(), android.R.drawable.ic_delete);
            private final ColorDrawable background = new ColorDrawable(Color.RED);
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAbsoluteAdapterPosition();
                Toast.makeText(getActivity(), "A", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                View itemView = viewHolder.itemView;

                int iconMargin = (itemView.getHeight() - deleteIcon.getIntrinsicHeight()) / 2;
                int iconTop = itemView.getTop() + (itemView.getHeight() - deleteIcon.getIntrinsicHeight()) / 2;
                int iconBottom = iconTop + deleteIcon.getIntrinsicHeight();

                if (dX > 0) {
                    int iconLeft = itemView.getLeft() + iconMargin + deleteIcon.getIntrinsicWidth();
                    int iconRight = itemView.getLeft() + iconMargin;

                    deleteIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
                    background.setBounds(itemView.getLeft(), itemView.getTop(), itemView.getLeft() + ((int) dX), itemView.getBottom());
                } else if (dX < 0) {
                    int iconLeft = itemView.getRight() - iconMargin - deleteIcon.getIntrinsicWidth();
                    int iconRight = itemView.getRight() - iconMargin;

                    deleteIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
                    background.setBounds(itemView.getRight() + ((int) dX), itemView.getTop(), itemView.getRight(), itemView.getBottom());
                } else {
                    background.setBounds(0, 0, 0, 0);
                }

                background.draw(c);
                deleteIcon.draw(c);
            }
        }).attachToRecyclerView(listView);

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
        swipe = view.findViewById(R.id.swipe);
        int colorFirst = Color.rgb(111, 161, 230);
        int colorSecond = Color.rgb(255, 193, 7);
        int colorThird = Color.rgb(9, 137, 223);
        int colorFourth = Color.rgb(248, 176, 80);
        swipe.setColorSchemeColors(colorFirst, colorSecond, colorThird, colorFourth);
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
            Log.i("SelectedFragment ", "Filter null");
        } else {
            candidatesRecycleViewAdapter.filter(newText);
        }
        return false;
    }

    @Override
    public void onRefresh() {
        ApplicationSharedPreferences sp = new ApplicationSharedPreferences(getContext());
        rootResponse = new ArrayList<>();
        mapUser = new HashMap<>();
        swipe.setRefreshing(false);
        if (sp.getSelectedUserList() != null) {
            mapUser = sp.getSelectedUserList();
            if (mapUser != null) {
                if (mapUser.size() <= 0) {
                    listView.setVisibility(View.GONE);
                    notFoundLinearLayout.setVisibility(View.VISIBLE);
                } else {
                    listView.setVisibility(View.VISIBLE);
                    notFoundLinearLayout.setVisibility(View.GONE);
                    for (Map.Entry<String, Results> set : mapUser.entrySet()) {
                        rootResponse.add(set.getValue());
                    }
                    initRecyclerView();
                }
            } else {

                listView.setVisibility(View.GONE);
                notFoundLinearLayout.setVisibility(View.VISIBLE);
            }
        } else {

            swipe.setRefreshing(false);
            listView.setVisibility(View.GONE);
            notFoundLinearLayout.setVisibility(View.VISIBLE);
        }
    }

}