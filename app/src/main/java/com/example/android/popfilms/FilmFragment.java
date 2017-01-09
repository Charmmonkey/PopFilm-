package com.example.android.popfilms;


import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.example.android.popfilms.data.FilmContract;


/**
 * A simple {@link Fragment} subclass.
 * Main fragment. Contains GridView and releaseDate. Implements CursorLoader and swaps cursor on finished
 */
public class FilmFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    // Variables
    private static PosterAdapter mPosterAdapter;
    private static final int FILM_LOADER = 0;
    private String LOG_TAG = getClass().getSimpleName();
    private CursorLoader cursorLoader;
    private Bundle mSavedInstanceState;
    private Toast toast;

    public FilmFragment() {
        // Required empty public constructor
    }

    // Initiate Loader
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mSavedInstanceState = savedInstanceState;
        setHasOptionsMenu(true);

        getLoaderManager().initLoader(FILM_LOADER, savedInstanceState, this);

    }

    // Implement Loader to query in the background and not have it tied to the UI lifecycle
    // Type of query depends on preference
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        // Query favorites table
        if(Utility.getSortingPreference(getContext()).equals("favorites")){
            cursorLoader = new CursorLoader(getContext(),
                    FilmContract.FilmEntry.FAVORITES_URI,
                    Utility.FAVORITES_COLUMN,
                    null,
                    null,
                    null);
        }else{ // Query general table
            cursorLoader = new CursorLoader(getContext(),
                    FilmContract.FilmEntry.CONTENT_URI,
                    Utility.FILM_COLUMN,
                    null,
                    null,
                    null);
        }
        return cursorLoader;
    }

    // Swap cursor to cursorAdapter on loader finished. This populates gridview with updates
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mPosterAdapter.swapCursor(data);
    }

    // Drops cursor on reset
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Release any resources being help
        mPosterAdapter.swapCursor(null);
    }

    // Restarts loader to query data from local db
    @Override
    public void onStart() {
        super.onStart();

        // Fetches new data from TMDB if preference is popular/top_rated
        if (!Utility.getSortingPreference(getContext()).equals("favorites")) {
            updateFetcher();
            SortActivity.preferenceChanged = false;

            // Fail-safe because data insert to local DB doesn't notify loader automatically sometimes
            getLoaderManager().restartLoader(FILM_LOADER,mSavedInstanceState,this);
        }

        // If preference is favorites. Grabs data offline from local db
        else if(Utility.getSortingPreference(getContext()).equals("favorites")){
            getLoaderManager().restartLoader(FILM_LOADER,mSavedInstanceState,this);
        }
    }

    // Inflates view and sets CursorAdapter, set onClickListener
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate view
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        // Pass in null cursor because query hasn't been made yet. We'll swap it out later after loader finishes query.
        mPosterAdapter = new PosterAdapter(this.getContext(), null, 0);
        GridView gridView = (GridView) rootView.findViewById(R.id.grid_view_id);
        gridView.setAdapter(mPosterAdapter);

        // Start an explicit intent to the detailed page when ImageView is clicked
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // References the adapter, in this case custom ArrayAdapter which contains Strings. Need to
                // use CursorAdapter.
                Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                String itemMovieWithID = cursor.getString(Utility.COL_SPECIFIC_ID + 1); // +1 because IDs are mismatched on purpose
                Intent detailedFilm = new Intent(getContext(), DetailedFilmActivity.class);
                // Pass the movie title Uri in the intent
                detailedFilm.setData(FilmContract.FilmEntry.buildFilmUriWithId(itemMovieWithID));
                startActivity(detailedFilm);
            }
        });
        return rootView;
    }

    // Toast message to show sorting preference
    @Override
    public void onResume() {
        super.onResume();

        CharSequence toastText = Utility.getSortingPreference(getContext());
        toast = Toast.makeText(getContext(), "Sorted By: " + toastText, Toast.LENGTH_SHORT);
        toast.show();
    }

    // Toast is killed if page is paused
    @Override
    public void onPause() {
        super.onPause();
        toast.cancel();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    // Inflate settings menu
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.sort, menu);
    }

    // Creates intent for menu press
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sort:
                Intent intent = new Intent(getActivity(), SortActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // Method to call our custom Volley class (data fetcher)
    private void updateFetcher() {
        VolleyFetcher.volleyFetcher(Utility.buildFilmListUri(getContext()).toString(), Utility.ENTRY_COLUMN, getContext());
    }

}


