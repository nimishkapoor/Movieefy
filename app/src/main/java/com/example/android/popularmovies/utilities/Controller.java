
package com.example.android.popularmovies.utilities;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.android.popularmovies.utilities.Constant.MOVIE_BASE_URL;
import static com.example.android.popularmovies.utilities.Constant.REVIEW_SENTIMENT_BASE_URL;

/**
 *  Create a singleton of Retrofit.
 */
public class Controller {

    /** Static variable for Retrofit */
    private static Retrofit sRetrofit = null;
    private static Retrofit sRetrofit23 = null;


    public static Retrofit getClient() {
        if (sRetrofit == null) {
            // Create the Retrofit instance using the builder
            sRetrofit = new Retrofit.Builder()
                    // Set the API base URL
                    .baseUrl(MOVIE_BASE_URL)
                    // Use GsonConverterFactory class to generate an implementation of the TheMovieApi interface
                    // which uses Gson for its deserialization
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return sRetrofit;
    }

    public static Retrofit getClientForReviewSentiment() {
        if (sRetrofit23 == null) {
            // Create the Retrofit instance using the builder
            sRetrofit23 = new Retrofit.Builder()
                    // Set the API base URL
                    .baseUrl(REVIEW_SENTIMENT_BASE_URL)
                    // Use GsonConverterFactory class to generate an implementation of the TheMovieApi interface
                    // which uses Gson for its deserialization
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return sRetrofit23;
    }
}
