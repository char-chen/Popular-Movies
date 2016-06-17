package com.charleschen.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

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
import java.util.List;

public class MainFragment extends Fragment {

    private MovieImageAdapter adapter;

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
                intent.putExtra("Movie", adapter.getItem(position));
                startActivity(intent);
            }
        });
        return rootView;
    }

    private void updateAPI() {
        SyncAPI apiTask = new SyncAPI();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        boolean sort_by_top_rated = Integer.parseInt(prefs.getString("pref_sort_key", "0")) == 1;
        apiTask.execute(sort_by_top_rated);
    }

    public class SyncAPI extends AsyncTask<Boolean, Void, Movie[]> {

        private final String LOG_TAG = SyncAPI.class.getSimpleName();

        @Override
        protected Movie[] doInBackground(Boolean... params) {
            String url;
            if (!params[0]) {
                url = "http://api.themoviedb.org/3/movie/popular?api_key=" + BuildConfig
                        .OPEN_WEATHER_MAP_API_KEY;
            } else {
                url = "http://api.themoviedb.org/3/movie/top_rated?api_key=" + BuildConfig
                        .OPEN_WEATHER_MAP_API_KEY;
            }
            String json = fetchAPIData(url);
            try {
                return parseAPIData(params[0], json);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Movie[] items) {
            if (items != null && adapter != null) {
                adapter.clear();
                for (Movie item : items) {
                    adapter.add(item);
                }
            } else {
                CharSequence text = "No internet connection detected!";
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(getContext(), text, duration);
                toast.show();
            }
        }

        public String fetchAPIData(String url) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            try {
                URL url_p = new URL(url);
                urlConnection = (HttpURLConnection) url_p.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuilder buffer = new StringBuilder();

                if (inputStream == null)
                    return null;

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0)
                    return null;

                return buffer.toString();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                return null;
            } finally {
                if (urlConnection != null)
                    urlConnection.disconnect();

                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
        }

        private Movie[] parseAPIData(Boolean sort_top_rating, String... json) throws JSONException {
            List<Movie> listData = new ArrayList<>();

            if (json == null || json[0] == null)
                return null;

            for (String jsonStr : json) {
                JSONObject dataArray = new JSONObject(jsonStr);
                JSONArray results = dataArray.getJSONArray("results");

                for (int i = 0; i < results.length(); i++) {
                    String original_title = results.getJSONObject(i).getString("original_title");
                    String poster_path = results.getJSONObject(i).getString("poster_path");
                    String overview = results.getJSONObject(i).getString("overview");
                    double vote_average = Double.parseDouble(results.getJSONObject(i).getString
                            ("vote_average"));
                    double popularity = Double.parseDouble(results.getJSONObject(i).getString
                            ("popularity"));
                    String release_date = results.getJSONObject(i).getString("release_date");
                    int id = Integer.parseInt(results.getJSONObject(i).getString("id"));
                    Movie movie = new Movie(poster_path, overview, release_date, original_title,
                            vote_average, popularity, id);
                    if (!listData.contains(movie))
                        listData.add(movie);
                }
            }
            return listData.toArray(new Movie[listData.size()]);
        }
    }
}
