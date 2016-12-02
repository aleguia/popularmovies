package com.example.android.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Fernando on 15/11/2016.
 */

public class MovieDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 4;

    static final String DATABASE_NAME = "movie.db";

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " + MovieContract.MovieEntry.TABLE_NAME + " (" +
                MovieContract.MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MovieContract.MovieEntry.MOVIE_ID + " INTEGER NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_TITLE + " TEXT UNIQUE NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_DATE + " TEXT NOT NULL, "+
                MovieContract.MovieEntry.COLUMN_POSTER + " TEXT NOT NULL, "+
                MovieContract.MovieEntry.COLUMN_RATE + " REAL NOT NULL "+
                " );";


        final String SQL_CREATE_FAVOURITE_MOVIE_TABLE = "CREATE TABLE " + MovieContract.FavouriteMovieEntry.TABLE_NAME + " (" +
                MovieContract.FavouriteMovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MovieContract.FavouriteMovieEntry.MOVIE_ID + " INTEGER NOT NULL, " +
                MovieContract.FavouriteMovieEntry.COLUMN_TITLE + " TEXT UNIQUE NOT NULL, " +
                MovieContract.FavouriteMovieEntry.COLUMN_DATE + " TEXT NOT NULL, "+
                MovieContract.FavouriteMovieEntry.COLUMN_POSTER + " TEXT NOT NULL, "+
                MovieContract.FavouriteMovieEntry.COLUMN_RATE + " REAL NOT NULL "+
                " );";

        db.execSQL(SQL_CREATE_MOVIE_TABLE);
        db.execSQL(SQL_CREATE_FAVOURITE_MOVIE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MovieContract.MovieEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + MovieContract.FavouriteMovieEntry.TABLE_NAME);
        onCreate(db);
    }
}
