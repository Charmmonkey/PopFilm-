package com.example.android.popfilms;


import android.content.Intent;
import android.database.Cursor;
import android.media.Image;
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
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.Volley;
import com.example.android.popfilms.data.FilmContract;
import com.example.android.popfilms.data.FilmDBHelper;

import java.util.ArrayList;

import static com.example.android.popfilms.R.id.container;

/**
 * A simple {@link Fragment} subclass.
 */
public class FilmFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    private static PosterAdapter mPosterAdapter;
    private static final int FILM_LOADER = 0;


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
        getLoaderManager().initLoader(FILM_LOADER,savedInstanceState,this);
    }

    // Implement Loader to query in the background and not have it tied to the UI lifecycle
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        CursorLoader loader = new CursorLoader(getContext(),FilmContract.FilmEntry.CONTENT_URI, FILM_COLUMN, null, null, null);
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mPosterAdapter.swapCursor(data);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Release any resources being help
        mPosterAdapter.swapCursor(null);
    }



    // Logging tag
    private String LOG_TAG = getClass().getSimpleName();
//    private static ArrayList<String> posterUriString = new ArrayList<String>();

    // String array for projection, ordered intentionally so they will return in this order
    static final String[] FILM_COLUMN = {
            FilmContract.FilmEntry.TABLE_NAME + "." + FilmContract.FilmEntry._ID,
            FilmContract.FilmEntry.COLUMN_ORIGINAL_TITLE,
            FilmContract.FilmEntry.COLUMN_OVERVIEW,
            FilmContract.FilmEntry.COLUMN_POSTER_PATH,
            FilmContract.FilmEntry.COLUMN_RELEASE_DATE,
            FilmContract.FilmEntry.COLUMN_VOTE_AVERAGE,
            FilmContract.FilmEntry.COLUMN_BACKDROP_PATH,
            FilmContract.FilmEntry.COLUMN_SPECIFIC_ID
    };

    static final String[] ENTRY_COLUMN = { FilmContract.FilmEntry.COLUMN_ORIGINAL_TITLE,
            FilmContract.FilmEntry.COLUMN_OVERVIEW,
            FilmContract.FilmEntry.COLUMN_POSTER_PATH,
            FilmContract.FilmEntry.COLUMN_RELEASE_DATE,
            FilmContract.FilmEntry.COLUMN_VOTE_AVERAGE,
            FilmContract.FilmEntry.COLUMN_BACKDROP_PATH,
            FilmContract.FilmEntry.COLUMN_SPECIFIC_ID};

    // These IDs are for matching to the cursor ID when obtaining values of the column
    static final int COL_FILM_ID = 0;
    static final int COL_ORIGINAL_TITLE_ID = 1;
    static final int COL_OVERVIEW_ID = 2;
    static final int COL_POSTER_PATH_ID = 3;
    static final int COL_RELEASE_DATE_ID = 4;
    static final int COL_VOTE_AVERAGE_ID = 5;
    static final int COL_BACKDROP_PATH_ID = 6;
    static final int COL_SPECIFIC_ID = 7;

    public FilmFragment() {
        // Required empty public constructor
    }


    @Override
    public void onStart() {
        super.onStart();
        Log.v(LOG_TAG, "onStart");
        VolleyFetcher.volleyFetcher("https://api.themoviedb.org/3/movie/popular?api_key=***REMOVED***&language=en-US",ENTRY_COLUMN,getContext());

//        FetchMovieTask fetchMovieTask = new FetchMovieTask(getContext());
//        fetchMovieTask.execute();



        // Query the database using ContentResolver and return a cursor
//        Cursor queryCursor = getContext().getContentResolver().query(FilmContract.FilmEntry.CONTENT_URI, FILM_COLUMN, null, null, null);

        // Move the cursor to row 1 ----IMPORTANT---- default cursor position is -1,  will cause error
//        if (queryCursor.moveToFirst()) {
//            posterUriString.add(queryCursor.getString(COL_POSTER_PATH_ID));
//            Log.v(LOG_TAG + " if statement", queryCursor.getString(COL_POSTER_PATH_ID));
//            while (queryCursor.moveToNext()) {
//                posterUriString.add(queryCursor.getString(COL_POSTER_PATH_ID));
//                Log.v(LOG_TAG + "while loop", queryCursor.getString(COL_POSTER_PATH_ID));
//            }
//
//        } else {
//            Log.v(LOG_TAG, "Cursor was not moved to first row");
//        }

//        queryCursor.close();

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.v(LOG_TAG, "onCreateView");

        // Create and bind ArrayAdapter to GridView
//        ArrayAdapter mArrayAdapter = new ArrayAdapter(this.getActivity(), R.layout.grid_item, words);
        // Pass in null cursor because query hasn't been made yet. We'll swap it out later after loader finishes query.
        mPosterAdapter= new PosterAdapter(this.getContext(),null,0);
//        Log.v(LOG_TAG, "onCreateView" + posterUriString.toString());
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        GridView gridView = (GridView) rootView.findViewById(R.id.grid_view_id);
        gridView.setAdapter(mPosterAdapter);


        // Start and explicit intent to the detailed page when ImageView is clicked
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // References the adapter, in this case custom ArrayAdapter which contains Strings. Need to
                // use CursorAdapter.
                Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                String itemMovieTitle = cursor.getString(COL_ORIGINAL_TITLE_ID);
                Intent detailedFilm = new Intent(getActivity(),DetailedFilmView.class);
                // Pass the movie title Uri in the intent
                detailedFilm.setData(FilmContract.FilmEntry.buildMovieUriWithTitle(itemMovieTitle));
                startActivity(detailedFilm);
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        CharSequence toastText = Utility.getSortingPreference(getContext());
        Toast.makeText(getContext(), "Sorted By: " + toastText, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.sort,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_sort:
                Intent intent = new Intent(getActivity(),SortActivity.class);
                startActivity(intent);
                return true;
        default:
            return super.onOptionsItemSelected(item);}
    }
}


