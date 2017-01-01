package com.example.android.popfilms;

import android.animation.ObjectAnimator;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by jerye on 12/28/2016.
 */

public class ClickableViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private static final String LOG_TAG = ClickableViewHolder.class.getSimpleName();

    public TextView trailerNameTextView;
    public TextView reviewAuthorTextView;
    public TextView reviewContentTextView;
    public ImageView trailerThumbnailImageView;

    private final int REVIEW_ID = 100;
    private final int TRAILER_ID = 101;

    private int mMatcher;

    private ArrayList<String[]> mDataset;
    private Context mContext;

    private boolean isExpanded = false;

    public ClickableViewHolder(final View itemView, int match, ArrayList<String[]> data, Context context) {
        super(itemView);
        mMatcher = match;
        mDataset = data;
        mContext = context;

        switch (mMatcher) {
            case REVIEW_ID:
                reviewAuthorTextView = (TextView) itemView.findViewById(R.id.review_author);
                reviewContentTextView = (TextView) itemView.findViewById(R.id.review_content);
                reviewContentTextView.setMaxLines(5);
                reviewContentTextView.setOnClickListener(this);
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

        switch (mMatcher) {
            case REVIEW_ID:
                Log.v(LOG_TAG, Boolean.toString(isExpanded));
                Log.v(LOG_TAG, " REVIEW_ID match");
                reviewContentTextView.post(new Runnable() {
                                               @Override
                                               public void run() {
                                                   Log.v(LOG_TAG, " REVIEW_ID runnable ");
                                                   if (isExpanded == false) {
                                                       Log.v(LOG_TAG, " REVIEW_ID collapsed");
                                                       expandTextView(reviewContentTextView);
                                                       isExpanded = true;
                                                       Log.v(LOG_TAG, Boolean.toString(isExpanded));
                                                   } else {
                                                       Log.v(LOG_TAG, " REVIEW_ID expanded");
                                                       collapseTextView(reviewContentTextView, 5);
                                                       isExpanded = false;
                                                       Log.v(LOG_TAG, Boolean.toString(isExpanded));

                                                   }


                                               }
                                           }
                );
                break;
            case TRAILER_ID:
                String videoKey = mDataset.get(getAdapterPosition())[1];
                watchYoutubeVideo(videoKey);
                break;
        }
    }


    public void watchYoutubeVideo(String id) {
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Utility.buildYouTubeAppUri(id));
        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                Utility.buildYouTubeWebUri(id));
        try {
            mContext.startActivity(appIntent);
        } catch (ActivityNotFoundException ex) {
            mContext.startActivity(webIntent);
        }
    }

    private void expandTextView(TextView tv) {
        Log.v(LOG_TAG, " REVIEW_ID expanded method");

        ObjectAnimator animation = ObjectAnimator.ofInt(tv, "maxLines", tv.getLineCount());
        animation.setDuration(200).start();
    }

    private void collapseTextView(TextView tv, int numLines) {
        Log.v(LOG_TAG, " REVIEW_ID collapsed method");

        ObjectAnimator animation = ObjectAnimator.ofInt(tv, "maxLines", numLines);
        animation.setDuration(200).start();
    }

}
