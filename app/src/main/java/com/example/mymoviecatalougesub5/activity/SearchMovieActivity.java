package com.example.mymoviecatalougesub5.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import com.example.mymoviecatalougesub5.R;
import com.example.mymoviecatalougesub5.adapter.ListMovieAdapter;
import com.example.mymoviecatalougesub5.api.Api;
import com.example.mymoviecatalougesub5.model.Movie;
import com.example.mymoviecatalougesub5.network.Network;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class SearchMovieActivity extends AppCompatActivity {
    public static final String EXTRA_QUERY = "extra_query";
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private ListMovieAdapter adapter;
    private ArrayList<Movie> movieArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_movie);

        recyclerView = findViewById(R.id.rv_search_movie);
        progressBar = findViewById(R.id.pb_search_movie);
        movieArrayList = new ArrayList<>();
        adapter = new ListMovieAdapter(this);
        String query = getIntent().getStringExtra(EXTRA_QUERY);
        Log.e("query", query);

        showRecyclerView();
        if (savedInstanceState == null) {
            URL url = Api.getSearchMovie(query);
            Log.e("url", url.toString());
            new SearchMovieAsyncTask().execute(url);
        } else {
            movieArrayList = savedInstanceState.getParcelableArrayList("list");
            if (movieArrayList!=null){
                adapter.setMovieArrayList(movieArrayList);
            }
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("list", movieArrayList);
    }

    private void showRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    public class SearchMovieAsyncTask extends AsyncTask<URL, Void, String>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.INVISIBLE);
        }

        @Override
        protected String doInBackground(URL... urls) {
            URL url = urls[0];
            String result = null;
            try {
                result = Network.getFromNetwork(url);
            }
            catch (IOException e){
                e.printStackTrace();
            }
            Log.e("result", result);
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            recyclerView.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.INVISIBLE);

            try {
                JSONObject jsonObject = new JSONObject(s);
                JSONArray jsonArray = jsonObject.getJSONArray("results");

                for (int i=0; i<jsonArray.length();i++){
                    JSONObject object = jsonArray.getJSONObject(i);
                    Movie movie = new Movie(object);
                    movieArrayList.add(movie);
                }
            }
            catch (JSONException e){
                e.printStackTrace();
            }
            adapter.setMovieArrayList(movieArrayList);
        }
    }
}
