package com.example.mymoviecatalougesub5.fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mymoviecatalougesub5.R;
import com.example.mymoviecatalougesub5.adapter.ListMovieAdapter;
import com.example.mymoviecatalougesub5.model.Movie;
import com.example.mymoviecatalougesub5.model.MovieFavorite;

import java.util.ArrayList;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.exceptions.RealmMigrationNeededException;

/**
 * A simple {@link Fragment} subclass.
 */
public class FavoriteMovieFragment extends Fragment {
    private RecyclerView recyclerView;
    private ArrayList<Movie> movieArrayList;
    private ListMovieAdapter listMovieAdapter;
    private RealmResults<MovieFavorite> realmResults;
    private Realm realm;

    public FavoriteMovieFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_favorite_movie, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.rv_favorite_movie);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        try {
            Realm.init(Objects.requireNonNull(getActivity()));
            realm = Realm.getDefaultInstance();
        } catch (RealmMigrationNeededException e) {
            if (Realm.getDefaultConfiguration() != null) {
                Realm.deleteRealm(Realm.getDefaultConfiguration());
                realm = Realm.getDefaultInstance();
            }

        }
        movieArrayList = new ArrayList<>();
        showRecyclerList();
        loadData();
    }

    private void showRecyclerList() {
        listMovieAdapter = new ListMovieAdapter(getActivity());
        listMovieAdapter.setMovieArrayList(movieArrayList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(listMovieAdapter);
        realmResults = realm.where(MovieFavorite.class).findAll();
    }

    private void loadData() {
        realmResults = realm.where(MovieFavorite.class).findAll();
        movieArrayList.clear();
        if (!realmResults.isEmpty()) {
            for (int i = 0; i < realmResults.size(); i++) {
                Movie dummy = new Movie();
                dummy.setId(realmResults.get(i).getId());
                dummy.setPoster(realmResults.get(i).getPoster());
                dummy.setTitle(realmResults.get(i).getTitle());
                dummy.setOverview(realmResults.get(i).getOverview());
                dummy.setOriginalLanguage(realmResults.get(i).getOriginalLanguage());
                dummy.setReleaseDate(realmResults.get(i).getReleaseDate());
                dummy.setVoteAverage(realmResults.get(i).getVoteAverage());
                movieArrayList.add(dummy);
            }
        }
        listMovieAdapter.setMovieArrayList(movieArrayList);
    }
}
