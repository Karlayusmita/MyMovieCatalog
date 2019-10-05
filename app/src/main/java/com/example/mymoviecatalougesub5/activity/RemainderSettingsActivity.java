package com.example.mymoviecatalougesub5.activity;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;

import com.example.mymoviecatalougesub5.R;
import com.example.mymoviecatalougesub5.alarm.DailyAlarmReceiver;
import com.example.mymoviecatalougesub5.alarm.ReleaseAlarmReceiver;
import com.example.mymoviecatalougesub5.api.Api;
import com.example.mymoviecatalougesub5.model.Movie;
import com.example.mymoviecatalougesub5.network.Network;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.appcompat.app.ActionBar;

public class RemainderSettingsActivity extends AppCompatPreferencesActivity {
    private static DailyAlarmReceiver dailyAlarmReceiver;
    private static ReleaseAlarmReceiver releaseAlarmReceiver;
    private static Context context;
    private static List<Movie> movieList;

    private static Preference.OnPreferenceChangeListener bindPreferencesSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            boolean value = (boolean) newValue;

            String key = preference.getKey();
            String daily = "daily_remainder";
            String release = "release_remainder";
            if (key.equals(daily)) {
                if (value) {
                    dailyAlarmReceiver.setRepeatingAlarm(getAppContext());
                } else {
                    dailyAlarmReceiver.cancelAlarm(getAppContext());
                }
            }

            if (key.equals(release)) {
                if (value) {
                    setRepeatingAlarm();
                } else {
                    releaseAlarmReceiver.cancelAlarm(getAppContext());
                }
            }
            return true;
        }
    };

    private static boolean isXLargeTable(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    private static void bindPreferencesSummaryToValue(Preference preference) {
        preference.setOnPreferenceChangeListener(bindPreferencesSummaryToValueListener);

        bindPreferencesSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager.getDefaultSharedPreferences(preference.getContext())
                        .getBoolean(preference.getKey(), false));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();

        dailyAlarmReceiver = new DailyAlarmReceiver();
        releaseAlarmReceiver = new ReleaseAlarmReceiver();
        context = getApplicationContext();
        movieList = new ArrayList<>();

        this.getFragmentManager().beginTransaction().replace(android.R.id.content, new RemainderPreferenceFragment()).commit();
    }


    public static Context getAppContext() {
        return context;
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowHomeEnabled(true);
        }
    }

    public static void setAlarm(List<Movie> movies) {
        releaseAlarmReceiver.setRepeatingAlarm(getAppContext(), movies);
    }

    public static void setRepeatingAlarm() {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date date = new Date();
        String currentDate = dateFormat.format(date);
        Log.e("currentDate", currentDate);

        URL url = Api.getUpComingMovie(currentDate);
        new MovieAsyncTask(currentDate).execute(url);

    }

    @Override
    public boolean onNavigateUp() {
        onBackPressed();
        return super.onNavigateUp();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    @Override
    public boolean onIsMultiPane() {
        return isXLargeTable(this);
    }

    protected boolean isValidFragment(String fragmentName) {
        return PreferenceFragment.class.getName().equals(fragmentName)
                || RemainderPreferenceFragment.class.getName().equals(fragmentName);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class RemainderPreferenceFragment extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_remainder);

            Preference preference = findPreference(getString(R.string.key_daily_remainder));
            bindPreferencesSummaryToValue(findPreference(getString(R.string.key_daily_remainder)));
            bindPreferencesSummaryToValue(findPreference(getString(R.string.key_release_remainder)));

        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), RemainderSettingsActivity.class));
                return true;
            }

            return super.onOptionsItemSelected(item);
        }

    }

    private static class MovieAsyncTask extends AsyncTask<URL, Void, String> {
        String currentDate;

        private MovieAsyncTask(String currentDate) {
            this.currentDate = currentDate;
        }

        @Override
        protected String doInBackground(URL... urls) {
            URL url = urls[0];
            String result = null;
            try {
                result = Network.getFromNetwork(url);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            Log.e("data up", s);

            try {
                JSONObject jsonObject = new JSONObject(s);
                JSONArray jsonArray = jsonObject.getJSONArray("results");

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);
                    Movie movie = new Movie(object);
                    if (movie.getReleaseDate().equals(currentDate)) {
                        movieList.add(movie);
                        Log.e("release date", movie.getReleaseDate());
                    }
                }
                setAlarm(movieList);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}