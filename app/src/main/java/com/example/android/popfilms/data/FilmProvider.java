package com.example.android.popfilms.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.CancellationSignal;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.android.popfilms.FilmFragment;
import com.example.android.popfilms.Utility;

import static android.R.attr.author;
import static android.R.attr.id;

/**
 * Created by jerye on 12/4/2016.
 */

public class FilmProvider extends ContentProvider {
    // IDs for UriMatcher
    static final int MOVIE = 100;
    static final int MOVIE_WITH_TITLE = 101;
    static final int MOVIE_REVIEW = 102;
    static final int MOVIE_TRAILER = 103;
    static final int FAVORITES = 104;
    static final int FAVORITES_WITH_TITLE = 105;

    // Static variable
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private FilmDBHelper mOpenDBHelper;


    @Override
    public boolean onCreate() {
        mOpenDBHelper = new FilmDBHelper(getContext());
        return false;
    }


    @Override
    public String getType(Uri uri) {
        int match = sUriMatcher.match(uri);
        switch (match) {
            case MOVIE:
                return FilmContract.FilmEntry.CONTENT_TYPE;
            case MOVIE_WITH_TITLE:
                return FilmContract.FilmEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

    }


    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase filmDB = mOpenDBHelper.getReadableDatabase();
        int match = sUriMatcher.match(uri);
        Cursor queryCursor;
        switch (match) {
            case MOVIE: {
                queryCursor = filmDB.query(
                        FilmContract.FilmEntry.FILM_TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            // Query for the detailed film page
            //
            case MOVIE_WITH_TITLE: {
                queryCursor = filmDB.query(
                        FilmContract.FilmEntry.FILM_TABLE_NAME,
                        projection,
                        "id = ?",
                        new String[]{FilmContract.FilmEntry.getMovieIdFromUri(uri)},
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            // Query for review table
            case MOVIE_REVIEW:
                Log.v("FilmProvider", "movie review query");
                queryCursor = filmDB.query(
                        FilmContract.FilmEntry.REVIEW_TABLE_NAME,
                        projection,
                        "id = ?",
                        new String[]{FilmContract.FilmEntry.getMovieIdFromUri(uri)},
                        null,
                        null,
                        sortOrder
                );
                break;
            case MOVIE_TRAILER:
                Log.v("FilmProvider", "movie trailer query");
                queryCursor = filmDB.query(
                        FilmContract.FilmEntry.TRAILER_TABLE_NAME,
                        projection,
                        "id = ?",
                        new String[]{FilmContract.FilmEntry.getMovieIdFromUri(uri)},
                        null,
                        null,
                        sortOrder
                );
                break;
            case FAVORITES: {
                queryCursor = filmDB.query(
                        FilmContract.FilmEntry.FAVORITES_TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case FAVORITES_WITH_TITLE: {
                queryCursor = filmDB.query(
                        FilmContract.FilmEntry.FAVORITES_TABLE_NAME,
                        projection,
                        "id = ?",
                        new String[]{FilmContract.FilmEntry.getMovieIdFromUri(uri)},
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri);
        }
        queryCursor.setNotificationUri(getContext().getContentResolver(), uri);
        Log.v("FilmProvider", "Query update");
        return queryCursor;
    }


    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = mOpenDBHelper.getWritableDatabase();
        Uri insertUri;
        int match = sUriMatcher.match(uri);


        // _id of the inserted row. If it exists, create uri with _id, else throw SQLException
        switch (match) {
            case MOVIE:
                long _id = db.insert(FilmContract.FilmEntry.FILM_TABLE_NAME, null, values);
                if (_id > 0) {
                    insertUri = FilmContract.FilmEntry.buildFilmUri(_id);
                } else {
                    throw new SQLException("Error inserting Uri to database: " + uri);
                }
                break;
            case MOVIE_REVIEW:
                _id = db.insert(FilmContract.FilmEntry.REVIEW_TABLE_NAME, null, values);
                if (_id > 0) {
                    insertUri = FilmContract.FilmEntry.buildFilmUri(_id);
                } else {
                    throw new SQLException("Error inserting Uri to database: " + uri);
                }
                break;
            case MOVIE_TRAILER:
                _id = db.insert(FilmContract.FilmEntry.TRAILER_TABLE_NAME, null, values);
                if (_id > 0) {
                    insertUri = FilmContract.FilmEntry.buildFilmUri(_id);
                } else {
                    throw new SQLException("Error inserting Uri to database: " + uri);
                }
                break;
            case FAVORITES:
                _id = db.insert(FilmContract.FilmEntry.FAVORITES_TABLE_NAME, null, values);
                if (_id > 0) {
                    insertUri = FilmContract.FilmEntry.buildFilmUri(_id);
                } else {
                    throw new SQLException("Error inserting Uri to database: " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri);

        }
        getContext().getContentResolver().notifyChange(uri, null);

        return insertUri;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        SQLiteDatabase db = mOpenDBHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);

        // bulkInsert needs a Transaction because it's a longer process?
        switch (match) {
            case MOVIE:
                db.beginTransaction();
                int count = 0;
                try {
                    // Iterate each value of ContentValues array
                    for (ContentValues singleValue : values) {
                        long _id = db.insert(FilmContract.FilmEntry.FILM_TABLE_NAME, null, singleValue);
                        // +1 to count only if insert is successful
                        if (_id != -1) {
                            count++;
                        }
                    }
                    db.setTransactionSuccessful();
                    // Catch block already exists inside insert method
                } finally {
                    db.endTransaction();
                }
                return count;
            case MOVIE_REVIEW:
                db.beginTransaction();
                count = 0;
                Log.v("FilmProvider", "bulk insert");
                try {
                    // Iterate each value of ContentValues array
                    for (ContentValues singleValue : values) {
                        long _id = db.insert(FilmContract.FilmEntry.REVIEW_TABLE_NAME, null, singleValue);
                        // +1 to count only if insert is successful
                        if (_id != -1) {
                            count++;
                        }
                    }
                    db.setTransactionSuccessful();
                    // Catch block already exists inside insert method
                } finally {
                    getContext().getContentResolver().notifyChange(uri, null);
                    db.endTransaction();
                }
                return count;
            case MOVIE_TRAILER:
                db.beginTransaction();
                count = 0;
                try {
                    // Iterate each value of ContentValues array
                    for (ContentValues singleValue : values) {
                        long _id = db.insert(FilmContract.FilmEntry.TRAILER_TABLE_NAME, null, singleValue);
                        // +1 to count only if insert is successful
                        if (_id != -1) {
                            count++;
                        }
                    }
                    db.setTransactionSuccessful();
                    // Catch block already exists inside insert method
                } finally {
                    getContext().getContentResolver().notifyChange(uri, null);
                    db.endTransaction();
                }
                return count;
            default:
                return super.bulkInsert(uri, values);
        }

    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mOpenDBHelper.getWritableDatabase();
        int updateRow;
        int match = sUriMatcher.match(uri);

        switch (match) {
            case MOVIE:
                updateRow = db.update(FilmContract.FilmEntry.FILM_TABLE_NAME, values, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri);
        }

        return updateRow;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mOpenDBHelper.getWritableDatabase();
        int deletedRow;
        int match = sUriMatcher.match(uri);


        // _id of the inserted row. If it exists, create uri with _id, else throw SQLException
        switch (match) {
            case MOVIE:
                deletedRow = db.delete(FilmContract.FilmEntry.FILM_TABLE_NAME, selection, selectionArgs);
                break;
            case FAVORITES:
                deletedRow = db.delete(FilmContract.FilmEntry.FAVORITES_TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri);

        }




        if (deletedRow != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return deletedRow;
    }


    // UriMatcher matches the Uri to appropriate type
    public static UriMatcher buildUriMatcher() {
        // s suffix denotes static field name
        final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = FilmContract.CONTENT_AUTHORITY;

        // addURI(String authority, String path, int code)
        uriMatcher.addURI(authority, FilmContract.PATH_MOVIE, MOVIE);
        uriMatcher.addURI(authority, FilmContract.PATH_MOVIE + "/#", MOVIE_WITH_TITLE);
        uriMatcher.addURI(authority, FilmContract.PATH_MOVIE + "/#/" + Utility.PATH_REVIEW, MOVIE_REVIEW);
        uriMatcher.addURI(authority, FilmContract.PATH_MOVIE + "/#/" + Utility.PATH_TRAILER, MOVIE_TRAILER);
        uriMatcher.addURI(authority, FilmContract.PATH_FAVORITES, FAVORITES);
        uriMatcher.addURI(authority, FilmContract.PATH_FAVORITES + "/#", FAVORITES_WITH_TITLE);


        return uriMatcher;
    }

    @Override
    @TargetApi(11)
    public void shutdown() {
        mOpenDBHelper.close();
        super.shutdown();
    }

}
