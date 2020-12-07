package com.example.android.popularmovies.ui.review;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.model.FbUserReview;
import com.example.android.popularmovies.model.Review;
import com.example.android.popularmovies.utilities.FirebaseHelper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

public class UserReviewsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView noReview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_review);
        recyclerView = findViewById(R.id.rv_review);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        noReview = findViewById(R.id.tv_no_reviews);
        getSupportActionBar().setTitle("Your Reviews");
        getUserReviews();
    }

    public void getUserReviews(){
        FirebaseDatabase firebaseDatabase = FirebaseHelper.getFirebaseDatabaseObject();

        DatabaseReference databaseReference = firebaseDatabase.getReference("user_reviews");
        final ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                DataSnapshot reviewsSnapshot = dataSnapshot.child(FirebaseHelper.getFirebaseAuthObject().getUid());

                Map<String, Object> reviewData = (Map<String, Object>)reviewsSnapshot.getValue();
                if (reviewData == null){
                    noReview.setVisibility(View.VISIBLE);
                } else {
                    noReview.setVisibility(View.GONE);
                    ArrayList<Review> reviewArrayList = getListofReviews(reviewData);
                    ReviewAdapter reviewAdapter = new ReviewAdapter(reviewArrayList, null);
                    recyclerView.setAdapter(reviewAdapter);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        databaseReference.addValueEventListener(valueEventListener);
    }

    public ArrayList<Review> getListofReviews(Map<String, Object> map){
        ArrayList<Review> fbreviews1 = new ArrayList<>();
        for (Map.Entry<String,Object> entry : map.entrySet()){
            // String name = databaseReference.child(firebaseAuth.getUid())
            fbreviews1.add(new Review(entry.getKey(), (String)entry.getValue()));
        }
        Log.e("sizeofreview", String.valueOf(fbreviews1.size()));
        return fbreviews1;
    }
}
