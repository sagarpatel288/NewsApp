package com.example.android.newsapp.ui.newslist.viewholder;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.android.newsapp.databinding.ItemNewsListBinding;
import com.library.android.common.listeners.Callbacks;

public class NewsListViewHolder extends RecyclerView.ViewHolder {

    public ItemNewsListBinding mBinding;

    public NewsListViewHolder(@NonNull View itemView, ItemNewsListBinding mBinding, final Callbacks.OnEventCallback onEventCallback) {
        super(itemView);
        this.mBinding = mBinding;
        Context context = itemView.getContext();
        if (context != null && mBinding != null) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onEventCallback != null) {
                        onEventCallback.onEventClick(v, (Integer) v.getTag());
                    }
                }
            });
        }
    }
}
