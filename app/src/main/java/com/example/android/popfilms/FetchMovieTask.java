package com.example.android.popfilms;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.example.android.popfilms.data.FilmContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Vector;

/**
 * Created by jerye on 12/2/2016.
 */

public class FetchMovieTask extends AsyncTask<Void, Void, String> {
    // Log tag
    private final String LOG_TAG = FetchMovieTask.class.getSimpleName();
    // Context member
    private final Context mContext;

    // Constructor
    public FetchMovieTask(Context context) {
        mContext = context;
    }


    // Custom AsyncTask class to fetch JSON string from TheMovieDB
    @Override
    protected String doInBackground(Void... params) {

        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String popfilmJsonString = null;


        try {

            // Parameters for Query call
            final String MOVIE_BASE_URL = "https://api.themoviedb.org/3/movie/" + Utility.getSortingPreference(mContext);
            final String API_KEY = "api_key";
            final String LANGUAGE_PARAM = "language";
            final String REGION = "region";

            // Build the Uri for TheMovieDB API call by popular descending order
            Uri buildUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                    .appendQueryParameter(API_KEY, BuildConfig.THE_MOVIE_DB_API_KEY)
                    .appendQueryParameter(LANGUAGE_PARAM, "en-US")
                    .appendQueryParameter(REGION, "Mountain View")
                    .build();

            URL url = new URL(buildUri.toString());

            // Request from TheMovieDB using url
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read input stream into string
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // parsing string?
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // if string is empty. No point in parsing.
                return null;
            }
            popfilmJsonString = buffer.toString();

        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attemping
            // to parse it.
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }

        try {
            return getMovieDataFromJson(popfilmJsonString);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }

        return null;

    }

    // Construct a method to convert JSON string in to Object hierarchy.
    private String getMovieDataFromJson(String movieJsonString) throws JSONException {

        // JSON arrays that contains all our objects
        final String TMDB_RESULTS = "results";

        // Names of JSON Objects that we need
        final String TMDB_POSTER_PATH = "poster_path";
        final String TMDB_BACKDROP_PATH = "backdrop_path";
        final String TMDB_OVERVIEW = "overview";
        final String TMDB_RELEASE_DATE = "release_date";
        final String TMDB_ORIGINAL_TITLE = "original_title";
        final String TMDB_VOTE_AVERAGE = "vote_average";

        // Storage variable for JSON objects
        String posterPath;
        String backdropPath;
        String description;
        String releaseDate;
        String title;
        double userRating;

        // Create vector of contentvalues bin with length of 5 for now
        Vector<ContentValues> cvVector = new Vector<ContentValues>();

        // Put all JSON operations inside try/catch block to catch exceptions
        try {

            // Create JSON Object and Array
            JSONObject filmJson = new JSONObject(movieJsonString);
            JSONArray resultsArray = filmJson.getJSONArray(TMDB_RESULTS);

            for(int i=0;i<resultsArray.length();i++) {
                // Obtain values
                JSONObject movieArray = resultsArray.getJSONObject(i);
                posterPath = movieArray.getString(TMDB_POSTER_PATH);
                backdropPath = movieArray.getString(TMDB_BACKDROP_PATH);
                description = movieArray.getString(TMDB_OVERVIEW);
                releaseDate = movieArray.getString(TMDB_RELEASE_DATE);
                title = movieArray.getString(TMDB_ORIGINAL_TITLE);
                userRating = movieArray.getDouble(TMDB_VOTE_AVERAGE);

                // Key-Value pairs creation
                ContentValues filmValues = new ContentValues();
                filmValues.put(FilmContract.FilmEntry.COLUMN_POSTER_PATH, posterPath);
                filmValues.put(FilmContract.FilmEntry.COLUMN_BACKDROP_PATH, backdropPath);
                filmValues.put(FilmContract.FilmEntry.COLUMN_OVERVIEW, description);
                filmValues.put(FilmContract.FilmEntry.COLUMN_RELEASE_DATE, releaseDate);
                filmValues.put(FilmContract.FilmEntry.COLUMN_ORIGINAL_TITLE, title);
                filmValues.put(FilmContract.FilmEntry.COLUMN_VOTE_AVERAGE, userRating);

                // Add key-value pairs to our vector bin
                cvVector.add(filmValues);
            }
            // Create ContentValue array if size is > 0 and bulkInsert
            if (cvVector.size() > 0) {
                Log.v(LOG_TAG, "Vector Size: " + cvVector.size());
                ContentValues[] cvArray = new ContentValues[cvVector.size()];
                // Transfer content of vector array to ContentValues array?
                cvVector.toArray(cvArray);
                mContext.getContentResolver().delete(FilmContract.FilmEntry.CONTENT_URI,null,null);
                mContext.getContentResolver().bulkInsert(FilmContract.FilmEntry.CONTENT_URI,cvArray);
            }



        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
        return null;
    }
}
