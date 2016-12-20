package com.example.android.popfilms.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

/**
 * Created by jerye on 12/4/2016.
 */

public class FilmContract {
    // Log tag
    private static String LOG_TAG = FilmContract.class.getSimpleName();

    // FilmContracts defines all variables needed for SQLiteDB
    public static final String CONTENT_AUTHORITY = "com.example.android.popfilms";
    public static final String SCHEMA = "content://";


    // The base of all Uri in which apps will use to query.
    public static final Uri BASE_CONTENT_URI = Uri.parse(SCHEMA + CONTENT_AUTHORITY);
    public static final Uri BASE_POSTER_URI = Uri.parse("http://image.tmdb.org/t/p");

    // Path to our movie database
    public static final String PATH_MOVIE = "movie";

    // Size of poster
    public static final String POSTER_SIZE = "w500";


    // Build Uri for poster
    public static Uri buildPosterUri(String posterPath){
        Uri uri = BASE_POSTER_URI.buildUpon().
                appendPath(POSTER_SIZE).
                // Since posterPath starts with "/" use encoded path to prevent encoding into "%2F"
                appendEncodedPath(posterPath).
                build();
        return uri;
    }

    public static final class FilmEntry implements BaseColumns {

        // Film Table Uri
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE).build();

        // Film Table name
        public static final String TABLE_NAME = "film";

        // Film Table Columns
        // COLUMN_ID already defined inside BaseColumns class "_id"
        public static final String COLUMN_POSTER_PATH = "poster_path";
        public static final String COLUMN_BACKDROP_PATH = "backdrop_path";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_ORIGINAL_TITLE = "original_title";
        public static final String COLUMN_VOTE_AVERAGE = "vote_average";

        // Uri types (for getType in ContentProvider MIME type matcher)
        // ie. vnd.android.cursor.dir/com.example.android.popfilms/movie for .dir type
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;


        // Build film data table Uri with unique _id identifier for each row
        public static Uri buildFilmUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static String getMovieTitleFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

        public static Uri buildMovieUriWithTitle(String title){
            return CONTENT_URI.buildUpon().appendPath(title).build();
        }
    }
}
