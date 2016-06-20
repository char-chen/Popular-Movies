package com.charleschen.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import java.util.ArrayList;

public class MainFragment extends Fragment {

    public MovieImageAdapter adapter;

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.grid_item_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will automatically handle clicks on
        // the Home/Up button, so long as activity is specified in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            updateAPI();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
        updateAPI();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        adapter = new MovieImageAdapter(getActivity(), new ArrayList<Movie>());
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        GridView gridView = (GridView) rootView.findViewById(R.id.gridView);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity().getApplicationContext(),
                        MovieDetailActivity.class);
                System.out.println(adapter.getItem(position).id);
                intent.putExtra("Movie", adapter.getItem(position));
                startActivity(intent);
            }
        });
        return rootView;
    }

    private void updateAPI() {
        FetchMoviesAPI apiTask = new FetchMoviesAPI(this);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        boolean sort_by_top_rated = Integer.parseInt(prefs.getString("pref_sort_key", "0")) == 1;
        apiTask.execute(sort_by_top_rated);
    }
}
