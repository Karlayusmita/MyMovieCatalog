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
import com.example.mymoviecatalougesub5.activity.DetailTvShowActivity;
import com.example.mymoviecatalougesub5.model.TvShow;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ListTvShowAdapter extends RecyclerView.Adapter<ListTvShowAdapter.ViewHolder> {
    private ArrayList<TvShow> tvShowArrayList;
    private Context context;
    private static final String IMG_BASE = "http://image.tmdb.org/t/p/";

    public ListTvShowAdapter(Context context){
        this.context = context;
        tvShowArrayList = new ArrayList<>();
    }
    private ArrayList<TvShow> getTvShowArrayList() {
        return tvShowArrayList;
    }

    public void setTvShowArrayList(ArrayList<TvShow> tvShowArrayList) {
        this.tvShowArrayList = tvShowArrayList;
        notifyDataSetChanged();
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list,parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        TvShow tvShow = tvShowArrayList.get(position);

        Glide.with(holder.itemView.getContext())
                .load(IMG_BASE+"w185" + tvShow.getPoster())
                .apply(new RequestOptions().override(96,144))
                .into(holder.poster);
        holder.name.setText(tvShow.getName());
        holder.firstAirDate.setText(tvShow.getFirstAirDate());
        holder.voteCount.setRating((float)tvShow.getVoteCount()/2);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(ListTvShowAdapter.this.getContext(), DetailTvShowActivity.class);
            intent.putExtra("TV_SHOW_DATA", ListTvShowAdapter.this.getTvShowArrayList().get(position));
            ListTvShowAdapter.this.getContext().startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return tvShowArrayList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView poster;
        TextView name;
        TextView firstAirDate;
        RatingBar voteCount;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            poster = itemView.findViewById(R.id.item_poster);
            name = itemView.findViewById(R.id.item_title);
            firstAirDate = itemView.findViewById(R.id.item_release_date);
            voteCount = itemView.findViewById(R.id.item_vote);
        }
    }
}
