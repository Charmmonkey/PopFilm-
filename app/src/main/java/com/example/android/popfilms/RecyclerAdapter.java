package com.example.android.popfilms;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by jerye on 12/28/2016.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<ClickableViewHolder> {
    public ArrayList<String[]> mDataset;
    private int mMatcher;
    private Context mContext;
    private LinearLayout ll;
    private CardView cv;
    private FrameLayout fl;
    private final int REVIEW_ID = 100;
    private final int TRAILER_ID = 101;
    private ClickableViewHolder vh;
    public static String reviewAuthorData;
    public static String reviewContentData;
    public static String trailerThumbnailData;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder


    // Provide a suitable constructor (depends on the kind of dataset)
    public RecyclerAdapter(ArrayList<String[]> myDataset, int match, Context context) {
        mDataset = myDataset;
        mMatcher = match;
        mContext = context;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ClickableViewHolder onCreateViewHolder(ViewGroup parent,
                                                  int viewType) {
        switch (mMatcher) {
            case REVIEW_ID:
                ll = (LinearLayout) LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.review_item, parent, false);
                vh = new ClickableViewHolder(ll, mMatcher, mDataset, mContext);
                break;
            case TRAILER_ID:
                ll = (LinearLayout) LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.trailer_item, parent, false);
                vh = new ClickableViewHolder(ll, mMatcher, mDataset, mContext);
                break;
        }
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ClickableViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        Log.v("RecyclerAdapter", "View bound");
        switch (mMatcher) {
            case REVIEW_ID:
                Log.v("RecyclerAdapter", "Review views bound");
                reviewAuthorData = mDataset.get(position)[0];
                reviewContentData = mDataset.get(position)[1];

                holder.reviewAuthorTextView.setText(reviewAuthorData);
                holder.reviewContentTextView.setText(reviewContentData);


                if (getItemCount() > 1) {
                    if (position == 0) { // First view with right arrow
                        holder.reviewRightButton.setImageResource(R.drawable.ic_chevron_right_white_24dp);

                    } else if (position == getItemCount() - 1) { // Last view with left arrow

                        holder.reviewLeftButton.setImageResource(R.drawable.ic_chevron_left_white_24dp);

                    } else { // Middle views with both arrows
                        holder.reviewLeftButton.setImageResource(R.drawable.ic_chevron_left_white_24dp);
                        holder.reviewRightButton.setImageResource(R.drawable.ic_chevron_right_white_24dp);
                    }

                }
                break;
            case TRAILER_ID:
                Log.v("RecyclerAdapter", "trailer views bound");
                trailerThumbnailData = mDataset.get(position)[1];
                Picasso.with(mContext).load(Utility.buildThumbnailUri(trailerThumbnailData)).into(holder.trailerThumbnailImageView);
                holder.trailerNameTextView.setText(mDataset.get(position)[0]);

                Log.v("RecyclerAdapter", Integer.toString(holder.trailerThumbnailImageView.getWidth()));
        }


    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}



