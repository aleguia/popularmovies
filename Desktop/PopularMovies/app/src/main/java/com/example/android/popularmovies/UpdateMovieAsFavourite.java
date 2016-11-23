package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v4.app.LoaderManager;

import com.example.android.popularmovies.data.MovieContract;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.V;
import static java.security.AccessController.getContext;

/**
 * Created by Fernando on 22/11/2016.
 */

public class UpdateMovieAsFavourite extends AsyncTask<Void,Void,Void>  {






    @Override
    protected Void doInBackground(Void... params) {

        deleteOrSaveFovouriteMovie();

    return null;}

    private void deleteOrSaveFovouriteMovie(){



        Cursor cursor = getContext().getContentResolver().query(MovieContract.FavouriteMovieEntry.CONTENT_URI,
                        new String[]{MovieContract.FavouriteMovieEntry.MOVIE_ID},
                        MovieContract.FavouriteMovieEntry.MOVIE_ID + " = ?",
                        new String[]{String.valueOf()}
                )

    }
}
