package com.example.android.newsapp.newslist.model;

import android.os.Parcel;
import android.os.Parcelable;

public class News implements Parcelable {

    public static final Parcelable.Creator<News> CREATOR = new Parcelable.Creator<News>() {
        @Override
        public News createFromParcel(Parcel source) {
            return new News(source);
        }

        @Override
        public News[] newArray(int size) {
            return new News[size];
        }
    };
    private String title;
    private String section;
    private String webUrl;
    private String thumbnailUrl;
    private String publishDate;
    private String authorName;

    public News(String title, String section, String webUrl, String thumbnailUrl, String publishDate, String authorName) {
        this.title = title;
        this.section = section;
        this.webUrl = webUrl;
        this.thumbnailUrl = thumbnailUrl;
        this.publishDate = publishDate;
        this.authorName = authorName;
    }

    protected News(Parcel in) {
        this.title = in.readString();
        this.section = in.readString();
        this.webUrl = in.readString();
        this.thumbnailUrl = in.readString();
        this.publishDate = in.readString();
        this.authorName = in.readString();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getWebUrl() {
        return webUrl;
    }

    public void setWebUrl(String webUrl) {
        this.webUrl = webUrl;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(String publishDate) {
        this.publishDate = publishDate;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeString(this.section);
        dest.writeString(this.webUrl);
        dest.writeString(this.thumbnailUrl);
        dest.writeString(this.publishDate);
        dest.writeString(this.authorName);
    }
}
