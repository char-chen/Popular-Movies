package com.charleschen.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MovieImageAdapter extends RecyclerView.Adapter<MovieImageAdapter.ViewHolder> {
    private  List<Movie> itemList;
    private Context context;

    // Provide a reference to the views for each data item. Complex data items may need more than
    // one view per item, and needs access to all the views for a data item in a view holder
    public  class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // each data item is just a string in this case
        public ImageView imageView;
        private Context context;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            imageView = (ImageView)itemView.findViewById(R.id.movie_poster);
            context = itemView.getContext();
        }

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(context, MovieDetailActivity.class);;
            intent.putExtra("Movie", itemList.get(getAdapterPosition()));
            context.startActivity(intent);
        }
    }

    public MovieImageAdapter(Context context, ArrayList<Movie> items) {
        this.itemList = items;
        this.context = context;
    }

    @Override
    public MovieImageAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_item, parent,
                false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieImageAdapter.ViewHolder holder, int position) {
        ImageView view = (ImageView) holder.imageView.findViewById(R.id.movie_poster);
        String url = itemList.get(position).getPosterPath();
        Picasso.with(context).load(url).into(view);
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public void add(Movie item) {
        itemList.add(item);
        notifyItemInserted(getItemCount() - 1);
    }

    public void clear() {
        int size = getItemCount();
        itemList.clear();
        notifyItemRangeRemoved(0, size);
    }
}
