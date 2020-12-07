

package com.example.android.popularmovies.model;

import com.google.gson.annotations.SerializedName;

/**
 * A {@link Review} object includes information related to a movie review.
 */
public class Review {

    @SerializedName("author")
    private String mAuthor;

    @SerializedName("content")
    private String mContent;

    @SerializedName("id")
    private String mId;

    @SerializedName("url")
    private String mUrl;

    public Review(){}

    public Review(String mAuthor, String mContent, String mId, String mUrl){
        this.mAuthor = mAuthor;
        this.mContent = mContent;
        this.mId = mId;
        this.mUrl = mUrl;
    }

    public Review(String mAuthor, String mContent){
        this.mAuthor = mAuthor;
        this.mContent = mContent;
    }

    public void setAuthor(String author) {
        mAuthor = author;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public void setContent(String content) {
        mContent = content;
    }

    public String getContent() {
        return mContent;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getId() {
        return mId;
    }

    public void setUrl(String url) {
        mUrl = url;
    }

    public String getUrl() {
        return mUrl;
    }
}
