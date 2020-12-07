package com.example.android.popularmovies.model;

public class Word {

    private String word;
    private double static_weight;
    private double tf_idf_weight;
    private double polarity_Score;
    private int document_freq;

    public Word(String word, double static_weight, double tf_idf_weight,
                double polarity_Score, int document_freq){
        this.word= word;
        this.static_weight = static_weight;
        this.tf_idf_weight = tf_idf_weight;
        this.polarity_Score = polarity_Score;
        this.document_freq = document_freq;
    }

    public String getWord() {
        return word;
    }

    public int getDocument_freq() {
        return document_freq;
    }

    public void setDocument_freq(int document_freq) {
        this.document_freq = document_freq;
    }

    public void setPolarity_Score(double polarity_Score) {
        this.polarity_Score = polarity_Score;
    }

    public void setStatic_weight(double static_weight) {
        this.static_weight = static_weight;
    }

    public void setTf_idf_weight(double tf_idf_weight) {
        this.tf_idf_weight = tf_idf_weight;
    }

    public double getPolarity_Score() {
        return polarity_Score;
    }

    public double getStatic_weight() {
        return static_weight;
    }

    public double getTf_idf_weight() {
        return tf_idf_weight;
    }
}
