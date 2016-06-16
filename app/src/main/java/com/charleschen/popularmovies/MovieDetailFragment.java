package com.charleschen.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class MovieDetailFragment extends Fragment {

    public MovieDetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);
        Intent intent = getActivity().getIntent();
        Movie movie = null;

        if (intent != null)
            movie = intent.getExtras().getParcelable("Movie");

        if (movie != null) {
            ((TextView) rootView.findViewById(R.id.movie_title)).setText(movie.original_title);
            ImageView view = (ImageView) rootView.findViewById(R.id.movie_poster);
            Picasso.with(getContext()).load(movie.getPosterPath()).into(view);
            ((TextView) rootView.findViewById(R.id.movie_plot)).setText(movie.overview);
            String rrStr = "Rating: " + String.valueOf(movie.vote_average) + "/10 " +
                    "Release: " + movie.release_date;
            ((TextView) rootView.findViewById(R.id.movie_rating_release)).setText(rrStr);
        }

        return rootView;
    }
}
