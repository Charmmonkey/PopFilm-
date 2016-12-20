package com.example.android.popfilms;

import android.content.Context;
import android.database.Cursor;
import android.media.Image;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.ImageView;

import com.example.android.popfilms.data.FilmContract;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jerye on 12/10/2016.
 */

public class PosterAdapter extends CursorAdapter {
    private static String LOG_TAG = PosterAdapter.class.getSimpleName();

    public PosterAdapter(Context context, Cursor cursor, int flags){
        super(context,cursor,flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.grid_item,parent,false);
        Log.v(LOG_TAG, "New view created");
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        Log.v(LOG_TAG, "View has been bound");
        String posterUri = cursor.getString(FilmFragment.COL_POSTER_PATH_ID);
        ImageView posterImage = (ImageView) view.findViewById(R.id.poster);
        Picasso.with(context).load(FilmContract.buildPosterUri(posterUri)).into(posterImage);
    }

    //    public PosterAdapter(Context context, List<String> posters) {
//        super(context, 0, posters);
//    }
//
//
////    @NonNull
////    @Override
////    public View getView(int position, View convertView, ViewGroup parent) {
////        // Grabs the item (poster's string Uri) at the given position
////        String posterItem = getItem(position);
////
////        // Inflate item view if it doesn't exist
////        View currentView = convertView;
////        if (currentView == null) {
////            currentView = LayoutInflater.from(getContext()).inflate(
////                    R.layout.grid_item, parent, false);
////            Log.v(LOG_TAG, "successfully created grid item");
////        }
////        ImageView posterImage = (ImageView) currentView.findViewById(R.id.poster);
//////        Log.v(LOG_TAG,FilmContract.buildPosterUri(posterItem).toString());
////        Picasso.with(getContext()).load(FilmContract.buildPosterUri(posterItem)).into(posterImage);
////
////        return currentView;
////    }
}
