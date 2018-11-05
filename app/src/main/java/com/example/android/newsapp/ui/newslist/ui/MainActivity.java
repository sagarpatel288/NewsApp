package com.example.android.newsapp.ui.newslist.ui;

import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.example.android.newsapp.BuildConfig;
import com.example.android.newsapp.R;
import com.example.android.newsapp.databinding.ActivityMainBinding;
import com.example.android.newsapp.ui.newslist.adapter.NewsListAdapter;
import com.example.android.newsapp.ui.newslist.model.News;
import com.example.android.newsapp.ui.newslist.utils.NewsLoader;
import com.library.android.common.appconstants.AppConstants;
import com.library.android.common.utils.LoadMoreScrollListener;
import com.library.android.common.utils.NetworkUtils;
import com.library.android.common.utils.ViewUtils;

import java.util.List;

public class MainActivity extends com.example.android.newsapp.ui.base.BaseActivity
        implements SwipeRefreshLayout.OnRefreshListener, LoaderManager.LoaderCallbacks<List<News>> {

    private ActivityMainBinding binding;

    private boolean hasMoreData;
    private NewsListAdapter mNewsListAdapter;
    private LoaderManager mLoaderManager;
    private int mPageNumber = 1;

    public boolean hasMoreData() {
        return hasMoreData;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void onViewStubInflated(View inflatedView, Bundle savedInstanceState) {
        binding = DataBindingUtil.bind(inflatedView);
    }

    @Override
    public void initControllers() {
        mNewsListAdapter = new NewsListAdapter(this);
        mLoaderManager = LoaderManager.getInstance(this);
        if (NetworkUtils.isNetworkAvailable(this)) {
            mLoaderManager.initLoader(1, null, this);
        } else {
            onEmptyData();
            showError(getString(R.string.label_no_internet));
        }
    }

    @Override
    public void handleViews() {
        setRecyclerView();
    }

    private void setRecyclerView() {
        ViewUtils.optimizeRecyclerView(binding.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        binding.recyclerView.setLayoutManager(linearLayoutManager);
        binding.recyclerView.setAdapter(mNewsListAdapter);
    }

    @Override
    public void setListeners() {
        binding.swipeRefreshRecyclerList.setOnRefreshListener(this);
        binding.recyclerView.addOnScrollListener(new LoadMoreScrollListener() {
            @Override
            public void onEndOfScrollReached(RecyclerView recyclerView) {
                if (hasMoreData) {
                    if (NetworkUtils.isNetworkAvailable(MainActivity.this)) {
                        if (mLoaderManager != null) {
                            mLoaderManager.restartLoader(1, null, MainActivity.this);
                        }
                    } else if (mNewsListAdapter != null && mNewsListAdapter.getItemCount() > 0) {
                        Toast.makeText(MainActivity.this, getString(R.string.label_no_internet), Toast.LENGTH_SHORT).show();
                    } else {
                        onEmptyData();
                        showError(getString(R.string.label_no_internet));
                    }
                }
            }
        });
    }

    @Override
    public void restoreValues(Bundle savedInstanceState) {

    }

    @Override
    public void onGetConnectionState(boolean isConnected) {
        if (!isConnected) {
            if (mNewsListAdapter != null && mNewsListAdapter.getItemCount() > 0) {
                Toast.makeText(this, getString(R.string.label_no_internet), Toast.LENGTH_SHORT).show();
            } else {
                onEmptyData();
                showError(getString(R.string.label_no_internet));
            }
        }
    }

    private void onEmptyData() {
        ViewUtils.toggleVisibility(View.GONE, binding.recyclerView);
        ViewUtils.toggleVisibility(View.VISIBLE, binding.ctvMsg);
    }

    private void showError(String message) {
        ViewUtils.setText(binding.ctvMsg, message);
    }

    @Override
    public void onRefresh() {
        // TODO: 11/4/2018 by sagar  Call API
        binding.swipeRefreshRecyclerList.setRefreshing(true);
        // Note: 11/4/2018 by sagar  set false in onGetResponse
    }

    @NonNull
    @Override
    public Loader<List<News>> onCreateLoader(int i, @Nullable Bundle bundle) {
        Uri.Builder builder = Uri.parse(com.example.android.newsapp.AppConstants.BASE_URL).buildUpon();
        builder.appendQueryParameter(com.example.android.newsapp.AppConstants.QUERY_PARAM, com.example.android.newsapp.AppConstants.QUERY_VALUE)
                .appendQueryParameter(com.example.android.newsapp.AppConstants.QUERY_SHOW_FIELD, com.example.android.newsapp.AppConstants.QUERY_SHOW_THUMBNAIL)
                .appendQueryParameter(com.example.android.newsapp.AppConstants.QUERY_AUTHOR, com.example.android.newsapp.AppConstants.QUERY_AUTHOR_VALUE)
                .appendQueryParameter(com.example.android.newsapp.AppConstants.QUERY_PAGE, String.valueOf(mPageNumber))
                .appendQueryParameter(com.example.android.newsapp.AppConstants.QUERY_API_KEY, BuildConfig.API_KEY_GUARDIAN);

        return new NewsLoader(this, builder.toString(), AppConstants.HttpRequestMethod.GET);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<News>> loader, List<News> newsList) {
        // TODO: 11/4/2018 by sagar  if list size >= 10; loadMore true, else false
        binding.swipeRefreshRecyclerList.setRefreshing(false);
        if (newsList != null) {
            if (newsList.size() > 0) {
                onGetData();
                mPageNumber++;
                if (newsList.size() >= AppConstants.PAGINATION) {
                    setHasMoreData(true);
                } else {
                    setHasMoreData(false);
                }
                mNewsListAdapter.addData(newsList);
            } else {
                onEmptyData();
                showError(getString(R.string.label_no_data));
            }
        }
        mLoaderManager.destroyLoader(1);
    }

    private void onGetData() {
        ViewUtils.toggleVisibility(View.VISIBLE, binding.recyclerView);
        ViewUtils.toggleVisibility(View.GONE, binding.ctvMsg);
    }

    public void setHasMoreData(boolean hasMoreData) {
        this.hasMoreData = hasMoreData;
    }

    @Override
    public void onLoaderReset(@NonNull Loader loader) {

    }
}