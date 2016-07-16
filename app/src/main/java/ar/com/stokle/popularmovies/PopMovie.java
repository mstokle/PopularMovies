package ar.com.stokle.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Mariano_Stokle on 13/07/2016.
 * Popular Movies project for Udacity Project 1 - Android Developer Nanodegree
 */
public class PopMovie implements Parcelable{
    int id;
    String poster_path;

    public PopMovie (int movie_id, String movie_poster_path) {
        this.id = movie_id;
        this.poster_path = movie_poster_path;
    }

    private PopMovie (Parcel parcel) {
        id = parcel.readInt();
        poster_path = parcel.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String toString() {return "Id: " + id + " poster: " + poster_path;}

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(poster_path);
    }

    public final Parcelable.Creator<PopMovie> CREATOR = new Parcelable.Creator<PopMovie>() {
        @Override
        public PopMovie createFromParcel(Parcel parcel) {
            return new PopMovie(parcel);
        }

        @Override
        public PopMovie[] newArray(int i) {
            return new PopMovie[i];
        }
    };
}
