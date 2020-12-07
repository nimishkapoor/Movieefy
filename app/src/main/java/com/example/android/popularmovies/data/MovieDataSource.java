
package com.example.android.popularmovies.data;

import android.arch.paging.PageKeyedDataSource;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.android.popularmovies.model.Movie;
import com.example.android.popularmovies.model.MovieResponse;
import com.example.android.popularmovies.utilities.Constant;
import com.example.android.popularmovies.utilities.Controller;
import com.example.android.popularmovies.utilities.FirebaseHelper;
import com.example.android.popularmovies.utilities.GenreIdMapper;
import com.example.android.popularmovies.utilities.TheMovieApi;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.android.popularmovies.utilities.Constant.NEXT_PAGE_KEY_TWO;
import static com.example.android.popularmovies.utilities.Constant.PREVIOUS_PAGE_KEY_ONE;
import static com.example.android.popularmovies.utilities.Constant.RESPONSE_CODE_API_STATUS;

/**
 * The MovieDataSource is the base class for loading snapshots of movie data into a given PagedList,
 * which is backed by the network. Since the TMDb API includes a key with each page load, extend
 * from PageKeyedDataSource.
 *
tps://codelabs.developers.google.com/codelabs/android-paging/index.html#2"
 */
public class MovieDataSource extends PageKeyedDataSource<Integer, Movie> {

    /** Tag for logging */
    private static final String TAG = MovieDataSource.class.getSimpleName();

    /** Member variable for TheMovieApi interface */
    private TheMovieApi mTheMovieApi;

    /** String for the sort order of the movies */
    private String mSortCriteria;

    public static boolean isRecommendations = false;

    public MovieDataSource(String sortCriteria) {
        mTheMovieApi = Controller.getClient().create(TheMovieApi.class);
        mSortCriteria = sortCriteria;
    }



    /**
     * This method is called first to initialize a PageList with data.
     */
    @Override
    public void loadInitial(@NonNull LoadInitialParams<Integer> params,
                            @NonNull final LoadInitialCallback<Integer, Movie> callback) {
        mTheMovieApi.getMovies(mSortCriteria, Constant.API_KEY, Constant.LANGUAGE, Constant.PAGE_ONE)
                .enqueue(new Callback<MovieResponse>() {
                    @Override
                    public void onResponse(Call<MovieResponse> call, final Response<MovieResponse> response) {
                        if (response.isSuccessful()) {
                            if (isRecommendations){
                                final List<Movie> movieList = new ArrayList<>();
                                final Set<Integer> ids = new HashSet<>();
                                FirebaseDatabase firebaseDatabase = FirebaseHelper.getFirebaseDatabaseObject();
                                final DatabaseReference databaseReference = firebaseDatabase.getReference("users_liked_genres");

                                final ValueEventListener valueEventListener = new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        DataSnapshot reviewsSnapshot = dataSnapshot.child(FirebaseHelper.getFirebaseAuthObject().getUid());
                                        Map<String, String> map = (HashMap<String, String>)reviewsSnapshot.getValue();
                                        Set<String> genreSet = map.keySet();

                                        for (final Movie m: response.body().getMovieResults()){
                                            Log.d("lllll", String.valueOf(response.body().getMovieResults().size()));

                                            String genre = "";
                                            for (int i = 0; i < m.getmGenreIds().size(); i++){
                                                genre = genre + GenreIdMapper.mapIdToGenre(m.getmGenreIds().get(i))+", ";
                                            }
                                            final String genres = genre.substring(0, genre.lastIndexOf(","));
                                            Log.d("lllll567", genres);

                                            final String[] genreList = genres.split(",");
                                            Log.d("lllll0000", genreList[0]);
                                            if (genreSet.contains(genres) || genreSet.contains(new StringBuilder(genres).reverse().toString())){
                                                m.setScore(genreList.length);
                                                movieList.add(m);
                                            }

                                            for (int i = 0; i < genreList.length; i++){
                                                if (i == genreList.length - 1){
                                                    if (genreSet.contains(genreList[0]+","+genreList[i]) || genreSet.contains(new StringBuilder(genreList[0]+","
                                                            +genreList[i]).reverse())){
                                                        Log.d("bhaihojaaaaaaaaa", genreList[0]+","+genreList[i]);
                                                        if (!(ids.contains(m.getId()))){
                                                            m.setScore(2);
                                                            movieList.add(m);
                                                            ids.add(m.getId());
                                                        }
                                                    } else if (genreSet.contains(genreList[i])){
                                                        if (!(ids.contains(m.getId()))){
                                                            m.setScore(1);
                                                            movieList.add(m);
                                                            ids.add(m.getId());
                                                        }
                                                    }
                                                } else {
                                                    Log.d("ll000000999999", genreList[i]);
                                                    Iterator iterator = genreSet.iterator();
                                                    while (iterator.hasNext()){
                                                        String iy = (String)iterator.next();
                                                        if (iy.contains(genreList[i]+","+genreList[i+1]) || iy.contains(new StringBuilder(genreList[0]+","
                                                                +genreList[i]).reverse().toString())){
                                                            Log.d("lll090909", m.getmGenreIds().toString());
                                                            Log.d("pakhija", genreList[i]+", "+genreList[i+1]+"222");
                                                            Log.d("pakhijaaaa", genreSet.toString());
                                                            if (!(ids.contains(m.getId()))){
                                                                m.setScore(2);
                                                                movieList.add(m);
                                                                ids.add(m.getId());
                                                            }
                                                        } else if (iy.contains(genreList[i])){
                                                            Log.d("pakhija", genreList[i]+", "+genreList[i+1]+"111");
                                                            Log.d("pakhijaaaa", genreSet.toString());
                                                            if (!(ids.contains(m.getId()))){
                                                                m.setScore(1);
                                                                movieList.add(m);
                                                                ids.add(m.getId());
                                                            }
                                                        }
                                                    }

                                                }
                                            }
                                        }
                                        Collections.sort(movieList);
                                        for (Movie m: movieList){
                                            Log.d("bhaihoja", m.getOriginalTitle()+"-----"+m.getScore());
                                        }
                                        Log.d("lllllllll", String.valueOf(movieList.size()));
                                        callback.onResult(movieList,
                                                PREVIOUS_PAGE_KEY_ONE, NEXT_PAGE_KEY_TWO);

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                };

                                databaseReference.addValueEventListener(valueEventListener);
                            } else {
                                Log.d("genreidsss", String.valueOf(response.body().getMovieResults().get(0).getmGenreIds().size()));
                                callback.onResult(response.body().getMovieResults(),
                                        PREVIOUS_PAGE_KEY_ONE, NEXT_PAGE_KEY_TWO);
                            }

                        } else if (response.code() == RESPONSE_CODE_API_STATUS) {
                            Log.e(TAG, "Invalid Api key. Response code: " + response.code());
                        } else {
                            Log.e(TAG, "Response Code: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<MovieResponse> call, Throwable t) {
                        Log.e(TAG, "Failed initializing a PageList: " + t.getMessage());
                    }
                });
    }

    /**
     * Prepend page with the key specified by LoadParams.key
     */
    @Override
    public void loadBefore(@NonNull LoadParams<Integer> params,
                           @NonNull LoadCallback<Integer, Movie> callback) {

    }

    /**
     * Append page with the key specified by LoadParams.key
     */
    @Override
    public void loadAfter(@NonNull LoadParams<Integer> params,
                          @NonNull final LoadCallback<Integer, Movie> callback) {

        final int currentPage = params.key;

        mTheMovieApi.getMovies(mSortCriteria, Constant.API_KEY, Constant.LANGUAGE, currentPage)
                .enqueue(new Callback<MovieResponse>() {
                    @Override
                    public void onResponse(Call<MovieResponse> call, final Response<MovieResponse> response) {
                        if (response.isSuccessful()) {
                            final int nextKey = currentPage + 1;
                            if (isRecommendations){
                                final List<Movie> movieList = new ArrayList<>();
                                final Set<Integer> ids = new HashSet<>();
                                FirebaseDatabase firebaseDatabase = FirebaseHelper.getFirebaseDatabaseObject();
                                final DatabaseReference databaseReference = firebaseDatabase.getReference("users_liked_genres");

                                final ValueEventListener valueEventListener = new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        DataSnapshot reviewsSnapshot = dataSnapshot.child(FirebaseHelper.getFirebaseAuthObject().getUid());
                                        Map<String, String> map = (HashMap<String, String>)reviewsSnapshot.getValue();
                                        Set<String> genreSet = map.keySet();
                                        Log.d("lllllllllllllllllll", String.valueOf(genreSet.iterator().next()));

                                        for (final Movie m: response.body().getMovieResults()){
                                            Log.d("lllll", String.valueOf(response.body().getMovieResults().size()));

                                            String genre = "";
                                            Log.d("ppppppp", String.valueOf(m.getmGenreIds().size()) +"--"+m.getOriginalTitle());
                                            for (int i = 0; i < m.getmGenreIds().size(); i++){
                                                genre = genre + GenreIdMapper.mapIdToGenre(m.getmGenreIds().get(i))+", ";
                                            }
                                            if (!genre.isEmpty()){
                                                final String genres = genre.substring(0, genre.lastIndexOf(","));
                                                Log.d("lllll567", genres);

                                                final String[] genreList = genres.split(",");
                                                Log.d("lllll0000", genreList.toString());
                                                if (genreSet.contains(genres)) {
                                                    m.setScore(genreList.length);
                                                    movieList.add(m);
                                                }
                                                for (int i = 0; i < genreList.length; i++){
                                                    if (i == genreList.length - 1){
                                                        if (genreSet.contains(genreList[0]+","+genreList[i]) || genreSet.contains(new StringBuilder(genreList[0]+","
                                                                +genreList[i]).reverse().toString())){
                                                            if (!(ids.contains(m.getId()))){
                                                                m.setScore(2);
                                                                movieList.add(m);
                                                                ids.add(m.getId());
                                                            }
                                                        } else if (genreSet.contains(genreList[i])){
                                                            if (!(ids.contains(m.getId()))){
                                                                m.setScore(1);
                                                                movieList.add(m);
                                                                ids.add(m.getId());
                                                            }
                                                        }
                                                    } else {
                                                        Log.d("ll000000999999", genreList[i]);
                                                        Iterator iterator = genreSet.iterator();
                                                        while (iterator.hasNext()){
                                                            String iy = (String)iterator.next();
                                                            if (iy.contains(genreList[i]+","+genreList[i+1]) || iy.contains(new StringBuilder(genreList[0]+","
                                                                    +genreList[i]).reverse().toString())){
                                                                Log.d("lll090909", m.getmGenreIds().toString());
                                                                if (!(ids.contains(m.getId()))){
                                                                    m.setScore(2);
                                                                    movieList.add(m);
                                                                    ids.add(m.getId());
                                                                }
                                                            } else if (iy.contains(genreList[i])){
                                                                if (!(ids.contains(m.getId()))){
                                                                    m.setScore(1);
                                                                    movieList.add(m);
                                                                    ids.add(m.getId());
                                                                }
                                                            }
                                                        }

                                                    }
                                                }
                                            }
                                        }
                                        Collections.sort(movieList);
                                        for (Movie m: movieList){
                                            Log.d("bhaihoja", m.getOriginalTitle()+"-----"+m.getScore());
                                        }
                                        Log.d("lllllllll", String.valueOf(movieList.size()));
                                        callback.onResult(movieList,
                                                nextKey);

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                };

                                databaseReference.addValueEventListener(valueEventListener);

                            }else {
                                callback.onResult(response.body().getMovieResults(), nextKey);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<MovieResponse> call, Throwable t) {
                        Log.e(TAG, "Failed appending page: " + t.getMessage());
                    }
                });
    }
}
