package com.example.android.popfilms;


import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popfilms.data.FilmContract;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * DetailedFilmFragment contains the bulk of code for populating the detailed film page.
 * Rather than swapping cursors in onLoadFinished, it loops and stores all the information in the cursor in a variable.
 * It utilizes several switches to determine what information to fetch/load/populate
 * The fragment creates loaders to query the local database, if nothing is returned, the necessary information is fetched from
 * TMDB and inserted into the local database. Once local database is updated, the loader is notified to requery.
 */
public class DetailedFilmFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {


    public DetailedFilmFragment() {
        // Required empty public constructor
    }

    // Loader IDs
    private static final int GENERAL_LOADER_ID = 0;
    private static final int REVIEW_LOADER_ID = 1;
    private static final int TRAILER_LOADER_ID = 2;
    // Matcher IDs
    private final int REVIEW_ID = 100;
    private final int TRAILER_ID = 101;
    // Empty bins for data
    public ArrayList<String[]> trailerData = new ArrayList<String[]>();
    public ArrayList<String[]> reviewData = new ArrayList<String[]>();
    // Empty bins for RecyclerView variables
    private RecyclerView.LayoutManager mTrailerLayoutManager;
    private RecyclerView trailerRecyclerView;
    private RecyclerView.Adapter trailerClickableRecyclerAdapter;
    private RecyclerView.LayoutManager mReviewLayoutManager;
    private RecyclerView reviewRecyclerView;
    private RecyclerView.Adapter reviewClickableRecyclerAdapter;
    // Empty bins for Uri
    private Uri filmDetailContentUri;
    private Uri filmFavoritesContentUri;
    private String detailedFilmId;
    private Uri filmReviewContentUri;
    private Uri filmTrailerContentUri;
    // Variables for favorites table
    private int favoritesMarker = 0;
    private ContentValues favoritesValues = new ContentValues();
    private Cursor favoritesCursor;

    private String[] detailedFilmIdArray;
    private Bundle mSavedInstanceState;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mSavedInstanceState = savedInstanceState;

        // Get the specific film's ID to know which film was clicked
        filmDetailContentUri = getIntentData();
        detailedFilmId = FilmContract.FilmEntry.getMovieIdFromUri(filmDetailContentUri);
        filmFavoritesContentUri = FilmContract.FilmEntry.buildFavoritesUriWithId(detailedFilmId);
        filmReviewContentUri = FilmContract.FilmEntry.buildReviewContentUriWithId(detailedFilmId);
        filmTrailerContentUri = FilmContract.FilmEntry.buildTrailerContentUriWithId(detailedFilmId);

        // Initiate loader for the 3 queries
        getActivity().getSupportLoaderManager().initLoader(TRAILER_LOADER_ID, savedInstanceState, this);
        getActivity().getSupportLoaderManager().initLoader(REVIEW_LOADER_ID, savedInstanceState, this);
        getActivity().getSupportLoaderManager().initLoader(GENERAL_LOADER_ID, savedInstanceState, this);

        // Inflate view
        View rootView = inflater.inflate(R.layout.detailed_item, container, false);

        // Set up for trailers RecyclerView
        trailerRecyclerView = (RecyclerView) rootView.findViewById(R.id.trailer_recycler);
        trailerRecyclerView.setHasFixedSize(true);
        mTrailerLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        trailerRecyclerView.setLayoutManager(mTrailerLayoutManager);

        // Set up for reviews RecyclerView
        reviewRecyclerView = (RecyclerView) rootView.findViewById(R.id.review_recycler);
        reviewRecyclerView.setHasFixedSize(true);
        mReviewLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        reviewRecyclerView.setLayoutManager(mReviewLayoutManager);

        return rootView;
    }


    // Implementing the Loader this way prevents duplicate records of the same film
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        switch (id) {
            // Query general (title, backdrop, rating, etc.) information from the general table or favorites table depending on preference.
            // This matters because favorites table is cached offline in a separate table and it can contains information on movies from "popular" or "top_rated"
            case GENERAL_LOADER_ID:
                if (Utility.getSortingPreference(getContext()).equals("favorites")) {
                    return new CursorLoader(getContext(), filmFavoritesContentUri, Utility.ENTRY_COLUMN, null, null, null);
                } else {
                    return new CursorLoader(getContext(), filmDetailContentUri, Utility.ENTRY_COLUMN, null, null, null);
                }
            case REVIEW_LOADER_ID: // Query review table
                return new CursorLoader(getContext(), filmReviewContentUri, Utility.REVIEW_COLUMN, null, null, null);
            case TRAILER_LOADER_ID: // Query trailer table
                return new CursorLoader(getContext(), filmTrailerContentUri, Utility.TRAILER_COLUMN, null, null, null);
            default:
                return null;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Not using cursor. So no cursor reset
    }


    // If query returns empty cursor, fetch the data, which triggers a requery then set contents to views.
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        switch (loader.getId()) {
            // For title, overview, vote average,etc..
            case GENERAL_LOADER_ID:
                if (cursor.moveToFirst()) {
                    // Retrieve data from cursor
                    String detailedTitle = cursor.getString(Utility.COL_ORIGINAL_TITLE_ID);
                    String detailedOverview = cursor.getString(Utility.COL_OVERVIEW_ID);
                    String detailedReleaseDate = cursor.getString(Utility.COL_RELEASE_DATE_ID);
                    String detailedVoteAverage = cursor.getString(Utility.COL_VOTE_AVERAGE_ID);
                    String detailedPosterPath = cursor.getString(Utility.COL_POSTER_PATH_ID);
                    String detailedBackdropPath = cursor.getString(Utility.COL_BACKDROP_PATH_ID);

                    // Puts all data into a contentValue for later use (remove/add entry to favorites table)
                    favoritesValues.put(FilmContract.FilmEntry.COLUMN_ORIGINAL_TITLE, detailedTitle);
                    favoritesValues.put(FilmContract.FilmEntry.COLUMN_RELEASE_DATE, detailedReleaseDate);
                    favoritesValues.put(FilmContract.FilmEntry.COLUMN_OVERVIEW, detailedOverview);
                    favoritesValues.put(FilmContract.FilmEntry.COLUMN_VOTE_AVERAGE, detailedVoteAverage);
                    favoritesValues.put(FilmContract.FilmEntry.COLUMN_POSTER_PATH, detailedPosterPath);
                    favoritesValues.put(FilmContract.FilmEntry.COLUMN_BACKDROP_PATH, detailedBackdropPath);
                    favoritesValues.put(FilmContract.FilmEntry.COLUMN_SPECIFIC_ID, detailedFilmId);
                    favoritesValues.put(FilmContract.FilmEntry.COLUMN_FAVORITE, "1");

                    // Find the views associated with id
                    TextView titleView = (TextView) getView().findViewById(R.id.detailed_title);
                    TextView overviewView = (TextView) getView().findViewById(R.id.detailed_overview);
                    TextView voteAverageView = (TextView) getView().findViewById(R.id.detailed_vote_average);
                    ImageView backdropImageView = (ImageView) getView().findViewById(R.id.detailed_backdrop_image);
                    final ImageView favoriteButton = (ImageView) getView().findViewById(R.id.favorite_button);

                    // Set contents to respective views
                    Picasso.with(getContext()).load(Utility.buildPosterUri(detailedBackdropPath)).into(backdropImageView);
                    titleView.setText(detailedTitle);
                    overviewView.setText(detailedOverview);
                    voteAverageView.setText(detailedVoteAverage);

                    // Creates an empty toast for later use
                    final Toast mToast = Toast.makeText(getContext(), "", Toast.LENGTH_SHORT);

                    // Hack to bypass String[] initiating error
                    String[] detailedFilmIdArrayTemp = {detailedFilmId};
                    detailedFilmIdArray = detailedFilmIdArrayTemp;

                    // Query to check if this film exists in favorites table
                    favoritesCursor = getContext().getContentResolver().query(FilmContract.FilmEntry.FAVORITES_URI,
                            null,
                            FilmContract.FilmEntry.COLUMN_SPECIFIC_ID + " = ?",
                            detailedFilmIdArray,
                            null);

                    // Set the state of favorite button depending on its existence in favorites table
                    if (!favoritesCursor.moveToFirst()) {
                        favoriteButton.setImageResource(R.drawable.ic_favorite_border_white_36dp);
                    } else {
                        favoriteButton.setImageResource(R.drawable.ic_favorite_white_36dp);
                    }

                    // If else statements to determine state of favorites button, toast message, and favoritesMarker
                    favoriteButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!favoritesCursor.moveToFirst()) {
                                if (favoritesMarker == 1) {
                                    favoritesMarker = -1;
                                    favoriteButton.setImageResource(R.drawable.ic_favorite_border_white_36dp);
                                    mToast.setText("Unfavorited");
                                    mToast.show();
                                } else {
                                    favoritesMarker = 1;
                                    favoriteButton.setImageResource(R.drawable.ic_favorite_white_36dp);
                                    mToast.setText("Favorited");
                                    mToast.show();
                                }
                            } else {
                                if (favoritesMarker == -1) {
                                    favoritesMarker = 1;
                                    favoriteButton.setImageResource(R.drawable.ic_favorite_white_36dp);
                                    mToast.setText("Favorited");
                                    mToast.show();
                                } else {
                                    favoritesMarker = -1;
                                    favoriteButton.setImageResource(R.drawable.ic_favorite_border_white_36dp);
                                    mToast.setText("Unfavorited");
                                    mToast.show();
                                }
                            }
                        }
                    });
                }
                break;

            // Review case on fetches data from TMDB if data didn't exist before. Prevents fetching every time you exit and enter to detailed page
            case REVIEW_LOADER_ID:
                if (cursor.moveToFirst()) {
                    // Puts all data from cursor to a variable
                    do {
                        String[] dataSet = {cursor.getString(Utility.COL_REVIEW_AUTHOR_ID),
                                cursor.getString(Utility.COL_REVIEW_CONTENT_ID)};
                        reviewData.add(dataSet);
                    } while (cursor.moveToNext());

                    // Creates RecyclerAdapter and pass in dataset, then set adapter
                    reviewClickableRecyclerAdapter = new RecyclerAdapter(reviewData, REVIEW_ID, getContext());
                    reviewRecyclerView.setAdapter(reviewClickableRecyclerAdapter);

                    // Fetch data if none existed
                } else {
                    VolleyFetcher.volleyFetcher(Utility.buildFilmReviewUriWithId(detailedFilmId, getContext()).toString(), Utility.REVIEW_COLUMN, getContext());
                }
                break;

            // Trailer case on fetches data from TMDB if data didn't exist before. Prevents fetching every time you exit and enter to detailed page
            case TRAILER_LOADER_ID:
                if (cursor.moveToFirst()) {
                    // Puts all data from cursor to variable
                    do {
                        String[] dataSet = {cursor.getString(Utility.COL_TRAILER_NAME_ID),
                                cursor.getString(Utility.COL_TRAILER_KEY_ID)};
                        trailerData.add(dataSet);
                    } while (cursor.moveToNext());

                    // Creates RecyclerAdapter and pass in dataset, then set adapter
                    trailerClickableRecyclerAdapter = new RecyclerAdapter(trailerData, TRAILER_ID, getContext());
                    trailerRecyclerView.setAdapter(trailerClickableRecyclerAdapter);

                    // Fetch data if none existed
                } else {
                    VolleyFetcher.volleyFetcher(Utility.buildFilmTrailerUriWithId(detailedFilmId, getContext()).toString(), Utility.TRAILER_COLUMN, getContext());
                }
                break;
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Close favoritesCursor
        favoritesCursor.close();
        // Depending on value of favoritesMarker, delete, do nothing, or insert detailed film data to favorites table
        switch (favoritesMarker) {
            // Unfavorited
            case -1:
                getContext().getContentResolver().delete(FilmContract.FilmEntry.FAVORITES_URI,
                        FilmContract.FilmEntry.COLUMN_SPECIFIC_ID + " = ?",
                        detailedFilmIdArray);
                break;

            // No change
            case 0:
                break;

            // Favorited
            case 1:
                getContext().getContentResolver().delete(FilmContract.FilmEntry.FAVORITES_URI,
                        FilmContract.FilmEntry.COLUMN_SPECIFIC_ID + " = ?",
                        detailedFilmIdArray);

                getContext().getContentResolver().insert(FilmContract.FilmEntry.FAVORITES_URI,
                        favoritesValues);
                break;
        }
    }

    // Must retrieve intent data inside a override method or else will cause Nullpointerexception
    private Uri getIntentData() {
        return getActivity().getIntent().getData();
    }


}
