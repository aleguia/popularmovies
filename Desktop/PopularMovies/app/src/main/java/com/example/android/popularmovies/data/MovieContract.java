package com.example.android.popularmovies.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

import static android.R.attr.id;
import static com.example.android.popularmovies.data.MovieContract.BASE_CONTENT_URI;
import static com.example.android.popularmovies.data.MovieContract.CONTENT_AUTHORITY;
import static com.example.android.popularmovies.data.MovieContract.PATH_MOVIE;

/**
 * Created by Fernando on 14/11/2016.
 */

public class MovieContract {

    public static final String CONTENT_AUTHORITY = "com.example.android.popularmovies.app";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_MOVIE = "movie";

    public static final String PATH_FAVOURITE_MOVIE = "favourite_movie";




    /* Inner class that defines the table contents of the movie table */
    public static final class MovieEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;
        //Table name
        public static final String TABLE_NAME = "movie";

        //The id column for movie table provided by theopenmoviedb

        public static final String MOVIE_ID = "movie_id";

        public static final String COLUMN_TITLE ="title";

        public static final String COLUMN_DATE = "release_date";

        public static final String COLUMN_POSTER = "poster_path";

        public static final String COLUMN_RATE = "vote_average";


        public static Uri buildMovieUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI,id);
        }

    }

    public static final class FavouriteMovieEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVOURITE_MOVIE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FAVOURITE_MOVIE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FAVOURITE_MOVIE;
        //Table name
        public static final String TABLE_NAME = "movie";

        //The id column for movie table provided by theopenmoviedb

        public static final String MOVIE_ID = "movie_id";

        public static final String COLUMN_TITLE ="title";

        public static final String COLUMN_DATE = "release_date";

        public static final String COLUMN_POSTER = "poster_path";

        public static final String COLUMN_RATE = "vote_average";

        //the order by setting that´s going to be sent to the api
        public static final String ORDER_BY = "orderby";

        public static Uri buildMovieUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI,id);
        }

        public static Uri buildMovieWithIdUri(long id){
            return CONTENT_URI.buildUpon().appendPath(Long.toString(id)).build();
        }
    }
}
