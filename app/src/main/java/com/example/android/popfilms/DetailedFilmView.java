package com.example.android.popfilms;

import android.content.ActivityNotFoundException;
import android.content.ContentValues;
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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.Volley;
import com.example.android.popfilms.data.FilmContract;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by jerye on 12/14/2016.
 */

public class DetailedFilmView extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = DetailedFilmView.class.getSimpleName();

    private Context mContext = DetailedFilmView.this;

    private static final int GENERAL_LOADER_ID = 0;
    private static final int REVIEW_LOADER_ID = 1;
    private static final int TRAILER_LOADER_ID = 2;


    private final int REVIEW_ID = 100;
    private final int TRAILER_ID = 101;

    public ArrayList<String[]> trailerData = new ArrayList<String[]>();

    private RecyclerView.LayoutManager mTrailerLayoutManager;
    private RecyclerView trailerRecyclerView;
    private RecyclerView.Adapter trailerClickableRecyclerAdapter;

    public ArrayList<String[]> reviewData = new ArrayList<String[]>();

    private RecyclerView.LayoutManager mReviewLayoutManager;
    private RecyclerView reviewRecyclerView;
    private RecyclerView.Adapter reviewClickableRecyclerAdapter;

    private boolean isFavorited = false;

    private Uri filmDetailContentUri;
    private Uri filmFavoritesContentUri;
    private String detailedFilmId;
    private Uri filmReviewContentUri;
    private Uri filmTrailerContentUri;

    private ContentValues favoriteTrue = new ContentValues(1);
    private ContentValues favoriteFalse = new ContentValues(1);

    private ContentValues favoritesValues = new ContentValues();


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


        trailerRecyclerView = (RecyclerView) findViewById(R.id.trailer_recycler);
        trailerRecyclerView.setHasFixedSize(true);
        mTrailerLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        trailerRecyclerView.setLayoutManager(mTrailerLayoutManager);

        reviewRecyclerView = (RecyclerView) findViewById(R.id.review_recycler);
        reviewRecyclerView.setHasFixedSize(true);
        mReviewLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        reviewRecyclerView.setLayoutManager(mReviewLayoutManager);

        favoriteTrue.put(FilmContract.FilmEntry.COLUMN_FAVORITE, "1");
        favoriteFalse.put(FilmContract.FilmEntry.COLUMN_FAVORITE, "0");
    }


    // Implementing the Loader this way prevents duplicate records of the same film
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.v(LOG_TAG, " Loader created");
        filmDetailContentUri = getIntentData();
        detailedFilmId = FilmContract.FilmEntry.getMovieIdFromUri(filmDetailContentUri);
        filmFavoritesContentUri = FilmContract.FilmEntry.buildFavoritesUriWithId(detailedFilmId);
        filmReviewContentUri = FilmContract.FilmEntry.buildReviewContentUriWithId(detailedFilmId);
        filmTrailerContentUri = FilmContract.FilmEntry.buildTrailerContentUriWithId(detailedFilmId);

        switch (id) {
            case GENERAL_LOADER_ID:
                if (Utility.getSortingPreference(mContext).equals("favorites")) {
                   return new CursorLoader(DetailedFilmView.this, filmFavoritesContentUri, Utility.ENTRY_COLUMN, null, null, null);
                } else {
                   return new CursorLoader(DetailedFilmView.this, filmDetailContentUri, Utility.ENTRY_COLUMN, null, null, null);
                }
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

        switch (loader.getId()) {

            case GENERAL_LOADER_ID:
                if (cursor.moveToFirst()) {
                    String detailedTitle = cursor.getString(Utility.COL_ORIGINAL_TITLE_ID);
                    Log.v(LOG_TAG, detailedTitle);
                    String detailedOverview = cursor.getString(Utility.COL_OVERVIEW_ID);
                    String detailedReleaseDate = cursor.getString(Utility.COL_RELEASE_DATE_ID);
                    String detailedVoteAverage = cursor.getString(Utility.COL_VOTE_AVERAGE_ID);
                    String detailedPosterPath = cursor.getString(Utility.COL_POSTER_PATH_ID);
                    String detailedBackdropPath = cursor.getString(Utility.COL_BACKDROP_PATH_ID);

                    favoritesValues.put(FilmContract.FilmEntry.COLUMN_ORIGINAL_TITLE, detailedTitle);
                    favoritesValues.put(FilmContract.FilmEntry.COLUMN_RELEASE_DATE, detailedReleaseDate);
                    favoritesValues.put(FilmContract.FilmEntry.COLUMN_OVERVIEW, detailedOverview);
                    favoritesValues.put(FilmContract.FilmEntry.COLUMN_VOTE_AVERAGE, detailedVoteAverage);
                    favoritesValues.put(FilmContract.FilmEntry.COLUMN_POSTER_PATH, detailedPosterPath);
                    favoritesValues.put(FilmContract.FilmEntry.COLUMN_BACKDROP_PATH, detailedBackdropPath);
                    favoritesValues.put(FilmContract.FilmEntry.COLUMN_SPECIFIC_ID, detailedFilmId);

                    // Find the views associated with id
                    TextView titleView = (TextView) findViewById(R.id.detailed_title);
                    TextView overviewView = (TextView) findViewById(R.id.detailed_overview);
                    TextView releaseDateView = (TextView) findViewById(R.id.detailed_release_date);
                    TextView voteAverageView = (TextView) findViewById(R.id.detailed_vote_average);
                    ImageView backdropImageView = (ImageView) findViewById(R.id.detailed_backdrop_image);
                    ImageView favoriteButton = (ImageView) findViewById(R.id.favorite_button);


                    // Set content to respective view
                    Picasso.with(DetailedFilmView.this).load(Utility.buildPosterUri(detailedBackdropPath)).into(backdropImageView);
                    titleView.setText(detailedTitle);
                    overviewView.setText(detailedOverview);
                    releaseDateView.setText(detailedReleaseDate);
                    voteAverageView.setText("Rating: " + detailedVoteAverage + "/10");


                    favoriteButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String[] detailedFilmIdArray = {detailedFilmId};
                            if (isFavorited == false) {

                                mContext.getContentResolver().delete(FilmContract.FilmEntry.FAVORITES_URI,
                                        FilmContract.FilmEntry.COLUMN_SPECIFIC_ID + " = ? ",
                                        detailedFilmIdArray
                                );
                                mContext.getContentResolver().insert(FilmContract.FilmEntry.FAVORITES_URI, favoritesValues);

                                isFavorited = true;

                            } else {

                                mContext.getContentResolver().delete(FilmContract.FilmEntry.FAVORITES_URI,
                                        FilmContract.FilmEntry.COLUMN_SPECIFIC_ID + " = ? ",
                                        detailedFilmIdArray
                                );
                                isFavorited = false;
                            }

                        }
                    });
                }
                break;

            case REVIEW_LOADER_ID:
                if (cursor.moveToFirst()) {
                    do {
                        String[] dataSet = {cursor.getString(Utility.COL_REVIEW_AUTHOR_ID),
                                cursor.getString(Utility.COL_REVIEW_CONTENT_ID)};
                        reviewData.add(dataSet);
                    } while (cursor.moveToNext());

                    reviewClickableRecyclerAdapter = new RecyclerAdapter(reviewData, REVIEW_ID, mContext);
                    reviewRecyclerView.setAdapter(reviewClickableRecyclerAdapter);


                } else {
                    VolleyFetcher.volleyFetcher(Utility.buildFilmReviewUriWithId(detailedFilmId, mContext).toString(), Utility.REVIEW_COLUMN, mContext);
                }
                break;

            case TRAILER_LOADER_ID:
                if (cursor.moveToFirst()) {
                    do {
                        String[] dataSet = {cursor.getString(Utility.COL_TRAILER_NAME_ID),
                                cursor.getString(Utility.COL_TRAILER_KEY_ID)};
                        trailerData.add(dataSet);
                    } while (cursor.moveToNext());

                    trailerClickableRecyclerAdapter = new RecyclerAdapter(trailerData, TRAILER_ID, mContext);
                    trailerRecyclerView.setAdapter(trailerClickableRecyclerAdapter);


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
