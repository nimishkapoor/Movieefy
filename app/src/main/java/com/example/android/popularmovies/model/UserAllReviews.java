package com.example.android.popularmovies.model;

import java.util.List;

public class UserAllReviews {

    private String uId;
    private List<UserReview> allReviews;

    public UserAllReviews(String uId, List<UserReview> allReviews){
        this.allReviews = allReviews;
        this.uId = uId;
    }

    public List<UserReview> getAllReviews() {
        return allReviews;
    }

    public String getuId() {
        return uId;
    }
}
