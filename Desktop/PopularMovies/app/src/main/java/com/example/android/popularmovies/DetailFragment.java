package com.example.android.popularmovies;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmovies.data.MovieContract;
import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import static android.R.attr.data;
import static com.example.android.popularmovies.R.id.container;

/**
 * Created by Fernando on 27/11/2016.
 */

public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private final String LOG_TAG = DetailFragment.class.getSimpleName();

    static final String DETAIL_URI = "URI";

    private Uri mUri;

    private static final int DETAIL_LOADER = 0;

    private static final String[] MOVIE_COLUMNS = {

            MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.MOVIE_ID,
            MovieContract.MovieEntry.COLUMN_TITLE,
            MovieContract.MovieEntry.COLUMN_DATE,
            MovieContract.MovieEntry.COLUMN_POSTER,
            MovieContract.MovieEntry.COLUMN_RATE
    };

    static final int COL_MOVIE_ID = 0;
    static final int COL_ORIGINAL_ID = 1;
    static final int COL_MOVIE_TITLE = 2;
    static final int COL_MOVIE_DATE = 3;
    static final int COL_MOVIE_POSTER = 4;
    static final int COL_MOVIE_RATE = 5;


    public DetailFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle arguments = getArguments();

        if(arguments!=null){

            mUri = arguments.getParcelable(DetailFragment.DETAIL_URI);

        }

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {


        if(null!=mUri){
        return new CursorLoader(getActivity(),
                mUri,
                MOVIE_COLUMNS, null, null, null);
        }

        return null;

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        Log.v(LOG_TAG, "In onLoadFinished");


        if(data != null && data.moveToFirst()){
        ImageView img = (ImageView) getView().findViewById(R.id.detail_imageview);

        Picasso
                .with(getActivity())
                .load("http://image.tmdb.org/t/p/w185/" + data.getString(COL_MOVIE_POSTER))
                .placeholder(R.drawable.family_father)
                .into(img);

        ((TextView) getView().findViewById(R.id.title_text_view)).setText(data.getString(COL_MOVIE_TITLE));
//          ((TextView) getView().findViewById(R.id.movie_overview_text_view)).setText(data.getString(COL));

        String input = data.getString(COL_MOVIE_DATE);
        String[] dateFormat = input.split("-");

        ((TextView) getView().findViewById(R.id.release_year_textview)).setText(dateFormat[0]);

        ((TextView) getView().findViewById(R.id.rate_textview)).setText(data.getFloat(COL_MOVIE_RATE) + "/10");

        final Movie movie = new Movie(data.getString(COL_MOVIE_POSTER),
                data.getString(COL_MOVIE_TITLE),
                data.getString(COL_MOVIE_DATE),
                data.getFloat(COL_MOVIE_RATE),
                data.getInt(COL_ORIGINAL_ID));

        final ImageButton imageButton = (ImageButton) getView().findViewById(R.id.imageButton);


        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Toast.makeText(getContext(), "Se añadió a favoritos", Toast.LENGTH_SHORT).show();


                UpdateMovieAsFavourite updateFavorite = new UpdateMovieAsFavourite(movie, getContext());
                updateFavorite.execute();


                if (updateFavorite.isAlreadyFavorite()) {
                    imageButton.setBackgroundColor(Color.YELLOW);
                } else {
                    imageButton.setBackgroundColor(Color.BLACK);
                }
            }
        });

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {


    }


}





