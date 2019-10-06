package com.example.mymoviecatalougesub5.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.mymoviecatalougesub5.R;
import com.example.mymoviecatalougesub5.activity.DetailMovieActivity;
import com.example.mymoviecatalougesub5.model.Movie;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ListMovieAdapter extends RecyclerView.Adapter<ListMovieAdapter.ViewHolder> {
    private static final String IMG_BASE = "http://image.tmdb.org/t/p/";
    private ArrayList<Movie> movieArrayList;
    private Context context;

    public ListMovieAdapter(Context context) {
        this.context = context;
        movieArrayList = new ArrayList<>();
    }

    private ArrayList<Movie> getMovieArrayList() {
        return movieArrayList;
    }

    public void setMovieArrayList(ArrayList<Movie> movieArrayList) {
        this.movieArrayList = movieArrayList;
        notifyDataSetChanged();
    }

    private Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }


    @NonNull
    @Override
    public ListMovieAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list, parent, false);
        return new ListMovieAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListMovieAdapter.ViewHolder holder, final int position) {
        Movie movie = movieArrayList.get(position);

        Glide.with(holder.itemView.getContext())
                .load(IMG_BASE + "w185" + movie.getPoster())
                .apply(new RequestOptions().override(96, 144))
                .into(holder.poster);
        holder.title.setText(movie.getTitle());
        holder.releaseDate.setText(movie.getReleaseDate());
        holder.voteAverage.setRating((float) movie.getVoteAverage() / 2);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(ListMovieAdapter.this.getContext(), DetailMovieActivity.class);
            intent.putExtra("MOVIE_DATA", ListMovieAdapter.this.getMovieArrayList().get(position));
            ListMovieAdapter.this.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return movieArrayList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView poster;
        TextView title;
        TextView releaseDate;
        RatingBar voteAverage;

        ViewHolder(View view) {
            super(view);
            poster = view.findViewById(R.id.item_poster);
            title = view.findViewById(R.id.item_title);
            releaseDate = view.findViewById(R.id.item_release_date);
            voteAverage = view.findViewById(R.id.item_vote);
        }
    }
}
