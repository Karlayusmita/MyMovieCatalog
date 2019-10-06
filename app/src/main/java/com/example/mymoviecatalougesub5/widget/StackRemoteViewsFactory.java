package com.example.mymoviecatalougesub5.widget;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.bumptech.glide.Glide;
import com.example.mymoviecatalougesub5.R;
import com.example.mymoviecatalougesub5.api.Api;
import com.example.mymoviecatalougesub5.model.Movie;
import com.example.mymoviecatalougesub5.model.MovieFavorite;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.exceptions.RealmMigrationNeededException;

public class StackRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    private final Context mContext;
    private ArrayList<Movie> movieArrayList;
    private RealmResults<MovieFavorite> movieFavorites;
    private Realm realm;

    StackRemoteViewsFactory(Context context) {
        mContext = context;
        movieArrayList = new ArrayList<>();
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {
        try {
            Realm.init(mContext);
            realm = Realm.getDefaultInstance();
        } catch (RealmMigrationNeededException e) {
            if (Realm.getDefaultConfiguration() != null) {
                Realm.deleteRealm(realm.getDefaultConfiguration());
                realm = Realm.getDefaultInstance();
            }
        }

        movieFavorites = realm.where(MovieFavorite.class).findAll();
        if (!movieFavorites.isEmpty()) {
            for (int i = 0; i < movieFavorites.size(); i++) {
                Movie dummy = new Movie();
                dummy.setId(movieFavorites.get(i).getId());
                dummy.setPoster(movieFavorites.get(i).getPoster());
                dummy.setTitle(movieFavorites.get(i).getTitle());
                movieArrayList.add(dummy);
            }
        }
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return movieArrayList.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        try {
            Realm.init(mContext);
            realm = Realm.getDefaultInstance();
        } catch (RealmMigrationNeededException e) {
            if (Realm.getDefaultConfiguration() != null) {
                Realm.deleteRealm(realm.getDefaultConfiguration());
                realm = Realm.getDefaultInstance();
            }
        }
        RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(), R.layout.widget_item);
        Bitmap bitmap = null;
        String posterPath = movieArrayList.get(position).getPoster();
        String title = movieArrayList.get(position).getTitle();
        Log.d("Widget Load", posterPath);

        if (movieArrayList.size() > 0) {
            try {
                bitmap = Glide.with(mContext)
                        .asBitmap()
                        .load(Api.getPoster(posterPath))
                        .into(800, 600).get();
                Log.d("Widget Load", "Success");
            } catch (InterruptedException | ExecutionException e) {
                Log.d("Widget Load", "error");
            }

            remoteViews.setImageViewBitmap(R.id.image_view, bitmap);
            remoteViews.setTextViewText(R.id.title_view, title);
        }

        Bundle extras = new Bundle();
        extras.putInt(FavoriteBannerWidget.EXTRA_ITEM, position);
        Intent fillIntent = new Intent();
        fillIntent.putExtras(extras);

        remoteViews.setOnClickFillInIntent(R.id.image_view, fillIntent);
        return remoteViews;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }
}
