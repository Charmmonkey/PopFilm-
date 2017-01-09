package com.example.android.popfilms;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;

import com.example.android.popfilms.data.FilmContract;

/**
 * Utility class contained all the variables and URI pertaining to:
 * 1. 20 listed films by popularity and top rated
 * 2. Fetching poster using Picasso
 * 3. Fetching review and trailers
 * 4. Youtube intent call
 * 5. Efficient Preferences methods
 */

public class Utility {

    //// YOUTUBE SECTION ////
    public static Uri buildYouTubeWebUri(String videoKey){
        Uri uri = Uri.parse("https://www.youtube.com/watch?v=" + videoKey);
        return uri;
    }
    public static Uri buildYouTubeAppUri(String videoKey){
        Uri uri = Uri.parse("vnd.youtube:" + videoKey);
        return uri;
    }


    //// PICASSO SECTION ////
    public static final Uri BASE_POSTER_URI = Uri.parse("http://image.tmdb.org/t/p");
    // Size of poster
    public static final String POSTER_SIZE = "w500";

    // Build Uri for poster
    public static Uri buildPosterUri(String posterPath) {
        Uri uri = BASE_POSTER_URI.buildUpon().
                appendPath(POSTER_SIZE).
                // Since posterPath starts with "/" use encoded path to prevent encoding into "%2F"
                        appendEncodedPath(posterPath).
                        build();
        return uri;
    }

    public static Uri buildThumbnailUri(String thumbnailKey) {
        Uri uri = Uri.parse("http://img.youtube.com/vi/" + thumbnailKey + "/hqdefault.jpg");
        return uri;
    }


    //// FILM URIS ////
    // Parameters for Query call
    private static final String MOVIE_BASE_URL = "https://api.themoviedb.org/3/movie/";
    private static final String API_KEY = "api_key";
    private static final String LANGUAGE_PARAM = "language";
    private static final String REGION = "region";
    public static final String PATH_REVIEW = "reviews";
    public static final String PATH_TRAILER = "videos";

    // Build the Uri for TheMovieDB API call by preference order
    public static Uri buildFilmListUri(Context context) {
        return Uri.parse(MOVIE_BASE_URL + Utility.getSortingPreference(context)).buildUpon()
                .appendQueryParameter(API_KEY, BuildConfig.THE_MOVIE_DB_API_KEY)
                .appendQueryParameter(LANGUAGE_PARAM, "en-US")
                .appendQueryParameter(REGION, "Mountain View")
                .build();
    }

    public static Uri buildFilmReviewUriWithId(String id, Context context) {
        final Uri TMDB_FILM_URI = Uri.parse(MOVIE_BASE_URL);
        return TMDB_FILM_URI.buildUpon()
                .appendEncodedPath(id)
                .appendEncodedPath(PATH_REVIEW)
                .appendQueryParameter(API_KEY, BuildConfig.THE_MOVIE_DB_API_KEY)
                .appendQueryParameter(LANGUAGE_PARAM, "en-US")
                .build();
    }

    public static Uri buildFilmTrailerUriWithId(String id, Context context) {
        final Uri TMDB_FILM_URI = Uri.parse(MOVIE_BASE_URL);
        return TMDB_FILM_URI.buildUpon()
                .appendEncodedPath(id)
                .appendEncodedPath(PATH_TRAILER)
                .appendQueryParameter(API_KEY, BuildConfig.THE_MOVIE_DB_API_KEY)
                .appendQueryParameter(LANGUAGE_PARAM, "en-US")
                .build();
    }


    //// FILM QUERY PARAMETERS ////
    // String array for projection, ordered intentionally so they will return in this order
    public static final String[] FILM_COLUMN = {
            FilmContract.FilmEntry.FILM_TABLE_NAME + "." + FilmContract.FilmEntry._ID,
            FilmContract.FilmEntry.COLUMN_ORIGINAL_TITLE,
            FilmContract.FilmEntry.COLUMN_OVERVIEW,
            FilmContract.FilmEntry.COLUMN_POSTER_PATH,
            FilmContract.FilmEntry.COLUMN_RELEASE_DATE,
            FilmContract.FilmEntry.COLUMN_VOTE_AVERAGE,
            FilmContract.FilmEntry.COLUMN_BACKDROP_PATH,
            FilmContract.FilmEntry.COLUMN_SPECIFIC_ID
    };

    public static final String[] FAVORITES_COLUMN = {
            FilmContract.FilmEntry.FAVORITES_TABLE_NAME + "." + FilmContract.FilmEntry._ID,
            FilmContract.FilmEntry.COLUMN_ORIGINAL_TITLE,
            FilmContract.FilmEntry.COLUMN_OVERVIEW,
            FilmContract.FilmEntry.COLUMN_POSTER_PATH,
            FilmContract.FilmEntry.COLUMN_RELEASE_DATE,
            FilmContract.FilmEntry.COLUMN_VOTE_AVERAGE,
            FilmContract.FilmEntry.COLUMN_BACKDROP_PATH,
            FilmContract.FilmEntry.COLUMN_SPECIFIC_ID
    };

    public static final String[] ENTRY_COLUMN = {
            FilmContract.FilmEntry.COLUMN_ORIGINAL_TITLE,
            FilmContract.FilmEntry.COLUMN_OVERVIEW,
            FilmContract.FilmEntry.COLUMN_POSTER_PATH,
            FilmContract.FilmEntry.COLUMN_RELEASE_DATE,
            FilmContract.FilmEntry.COLUMN_VOTE_AVERAGE,
            FilmContract.FilmEntry.COLUMN_BACKDROP_PATH,
            FilmContract.FilmEntry.COLUMN_SPECIFIC_ID};

    public static final String[] REVIEW_COLUMN = {FilmContract.FilmEntry.COLUMN_REVIEW_AUTHOR,
            FilmContract.FilmEntry.COLUMN_REVIEW_CONTENT};

    public static final String[] TRAILER_COLUMN = {FilmContract.FilmEntry.COLUMN_TRAILER_KEY,
            FilmContract.FilmEntry.COLUMN_TRAILER_NAME};

    // These IDs are for matching to the cursor ID when obtaining values of the column
    public static final int COL_FILM_ID = 0;
    public static final int COL_ORIGINAL_TITLE_ID = 0;
    public static final int COL_OVERVIEW_ID = 1;
    public static final int COL_POSTER_PATH_ID = 2;
    public static final int COL_RELEASE_DATE_ID = 3;
    public static final int COL_VOTE_AVERAGE_ID = 4;
    public static final int COL_BACKDROP_PATH_ID = 5;
    public static final int COL_SPECIFIC_ID = 6;

    // ID for REVIEW table
    public static final int COL_REVIEW_AUTHOR_ID = 0;
    public static final int COL_REVIEW_CONTENT_ID = 1;

    // ID for TRAILER table
    public static final int COL_TRAILER_KEY_ID = 0;
    public static final int COL_TRAILER_NAME_ID = 1;


    //// PREFERENCE SECTION ////
    public static String getSortingPreference(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString(getSortPreferenceKey(context), context.getString(R.string.Pref_values_popular));
    }

    public static String getSortPreferenceKey(Context context) {
        return context.getString(R.string.Pref_sort_key);
    }



}
