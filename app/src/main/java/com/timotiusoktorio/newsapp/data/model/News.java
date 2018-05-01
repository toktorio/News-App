package com.timotiusoktorio.newsapp.data.model;

import java.util.List;

@SuppressWarnings("unused")
public class News {

    private String mTitle;
    private String mDate;
    private String mUrl;
    private String mTag;
    private List<String> mContributors;

    public News(String title, String date, String url, String tag, List<String> contributors) {
        mTitle = title;
        mDate = date;
        mUrl = url;
        mTag = tag;
        mContributors = contributors;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getDate() {
        return mDate;
    }

    public void setDate(String date) {
        mDate = date;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String url) {
        mUrl = url;
    }

    public String getTag() {
        return mTag;
    }

    public void setTag(String tag) {
        mTag = tag;
    }

    public List<String> getContributors() {
        return mContributors;
    }

    public void setContributors(List<String> contributors) {
        mContributors = contributors;
    }

    @Override
    public String toString() {
        return "News{" +
                "mTitle='" + mTitle + '\'' +
                ", mDate='" + mDate + '\'' +
                ", mUrl='" + mUrl + '\'' +
                ", mTag='" + mTag + '\'' +
                ", mContributors=" + mContributors +
                '}';
    }
}