package ar.com.stokle.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Mariano_Stokle on 13/07/2016.
 * Popular Movies project for Udacity Project 1 - Android Developer Nanodegree
 */

public class PopMoviesDetailActivityFragment extends Fragment {

    private class PopMovieDetail {
        int id;
        int runtime;
        String title;
        String description;
        long vote;
        String release_date;
        String poster_path;

        public PopMovieDetail () {
            this.id = 0;
            this.runtime = 0;
            this.title = "";
            this.description = "";
            this.vote = 0;
            this.release_date = "";
            this.poster_path = "";
        }

        public PopMovieDetail (int id_movie, int runtime_movie, String title_movie, String description_movie, long vote_movie, String release_date_movie, String poster_path_movie) {
            this.id = id_movie;
            this.runtime = runtime_movie;
            this.title = title_movie;
            this.description = description_movie;
            this.vote = vote_movie;
            this.release_date = release_date_movie;
            this.poster_path = poster_path_movie;
        }
    }

    TextView titleTextView;
    TextView durationTextView;
    TextView voteTextView;
    TextView yearTextView;
    TextView descriptionTextView;
    ImageView posterImageView;

    public PopMoviesDetailActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra(getString(R.string.bundle_movies_key))) {
            PopMovie movie = intent.getParcelableExtra(getString(R.string.bundle_movies_key));
            if (isNetworkUp()) {
                getMovieDetails(movie);
            } else {
                Context context = getContext();
                CharSequence text = getString(R.string.internet_connection);
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_pop_movies_detail, container, false);
        titleTextView = (TextView)rootView.findViewById(R.id.titleTextView);
        posterImageView = (ImageView)rootView.findViewById(R.id.posterImageView);
        durationTextView = (TextView)rootView.findViewById(R.id.runtimeTextView);
        yearTextView = (TextView)rootView.findViewById(R.id.yearTextView);
        voteTextView = (TextView)rootView.findViewById(R.id.voteTextView);
        descriptionTextView = (TextView)rootView.findViewById(R.id.overviewTextView);

        return rootView;
    }

    private boolean isNetworkUp() {
        ConnectivityManager cm = (ConnectivityManager)getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }

    private void getMovieDetails(PopMovie movie) {
        FetchMovieDetailTask fetchDetails = new FetchMovieDetailTask();
        fetchDetails.execute(movie);
    }

    private PopMovieDetail getMovieDetailDataFromJson(String movieDetailJsonStr, PopMovie movie)
            throws JSONException {

        JSONObject movieDetailJson = new JSONObject(movieDetailJsonStr);

        int id;
        String poster_path;
        String title;
        String description;
        String release_date;
        long vote;
        int runtime;

        id = movie.id;
        poster_path = movie.poster_path;
        title = movieDetailJson.getString(getString(R.string.TMDB_JSON_TITLE));
        description = movieDetailJson.getString(getString(R.string.TMDB_JSON_OVERVIEW));
        release_date = movieDetailJson.getString(getString(R.string.TMDB_JSON_RELEASE_DATE));
        vote = movieDetailJson.getLong(getString(R.string.TMDB_JSON_VOTE));
        runtime = movieDetailJson.getInt(getString(R.string.TMDB_JSON_RUNTIME));

        return new PopMovieDetail(id, runtime, title, description, vote, release_date, poster_path);

    }
    private class FetchMovieDetailTask extends AsyncTask<PopMovie, Void, PopMovieDetail> {

        private final String LOG_TAG = FetchMovieDetailTask.class.getSimpleName();

        @Override
        protected PopMovieDetail doInBackground(PopMovie... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String movie_id = params[0].id.toString();
            String movieDetailJsonStr = null;
            PopMovieDetail movieDetailData = new PopMovieDetail();
            String TMDB_Url = getString(R.string.TMDB_API_BASE_URL);
            String API_PARAM = getString(R.string.TMDB_API_PARAM);
            String API_KEY = getString(R.string.TMDB_API_KEY);

            try {
                Uri TMDB_Uri = Uri.parse(TMDB_Url).buildUpon().appendPath(movie_id).appendQueryParameter(API_PARAM, API_KEY).build();
                URL TMDB_Url_Connection = new URL(TMDB_Uri.toString());
                Log.v(LOG_TAG, "TMDB URI " + TMDB_Uri.toString());

                urlConnection = (HttpURLConnection) TMDB_Url_Connection.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuilder buffer = new StringBuilder();
                if (inputStream == null) {
                    movieDetailJsonStr = null;
                } else {
                    reader = new BufferedReader(new InputStreamReader(inputStream));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        buffer.append(line).append("\n");
                    }
                }
                if (buffer.length() == 0) {
                    movieDetailJsonStr = null;
                }
                movieDetailJsonStr = buffer.toString();
                Log.d(LOG_TAG, movieDetailJsonStr);

            } catch (IOException e) {
                Log.e(LOG_TAG, "Network Error ", e);
                movieDetailJsonStr = null;
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
                if (movieDetailJsonStr != null) {
                    movieDetailData = getMovieDetailDataFromJson(movieDetailJsonStr, params[0]);
                }
            } catch (JSONException e) {
                Log.e(LOG_TAG, "JSON Error ", e);
            }
            return movieDetailData;
        }

        @Override
        protected void onPostExecute(PopMovieDetail result) {
            if (result != null) {
                String base_url = getContext().getString(R.string.TMDB_IMAGE_BASE_URL);
                String image_path = getContext().getString(R.string.TMDB_IMAGE_PATH);
                String poster_url = base_url + image_path + result.poster_path;
                titleTextView.setText(result.title);
                Picasso.with(getContext()).load(poster_url).into(posterImageView);
                Integer duration = result.runtime;
                durationTextView.setText(duration.toString()+ "min");
                yearTextView.setText(result.release_date.substring(0,4));
                Long vote = result.vote;
                voteTextView.setText(vote.toString() + "/10");
                descriptionTextView.setText(result.description);
            }
        }
    }
}

