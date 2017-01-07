package com.example.android.popfilms;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;

public class DetailedFilmActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        supportRequestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);

        setContentView(R.layout.activity_detailed);
        // Add FilmFragment to MainActivity.
        if (savedInstanceState==null){
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.detailed_container, new DetailedFilmFragment()).commit();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        ActionBar mActionBar = getSupportActionBar();

        Drawable drawable = ContextCompat.getDrawable(DetailedFilmActivity.this,R.drawable.background_translucent_down);
        mActionBar.setBackgroundDrawable(drawable);
        mActionBar.setDisplayShowTitleEnabled(false);
    }


    @Override
    protected void onResume() {
        super.onResume();
        // Fixes screen rotation crash
    }
}
