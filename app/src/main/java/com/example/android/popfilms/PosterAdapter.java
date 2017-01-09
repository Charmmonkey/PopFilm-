package com.example.android.popfilms;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Created by jerye on 12/10/2016.
 * PosterAdapter is used to populate contents in the gridview
 */

public class PosterAdapter extends CursorAdapter {
    private static String LOG_TAG = PosterAdapter.class.getSimpleName();

    // Constructor takes in cursor
    public PosterAdapter(Context context, Cursor cursor, int flags){
        super(context,cursor,flags);
    }

    // Inflate a grid_item
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.grid_item,parent,false);
        return view;
    }

    // Sets contents to views when a grid is bound
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Get data from cursor
        String posterUri = cursor.getString(Utility.COL_POSTER_PATH_ID + 1);
        String releaseDate = cursor.getString(Utility.COL_RELEASE_DATE_ID + 1);
        ImageView posterImage = (ImageView) view.findViewById(R.id.poster);
        TextView releaseDateView = (TextView) view.findViewById(R.id.release_date);

        // Converts date to proper format
        SimpleDateFormat readFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat writeFormat = new SimpleDateFormat("MMMM dd, yyyy");
        java.util.Date date;
        try{
            date = readFormat.parse(releaseDate);
            releaseDate = writeFormat.format(date);
        }catch(ParseException e){
            e.printStackTrace();
        }

        // Set text and image
        releaseDateView.setText(releaseDate);
        Picasso.with(context).load(Utility.buildPosterUri(posterUri)).into(posterImage);
    }
}
