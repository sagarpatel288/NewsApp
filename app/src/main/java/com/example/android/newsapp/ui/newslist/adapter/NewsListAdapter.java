package com.example.android.newsapp.ui.newslist.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.newsapp.R;
import com.example.android.newsapp.databinding.ItemNewsListBinding;
import com.example.android.newsapp.ui.newslist.model.News;
import com.example.android.newsapp.ui.newslist.viewholder.NewsListViewHolder;
import com.library.android.common.appconstants.AppConstants;
import com.library.android.common.listeners.Callbacks;
import com.library.android.common.utils.ViewUtils;

import java.util.ArrayList;
import java.util.List;

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

    public void addData(List<News> mNewsList) {
        removeLoadMore();
        setItemType(AppConstants.ItemViewType.ITEM_DATA);
        if (mNewsList != null) {
            this.mNewsList.addAll(mNewsList);
        }
        notifyDataSetChanged();
    }

    private void removeLoadMore() {
        // Note: 11/4/2018 by sagar  Proceed only if there is anything to remove
        if (mNewsList != null && mNewsList.size() > 0) {
            // Note: 11/4/2018 by sagar  Only last item that must be progress bar (null item) should be removed
            if (mNewsList.get(mNewsList.size() - 1) == null) {
                mNewsList.remove(mNewsList.size() - 1);
            }
        }
    }

    private void addLoadMore() {
        if (mNewsList != null) {
            mNewsList.add(null);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_news_list, viewGroup, false);
        ItemNewsListBinding binding = ItemNewsListBinding.bind(view);
        return new NewsListViewHolder(view, binding, this);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        if (viewHolder instanceof NewsListViewHolder) {
            NewsListViewHolder holder = (NewsListViewHolder) viewHolder;
            ItemNewsListBinding binding = holder.mBinding;
            News news = mNewsList.get(i);
            if (mContext != null && binding != null) {
                setTags(holder, binding, i);
                setData(binding, news, i);
            }
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
        return mNewsList.get(position) != null
                ? com.library.android.common.appconstants.AppConstants.ItemViewType.ITEM_DATA
                : com.library.android.common.appconstants.AppConstants.ItemViewType.ITEM_PROGRESS;
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
}
