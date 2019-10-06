package com.example.mymoviecatalougesub5.activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.SearchView;

import com.example.mymoviecatalougesub5.R;
import com.example.mymoviecatalougesub5.fragment.FavoriteFragment;
import com.example.mymoviecatalougesub5.fragment.MovieFragment;
import com.example.mymoviecatalougesub5.fragment.TvShowFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class MainActivity extends AppCompatActivity {
    private final String TAG_FRAGMENT = "fragmnet";
    private Fragment fragment;
    private BottomNavigationView.OnNavigationItemSelectedListener bottomNavigationView
            = menuItem -> {
        switch (menuItem.getItemId()) {
            case R.id.navigation_movie:
                fragment = new MovieFragment();
                loadFragment(fragment);
                return true;
            case R.id.navigation_tv_show:
                fragment = new TvShowFragment();
                loadFragment(fragment);
                return true;
            case R.id.navigation_favorite:
                fragment = new FavoriteFragment();
                loadFragment(fragment);
                return true;
        }
        return false;
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView1 = findViewById(R.id.bottom_navigation);
        bottomNavigationView1.setOnNavigationItemSelectedListener(bottomNavigationView);
        if (savedInstanceState == null) {
            bottomNavigationView1.setSelectedItemId(R.id.navigation_movie);
            fragment = new MovieFragment();
        } else {
            fragment = getSupportFragmentManager().findFragmentByTag(TAG_FRAGMENT);
            loadFragment(fragment);
        }
    }

    private void loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container_layout, fragment, TAG_FRAGMENT)
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        if (searchManager != null) {
            SearchView searchView = (SearchView) (menu.findItem(R.id.search_menu)).getActionView();
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            searchView.setQueryHint(getResources().getString(R.string.search));
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    Fragment fragment1 = getSupportFragmentManager().findFragmentByTag(TAG_FRAGMENT);
                    if (fragment1 instanceof MovieFragment) {
                        Intent intent = new Intent(MainActivity.this, SearchMovieActivity.class);
                        intent.putExtra(SearchMovieActivity.EXTRA_QUERY, query);
                        startActivity(intent);
                        return true;
                    } else if (fragment1 instanceof TvShowFragment) {
                        Intent intent = new Intent(MainActivity.this, SearchTvShowActivity.class);
                        intent.putExtra(SearchTvShowActivity.EXTRA_QUERY, query);
                        startActivity(intent);
                        return true;
                    }
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    return false;
                }
            });
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.change_language_menu:
                Intent intent = new Intent(Settings.ACTION_LOCALE_SETTINGS);
                startActivity(intent);
                break;
            case R.id.reminder_menu:
                Intent intent1 = new Intent(MainActivity.this, RemainderSettingsActivity.class);
                startActivity(intent1);
        }
        return super.onOptionsItemSelected(item);
    }
}
