
package com.example.android.popularmovies.ui.main;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.example.android.popularmovies.data.MovieEntry;
import com.example.android.popularmovies.data.MovieRepository;

/**
 * {@link ViewModel} for Favorites
 */
public class FavViewModel extends ViewModel {

    private final MovieRepository mRepository;
    private LiveData<MovieEntry> mMovieEntry;

    public FavViewModel(MovieRepository repository, int movieId) {
        mRepository = repository;
        mMovieEntry = mRepository.getFavoriteMovieByMovieId(movieId);
    }

    public LiveData<MovieEntry> getMovieEntry() {
        return mMovieEntry;
    }
}
