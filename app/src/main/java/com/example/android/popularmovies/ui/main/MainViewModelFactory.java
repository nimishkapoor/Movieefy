

package com.example.android.popularmovies.ui.main;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import com.example.android.popularmovies.data.MovieRepository;

/**
 * Factory method that allows us to create a ViewModel with a constructor that takes a
 * {@link MovieRepository} and String sortCriteria
 */
public class MainViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final MovieRepository mRepository;
    private final String mSortCriteria;

    public MainViewModelFactory (MovieRepository repository, String sortCriteria) {
        this.mRepository = repository;
        this.mSortCriteria = sortCriteria;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        //noinspection unchecked
        return (T) new MainActivityViewModel(mRepository, mSortCriteria);
    }
}
