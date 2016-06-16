package com.charleschen.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

public class Movie implements Parcelable {
    String poster_path;
    String adult;
    String overview;
    String release_date;
    int[] genre_ids;
    int id;
    String original_title;
    String original_language;
    String title;
    String backdrop_path;
    double popularity;
    int vote_count;
    boolean video;
    double vote_average;

    public Movie(String poster_path, String overview, String release_date, String original_title,
                 double vote_average, double popularity, int id) {
        this.poster_path = poster_path;
        this.overview = overview;
        this.release_date = release_date;
        this.original_title = original_title;
        this.vote_average = vote_average;
        this.popularity = popularity;
        this.id = id;
    }

    protected Movie(Parcel in) {
        poster_path = in.readString();
        adult = in.readString();
        overview = in.readString();
        release_date = in.readString();
        genre_ids = in.createIntArray();
        id = in.readInt();
        original_title = in.readString();
        original_language = in.readString();
        title = in.readString();
        backdrop_path = in.readString();
        popularity = in.readDouble();
        vote_count = in.readInt();
        video = in.readByte() != 0;
        vote_average = in.readDouble();
    }

    public String getPosterPath() {
        return "http://image.tmdb.org/t/p/w342/" + poster_path;
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    @Override
    public String toString() {
        String result = "";
        result = result.concat("Title: " + original_title + "\n");
        result = result.concat("Plot Synopsis: " + overview + "\n");
        result = result.concat("User Rating: " + vote_average + "\n");
        result = result.concat("Release Date: " + release_date + "\n");
        result = result.concat("Popularity: " + popularity + "\n");
        return result;
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + id;
        return result;
    }

    @Override
    public boolean equals(Object rhs) {
        return rhs != null && hashCode() == rhs.hashCode();
    }

    @Override
    public int describeContents() {
        return hashCode();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(poster_path);
        dest.writeString(adult);
        dest.writeString(overview);
        dest.writeString(release_date);
        dest.writeIntArray(genre_ids);
        dest.writeInt(id);
        dest.writeString(original_title);
        dest.writeString(original_language);
        dest.writeString(title);
        dest.writeString(backdrop_path);
        dest.writeDouble(popularity);
        dest.writeInt(vote_count);
        dest.writeByte((byte) (video ? 1 : 0));
        dest.writeDouble(vote_average);
    }
}
