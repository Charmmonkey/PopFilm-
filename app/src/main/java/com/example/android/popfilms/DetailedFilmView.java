package com.example.android.popfilms;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
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

public class DetailedFilmView extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = DetailedFilmView.class.getSimpleName();

    private Context mContext = DetailedFilmView.this;

    private static final int GENERAL_LOADER_ID = 0;
    private static final int REVIEW_LOADER_ID = 1;
    private static final int TRAILER_LOADER_ID = 2;


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Fixes screen rotation crash
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initiate loader for the 3 queries
        getSupportLoaderManager().initLoader(GENERAL_LOADER_ID, savedInstanceState, this);
        getSupportLoaderManager().initLoader(REVIEW_LOADER_ID, savedInstanceState, this);
        getSupportLoaderManager().initLoader(TRAILER_LOADER_ID, savedInstanceState, this);

        setContentView(R.layout.detailed_item);
    }


    // Implementing the Loader this way prevents duplicate records of the same film
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.v(LOG_TAG, " Loader created");
        final Uri filmDetailContentUri = getIntentData();
        final String detailedFilmId = FilmContract.FilmEntry.getMovieIdFromUri(filmDetailContentUri);
        final Uri filmReviewContentUri = FilmContract.FilmEntry.buildReviewContentUriWithId(detailedFilmId);
        final Uri filmTrailerContentUri = FilmContract.FilmEntry.buildTrailerContentUriWithId(detailedFilmId);

        switch (id) {
            case GENERAL_LOADER_ID:
                return new CursorLoader(DetailedFilmView.this, filmDetailContentUri, Utility.ENTRY_COLUMN, null, null, null);
            case REVIEW_LOADER_ID:
                return new CursorLoader(DetailedFilmView.this, filmReviewContentUri, Utility.REVIEW_COLUMN, null, null, null);
            case TRAILER_LOADER_ID:
                return new CursorLoader(DetailedFilmView.this, filmTrailerContentUri, Utility.TRAILER_COLUMN, null, null, null);
            default:
                return null;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.v(LOG_TAG, " Loader Reset");
    }

    // If query returns empty cursor, fetch the data, which triggers a requery then set.
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        final Uri detailedFilmUri = getIntentData();
        final String detailedFilmId = FilmContract.FilmEntry.getMovieIdFromUri(detailedFilmUri);

        switch (loader.getId()) {

            case GENERAL_LOADER_ID:
                if (cursor.moveToFirst()) {
                    String detailedTitle = cursor.getString(Utility.COL_ORIGINAL_TITLE_ID);
                    Log.v(LOG_TAG, detailedTitle);
                    String detailedOverview = cursor.getString(Utility.COL_OVERVIEW_ID);
                    String detailedReleaseDate = cursor.getString(Utility.COL_RELEASE_DATE_ID);
                    String detailedVoteAverage = cursor.getString(Utility.COL_VOTE_AVERAGE_ID);
                    String detailedBackdropPath = cursor.getString(Utility.COL_BACKDROP_PATH_ID);

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
                break;

            case REVIEW_LOADER_ID:
                if (cursor.moveToFirst()) {

                    String reviewAuthor = cursor.getString(Utility.COL_REVIEW_AUTHOR_ID);
                    String reviewContent = cursor.getString(Utility.COL_REVIEW_CONTENT_ID);

                    TextView reviewAuthorView = (TextView) findViewById(R.id.review_author);
                    TextView reviewContentView = (TextView) findViewById(R.id.review_content);

                    reviewAuthorView.setText(reviewAuthor);
                    reviewContentView.setText(reviewContent);

                } else {
                    VolleyFetcher.volleyFetcher(Utility.buildFilmReviewUriWithId(detailedFilmId, mContext).toString(), Utility.REVIEW_COLUMN, mContext);
                }
                break;

            case TRAILER_LOADER_ID:
                Log.v(LOG_TAG, " trailer case set");
                if (cursor.moveToFirst()) {
                    String trailerKey = cursor.getString(Utility.COL_TRAILER_KEY_ID);
                    String trailerName = cursor.getString(Utility.COL_TRAILER_NAME_ID);

                    TextView trailerKeyView = (TextView) findViewById(R.id.trailer_key);
                    TextView trailerNameView = (TextView) findViewById(R.id.trailer_name);

                    trailerKeyView.setText(trailerKey);
                    trailerNameView.setText(trailerName);

                } else {
                    VolleyFetcher.volleyFetcher(Utility.buildFilmTrailerUriWithId(detailedFilmId, mContext).toString(), Utility.TRAILER_COLUMN, mContext);
                }
                break;
        }
    }

    // Must retrieve intent data inside a override method or else will cause Nullpointerexception
    private Uri getIntentData() {
        return getIntent().getData();
    }

}
