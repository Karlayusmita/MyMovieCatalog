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
import com.example.mymoviecatalougesub5.model.Movie;
import com.example.mymoviecatalougesub5.model.MovieFavorite;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.exceptions.RealmMigrationNeededException;

public class DetailMovieActivity extends AppCompatActivity {
    public static final String IMG_BASE = "http://image.tmdb.org/t/p/";
    private Realm realm;
    private int id;
    private String moviePoster;
    private String movieTitle;
    private String movieOriginalLanguage;
    private String movieReleaseDate;
    private String movieOverview;
    private int movieVoteAverage;
    private boolean isFavorite = false;
    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_movie);
        ImageView poster;
        TextView title;
        TextView originalLanguage;
        TextView releaseDate;
        TextView overview;
        RatingBar voteAverage;

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.detail_movie);
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
        title = findViewById(R.id.detail_title);
        originalLanguage = findViewById(R.id.detail_original_language);
        releaseDate = findViewById(R.id.detail_release_date);
        overview = findViewById(R.id.detail_overview);
        voteAverage = findViewById(R.id.detail_vote_average);

        Movie movie = getIntent().getParcelableExtra("MOVIE_DATA");

        this.id = movie.getId();
        this.moviePoster = movie.getPoster();
        this.movieTitle = movie.getTitle();
        this.movieOriginalLanguage = movie.getOriginalLanguage();
        this.movieReleaseDate = movie.getReleaseDate();
        this.movieOverview = movie.getOverview();
        this.movieVoteAverage = movie.getVoteAverage();

        Glide.with(this)
                .load(IMG_BASE + "w185" + movie.getPoster())
                .apply(new RequestOptions().override(96, 144))
                .into(poster);
        title.setText(movie.getTitle());
        originalLanguage.setText(movie.getOriginalLanguage());
        releaseDate.setText(movie.getReleaseDate());
        overview.setText(movie.getOverview());
        voteAverage.setRating((float) movie.getVoteAverage() / 2);

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
                boolean delete = removeFromFavoriteMovie();
                if (delete) {
                    isFavorite = false;
                    setFavorite();
                    setToast(getString(R.string.delete_from_favorite));
                } else {
                    setToast(getString(R.string.delete_failed));
                }
            } else {
                isFavorite = addToFavoriteMovie();
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

    private boolean addToFavoriteMovie() {
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

        MovieFavorite movieFavorite = new MovieFavorite();
        movieFavorite.setId(this.id);
        movieFavorite.setPoster(this.moviePoster);
        movieFavorite.setTitle(this.movieTitle);
        movieFavorite.setOriginalLanguage(this.movieOriginalLanguage);
        movieFavorite.setOverview(this.movieOverview);
        movieFavorite.setReleaseDate(this.movieReleaseDate);
        movieFavorite.setVoteAverage(this.movieVoteAverage);

        realm = Realm.getDefaultInstance();
        MovieFavorite puppies = realm.where(MovieFavorite.class).equalTo("id", this.id).findFirst();
        if (puppies == null) {
            try {
                realm.beginTransaction();
                realm.copyToRealm(movieFavorite);
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

    private boolean removeFromFavoriteMovie() {
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
            MovieFavorite movieFavorite = realm.where(MovieFavorite.class).equalTo("id", id).findFirst();
            if (movieFavorite != null) {
                movieFavorite.deleteFromRealm();
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
        RealmResults<MovieFavorite> results;
        results = realm.where(MovieFavorite.class).equalTo("id", id).findAll();
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
