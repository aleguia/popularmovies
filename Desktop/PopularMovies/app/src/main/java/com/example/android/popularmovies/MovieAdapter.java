package com.example.android.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;

import com.example.android.popularmovies.data.MovieContract;
import com.squareup.picasso.Picasso;

import static com.squareup.picasso.Picasso.with;

/**
 * Created by Fernando on 21/11/2016.
 */

public class MovieAdapter extends CursorAdapter {

    private static final String LOG_TAG = MovieAdapter.class.getSimpleName();

    public MovieAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        View view = LayoutInflater.from(context).inflate(R.layout.grid_item_movie, parent, false);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ImageView imageView = (ImageView) view.findViewById(R.id.movie_imageview);

        String path = cursor.getString(MovieFragment.COL_MOVIE_POSTER);
//        Log.v(LOG_TAG, path);
        Picasso
                .with(context)
                .load("http://image.tmdb.org/t/p/w185/" + path)
                .into(imageView);

    }


}
