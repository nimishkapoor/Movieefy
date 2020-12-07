package com.example.android.popularmovies.model;

public class UserReview {

    private String movieId;
    private String review;

    public UserReview(String movieId, String review){
        this.movieId = movieId;
        this.review = review;
    }

    public String getMovieId() {
        return movieId;
    }

    public String getReview() {
        return review;
    }
}
