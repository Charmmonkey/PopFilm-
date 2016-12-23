package com.example.android.popfilms;

import android.content.ContentValues;
import android.content.Context;
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
// It is also revised to handle different API calls to TMDB to include review and trailers.
public class VolleyFetcher {
    private static final String LOG_TAG = VolleyFetcher.class.getSimpleName();


    // Google Volley handles HTTP requests and parses JSONObject for you. Wrap this whole thing with a method.
    public static void volleyFetcher(String urlString, final String[] filmColumn, final Context context) {

        // Log tag
        final String LOG_TAG = VolleyFetcher.class.getSimpleName();

        // Create the request
        JsonObjectRequest jsonRequest = new JsonObjectRequest(
                Request.Method.GET,
                urlString,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.v(LOG_TAG, response.toString());
                        try{
                            JSONArray jsonArray = response.getJSONArray("results");
                            putJsonIntoSQLite(jsonArray,filmColumn,context);
                        }catch (JSONException e){
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
        Log.v(LOG_TAG,"VolleyFetcher Worked!");

    }

    /**
     * Method that handles JSONArray and JSONObject output and input into SQLiteDB
     *
     * @param jsonArray   Output from volleyFetcher
     * @param jsonEntries String array of the JSONObject names to be read for the value, also doubles as the key in the key-value pair
     */
    public static void putJsonIntoSQLite(JSONArray jsonArray, String[] jsonEntries, Context context) {
        String entryValue;

        Vector<ContentValues> cvVector = new Vector<ContentValues>();
        try {
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
                    Log.v(LOG_TAG, "entryValue: " + entryValue);
                }
                Log.v(LOG_TAG,"filmValue Size: " + filmValues.size());
                cvVector.add(filmValues);
                Log.v(LOG_TAG,"cvVector size: " + cvVector.size());
                Log.v(LOG_TAG,cvVector.toString());

            }
            if (cvVector.size() > 0) {
                ContentValues[] cvArray = new ContentValues[cvVector.size()];
                cvVector.toArray(cvArray);
                Log.v(LOG_TAG, "cvArray length: " + cvArray.length);
                context.getContentResolver().delete(FilmContract.FilmEntry.CONTENT_URI, null, null);
                context.getContentResolver().bulkInsert(FilmContract.FilmEntry.CONTENT_URI, cvArray);
            }else{
                Log.e(LOG_TAG,"cvVector.size = 0");
            }

        } catch (JSONException e) {
            e.printStackTrace();

        }
    }


}
