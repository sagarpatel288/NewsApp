package com.example.android.newsapp.newslist.ui;

import android.databinding.DataBindingUtil;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.Toast;

import com.example.android.newsapp.BuildConfig;
import com.example.android.newsapp.R;
import com.example.android.newsapp.base.BaseActivity;
import com.example.android.newsapp.databinding.ActivityMainBinding;
import com.example.android.newsapp.newslist.adapter.NewsListAdapter;
import com.example.android.newsapp.newslist.model.News;
import com.example.android.newsapp.newslist.utils.NewsLoader;
import com.library.android.common.appconstants.AppConstants;
import com.library.android.common.utils.NetworkUtils;
import com.library.android.common.utils.RvLoadMoreScrollListener;
import com.library.android.common.utils.ViewUtils;

import java.util.List;

import static android.util.Log.d;
import static com.library.android.common.appconstants.AppConstants.TAG;

public class MainActivity extends BaseActivity
        implements SwipeRefreshLayout.OnRefreshListener, LoaderManager.LoaderCallbacks<List<News>> {

    private ActivityMainBinding binding;

    private boolean hasMoreData;
    private NewsListAdapter mNewsListAdapter;
    private LoaderManager mLoaderManager;
    private int mPageNumber = 1;
    private boolean isLoading;

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
            d(TAG, "MainActivity: initControllers: initLoader");
            mLoaderManager.initLoader(1, null, this);
        } else {
            onEmptyData();
            showError(getString(R.string.label_no_internet));
        }
    }

    @Override
    public void handleViews() {
        ViewUtils.toggleVisibility(View.GONE, binding.ctvMsg);
        ViewUtils.toggleVisibility(View.VISIBLE, binding.progressbar);
        binding.progressbar.getIndeterminateDrawable()
                .setColorFilter(ContextCompat.getColor(this, R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
        setRecyclerView();
        checkNetwork();
    }

    private void checkNetwork() {
        if (!NetworkUtils.isNetworkAvailable(this)){
            showError(getString(R.string.label_no_internet));
        }
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
        binding.recyclerView.addOnScrollListener(new RvLoadMoreScrollListener() {
            @Override
            public void onLoadMore() {
                if (!isLoading && hasMoreData) {
                    if (mNewsListAdapter != null){
                        d(TAG, "MainActivity: onLoadMore: adding load more");
                        mNewsListAdapter.addLoadMore();
                    }
                    Runnable runnable = new Runnable() {
                        public void run() {
                            loadData();
                        }
                    };
                    Handler handler = new Handler();
                    handler.post(runnable);
                    isLoading = true;
                }
            }
        });
    }

    private void loadData() {
        if (NetworkUtils.isNetworkAvailable(MainActivity.this)) {
            if (mLoaderManager != null) {
                d(TAG, "MainActivity: loadData: restarting loader");
                mLoaderManager.restartLoader(1, null, MainActivity.this);
            }
        } else if (mNewsListAdapter != null && mNewsListAdapter.getItemCount() > 0) {
            setPullToRefresh(false);
            Toast.makeText(MainActivity.this, getString(R.string.label_no_internet), Toast.LENGTH_SHORT).show();
        } else {
            onEmptyData();
            showError(getString(R.string.label_no_internet));
        }
    }

    @Override
    public void restoreValues(Bundle savedInstanceState) {

    }

    private void setPullToRefresh(boolean isRefreshing){
        if (binding.swipeRefreshRecyclerList != null){
            binding.swipeRefreshRecyclerList.setRefreshing(false);
        }
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
        setPullToRefresh(false);
        ViewUtils.toggleVisibility(View.GONE, binding.recyclerView);
        ViewUtils.toggleVisibility(View.VISIBLE, binding.ctvMsg);
    }

    private void showError(String message) {
        d(TAG, "MainActivity: showError: ");
        ViewUtils.toggleVisibility(View.GONE, binding.progressbar);
        ViewUtils.setText(binding.ctvMsg, message);
    }

    @Override
    public void onRefresh() {
        mPageNumber = 1;
        binding.swipeRefreshRecyclerList.setRefreshing(true);
        loadData();
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

        d(TAG, "MainActivity: onCreateLoader: ");
        return new NewsLoader(this, builder.toString(), AppConstants.HttpRequestMethod.GET);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<News>> loader, final List<News> newsList) {
        d(TAG, "MainActivity: onLoadFinished: ");
        ViewUtils.toggleVisibility(View.GONE, binding.progressbar);
        if (newsList != null) {
            if (newsList.size() > 0) {
                onGetData();
                if (newsList.size() >= AppConstants.PAGINATION) {
                    setHasMoreData(true);
                } else {
                    setHasMoreData(false);
                }
                mNewsListAdapter.removeLoadMore();
                // Note: 11/14/2018 by sagar  Forcefully stopping load more
//                setHasMoreData(false);
//                mNewsListAdapter.addData(newsList, mPageNumber);
                isLoading = false;
                Runnable runnable = new Runnable() {
                    public void run() {
                        mNewsListAdapter.addData(newsList, mPageNumber);
                    }
                };
                Handler handler = new Handler();
                handler.post(runnable);

                mPageNumber++;
            } else {
                onEmptyData();
                showError(getString(R.string.label_no_data));
            }
        }
        mLoaderManager.destroyLoader(1);
    }

    private void onGetData() {
        setPullToRefresh(false);
        ViewUtils.toggleVisibility(View.VISIBLE, binding.recyclerView);
        ViewUtils.toggleVisibility(View.GONE, binding.ctvMsg);
    }

    public void setHasMoreData(boolean hasMoreData) {
        this.hasMoreData = hasMoreData;
    }

    @Override
    public void onLoaderReset(@NonNull Loader loader) {
        d(TAG, "MainActivity: onLoaderReset: ");
    }
}
