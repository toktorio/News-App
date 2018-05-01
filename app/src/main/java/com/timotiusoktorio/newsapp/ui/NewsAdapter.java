package com.timotiusoktorio.newsapp.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.timotiusoktorio.newsapp.R;
import com.timotiusoktorio.newsapp.data.model.News;
import com.timotiusoktorio.newsapp.util.StringHelper;

import java.text.ParseException;
import java.util.List;

class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {

    private static final String TAG = NewsAdapter.class.getSimpleName();

    private Context mContext;
    private List<News> mNewsList;
    private OnItemClickListener mOnItemClickListener;

    NewsAdapter(Context context, List<News> newsList, OnItemClickListener listener) {
        mContext = context;
        mNewsList = newsList;
        mOnItemClickListener = listener;
    }

    public void clearData() {
        mNewsList.clear();
        notifyDataSetChanged();
    }

    public void addData(List<News> data) {
        mNewsList.addAll(data);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_item_news, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final News news = mNewsList.get(position);
        holder.mTitleTextView.setText(news.getTitle());
        holder.mTagTextView.setText(news.getTag());

        List<String> contributors = news.getContributors();
        boolean isContributorsEmpty = contributors.isEmpty();
        if (!isContributorsEmpty) {
            holder.mContributorsTextView.setText(contributors.get(0));
        }
        holder.mContributorsTextView.setVisibility(isContributorsEmpty ? View.GONE : View.VISIBLE);

        try {
            String date = StringHelper.formatDateTimeToElapsedTimeString(mContext, news.getDate());
            holder.mDateTextView.setText(date);
            holder.mDateTextView.setVisibility(View.VISIBLE);
        } catch (ParseException e) {
            Log.e(TAG, e.getMessage(), e);
            holder.mDateTextView.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemClickListener.onItemClick(news.getUrl());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mNewsList.size();
    }

    public interface OnItemClickListener {

        void onItemClick(String newsUrl);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView mTitleTextView;
        TextView mTagTextView;
        TextView mContributorsTextView;
        TextView mDateTextView;

        ViewHolder(View itemView) {
            super(itemView);
            mTitleTextView = itemView.findViewById(R.id.title_text_view);
            mTagTextView = itemView.findViewById(R.id.tag_text_view);
            mContributorsTextView = itemView.findViewById(R.id.contributors_text_view);
            mDateTextView = itemView.findViewById(R.id.date_text_view);
        }
    }
}