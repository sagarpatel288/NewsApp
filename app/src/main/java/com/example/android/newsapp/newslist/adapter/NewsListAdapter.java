package com.example.android.newsapp.newslist.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.newsapp.R;
import com.example.android.newsapp.databinding.ItemNewsListBinding;
import com.example.android.newsapp.newslist.model.News;
import com.example.android.newsapp.newslist.viewholder.NewsListViewHolder;
import com.library.android.common.appconstants.AppConstants;
import com.library.android.common.databinding.LayoutProgressBinding;
import com.library.android.common.listeners.Callbacks;
import com.library.android.common.utils.ViewUtils;
import com.library.android.common.viewholder.LoadMoreViewHolder;

import java.util.ArrayList;
import java.util.List;

import static android.util.Log.d;
import static com.library.android.common.appconstants.AppConstants.TAG;

public class NewsListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements Callbacks.OnEventCallback {

    private Context mContext;
    private List<News> mNewsList;
    private int mItemType;

    public NewsListAdapter(Context mContext) {
        this.mContext = mContext;
        mNewsList = new ArrayList<>();
    }

    public int getItemType() {
        return mItemType;
    }

    public void setItemType(int mItemType) {
        this.mItemType = mItemType;
    }

    public void addData(List<News> mNewsList, int mPageNumber) {
//        removeLoadMore();
        setItemType(AppConstants.ItemViewType.ITEM_DATA);
        if (this.mNewsList == null) {
            this.mNewsList = new ArrayList<>();
        }
        if (mNewsList != null) {
            if (mPageNumber == 1) {
                d(TAG, "NewsListAdapter: addData: clearing data for first page reload case");
                this.mNewsList.clear();
            }
            d(TAG, "NewsListAdapter: addData: ");
            this.mNewsList.addAll(mNewsList);
        }
        notifyDataSetChanged();
    }

    public void removeLoadMore() {
        d(TAG, "NewsListAdapter: removeLoadMore: entered");
        // Note: 11/4/2018 by sagar  Proceed only if there is anything to remove
        if (mNewsList != null && mNewsList.size() > 0) {
            // Note: 11/4/2018 by sagar  Only last item that must be progress bar (null item) should be removed
            if (mNewsList.get(mNewsList.size() - 1) == null) {
                d(TAG, "NewsListAdapter: removeLoadMore: removing last item");
                mNewsList.remove(mNewsList.size() - 1);
            }
        }
        notifyDataSetChanged();
    }

    public void addLoadMore() {
        if (mNewsList == null) {
            mNewsList = new ArrayList<>();
        }
        Runnable runnable = new Runnable() {
            public void run() {
                mNewsList.add(null);
                notifyDataSetChanged();
            }
        };
        Handler handler = new Handler();
        handler.post(runnable);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (i == AppConstants.ItemViewType.ITEM_DATA) {
            d(TAG, "NewsListAdapter: onCreateViewHolder: item");
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_news_list, viewGroup, false);
            ItemNewsListBinding binding = ItemNewsListBinding.bind(view);
            return new NewsListViewHolder(view, binding, this);
        } else {
            d(TAG, "NewsListAdapter: onCreateViewHolder: loading");
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_progress, viewGroup, false);
            LayoutProgressBinding binding = LayoutProgressBinding.bind(view);
            return new LoadMoreViewHolder(view, binding);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        if (viewHolder instanceof NewsListViewHolder) {
            d(TAG, "NewsListAdapter: onBindViewHolder: item");
            NewsListViewHolder holder = (NewsListViewHolder) viewHolder;
            ItemNewsListBinding binding = holder.mBinding;
            News news = mNewsList.get(i);
            d(TAG, "NewsListAdapter: onBindViewHolder: news: " + news);
            if (mContext != null && binding != null && news != null) {
                setTags(holder, binding, i);
                setData(binding, news, i);
            }
        } else {
            d(TAG, "NewsListAdapter: onBindViewHolder: loading");
            LoadMoreViewHolder loadMoreViewHolder = (LoadMoreViewHolder) viewHolder;
            LayoutProgressBinding binding = loadMoreViewHolder.mBinding;
            binding.progressBar.getIndeterminateDrawable().setColorFilter
                    (ContextCompat.getColor(mContext, R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
            binding.progressBar.setIndeterminate(true);
        }
    }

    private void setTags(NewsListViewHolder holder, ItemNewsListBinding binding, int i) {
        holder.itemView.setTag(i);
    }

    private void setData(ItemNewsListBinding binding, News news, int position) {
        ViewUtils.setEnable(false, binding.cbtnTitle, binding.cbtnSection, binding.cbtnTime, binding.cbtnTime);
        ViewUtils.setText(binding.cbtnTitle, news.getTitle());
        ViewUtils.setText(binding.cbtnSection, news.getSection());
        ViewUtils.setText(binding.cbtnTime, news.getPublishDate());
        ViewUtils.setText(binding.cbtnAuthor, news.getAuthorName());
        ViewUtils.loadImage(mContext, news.getThumbnailUrl(), R.drawable.ic_img_not_found, R.drawable.ic_img_not_found, binding.civProfile);
    }

    @Override
    public int getItemViewType(int position) {
        d(TAG, "NewsListAdapter: getItemViewType: " + mNewsList.get(position));
        return mNewsList.get(position) != null
                ? com.library.android.common.appconstants.AppConstants.ItemViewType.ITEM_DATA
                : AppConstants.ItemViewType.ITEM_LOADING;
    }

    @Override
    public int getItemCount() {
        return mNewsList.size();
    }

    @Override
    public void onEventClick(View view, int positionTag) {
        News news = mNewsList.get(positionTag);
        if (news != null && news.getWebUrl() != null) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(news.getWebUrl()));
            mContext.startActivity(intent);
        }
    }

    public void removeLoadMore(Object o) {
        if (mNewsList != null && mNewsList.size() > 0) {
            News news = (News) o;
            mNewsList.remove(news);
            notifyDataSetChanged();
        }
    }
}
