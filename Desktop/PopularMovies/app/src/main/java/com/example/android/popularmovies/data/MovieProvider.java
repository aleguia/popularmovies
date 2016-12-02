package com.example.android.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;


/**
 * Created by Fernando on 15/11/2016.
 */

public class MovieProvider extends ContentProvider {

    private static final String LOG_TAG = MovieProvider.class.getSimpleName();
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private SQLiteOpenHelper mOpenHelper;


    // Codes for the UriMatcher //////
    private static final int MOVIE = 100;
    private static final int MOVIE_WITH_ID = 200;
    private static final int FAVOURITE_MOVIE = 300;
    private static final int FAVOURITE_MOVIE_WITH_ID = 400;
    ////////

    private static UriMatcher buildUriMatcher(){
        // Build a UriMatcher by adding a specific code to return based on a match
        // It's common to use NO_MATCH as the code for this case.
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, MovieContract.MovieEntry.TABLE_NAME, MOVIE);

        //com.example.android.popularmovies.app.movie
        matcher.addURI(authority, MovieContract.MovieEntry.TABLE_NAME + "/#", MOVIE_WITH_ID);

        matcher.addURI(authority, MovieContract.FavouriteMovieEntry.TABLE_NAME, FAVOURITE_MOVIE);

        matcher.addURI(authority, MovieContract.FavouriteMovieEntry.TABLE_NAME + "/#", FAVOURITE_MOVIE_WITH_ID);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new MovieDbHelper(getContext());
        return true;
    }



    @Override
    public String getType(Uri uri){
        final int match = sUriMatcher.match(uri);

        switch (match){
            case MOVIE:{
                return MovieContract.MovieEntry.CONTENT_TYPE;
            }
            case MOVIE_WITH_ID:{
                return MovieContract.MovieEntry.CONTENT_ITEM_TYPE;
            }

            case FAVOURITE_MOVIE:{
                return MovieContract.FavouriteMovieEntry.CONTENT_TYPE;
            }

            case FAVOURITE_MOVIE_WITH_ID:{
                return MovieContract.FavouriteMovieEntry.CONTENT_ITEM_TYPE;
            }

            default:{
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            // All Movies selected
            case MOVIE: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);

                break;
            }
            // Individual movie based on Id selected
            case MOVIE_WITH_ID: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.MovieEntry.TABLE_NAME,
                        projection,
                        MovieContract.MovieEntry._ID + " = ?",
                        new String[]{String.valueOf(ContentUris.parseId(uri))},
                        null,
                        null,
                        sortOrder);

                break;
            }

            case FAVOURITE_MOVIE: {

                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.FavouriteMovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);

                break;
            }


            case FAVOURITE_MOVIE_WITH_ID: {

                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.FavouriteMovieEntry.TABLE_NAME,
                        projection,
                        MovieContract.FavouriteMovieEntry._ID + " = ?",
                        new String[]{String.valueOf(ContentUris.parseId(uri))},
                        null,
                        null,
                        sortOrder);

                break;
            }
            default: {
                // By default, we assume a bad URI
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }

        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }




    @Override
    public Uri insert(Uri uri, ContentValues values){
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        Uri returnUri;
        switch (sUriMatcher.match(uri)) {
            case MOVIE: {
                long _id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, values);
                // insert unless it is already contained in the database
                if (_id > 0) {
                    returnUri = MovieContract.MovieEntry.buildMovieUri(_id);
                } else {
                    throw new SQLException("Failed to insert row into: " + uri);
                }
                break;
            }

                case FAVOURITE_MOVIE: {
                    long _id = db.insert(MovieContract.FavouriteMovieEntry.TABLE_NAME, null, values);
                    // insert unless it is already contained in the database
                    if (_id > 0) {
                        returnUri = MovieContract.FavouriteMovieEntry.buildFavoriteMovieUri(_id);
                    } else {
                        throw new SQLException("Failed to insert row into: " + uri);
                    }
                    break;
            }

            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);

            }
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs){
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int numDeleted;
        switch(match){
            case MOVIE:
                numDeleted = db.delete(
                        MovieContract.MovieEntry.TABLE_NAME, selection, selectionArgs);
                // reset _ID
                db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" +
                        MovieContract.MovieEntry.TABLE_NAME + "'");
                break;
            case MOVIE_WITH_ID:
                numDeleted = db.delete(MovieContract.MovieEntry.TABLE_NAME,
                        MovieContract.MovieEntry._ID + " = ?",
                        new String[]{String.valueOf(ContentUris.parseId(uri))});
                // reset _ID
                db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" +
                        MovieContract.MovieEntry.TABLE_NAME + "'");

                break;

            case FAVOURITE_MOVIE:
                numDeleted = db.delete(
                        MovieContract.FavouriteMovieEntry.TABLE_NAME, selection, selectionArgs);
                // reset _ID
                db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" +
                        MovieContract.FavouriteMovieEntry.TABLE_NAME + "'");
                break;


            case FAVOURITE_MOVIE_WITH_ID:
            numDeleted = db.delete(MovieContract.FavouriteMovieEntry.TABLE_NAME,
                    MovieContract.FavouriteMovieEntry._ID + " = ?",
                    new String[]{String.valueOf(ContentUris.parseId(uri))});
            // reset _ID
            db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" +
                    MovieContract.FavouriteMovieEntry.TABLE_NAME + "'");

            break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        return numDeleted;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values){
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch(match){
            case MOVIE:
                // allows for multiple transactions
                db.beginTransaction();

                // keep track of successful inserts
                int numInserted = 0;
                try{
                    for(ContentValues value : values){
                        if (value == null){
                            throw new IllegalArgumentException("Cannot have null content values");
                        }
                        long _id = -1;
                        try{
                            _id = db.insertOrThrow(MovieContract.MovieEntry.TABLE_NAME,
                                    null, value);
                        }catch(SQLiteConstraintException e) {
                            Log.w(LOG_TAG, "Attempting to insert " +
                                    value.getAsString(
                                            MovieContract.MovieEntry.MOVIE_ID)
                                    + " but value is already in database.");
                        }
                        if (_id != -1){
                            numInserted++;
                        }
                    }
                    if(numInserted > 0){
                        // If no errors, declare a successful transaction.
                        // database will not populate if this is not called
                        db.setTransactionSuccessful();
                    }
                } finally {
                    // all transactions occur at once
                    db.endTransaction();
                }
                if (numInserted > 0){
                    // if there was successful insertion, notify the content resolver that there
                    // was a change
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return numInserted;
            default:
                return super.bulkInsert(uri, values);
        }
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs){
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int numUpdated = 0;

        if (contentValues == null){
            throw new IllegalArgumentException("Cannot have null content values");
        }

        switch(sUriMatcher.match(uri)){
            case MOVIE:{
                numUpdated = db.update(MovieContract.MovieEntry.TABLE_NAME,
                        contentValues,
                        selection,
                        selectionArgs);
                break;
            }
            case MOVIE_WITH_ID: {
                numUpdated = db.update(MovieContract.MovieEntry.TABLE_NAME,
                        contentValues,
                        MovieContract.MovieEntry._ID + " = ?",
                        new String[] {String.valueOf(ContentUris.parseId(uri))});
                break;
            }
            default:{
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }

        if (numUpdated > 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return numUpdated;
    }


}
