package com.example.android.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.Image;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.Toast;

import com.example.android.popularmovies.data.MovieContract;
import com.facebook.stetho.Stetho;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Vector;

import static android.R.attr.id;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;
import static android.icu.text.UnicodeSet.CASE;
import static android.os.Build.VERSION_CODES.M;
import static java.lang.reflect.Array.getInt;
import static java.security.AccessController.getContext;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = MovieFragment.class.getSimpleName();


    private MovieAdapter mMovieAdapter;
    private static final int MOVIE_LOADER = 0;
    private static final int FAVORITE_MOVIE_LOADER = 1;
    private Uri movieUri;

    private static final String[] MOVIE_COLUMNS = {

            MovieContract.MovieEntry.TABLE_NAME
                    + "."
                    + MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.COLUMN_POSTER,

    };

    static final int COL_MOVIE_ID = 0;
    static final int COL_MOVIE_POSTER = 1;


    private static final String[] FAVORITE_MOVIE_COLUMNS = {

            MovieContract.FavouriteMovieEntry.TABLE_NAME
                    + "."
                    + MovieContract.FavouriteMovieEntry._ID,
            MovieContract.FavouriteMovieEntry.COLUMN_POSTER,

    };


    public interface Callback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        public void onItemSelected(Uri dateUri);
    }


    public MovieFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        FetchMovieTask movieTask = new FetchMovieTask(getContext());

        switch (id) {
            case R.id.action_most_popular: {

                if (Utility.isOnline(getContext())) {

                    Toast.makeText(getContext(), "Sort by MOST POPULAR", Toast.LENGTH_SHORT).show();
                    movieUri = MovieContract.MovieEntry.CONTENT_URI;

                    movieTask.execute("popular?");
                } else {
                    Toast.makeText(getContext(), "No connectivy available", Toast.LENGTH_SHORT).show();
                }
                return true;
            }

            case R.id.action_top_rated: {

                if (Utility.isOnline(getContext())) {

                    Toast.makeText(getContext(), "Sort by TOP RATED", Toast.LENGTH_SHORT).show();
                    movieUri = MovieContract.MovieEntry.CONTENT_URI;
                    movieTask.execute("top_rated?");

                } else {
                    Toast.makeText(getContext(), "No connectivy available", Toast.LENGTH_SHORT).show();
                }

                return true;
            }

            case R.id.action_favorites: {

                Toast.makeText(getContext(), "Sort by FAVORITES", Toast.LENGTH_SHORT).show();
                movieUri = MovieContract.FavouriteMovieEntry.CONTENT_URI;
                getLoaderManager().initLoader(FAVORITE_MOVIE_LOADER, null, this);

                return true;

            }
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {

        mMovieAdapter = new MovieAdapter(getActivity(), null, 0);

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        GridView gv = (GridView) rootView.findViewById(R.id.gridview_movie);
        gv.setAdapter(mMovieAdapter);

        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Cursor cursor = (Cursor) adapterView.getItemAtPosition(i);


                if (cursor != null) {


                    if (movieUri == MovieContract.MovieEntry.CONTENT_URI) {
                        Uri uri = MovieContract.MovieEntry
                                .buildMovieUri(cursor.getLong(COL_MOVIE_ID));

                        ((Callback) getActivity()).onItemSelected(uri);

                    }

                    if (movieUri == MovieContract.FavouriteMovieEntry.CONTENT_URI) {

                        Uri uri = MovieContract.FavouriteMovieEntry
                                .buildFavoriteMovieUri(cursor.getLong(COL_MOVIE_ID));

                        ((Callback) getActivity()).onItemSelected(uri);
                        Log.v(LOG_TAG, movieUri.toString());

                    }
//                    Toast.makeText(getContext()," No tiene valor!! ",Toast.LENGTH_SHORT).show();
                }
            }
        });
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);
        movieUri = MovieContract.MovieEntry.CONTENT_URI;
        getLoaderManager().initLoader(MOVIE_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        Uri uri;
        String[] projections;

        switch (i) {

            case MOVIE_LOADER: {

                projections = MOVIE_COLUMNS;
                return new CursorLoader(getActivity(), movieUri, projections, null, null, null);
            }

            case FAVORITE_MOVIE_LOADER: {

                projections = FAVORITE_MOVIE_COLUMNS;
                return new CursorLoader(getActivity(), movieUri, projections, null, null, null);
            }
        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {

        mMovieAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) { }


}








