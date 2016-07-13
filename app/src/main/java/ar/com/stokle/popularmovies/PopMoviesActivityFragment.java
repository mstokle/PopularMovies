package ar.com.stokle.popularmovies;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

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

/**
 * Created by Mariano_Stokle on 13/07/2016.
 * Popular Movies project for Udacity Project 1 - Android Developer Nanodegree
 */

public class PopMoviesActivityFragment extends Fragment {

    PopMoviesArrayAdapter mPopMoviesArrayAdapter;

    public PopMoviesActivityFragment() {
    }

    @Override
    public void onStart() {
        super.onStart();
        getMovies();
    }

    private void getMovies() {
        FetchMoviesTask movieTask = new FetchMoviesTask();
        movieTask.execute(R.string.popular_path);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_pop_movies, container, false);

        GridView gridView = (GridView) rootView.findViewById(R.id.posters_gridView);
        ArrayList<PopMovie> cleanMovies = new ArrayList<>();
        mPopMoviesArrayAdapter = new PopMoviesArrayAdapter(getActivity(),cleanMovies);
        gridView.setAdapter(mPopMoviesArrayAdapter);
        return inflater.inflate(R.layout.fragment_pop_movies, container, false);
    }

    private ArrayList<PopMovie> getMoviesDataFromJson(String moviesJsonStr)
            throws JSONException {

        ArrayList<PopMovie> resultArray = new ArrayList<>();

        JSONObject moviesJson = new JSONObject(moviesJsonStr);
        JSONArray moviesArray = moviesJson.getJSONArray(getString(R.string.TMDB_JSON_RESULTS));

        for(int i = 0; i < moviesArray.length(); i++) {
            String poster_path;
            Integer id;


            JSONObject movie = moviesArray.getJSONObject(i);

            poster_path = movie.getString(getString(R.string.TMDB_JSON_POSTER_PATH));
            id = movie.getInt(getString(R.string.TMDB_JSON_ID));

            resultArray.add(new PopMovie(id, poster_path));
        }

        return resultArray;

    }

    private class FetchMoviesTask extends AsyncTask<Integer, Void, ArrayList<PopMovie>> {

        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

        @Override
        protected ArrayList<PopMovie> doInBackground(Integer... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String moviesJsonStr = null;
            ArrayList<PopMovie> moviesData = new ArrayList<>();
            String TMDB_Url = getString(R.string.TMDB_API_BASE_URL);
            String API_PARAM = getString(R.string.TMDB_API_PARAM);
            String API_KEY = getString(R.string.TMDB_API_KEY);
            String moviesPath;

            switch (params[0]) {
                case R.string.popular_path:
                    moviesPath = getString(R.string.popular_path);
                    break;
                case R.string.top_rated_path:
                    moviesPath = getString(R.string.top_rated_path);
                    break;
                default:
                    moviesPath = "popular";
            }
            try {
                Uri TMDB_Uri = Uri.parse(TMDB_Url).buildUpon().appendPath(moviesPath).appendQueryParameter(API_PARAM, API_KEY).build();

                URL TMDB_Url_Connection = new URL(TMDB_Uri.toString());

                Log.v(LOG_TAG, "TMDB URI " + TMDB_Uri.toString());

                urlConnection = (HttpURLConnection) TMDB_Url_Connection.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuilder buffer = new StringBuilder();
                if (inputStream == null) {
                    moviesJsonStr = null;
                } else {
                    reader = new BufferedReader(new InputStreamReader(inputStream));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        buffer.append(line).append("\n");
                    }
                }
                if (buffer.length() == 0) {
                    moviesJsonStr = null;
                }
                moviesJsonStr = buffer.toString();
                Log.d(LOG_TAG, moviesJsonStr);

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                moviesJsonStr = null;
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
            try {
                moviesData = getMoviesDataFromJson(moviesJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, "Error ", e);
            }
            return moviesData;
        }

        @Override
        protected void onPostExecute(ArrayList<PopMovie> result) {
            if (result != null) {
                mPopMoviesArrayAdapter.clear();
                for (PopMovie movies : result) {
                    mPopMoviesArrayAdapter.add(movies);
                }
            }
        }
    }
}

