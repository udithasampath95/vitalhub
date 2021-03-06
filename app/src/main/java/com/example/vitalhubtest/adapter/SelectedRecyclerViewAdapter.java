package com.example.vitalhubtest.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.vitalhubtest.R;
import com.example.vitalhubtest.model.Results;
import com.example.vitalhubtest.utill.ApplicationSharedPreferences;
import com.example.vitalhubtest.utill.ViewDetailsCallBack;

import java.util.ArrayList;
import java.util.Locale;

public class SelectedRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    Context context;
    ArrayList<Results> arryList;
    ArrayList<Results> userList;
    ViewDetailsCallBack viewDetailsCallBack;

    public SelectedRecyclerViewAdapter(ArrayList<Results> userList, Context context) {
        this.context = context;
        this.userList=userList;
        arryList = new ArrayList<>();
        arryList.addAll(userList);
        this.viewDetailsCallBack = (ViewDetailsCallBack) context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.content_candidate, viewGroup, false);
        return new TextViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Results result=  userList.get(position);
        final int sdk = android.os.Build.VERSION.SDK_INT;
        ApplicationSharedPreferences sp=new ApplicationSharedPreferences(context);
        if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            ((TextViewHolder) holder).mainLinearLayout.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.selected_candidate_bg));
        } else {
            ((TextViewHolder) holder).mainLinearLayout.setBackground(ContextCompat.getDrawable(context, R.drawable.selected_candidate_bg));
        }
        ((TextViewHolder) holder).nameTV.setText(result.getName().getFirst()+" "+result.getName().getLast());
        ((TextViewHolder) holder).ageTV.setText(String.valueOf(result.getDateOfBirth().getAge())+" years");
        Glide.with(context)
                .load(result.getPicture().getLarge())
                .placeholder(R.drawable.place_holder)
                .apply(RequestOptions.centerInsideTransform())
                .skipMemoryCache(true)
                .into(((TextViewHolder) holder).imageView);
        ((TextViewHolder) holder).mainLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewDetailsCallBack.viewDetails(result);
            }
        });

        ((TextViewHolder) holder).bind(result, viewDetailsCallBack);


    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class TextViewHolder extends RecyclerView.ViewHolder{
        View view;
        TextView nameTV;
        TextView ageTV;
        ImageView imageView;
        LinearLayout mainLinearLayout;

        TextViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            nameTV = view.findViewById(R.id.nameTV);
            ageTV = view.findViewById(R.id.ageTV);
            imageView = view.findViewById(R.id.imageView);
            mainLinearLayout = view.findViewById(R.id.mainLinearLayout);
        }
        public void bind(final Results results, final ViewDetailsCallBack viewDetailsCallBack) {
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    viewDetailsCallBack.viewDetails(results);
                }
            });
        }
    }
    //return the filter class object
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        userList.clear();
        if (charText.length() == 0||charText.length() == 1||charText.length() == 2
        ||charText.length() == 3) {
            userList.addAll(arryList);
        } else {
            for (Results response : arryList) {
                if (response.getName().getFirst().toLowerCase(Locale.getDefault()).contains(charText)) {
                    userList.add(response);
                } else if (response.getName().getFirst().toLowerCase(Locale.getDefault()).contains(charText)) {
                    userList.add(response);
                }
            }
        }
        SelectedRecyclerViewAdapter.this.notifyDataSetChanged();
    }

}
