package com.example.android.popfilms;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.Volley;
import com.example.android.popfilms.data.FilmContract;
import com.squareup.picasso.Picasso;

/**
 * Created by jerye on 12/14/2016.
 */

public class DetailedFilmView extends AppCompatActivity {
    private static final String LOG_TAG = DetailedFilmView.class.getSimpleName();
    private Context mContext = DetailedFilmView.this;


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Uri detailedFilmUri = getIntentData();
        final String detailedFilmId = FilmContract.FilmEntry.getMovieIdFromUri(detailedFilmUri);
        final Uri filmReviewContentUri = FilmContract.FilmEntry.buildReviewContentUriWithId(detailedFilmId);
        final Uri filmTrailerContentUri = FilmContract.FilmEntry.buildTrailerContentUriWithId(detailedFilmId);
        Log.v(LOG_TAG, detailedFilmUri.toString());

        setContentView(R.layout.detailed_item);

        // Move these to background thread eventually.
        // Grab the content of these movies with specific Id
        Cursor cursor = mContext.getContentResolver().query(detailedFilmUri, Utility.ENTRY_COLUMN, null, null, null);

        if (cursor.moveToFirst()) {
            String detailedTitle = cursor.getString(Utility.COL_ORIGINAL_TITLE_ID);
            Log.v(LOG_TAG, detailedTitle);
            String detailedOverview = cursor.getString(Utility.COL_OVERVIEW_ID);
            String detailedReleaseDate = cursor.getString(Utility.COL_RELEASE_DATE_ID);
            String detailedVoteAverage = cursor.getString(Utility.COL_VOTE_AVERAGE_ID);
            String detailedBackdropPath = cursor.getString(Utility.COL_BACKDROP_PATH_ID);

            cursor.close();

            // Find the views associated with id
            TextView titleView = (TextView) findViewById(R.id.detailed_title);
            TextView overviewView = (TextView) findViewById(R.id.detailed_overview);
            TextView releaseDateView = (TextView) findViewById(R.id.detailed_release_date);
            TextView voteAverageView = (TextView) findViewById(R.id.detailed_vote_average);
            ImageView backdropImageView = (ImageView) findViewById(R.id.detailed_backdrop_image);


            // Set content to respective view
            Picasso.with(DetailedFilmView.this).load(Utility.buildPosterUri(detailedBackdropPath)).into(backdropImageView);
            titleView.setText(detailedTitle);
            overviewView.setText(detailedOverview);
            releaseDateView.setText("Release Date: " + detailedReleaseDate);
            voteAverageView.setText("Rating: " + detailedVoteAverage + "/10");
        }


        // Query TMDB API for movie review and trailer data
        VolleyFetcher.volleyFetcher(Utility.buildFilmReviewUriWithId(detailedFilmId, mContext).toString(),Utility.REVIEW_COLUMN,mContext);
        VolleyFetcher.volleyFetcher(Utility.buildFilmTrailerUriWithId(detailedFilmId, mContext).toString(),Utility.TRAILER_COLUMN,mContext);

        // Query review and trailer SQLiteDB
        Cursor cursorReview = mContext.getContentResolver().query(filmReviewContentUri, Utility.REVIEW_COLUMN, null, null, null);
        if(cursorReview.moveToFirst()){
            String reviewAuthor = cursorReview.getString(Utility.COL_REVIEW_AUTHOR_ID);
            String reviewContent = cursorReview.getString(Utility.COL_REVIEW_CONTENT_ID);

            cursorReview.close();

            TextView reviewAuthorView = (TextView) findViewById(R.id.review_author);
            TextView reviewContentView = (TextView) findViewById(R.id.review_content);

            reviewAuthorView.setText(reviewAuthor);
            reviewContentView.setText(reviewContent);

        }
        Cursor cursorTrailer = mContext.getContentResolver().query(filmTrailerContentUri, Utility.TRAILER_COLUMN,null, null, null);
        if(cursorTrailer.moveToFirst()){
            String trailerKey = cursorTrailer.getString(Utility.COL_TRAILER_KEY_ID);
            String trailerName = cursorTrailer.getString(Utility.COL_TRAILER_NAME_ID);
            cursorTrailer.close();

            TextView trailerKeyView = (TextView) findViewById(R.id.trailer_key);
            TextView trailerNameView = (TextView) findViewById(R.id.trailer_name);

            trailerKeyView.setText(trailerKey);
            trailerNameView.setText(trailerName);
        }
    }

    private Uri getIntentData() {
        return getIntent().getData();
    }

}
