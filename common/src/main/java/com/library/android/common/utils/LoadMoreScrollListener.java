package com.library.android.common.utils;

import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

public abstract class LoadMoreScrollListener extends RecyclerView.OnScrollListener {

    private volatile boolean mEnabled = true;
    private int mPreLoadCount = 0;

    @Override
    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        if (mEnabled) {
            RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();

            if (!(manager instanceof LinearLayoutManager)) {
                throw new IllegalArgumentException("Expected recyclerview to have linear layout manager");
            }

            LinearLayoutManager mLayoutManager = (LinearLayoutManager) manager;

            int visibleItemCount = mLayoutManager.getChildCount();
            int totalItemCount = mLayoutManager.getItemCount();
            int firstVisibleItem = mLayoutManager.findFirstVisibleItemPosition();

            if (firstVisibleItem + visibleItemCount >= totalItemCount - mPreLoadCount) {
                onEndOfScrollReached(recyclerView);
            }
        }
    }

    /**
     * Called when end of scroll is reached.
     *
     * @param recyclerView - related recycler view.
     */
    public abstract void onEndOfScrollReached(RecyclerView recyclerView);

    public void disableScrollListener() {
        mEnabled = false;
    }

    public void enableScrollListener() {
        mEnabled = true;
    }

    public void setPreLoadCount(int mPreLoadCount) {
        this.mPreLoadCount = mPreLoadCount;
    }
}
