
package com.example.android.popularmovies.ui.review;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.example.android.popularmovies.data.MovieRepository;
import com.example.android.popularmovies.model.ReviewResponse;

/**
 * {@link ViewModel} for ReviewFragment
 */
public class ReviewViewModel extends ViewModel {
    private final MovieRepository mRepository;
    private final LiveData<ReviewResponse> mReviewResponse;

    public ReviewViewModel (MovieRepository repository, int movieId) {
        mRepository = repository;
        mReviewResponse = mRepository.getReviewResponse(movieId);
    }

    public LiveData<ReviewResponse> getReviewResponse() {
        return mReviewResponse;
    }
}
