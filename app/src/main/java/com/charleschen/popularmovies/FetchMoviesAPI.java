package com.charleschen.popularmovies;

import android.os.AsyncTask;
import android.util.Log;
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


public class FetchMoviesAPI extends AsyncTask<Boolean, Void, Movie[]> {

    private MainFragment mainFragment;
    private final String LOG_TAG = FetchMoviesAPI.class.getSimpleName();

    public FetchMoviesAPI(MainFragment mainFragment) {
        this.mainFragment = mainFragment;
    }

    @Override
    protected Movie[] doInBackground(Boolean... params) {
        String url;
        if (!params[0]) {
            url = "http://api.themoviedb.org/3/movie/popular?api_key=" + BuildConfig
                    .THEMOVIEDB_API_KEY;
        } else {
            url = "http://api.themoviedb.org/3/movie/top_rated?api_key=" + BuildConfig
                    .THEMOVIEDB_API_KEY;
        }
        String json = fetchAPIData(url);
        try {
            return parseAPIData(json);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Movie[] items) {
        if (items != null && mainFragment.adapter != null) {
            mainFragment.adapter.clear();
            for (Movie item : items) {
                mainFragment.adapter.add(item);
            }
        } else {
            CharSequence text = "No internet connection detected!";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(mainFragment.getContext(), text, duration);
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

    private Review[] requestReviewsAPIData(int id) throws JSONException {
        String reviews_path = "http://api.themoviedb.org/3/movie/" + id + "/reviews?api_key=" +
                BuildConfig.THEMOVIEDB_API_KEY;
        String requestJSON = fetchAPIData(reviews_path);

        if (requestJSON == null)
            return null;

        JSONObject dataArray = new JSONObject(requestJSON);
        JSONArray results = dataArray.getJSONArray("results");
        Review reviews[] = new Review[results.length()];

        for (int i = 0; i < results.length(); i++) {
            String author = results.getJSONObject(i).getString("author");
            String content = results.getJSONObject(i).getString("content");
            String url = results.getJSONObject(i).getString("url");
            Review review = new Review(author, url, content);
            reviews[i] = review;
        }

        return reviews;
    }

    private String[] requestVideosAPIData(int id) throws JSONException {
        String videos_path = "http://api.themoviedb.org/3/movie/" + id + "/videos?api_key=" +
                BuildConfig.THEMOVIEDB_API_KEY;
        String requestJSON = fetchAPIData(videos_path);

        if (requestJSON == null)
            return null;

        JSONObject dataArray = new JSONObject(requestJSON);
        JSONArray results = dataArray.getJSONArray("results");
        String links[] = new String[results.length()];

        for (int i = 0; i < results.length(); i++) {
            String key = results.getJSONObject(i).getString("key");
            String link = "https://www.youtube.com/watch?v=" + key;
            links[i] = link;
        }

        return links;
    }

    private Movie[] parseAPIData(String... json) throws JSONException {
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
                Review[] reviews = requestReviewsAPIData(id);
                String[] youtubeLinks = null;// = requestVideosAPIData(id);
                Movie movie = new Movie(poster_path, overview, release_date, original_title,
                        vote_average, popularity, id, reviews, youtubeLinks);
                if (!listData.contains(movie))
                    listData.add(movie);
            }
        }
        return listData.toArray(new Movie[listData.size()]);
    }
}
