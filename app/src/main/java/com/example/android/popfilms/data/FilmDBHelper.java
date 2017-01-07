package com.example.android.popfilms.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import static com.example.android.popfilms.data.FilmContract.FilmEntry.FAVORITES_TABLE_NAME;
import static com.example.android.popfilms.data.FilmContract.FilmEntry.FILM_TABLE_NAME;
import static com.example.android.popfilms.data.FilmContract.FilmEntry.REVIEW_TABLE_NAME;
import static com.example.android.popfilms.data.FilmContract.FilmEntry.TRAILER_TABLE_NAME;

/**
 * Created by jerye on 12/4/2016.
 */

// FilmDBHelper is used to help us create the database tables and manage its versions.
public class FilmDBHelper extends SQLiteOpenHelper {
    // Log tag
    private String LOG_TAG = FilmDBHelper.class.getSimpleName();

    // Constructor for our class
    public FilmDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Name and version of database
    private static final int DATABASE_VERSION = 8;
    static final String DATABASE_NAME = "film.db";


    // This is where the database table is created
    @Override
    public void onCreate(SQLiteDatabase db) {
        // film Table containing 20 films' general information
        final String SQL_CREATE_FILM_TABLE =
                "CREATE TABLE " + FILM_TABLE_NAME + "("
                        + FilmContract.FilmEntry._ID + " INT AUTO_INCREMENT,"
                        + FilmContract.FilmEntry.COLUMN_ORIGINAL_TITLE + " TEXT NOT NULL,"
                        + FilmContract.FilmEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL,"
                        + FilmContract.FilmEntry.COLUMN_OVERVIEW + " TEXT NOT NULL,"
                        + FilmContract.FilmEntry.COLUMN_VOTE_AVERAGE + " REAL NOT NULL,"
                        + FilmContract.FilmEntry.COLUMN_POSTER_PATH + " TEXT NOT NULL, "
                        + FilmContract.FilmEntry.COLUMN_BACKDROP_PATH + " TEXT NOT NULL, "
                        + FilmContract.FilmEntry.COLUMN_SPECIFIC_ID + " REAL NOT NULL, "
                        + FilmContract.FilmEntry.COLUMN_FAVORITE + " REAL"
                        + ");";
        db.execSQL(SQL_CREATE_FILM_TABLE);

        // Table for review of each film
        final String SQL_CREATE_REVIEW_TABLE =
                "CREATE TABLE " + REVIEW_TABLE_NAME + "("
                        + FilmContract.FilmEntry.COLUMN_SPECIFIC_ID + " TEXT NOT NULL,"
                        + FilmContract.FilmEntry.COLUMN_REVIEW_AUTHOR + " TEXT NOT NULL,"
                        + FilmContract.FilmEntry.COLUMN_REVIEW_CONTENT + " TEXT NOT NULL"
                        + ");";
        db.execSQL(SQL_CREATE_REVIEW_TABLE);

        // Table for trailer of each film
        final String SQL_CREATE_TRAILER_TABLE =
                "CREATE TABLE " + TRAILER_TABLE_NAME + "("
                        + FilmContract.FilmEntry.COLUMN_SPECIFIC_ID + " TEXT NOT NULL,"
                        + FilmContract.FilmEntry.COLUMN_TRAILER_NAME + " TEXT NOT NULL,"
                        + FilmContract.FilmEntry.COLUMN_TRAILER_KEY + " TEXT NOT NULL"
                        + ");";
        db.execSQL(SQL_CREATE_TRAILER_TABLE);


        final String SQL_CREATE_FAVORITES_TABLE =
                "CREATE TABLE " + FAVORITES_TABLE_NAME + "("
                        + FilmContract.FilmEntry._ID + " INT AUTO_INCREMENT,"
                        + FilmContract.FilmEntry.COLUMN_ORIGINAL_TITLE + " TEXT NOT NULL,"
                        + FilmContract.FilmEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL,"
                        + FilmContract.FilmEntry.COLUMN_OVERVIEW + " TEXT NOT NULL,"
                        + FilmContract.FilmEntry.COLUMN_VOTE_AVERAGE + " REAL NOT NULL,"
                        + FilmContract.FilmEntry.COLUMN_POSTER_PATH + " TEXT, "
                        + FilmContract.FilmEntry.COLUMN_BACKDROP_PATH + " TEXT NOT NULL, "
                        + FilmContract.FilmEntry.COLUMN_SPECIFIC_ID + " REAL NOT NULL, "
                        + FilmContract.FilmEntry.COLUMN_FAVORITE + " REAL"
                        + ");";
        db.execSQL(SQL_CREATE_FAVORITES_TABLE);

    }

    // Update when database version changes
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        dropTable(db);

    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        dropTable(db);
    }

    public void dropTable(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + FilmContract.FilmEntry.FILM_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + FilmContract.FilmEntry.REVIEW_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + FilmContract.FilmEntry.TRAILER_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + FilmContract.FilmEntry.FAVORITES_TABLE_NAME);

        onCreate(db);
    }
}
