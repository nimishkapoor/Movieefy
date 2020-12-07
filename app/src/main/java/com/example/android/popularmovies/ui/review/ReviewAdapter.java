

package com.example.android.popularmovies.ui.review;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.databinding.ReviewListItemBinding;
import com.example.android.popularmovies.model.Review;

import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    /** Member variable for the list of {@link Review}s */
    private List<Review> mReviews;

    /** An on-click handler that we've defined to make it easy for a Fragment to interface with
     * our RecyclerView
     */
    private final ReviewAdapterOnClickHandler mOnClickHandler;

    /**
     * The interface that receives onClick messages.
     */
    public interface ReviewAdapterOnClickHandler {
        void onItemClick(String url);
    }

    /**
     * Constructor for ReviewAdapter that accepts a list of reviews to display
     *
     * @param reviews List of {@link Review}s
     * @param onClickHandler The on-click handler for this adapter. This single handler is called
     *                       when an item is clicked.
     */
    public ReviewAdapter(List<Review> reviews, ReviewAdapterOnClickHandler onClickHandler) {
        mReviews = reviews;
        mOnClickHandler = onClickHandler;
    }

    /**
     * This gets called when each new ViewHolder is created.
     *
     *  @return A new ReviewViewHolder that holds the ReviewListItemBinding
     */
    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        ReviewListItemBinding reviewItemBinding = DataBindingUtil
                .inflate(layoutInflater, R.layout.review_list_item, viewGroup, false);
        return new ReviewViewHolder(reviewItemBinding);
    }

    /**
     * OnBindViewHolder is called by the RecyclerView to display the data at the specified
     * position.
     */
    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        Review review = mReviews.get(position);
        holder.bind(review);
    }

    /**
     * This method simply return the number of items to display. It is used behind the scenes
     * to help layout our Views and for animations.
     *
     * @return The number of reviews
     */
    @Override
    public int getItemCount() {
        if (null == mReviews) return 0;
      //  Log.e("sizzzzze", String.valueOf(mReviews.size()));
        return mReviews.size();
    }

    /**
     * This method is to add a list of {@link Review}s
     */
    public void addAll(List<Review> reviews) {
        Log.e("sizzzzze89000", String.valueOf(reviews.size()));

        mReviews.clear();
        mReviews.addAll(reviews);
        notifyDataSetChanged();
    }

    /**
     * Cache of the children views for a review list item.
     */
    public class ReviewViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        /** This field is used for data binding */
        ReviewListItemBinding mReviewItemBinding;

        /**
         * Constructor for our ViewHolder
         *
         * @param reviewItemBinding Used to access the layout's variables and views
         */
        ReviewViewHolder(ReviewListItemBinding reviewItemBinding) {
            super(reviewItemBinding.getRoot());
            mReviewItemBinding = reviewItemBinding;

            // Call setOnClickListener on  the View passed into the constructor
            itemView.setOnClickListener(this);
        }

        /**
         * This method will take a Review object as input and use that review to display the appropriate
         * text within a list item
         */
        void bind(Review review) {
            // Set author and content of review to the TextView
            mReviewItemBinding.setReview(review);
        }

        /**
         * Called by the child views during a click.
         *
         * @param v The View that was clicked
         */
        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            Review review = mReviews.get(adapterPosition);
            mOnClickHandler.onItemClick(review.getUrl());
        }
    }
}
