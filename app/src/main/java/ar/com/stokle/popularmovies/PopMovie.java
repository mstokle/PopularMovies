package ar.com.stokle.popularmovies;

/**
 * Created by Mariano_Stokle on 13/07/2016.
 * Popular Movies project for Udacity Project 1 - Android Developer Nanodegree
 */
public class PopMovie {
    int id;
    String poster_path;

    public PopMovie (int movie_id, String movie_poster_path) {
        id = movie_id;
        poster_path = movie_poster_path;
    }
}
