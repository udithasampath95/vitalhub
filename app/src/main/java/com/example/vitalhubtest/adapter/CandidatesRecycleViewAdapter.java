package com.example.vitalhubtest.adapter;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.vitalhubtest.R;
import com.example.vitalhubtest.model.Results;
import com.example.vitalhubtest.utill.RecyclerViewItemEnabler;
import com.example.vitalhubtest.utill.ViewDetailsCallBack;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class CandidatesRecycleViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements RecyclerViewItemEnabler{
    Context context;
    ArrayList<Results> arryList;
    ArrayList<Results> userList;
    ViewDetailsCallBack viewDetailsCallBack;

    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;

    private OnLoadMoreListener onLoadMoreListener;
    private boolean isMoreLoading = true;
    private boolean mAllEnabled;

    public CandidatesRecycleViewAdapter(OnLoadMoreListener onLoadMoreListener,ArrayList<Results> userList, Context context) {
        this.context = context;
        this.userList = userList;
        this.onLoadMoreListener = onLoadMoreListener;
        arryList = new ArrayList<>();
        arryList.addAll(userList);
        this.viewDetailsCallBack = (ViewDetailsCallBack) context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        RecyclerView.ViewHolder vh;
        if (viewType == VIEW_ITEM) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.content_candidate, viewGroup, false);
            vh = new TextViewHolder(view);
        } else {
            View v = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.progress_item, viewGroup, false);
            vh = new ProgressViewHolder(v);
        }
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof TextViewHolder){
          final   Results result = userList.get(position);
            ((TextViewHolder) holder).nameTV.setText(result.getName().getFirst() + " " + result.getName().getLast());
            ((TextViewHolder) holder).ageTV.setText(String.valueOf(result.getDateOfBirth().getAge()) + " years");
            Glide.with(context)
                    .load(result.getPicture().getLarge())
                    .placeholder(R.drawable.place_holder)
                    .apply(RequestOptions.centerInsideTransform())
                    .skipMemoryCache(true)
                    .into(((TextViewHolder) holder).imageView)
            ;
            ((TextViewHolder) holder).mainLinearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewDetailsCallBack.viewDetails(result);
                }
            });
            ((TextViewHolder) holder).bind(result, viewDetailsCallBack);
        }else {
            ((ProgressViewHolder) holder).progressBar.setIndeterminate(true);
        }

    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class TextViewHolder extends RecyclerView.ViewHolder {
        View view;
        TextView nameTV;
        TextView ageTV;
        CircleImageView imageView;
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
        CandidatesRecycleViewAdapter.this.notifyDataSetChanged();
    }

    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public ProgressViewHolder(View v) {
            super(v);
            progressBar = v.findViewById(R.id.progressBar);
        }
    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }

    public void showLoading() {
        if (isMoreLoading && userList != null && onLoadMoreListener != null) {
            isMoreLoading = false;
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    userList.add(null);
                    notifyItemInserted(userList.size() - 1);
                    onLoadMoreListener.onLoadMore();
                }
            });
        }
    }

    public void setMore(boolean isMore) {
        this.isMoreLoading = isMore;
    }

    public void dismissLoading() {
        if (userList != null && userList.size() > 0) {
            userList.remove(userList.size() - 1);
            notifyItemRemoved(userList.size());
        }
    }

    public void addItemMore(List<Results> lst) {
        int sizeInit = userList.size();
        userList.addAll(lst);
        notifyItemRangeChanged(sizeInit, userList.size());
    }

    @Override
    public boolean getItemEnabled(int position) {
        return false;
    }

    @Override
    public void onViewAttachedToWindow(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        holder.itemView.setEnabled(isAllItemsEnabled());
    }
    public void setAllItemsEnabled(boolean enable) {
        mAllEnabled = enable;

        notifyItemRangeChanged(0, getItemCount());
    }
    @Override
    public int getItemViewType(int position) {
        return userList.get(position) != null ? VIEW_ITEM : VIEW_PROG;
    }

    @Override
    public boolean isAllItemsEnabled() {
        return mAllEnabled;
    }

}
