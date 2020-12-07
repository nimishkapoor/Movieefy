package com.example.android.popularmovies.Ranking;

public class Document {
    private int id;
    private String text;

    public Document (){}

    public Document(int id, String text){
        this.id = id;
        this.text = text;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
