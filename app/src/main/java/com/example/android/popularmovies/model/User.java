package com.example.android.popularmovies.model;

public class User {

    private String username;
    private String useremail;

    public User(String name, String email){
        this.username = name;
        this.useremail = email;
    }

    public String getUseremail() {
        return useremail;
    }

    public String getUsername() {
        return username;
    }
}
