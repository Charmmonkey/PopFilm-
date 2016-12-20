package com.example.android.popfilms;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popfilms.data.FilmContract;
import com.squareup.picasso.Picasso;

/**
 * Created by jerye on 12/14/2016.
 */

public class DetailedFilmView extends AppCompatActivity {

    private Context mContext = DetailedFilmView.this;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.detailed_item);

//        // Move these to background thread eventually.
//        // Grab the content of these id's
        Cursor cursor = mContext.getContentResolver().query(getIntentData(), FilmFragment.FILM_COLUMN, null, null, null);

        if (cursor.moveToFirst()) {
            String detailedTitle = cursor.getString(FilmFragment.COL_ORIGINAL_TITLE_ID);
            String detailedOverview = cursor.getString(FilmFragment.COL_OVERVIEW_ID);
            String detailedReleaseDate = cursor.getString(FilmFragment.COL_RELEASE_DATE_ID);
            String detailedVoteAverage = cursor.getString(FilmFragment.COL_VOTE_AVERAGE_ID);
            String detailedBackdropPath = cursor.getString(FilmFragment.COL_BACKDROP_PATH_ID);

            // Find the views associated with id
            TextView titleView = (TextView) findViewById(R.id.detailed_title);
            TextView overviewView = (TextView) findViewById(R.id.detailed_overview);
            TextView releaseDateView = (TextView) findViewById(R.id.detailed_release_date);
            TextView voteAverageView = (TextView) findViewById(R.id.detailed_vote_average);
            ImageView backdropImageView = (ImageView) findViewById(R.id.detailed_backdrop_image);


            // Set content to respective view
            Picasso.with(DetailedFilmView.this).load(FilmContract.buildPosterUri(detailedBackdropPath)).into(backdropImageView);
            titleView.setText(detailedTitle);
            overviewView.setText(detailedOverview);
            releaseDateView.setText("Release Date: " + detailedReleaseDate);
            voteAverageView.setText("Rating: " + detailedVoteAverage + "/10");

            cursor.close();
        }

    }

    private Uri getIntentData() {
        return getIntent().getData();
    }

}
