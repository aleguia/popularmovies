package com.example.android.popularmovies;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;

/**
 * Created by Fernando on 30/11/2016.
 */

public class FetchMovieTrailer extends AsyncTask<String, Void, Void> {

    private Context mContext;
    private ArrayList<String> mTrailerNameList;
    private ArrayList<String> mTrailerKeyList;

    private static final String LOG_TAG = FetchMovieTrailer.class.getSimpleName();

    public FetchMovieTrailer(Context mContext) {
        this.mContext = mContext;
//        this.mArrayAdapter = arrayList;
//        this.mTrailerKeyList = arrayList1;
    }

    @Override
    protected Void doInBackground(String... params) {


        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;


        // Will contain the raw JSON response as a string.
        String trailerJsonStr = null;

        try {
            // Construct the URL for the TheMovieDB query

            final String MOVIE_BASE_URL = "http://api.themoviedb.org/3/movie/" + params[0] + "/videos?";

            final String APPID_PARAM = "api_key";

            Uri builtUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                    .appendQueryParameter(APPID_PARAM, BuildConfig.THE_MOVIE_DB_API_KEY)
                    .build();

            URL url = new URL(builtUri.toString());
            Log.v(LOG_TAG,url.toString());


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
            trailerJsonStr = buffer.toString();
            getTrailerDataFromJson(trailerJsonStr);
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

        return null;
    }


    public void getTrailerDataFromJson(String trailerJson) {


        final String OWM_RESULTS = "results";
        final String OWM_KEY = "key";
        final String OWM_NAME = "name";

        JSONObject jsonObject = null;
        JSONArray movieArray = null;

        try {
            jsonObject = new JSONObject(trailerJson);
            movieArray = jsonObject.getJSONArray(OWM_RESULTS);


        for (int i = 0; i < movieArray.length(); i++) {

            String trailerKey;
            String trailerName;

            mTrailerNameList = new ArrayList<>();
            mTrailerKeyList = new ArrayList<>();


            JSONObject movie = movieArray.getJSONObject(i);

            trailerKey = movie.getString(OWM_KEY);
            trailerName = movie.getString(OWM_NAME);

//           TrailerAndReview.
//            mTrailerKeyList.add(trailerKey);


        }



        }
        catch (JSONException e) {
            e.printStackTrace();
        }


    }

    public ArrayList<String> getmTrailerKeyList() {
        return mTrailerKeyList;
    }

    public void setmTrailerKeyList(ArrayList<String> mTrailerKeyList) {
        this.mTrailerKeyList = mTrailerKeyList;
    }

    public ArrayList<String> getmTrailerNameList() {
        return mTrailerNameList;
    }

    public void setmTrailerNameList(ArrayList<String> mTrailerNameList) {
        this.mTrailerNameList = mTrailerNameList;
    }
}