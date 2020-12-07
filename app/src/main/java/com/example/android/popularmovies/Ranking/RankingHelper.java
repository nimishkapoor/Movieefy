package com.example.android.popularmovies.Ranking;

import android.support.annotation.NonNull;
import android.util.Log;

import com.example.android.popularmovies.model.User;
import com.example.android.popularmovies.model.Word;
import com.example.android.popularmovies.utilities.FirebaseHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class RankingHelper {

    private HashMap<String, HashMap<String, Double>> word_scores;

    public RankingHelper(){

    }

    public static void addToDb(HashMap<String, Double> wordsWithScore){
        Iterator iterator = wordsWithScore.entrySet().iterator();
        while (iterator.hasNext()){
            Map.Entry mapElement = (Map.Entry)iterator.next();
            String word = (String) mapElement.getKey();
            double weight= (double) mapElement.getValue();
            checkIfPresentInBOG(word, weight);
        }
    }

    public static void checkIfPresentInBOG(final String word, final double tfIDfScore){
        FirebaseDatabase firebaseDatabase = FirebaseHelper.getFirebaseDatabaseObject();
        final DatabaseReference databaseReference = firebaseDatabase.getReference("bag_of_words");

        final ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DataSnapshot reviewsSnapshot = dataSnapshot.child(word);
                if (reviewsSnapshot != null){
                    Word word1 = (Word)reviewsSnapshot.getValue();
                    double newtfidf = word1.getTf_idf_weight() + tfIDfScore;
                    int newdocfreq = word1.getDocument_freq() + 1;
                    double newPolarityScore = (newtfidf*word1.getStatic_weight() + word1.getPolarity_Score())/2;
                    word1.setTf_idf_weight(newtfidf);
                    word1.setDocument_freq(newdocfreq);
                    word1.setPolarity_Score(newPolarityScore);
                    databaseReference.child(word).setValue(word1);
                } else {


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        databaseReference.addListenerForSingleValueEvent(valueEventListener);
    }

    public static void setInitialBagOfWords(){
        Word w1 = new Word("good", 16.0, 0.0, 0.0, 1);
        Word w2 = new Word("nice", 17.0, 0.0, 0.0, 1);
        Word w3 = new Word("interesting", 18.0, 0.0, 0.0, 1);
        Word w4 = new Word("great", 20.0, 0.0, 0.0, 1);
        ArrayList<Word> words = new ArrayList<>();
        words.add(w1);
        words.add(w2);
        words.add(w3);
        words.add(w4);
        addToBagDb(words);
    }

    public static void addToBagDb(ArrayList<Word> words){
        for(Word word: words)
            add_to_bag(word);
    }

    public static void add_to_bag(Word w){
        FirebaseDatabase firebaseDatabase = FirebaseHelper.getFirebaseDatabaseObject();
        DatabaseReference databaseReference = firebaseDatabase.getReference("bag_of_words");
        databaseReference.child(w.getWord()).setValue(w);
    }
}
