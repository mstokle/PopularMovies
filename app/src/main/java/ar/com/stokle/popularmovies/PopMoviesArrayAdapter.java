package ar.com.stokle.popularmovies;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;


/**
 * Created by Mariano_Stokle on 13/07/2016.
 * Popular Movies project for Udacity Project 1 - Android Developer Nanodegree
 */
public class PopMoviesArrayAdapter extends ArrayAdapter<PopMovie> {

    private static final String LOG_TAG = PopMoviesArrayAdapter.class.getSimpleName();

    public PopMoviesArrayAdapter (Activity context, ArrayList<PopMovie> listMovies) {
        super(context, 0, listMovies);
    }

    @Override
    public View getView (int position, View convertView, ViewGroup parent) {

        PopMovie movie = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.poster_pop_movies, parent, false);
        }

        String base_url = getContext().getString(R.string.TMDB_IMAGE_BASE_URL);
        String image_path = getContext().getString(R.string.TMDB_IMAGE_PATH);
        String poster_url = base_url + image_path + movie.poster_path;
        ImageView posterView = (ImageView) convertView.findViewById(R.id.posterImageView);
        Picasso.with(getContext()).load(poster_url).into(posterView);

        return convertView;
    }

}
