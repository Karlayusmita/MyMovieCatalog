package com.example.mymoviecatalougesub5.fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mymoviecatalougesub5.R;
import com.example.mymoviecatalougesub5.adapter.ListTvShowAdapter;
import com.example.mymoviecatalougesub5.model.TvShow;
import com.example.mymoviecatalougesub5.model.TvShowFavorite;

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
public class FavoriteTvShowFragment extends Fragment {
    private RecyclerView recyclerView;
    private ArrayList<TvShow> tvShowArrayList;
    private ListTvShowAdapter listTvShowAdapter;
    private RealmResults<TvShowFavorite> realmResults;
    private Realm realm;

    public FavoriteTvShowFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_favorite_tv_show, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.rv_favorite_tv_show);
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
        tvShowArrayList = new ArrayList<>();
        showRecyclerList();
        loadData();
    }

    private void showRecyclerList() {
        listTvShowAdapter = new ListTvShowAdapter(getActivity());
        listTvShowAdapter.setTvShowArrayList(tvShowArrayList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(listTvShowAdapter);
        realmResults = realm.where(TvShowFavorite.class).findAll();
    }

    private void loadData() {
        realmResults = realm.where(TvShowFavorite.class).findAll();
        tvShowArrayList.clear();
        if (!realmResults.isEmpty()) {
            for (int i = 0; i < realmResults.size(); i++) {
                TvShow dummy = new TvShow();
                dummy.setId(realmResults.get(i).getId());
                dummy.setPoster(realmResults.get(i).getPoster());
                dummy.setName(realmResults.get(i).getName());
                dummy.setOverview(realmResults.get(i).getOverview());
                dummy.setOriginalLanguage(realmResults.get(i).getOriginalLanguage());
                dummy.setFirstAirDate(realmResults.get(i).getFirstAirDate());
                dummy.setVoteCount(realmResults.get(i).getVoteCount());
                tvShowArrayList.add(dummy);
            }
        }
        listTvShowAdapter.setTvShowArrayList(tvShowArrayList);
    }
}
