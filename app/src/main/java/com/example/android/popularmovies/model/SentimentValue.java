package com.example.android.popularmovies.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SentimentValue {

    @SerializedName("sentiment")
    private String sentiment_value;

    @SerializedName("positive_words")
    private List<String> positive_words;

    public SentimentValue(){}

    public SentimentValue(String sentiment_value){
        this.sentiment_value = sentiment_value;
    }

    public void setSentiment_value(String sentiment_value){
        this.sentiment_value = sentiment_value;
    }

    public String getSentiment_value() {
        return sentiment_value;
    }

    public void setPositive_words(List<String> positive_words) {
        this.positive_words = positive_words;
    }

    public List<String> getPositive_words() {
        return positive_words;
    }
}
