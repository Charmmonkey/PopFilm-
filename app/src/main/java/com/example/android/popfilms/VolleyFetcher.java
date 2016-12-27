package com.example.android.popfilms;

import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.net.Uri;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.android.popfilms.data.FilmContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Vector;

/**
 * Created by jerye on 12/21/2016.
 */

// VolleyFetcher is a modification of the original FetchMovieTask. It eliminates the use of AsyncTask and simplifies the code.
// It is also revised to handle different API calls to TMDB and inserts in to corresponding data tables in our DB.
public class VolleyFetcher {
    private static final String LOG_TAG = VolleyFetcher.class.getSimpleName();

    // Google Volley handles HTTP requests and parses JSONObject for you. Wrap this whole thing with a method.
    public static void volleyFetcher(final String uriString, final String[] filmColumn, final Context context) {

        // Log tag
        final String LOG_TAG = VolleyFetcher.class.getSimpleName();
        Log.v(LOG_TAG,"uriString: " + uriString);
        // Create the request
        JsonObjectRequest jsonRequest = new JsonObjectRequest(
                Request.Method.GET,
                uriString,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("results");
                            putJsonIntoSQLite(uriString, jsonArray, filmColumn, context);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();

                    }
                }
        );
        // Add new request to the queue
        Volley.newRequestQueue(context).add(jsonRequest);
        Log.v(LOG_TAG, "VolleyFetcher Worked!");

    }

    /**
     * Method that handles JSONArray and JSONObject output and insert into SQLiteDB
     *
     * @param uriString   String input of the uri call to TMDB API
     * @param jsonArray   Output from volleyFetcher. Contains the array of movies, each with a list of requested data.
     * @param jsonEntries String array of the JSONObject names to be read for the value, also doubles as the key in the key-value pair
     * @param context     context
     */
    public static void putJsonIntoSQLite(String uriString, JSONArray jsonArray, String[] jsonEntries, Context context) {
        String entryValue;

        Uri uri = Uri.parse(uriString);
        String movieIdString = uri.getPathSegments().get(2);

        // Try catch block to create movieId if existed in API call
        int movieId = 0;
        try{
            movieId = Integer.valueOf(movieIdString);
        }catch(NumberFormatException e){
            Log.v(LOG_TAG, "Failed to retrieve value of string");
        }

        Vector<ContentValues> cvVector = new Vector<ContentValues>();
        try {

            // Double loop construction iterates through all the entries of all the movies to create KV pair
            for (int i = 0; i < jsonArray.length(); i++) {
                ContentValues filmValues = new ContentValues();

                // Get JSONObject for each movie in the array
                JSONObject eachMovieObject = jsonArray.getJSONObject(i);

                // Each movie has entries that we want. Iterate through each entry and store as KV pairs
                for (int j = 0; j < jsonEntries.length; j++) {
                    // Get the string value associated with the jsonEntry name
                    entryValue = eachMovieObject.getString(jsonEntries[j]);
                    // Put key-value pair into filmValues
                    filmValues.put(jsonEntries[j], entryValue);
                }

                // Add additional KV pair of movie id if fetching for Review or Trailers
                if(movieId != 0){
                    filmValues.put("id", movieId);
                }
                cvVector.add(filmValues);
            }

            ContentValues[] cvArray = new ContentValues[cvVector.size()];
            cvVector.toArray(cvArray);

            // Types of Uri
            // https://api.themoviedb.org/3/movie/346672/reviews?
            // https://api.themoviedb.org/3/movie/popular?
            // https://api.themoviedb.org/3/movie/top_rated?
            // https://api.themoviedb.org/3/movie/346672/videos?

            // Uri pattern: <scheme>://<authority><absolute path>?<query>#<fragment>
            // Switch case handles different API calls and inserts into appropriate DB

            switch (uri.getLastPathSegment()) {
                case "popular":
                    // Delete db entries before inserting
                    context.getContentResolver().delete(FilmContract.FilmEntry.CONTENT_URI, null, null);
                    context.getContentResolver().bulkInsert(FilmContract.FilmEntry.CONTENT_URI, cvArray);
                    break;
                case "top_rated":
                    // Delete db entries before inserting
                    context.getContentResolver().delete(FilmContract.FilmEntry.CONTENT_URI, null, null);
                    context.getContentResolver().bulkInsert(FilmContract.FilmEntry.CONTENT_URI, cvArray);
                    break;
                case Utility.PATH_REVIEW:
                    // Insert into Review data table. Uri will be handled by ContentProvider's UriMatcher
                    context.getContentResolver().bulkInsert(FilmContract.FilmEntry.buildReviewContentUriWithId(movieIdString), cvArray);
                    break;
                case Utility.PATH_TRAILER:
                    // Insert into Trailer data table. Uri will be handled by ContentProvider's UriMatcher
                    context.getContentResolver().bulkInsert(FilmContract.FilmEntry.buildTrailerContentUriWithId(movieIdString), cvArray);
                    break;
                default:
            }



        } catch (JSONException e) {
            e.printStackTrace();

        }
    }

}
