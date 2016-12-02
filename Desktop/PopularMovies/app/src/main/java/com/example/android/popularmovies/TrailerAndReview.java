package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

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
import java.util.ArrayList;

import static android.R.attr.data;
import static android.R.attr.id;
import static android.R.attr.key;
import static com.example.android.popularmovies.R.id.fragment_trailer_listview;
import static java.security.AccessController.getContext;

/**
 * Created by Fernando on 27/11/2016.
 */

public class TrailerAndReview extends Fragment {

    private final String LOG_TAG = TrailerAndReview.class.getSimpleName();

    static final String TRAILER_URI = "URI";

    private Uri mUri;


    private ArrayAdapter<String> mTrailerAdapter;
    private String[] mTrailerKey;

    public TrailerAndReview() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        Bundle arguments = getArguments();

        if(arguments!=null){

            mUri = arguments.getParcelable(TrailerAndReview.TRAILER_URI);

        }

        View rootView = inflater.inflate(R.layout.fragment_trailer, container, false);



        if(mUri!=null) {

        mTrailerAdapter = new ArrayAdapter<String>(getActivity(),
                                                    R.layout.trailer,
                                                    R.id.trailer_title_textView);


        ListView lv = (ListView) rootView.findViewById(R.id.fragment_trailer_listview);

        lv.setAdapter(mTrailerAdapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String key = mTrailerKey[position];

                Uri webpage = Uri.parse("https://www.youtube.com/watch?v="+key);
                Intent webIntent = new Intent(Intent.ACTION_VIEW, webpage);
                startActivity(webIntent);
            }
        });

        if(Utility.isOnline(getContext())) {

            FetchMovieTrailer fetchMovieTrailer = new FetchMovieTrailer();
            fetchMovieTrailer.execute(mUri);

        }else {

            Toast.makeText(getActivity(),"Unable to show trailers, no connectivity ",Toast.LENGTH_SHORT).show();

        }

        }

        return rootView;
    }


    public class FetchMovieTrailer extends AsyncTask<Uri, Void, String[]> {

        private final String LOG_TAG = com.example.android.popularmovies.FetchMovieTrailer.class.getSimpleName();

        String[] trailerNames;


        @Override
        protected String[] doInBackground(Uri... params) {

            Log.v(LOG_TAG, params[0].toString());

            Cursor cursor = getContext().getContentResolver().query(params[0],
                    null,
                    null,
                    null,
                    null);

            int index;
            String id;

            if (cursor.moveToFirst()) {
                index = cursor.getColumnIndex("movie_id");
                id = String.valueOf(cursor.getInt(index));
                Log.v(LOG_TAG, String.valueOf(index));
                Log.v(LOG_TAG, id);
                cursor.close();

                // These two need to be declared outside the try/catch
                // so that they can be closed in the finally block.
                HttpURLConnection urlConnection = null;
                BufferedReader reader = null;


                // Will contain the raw JSON response as a string.
                String trailerJsonStr = null;

                try {
                    // Construct the URL for the TheMovieDB query

                    final String MOVIE_BASE_URL = "http://api.themoviedb.org/3/movie/" + id + "/videos?";

                    final String APPID_PARAM = "api_key";

                    Uri builtUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                            .appendQueryParameter(APPID_PARAM, BuildConfig.THE_MOVIE_DB_API_KEY)
                            .build();

                    URL url = new URL(builtUri.toString());
                    Log.v(LOG_TAG, url.toString());


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
                    Log.v(LOG_TAG, trailerJsonStr);
                    try {
                        return getTrailerDataFromJson(trailerJsonStr);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
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

            return null;
        }

        @Override
        protected void onPostExecute(String[] result) {
            if (result != null) {
                mTrailerAdapter.clear();

                for(String trailer : result) {
                    mTrailerAdapter.add(trailer);
                }
                // New data is back from the server.  Hooray!
            }
        }

        public String[] getTrailerDataFromJson(String trailerJson) throws JSONException {


            final String OWM_RESULTS = "results";
            final String OWM_KEY = "key";
            final String OWM_NAME = "name";

            JSONObject jsonObject = null;
            JSONArray movieArray = null;




                jsonObject = new JSONObject(trailerJson);
                movieArray = jsonObject.getJSONArray(OWM_RESULTS);

                trailerNames = new String[movieArray.length()];
                mTrailerKey = new String[movieArray.length()];


                for (int i = 0; i < movieArray.length(); i++) {

                    String trailerKey;
                    String trailerName;


                    JSONObject movie = movieArray.getJSONObject(i);

                    trailerKey = movie.getString(OWM_KEY);
                    trailerName = movie.getString(OWM_NAME);
                    Log.v("PROABANDO EL TRAILER ", trailerName);

                    trailerNames[i]=trailerName;
                    mTrailerKey[i] = trailerKey;

                }

            return trailerNames;


        }
    }


}
