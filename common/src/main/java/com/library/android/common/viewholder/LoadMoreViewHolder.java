package com.library.android.common.viewholder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.library.android.common.databinding.LayoutProgressBinding;

public class LoadMoreViewHolder extends RecyclerView.ViewHolder {

    public LayoutProgressBinding mBinding;

    public LoadMoreViewHolder(@NonNull View itemView, LayoutProgressBinding mBinding) {
        super(itemView);
        this.mBinding = mBinding;
    }
}
