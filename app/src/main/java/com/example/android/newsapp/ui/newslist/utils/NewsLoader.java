package com.example.android.newsapp.ui.newslist.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;

import com.example.android.newsapp.ui.newslist.model.News;

import java.util.List;


//AsyncTaskLoader class to get news data from server
public class NewsLoader extends AsyncTaskLoader<List<News>> {

    private String mRequestUrl;
    private String mRequestMethod;

    //constructor
    public NewsLoader(@NonNull Context context, String mRequestUrl, String mRequestMethod) {
        super(context);
        this.mRequestUrl = mRequestUrl;
        this.mRequestMethod = mRequestMethod;
    }

    /**
     * starts an asynchronous load of the loader's data
     */
    @Override
    protected void onStartLoading() {
        //forces an asynchronous load, ignoring any previously loaded data
        forceLoad();
    }

    /**
     * called on a worker thread to perform the loading of the data
     *
     * @return fetched data
     */
    @Nullable
    @Override
    public List<News> loadInBackground() {
        if (mRequestUrl == null) {
            return null;
        }
        return NewsHelper.getNews(mRequestUrl, mRequestMethod);
    }
}
