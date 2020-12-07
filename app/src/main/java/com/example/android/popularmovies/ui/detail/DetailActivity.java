

package com.example.android.popularmovies.ui.detail;

import android.app.AlertDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.example.android.popularmovies.AppExecutors;
import com.example.android.popularmovies.R;
import com.example.android.popularmovies.Ranking.Document;
import com.example.android.popularmovies.Ranking.RankingHelper;
import com.example.android.popularmovies.Ranking.VectorSpaceModelImp;
import com.example.android.popularmovies.data.MovieDatabase;
import com.example.android.popularmovies.data.MovieEntry;
import com.example.android.popularmovies.databinding.ActivityDetailBinding;
import com.example.android.popularmovies.model.SentimentValue;
import com.example.android.popularmovies.model.UserReview;
import com.example.android.popularmovies.ui.info.InformationFragment;
import com.example.android.popularmovies.ui.trailer.TrailerFragment;
import com.example.android.popularmovies.model.Genre;
import com.example.android.popularmovies.model.Movie;
import com.example.android.popularmovies.model.MovieDetails;
import com.example.android.popularmovies.model.Video;
import com.example.android.popularmovies.utilities.Controller;
import com.example.android.popularmovies.utilities.FirebaseHelper;
import com.example.android.popularmovies.utilities.FormatUtils;
import com.example.android.popularmovies.utilities.InjectorUtils;
import com.example.android.popularmovies.ui.main.FavViewModel;
import com.example.android.popularmovies.ui.main.FavViewModelFactory;
import com.example.android.popularmovies.utilities.SentimentApi;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.android.popularmovies.utilities.Constant.BACKDROP_FILE_SIZE;
import static com.example.android.popularmovies.utilities.Constant.CAST;
import static com.example.android.popularmovies.utilities.Constant.EXTRA_MOVIE;
import static com.example.android.popularmovies.utilities.Constant.IMAGE_BASE_URL;
import static com.example.android.popularmovies.utilities.Constant.RELEASE_YEAR_BEGIN_INDEX;
import static com.example.android.popularmovies.utilities.Constant.RELEASE_YEAR_END_INDEX;
import static com.example.android.popularmovies.utilities.Constant.RESULTS_GENRE;
import static com.example.android.popularmovies.utilities.Constant.RESULTS_RELEASE_YEAR;
import static com.example.android.popularmovies.utilities.Constant.RESULTS_RUNTIME;
import static com.example.android.popularmovies.utilities.Constant.SHARE_INTENT_TYPE_TEXT;
import static com.example.android.popularmovies.utilities.Constant.SHARE_URL;
import static com.example.android.popularmovies.utilities.Constant.YOUTUBE_BASE_URL;

/**
 * This activity is responsible for displaying the details for a selected movie.
 */
public class DetailActivity extends AppCompatActivity implements
        InformationFragment.OnInfoSelectedListener, TrailerFragment.OnTrailerSelectedListener,
        InformationFragment.OnViewAllSelectedListener {

    /** Tag for logging */
    public static final String TAG = DetailActivity.class.getSimpleName();

    /** ViewModel for Favorites */
    private FavViewModel mFavViewModel;

    /** True when the movie is in favorites collection, otherwise false */
    private boolean mIsInFavorites;

    /** Member variable for the MovieDatabase*/
    private MovieDatabase mDb;

    /** Member variable for the MovieEntry */
    private MovieEntry mMovieEntry;

    /** Movie object */
    private Movie mMovie;

    /** This field is used for data binding */
    private ActivityDetailBinding mDetailBinding;

    /** The first trailer's YouTube URL */
    private String mFirstVideoUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDetailBinding = DataBindingUtil.setContentView(this, R.layout.activity_detail);

        // Get the movie data from the MainActivity. The movie data includes the movie id, original title,
        // title, poster path, overview, vote average, release date, and backdrop path.
        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra(EXTRA_MOVIE)) {
                Bundle b = intent.getBundleExtra(EXTRA_MOVIE);
                mMovie = b.getParcelable(EXTRA_MOVIE);
            }
        }

        // Get the MovieDatabase instance
        mDb = MovieDatabase.getInstance(getApplicationContext());
        // Check if the movie is in the favorites collection or not
        mIsInFavorites = isInFavoritesCollection();

        // Setup the UI
        setupUI();

        if (savedInstanceState != null) {
            mDetailBinding.pbDetailLoadingIndicator.setVisibility(View.GONE);

            String resultRuntime = savedInstanceState.getString(RESULTS_RUNTIME);
            String resultReleaseYear = savedInstanceState.getString(RESULTS_RELEASE_YEAR);
            String resultGenre = savedInstanceState.getString(RESULTS_GENRE);

            mDetailBinding.tvRuntime.setText(resultRuntime);
            mDetailBinding.tvReleaseYear.setText(resultReleaseYear);
            mDetailBinding.tvGenre.setText(resultGenre);
        }
    }

    /**
     *  This method is called from onCreate to setup the UI
     */
    private void setupUI() {
        // Show the up button in Collapsing Toolbar
        showUpButton();

        // Give the TabLayout the ViewPager
        mDetailBinding.tabLayout.setupWithViewPager(mDetailBinding.contentDetail.viewpager);
        // Set gravity for the TabLayout
        mDetailBinding.tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        mDetailBinding.reviewAdd.setImageResource(R.drawable.ic_add_black_24dp);


        // Create an adapter that knows which fragment should be shown on each page
        DetailPagerAdapter pagerAdapter = new DetailPagerAdapter(
                this, getSupportFragmentManager());
        // Set the adapter onto the ViewPager
        mDetailBinding.contentDetail.viewpager.setAdapter(pagerAdapter);

        // Show the title in the app bar when a CollapsingToolbarLayout is fully collapsed
        setCollapsingToolbarTitle();
        // Display the backdrop image
        loadBackdropImage();
        // Display title
        setTitle();

        // When it is online, show loading indicator, otherwise hide loading indicator.
        showLoading(isOnline());
        // When offline, show runtime, release year, and genre of the movie
        if (!isOnline()) {
            loadMovieDetailData();
        }
    }

    /**
     * This method is called when the fab button is clicked.
     * If the movie is not in the favorites collection, insert the movie data into the database.
     * Otherwise, delete the movie data from the database
     */
    public void onFavoriteClick(View view) {
        // Create a new MovieEntry
        mMovieEntry = getMovieEntry();

        if (!mIsInFavorites) {
            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    // Insert a movie to the MovieDatabase by using the movieDao
                    mDb.movieDao().insertMovie(mMovieEntry);
                }
            });

            // Show snack bar message "Added to your favorites collection"
            showSnackbarAdded();
        } else {
            mMovieEntry = mFavViewModel.getMovieEntry().getValue();
            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    // Delete a movie from the MovieDatabase by using the movieDao
                    mDb.movieDao().deleteMovie(mMovieEntry);
                }
            });

            // Show snack bar message "Removed from your favorites collection"
            showSnackbarRemoved();
        }
    }

    public void onAddReviewClick(View view){

        View view1 = getLayoutInflater().inflate(R.layout.dialog_add_review, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(view1.getContext(), R.style.AlertDialog_Dark);
        builder.setView(view1);
        final EditText editText = (EditText) view1.findViewById(R.id.new_review_text);
        //editText.setHintTextColor();
        final AlertDialog dialog = builder.create();
        //dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorAccent));
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK".toUpperCase(), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                final String reviewText = editText.getText().toString().trim();
                if(!reviewText.isEmpty()){
                    getReviewSentiment(reviewText);
                    final FirebaseAuth firebaseAuth = FirebaseHelper.getFirebaseAuthObject();
                    UserReview userReview = new UserReview(String.valueOf(mMovie.getId()), reviewText);
                    FirebaseDatabase firebaseDatabase = FirebaseHelper.getFirebaseDatabaseObject();
                    DatabaseReference databaseReference = firebaseDatabase.getReference("user_reviews");
                    databaseReference.child(firebaseAuth.getUid()).child(mMovie.getTitle()).setValue(userReview.getReview());
                    final DatabaseReference databaseReference2 = firebaseDatabase.getReference("movie_reviews");
                    DatabaseReference databaseReference3 = firebaseDatabase.getReference("users");
                    final ValueEventListener valueEventListener = new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            DataSnapshot reviewsSnapshot = dataSnapshot.child(firebaseAuth.getUid());
                            Map<String, Object> reviewData = (Map<String, Object>)reviewsSnapshot.getValue();
                            String nameuser = (String)reviewData.get("username");
                            databaseReference2.child(String.valueOf(mMovie.getId())).child(nameuser).setValue(reviewText);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    };

                    databaseReference3.addValueEventListener(valueEventListener);
                    dialog.dismiss();
                }
            }
        });
        //dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorAccent));

        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "CANCEL".toUpperCase(), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialog.dismiss();
            }
        });
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialog.show();
    }

    public void getReviewSentiment(final String review){
        final String[] sentimentValue = {""};
        Log.d("reviewwww", getMovieEntry().getGenre());

        SentimentApi sentimentApi = Controller.getClientForReviewSentiment().create(SentimentApi.class);
        sentimentApi.getSentimentValue(review).enqueue(new Callback<SentimentValue>() {
            @Override
            public void onResponse(Call<SentimentValue> call, Response<SentimentValue> response) {
                Log.d("reviewwwwwwwwwwww", response.body().getSentiment_value());

                sentimentValue[0] = response.body().getSentiment_value();
                Log.d("papapapapapapap", sentimentValue.toString());
                ArrayList<String> positive_words = (ArrayList)response.body().getPositive_words();
                VectorSpaceModelImp.initialize();
                ArrayList<Document> documents = new ArrayList<>();
                documents.add(new Document(45, review));
                HashMap<String, Double> posWordsscoreTFIDF = VectorSpaceModelImp.exec(documents, positive_words);
                RankingHelper.addToDb(posWordsscoreTFIDF);
                if (sentimentValue[0].equals("1")){
                    FirebaseDatabase firebaseDatabase = FirebaseHelper.getFirebaseDatabaseObject();
                    final DatabaseReference databaseReference = firebaseDatabase.getReference("users_liked_genres");

                    final ValueEventListener valueEventListener = new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            DataSnapshot reviewsSnapshot = dataSnapshot.child(FirebaseHelper.getFirebaseAuthObject().getUid()).
                                    child(getMovieEntry().getGenre());
                            if (reviewsSnapshot != null){
                                Log.e("mmmmmmmm", reviewsSnapshot.toString());
                                if (reviewsSnapshot.getValue() != null){
                                    int score = Integer.parseInt((String)reviewsSnapshot.getValue());
                                    databaseReference.child(FirebaseHelper.getFirebaseAuthObject().getUid()).child(getMovieEntry().getGenre()).setValue(String.valueOf(score+1));


                                } else databaseReference.child(FirebaseHelper.getFirebaseAuthObject().getUid()).child(getMovieEntry().getGenre()).setValue(String.valueOf(1));


                            } else {
                                databaseReference.child(FirebaseHelper.getFirebaseAuthObject().getUid()).child(getMovieEntry().getGenre()).setValue(String.valueOf(1));

                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    };

                    databaseReference.addListenerForSingleValueEvent(valueEventListener);
                }

            }

            @Override
            public void onFailure(Call<SentimentValue> call, Throwable t) {
                Log.e("errooooooorrrrr", t.getMessage());

            }
        });
    }


    /**
     * Returns a MovieEntry
     */
    private MovieEntry getMovieEntry() {
        String runtime = mDetailBinding.tvRuntime.getText().toString();
        String releaseYear = mDetailBinding.tvReleaseYear.getText().toString();
        String genre = mDetailBinding.tvGenre.getText().toString();

        // Create a MovieEntry
        mMovieEntry = new MovieEntry(mMovie.getId(), mMovie.getOriginalTitle(), mMovie.getTitle(),
                mMovie.getPosterPath(), mMovie.getOverview(), mMovie.getVoteAverage(),
                mMovie.getReleaseDate(), mMovie.getBackdropPath(), new Date(),
                runtime, releaseYear, genre);

        return mMovieEntry;
    }

    /**
     * When offline, display runtime, release year, and genre of the movie.
     */
    private void loadMovieDetailData() {
        FavViewModelFactory factory = InjectorUtils.provideFavViewModelFactory(
                DetailActivity.this, mMovie.getId());
        mFavViewModel = ViewModelProviders.of(this, factory).get(FavViewModel.class);

        mFavViewModel.getMovieEntry().observe(this, new Observer<MovieEntry>() {
            @Override
            public void onChanged(@Nullable MovieEntry movieEntry) {
                if (movieEntry != null) {
                    mDetailBinding.tvRuntime.setText(movieEntry.getRuntime());
                    mDetailBinding.tvReleaseYear.setText(movieEntry.getReleaseYear());
                    mDetailBinding.tvGenre.setText(movieEntry.getGenre());
                }
            }
        });
    }

    /**
     * Return true and set a favoriteFab image to full heart image if the movie is in favorites collection.
     * Otherwise return false and set favoriteFab image to border heart image.
     */
    private boolean isInFavoritesCollection() {
        // Get the FavViewModel from the factory
        FavViewModelFactory factory = InjectorUtils.provideFavViewModelFactory(
                DetailActivity.this, mMovie.getId());
        mFavViewModel = ViewModelProviders.of(this, factory).get(FavViewModel.class);

        // Changes the favoriteFab image based on whether or not the movie exists
        mFavViewModel.getMovieEntry().observe(this, new Observer<MovieEntry>() {
            @Override
            public void onChanged(@Nullable MovieEntry movieEntry) {
                if (mFavViewModel.getMovieEntry().getValue() == null) {
                    mDetailBinding.fab.setImageResource(R.drawable.favorite_border);
                    mIsInFavorites = false;
                } else {
                    mDetailBinding.fab.setImageResource(R.drawable.favorite);
                    mIsInFavorites = true;
                }
            }
        });
        return mIsInFavorites;
    }

    /**
     * Show a snackbar message when a movie added to MovieDatabase
     *
     * Reference: @see "https://stackoverflow.com/questions/34020891/how-to-change-background-color-of-the-snackbar"
     */
    private void showSnackbarAdded() {
        Snackbar snackbar = Snackbar.make(
                mDetailBinding.coordinator, R.string.snackbar_added, Snackbar.LENGTH_SHORT);
        // Set background color of the snackbar
        View sbView = snackbar.getView();
        sbView.setBackgroundColor(Color.WHITE);
        // Set text color of the snackbar
        TextView textView = sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.BLACK);
        snackbar.show();
    }

    /**
     * Show a snackbar message when a movie removed from MovieDatbase
     */
    private void showSnackbarRemoved() {
        Snackbar snackbar = Snackbar.make(
                mDetailBinding.coordinator, R.string.snackbar_removed, Snackbar.LENGTH_SHORT);
        // Set background color of the snackbar
        View sbView = snackbar.getView();
        sbView.setBackgroundColor(Color.WHITE);
        // Set background color of the snackbar
        TextView textView = sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.BLACK);
        snackbar.show();
    }

    /**
     * Define the behavior for onTrailerSelected
     */
    @Override
    public void onTrailerSelected(final Video video) {
        // Display play circle image button
        mDetailBinding.ivPlayCircle.setVisibility(View.VISIBLE);

        // Get the key of the first video
        String firstVideoKey = video.getKey();
        // The complete the first trailer's YouTube URL
        mFirstVideoUrl = YOUTUBE_BASE_URL + firstVideoKey;

        mDetailBinding.ivPlayCircle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // When the user clicks the play circle button on the backdrop image,
                // launch the trailer using intent
                launchTrailer(mFirstVideoUrl);
            }
        });
    }

    /**
     * Use Intent to open a YouTube link in either the native app or a web browser of choice
     *
     * @param videoUrl The first trailer's YouTube URL
     */
    private void launchTrailer(String videoUrl) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(videoUrl));
        if (intent.resolveActivity(this.getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    /**
     * Show an up button in Collapsing Toolbar
     */
    private void showUpButton() {
        setSupportActionBar(mDetailBinding.toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
    }

    /**
     * Display the backdrop image
     */
    private void loadBackdropImage() {
        // Get the backdrop path
        String backdropPath = mMovie.getBackdropPath();
        // The complete backdrop image url
        String backdrop = IMAGE_BASE_URL + BACKDROP_FILE_SIZE + backdropPath;
        // Load image with Picasso library
        Picasso.with(this)
                .load(backdrop)
                .error(R.drawable.photo)
                .into(mDetailBinding.ivBackdrop);
    }

    /**
     * The {@link Movie} object contains information, such as ID, original title, title, poster path,
     * vote average, release date, and backdrop path. Get the title from the {@link Movie} and
     * set the title to the TextViews
     */
    private void setTitle() {
        // Get title of the movie
        String title = mMovie.getTitle();
        // Set title to the TextView
        mDetailBinding.tvDetailTitle.setText(title);
    }

    /**
     * Get the release date from the {@link Movie} and display the release year. This method is
     * called as soon as the loading indicator is gone.
     */
    private void showReleaseYear() {
        // Get the release date of the movie (e.g. "2018-06-20")
        String releaseDate = mMovie.getReleaseDate();
        // Get the release year (e.g. "2018")
        String releaseYear = releaseDate.substring(RELEASE_YEAR_BEGIN_INDEX, RELEASE_YEAR_END_INDEX);
        // Set the release year to the TextView
        mDetailBinding.tvReleaseYear.setText(releaseYear);
    }

    /**
     * Show the title in the app bar when a CollapsingToolbarLayout is fully collapsed, otherwise hide the title.
     *
     * Reference: @see "https://stackoverflow.com/questions/31662416/show-collapsingtoolbarlayout-title-only-when-collapsed"
     */
    private void setCollapsingToolbarTitle() {
        // Set onOffsetChangedListener to determine when CollapsingToolbar is collapsed
        mDetailBinding.appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = true;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    // Show title when a CollapsingToolbarLayout is fully collapse
                    mDetailBinding.collapsingToolbarLayout.setTitle(mMovie.getTitle());
                    isShow = true;
                } else if (isShow) {
                    // Otherwise hide the title
                    mDetailBinding.collapsingToolbarLayout.setTitle(" ");
                    isShow = false;
                }
            }
        });
    }

    /**
     * Define the behavior for onInformationSelected
     * @param movieDetails The movie details contains information, such as budget, genre, runtime,
     *                    revenue, status, vote count, credits.
     */
    @Override
    public void onInformationSelected(MovieDetails movieDetails) {
        // Hide the loading indicator
        mDetailBinding.pbDetailLoadingIndicator.setVisibility(View.GONE);

        // As soon as the loading indicator is gone, show release year
        showReleaseYear();

        // Get the runtime of the movie from MovieDetails object
        int runtime = movieDetails.getRuntime();
        // Convert Minutes to Hours and Minutes (e.g. "118" -> "1h 58m") and set the runtime to the TextView
        mDetailBinding.tvRuntime.setText(FormatUtils.formatTime(this, runtime));

        // Get the genre of the movie from MovieDetails
        List<Genre> genres = movieDetails.getGenres();
        // Create an empty arrayList
        List<String> genresStrList = new ArrayList<>();
        // Iterate through the list of genres, and add genre name to the list of strings
        for (int i = 0; i < genres.size(); i++) {
            Genre genre = genres.get(i);
            // Get the genre name from the genre at ith position
            String genreName = genre.getGenreName();
            // Add genre name to the list of strings
            genresStrList.add(genreName);
        }
        // Join a string using a delimiter
        String genreStr = TextUtils.join(getString(R.string.delimiter_comma), genresStrList);
        // Display the genre
        mDetailBinding.tvGenre.setText(genreStr);
    }

    /**
     * When the arrow icon in the app bar is clicked, finishes DetailActivity.
     */
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case R.id.action_share:
                // Share movie information using share intent
                startActivity(createShareIntent());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Uses the ShareCompat Intent builder to create our share intent for sharing.
     * Return the newly created intent.
     *
     * @return The Intent to use to start our share.
     */
    private Intent createShareIntent() {
        // Text message to share
        String shareText = getString(R.string.check_out) + mMovie.getTitle()
                + getString(R.string.new_line) + SHARE_URL + mMovie.getId();
        // If there is the first trailer, add the first trailer's YouTube URL to the text message
        if (mFirstVideoUrl != null) {
            shareText += getString(R.string.new_line) + getString(R.string.youtube_trailer)
                    + mFirstVideoUrl;
        }

        // Create share intent
        Intent shareIntent = ShareCompat.IntentBuilder.from(this)
                .setType(SHARE_INTENT_TYPE_TEXT)
                .setText(shareText)
                .setChooserTitle(getString(R.string.chooser_title))
                .createChooserIntent();
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        return shareIntent;
    }

    /**
     * Persist the runtime, release year, and genre of the movie by saving the data in onSaveInstanceState.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        String resultRuntime = mDetailBinding.tvRuntime.getText().toString();
        outState.putString(RESULTS_RUNTIME, resultRuntime);

        String resultReleaseYear = mDetailBinding.tvReleaseYear.getText().toString();
        outState.putString(RESULTS_RELEASE_YEAR, resultReleaseYear);

        String resultGenre = mDetailBinding.tvGenre.getText().toString();
        outState.putString(RESULTS_GENRE, resultGenre);
    }

    /**
     * Switch to CastFragment in a ViewPager when "VIEW ALL" TextView is clicked in the DetailActivity
     */
    @Override
    public void onViewAllSelected() {
        mDetailBinding.contentDetail.viewpager.setCurrentItem(CAST);
    }

    /**
     * Check if there is the network connectivity
     *
     * @return true if connected to the network
     */
    private boolean isOnline() {
        // Get a reference to the ConnectivityManager to check the state of network connectivity
        ConnectivityManager connectivityManager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

    /**
     * When online, show loading indicator, otherwise hide loading indicator.
     *
     * @param isOnline true if connected to the network
     */
    private void showLoading(boolean isOnline) {
        if (!isOnline) {
            mDetailBinding.pbDetailLoadingIndicator.setVisibility(View.GONE);
        } else {
            mDetailBinding.pbDetailLoadingIndicator.setVisibility(View.VISIBLE);
        }
    }
}
