
package com.example.android.popularmovies.ui.review;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.databinding.FragmentReviewBinding;
import com.example.android.popularmovies.model.FbReviewObject;
import com.example.android.popularmovies.model.Movie;
import com.example.android.popularmovies.model.Review;
import com.example.android.popularmovies.model.ReviewResponse;
import com.example.android.popularmovies.model.User;
import com.example.android.popularmovies.utilities.FirebaseHelper;
import com.example.android.popularmovies.utilities.InjectorUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.example.android.popularmovies.utilities.Constant.EXTRA_MOVIE;

public class ReviewFragment extends Fragment implements ReviewAdapter.ReviewAdapterOnClickHandler {

    /** Tag for a log message */
    private static final String TAG = ReviewFragment.class.getSimpleName();

    /** Member variable for the list of reviews */
    private List<Review> mReviews;

    /** This field is used for data binding */
    private FragmentReviewBinding mReviewBinding;

    /** Member variable for ReviewAdapter */
    private ReviewAdapter mReviewAdapter;

    /** Member variable for the Movie object */
    private Movie mMovie;

    /** ViewModel for ReviewFragment */
    private ReviewViewModel mReviewViewModel;

    private ArrayList<Review> fbreviews;

    private static String nameHolder;




    /**
     * Mandatory empty constructor for the fragment manager to instantiate the fragment
     */
    public ReviewFragment() {
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.e("callbackActviity", "onActivityCreated");

        // Store the Intent
        Intent intent = getActivity().getIntent();
        // Check if the Intent is not null, and has the extra we passed from MainActivity
        if (intent != null) {
            if (intent.hasExtra(EXTRA_MOVIE)) {
                // Receive the Movie object which contains information, such as ID, original title,
                // poster path, overview, vote average, release date, backdrop path.
                Bundle b = intent.getBundleExtra(EXTRA_MOVIE);
                mMovie = b.getParcelable(EXTRA_MOVIE);
            }
        }

        // Observe the data and update the UI
        setupViewModel(this.getActivity());
    }

    /**
     * Every time the user data is updated, the onChanged callback will be invoked and update the UI
     */
    private void setupViewModel(Context context) {
        ReviewViewModelFactory factory = InjectorUtils.provideReviewViewModelFactory(context, mMovie.getId());
        mReviewViewModel = ViewModelProviders.of(this, factory).get(ReviewViewModel.class);

        mReviewViewModel.getReviewResponse().observe(this, new Observer<ReviewResponse>() {
            @Override
            public void onChanged(@Nullable ReviewResponse reviewResponse) {
                if (reviewResponse != null) {
                    // Get the list of reviews
                    mReviews = reviewResponse.getReviewResults();
                   // getReviewsFromFirebase(mMovie.getId());
                    //mReviews.addAll(getReviewsFromFirebase(String.valueOf(mMovie.getId())));
                    getReviewsFromFirebase(String.valueOf(mMovie.getId()), reviewResponse, mReviews);
                   // Log.e("lalalamkmkmkmk890", String.valueOf(arrayList.size()));
                    //mReviews.add(new Review("iopppp", "opiiiiii", "", ""));
                }
            }
        });
    }

    private ArrayList<Review> getReviewsFromFirebase(final String movieId, final ReviewResponse reviewResponse, final List<Review> mReviews){
        final ArrayList<Review> reviewArrayList = new ArrayList<>();
        FirebaseDatabase firebaseDatabase = FirebaseHelper.getFirebaseDatabaseObject();

        DatabaseReference databaseReference = firebaseDatabase.getReference("movie_reviews");
        final ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                DataSnapshot reviewsSnapshot = dataSnapshot.child(movieId);
                Map<String, Object> reviewData = (Map<String, Object>)reviewsSnapshot.getValue();
                ArrayList<Review>  list = getListofReviews(reviewData);
                for (int i = 0; i < list.size(); i++)
                    mReviews.add(list.get(i));
                //mReviewAdapter.addAll(list);
                reviewResponse.setReviewResults(mReviews);
                if (!mReviews.isEmpty()) {
                    mReviewAdapter.addAll(mReviews);
                } else {
                    // If there are no reviews, show a message that says no reviews found
                    showNoReviewsMessage();
                }
                //reviewArrayList.addAll(getListofReviews(reviewData));
               /** Iterable<DataSnapshot> reviewsChildren = reviewsSnapshot.getChildren();

                for (DataSnapshot d: reviewsChildren){
                    fbreviews.add(d.getValue(String.class));
                }

                Log.e("lalalamkmkmkmk", String.valueOf(fbreviews.size()));*/

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        databaseReference.addValueEventListener(valueEventListener);
        Log.e("hakuna", String.valueOf(reviewArrayList.size()));
        return reviewArrayList;
    }

    public ArrayList<Review> getListofReviews(Map<String, Object> map){
        ArrayList<Review> fbreviews1 = new ArrayList<>();
        if (map == null){

        } else {
            for (Map.Entry<String,Object> entry : map.entrySet()){
                // String name = databaseReference.child(firebaseAuth.getUid())
                fbreviews1.add(new Review(entry.getKey(), (String)entry.getValue()));
            }
            Log.e("sizeofreview", String.valueOf(fbreviews1.size()));
            /** if (map !=null){

             }*/
        }
        return fbreviews1;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Log.e("callback", "oncreate");
        // Instantiate mReviewBinding using DataBindingUtil
        mReviewBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_review, container, false);
        View rootView = mReviewBinding.getRoot();

        // A LinearLayoutManager is responsible for measuring and positioning item views within a
        // RecyclerView into a linear list.
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mReviewBinding.rvReview.setLayoutManager(layoutManager);
        mReviewBinding.rvReview.setHasFixedSize(true);

        // Create an empty ArrayList
        mReviews = new ArrayList<>();
        mReviews.add(new Review("Johnny", "Best of the lot"));
        mReviews.add(new Review("Nathan", "Worst of the lot"));

        // The ReviewAdapter is responsible for displaying each item in the list.
        mReviewAdapter = new ReviewAdapter(mReviews, this);
        // Set ReviewAdapter on RecyclerView
        mReviewBinding.rvReview.setAdapter(mReviewAdapter);

        // Show a message when offline
        showOfflineMessage(isOnline());

        return rootView;
    }

    /**
     * Handles RecyclerView item clicks to open a website that displays the user review.
     *
     * @param url The URL that displays the user review
     */
    @Override
    public void onItemClick(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }

    /**
     * This method will make the message that says no reviews found visible and
     * hide the View for the review data
     */
    private void showNoReviewsMessage() {
        // First, hide the currently visible data
        mReviewBinding.rvReview.setVisibility(View.INVISIBLE);
        // Then, show a message that says no reviews found
        mReviewBinding.tvNoReviews.setVisibility(View.VISIBLE);
    }

    /**
     * Make the offline message visible and hide the review View when offline
     *
     * @param isOnline True when connected to the network
     */
    private void showOfflineMessage(boolean isOnline) {
        if (isOnline) {
            // First, hide the offline message
            mReviewBinding.tvOffline.setVisibility(View.INVISIBLE);
            // Then, make sure the review data is visible
            mReviewBinding.rvReview.setVisibility(View.VISIBLE);
        } else {
            // First, hide the currently visible data
            mReviewBinding.rvReview.setVisibility(View.INVISIBLE);
            // Then, show an offline message
            mReviewBinding.tvOffline.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Check if there is the network connectivity
     *
     * @return true if connected to the network
     */
    private boolean isOnline() {
        // Get a reference to the ConnectivityManager to check the state of network connectivity
        ConnectivityManager connectivityManager = (ConnectivityManager)
                getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }
}
