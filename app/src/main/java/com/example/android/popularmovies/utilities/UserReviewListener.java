package com.example.android.popularmovies.utilities;

import android.support.annotation.NonNull;
import android.util.Log;

import com.example.android.popularmovies.model.FbUserReview;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class UserReviewListener {

    public static void listeningToNewReviews(){
        DatabaseReference databaseReference = FirebaseHelper.getFirebaseDatabaseObject().getReference("user_reviews");
        DatabaseReference databaseReference1 = databaseReference.child(FirebaseHelper.getFirebaseAuthObject().getUid());
        final ValueEventListener valueEventListener1 = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                FbUserReview userReview = dataSnapshot.getValue(FbUserReview.class);
               // Log.e("sarararaa", userReview.getMovie()+" "+userReview.getReview());


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        databaseReference1.addValueEventListener(valueEventListener1);
    }
}
