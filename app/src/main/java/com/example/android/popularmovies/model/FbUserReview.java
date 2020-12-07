package com.example.android.popularmovies.model;

public class FbUserReview {

    private String movie;
    private String review;

    public FbUserReview(){}

    public FbUserReview(String movie, String review){
        this.movie = movie;
        this.review = review;
    }

    public String getReview() {
        return review;
    }

    public String getMovie() {
        return movie;
    }
}
