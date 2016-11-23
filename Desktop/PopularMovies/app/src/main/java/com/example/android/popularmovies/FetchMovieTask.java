package com.example.android.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.example.android.popularmovies.data.MovieContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

import static java.security.AccessController.getContext;


/**
 * Created by Fernando on 21/11/2016.
 */

public class FetchMovieTask extends AsyncTask<String, Void, Void> {

    private final String LOG_TAG = FetchMovieTask.class.getSimpleName();
        private Context mContext;

    public FetchMovieTask(Context mContext) {
        this.mContext = mContext;
    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    @Override
    protected Void doInBackground(String... params) {


        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;


        // Will contain the raw JSON response as a string.
        String movieJsonStr = null;
        if (isOnline()) {
            try {
                // Construct the URL for the TheMovieDB query

                final String MOVIE_BASE_URL = "http://api.themoviedb.org/3/movie/" + params[0];

                final String APPID_PARAM = "api_key";

                Uri builtUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                        .appendQueryParameter(APPID_PARAM, BuildConfig.THE_MOVIE_DB_API_KEY)
                        .build();

                URL url = new URL(builtUri.toString());


                // Create the request to TheMovieDB, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                movieJsonStr = buffer.toString();
                getMovieDataFromJson(movieJsonStr);
//                Log.v(LOG_TAG, movieJsonStr);
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
        }
//            else{
//                Toast.makeText( getContext(),"No connectivy available", Toast.LENGTH_SHORT).show();
//            }
        return null ;
    }


    private void getMovieDataFromJson(String movieData) {

        if (movieData != null) {

            final String OWM_RESULTS = "results";
            final String OWN_PATH = "poster_path";
            final String OWM_TITLE = "title";
            final String OWM_RELEASE_DATE = "release_date";
            final String OWM_RATE = "vote_average";
            final String OWM_OVERVIEW = "overview";
            final String OWM_MOVIE_ID = "id";

            JSONObject jsonObject = null;
            JSONArray movieArray = null;

            try {
                jsonObject = new JSONObject(movieData);
                movieArray = jsonObject.getJSONArray(OWM_RESULTS);

                // Insert the new weather information into the database
                Vector<ContentValues> cVVector = new Vector<ContentValues>(movieArray.length());

                for (int i = 0; i < movieArray.length(); i++) {

                    String poster_path;
                    String title;
                    String release_date;
                    double vote_average;
                    String overview;
                    int movie_db_id;

                    JSONObject movie = movieArray.getJSONObject(i);

                    poster_path = movie.getString(OWN_PATH);
                    title = movie.getString(OWM_TITLE);
                    release_date = movie.getString(OWM_RELEASE_DATE);
                    vote_average = movie.getDouble(OWM_RATE);
                    overview = movie.getString(OWM_OVERVIEW);
                    movie_db_id = movie.getInt(OWM_MOVIE_ID);

                    ContentValues movieValues = new ContentValues();

                    movieValues.put(MovieContract.MovieEntry.COLUMN_POSTER, poster_path);
                    movieValues.put(MovieContract.MovieEntry.COLUMN_TITLE, title);
                    movieValues.put(MovieContract.MovieEntry.COLUMN_DATE, release_date);
                    movieValues.put(MovieContract.MovieEntry.COLUMN_RATE, vote_average);
                    movieValues.put(MovieContract.MovieEntry.MOVIE_ID, movie_db_id);

                    cVVector.add(movieValues);
                }

                mContext.getContentResolver().delete(MovieContract.MovieEntry.CONTENT_URI, null, null);
                Log.v(LOG_TAG,"se borró");

                // add to database
                if (cVVector.size() > 0) {
                    ContentValues[] cvArray = new ContentValues[cVVector.size()];
                    cVVector.toArray(cvArray);
                    mContext.getContentResolver().bulkInsert(MovieContract.MovieEntry.CONTENT_URI, cvArray);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


    }


}
