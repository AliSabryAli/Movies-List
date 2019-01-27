package com.ali.moviedirectory.activities.data;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ali.moviedirectory.R;
import com.ali.moviedirectory.activities.activities.DetailsActivity;
import com.ali.moviedirectory.activities.modle.Movie;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.zip.Inflater;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.Viewholder> {
    Context context;
    List<Movie> movieList;

    public RecyclerViewAdapter(Context context, List<Movie> movieList) {
        this.context = context;
        this.movieList = movieList;
    }

    @NonNull
    @Override
    public RecyclerViewAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.movie_row, viewGroup, false);
        return new Viewholder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter.Viewholder holder, int position) {

        Movie movie = movieList.get(position);
        String posterLink = movie.getPoster();

        holder.title.setText(position + "-" + movie.getTitle());
        holder.type.setText("Type: " + movie.getType());
        holder.year.setText("Year: " + movie.getYear());
        Picasso.get().load(posterLink).fit().placeholder(android.R.drawable.stat_sys_download).into(holder.poster);


    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView title;
        TextView year;
        TextView type;
        ImageView poster;

        public Viewholder(@NonNull View itemView, final Context ctx) {
            super(itemView);
            context = ctx;
            title = itemView.findViewById(R.id.movieTitleID);
            year = itemView.findViewById(R.id.movieReleaseID);
            type = itemView.findViewById(R.id.movieCatID);
            poster = itemView.findViewById(R.id.movieImgID);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Movie movie = movieList.get(getAdapterPosition());
                    Intent intent = new Intent(ctx, DetailsActivity.class);
                    intent.putExtra("movie", movie);
                    ctx.startActivity(intent);
                }
            });
        }

        @Override
        public void onClick(View view) {

        }
    }
}
