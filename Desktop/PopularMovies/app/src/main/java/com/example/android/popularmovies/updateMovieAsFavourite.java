package com.example.android.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.example.android.popularmovies.data.MovieContract;

/**
 * Created by Fernando on 23/11/2016.
 */


public class UpdateMovieAsFavourite extends AsyncTask<Void, Void, Void> {

    private final String LOG_TAG = UpdateMovieAsFavourite.class.getSimpleName();

    private Movie mMovie;
    private Context mContext;


    public UpdateMovieAsFavourite(Movie mMovie, Context mContext) {
        this.mMovie = mMovie;
        this.mContext = mContext;
    }

    @Override
    protected Void doInBackground(Void... params) {

        deleteOrSaveFavoriteMovie();


        return null;
    }

    private void deleteOrSaveFavoriteMovie() {


        Cursor cursor = mContext.getContentResolver().query(
                MovieContract.FavouriteMovieEntry.CONTENT_URI,
                new String[]{MovieContract.FavouriteMovieEntry._ID},
                MovieContract.FavouriteMovieEntry.MOVIE_ID + " = ?",
                new String[]{String.valueOf(mMovie.getmOriginalId())},
                null);



        if (cursor.moveToFirst()) {


            int rowDeleted = mContext.getContentResolver().delete(MovieContract.FavouriteMovieEntry.CONTENT_URI,
                    MovieContract.FavouriteMovieEntry.MOVIE_ID + " = ?",
                    new String[]{String.valueOf(mMovie.getmOriginalId())});

            if (rowDeleted > 0) {




            }
        } else {



            ContentValues values = new ContentValues();

            values.put(MovieContract.FavouriteMovieEntry.MOVIE_ID, mMovie.getmOriginalId());
            values.put(MovieContract.FavouriteMovieEntry.COLUMN_TITLE, mMovie.getmMovieName());
            values.put(MovieContract.FavouriteMovieEntry.COLUMN_DATE, mMovie.getmReleaseDate());
            values.put(MovieContract.FavouriteMovieEntry.COLUMN_RATE, mMovie.getmRate());
            values.put(MovieContract.FavouriteMovieEntry.COLUMN_POSTER, mMovie.getmMovieImage());

            Uri insertedUri = mContext.getContentResolver().insert(
                                MovieContract.FavouriteMovieEntry.CONTENT_URI,
                                values);


        }

    cursor.close();


    }

    public boolean isAlreadyFavorite() {

        Cursor cursor =  mContext.getContentResolver().query(
                MovieContract.FavouriteMovieEntry.CONTENT_URI,
                new String[]{MovieContract.FavouriteMovieEntry._ID},
                MovieContract.FavouriteMovieEntry.MOVIE_ID + " = ?",
                new String[]{String.valueOf(mMovie.getmOriginalId())},
                null);

        if (cursor.moveToFirst()){
            return true;
        }

        else{
            return false;
        }


    }
}
