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
 */
public class FilmFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    private static PosterAdapter mPosterAdapter;
    private static final int FILM_LOADER = 0;
    private String LOG_TAG = getClass().getSimpleName();
    public FilmFragment() {
        // Required empty public constructor
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
        getLoaderManager().initLoader(FILM_LOADER,savedInstanceState,this);
    }

    // Implement Loader to query in the background and not have it tied to the UI lifecycle
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        CursorLoader loader = new CursorLoader(getContext(),FilmContract.FilmEntry.CONTENT_URI, Utility.FILM_COLUMN, null, null, null);
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


    @Override
    public void onStart() {
        super.onStart();
        Log.v(LOG_TAG, "onStart");
        updateFetcher();
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
                String itemMovieWithID = cursor.getString(Utility.COL_SPECIFIC_ID + 1);
                Intent detailedFilm = new Intent(getContext(),DetailedFilmView.class);
                // Pass the movie title Uri in the intent
                Log.v(LOG_TAG,FilmContract.FilmEntry.buildFilmUriWithId(itemMovieWithID).toString());
                detailedFilm.setData(FilmContract.FilmEntry.buildFilmUriWithId(itemMovieWithID));
                startActivity(detailedFilm);
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateFetcher();
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

    private void updateFetcher(){
        VolleyFetcher.volleyFetcher(Utility.buildFilmListUri(getContext()).toString(),Utility.ENTRY_COLUMN,getContext());
    }

}


