package com.example.mymoviecatalougesub5.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.mymoviecatalougesub5.R;
import com.example.mymoviecatalougesub5.model.TvShow;
import com.example.mymoviecatalougesub5.model.TvShowFavorite;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.exceptions.RealmMigrationNeededException;

public class DetailTvShowActivity extends AppCompatActivity {
    public static final String IMG_BASE = "http://image.tmdb.org/t/p/";
    private Realm realm;
    private int id;
    private String tvPoster;
    private String tvName;
    private String tvOriginalLanguage;
    private String tvFirstAirDate;
    private String tvOverview;
    private int tvVoteCount;
    private boolean isFavorite = false;
    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_tv_show);
        ImageView poster;
        TextView name;
        TextView originalLanguage;
        TextView firstAirDate;
        TextView overview;
        RatingBar voteCount;
        TvShow tvShow;

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.detail_tv_show);
        }

        try {
            Realm.init(this);
            realm = Realm.getDefaultInstance();
        } catch (RealmMigrationNeededException e) {
            if (Realm.getDefaultConfiguration() != null) {
                Realm.deleteRealm(Realm.getDefaultConfiguration());
                realm = Realm.getDefaultInstance();
            }

        }

        poster = findViewById(R.id.detail_poster);
        name = findViewById(R.id.detail_name);
        originalLanguage = findViewById(R.id.detail_original_language);
        firstAirDate = findViewById(R.id.detail_first_air_date);
        overview = findViewById(R.id.detail_overview);
        voteCount = findViewById(R.id.detail_vote_average);

        tvShow = getIntent().getParcelableExtra("TV_SHOW_DATA");

        this.id = tvShow.getId();
        this.tvPoster = tvShow.getPoster();
        this.tvName = tvShow.getName();
        this.tvOriginalLanguage = tvShow.getOriginalLanguage();
        this.tvFirstAirDate = tvShow.getFirstAirDate();
        this.tvOverview = tvShow.getOverview();
        this.tvVoteCount = tvShow.getVoteCount();

        Glide.with(this)
                .load(IMG_BASE + "w185" + tvShow.getPoster())
                .apply(new RequestOptions().override(96, 144))
                .into(poster);
        name.setText(tvShow.getName());
        originalLanguage.setText(tvShow.getOriginalLanguage());
        firstAirDate.setText(tvShow.getFirstAirDate());
        overview.setText(tvShow.getOverview());
        voteCount.setRating((float) tvShow.getVoteCount() / 2);

        favoriteState();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_detail_menu, menu);
        setFavorite();
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_favorite) {
            if (isFavorite) {
                boolean delete = removeFromFavoriteTvShow();
                if (delete) {
                    isFavorite = false;
                    setFavorite();
                    setToast(getString(R.string.delete_from_favorite));
                } else {
                    setToast(getString(R.string.delete_failed));
                }
            } else {
                isFavorite = addToFavoriteTvShow();
                if (isFavorite) {
                    setFavorite();
                    setToast(getString(R.string.add_to_favorite));
                } else {
                    setToast(getString(R.string.add_failed));
                }
            }
        }
        return false;
    }

    private boolean addToFavoriteTvShow() {
        try {
            Realm.init(this);
            realm = Realm.getDefaultInstance();
        } catch (RealmMigrationNeededException e) {
            if (Realm.getDefaultConfiguration() != null) {
                Realm.deleteRealm(Realm.getDefaultConfiguration());
                realm = Realm.getDefaultInstance();
                realm.beginTransaction();
            }
        }

        TvShowFavorite tvShowFavorite = new TvShowFavorite();
        tvShowFavorite.setId(this.id);
        tvShowFavorite.setPoster(this.tvPoster);
        tvShowFavorite.setName(this.tvName);
        tvShowFavorite.setOriginalLanguage(this.tvOriginalLanguage);
        tvShowFavorite.setOverview(this.tvOverview);
        tvShowFavorite.setFirstAirDate(this.tvFirstAirDate);
        tvShowFavorite.setVoteCount(this.tvVoteCount);

        realm = Realm.getDefaultInstance();
        TvShowFavorite puppies = realm.where(TvShowFavorite.class).equalTo("id", this.id).findFirst();
        if (puppies == null) {
            try {
                realm.beginTransaction();
                realm.copyToRealm(tvShowFavorite);
                realm.commitTransaction();
                realm.close();
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    private boolean removeFromFavoriteTvShow() {
        try {
            try {
                Realm.init(this);
                realm = Realm.getDefaultInstance();
            } catch (RealmMigrationNeededException e) {
                if (Realm.getDefaultConfiguration() != null) {
                    Realm.deleteRealm(Realm.getDefaultConfiguration());
                    realm = Realm.getDefaultInstance();
                    realm.beginTransaction();
                }
            }
            realm.beginTransaction();
            TvShowFavorite tvShowFavorite = realm.where(TvShowFavorite.class).equalTo("id", id).findFirst();
            if (tvShowFavorite != null) {
                tvShowFavorite.deleteFromRealm();
                realm.commitTransaction();
                while (realm.isInTransaction()) {
                    Log.e("Realm", "still in transaction");
                }
                realm.close();
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    private void setToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            realm.close();
        } catch (RealmMigrationNeededException e) {
            e.printStackTrace();
        }
    }

    private void favoriteState() {
        RealmResults<TvShowFavorite> results;
        results = realm.where(TvShowFavorite.class).equalTo("id", id).findAll();
        isFavorite = !results.isEmpty();
    }

    private void setFavorite() {
        if (isFavorite) {
            menu.getItem(0).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_favorite_black_24dp));
        } else {
            menu.getItem(0).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_favorite_border_black_24dp));
        }
    }
}
