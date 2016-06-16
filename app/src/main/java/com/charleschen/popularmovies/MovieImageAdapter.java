package com.charleschen.popularmovies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MovieImageAdapter extends ArrayAdapter<Movie> {
    public MovieImageAdapter(Context context, ArrayList<Movie> items) {
        super(context, 0, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Movie item = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.grid_item, parent,
                    false);
        }

        // Image population
        ImageView view = (ImageView) convertView.findViewById(R.id.movie_poster);
        String url = item.getPosterPath();
        Picasso.with(getContext()).load(url).into(view);
        // Return the completed view to render on screen
        return convertView;
    }
}
