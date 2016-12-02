package com.example.android.popularmovies;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import static com.example.android.popularmovies.R.id.container;
import static com.example.android.popularmovies.R.id.detail_container;

public class DetailActivity extends AppCompatActivity {

    private static final String LOG_TAG = DetailActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        if (savedInstanceState == null) {

            Bundle arguments = new Bundle();
            arguments.putParcelable(DetailFragment.DETAIL_URI, getIntent().getData());

            DetailFragment fragment = new DetailFragment();
            fragment.setArguments(arguments);

            Bundle arguments2 = new Bundle();
            arguments2.putParcelable(TrailerAndReview.TRAILER_URI, getIntent().getData());

            TrailerAndReview fragment2 = new TrailerAndReview();
            fragment2.setArguments(arguments2);

            getSupportFragmentManager().beginTransaction()
                    .add(detail_container, fragment)
                    .add(detail_container, fragment2)
                    .commit();


        }
    }
}






