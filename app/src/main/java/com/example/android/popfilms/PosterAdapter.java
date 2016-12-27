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
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        String posterUri = cursor.getString(Utility.COL_POSTER_PATH_ID + 1);
        ImageView posterImage = (ImageView) view.findViewById(R.id.poster);
        Picasso.with(context).load(Utility.buildPosterUri(posterUri)).into(posterImage);
    }

}
