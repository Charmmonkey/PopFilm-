package com.example.android.popfilms;

import android.animation.ObjectAnimator;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by jerye on 12/28/2016.
 * ClickableViewHolder is the custom ViewHolder class for RecyclerView.
 * Using switch cases, it populates the RecyclerView items and initiates different behaviors when each item is clicked
 */

public class ClickableViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private static final String LOG_TAG = ClickableViewHolder.class.getSimpleName();

    // Bins for the views
    public TextView trailerNameTextView;
    public TextView reviewAuthorTextView;
    public TextView reviewContentTextView;
    public ImageView trailerThumbnailImageView;
    public ImageView reviewButton;
    public ImageView reviewLeftButton;
    public ImageView reviewRightButton;
    public CardView reviewCardView;
    // Matcher id for the switch-case
    private final int REVIEW_ID = 100;
    private final int TRAILER_ID = 101;
    private int mMatcher;

    // Empty bins
    private ArrayList<String[]> mDataset;
    private Context mContext;

    // Identifier for whether the Review TextView is expanded. Initially collapsed (false)
    private boolean isExpanded = false;

    public ClickableViewHolder(final View itemView, int match, ArrayList<String[]> data, Context context) {
        super(itemView);

        // Pass in necessary data from the RecyclerAdapter
        mMatcher = match;
        mDataset = data;
        mContext = context;

        // Switch cases finds the appropriate views.
        switch (mMatcher) {
            case REVIEW_ID:
                reviewCardView = (CardView) itemView.findViewById(R.id.review_card_view);
                reviewAuthorTextView = (TextView) itemView.findViewById(R.id.review_author);
                reviewContentTextView = (TextView) itemView.findViewById(R.id.review_content);
                reviewButton = (ImageView) itemView.findViewById(R.id.review_button);
                reviewLeftButton = (ImageView) itemView.findViewById(R.id.review_left_button);
                reviewRightButton = (ImageView) itemView.findViewById(R.id.review_right_button);
                reviewContentTextView.setMaxLines(5);
                reviewCardView.setOnClickListener(this);
                break;
            case TRAILER_ID:
                trailerNameTextView = (TextView) itemView.findViewById(R.id.trailer_name);
                trailerThumbnailImageView = (ImageView) itemView.findViewById(R.id.trailer_thumbnail);
                trailerThumbnailImageView.setOnClickListener(this);

                break;
        }
    }


    @Override
    public void onClick(View v) {

        // Switch cases determines what happens when different items are clicked
        switch (mMatcher) {

            // For items in Review RecyclerView, TextView expands/collapse to show full/partial content
            case REVIEW_ID:
                reviewContentTextView.post(new Runnable() {
                                               @Override
                                               public void run() {
                                                   if (isExpanded == false) {
                                                       expandTextView(reviewContentTextView);
                                                       reviewButton.setImageResource(R.drawable.ic_keyboard_arrow_up_white_24dp);
                                                       isExpanded = true;
                                                   } else {
                                                       collapseTextView(reviewContentTextView, 5);
                                                       reviewButton.setImageResource(R.drawable.ic_keyboard_arrow_down_white_24dp);

                                                       isExpanded = false;
                                                   }
                                               }
                                           }
                );

                break;

            // For items in Trailer RecyclerView, a corresponding YouTube video is opened
            case TRAILER_ID:
                String videoKey = mDataset.get(getAdapterPosition())[1];
                watchYoutubeVideo(videoKey);
                break;
        }
    }

    // Method to start an Intent to open YouTube video
    public void watchYoutubeVideo(String id) {
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Utility.buildYouTubeAppUri(id));
        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                Utility.buildYouTubeWebUri(id));

        // Open from either the YouTube app or website
        try {
            mContext.startActivity(appIntent);
        } catch (ActivityNotFoundException ex) {
            mContext.startActivity(webIntent);
        }
    }


    // Method to expand TextView using an ObjectAnimator
    private void expandTextView(TextView tv) {
        Log.v(LOG_TAG, " REVIEW_ID expanded method");

        ObjectAnimator animation = ObjectAnimator.ofInt(tv, "maxLines", tv.getLineCount());
        animation.setDuration(200).start();
    }

    // Method to collapse TextView
    private void collapseTextView(TextView tv, int numLines) {
        Log.v(LOG_TAG, " REVIEW_ID collapsed method");

        ObjectAnimator animation = ObjectAnimator.ofInt(tv, "maxLines", numLines);
        animation.setDuration(200).start();
    }

}
