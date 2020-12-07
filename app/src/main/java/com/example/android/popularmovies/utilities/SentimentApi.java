package com.example.android.popularmovies.utilities;

import com.example.android.popularmovies.model.SentimentValue;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Sentiment API interface, retrofit turns it into Java objects to use
 */
public interface SentimentApi {

    @GET("/predict")
    Call<SentimentValue> getSentimentValue(@Query("review") String review);

}
