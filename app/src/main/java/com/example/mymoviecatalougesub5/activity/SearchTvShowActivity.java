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
import com.example.mymoviecatalougesub5.adapter.ListTvShowAdapter;
import com.example.mymoviecatalougesub5.api.Api;
import com.example.mymoviecatalougesub5.model.TvShow;
import com.example.mymoviecatalougesub5.network.Network;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class SearchTvShowActivity extends AppCompatActivity {

    public static final String EXTRA_QUERY = "extra_query" ;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private ListTvShowAdapter adapter;
    private ArrayList<TvShow> tvShows;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_tv_show);

        recyclerView = findViewById(R.id.rv_search_tv_show);
        progressBar = findViewById(R.id.pb_search_tv_show);
        tvShows = new ArrayList<>();
        adapter = new ListTvShowAdapter(this);
        String query = getIntent().getStringExtra(EXTRA_QUERY);

        showRecyclerView();
        if (savedInstanceState == null) {
            URL url = Api.getSearchTvShow(query);
            Log.e("url", url.toString());
            new SearchTvShowActivity.SearchTvShowAsyncTask().execute(url);
        } else {
            tvShows= savedInstanceState.getParcelableArrayList("list");
            if (tvShows!=null){
                adapter.setTvShowArrayList(tvShows);
            }
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("list", tvShows);
    }

    private void showRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    public class SearchTvShowAsyncTask extends AsyncTask<URL, Void, String>{
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
                    TvShow tvShow = new TvShow(object);
                    tvShows.add(tvShow);
                }
            }
            catch (JSONException e){
                e.printStackTrace();
            }
            adapter.setTvShowArrayList(tvShows);
        }
    }
}
