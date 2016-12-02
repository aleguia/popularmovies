package com.example.android.popularmovies;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.facebook.stetho.Stetho;

public class MainActivity extends AppCompatActivity implements MovieFragment.Callback {

    private static final String DETAILFRAGMENT_TAG = "DFTAG";
    private static final String TRAILER_TAG = "TFTAG";
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
                        .build());


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (findViewById(R.id.detail_container) != null) {

            mTwoPane = true;

            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.container, new MovieFragment())
                        .add(R.id.detail_container, new DetailFragment(), DETAILFRAGMENT_TAG)
                        .add(R.id.detail_container, new TrailerAndReview(), TRAILER_TAG)
                        .commit();
            }

        } else {

            mTwoPane = false;

            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.container, new MovieFragment())
                        .commit();
            }
        }
    }

    @Override
    public void onItemSelected(Uri contentUri) {

        if (mTwoPane) { // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle args = new Bundle();
            args.putParcelable(DetailFragment.DETAIL_URI, contentUri);

            DetailFragment fragment = new DetailFragment();
            fragment.setArguments(args);

            Bundle arguments2 = new Bundle();
            arguments2.putParcelable(TrailerAndReview.TRAILER_URI, contentUri);

            TrailerAndReview fragment2 = new TrailerAndReview();
            fragment2.setArguments(arguments2);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_container, fragment, DETAILFRAGMENT_TAG)
                    .add(R.id.detail_container,fragment2, TRAILER_TAG)
                    .commit();
        } else {
            Intent intent = new Intent(this, DetailActivity.class)
                    .setData(contentUri);
            startActivity(intent);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}
